package moirai.acceptance

import moirai.semantics.core.InvalidRangeArg
import org.junit.jupiter.api.Test

class StaticPluginErrorTests {
    @Test
    fun basicRangeNegativeTest() {
        failTest(
            """
            val r = range(-1, -10)
            r
        """.trimIndent(), 1
        ) {
            it.error is InvalidRangeArg
        }
    }

    @Test
    fun basicRangeNegativeReverseTest() {
        failTest(
            """
            val r = range(-10, -1)
            r
        """.trimIndent(), 1
        ) {
            it.error is InvalidRangeArg
        }
    }
}