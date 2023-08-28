package org.shardscript.composition

import org.shardscript.semantics.core.*
import org.shardscript.semantics.workflow.*

data class ExecutionArtifacts(
    val importScan: ImportScan,
    val processedAst: Ast,
    val semanticArtifacts: SemanticArtifacts
)

class CompilerFrontend(
    private val architecture: Architecture,
    private val sourceStore: SourceStore
) {
    fun quickCompile(
        fileName: String,
        contents: String,
        systemScopes: SystemScopes,
        fileNamespace: Namespace
    ): Ast {
        val errors = LanguageErrors()
        val parser = createParser(fileName, contents)
        val astParseTreeVisitor = AstParseTreeVisitor(fileName, errors)
        val file = astParseTreeVisitor.visit(parser.grammar.file()) as FileAst
        processAstResurrectOnly(systemScopes, fileNamespace, file, architecture)
        return file
    }

    fun compile(
        fileName: String,
        contents: String,
        systemScopes: SystemScopes,
        transient: Boolean
    ): ExecutionArtifacts {
        val importFanOut = preScanImportFanOut(sourceStore, fileName, contents, transient)

        val semanticsMap: MutableMap<ImportId, SemanticArtifacts> = HashMap()
        val astMap: MutableMap<List<String>, Ast> = HashMap()

        val errors = LanguageErrors()
        importFanOut.forEach { importScan ->
            val astParseTreeVisitor = AstParseTreeVisitor(importScan.fileName, errors)
            val rawAst = astParseTreeVisitor.visit(importScan.parseTree) as FileAst
            if (errors.toSet().isNotEmpty()) {
                throw LanguageException(errors.toSet())
            }

            val existingArtifacts = importScan.imports.map {
                semanticsMap[it]!!
            }

            val artifacts = processAstAllPhases(
                systemScopes,
                rawAst,
                importScan.id.namespace,
                architecture,
                existingArtifacts
            )
            semanticsMap[importScan.id] = artifacts
            astMap[importScan.id.namespace] = rawAst
        }

        val res = importFanOut.last()
        return ExecutionArtifacts(
            res,
            astMap[res.id.namespace]!!,
            semanticsMap[res.id]!!
        )
    }
}