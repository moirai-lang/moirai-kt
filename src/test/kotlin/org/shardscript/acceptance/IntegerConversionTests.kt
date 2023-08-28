package org.shardscript.acceptance

import org.junit.Test

class IntegerConversionTests {
    @Test
    fun s8OutputShort() {
        val input = """
        val x = 100s8
        x.toSigned16()
        ^^^^^
        100s16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s8OutputInt() {
        val input = """
        val x = 100s8
        x.toSigned32()
        ^^^^^
        100
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s8OutputLong() {
        val input = """
        val x = 100s8
        x.toSigned64()
        ^^^^^
        100s64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s8OutputByte() {
        val input = """
        val x = 100s8
        x.toUnsigned8()
        ^^^^^
        100u8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s8OutputUShort() {
        val input = """
        val x = 100s8
        x.toUnsigned16()
        ^^^^^
        100u16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s8OutputUInt() {
        val input = """
        val x = 100s8
        x.toUnsigned32()
        ^^^^^
        100u32
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s8OutputULong() {
        val input = """
        val x = 100s8
        x.toUnsigned64()
        ^^^^^
        100u64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s16OutputSByte() {
        val input = """
        val x = 100s16
        x.toSigned8()
        ^^^^^
        100s8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s16OutputInt() {
        val input = """
        val x = 100s16
        x.toSigned32()
        ^^^^^
        100
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s16OutputLong() {
        val input = """
        val x = 100s16
        x.toSigned64()
        ^^^^^
        100s64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s16OutputByte() {
        val input = """
        val x = 100s16
        x.toUnsigned8()
        ^^^^^
        100u8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s16OutputUShort() {
        val input = """
        val x = 100s16
        x.toUnsigned16()
        ^^^^^
        100u16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s16OutputUInt() {
        val input = """
        val x = 100s16
        x.toUnsigned32()
        ^^^^^
        100u32
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s16OutputULong() {
        val input = """
        val x = 100s16
        x.toUnsigned64()
        ^^^^^
        100u64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s32OutputSByte() {
        val input = """
        val x = 100
        x.toSigned8()
        ^^^^^
        100s8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s32OutputShort() {
        val input = """
        val x = 100
        x.toSigned16()
        ^^^^^
        100s16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s32OutputLong() {
        val input = """
        val x = 100
        x.toSigned64()
        ^^^^^
        100s64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s32OutputByte() {
        val input = """
        val x = 100
        x.toUnsigned8()
        ^^^^^
        100u8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s32OutputUShort() {
        val input = """
        val x = 100
        x.toUnsigned16()
        ^^^^^
        100u16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s32OutputUInt() {
        val input = """
        val x = 100
        x.toUnsigned32()
        ^^^^^
        100u32
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s32OutputULong() {
        val input = """
        val x = 100
        x.toUnsigned64()
        ^^^^^
        100u64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s64OutputSByte() {
        val input = """
        val x = 100s64
        x.toSigned8()
        ^^^^^
        100s8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s64OutputShort() {
        val input = """
        val x = 100s64
        x.toSigned16()
        ^^^^^
        100s16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s64OutputInt() {
        val input = """
        val x = 100s64
        x.toSigned32()
        ^^^^^
        100
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s64OutputByte() {
        val input = """
        val x = 100s64
        x.toUnsigned8()
        ^^^^^
        100u8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s64OutputUShort() {
        val input = """
        val x = 100s64
        x.toUnsigned16()
        ^^^^^
        100u16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s64OutputUInt() {
        val input = """
        val x = 100s64
        x.toUnsigned32()
        ^^^^^
        100u32
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun s64OutputULong() {
        val input = """
        val x = 100s64
        x.toUnsigned64()
        ^^^^^
        100u64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u8OutputSByte() {
        val input = """
        val x = 100u8
        x.toSigned8()
        ^^^^^
        100s8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u8OutputShort() {
        val input = """
        val x = 100u8
        x.toSigned16()
        ^^^^^
        100s16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u8OutputInt() {
        val input = """
        val x = 100u8
        x.toSigned32()
        ^^^^^
        100
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u8OutputLong() {
        val input = """
        val x = 100u8
        x.toSigned64()
        ^^^^^
        100s64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u8OutputUShort() {
        val input = """
        val x = 100u8
        x.toUnsigned16()
        ^^^^^
        100u16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u8OutputUInt() {
        val input = """
        val x = 100u8
        x.toUnsigned32()
        ^^^^^
        100u32
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u8OutputULong() {
        val input = """
        val x = 100u8
        x.toUnsigned64()
        ^^^^^
        100u64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u16OutputSByte() {
        val input = """
        val x = 100u16
        x.toSigned8()
        ^^^^^
        100s8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u16OutputShort() {
        val input = """
        val x = 100u16
        x.toSigned16()
        ^^^^^
        100s16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u16OutputInt() {
        val input = """
        val x = 100u16
        x.toSigned32()
        ^^^^^
        100
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u16OutputLong() {
        val input = """
        val x = 100u16
        x.toSigned64()
        ^^^^^
        100s64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u16OutputByte() {
        val input = """
        val x = 100u16
        x.toUnsigned8()
        ^^^^^
        100u8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u16OutputUInt() {
        val input = """
        val x = 100u16
        x.toUnsigned32()
        ^^^^^
        100u32
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u16OutputULong() {
        val input = """
        val x = 100u16
        x.toUnsigned64()
        ^^^^^
        100u64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u32OutputSByte() {
        val input = """
        val x = 100u32
        x.toSigned8()
        ^^^^^
        100s8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u32OutputShort() {
        val input = """
        val x = 100u32
        x.toSigned16()
        ^^^^^
        100s16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u32OutputInt() {
        val input = """
        val x = 100u32
        x.toSigned32()
        ^^^^^
        100
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u32OutputLong() {
        val input = """
        val x = 100u32
        x.toSigned64()
        ^^^^^
        100s64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u32OutputByte() {
        val input = """
        val x = 100u32
        x.toUnsigned8()
        ^^^^^
        100u8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u32OutputUShort() {
        val input = """
        val x = 100u32
        x.toUnsigned16()
        ^^^^^
        100u16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u32OutputULong() {
        val input = """
        val x = 100u32
        x.toUnsigned64()
        ^^^^^
        100u64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u64OutputSByte() {
        val input = """
        val x = 100u64
        x.toSigned8()
        ^^^^^
        100s8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u64OutputShort() {
        val input = """
        val x = 100u64
        x.toSigned16()
        ^^^^^
        100s16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u64OutputInt() {
        val input = """
        val x = 100u64
        x.toSigned32()
        ^^^^^
        100
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u64OutputLong() {
        val input = """
        val x = 100u64
        x.toSigned64()
        ^^^^^
        100s64
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u64OutputByte() {
        val input = """
        val x = 100u64
        x.toUnsigned8()
        ^^^^^
        100u8
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u64OutputUShort() {
        val input = """
        val x = 100u64
        x.toUnsigned16()
        ^^^^^
        100u16
    """.trimIndent()
        splitTest(input)
    }

    @Test
    fun u64OutputUInt() {
        val input = """
        val x = 100u64
        x.toUnsigned32()
        ^^^^^
        100u32
    """.trimIndent()
        splitTest(input)
    }
}