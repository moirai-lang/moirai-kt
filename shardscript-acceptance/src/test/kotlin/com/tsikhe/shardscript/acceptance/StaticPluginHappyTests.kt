package com.tsikhe.shardscript.acceptance

import org.junit.Test

class StaticPluginHappyTests {
    @Test
    fun basicRangeTest() {
        splitTest(
            """
            val r = range(0, 10)
            r
            ^^^^^
            List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        """.trimIndent()
        )
    }

    @Test
    fun basicRangeReverseTest() {
        splitTest(
            """
            val r = range(10, 0)
            r
            ^^^^^
            List(10, 9, 8, 7, 6, 5, 4, 3, 2, 1)
        """.trimIndent()
        )
    }

    @Test
    fun basicRandomTest() {
        splitTest(
            """
            map(x in range(0, 100)) {
                val r = random(45, 90)
                r < 90 && r >= 45
            }
            ^^^^^
            map(x in range(0, 100)) {
                true
            }
        """.trimIndent()
        )
    }

    @Test
    fun basicRandomReverseTest() {
        splitTest(
            """
            map(x in range(0, 100)) {
                val r = random(90, 45)
                r <= 90 && r > 45
            }
            ^^^^^
            map(x in range(0, 100)) {
                true
            }
        """.trimIndent()
        )
    }
}
