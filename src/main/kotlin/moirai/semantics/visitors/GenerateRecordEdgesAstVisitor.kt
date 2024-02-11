package moirai.semantics.visitors

import moirai.semantics.core.*

fun addRecordEdge(
    processFirst: Type,
    processSecond: Type,
    edges: MutableSet<DependencyEdge<Type>>,
    nodes: MutableSet<Type>
) {
    nodes.add(processFirst)
    nodes.add(processSecond)
    edges.add(DependencyEdge(processFirst, processSecond))
}

class GenerateRecordEdgesAstVisitor : UnitAstVisitor() {
    val edges: MutableSet<DependencyEdge<Type>> = HashSet()
    val nodes: MutableSet<Type> = HashSet()

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        val linearized = linearizeIdentifiers(ast.fields.map { it.ofType })
            .filterIsInstance<Identifier>()
            .map { ast.scope.fetchType(it) }
        val type = ast.scope as Type
        linearized.forEach {
            addRecordEdge(it, type, edges, nodes)
        }
    }
}