package moirai.acceptance

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import moirai.eval.*

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
        typeTest("5") { it is IntValue }
    }

    @Test
    fun testTrueIsTest() {
        typeTest("true") { it is BooleanValue }
    }

    @Test
    fun testFalseIsTest() {
        typeTest("false") { it is BooleanValue }
    }

    @Test
    fun decLiteralIsTest() {
        typeTest("5.0") { it is DecimalValue }
    }

    @Test
    fun testUnaryNegates32IsTest() {
        typeTest("-15") { it is IntValue }
    }

    @Test
    fun testUnaryNegateDecimalIsTest() {
        typeTest("-15.0") { it is DecimalValue }
    }

    @Test
    fun charLiteralIsTest() {
        typeTest("'c'") { it is CharValue }
    }

    @Test
    fun charLiteralEscapeIsTest() {
        typeTest("'\\\''") { it is CharValue }
    }

    @Test
    fun stringLiteralIsTest() {
        typeTest("\"hello world\"") { it is StringValue }
    }

    @Test
    fun stringLiteralEscapeIsTest() {
        typeTest("\"hello\\nworld\"") { it is StringValue }
    }

    @Test
    fun stringInterpolationLiteralIsTest() {
        typeTest("\"hello \${65} world\"") { it is StringValue }
    }

    @Test
    fun stringLiteralInterpolationEscapeIsTest() {
        typeTest("\"hello\\n \${65} world\"") { it is StringValue }
    }
}