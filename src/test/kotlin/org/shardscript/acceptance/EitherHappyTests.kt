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

    @Test
    fun valInferLeftTest() {
        splitTest(
            """
            val x = 1
            val o = if(1 == x) {
                Left(5)
            } else {
                Right("5")
            }
            match(o) {
                case Left { 7 }
                case Right { 10 }
            }
            ^^^^^
            7
        """.trimIndent()
        )
    }

    @Test
    fun valInferRightTest() {
        splitTest(
            """
            val x = 2
            val o = if(1 == x) {
                Left(5)
            } else {
                Right("5")
            }
            match(o) {
                case Left { 7 }
                case Right { 10 }
            }
            ^^^^^
            10
        """.trimIndent()
        )
    }
}