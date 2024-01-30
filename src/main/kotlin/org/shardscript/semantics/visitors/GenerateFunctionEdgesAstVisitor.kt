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
        is TypeInstantiation -> {
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
        is TypeInstantiation -> {
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
                is TypeInstantiation -> {
                    when (val parameterizedSymbol = symbolRef.substitutionChain.terminus) {
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
    private val edges: MutableSet<DependencyEdge<Symbol>>,
    private val nodes: MutableSet<Symbol>
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
            is TypeInstantiation -> {
                when (val parameterizedSymbol = symbolRef.substitutionChain.terminus) {
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
            is TypeInstantiation -> {
                when (val parameterizedType = symbolRef.substitutionChain.terminus) {
                    is ParameterizedMemberPluginSymbol -> Unit
                    // TODO: Fix this use of the as keyword
                    else -> addFunctionEdge(parameterizedType as Symbol, param, edges, nodes)
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
            is TypeInstantiation -> {
                when (val parameterizedType = symbolRef.substitutionChain.terminus) {
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