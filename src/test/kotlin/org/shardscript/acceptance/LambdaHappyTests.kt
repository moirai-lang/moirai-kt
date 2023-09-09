package org.shardscript.acceptance

import org.junit.jupiter.api.Test

class LambdaHappyTests {
    @Test
    fun applyHigherOrderTest() {
        splitTest(
            """
            def f(g: (Int, Int) -> Int, x: Int, y: Int): Int {
                g(x, y)
            }
            
            f(lambda (x: Int, y: Int) -> x + y, 5, 6)
            ^^^^^
            11
        """.trimIndent()
        )
    }
}