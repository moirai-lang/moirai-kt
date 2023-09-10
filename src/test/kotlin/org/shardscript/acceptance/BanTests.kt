package org.shardscript.acceptance

import org.shardscript.semantics.core.*

import org.junit.jupiter.api.Test

class BanTests {
    @Test
    fun cannotRefFunctionParamTest() {
        failTest(
            """
            def h(g: (Int, Int) -> Int, x: Int, y: Int): Int {
                g(x, y)
            }
            
            def f(g: (Int, Int) -> Int, x: Int, y: Int): Int {
                h(g, x, y)
            }
            
            def g(x: Int, y: Int): Int {
                x + y
            }
            
            f(g, 5, 6)
        """.trimIndent(), 1
        ) {
            it.error is CannotRefFunctionParam
        }
    }

    @Test
    fun secondDegreeHigherOrderFunctionTest() {
        failTest(
            """
            def m(x: Int, y: Int): Int {
                x + y
            }
            
            def h(g: (Int, Int) -> Int): Int {
                g(1, 2)
            }
            
            def f(g: ((Int, Int) -> Int) -> Int): Int {
                g(m)
            }
            
            f(h)
        """.trimIndent(), 7
        ) {
            it.error is SyntaxError
        }
    }

    @Test
    fun functionReturnTypeTest() {
        failTest(
            """
            def g(x: Int, y: Int): Int {
                x + y
            }
            
            def f(x: Int, y: Int): (Int, Int) -> Int {
                g
            }
            
            f(5, 6)
        """.trimIndent(), 1
        ) {
            it.error is FunctionReturnType
        }
    }

    @Test
    fun functionAssignTest() {
        failTest(
            """
            def g(x: Int, y: Int): Int {
                x + y
            }
            
            val x = g
        """.trimIndent(), 1
        ) {
            it.error is FunctionAssign
        }
    }

    @Test
    fun recordFieldFunctionTypeTest() {
        failTest(
            """
            record A(val x: (Int, Int) -> Int)
        """.trimIndent(), 4
        ) {
            it.error is SyntaxError
        }
    }

    @Test
    fun invalidStandardTypeSubTest() {
        failTest(
            """
            def g(x: Int, y: Int): Int {
                x + y
            }
            
            val x = List(g)
        """.trimIndent(), 1
        ) {
            it.error is InvalidStandardTypeSub
        }
    }

    @Test
    fun triangleRecursionTest() {
        failTest(
            """
            def f(x: Int, y: Int): Int {
                g(x, y)
            }
            
            def g(x: Int, y: Int): Int {
                h(x, y)
            }
            
            def h(x: Int, y: Int): Int {
                f(x, y)
            }
            
            f(1, 2)
        """.trimIndent(), 3
        ) {
            it.error is RecursiveFunctionDetected
        }
    }

    @Test
    fun selfRecursionTest() {
        failTest(
            """
            def g(x: Int, y: Int): Int {
                g(x, y)
            }
            
            g(1, 2)
        """.trimIndent(), 1
        ) {
            it.error is RecursiveFunctionDetected
        }
    }

    @Test
    fun higherOrderRecursionTest() {
        failTest(
            """
            def f(g: (Int, Int) -> Int, x: Int, y: Int): Int {
                g(x, y)
            }
            
            def g(x: Int, y: Int): Int {
                x + y
            }
            
            def h(x: Int, y: Int): Int {
                f(g, x, y)
            }
            
            f(h, 5, 6)
        """.trimIndent(), 2
        ) {
            it.error is RecursiveFunctionDetected
        }
    }

    @Test
    fun mutableListFieldTest() {
        failTest(
            """
            record A(val x: MutableList<Int, 7>)
        """.trimIndent(), 1
        ) {
            it.error is RecordFieldFeatureBan
        }
    }

    @Test
    fun mutableListTypeArgTest() {
        failTest(
            """
            record A<T>(val x: T)
            
            val y = MutableList<Int, 3>(1, 2, 3)
            val a = A(y)
        """.trimIndent(), 1
        ) {
            it.error is TypeArgFeatureBan
        }
    }

    @Test
    fun mutableListParamTest() {
        failTest(
            """
            def f(x: MutableList<Int, 3>): Int {
                x[0]
            }
        """.trimIndent(), 1
        ) {
            it.error is FormalParamFeatureBan
        }
    }

    @Test
    fun mutableListReturnTypeTest() {
        failTest(
            """
            def f(x: Int): MutableList<Int, 3> {
                MutableList<Int, 3>(x, 2, 3)
            }
        """.trimIndent(), 1
        ) {
            it.error is ReturnTypeFeatureBan
        }
    }

    @Test
    fun mutableSetFieldTest() {
        failTest(
            """
            record A(val x: MutableSet<Int, 7>)
        """.trimIndent(), 1
        ) {
            it.error is RecordFieldFeatureBan
        }
    }

    @Test
    fun mutableSetTypeArgTest() {
        failTest(
            """
            record A<T>(val x: T)
            
            val y = MutableSet<Int, 3>(1, 2, 3)
            val a = A(y)
        """.trimIndent(), 1
        ) {
            it.error is TypeArgFeatureBan
        }
    }

    @Test
    fun mutableSetParamTest() {
        failTest(
            """
            def f(x: MutableSet<Int, 3>): Boolean {
                x.contains(0)
            }
        """.trimIndent(), 1
        ) {
            it.error is FormalParamFeatureBan
        }
    }

    @Test
    fun mutableSetReturnTypeTest() {
        failTest(
            """
            def f(x: Int): MutableSet<Int, 3> {
                MutableSet<Int, 3>(x, 2, 3)
            }
        """.trimIndent(), 1
        ) {
            it.error is ReturnTypeFeatureBan
        }
    }

    @Test
    fun mutableDictionaryFieldTest() {
        failTest(
            """
            record A(val x: MutableDictionary<Int, Int, 7>)
        """.trimIndent(), 1
        ) {
            it.error is RecordFieldFeatureBan
        }
    }

    @Test
    fun mutableDictionaryTypeArgTest() {
        failTest(
            """
            record A<T>(val x: T)
            
            val y = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            val a = A(y)
        """.trimIndent(), 1
        ) {
            it.error is TypeArgFeatureBan
        }
    }

    @Test
    fun mutableDictionaryParamTest() {
        failTest(
            """
            def f(x: MutableDictionary<Int, Int, 3>): Boolean {
                x.contains(0)
            }
        """.trimIndent(), 1
        ) {
            it.error is FormalParamFeatureBan
        }
    }

    @Test
    fun mutableDictionaryReturnTypeTest() {
        failTest(
            """
            def f(x: Int): MutableDictionary<Int, Int, 3> {
                MutableDictionary<Int, Int, 3>(x to 1, 2 to 3, 3 to 4)
            }
        """.trimIndent(), 1
        ) {
            it.error is ReturnTypeFeatureBan
        }
    }

    @Test
    fun banNestedFunctionTest() {
        failTest(
            """
            def f(x: Int): Int {
                def g(y: Int): Int {
                    y
                }
                x
            }
        """.trimIndent(), 1
        ) {
            it.error is InvalidDefinitionLocation
        }
    }

    @Test
    fun banExplicitPluginInstantiationTest() {
        failTest(
            """
            val x = 5.5
            x.add<12>(5.5)
        """.trimIndent(), 1
        ) {
            it.error is CannotExplicitlyInstantiate
        }
    }
}