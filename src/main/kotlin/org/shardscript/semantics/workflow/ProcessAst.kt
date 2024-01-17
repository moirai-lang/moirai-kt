package org.shardscript.semantics.workflow

import org.shardscript.semantics.core.*
import org.shardscript.semantics.prelude.Lang

data class UserScopes(
    val prelude: SymbolTable,
    val imports: SymbolTable,
    val exports: SymbolTable
)

data class SemanticArtifacts(
    val processedAst: FileAst,
    val userScopes: UserScopes,
    val file: Scope<Symbol>,
    val sortedRecords: SortResult<Symbol>,
    val sortedFunctions: SortResult<Symbol>
)

fun createUserScopes(architecture: Architecture): UserScopes {
    val prelude = SymbolTable(NullSymbolTable)
    Lang.initNamespace(architecture, prelude)
    val imports = SymbolTable(prelude)
    val userRoot = SymbolTable(imports)
    return UserScopes(prelude, imports, userRoot)
}

fun topologicallySortAllArtifacts(
    semanticArtifacts: SemanticArtifacts,
    existingArtifacts: List<SemanticArtifacts>
) {
    val allRecordNodes = semanticArtifacts.sortedRecords.nodes.toMutableSet()
    val allRecordEdges = semanticArtifacts.sortedRecords.edges.toMutableSet()

    val allFunctionNodes = semanticArtifacts.sortedFunctions.nodes.toMutableSet()
    val allFunctionEdges = semanticArtifacts.sortedFunctions.edges.toMutableSet()

    existingArtifacts.forEach {
        allRecordNodes.addAll(it.sortedRecords.nodes)
        allRecordEdges.addAll(it.sortedRecords.edges)

        allFunctionNodes.addAll(it.sortedFunctions.nodes)
        allFunctionEdges.addAll(it.sortedFunctions.edges)
    }

    val errors = LanguageErrors()

    when (val sortedRecords = topologicalSort(allRecordNodes, allRecordEdges)) {
        is Left -> {
            sortedRecords.value.forEach {
                errors.add(NotInSource, RecursiveRecordDetected(it))
            }

        }
        is Right -> Unit
    }

    when (val sortedFunctions = topologicalSort(allFunctionNodes, allFunctionEdges)) {
        is Left -> {
            sortedFunctions.value.forEach {
                errors.add(NotInSource, RecursiveFunctionDetected(it))
            }

        }
        is Right -> Unit
    }

    if (errors.toSet().isNotEmpty()) {
        filterThrow(errors.toSet())
    }
}

fun processAstAllPhases(
    ast: FileAst,
    architecture: Architecture,
    existingArtifacts: List<SemanticArtifacts>
): SemanticArtifacts {
    val userScopes = createUserScopes(architecture)
    existingArtifacts.forEach { artifact ->
        artifact.userScopes.exports.toMap().forEach { entry ->
            userScopes.imports.define(Identifier(NotInSource, entry.key), entry.value)
        }
    }

    val fileScope = SymbolTable(userScopes.imports)

    bindScopes(ast, fileScope, architecture)
    parameterScan(ast)
    val sortedRecords = simpleRecursiveRecordDetection(ast)
    recordScan(ast)
    functionScan(ast)
    propagateTypes(ast, userScopes.prelude)
    checkTypes(ast, userScopes.prelude)
    bans(ast)
    calculateCostMultipliers(ast, architecture)

    // By default, records, objects, and functions are exported
    fileScope.toMap().forEach { kvp ->
        when (kvp.value) {
            is ObjectSymbol,
            is GroundFunctionSymbol,
            is ParameterizedFunctionSymbol,
            is GroundRecordTypeSymbol,
            is ParameterizedRecordTypeSymbol -> userScopes.exports.define(Identifier(NotInSource, kvp.key), kvp.value)

            else -> Unit
        }
    }

    val sortedFunctions = sortFunctions(ast)
    sortedFunctions.sorted.forEach { calculateCost(it, architecture) }
    calculateCost(ast, architecture)
    enforceCostLimit(ast, architecture)

    val res = SemanticArtifacts(ast, userScopes, fileScope, sortedRecords, sortedFunctions)
    topologicallySortAllArtifacts(res, existingArtifacts)
    return res
}