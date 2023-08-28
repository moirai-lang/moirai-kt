package org.shardscript.acceptance

import org.shardscript.semantics.core.*
import org.junit.Test

class SetErrorTests {
    @Test
    fun incorrectOmicronSetTest() {
        failTest(
            """
            val x = Set<2, 3>(1, 2, 3)
            x.contains(1)
        """.trimIndent(), 3
        ) {
            it.error is InvalidStandardTypeSub
        }
    }

    @Test
    fun incorrectStandardSetTest() {
        failTest(
            """
            val x = Set<Int, Int>(1, 2, 3)
            x.contains(1)
        """.trimIndent(), 2
        ) {
            it.error is InvalidOmicronTypeSub
        }
    }

    @Test
    fun tooManyElementsSetTest() {
        failTest(
            """
            val x = Set<Int, 2>(1, 2, 3)
            x.contains(1)
        """.trimIndent(), 1
        ) {
            it.error is TooManyElements
        }
    }

    @Test
    fun incorrectOmicronMutableSetTest() {
        failTest(
            """
            val x = MutableSet<2, 3>(1, 2, 3)
            x.contains(1)
        """.trimIndent(), 3
        ) {
            it.error is InvalidStandardTypeSub
        }
    }

    @Test
    fun incorrectStandardMutableSetTest() {
        failTest(
            """
            val x = MutableSet<Int, Int>(1, 2, 3)
            x.contains(1)
        """.trimIndent(), 3
        ) {
            it.error is InvalidOmicronTypeSub
        }
    }

    @Test
    fun typeRequiresExplicitMutableSetTest() {
        failTest(
            """
            val x = MutableSet(1, 2, 3)
            x.contains(1)
        """.trimIndent(), 1
        ) {
            it.error is TypeRequiresExplicit
        }
    }

    @Test
    fun tooManyElementsMutableSetTest() {
        failTest(
            """
            val x = MutableSet<Int, 2>(1, 2, 3)
            x.contains(1)
        """.trimIndent(), 1
        ) {
            it.error is TooManyElements
        }
    }

    @Test
    fun runtimeOmicronViolationMutableSetTest() {
        failTest(
            """
            val x = MutableSet<Int, 3>(1, 2, 3)
            x.add(4)
            x.contains(1)
        """.trimIndent(), 1
        ) {
            it.error is RuntimeOmicronViolation
        }
    }

    @Test
    fun invalidSourceTypeForEachMutableSetTest() {
        failTest(
            """
            val x = MutableSet<Int, 3>(1, 2, 3)
            for(y in x) {
                y
            }
        """.trimIndent(), 1
        ) {
            it.error is ForEachFeatureBan
        }
    }
}