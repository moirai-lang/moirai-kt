package org.shardscript.acceptance

import org.junit.jupiter.api.Test
import org.shardscript.semantics.core.TypeMismatch

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