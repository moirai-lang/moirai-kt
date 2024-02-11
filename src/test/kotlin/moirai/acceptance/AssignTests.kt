package moirai.acceptance

import moirai.semantics.core.ImmutableAssign
import org.junit.jupiter.api.Test

class AssignTests {
    @Test
    fun basicLet() {
        val input = """
        val x = 5 + 6
        x
        ^^^^^
        11
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicLetAssign() {
        val input = """
        val x = 5 + 6
        x = 13
        x
    """.trimIndent()
        failTest(input, 1) {
            it.error is ImmutableAssign
        }
    }

    @Test
    fun basicLetMutable() {
        val input = """
        mutable x = 5 + 6
        x
        ^^^^^
        11
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicLetMutableAssign() {
        val input = """
        mutable x = 5 + 6
        x = 13
        x
        ^^^^^
        13
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun recordAssign() {
        failTest(
            """
            record A(val x: Int, val y: Int)
            val a = A(2, 3)
            a.x = 5
            a.x
        """.trimIndent(), 1
        ) {
            it.error is ImmutableAssign
        }
    }

    @Test
    fun recordAssignMutable() {
        val input = """
        record A(mutable x: Int, mutable y: Int)
        val a = A(2, 3)
        a.x = 13
        a.x
        ^^^^^
        13
    """.trimIndent()
        splitTest(input)
    }
}