package org.shardscript.semantics.workflow

import org.shardscript.semantics.core.*
import org.shardscript.semantics.visitors.*

fun createFileScope(
    sourceContext: SourceContext,
    namespaceParts: List<String>,
    root: NamespaceBase
): Namespace {
    if (namespaceParts.isEmpty()) {
        val error = LanguageError(sourceContext, FilesMustHaveNamespace)
        filterThrow(setOf(error))
    }

    var owner: Namespace = namespaceParts.first().let { current ->
        navigateDown(root, Identifier(current), sourceContext)
    }

    namespaceParts.drop(1).forEach { current ->
        owner = navigateDown(owner, Identifier(current), sourceContext)
    }

    return owner
}

private fun navigateDown(
    namespaceBase: NamespaceBase,
    currentId: Identifier,
    sourceContext: SourceContext
) = if (!namespaceBase.existsHere(currentId)) {
    val currentSymbol = Namespace(namespaceBase, currentId)
    namespaceBase.define(currentId, currentSymbol)
    currentSymbol
} else {
    val currentSymbol = namespaceBase.fetchHere(currentId)
    if (currentSymbol is Namespace) {
        currentSymbol
    } else {
        langThrow(sourceContext, IdentifierAlreadyExists(currentId))
    }
}

fun bindScopes(
    ast: FileAst,
    namespace: Namespace,
    architecture: Architecture,
    preludeTable: PreludeTable
) {
    ast.scope = namespace
    val bindScopeVisitor = BindScopesAstVisitor(preludeTable, architecture)
    ast.accept(bindScopeVisitor, namespace)
}

fun parameterScan(ast: FileAst) {
    val parameterScanAstVisitor = ParameterScanAstVisitor()
    ast.accept(parameterScanAstVisitor)
    val errors = parameterScanAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun simpleRecursiveRecordDetection(ast: FileAst): SortResult<Symbol> {
    val generateEdgesAstVisitor = GenerateRecordEdgesAstVisitor()
    ast.accept(generateEdgesAstVisitor)
    val nodes = generateEdgesAstVisitor.nodes
    val edges = generateEdgesAstVisitor.edges
    when (val sorted = topologicalSort(nodes, edges)) {
        is Left -> {
            val errors = LanguageErrors()
            sorted.value.forEach {
                errors.add(NotInSource, RecursiveRecordDetected(it))
            }
            filterThrow(errors.toSet())
        }
        is Right -> return SortResult(edges, nodes, sorted.value)
    }
}

fun recordScan(ast: FileAst) {
    val recordScanAstVisitor = RecordScanAstVisitor()
    ast.accept(recordScanAstVisitor)
    val errors = recordScanAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun functionScan(ast: FileAst) {
    val functionScanAstVisitor = FunctionScanAstVisitor()
    ast.accept(functionScanAstVisitor)
    val errors = functionScanAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun propagateTypes(ast: FileAst, architecture: Architecture, preludeTable: PreludeTable) {
    val propagateTypesAstVisitor = PropagateTypesAstVisitor(architecture, preludeTable)
    ast.accept(propagateTypesAstVisitor)
    val errors = propagateTypesAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun checkTypes(ast: FileAst, prelude: PreludeTable) {
    val checkTypesAstVisitor = CheckTypesAstVisitor(prelude)
    ast.accept(checkTypesAstVisitor)
    val errors = checkTypesAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun bans(ast: FileAst) {
    val banHigherOrderRefAstVisitor = BanHigherOrderRefAstVisitor()
    ast.accept(banHigherOrderRefAstVisitor)
    val errors = banHigherOrderRefAstVisitor.errors.toSet().toMutableSet()

    val banFunctionPositionsAstVisitor = BanFeaturePositionsAstVisitor()
    ast.accept(banFunctionPositionsAstVisitor)
    errors.addAll(banFunctionPositionsAstVisitor.errors.toSet())

    val banSecondDegreeAstVisitor = BanSecondDegreeAstVisitor()
    ast.accept(banSecondDegreeAstVisitor)
    errors.addAll(banSecondDegreeAstVisitor.errors.toSet())

    val banNestedDefinitionIndicator = BanNestedDefinitionAstVisitor()
    ast.accept(banNestedDefinitionIndicator, OtherIndicator)
    errors.addAll(banNestedDefinitionIndicator.errors.toSet())

    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun linearizeBans(ast: FileAst) {
    val errors = LanguageErrors()
    val linearizedTypesAstVisitor = LinearizedTypesAstVisitor()
    ast.accept(linearizedTypesAstVisitor)
    linearizedTypesAstVisitor.linearized.forEach { linearized ->
        when (linearized) {
            is TypeArgChildren -> {
                linearized.typeParamChildren.forEach { typeParamChild ->
                    typeParamChild.children.forEach { child ->
                        validateSubstitution(child.ctx, errors, typeParamChild.typeParam, child.symbol)
                    }
                }
            }
            else -> Unit
        }
    }
    val errorsList = errors.toSet()
    if (errorsList.isNotEmpty()) {
        filterThrow(errorsList)
    }
}

fun sortFunctions(ast: FileAst): SortResult<Symbol> {
    val generateEdgesAstVisitor = GenerateFunctionEdgesAstVisitor()
    ast.accept(generateEdgesAstVisitor)
    val nodes = generateEdgesAstVisitor.nodes
    val edges = generateEdgesAstVisitor.edges
    when (val sorted = topologicalSort(nodes, edges)) {
        is Left -> {
            val errors = LanguageErrors()
            sorted.value.forEach {
                errors.add(NotInSource, RecursiveFunctionDetected(it))
            }
            filterThrow(errors.toSet())
        }
        is Right -> return SortResult(edges, nodes, sorted.value)
    }
}

fun calculateCostMultipliers(ast: FileAst, architecture: Architecture) {
    val costMultiplierAstVisitor = CostMultiplierAstVisitor(architecture)
    ast.accept(costMultiplierAstVisitor)
}

fun calculateCost(symbol: Symbol, architecture: Architecture) {
    val costExpressionAstVisitor = CostExpressionAstVisitor(architecture)
    when (symbol) {
        is GroundFunctionSymbol -> {
            symbol.body.accept(costExpressionAstVisitor)
            val bodyCost = symbol.body.costExpression
            symbol.costExpression = if (bodyCost.accept(CanEvalCostExpressionVisitor)) {
                OmicronTypeSymbol(bodyCost.accept(EvalCostExpressionVisitor(architecture)))
            } else {
                bodyCost
            }
        }
        is ParameterizedFunctionSymbol -> {
            symbol.body.accept(costExpressionAstVisitor)
            val bodyCost = symbol.body.costExpression
            symbol.costExpression = if (bodyCost.accept(CanEvalCostExpressionVisitor)) {
                OmicronTypeSymbol(bodyCost.accept(EvalCostExpressionVisitor(architecture)))
            } else {
                bodyCost
            }
        }
        else -> Unit
    }
    val errors = costExpressionAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun debugOmicron(ast: FileAst, architecture: Architecture) {
    ast.accept(OmicronDebuggerAstVisitor(architecture))
}

fun calculateCost(ast: FileAst, architecture: Architecture) {
    val costExpressionAstVisitor = CostExpressionAstVisitor(architecture)
    ast.accept(costExpressionAstVisitor)
    val errors = costExpressionAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun enforceCostLimit(ast: FileAst, architecture: Architecture) {
    val cost = ast.costExpression.accept(EvalCostExpressionVisitor(architecture))
    if (cost > architecture.costUpperLimit) {
        filterThrow(setOf(LanguageError(NotInSource, CostOverLimit)))
    }
}

fun registerImports(ast: FileAst, imports: ImportTable) {
    val importVisitor = ImportSymbolsAstVisitor(imports)
    ast.accept(importVisitor)
}

fun resurrectWhitelist(ast: FileAst) {
    val resurrectVisitor = ResurrectWhitelistAstVisitor()
    ast.accept(resurrectVisitor)
    val errors = resurrectVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}