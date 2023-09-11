package org.shardscript.composition

import org.shardscript.grammar.ShardScriptParser
import org.shardscript.semantics.core.*
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

// A file is transient if it was sent over the network to be executed
// The namespace combined with the transient flag is a unique way of identifying an import
data class ImportId(val namespace: List<String>, val transient: Boolean)

data class ImportScan(
    val fileName: String,
    val id: ImportId,
    val sourceText: String,
    val parseTree: ShardScriptParser.FileContext,
    val imports: Set<ImportId>
)

internal fun preScanFile(fileName: String, namespace: List<String>, sourceText: String, transient: Boolean): ImportScan {
    val parser = createParser(sourceText)
    val parseTree = parser.grammar.file()

    val parseErrors = parser.listener.populateErrors(fileName)

    if (parseErrors.toSet().isNotEmpty()) {
        throw LanguageException(parseErrors.toSet())
    }

    val importsListener = ImportsParseTreeListener(fileName, parseErrors)
    ParseTreeWalker.DEFAULT.walk(importsListener, parseTree)
    val importStats = importsListener.listImports()

    if (parseErrors.toSet().isNotEmpty()) {
        throw LanguageException(parseErrors.toSet())
    }

    val importedFiles: MutableList<ImportId> = ArrayList()
    importStats.forEach {
        importedFiles.add(ImportId(it.path, transient = false))
    }

    return ImportScan(fileName, ImportId(namespace, transient), sourceText, parseTree, importedFiles.toSet())
}

internal fun parseNamespace(fileName: String): List<String> =
    fileName.split(".")

internal fun persistNamespace(namespace: List<String>): String =
    namespace.joinToString(".")

internal fun fetchStoredFileByNamespaceAndScan(sourceStore: SourceStore, id: ImportId): ImportScan {
    val fileName = persistNamespace(id.namespace)
    val sourceText = sourceStore.fetchSourceText(id.namespace)
    return preScanFile(fileName, id.namespace, sourceText, id.transient)
}

/**
 * Returns the topologically-sorted fan-out of all imports, where each item in the result list points to a list
 * of all imports it needs to pass semantic analysis. Because the result is topologically sorted, the first
 * few items in the list will have no imports, and it will be possible to build a running and growing list
 * of available processed imports for future semantic analysis phases
 */
internal fun preScanImportFanOut(
    sourceStore: SourceStore,
    fileName: String,
    sourceText: String,
    transient: Boolean
): Set<ImportScan> {
    val namespace = parseNamespace(fileName)
    val initialScan = preScanFile(fileName, namespace, sourceText, transient)

    val unprocessed: Queue<ImportScan> = LinkedList()

    val processed: MutableMap<ImportId, ImportScan> = HashMap()

    val nodes: MutableSet<ImportId> = HashSet()
    val edges: MutableSet<DependencyEdge<ImportId>> = HashSet()

    if (transient) {
        val allImportsMergedFile: MutableSet<ImportId> = HashSet()
        initialScan.imports.forEach {
            allImportsMergedFile.add(it)
        }
        val storedFileId = ImportId(namespace, transient = false)
        // When executing a transient file, you need to "merge" the imports from the stored
        // file with the transient file that was passed over the network to be executed,
        // makes sure that the same symbols that are visible in the stored file are also
        // visible in the transient file
        val mergedStoredFile = fetchStoredFileByNamespaceAndScan(sourceStore, storedFileId)
        mergedStoredFile.imports.forEach {
            allImportsMergedFile.add(it)
        }
        // We also need to import the stored file itself into the transient file so that
        // symbols defined in the stored file will be visible in the transient file
        allImportsMergedFile.add(storedFileId)
        // Replace the old import scan with a new one that uses the implicit imports
        // described above
        val mergedFile = ImportScan(
            initialScan.fileName,
            ImportId(
                initialScan.id.namespace,
                transient = true
            ),
            initialScan.sourceText,
            initialScan.parseTree,
            allImportsMergedFile.toSet()
        )
        unprocessed.add(mergedFile) // we do not add the initial scan
    } else {
        unprocessed.add(initialScan)
    }

    while (unprocessed.isNotEmpty()) {
        val head = unprocessed.remove()
        if (!processed.contains(head.id)) {
            processed[head.id] = head
            nodes.add(head.id)
            head.imports.forEach { importedNamespace ->
                if (!processed.contains(importedNamespace)) {
                    val node = fetchStoredFileByNamespaceAndScan(sourceStore, importedNamespace)
                    unprocessed.add(node)
                    edges.add(
                        DependencyEdge(
                            processFirst = node.id,
                            processSecond = head.id
                        )
                    )
                } else {
                    val node = processed[importedNamespace]!!
                    edges.add(
                        DependencyEdge(
                            processFirst = node.id,
                            processSecond = head.id
                        )
                    )
                }
            }
        }
    }

    when (val res = topologicalSort(nodes, edges)) {
        is Left -> langThrow(NotInSource, RecursiveNamespaceDetected)
        is Right -> return res.value.map { processed[it]!! }.toSet()
    }
}