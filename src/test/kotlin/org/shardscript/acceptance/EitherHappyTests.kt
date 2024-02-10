package org.shardscript.acceptance

import org.junit.jupiter.api.Test

class EitherHappyTests {
    @Test
    fun rightEitherTest() {
        splitTest(
            """
            def f(x: Decimal<16>): Either<Int, Decimal<16>> {
                Right(x)
            }
            
            val o = f(5.5)
            o
            ^^^^^
            Right(5.5)
        """.trimIndent()
        )
    }

    @Test
    fun leftEitherTest() {
        splitTest(
            """
            def f(x: Int): Either<Int, Decimal<16>> {
                Left(x)
            }
            
            val o = f(5)
            o
            ^^^^^
            Left(5)
        """.trimIndent()
        )
    }
}