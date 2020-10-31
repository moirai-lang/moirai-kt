package com.tsikhe.shardscript.acceptance

import org.junit.Test

class ListHappyTests {
    @Test
    fun basicListTest() {
        val input = """
        val x = List(1, 2, 3)
        x[0]
        ^^^^^
        1
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun sizeListTest() {
        val input = """
        val x = List(1, 2, 3)
        x.size
        ^^^^^
        3
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicExplicitListTest() {
        val input = """
        val x = List<Int, 3>(1, 2, 3)
        x[0]
        ^^^^^
        1
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicForEachTest() {
        val input = """
        val x = List(1, 2, 3)
        for(y in x) {
            x
        }
        
        x[0]
        ^^^^^
        1
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicForEachExplicitTest() {
        val input = """
        val x = List(1, 2, 3)
        for(y: Int in x) {
            x
        }
        
        x[0]
        ^^^^^
        1
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicMapTest() {
        val input = """
        val x = List(1, 2, 3)
        val res = map(y in x) {
            y + 1
        }
        
        res[0]
        ^^^^^
        2
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicMapExplicitTest() {
        val input = """
        val x = List(1, 2, 3)
        val res = map(y: Int in x) {
            y + 1
        }
        
        res[0]
        ^^^^^
        2
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun listReturnMapTest() {
        val input = """
        val x = List(1, 2, 3)
        val res = map(y in x) {
            List(y + 1, y + 2)
        }
        
        res[2]
        ^^^^^
        List(4, 5)
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicFlatMapTest() {
        val input = """
        val x = List(1, 2, 3)
        val res = flatmap(y in x) {
            List(y + 1, y + 2)
        }
        
        res[5]
        ^^^^^
        5
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicFlatMapExplicitTest() {
        val input = """
        val x = List(1, 2, 3)
        val res = flatmap(y: Int in x) {
            List(y + 1, y + 2)
        }
        
        res[5]
        ^^^^^
        5
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicMutableListTest() {
        val input = """
        val x = MutableList<Int, 3>(1, 2, 3)
        x[0]
        ^^^^^
        1
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun sizeMutableListTest() {
        val input = """
        val x = MutableList<Int, 3>(1, 2, 3)
        x.size
        ^^^^^
        3
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun addMutableListTest() {
        val input = """
        val x = MutableList<Int, 4>(1, 2, 3)
        x.add(4)
        x[3]
        ^^^^^
        4
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun removeAtMutableListTest() {
        val input = """
        val x = MutableList<Int, 3>(1, 2, 3)
        x.removeAt(0)
        x[0]
        ^^^^^
        2
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun setMutableListTest() {
        val input = """
        val x = MutableList<Int, 3>(1, 2, 3)
        x[0] = 7
        x[0]
        ^^^^^
        7
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun equalsListTest() {
        splitTest(
            """
            val x = List<Int, 3>(1, 2, 3)
            val y = List<Int, 3>(1, 2, 3)
            
            x == y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun equalsMutableListTest() {
        splitTest(
            """
            val x = MutableList<Int, 3>(1, 2, 3)
            val y = MutableList<Int, 3>(1, 2, 3)
            
            x == y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun equalsNegativeListTest() {
        splitTest(
            """
            val x = List<Int, 3>(1, 2, 7)
            val y = List<Int, 3>(1, 2, 3)
            
            x == y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun equalsMutableNegativeListTest() {
        splitTest(
            """
            val x = MutableList<Int, 3>(1, 2, 7)
            val y = MutableList<Int, 3>(1, 2, 3)
            
            x == y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsListTest() {
        splitTest(
            """
            val x = List<Int, 3>(1, 2, 7)
            val y = List<Int, 3>(1, 2, 3)
            
            x != y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsMutableListTest() {
        splitTest(
            """
            val x = MutableList<Int, 3>(1, 2, 7)
            val y = MutableList<Int, 3>(1, 2, 3)
            
            x != y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsNegativeListTest() {
        splitTest(
            """
            val x = List<Int, 3>(1, 2, 3)
            val y = List<Int, 3>(1, 2, 3)
            
            x != y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsNegativeMutableListTest() {
        splitTest(
            """
            val x = MutableList<Int, 3>(1, 2, 3)
            val y = MutableList<Int, 3>(1, 2, 3)
            
            x != y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun omicronTypeParamListTest() {
        splitTest(
            """
            def size<E, #O>(x: List<E, #O>): Int {
                mutable res = 0
                for(e in x) {
                    res = res + 1
                }
                res
            }
            
            val x = List(1, 2, 3)
            size(x)
            ^^^^^
            3
        """.trimIndent()
        )
    }

    @Test
    fun listUpcastTest() {
        splitTest(
            """
                val x = List(1, 2, 3, 4, 5)
                x is List<Int, 10>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun listUpcastNegativeTest() {
        splitTest(
            """
                val x = List(1, 2, 3, 4, 5)
                x is List<Int, 3>
                ^^^^^
                false
            """.trimIndent()
        )
    }

    @Test
    fun listNestedUpcastTest() {
        splitTest(
            """
                val x = List(1, 2, 3, 4, 5)
                val y = List(x, x, x)
                y is List<List<Int, 10>, 3>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun listNestedUpcastNegativeTest() {
        splitTest(
            """
                val x = List(1, 2, 3, 4, 5)
                val y = List(x, x, x)
                y is List<List<Int, 3>, 3>
                ^^^^^
                false
            """.trimIndent()
        )
    }

    @Test
    fun mutableListToImmutableTest() {
        splitTest(
            """
                val x = MutableList<Int, 10>(1, 2, 3, 4, 5)
                val y = x.toList()
                y is List<Int, 10>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun immutableListToMutableTest() {
        splitTest(
            """
                val x = List(1, 2, 3, 4, 5)
                val y = MutableList<Int, 10>()
                for(i in range(0, 10)) {
                    if(i < x.size) {
                        y[i] = x[i]
                    }
                }
                y[1] = 9
                y[1]
                ^^^^^
                9
            """.trimIndent()
        )
    }

    @Test
    fun failedDemoIfTest() {
        splitTest("""
            def f(list: List<Int, 10>): Int {
                mutable max = 0
                for(x in list) {
                    if(x > max) {
                        max = x
                    }
                }
                max
            }
            
            val l = List(1, 2, 3, 4, 5)
            f(l)
            ^^^^^
            5
        """.trimIndent())
    }

    @Test
    fun failedDemoMaxTest() {
        splitTest("""
            def f(list: List<Int, 100>) {
                mutable max = 0
                for(y in list) {
                    if(y > max) {
                        max = y
                    }
                }
                max
            }
            
            val l = List(1, 2, 3, 4, 5)
            f(l)
            ^^^^^
            Unit
        """.trimIndent())
    }
}
