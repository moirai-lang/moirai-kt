package com.tsikhe.shardscript.acceptance

import com.tsikhe.shardscript.semantics.core.TypeMismatch
import org.junit.Test

class OptionErrorTests {
    @Test
    fun someOptionTypeMismatchTest() {
        failTest(
            """
            def f(x: Decimal<16>): Option<Int> {
                Some(x)
            }
            
            val o = f(13.3)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }
}
