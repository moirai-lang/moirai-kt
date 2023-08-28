package org.shardscript.acceptance

import org.junit.Test

class RecordHappyTests {
    @Test
    fun basicRecordTest() {
        splitTest(
            """
            record A(val x: Int, val y: Int)
            
            val a = A(5, 6)
            
            a.x
            ^^^^^
            5
        """.trimIndent()
        )
    }

    @Test
    fun genericRecordTest() {
        splitTest(
            """
            record A<T>(val x: T, val y: T)
            
            val a = A(5, 6)
            
            a.x + a.y
            ^^^^^
            11
        """.trimIndent()
        )
    }

    @Test
    fun nestedRecordTest() {
        splitTest(
            """
            record A(val x: Int, val y: Int)
            record B(val z: Int, val a: A)
            
            val a = A(5, 6)
            val b = B(7, a)
            
            b.a.x + a.y + b.z
            ^^^^^
            18
        """.trimIndent()
        )
    }

    @Test
    fun nestedGenericRecordTest() {
        splitTest(
            """
            record A<T>(val x: T, val y: T)
            record B<T>(val z: T, val a: A<T>)
            
            val a = A(5, 6)
            val b = B(7, a)
            
            b.a.x + a.y + b.z
            ^^^^^
            18
        """.trimIndent()
        )
    }

    @Test
    fun genericRecordExplicitTest() {
        splitTest(
            """
            record A<T>(val x: T, val y: T)
            
            val a = A<Int>(5, 6)
            
            a.x + a.y
            ^^^^^
            11
        """.trimIndent()
        )
    }

    @Test
    fun nestedGenericRecordExplicitTest() {
        splitTest(
            """
            record A<T>(val x: T, val y: T)
            record B<T>(val z: T, val a: A<T>)
            
            val a = A<Int>(5, 6)
            val b = B<Int>(7, a)
            
            b.a.x + a.y + b.z
            ^^^^^
            18
        """.trimIndent()
        )
    }
}