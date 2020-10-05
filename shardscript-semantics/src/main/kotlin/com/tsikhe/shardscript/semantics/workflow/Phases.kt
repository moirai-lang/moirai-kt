package com.tsikhe.shardscript.semantics.workflow

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.visitors.*

internal fun createFileScope(
    sourceContext: SourceContext,
    namespaceParts: List<String>,
    root: Scope<Symbol>
): Namespace {
    if (namespaceParts.isEmpty()) {
        val error = LanguageError(sourceContext, FilesMustHaveNamespace)
        filterThrow(setOf(error))
    }

    var owner: Scope<Symbol> = root
    namespaceParts.forEach { current ->
        val currentId = GroundIdentifier(current)
        owner = if (!owner.existsHere(currentId)) {
            val currentSymbol = Namespace(owner, currentId)
            owner.define(currentId, currentSymbol)
            currentSymbol
        } else {
            val currentSymbol = owner.fetchHere(currentId)
            if (currentSymbol is Namespace) {
                currentSymbol
            } else {
                langThrow(sourceContext, IdentifierAlreadyExists(currentId))
            }
        }
    }

    return owner as Namespace
}

internal fun bindScopes(
    ast: FileAst,
    namespace: Namespace,
    architecture: Architecture,
    preludeTable: PreludeTable
) {
    ast.scope = namespace
    val bindScopeVisitor = BindScopesAstVisitor(preludeTable, architecture)
    ast.accept(bindScopeVisitor, namespace)
}

internal fun parameterScan(ast: FileAst) {
    val parameterScanAstVisitor = ParameterScanAstVisitor()
    ast.accept(parameterScanAstVisitor)
    val errors = parameterScanAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

internal fun simpleRecursiveRecordDetection(ast: FileAst): SortResult<Symbol> {
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

internal fun recordScan(ast: FileAst) {
    val recordScanAstVisitor = RecordScanAstVisitor()
    ast.accept(recordScanAstVisitor)
    val errors = recordScanAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

internal fun functionScan(ast: FileAst) {
    val functionScanAstVisitor = FunctionScanAstVisitor()
    ast.accept(functionScanAstVisitor)
    val errors = functionScanAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

internal fun propagateTypes(ast: FileAst, architecture: Architecture, preludeTable: PreludeTable) {
    val propagateTypesAstVisitor = PropagateTypesAstVisitor(architecture, preludeTable)
    ast.accept(propagateTypesAstVisitor)
    val errors = propagateTypesAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

internal fun checkTypes(ast: FileAst, prelude: PreludeTable) {
    val checkTypesAstVisitor = CheckTypesAstVisitor(prelude)
    ast.accept(checkTypesAstVisitor)
    val errors = checkTypesAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

internal fun bans(ast: FileAst) {
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

internal fun linearizeBans(ast: FileAst) {
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

internal fun sortFunctions(ast: FileAst): SortResult<Symbol> {
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

internal fun calculateCostMultipliers(ast: FileAst, architecture: Architecture) {
    val costMultiplierAstVisitor = CostMultiplierAstVisitor(architecture)
    ast.accept(costMultiplierAstVisitor)
}

internal fun calculateCost(symbol: Symbol, architecture: Architecture) {
    val costExpressionAstVisitor = CostExpressionAstVisitor(architecture)
    when (symbol) {
        is GroundFunctionSymbol -> {
            symbol.body.accept(costExpressionAstVisitor)
            val bodyCost = symbol.body.costExpression
            symbol.costExpression = if (canEvalImmediately(bodyCost)) {
                OmicronTypeSymbol(evalCostExpression(bodyCost))
            } else {
                bodyCost
            }
        }
        is ParameterizedFunctionSymbol -> {
            symbol.body.accept(costExpressionAstVisitor)
            val bodyCost = symbol.body.costExpression
            symbol.costExpression = if (canEvalImmediately(bodyCost)) {
                OmicronTypeSymbol(evalCostExpression(bodyCost))
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

internal fun debugOmicron(ast: FileAst, architecture: Architecture) {
    ast.accept(OmicronDebuggerAstVisitor(architecture))
}

internal fun calculateCost(ast: FileAst, architecture: Architecture) {
    val costExpressionAstVisitor = CostExpressionAstVisitor(architecture)
    ast.accept(costExpressionAstVisitor)
    val errors = costExpressionAstVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}

internal fun enforceCostLimit(ast: FileAst, architecture: Architecture) {
    val cost = evalCostExpression(ast.costExpression)
    if (cost > architecture.costUpperLimit) {
        filterThrow(setOf(LanguageError(NotInSource, CostOverLimit)))
    }
}

internal fun registerImports(ast: FileAst, imports: ImportTable) {
    val importVisitor = ImportSymbolsAstVisitor(imports)
    ast.accept(importVisitor)
}

internal fun resurrectWhitelist(ast: FileAst) {
    val resurrectVisitor = ResurrectWhitelistAstVisitor()
    ast.accept(resurrectVisitor)
    val errors = resurrectVisitor.errors.toSet()
    if (errors.isNotEmpty()) {
        filterThrow(errors)
    }
}