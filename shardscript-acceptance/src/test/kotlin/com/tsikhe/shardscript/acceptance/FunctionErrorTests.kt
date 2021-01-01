package com.tsikhe.shardscript.acceptance

import com.tsikhe.shardscript.semantics.core.DuplicateTypeParameter
import com.tsikhe.shardscript.semantics.core.InvalidRef
import com.tsikhe.shardscript.semantics.core.SymbolHasNoParameters
import org.junit.Test

class FunctionErrorTests {
    @Test
    fun duplicateTypeParamsTest() {
        failTest(
            """
            def f<T, T>(x: T, y: T): T {
                x + y
            }
            
            f<Int, Int>(5, 6)
        """.trimIndent(), 1
        ) {
            it.error is DuplicateTypeParameter
        }
    }

    @Test
    fun noSuchParameterTest() {
        failTest(
            """
            def f(x: Int, y: Int): Int {
                x + y
            }
            
            f<Int>(5, 6)
        """.trimIndent(), 1
        ) {
            it.error is SymbolHasNoParameters
        }
    }

    @Test
    fun noSuchParameterHigherOrderTest() {
        failTest(
            """
            def f(g: (Int, Int) -> Int, x: Int, y: Int): Int {
                g(x, y)
            }
            
            def g(x: Int, y: Int): Int {
                x + y
            }
            
            f<Int>(g, 5, 6)
        """.trimIndent(), 1
        ) {
            it.error is SymbolHasNoParameters
        }
    }

    @Test
    fun applyGenericHigherOrderTest() {
        failTest(
            """
            def f<T>(g: (T, T) -> T, x: T, y: T): T {
                g(x, y)
            }
            
            def g<S>(x: S, y: S): S {
                x
            }
            
            f(g, 5, 6)
        """.trimIndent(), 1
        ) {
            it.error is InvalidRef
        }
    }

    @Test
    fun applyExplicitGenericHigherOrderTest() {
        failTest(
            """
            def f<T>(g: (T, T) -> T, x: T, y: T): T {
                g(x, y)
            }
            
            def g<S>(x: S, y: S): S {
                x
            }
            
            f<Int>(g, 5, 6)
        """.trimIndent(), 1
        ) {
            it.error is InvalidRef
        }
    }
}