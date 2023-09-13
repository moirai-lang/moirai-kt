package org.shardscript.acceptance

import org.shardscript.semantics.core.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class LiteralTests {
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
    fun intLiteralIsTest() {
        splitTest("5 is Int^^^^^true", TestArchitecture)
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