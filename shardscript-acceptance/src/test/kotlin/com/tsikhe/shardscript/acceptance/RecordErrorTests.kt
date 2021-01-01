package com.tsikhe.shardscript.acceptance

import com.tsikhe.shardscript.semantics.core.DuplicateTypeParameter
import com.tsikhe.shardscript.semantics.core.IncorrectNumberOfTypeArgs
import com.tsikhe.shardscript.semantics.core.RecursiveRecordDetected
import com.tsikhe.shardscript.semantics.core.SymbolHasNoParameters
import org.junit.Test

class RecordErrorTests {
    @Test
    fun duplicateTypeParamsTest() {
        failTest(
            """
            record A<T, T>(val x: T, val y: T)
            
            val a = A<Int, Int>(5, 6)
            
            a.x
        """.trimIndent(), 1
        ) {
            it.error is DuplicateTypeParameter
        }
    }

    @Test
    fun noSuchParameterTest() {
        failTest(
            """
            record A(val x: Int, val y: Int)
            
            val a = A<Int>(5, 6)
            
            a.x
        """.trimIndent(), 1
        ) {
            it.error is SymbolHasNoParameters
        }
    }

    @Test
    fun tooManyParameters() {
        failTest(
            """
            record A<T>(val x: T, val y: T)
            
            val a = A<Int, Int>(5, 6)
            
            a.x
        """.trimIndent(), 1
        ) {
            it.error is IncorrectNumberOfTypeArgs
        }
    }

    @Test
    fun recursiveRecordTest() {
        failTest(
            """
            record A(val b: B)
            record B(val a: A)
        """.trimIndent(), 2
        ) {
            it.error is RecursiveRecordDetected
        }
    }

    @Test
    fun recursiveParameterizedRecordTest() {
        failTest(
            """
            record A<T>(val b: B, val t: T)
            record B<T>(val a: A, val t: T)
        """.trimIndent(), 2
        ) {
            it.error is RecursiveRecordDetected
        }
    }
}