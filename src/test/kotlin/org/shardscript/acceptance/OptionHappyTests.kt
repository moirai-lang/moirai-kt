package org.shardscript.acceptance

import org.junit.jupiter.api.Test

class OptionHappyTests {
    @Test
    fun someOptionTest() {
        splitTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            o
            ^^^^^
            Some(13)
        """.trimIndent()
        )
    }

    @Test
    fun noneOptionTest() {
        splitTest(
            """
            def f(x: Int): Option<Int> {
                None
            }
            
            val o = f(13)
            o
            ^^^^^
            None
        """.trimIndent()
        )
    }
}