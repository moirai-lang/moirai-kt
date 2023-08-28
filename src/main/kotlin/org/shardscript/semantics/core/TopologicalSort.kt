package org.shardscript.semantics.core

data class DependencyEdge<T>(val processFirst: T, val processSecond: T)

sealed class Either<out A, out B>

data class Left<out A>(val value: A) : Either<A, Nothing>()
data class Right<out B>(val value: B) : Either<Nothing, B>()

data class SortResult<T>(
    val edges: Set<DependencyEdge<T>>,
    val nodes: Set<T>,
    val sorted: List<Symbol>
)

fun <T> topologicalSort(nodes: Set<T>, edges: Set<DependencyEdge<T>>): Either<Set<T>, List<T>> {
    data class EdgeCount<T>(val node: T, var count: Int)

    val edgeCounts: MutableMap<T, EdgeCount<T>> = HashMap()
    val outgoingEdges: MutableMap<T, MutableList<Pair<DependencyEdge<T>, EdgeCount<T>>>> = HashMap()

    nodes.forEach {
        edgeCounts[it] = EdgeCount(it, 0)
    }

    edges.forEach {
        if (!outgoingEdges.containsKey(it.processFirst)) {
            outgoingEdges[it.processFirst] = ArrayList()
        }
        if (edgeCounts.containsKey(it.processSecond)) {
            edgeCounts[it.processSecond]!!.count++
            outgoingEdges[it.processFirst]!!.add(Pair(it, edgeCounts[it.processSecond]!!))
        }
    }

    val res: MutableList<T> = ArrayList()

    val nodesWithNoIncomingEdges = edgeCounts.values.filter { it.count == 0 }.map { it.node }.toMutableList()
    val unresolvedEdges = edges.toMutableSet()

    while (nodesWithNoIncomingEdges.isNotEmpty()) {
        val toResolve = nodesWithNoIncomingEdges.last()
        nodesWithNoIncomingEdges.remove(toResolve)

        res.add(toResolve)

        if (outgoingEdges.containsKey(toResolve)) {
            outgoingEdges[toResolve]!!.forEach {
                unresolvedEdges.remove(it.first)
                it.second.count--
                if (it.second.count == 0) {
                    nodesWithNoIncomingEdges.add(it.second.node)
                }
            }
        }
    }

    return if (unresolvedEdges.isNotEmpty()) {
        val recursiveSet = unresolvedEdges.toList().flatMap {
            listOf(it.processFirst, it.processSecond)
        }.toSet()
        Left(recursiveSet)
    } else {
        Right(res)
    }
}
