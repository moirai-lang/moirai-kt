package org.shardscript.acceptance

import org.shardscript.semantics.core.*
import org.junit.jupiter.api.Test

class DictionaryErrorTests {
    @Test
    fun incorrectFinDictionaryTest() {
        failTest(
            """
            val x = Dictionary<2, 5, 3>(1 to 2, 2 to 3, 3 to 4)
            x[1]
        """.trimIndent(), 5
        ) {
            it.error is InvalidStandardTypeSub || it.error is ExpectOtherError
        }
    }

    @Test
    fun incorrectStandardDictionaryTest() {
        failTest(
            """
            val x = Dictionary<Int, Int, Int>(1 to 2, 2 to 3, 3 to 4)
            x[1]
        """.trimIndent(), 2
        ) {
            it.error is InvalidFinTypeSub
        }
    }

    @Test
    fun tooManyElementsDictionaryTest() {
        failTest(
            """
            val x = Dictionary<Int, Int, 2>(1 to 2, 2 to 3, 3 to 4)
            x[1]
        """.trimIndent(), 1
        ) {
            it.error is TooManyElements
        }
    }

    @Test
    fun invalidPairsDictionaryTest() {
        failTest(
            """
            val x = Dictionary(1, 2, 3)
            x[1]
        """.trimIndent(), 4
        ) {
            it.error is DictionaryArgsMustBePairs || it.error is TypeInferenceFailed
        }
    }

    @Test
    fun invalidPairsDictionaryExplicitTest() {
        failTest(
            """
            val x = Dictionary<Int, Int, 3>(1, 2, 3)
            x[1]
        """.trimIndent(), 1
        ) {
            it.error is DictionaryArgsMustBePairs
        }
    }

    @Test
    fun invalidSourceTypeForEachDictionaryTest() {
        failTest(
            """
            val x = Dictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            for(y in x) {
                y
            }
        """.trimIndent(), 1
        ) {
            it.error is ForEachFeatureBan
        }
    }

    @Test
    fun incorrectFinMutableDictionaryTest() {
        failTest(
            """
            val x = MutableDictionary<2, 5, 3>(1 to 2, 2 to 3, 3 to 4)
            x[1]
        """.trimIndent(), 5
        ) {
            it.error is InvalidStandardTypeSub || it.error is ExpectOtherError
        }
    }

    @Test
    fun incorrectStandardMutableDictionaryTest() {
        failTest(
            """
            val x = MutableDictionary<Int, Int, Int>(1 to 2, 2 to 3, 3 to 4)
            x[1]
        """.trimIndent(), 3
        ) {
            it.error is InvalidFinTypeSub
        }
    }

    @Test
    fun typeRequiresExplicitMutableDictionaryTest() {
        failTest(
            """
            val x = MutableDictionary(1 to 2, 2 to 3, 3 to 4)
            x[1]
        """.trimIndent(), 1
        ) {
            it.error is TypeRequiresExplicit
        }
    }

    @Test
    fun tooManyElementsMutableDictionaryTest() {
        failTest(
            """
            val x = MutableDictionary<Int, Int, 2>(1 to 2, 2 to 3, 3 to 4)
            x[1]
        """.trimIndent(), 1
        ) {
            it.error is TooManyElements
        }
    }

    @Test
    fun runtimeFinViolationMutableDictionaryTest() {
        failTest(
            """
            val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            x[4] = 5
            x[1]
        """.trimIndent(), 1
        ) {
            it.error is RuntimeFinViolation
        }
    }

    @Test
    fun invalidPairsMutableDictionaryExplicitTest() {
        failTest(
            """
            val x = MutableDictionary<Int, Int, 3>(1, 2, 3)
            x[1]
        """.trimIndent(), 1
        ) {
            it.error is DictionaryArgsMustBePairs
        }
    }

    @Test
    fun invalidSourceTypeForEachMutableDictionaryTest() {
        failTest(
            """
            val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
            for(y in x) {
                y
            }
        """.trimIndent(), 1
        ) {
            it.error is ForEachFeatureBan
        }
    }

    @Test
    fun invalidReturnTypeMutableDictionaryTest() {
        failTest(
            """
            def f(): MutableDictionary<Int, Int, 3> {
                val x = MutableDictionary<Int, Int, 3>(1 to 2, 2 to 3, 3 to 4)
                x
            }
        """.trimIndent(), 1
        ) {
            it.error is ReturnTypeFeatureBan
        }
    }
}