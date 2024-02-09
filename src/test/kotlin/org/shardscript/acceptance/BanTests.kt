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
        """.trimIndent(), 2
        ) {
            it.error is CannotRefFunctionParam || it.error is InvalidRef
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
        """.trimIndent(), 5
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
        """.trimIndent(), 3
        ) {
            it.error is SyntaxError
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
            it.error is InvalidRef
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
            val z = List(lambda (x: Int, y: Int) -> x + y)
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