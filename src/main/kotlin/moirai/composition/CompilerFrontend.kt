package moirai.composition

import moirai.semantics.core.*
import moirai.semantics.workflow.*

internal data class ExecutionArtifacts(
    val importScan: ImportScan,
    val processedAst: Ast,
    val semanticArtifacts: SemanticArtifacts
)

internal class CompilerFrontend(
    private val architecture: Architecture,
    private val sourceStore: SourceStore
) {
    fun compile(
        contents: String
    ): ExecutionArtifacts {
        val importFanOut = preScanImportFanOut(sourceStore, contents)
        val errors = LanguageErrors()

        if (importFanOut.count() == 1 && importFanOut.first().scriptType is PureTransient) {
            val res = importFanOut.first()

            val astParseTreeVisitor = AstParseTreeVisitor(res.scriptType.fileName(), errors)
            val rawAst = astParseTreeVisitor.visit(res.parseTree) as FileAst

            val artifacts = processAstAllPhases(
                rawAst,
                res.scriptType.fileName(),
                architecture,
                listOf()
            )

            return ExecutionArtifacts(
                res,
                artifacts.processedAst,
                artifacts
            )
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

            val scriptType = importScan.scriptType as NamedScriptType

            val artifacts = processAstAllPhases(
                rawAst,
                scriptType.fileName(),
                architecture,
                existingArtifacts
            )
            semanticsMap[scriptType.nameParts] = artifacts
            astMap[scriptType.nameParts] = rawAst
        }

        val res = importFanOut.last()
        val resScriptType = res.scriptType as NamedScriptType
        return ExecutionArtifacts(
            res,
            astMap[resScriptType.nameParts]!!,
            semanticsMap[resScriptType.nameParts]!!
        )
    }
}