package moirai.composition

import moirai.semantics.core.*
import moirai.semantics.workflow.*
import moirai.transport.TransportAst
import moirai.transport.convertToAst

class CompilerFrontend(
    private val architecture: Architecture,
    private val sourceStore: SourceStore,
    pluginSource: PluginSource = NoPluginSource
) {
    private val pluginScope = createPluginScope(pluginSource)

    private fun quickCompile(
        initialScan: ImportScan,
        errors: LanguageErrors,
        existingSemantics: List<SemanticArtifacts>
    ): ExecutionArtifacts {
        val astParseTreeVisitor = AstParseTreeVisitor(initialScan.scriptType.fileName(), errors)
        val rawAst = astParseTreeVisitor.visit(initialScan.parseTree) as FileAst

        val artifacts = processAstAllPhases(
            rawAst,
            initialScan.scriptType.fileName(),
            architecture,
            pluginScope,
            existingSemantics
        )

        val ea = ExecutionArtifacts(
            initialScan,
        )
        ea.processedAst = artifacts.processedAst
        ea.semanticArtifacts = artifacts
        return ea
    }

    internal fun compileTransportAst(
        fileName: String,
        transportAst: TransportAst,
        executionCache: ExecutionCache
    ): SemanticArtifacts {
        val nameParts = fileName.split(".")
        val line = convertToAst(transportAst)
        val file = FileAst(NotInSource, listOf(line))

        val ea = when (val res = executionCache.fetchExecutionArtifacts(nameParts)) {
            is InCache -> {
                res.executionArtifacts
            }

            NotInCache -> {
                val singleImport = sourceStore.fetchSourceText(nameParts)
                val toStore = fullCompileWithTopologicalSort(singleImport)
                executionCache.storeExecutionArtifacts(nameParts, toStore)
                toStore
            }
        }

        return processAstAllPhases(
            file,
            fileName,
            architecture,
            pluginScope,
            listOf(ea.semanticArtifacts)
        )
    }

    fun compileUsingCache(
        contents: String,
        executionCache: ExecutionCache
    ): ExecutionArtifacts {
        val initialScan = preScanFile(contents)
        val errors = LanguageErrors()

        return when (initialScan.scriptType) {
            is PureTransient -> {
                quickCompile(initialScan, errors, listOf())
            }

            is NamedScript -> {
                executionCache.invalidateCache(initialScan.scriptType.nameParts)
                val ea = fullCompileWithTopologicalSort(contents)
                executionCache.storeExecutionArtifacts(initialScan.scriptType.nameParts, ea)
                ea
            }

            is TransientScript -> {
                val nameParts = initialScan.scriptType.nameParts
                val ea = when (val res = executionCache.fetchExecutionArtifacts(nameParts)) {
                    is InCache -> {
                        res.executionArtifacts
                    }

                    NotInCache -> {
                        val singleImport = sourceStore.fetchSourceText(nameParts)
                        val toStore = fullCompileWithTopologicalSort(singleImport)
                        executionCache.storeExecutionArtifacts(nameParts, toStore)
                        toStore
                    }
                }
                quickCompile(initialScan, errors, listOf(ea.semanticArtifacts))
            }
        }
    }

    fun fullCompileWithTopologicalSort(
        contents: String
    ): ExecutionArtifacts {
        val importFanOut = preScanImportFanOut(sourceStore, contents)
        val errors = LanguageErrors()

        if (importFanOut.count() == 1 && importFanOut.first().scriptType is PureTransient) {
            val res = importFanOut.first()
            return quickCompile(res, errors, listOf())
        }

        if (importFanOut.count() > 1 && importFanOut.any { it.scriptType is PureTransient }) {
            langThrow(NotInSource, ImpossibleState("PureTransient ScriptType found in import list"))
        }

        val semanticsMap: MutableMap<List<String>, SemanticArtifacts> = HashMap()
        val astMap: MutableMap<List<String>, Ast> = HashMap()

        importFanOut.forEach { importScan ->
            val astParseTreeVisitor = AstParseTreeVisitor(importScan.scriptType.fileName(), errors)
            val rawAst = astParseTreeVisitor.visit(importScan.parseTree) as FileAst
            if (errors.toSet().isNotEmpty()) {
                throw LanguageException(errors.toSet())
            }

            val existingArtifacts = importScan.imports.map {
                semanticsMap[it.path]!!
            }

            val scriptType = importScan.scriptType as NamedScriptBase

            val artifacts = processAstAllPhases(
                rawAst,
                scriptType.fileName(),
                architecture,
                pluginScope,
                existingArtifacts
            )
            semanticsMap[scriptType.nameParts] = artifacts
            astMap[scriptType.nameParts] = rawAst
        }

        val res = importFanOut.last()
        val resScriptType = res.scriptType as NamedScriptBase
        val ea = ExecutionArtifacts(
            res,
        )
        ea.processedAst = astMap[resScriptType.nameParts]!!
        ea.semanticArtifacts = semanticsMap[resScriptType.nameParts]!!
        return ea
    }
}