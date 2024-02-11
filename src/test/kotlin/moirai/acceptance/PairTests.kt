package moirai.acceptance

import org.junit.jupiter.api.Test

class PairTests {
    @Test
    fun basicFirstPairTest() {
        splitTest(
            """
            val a = Pair(1, 2)
            
            a.first
            ^^^^^
            1
        """.trimIndent()
        )
    }

    @Test
    fun basicSecondPairTest() {
        splitTest(
            """
            val a = Pair(1, 2)
            
            a.second
            ^^^^^
            2
        """.trimIndent()
        )
    }

    @Test
    fun basicFirstPairExplicitTest() {
        splitTest(
            """
            val a = Pair<Int, Int>(1, 2)
            
            a.first
            ^^^^^
            1
        """.trimIndent()
        )
    }

    @Test
    fun basicSecondPairExplicitTest() {
        splitTest(
            """
            val a = Pair<Int, Int>(1, 2)
            
            a.second
            ^^^^^
            2
        """.trimIndent()
        )
    }
}