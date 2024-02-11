package moirai.acceptance

import org.junit.jupiter.api.Test

class BranchHappyTests {
    @Test
    fun ifExpressionTrueBranchTest() {
        splitTest(
            """
            def max(x: Int, y: Int): Int {
                if(x >= y) {
                    x
                } else {
                    y
                }
            }
            
            max(8, 6)
            ^^^^^
            8
        """.trimIndent()
        )
    }

    @Test
    fun ifExpressionFalseBranchTest() {
        splitTest(
            """
            def max(x: Int, y: Int): Int {
                if(x >= y) {
                    x
                } else {
                    y
                }
            }
            
            max(6, 8)
            ^^^^^
            8
        """.trimIndent()
        )
    }

    @Test
    fun ifElseIfTest() {
        splitTest(
            """
                val x = 5
                val y = 7
                if(x > 10) {
                    20
                } else if(y < 10) {
                    3
                } else {
                    84
                }
                ^^^^^
                3
            """.trimIndent()
        )
    }
}