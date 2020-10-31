package com.tsikhe.shardscript.acceptance

import org.junit.Test

class DictionaryHappyTests {
    @Test
    fun basicDictionaryTest() {
        val input = """
        val x = Dictionary(1 to 2, 2 to 3, 3 to 4)
        x[1]
        ^^^^^
        2
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun sizeDictionaryTest() {
        val input = """
        val x = Dictionary(1 to 2, 2 to 3, 3 to 4)
        x.size
        ^^^^^
        3
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicDictionaryContainsTest() {
        val input = """
        val x = Dictionary(1 to 2, 2 to 3, 3 to 4)
        x.contains(1)
        ^^^^^
        true
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicDictionaryContainsNegativeTest() {
        val input = """
        val x = Dictionary(1 to 2, 2 to 3, 3 to 4)
        x.contains(5)
        ^^^^^
        false
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicExplicitDictionaryTest() {
        val input = """
        val x = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
        x[1]
        ^^^^^
        2
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicDictionaryContainsExplicitTest() {
        val input = """
        val x = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
        x.contains(1)
        ^^^^^
        true
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicDictionaryContainsNegativeExplicitTest() {
        val input = """
        val x = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
        x.contains(5)
        ^^^^^
        false
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicMutableDictionaryTest() {
        val input = """
        val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
        x[1]
        ^^^^^
        2
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun basicMutableDictionaryContainsTest() {
        val input = """
        val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
        x.contains(1)
        ^^^^^
        true
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun setMutableDictionaryTest() {
        val input = """
        val x = MutableDictionary<Int, Int, 4>(1 to 2, 2 to 3, 3 to 4)
        x[4] = 5
        x[4]
        ^^^^^
        5
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun removeMutableDictionaryTest() {
        val input = """
        val x = MutableDictionary<Int, Int, 4>(1 to 2, 2 to 3, 3 to 4)
        x.remove(1)
        x.contains(1)
        ^^^^^
        false
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun sizeMutableDictionaryTest() {
        val input = """
        val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
        x.size
        ^^^^^
        3
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun equalsDictionaryTest() {
        splitTest(
            """
            val x = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            val y = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            
            x == y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun equalsMutableDictionaryTest() {
        splitTest(
            """
            val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            val y = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            
            x == y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun equalsNegativeDictionaryTest() {
        splitTest(
            """
            val x = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 7)
            val y = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            
            x == y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun equalsMutableNegativeDictionaryTest() {
        splitTest(
            """
            val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 7)
            val y = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            
            x == y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsDictionaryTest() {
        splitTest(
            """
            val x = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 7)
            val y = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            
            x != y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsMutableDictionaryTest() {
        splitTest(
            """
            val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 7)
            val y = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            
            x != y
            ^^^^^
            true
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsNegativeDictionaryTest() {
        splitTest(
            """
            val x = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            val y = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            
            x != y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun notEqualsNegativeMutableDictionaryTest() {
        splitTest(
            """
            val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            val y = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            
            x != y
            ^^^^^
            false
        """.trimIndent()
        )
    }

    @Test
    fun mutableDictionaryToImmutableTest() {
        splitTest(
            """
                val x = MutableDictionary<Int, Int, 10>(1 to 2, 2 to 3, 3 to 4, 4 to 5, 5 to 6)
                val y = x.toDictionary()
                y is Dictionary<Int, Int, 10>
                ^^^^^
                true
            """.trimIndent()
        )
    }
}
