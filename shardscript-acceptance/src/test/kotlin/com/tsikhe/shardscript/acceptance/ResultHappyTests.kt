package com.tsikhe.shardscript.acceptance

import org.junit.Test

class ResultHappyTests {
    @Test
    fun successResultTest() {
        splitTest(
            """
            def f(x: Decimal<16>): Result<Int, Decimal<16>> {
                Success(x)
            }
            
            val o = f(5.5)
            o
            ^^^^^
            Success(5.5)
        """.trimIndent()
        )
    }

    @Test
    fun failureResultTest() {
        splitTest(
            """
            def f(x: Int): Result<Int, Decimal<16>> {
                Failure(x)
            }
            
            val o = f(5)
            o
            ^^^^^
            Failure(5)
        """.trimIndent()
        )
    }
}
