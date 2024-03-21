package moirai.acceptance

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
}