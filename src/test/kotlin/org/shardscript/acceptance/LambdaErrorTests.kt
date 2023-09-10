package org.shardscript.acceptance

import org.junit.jupiter.api.Test
import org.shardscript.semantics.core.SymbolHasNoParameters

class LambdaErrorTests {
    @Test
    fun noSuchParameterHigherOrderTest() {
        failTest(
            """
            def f(g: (Int, Int) -> Int, x: Int, y: Int): Int {
                g(x, y)
            }
            
            f<Int>(lambda (x: Int, y: Int) -> x + y, 5, 6)
        """.trimIndent(), 1
        ) {
            it.error is SymbolHasNoParameters
        }
    }
}