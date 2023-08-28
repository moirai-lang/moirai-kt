package org.shardscript.acceptance

import org.shardscript.semantics.core.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class LiteralTests {
    @Test
    fun sByteLiteral() {
        val res = testEval("5s8", TestArchitecture)
        if (res is SByteValue) {
            Assertions.assertEquals((5).toByte(), res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun shortLiteral() {
        val res = testEval("5s16", TestArchitecture)
        if (res is ShortValue) {
            Assertions.assertEquals((5).toShort(), res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun intLiteral() {
        val res = testEval("5", TestArchitecture)
        if (res is IntValue) {
            Assertions.assertEquals(5, res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun longLiteral() {
        val res = testEval("5s64", TestArchitecture)
        if (res is LongValue) {
            Assertions.assertEquals((5).toLong(), res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun byteLiteral() {
        val res = testEval("5u8", TestArchitecture)
        if (res is ByteValue) {
            Assertions.assertEquals((5).toUByte(), res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun uShortLiteral() {
        val res = testEval("5u16", TestArchitecture)
        if (res is UShortValue) {
            Assertions.assertEquals((5).toUShort(), res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun uIntLiteral() {
        val res = testEval("5u32", TestArchitecture)
        if (res is UIntValue) {
            Assertions.assertEquals((5).toUInt(), res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun uLongLiteral() {
        val res = testEval("5u64", TestArchitecture)
        if (res is ULongValue) {
            Assertions.assertEquals((5).toULong(), res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun testTrue() {
        val res = testEval("true", TestArchitecture)
        Assertions.assertEquals(BooleanValue(true), res)
    }

    @Test
    fun testFalse() {
        val res = testEval("false", TestArchitecture)
        Assertions.assertEquals(BooleanValue(false), res)
    }

    @Test
    fun decLiteral() {
        val res = testEval("5.0", TestArchitecture)
        assertEqualsDec("5", res)
    }

    @Test
    fun testUnaryNegates32() {
        val res = testEval("-15", TestArchitecture)
        if (res is IntValue) {
            Assertions.assertEquals(-15, res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun testUnaryNegateDecimal() {
        val res = testEval("-15.0", TestArchitecture)
        assertEqualsDec("-15", res)
    }

    @Test
    fun charLiteral() {
        val res = testEval("'c'", TestArchitecture)
        Assertions.assertEquals(CharValue('c'), res)
    }

    @Test
    fun charLiteralEscape() {
        val res = testEval("'\\\''", TestArchitecture)
        Assertions.assertEquals(CharValue('\''), res)
    }

    @Test
    fun stringLiteral() {
        val res = testEval("\"hello world\"", TestArchitecture)
        if (res is StringValue) {
            Assertions.assertEquals("hello world", res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun stringLiteralEscape() {
        val res = testEval("\"hello\\nworld\"", TestArchitecture)
        if (res is StringValue) {
            Assertions.assertEquals("hello\nworld", res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun stringInterpolationLiteral() {
        val res = testEval("\"hello \${65} world\"", TestArchitecture)
        if (res is StringValue) {
            Assertions.assertEquals("hello 65 world", res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun stringLiteralInterpolationEscape() {
        val res = testEval("\"hello\\n \${65} world\"", TestArchitecture)
        if (res is StringValue) {
            Assertions.assertEquals("hello\n 65 world", res.canonicalForm)
        } else {
            fail()
        }
    }

    @Test
    fun sByteLiteralIsTest() {
        splitTest("5s8 is SByte^^^^^true", TestArchitecture)

    }

    @Test
    fun shortLiteralIsTest() {
        splitTest("5s16 is Short^^^^^true", TestArchitecture)
    }

    @Test
    fun intLiteralIsTest() {
        splitTest("5 is Int^^^^^true", TestArchitecture)
    }

    @Test
    fun longLiteralIsTest() {
        splitTest("5s64 is Long^^^^^true", TestArchitecture)
    }

    @Test
    fun byteLiteralIsTest() {
        splitTest("5u8 is Byte^^^^^true", TestArchitecture)
    }

    @Test
    fun uShortLiteralIsTest() {
        splitTest("5u16 is UShort^^^^^true", TestArchitecture)
    }

    @Test
    fun uIntLiteralIsTest() {
        splitTest("5u32 is UInt^^^^^true", TestArchitecture)
    }

    @Test
    fun uLongLiteralIsTest() {
        splitTest("5u64 is ULong^^^^^true", TestArchitecture)
    }

    @Test
    fun testTrueIsTest() {
        splitTest("true is Boolean^^^^^true", TestArchitecture)
    }

    @Test
    fun testFalseIsTest() {
        splitTest("false is Boolean^^^^^true", TestArchitecture)
    }

    @Test
    fun decLiteralIsTest() {
        splitTest("5.0 is Decimal<3>^^^^^true", TestArchitecture)
    }

    @Test
    fun testUnaryNegates32IsTest() {
        splitTest("-15 is Int^^^^^true", TestArchitecture)
    }

    @Test
    fun testUnaryNegateDecimalIsTest() {
        splitTest("-15.0 is Decimal<5>^^^^^true", TestArchitecture)
    }

    @Test
    fun charLiteralIsTest() {
        splitTest("'c' is Char^^^^^true", TestArchitecture)
    }

    @Test
    fun charLiteralEscapeIsTest() {
        splitTest("'\\\'' is Char^^^^^true", TestArchitecture)
    }

    @Test
    fun stringLiteralIsTest() {
        splitTest("\"hello world\" is String<20>^^^^^true", TestArchitecture)
    }

    @Test
    fun stringLiteralEscapeIsTest() {
        splitTest("\"hello\\nworld\" is String<20>^^^^^true", TestArchitecture)
    }

    @Test
    fun stringInterpolationLiteralIsTest() {
        splitTest("\"hello \${65} world\" is String<20>^^^^^true", TestArchitecture)
    }

    @Test
    fun stringLiteralInterpolationEscapeIsTest() {
        splitTest("\"hello\\n \${65} world\" is String<20>^^^^^true", TestArchitecture)
    }
}