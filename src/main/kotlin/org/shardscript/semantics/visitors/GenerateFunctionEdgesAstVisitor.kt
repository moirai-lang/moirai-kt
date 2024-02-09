package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

fun addFunctionEdge(
    processFirst: Symbol,
    processSecond: Symbol,
    edges: MutableSet<DependencyEdge<Symbol>>,
    nodes: MutableSet<Symbol>
) {
    val first: Symbol = when (processFirst) {
        is GroundFunctionSymbol -> processFirst
        is ParameterizedFunctionSymbol -> processFirst
        is SymbolInstantiation -> {
            when (val parameterizedSymbol = processFirst.substitutionChain.terminus) {
                is ParameterizedFunctionSymbol -> parameterizedSymbol
                else -> processFirst
            }
        }
        else -> processFirst
    }
    val second: Symbol = when (processSecond) {
        is GroundFunctionSymbol -> processSecond
        is ParameterizedFunctionSymbol -> processSecond
        is SymbolInstantiation -> {
            when (val parameterizedSymbol = processSecond.substitutionChain.terminus) {
                is ParameterizedFunctionSymbol -> parameterizedSymbol
                else -> processSecond
            }
        }
        else -> processSecond
    }
    nodes.add(first)
    nodes.add(second)
    edges.add(DependencyEdge(first, second))
}

class GenerateFunctionEdgesAstVisitor : UnitAstVisitor() {
    val edges: MutableSet<DependencyEdge<Symbol>> = HashSet()
    val nodes: MutableSet<Symbol> = HashSet()

    override fun visit(ast: FunctionAst) {
        val parameterizedVisitor = GenerateFunctionEdgesParameterizedAstVisitor(edges, nodes)
        val function = ast.scope as Symbol
        nodes.add(
            function
        ) // redundant add in case it's never called
        ast.body.accept(parameterizedVisitor, function)
    }
}

class GenerateFunctionEdgesParameterizedAstVisitor(
    private val edges: MutableSet<DependencyEdge<Symbol>>,
    private val nodes: MutableSet<Symbol>
) : ParameterizedUnitAstVisitor<Symbol>() {
    override fun visit(ast: GroundApplyAst, param: Symbol) {
        super.visit(ast, param)
        when (val symbolRef = ast.symbolRef) {
            is FunctionFormalParameterSymbol -> Unit
            is GroundFunctionSymbol,
            is ParameterizedFunctionSymbol -> {
                addFunctionEdge(symbolRef, param, edges, nodes)
            }
            is SymbolInstantiation -> {
                when (val parameterizedSymbol = symbolRef.substitutionChain.terminus) {
                    is ParameterizedFunctionSymbol -> addFunctionEdge(parameterizedSymbol, param, edges, nodes)
                    else -> Unit
                }
            }
            else -> Unit
        }
    }
}