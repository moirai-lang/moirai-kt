package moirai.composition

import moirai.grammar.MoiraiParser
import moirai.semantics.core.*
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

internal data class ImportScan(
    val scriptType: ScriptType,
    val sourceText: String,
    val parseTree: MoiraiParser.FileContext,
    val imports: Set<ImportStat>
)

internal fun preScanFile(sourceText: String): ImportScan {
    val parser = createParser(sourceText)
    val parseTree = parser.grammar.file()

    val errors = LanguageErrors()

    val importsListener = ImportsParseTreeListener(errors)
    ParseTreeWalker.DEFAULT.walk(importsListener, parseTree)
    val importStats = importsListener.listImports()

    if (parser.listener.hasErrors()) {
        parser.listener.populateErrors(errors, importsListener.scriptType().fileName())
        if (errors.toSet().isNotEmpty()) {
            throw LanguageException(errors.toSet())
        }
    }

    if (errors.toSet().isNotEmpty()) {
        throw LanguageException(errors.toSet())
    }

    return ImportScan(importsListener.scriptType(), sourceText, parseTree, importStats.toSet())
}

internal fun fetchStoredFileByNamespaceAndScan(sourceStore: SourceStore, nameParts: List<String>): ImportScan {
    val sourceText = sourceStore.fetchSourceText(nameParts)
    return preScanFile(sourceText)
}

/**
 * Returns the topologically-sorted fan-out of all imports, where each item in the result list points to a list
 * of all imports it needs to pass semantic analysis. Because the result is topologically sorted, the first
 * few items in the list will have no imports, and it will be possible to build a running and growing list
 * of available processed imports for future semantic analysis phases
 */
internal fun preScanImportFanOut(
    sourceStore: SourceStore,
    sourceText: String
): List<ImportScan> {
    val initialScan = preScanFile(sourceText)

    if (initialScan.scriptType is PureTransient) {
        return listOf(initialScan)
    }

    val unprocessed: Queue<ImportScan> = LinkedList()
    val processed: MutableMap<ImportStat, ImportScan> = HashMap()
    val nodes: MutableSet<ImportStat> = HashSet()
    val edges: MutableSet<DependencyEdge<ImportStat>> = HashSet()

    unprocessed.add(initialScan)

    while (unprocessed.isNotEmpty()) {
        val head = unprocessed.remove()
        val headScriptType = head.scriptType

        if (headScriptType is NamedScriptType) {
            val headImportStat = ImportStat(headScriptType.nameParts)
            if (!processed.contains(headImportStat)) {
                processed[headImportStat] = head
                nodes.add(headImportStat)
                head.imports.forEach { importedNamespace ->
                    if (!processed.contains(importedNamespace)) {
                        val node = fetchStoredFileByNamespaceAndScan(sourceStore, importedNamespace.path)
                        if (node.scriptType is NamedScriptType) {
                            val nodeImportStat = ImportStat(node.scriptType.nameParts)
                            unprocessed.add(node)
                            edges.add(
                                DependencyEdge(
                                    processFirst = nodeImportStat,
                                    processSecond = headImportStat
                                )
                            )
                        }
                    } else {
                        val node = processed[importedNamespace]!!
                        if (node.scriptType is NamedScriptType) {
                            val nodeImportStat = ImportStat(node.scriptType.nameParts)
                            edges.add(
                                DependencyEdge(
                                    processFirst = nodeImportStat,
                                    processSecond = headImportStat
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    when (val res = topologicalSort(nodes, edges)) {
        is Left -> langThrow(NotInSource, RecursiveNamespaceDetected)
        is Right -> return res.value.map { processed[it]!! }
    }
}