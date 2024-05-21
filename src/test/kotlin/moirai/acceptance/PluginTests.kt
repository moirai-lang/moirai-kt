package moirai.acceptance

import moirai.semantics.core.CostOverLimit
import org.junit.jupiter.api.Test

class PluginTests {
    @Test
    fun basicPluginTest() {
        val input = """
        val x = simplePlugin(5, 6)
        x
        ^^^^^
        11
    """.trimIndent()
        splitTestPlugins(input)
    }

    @Test
    fun paramPluginTest() {
        val input = """
        val x = paramPlugin(List(5, 6))
        x
        ^^^^^
        6
    """.trimIndent()
        splitTestPlugins(input)
    }

    @Test
    fun basicPluginNamedCostTest() {
        val input = """
        val x = simplePluginNamed(5, 6)
        x
        ^^^^^
        11
    """.trimIndent()
        splitTestPlugins(input)
    }

    @Test
    fun paramPluginNamedCostTest() {
        val input = """
        val x = paramPluginNamed(List(5, 6, 7, 8, 9))
        x
    """.trimIndent()
        failTestPlugins(input, 1) {
            it.error is CostOverLimit
        }
    }
}