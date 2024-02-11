package moirai.acceptance

import org.junit.jupiter.api.Test

class ExpressionTests {
    @Test
    fun plus() {
        splitTest("5 + 6^^^^^11")
    }

    @Test
    fun minus() {
        splitTest("5 - 1^^^^^4")
    }

    @Test
    fun mul() {
        splitTest("5 * 7^^^^^35")
    }

    @Test
    fun div() {
        splitTest("10 / 5^^^^^2")
    }

    @Test
    fun mod() {
        splitTest("5 % 2^^^^^1")
    }

    @Test
    fun orderOfOperations() {
        splitTest("5 + 2 * 6 + 9 / 3^^^^^20")
    }

    @Test
    fun testGreaterThan() {
        splitTest("5 > 6^^^^^false")
    }

    @Test
    fun testLessThan() {
        splitTest("5 < 6^^^^^true")
    }

    @Test
    fun testAnd() {
        splitTest("5 < 6 && 7 < 8^^^^^true")
    }

    @Test
    fun testOr() {
        splitTest("5 < 6 || 9 < 8^^^^^true")
    }

    @Test
    fun testComplexExpr() {
        splitTest("5 + 2 * 3 < 7 * 8 + 9 && false || 7 + 3 / 1 > 45 / 9^^^^^true")
    }

    @Test
    fun plusDec() {
        splitTest("5.0 + 6.0^^^^^11.0")
    }

    @Test
    fun minusDec() {
        splitTest("5.0 - 1.0^^^^^4.0")
    }

    @Test
    fun mulDec() {
        splitTest("5.0 * 7.0^^^^^35.0")
    }

    @Test
    fun divDec() {
        splitTest("10.0 / 5.0^^^^^2.0")
    }

    @Test
    fun modDec() {
        splitTest("5.0 % 2.0^^^^^1.0")
    }

    @Test
    fun orderOfOperationsDec() {
        splitTest("5.0 + 2.0 * 6.0 + 9.0 / 3.0^^^^^20.0")
    }

    @Test
    fun testGreaterThanDec() {
        splitTest("5.0 > 6.0^^^^^false")
    }

    @Test
    fun testLessThanDec() {
        splitTest("5.0 < 6.0^^^^^true")
    }

    @Test
    fun testAndDec() {
        splitTest("5.0 < 6.0 && 7.0 < 8.0^^^^^true")
    }

    @Test
    fun testOrDec() {
        splitTest("5.0 < 6.0 || 9.0 < 8.0^^^^^true")
    }

    @Test
    fun testComplexExprDec() {
        splitTest("5.0 + 2.0 * 3.0 < 7.0 * 8.0 + 9.0 && false || 7.0 + 3.0 / 1.0 > 45.0 / 9.0^^^^^true")
    }

    @Test
    fun testUnaryNot() {
        splitTest("!true^^^^^false")
    }
}