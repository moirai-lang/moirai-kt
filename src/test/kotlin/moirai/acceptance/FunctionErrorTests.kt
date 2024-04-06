package moirai.acceptance

import moirai.semantics.core.DuplicateTypeParameter
import moirai.semantics.core.InvalidRef
import moirai.semantics.core.SymbolHasNoParameters
import org.junit.jupiter.api.Test
import moirai.semantics.core.TypeMismatch

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

    @Test
    fun costExprLiteralReturnTypeErrorTest() {
        failTest(
            """
            def maxList<T, M: Fin, N: Fin, O: Fin>(listM: List<T, M>, listN: List<T, N>, listO: List<T, O>): List<T, Max(M, N)> {
                if (listM.size > listN.size) {
                    listM
                } else {
                    listO
                }
            }
                
            maxList(List(1, 2, 3), List(1, 2, 3, 4, 5), List(2, 3, 4, 5, 6, 7, 8, 9))
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }
}