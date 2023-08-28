package org.shardscript.acceptance

import org.junit.Test

class EqualityTests {
    @Test
    fun tests32EqualsTest() {
        splitTest("2 == 3^^^^^false")
    }

    @Test
    fun tests32NegativeEqualsTest() {
        splitTest("2 == 2^^^^^true")
    }

    @Test
    fun tests32NotEqualsTest() {
        splitTest("2 != 3^^^^^true")
    }

    @Test
    fun tests32NegativeNotEqualsTest() {
        splitTest("2 != 2^^^^^false")
    }

    @Test
    fun testDecimalEqualsTest() {
        splitTest("2.0 == 3.0^^^^^false")
    }

    @Test
    fun testDecimalNegativeEqualsTest() {
        splitTest("2.0 == 2.0^^^^^true")
    }

    @Test
    fun testDecimalNotEqualsTest() {
        splitTest("2.0 != 3.0^^^^^true")
    }

    @Test
    fun testDecimalNegativeNotEqualsTest() {
        splitTest("2.0 != 2.0^^^^^false")
    }

    @Test
    fun testBooleanEqualsTest() {
        splitTest("true == false^^^^^false")
    }

    @Test
    fun testBooleanNegativeEqualsTest() {
        splitTest("true == true^^^^^true")
    }

    @Test
    fun testBooleanNotEqualsTest() {
        splitTest("true != false^^^^^true")
    }

    @Test
    fun testBooleanNegativeNotEqualsTest() {
        splitTest("true != true^^^^^false")
    }

    @Test
    fun testCharEqualsTest() {
        splitTest("'c' == 'a'^^^^^false")
    }

    @Test
    fun testCharNegativeEqualsTest() {
        splitTest("'c' == 'c'^^^^^true")
    }

    @Test
    fun testCharNotEqualsTest() {
        splitTest("'c' != 'a'^^^^^true")
    }

    @Test
    fun testCharNegativeNotEqualsTest() {
        splitTest("'c' != 'c'^^^^^false")
    }

    @Test
    fun testStringEqualsTest() {
        splitTest("\"c\" == \"a\"^^^^^false")
    }

    @Test
    fun testStringNegativeEqualsTest() {
        splitTest("\"c\" == \"c\"^^^^^true")
    }

    @Test
    fun testStringNotEqualsTest() {
        splitTest("\"c\" != \"a\"^^^^^true")
    }

    @Test
    fun testStringNegativeNotEqualsTest() {
        splitTest("\"c\" != \"c\"^^^^^false")
    }
}