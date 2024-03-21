package moirai.semantics.workflow

import moirai.semantics.core.*
import moirai.semantics.prelude.Lang

internal data class UserScopes(
    val prelude: SymbolTable,
    val plugins: SymbolTable,
    val imports: SymbolTable,
    val exports: SymbolTable
)

internal data class SemanticArtifacts(
    val processedAst: FileAst,
    val userScopes: UserScopes,
    val file: Scope,
    val sortedRecords: SortResult<Type>,
    val sortedFunctions: SortResult<Symbol>
)

internal fun createUserScopes(plugins: SymbolTable): UserScopes {
    val imports = SymbolTable(plugins)
    val userRoot = SymbolTable(imports)
    return UserScopes(Lang.prelude, plugins, imports, userRoot)
}

internal fun topologicallySortAllArtifacts(
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
                errors.add(NotInSource, RecursiveRecordDetected(toError(it)))
            }

        }
        is Right -> Unit
    }

    when (val sortedFunctions = topologicalSort(allFunctionNodes, allFunctionEdges)) {
        is Left -> {
            sortedFunctions.value.forEach {
                errors.add(NotInSource, RecursiveFunctionDetected(toError(it)))
            }

        }
        is Right -> Unit
    }

    if (errors.toSet().isNotEmpty()) {
        filterThrow(errors.toSet())
    }
}

internal fun processAstAllPhases(
    ast: FileAst,
    fileName: String,
    architecture: Architecture,
    plugins: SymbolTable,
    existingArtifacts: List<SemanticArtifacts>
): SemanticArtifacts {
    val userScopes = createUserScopes(plugins)
    existingArtifacts.forEach { artifact ->
        artifact.userScopes.exports.symbolsToMap().forEach { entry ->
            userScopes.imports.define(Identifier(NotInSource, entry.key), entry.value)
        }
    }

    val fileScope = SymbolTable(userScopes.imports)

    bindScopes(ast, fileName, fileScope, architecture)
    parameterScan(ast, fileName)
    val sortedRecords = simpleRecursiveRecordDetection(ast)
    recordScan(ast)
    functionScan(ast)
    propagateTypes(ast, userScopes.prelude)
    checkTypes(ast, userScopes.prelude)
    bans(ast)
    calculateCostMultipliers(ast, architecture)

    // By default, records, objects, and functions are exported
    fileScope.symbolsToMap().forEach { kvp ->
        when (kvp.value) {
            is GroundFunctionSymbol,
            is ParameterizedFunctionSymbol -> userScopes.exports.define(Identifier(NotInSource, kvp.key), kvp.value)
            else -> Unit
        }
    }

    fileScope.typesToMap().forEach { kvp ->
        when (kvp.value) {
            is ObjectType,
            is GroundRecordType,
            is ParameterizedRecordType -> userScopes.exports.defineType(Identifier(NotInSource, kvp.key), kvp.value)
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