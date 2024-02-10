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

    @Test
    fun someForEachOptionTest() {
        splitTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            for(s in o) {
                s
            }
            ^^^^^
            Unit
        """.trimIndent()
        )
    }

    @Test
    fun someMapOptionTest() {
        splitTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            map(s in o) {
                s + 1
            }
            ^^^^^
            Some(14)
        """.trimIndent()
        )
    }
}