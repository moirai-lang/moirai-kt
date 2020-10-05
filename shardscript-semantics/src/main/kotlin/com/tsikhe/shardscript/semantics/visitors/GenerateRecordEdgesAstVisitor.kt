package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

internal fun addRecordEdge(
    processFirst: Symbol,
    processSecond: Symbol,
    edges: MutableSet<DependencyEdge<Symbol>>,
    nodes: MutableSet<Symbol>
) {
    nodes.add(processFirst)
    nodes.add(processSecond)
    edges.add(DependencyEdge(processFirst, processSecond))
}

internal class GenerateRecordEdgesAstVisitor : UnitAstVisitor() {
    val edges: MutableSet<DependencyEdge<Symbol>> = HashSet()
    val nodes: MutableSet<Symbol> = HashSet()

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        val linearized = linearizeIdentifiers(ast.fields.map { it.ofType })
            .filterIsInstance<GroundIdentifier>()
            .map { ast.scope.fetch(it) }
        val symbol = ast.scope as Symbol
        linearized.forEach {
            addRecordEdge(it, symbol, edges, nodes)
        }
    }

    override fun visit(ast: EnumDefinitionAst) {
        super.visit(ast)
        val symbol = ast.scope as Symbol
        ast.records.forEach {
            val record = it.scope as Symbol
            addRecordEdge(record, symbol, edges, nodes)
        }
    }
}