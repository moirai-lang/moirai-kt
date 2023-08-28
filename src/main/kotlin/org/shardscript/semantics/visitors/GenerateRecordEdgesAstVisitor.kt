package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

fun addRecordEdge(
    processFirst: Symbol,
    processSecond: Symbol,
    edges: MutableSet<DependencyEdge<Symbol>>,
    nodes: MutableSet<Symbol>
) {
    nodes.add(processFirst)
    nodes.add(processSecond)
    edges.add(DependencyEdge(processFirst, processSecond))
}

class GenerateRecordEdgesAstVisitor : UnitAstVisitor() {
    val edges: MutableSet<DependencyEdge<Symbol>> = HashSet()
    val nodes: MutableSet<Symbol> = HashSet()

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        val linearized = linearizeIdentifiers(ast.fields.map { it.ofType })
            .filterIsInstance<Identifier>()
            .map { ast.scope.fetch(it) }
        val symbol = ast.scope as Symbol
        linearized.forEach {
            addRecordEdge(it, symbol, edges, nodes)
        }
    }
}