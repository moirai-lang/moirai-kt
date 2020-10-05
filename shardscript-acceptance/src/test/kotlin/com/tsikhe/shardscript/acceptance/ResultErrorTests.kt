package com.tsikhe.shardscript.acceptance

import com.tsikhe.shardscript.semantics.core.TypeMismatch
import org.junit.Test

class ResultErrorTests {
    @Test
    fun failureResultTypeMismatchTest() {
        failTest(
            """
            def f(x: Decimal<16>): Result<Int, Decimal<16>> {
                Failure(x)
            }
            
            val o = f(13.3)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }

    @Test
    fun successResultTypeMismatchTest() {
        failTest(
            """
            def f(x: Int): Result<Int, Decimal<16>> {
                Success(x)
            }
            
            val o = f(13)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }
}