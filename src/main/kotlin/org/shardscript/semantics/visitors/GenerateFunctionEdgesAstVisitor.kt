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
            when (val parameterizedSymbol = processFirst.substitutionChain.originalSymbol) {
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
            when (val parameterizedSymbol = processSecond.substitutionChain.originalSymbol) {
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

fun higherOrderEdges(
    args: List<Ast>,
    astSymbolRef: Symbol,
    edges: MutableSet<DependencyEdge<Symbol>>,
    nodes: MutableSet<Symbol>
) {
    args.forEach {
        if (it is RefAst) {
            when (val symbolRef = it.symbolRef) {
                is GroundFunctionSymbol,
                is ParameterizedFunctionSymbol -> {
                    addFunctionEdge(symbolRef, astSymbolRef, edges, nodes)
                }
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = symbolRef.substitutionChain.originalSymbol) {
                        is ParameterizedFunctionSymbol -> addFunctionEdge(
                            parameterizedSymbol,
                            astSymbolRef,
                            edges,
                            nodes
                        )
                        else -> Unit
                    }
                }
                else -> Unit
            }
        }
    }
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

    override fun visit(ast: GroundApplyAst) {
        super.visit(ast)
        higherOrderEdges(ast.args, ast.symbolRef, edges, nodes)
    }

    override fun visit(ast: DotApplyAst) {
        super.visit(ast)
        higherOrderEdges(ast.args, ast.symbolRef, edges, nodes)
    }
}

class GenerateFunctionEdgesParameterizedAstVisitor(
    val edges: MutableSet<DependencyEdge<Symbol>>,
    val nodes: MutableSet<Symbol>
) : ParameterizedUnitAstVisitor<Symbol>() {
    override fun visit(ast: GroundApplyAst, param: Symbol) {
        super.visit(ast, param)
        higherOrderEdges(ast.args, ast.symbolRef, edges, nodes)
        when (val symbolRef = ast.symbolRef) {
            is FunctionFormalParameterSymbol -> Unit
            is GroundFunctionSymbol,
            is ParameterizedFunctionSymbol -> {
                addFunctionEdge(symbolRef, param, edges, nodes)
            }
            is SymbolInstantiation -> {
                when (val parameterizedSymbol = symbolRef.substitutionChain.originalSymbol) {
                    is ParameterizedFunctionSymbol -> addFunctionEdge(parameterizedSymbol, param, edges, nodes)
                    else -> Unit
                }
            }
            else -> Unit
        }
    }

    override fun visit(ast: DotApplyAst, param: Symbol) {
        super.visit(ast, param)
        higherOrderEdges(ast.args, ast.symbolRef, edges, nodes)
        when (val symbolRef = ast.symbolRef) {
            is GroundMemberPluginSymbol,
            is ParameterizedMemberPluginSymbol -> Unit
            is SymbolInstantiation -> {
                when (val parameterizedType = symbolRef.substitutionChain.originalSymbol) {
                    is ParameterizedMemberPluginSymbol -> Unit
                    else -> addFunctionEdge(parameterizedType, param, edges, nodes)
                }
            }
            else -> addFunctionEdge(symbolRef, param, edges, nodes)
        }
    }

    override fun visit(ast: RefAst, param: Symbol) {
        super.visit(ast, param)
        when (val symbolRef = ast.symbolRef) {
            is GroundFunctionSymbol,
            is ParameterizedFunctionSymbol -> {
                addFunctionEdge(symbolRef, param, edges, nodes)
            }
            is SymbolInstantiation -> {
                when (val parameterizedType = symbolRef.substitutionChain.originalSymbol) {
                    is ParameterizedFunctionSymbol -> {
                        addFunctionEdge(parameterizedType, param, edges, nodes)
                    }
                    else -> Unit
                }
            }
            else -> Unit
        }
    }
}