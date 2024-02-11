package moirai.acceptance

import org.junit.jupiter.api.Test
import moirai.eval.SetValue

class SetHappyTests {
    @Test
    fun basicSetContainsTest() {
        val input = """
        val x = Set(1, 2, 3)
        x.contains(1)
        ^^^^^
        true
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun sizeSetTest() {
        val input = """
        val x = Set(1, 2, 3)
        x.size
        ^^^^^
        3
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicExplicitSetContainsTest() {
        val input = """
        val x = Set<Int, 3>(1, 2, 3)
        x.contains(1)
        ^^^^^
        true
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicSetContainsNegativeTest() {
        val input = """
        val x = Set(1, 2, 3)
        x.contains(4)
        ^^^^^
        false
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicExplicitSetContainsNegativeTest() {
        val input = """
        val x = Set<Int, 3>(1, 2, 3)
        x.contains(4)
        ^^^^^
        false
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicExplicitMutableSetContainsTest() {
        val input = """
        val x = MutableSet<Int, 3>(1, 2, 3)
        x.contains(1)
        ^^^^^
        true
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicExplicitMutableSetContainsNegativeTest() {
        val input = """
        val x = MutableSet<Int, 3>(1, 2, 3)
        x.contains(4)
        ^^^^^
        false
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun sizeMutableSetTest() {
        val input = """
        val x = MutableSet<Int, 3>(1, 2, 3)
        x.size
        ^^^^^
        3
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicExplicitMutableSetAddTest() {
        val input = """
        val x = MutableSet<Int, 4>(1, 2, 3)
        x.add(4)
        x.contains(4)
        ^^^^^
        true
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicExplicitMutableSetRemoveTest() {
        val input = """
        val x = MutableSet<Int, 3>(1, 2, 3)
        x.remove(3)
        x.contains(3)
        ^^^^^
        false
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun equalsSetTest() {
        splitTest(
            """
            val x = Set<Int, 3>(1, 2, 3)
            val y = Set<Int, 3>(1, 2, 3)
            
            x == y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun equalsMutableSetTest() {
        splitTest(
            """
            val x = MutableSet<Int, 3>(1, 2, 3)
            val y = MutableSet<Int, 3>(1, 2, 3)
            
            x == y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun equalsNegativeSetTest() {
        splitTest(
            """
            val x = Set<Int, 3>(1, 2, 7)
            val y = Set<Int, 3>(1, 2, 3)
            
            x == y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun equalsMutableNegativeSetTest() {
        splitTest(
            """
            val x = MutableSet<Int, 3>(1, 2, 7)
            val y = MutableSet<Int, 3>(1, 2, 3)
            
            x == y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsSetTest() {
        splitTest(
            """
            val x = Set<Int, 3>(1, 2, 7)
            val y = Set<Int, 3>(1, 2, 3)
            
            x != y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsMutableSetTest() {
        splitTest(
            """
            val x = MutableSet<Int, 3>(1, 2, 7)
            val y = MutableSet<Int, 3>(1, 2, 3)
            
            x != y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsNegativeSetTest() {
        splitTest(
            """
            val x = Set<Int, 3>(1, 2, 3)
            val y = Set<Int, 3>(1, 2, 3)
            
            x != y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsNegativeMutableSetTest() {
        splitTest(
            """
            val x = MutableSet<Int, 3>(1, 2, 3)
            val y = MutableSet<Int, 3>(1, 2, 3)
            
            x != y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun mutableSetToImmutableTest() {
        typeTest(
            """
                val x = MutableSet<Int, 10>(1, 2, 3, 4, 5)
                x.toSet()
            """.trimIndent()
        ) { it is SetValue }
    }
}