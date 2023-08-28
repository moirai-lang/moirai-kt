package org.shardscript.semantics.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class TopologicalSortTest {

    data class TestNode(val str: String)

    @Test
    fun testBasicDependencyGraph() {

        val a = TestNode("A")
        val b = TestNode("B")
        val c = TestNode("C")
        val d = TestNode("D")
        val e = TestNode("E")
        val f = TestNode("F")

        val nodes = listOf(e, c, b, a, d, f)
        val edges: MutableList<DependencyEdge<TestNode>> = ArrayList()

        edges.add(DependencyEdge(a, b))
        edges.add(DependencyEdge(a, c))
        edges.add(DependencyEdge(a, e))
        edges.add(DependencyEdge(b, d))
        edges.add(DependencyEdge(c, d))
        edges.add(DependencyEdge(c, e))
        edges.add(DependencyEdge(d, f))
        edges.add(DependencyEdge(e, f))

        val res = topologicalSort(nodes.toSet(), edges.toSet())

        when (res) {
            is Right -> {
                val indexA = res.value.indexOf(a)
                val indexB = res.value.indexOf(b)
                val indexC = res.value.indexOf(c)
                val indexD = res.value.indexOf(d)
                val indexE = res.value.indexOf(e)
                val indexF = res.value.indexOf(f)
                assertTrue(indexA < indexB)
                assertTrue(indexA < indexC)
                assertTrue(indexA < indexE)
                assertTrue(indexB < indexD)
                assertTrue(indexC < indexD)
                assertTrue(indexC < indexE)
                assertTrue(indexD < indexF)
                assertTrue(indexE < indexF)
            }
            else -> fail()
        }
    }

    @Test
    fun testDependencyGraphWithLoop() {

        val a = TestNode("A")
        val b = TestNode("B")
        val c = TestNode("C")
        val d = TestNode("D")

        val nodes = listOf(c, b, a, d)
        val edges: MutableList<DependencyEdge<TestNode>> = ArrayList()

        edges.add(DependencyEdge(a, b))
        edges.add(DependencyEdge(a, c))
        edges.add(DependencyEdge(b, d))
        edges.add(DependencyEdge(c, d))
        edges.add(DependencyEdge(d, a))

        val res = topologicalSort(nodes.toSet(), edges.toSet())

        assertTrue(res is Left)
    }
}
