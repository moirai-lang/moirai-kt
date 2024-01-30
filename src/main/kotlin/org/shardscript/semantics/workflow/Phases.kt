package org.shardscript.semantics.workflow

import org.shardscript.semantics.core.*
import org.shardscript.semantics.visitors.*

fun bindScopes(
    ast: FileAst,
    fileName: String,
    fileScope: Scope,
    architecture: Architecture
) {
    ast.scope = fileScope
    val bindScopeVisitor = BindScopesAstVisitor(architecture, fileName)
    ast.accept(bindScopeVisitor, fileScope)
}

fun parameterScan(ast: FileAst, fileName: String) {
    val parameterScanAstVisitor = ParameterScanAstVisitor(fileName)
    ast.accept(parameterScanAstVisitor)
    val errors = parameterScanAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun simpleRecursiveRecordDetection(ast: FileAst): SortResult<Type> {
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

fun propagateTypes(ast: FileAst, preludeTable: Scope) {
    val propagateTypesAstVisitor = PropagateTypesAstVisitor(preludeTable)
    ast.accept(propagateTypesAstVisitor)
    val errors = propagateTypesAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

fun checkTypes(ast: FileAst, prelude: Scope) {
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
                FinTypeSymbol(bodyCost.accept(EvalCostExpressionVisitor(architecture)))
            } else {
                bodyCost
            }
        }
        is ParameterizedFunctionSymbol -> {
            symbol.body.accept(costExpressionAstVisitor)
            val bodyCost = symbol.body.costExpression
            symbol.costExpression = if (bodyCost.accept(CanEvalCostExpressionVisitor)) {
                FinTypeSymbol(bodyCost.accept(EvalCostExpressionVisitor(architecture)))
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

fun debugFin(ast: FileAst, architecture: Architecture) {
    ast.accept(FinDebuggerAstVisitor(architecture))
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