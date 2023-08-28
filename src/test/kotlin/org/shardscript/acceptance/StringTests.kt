package org.shardscript.acceptance

import org.junit.jupiter.api.Test

class StringTests {
    @Test
    fun sByteToStringTest() {
        splitTest(
            """
            val x = 42s8
            x.toString()
            ^^^^^
            "42s8"
        """.trimIndent()
        )
    }

    @Test
    fun shortToStringTest() {
        splitTest(
            """
            val x = 42s16
            x.toString()
            ^^^^^
            "42s16"
        """.trimIndent()
        )
    }

    @Test
    fun integerToStringTest() {
        splitTest(
            """
            val x = 42
            x.toString()
            ^^^^^
            "42"
        """.trimIndent()
        )
    }

    @Test
    fun longToStringTest() {
        splitTest(
            """
            val x = 42s64
            x.toString()
            ^^^^^
            "42s64"
        """.trimIndent()
        )
    }

    @Test
    fun byteToStringTest() {
        splitTest(
            """
            val x = 42u8
            x.toString()
            ^^^^^
            "42u8"
        """.trimIndent()
        )
    }

    @Test
    fun uShortToStringTest() {
        splitTest(
            """
            val x = 42u16
            x.toString()
            ^^^^^
            "42u16"
        """.trimIndent()
        )
    }

    @Test
    fun uIntToStringTest() {
        splitTest(
            """
            val x = 42u32
            x.toString()
            ^^^^^
            "42u32"
        """.trimIndent()
        )
    }

    @Test
    fun uLongToStringTest() {
        splitTest(
            """
            val x = 42u64
            x.toString()
            ^^^^^
            "42u64"
        """.trimIndent()
        )
    }

    @Test
    fun unitToStringTest() {
        splitTest(
            """
            val x = Unit
            x.toString()
            ^^^^^
            "Unit"
        """.trimIndent()
        )
    }

    @Test
    fun decimalToStringTest() {
        splitTest(
            """
            val x = 5.5
            x.toString()
            ^^^^^
            "5.5"
        """.trimIndent()
        )
    }

    @Test
    fun booleanFalseToStringTest() {
        splitTest(
            """
            val x = false
            x.toString()
            ^^^^^
            "false"
        """.trimIndent()
        )
    }

    @Test
    fun booleanTrueToStringTest() {
        splitTest(
            """
            val x = true
            x.toString()
            ^^^^^
            "true"
        """.trimIndent()
        )
    }

    @Test
    fun stringToStringTest() {
        splitTest(
            """
            val s = "Hello world!"
            s.toString()
            ^^^^^
            "Hello world!"
        """.trimIndent()
        )
    }

    @Test
    fun charToStringTest() {
        splitTest(
            """
            val s = 'c'
            s.toString()
            ^^^^^
            "c"
        """.trimIndent()
        )
    }

    @Test
    fun sByteStringInterpolationTest() {
        splitTest(
            """
            val x = 42s8
            "${"$"}{x}"
            ^^^^^
            "42s8"
        """.trimIndent()
        )
    }

    @Test
    fun shortStringInterpolationTest() {
        splitTest(
            """
            val x = 42s16
            "${"$"}{x}"
            ^^^^^
            "42s16"
        """.trimIndent()
        )
    }

    @Test
    fun integerStringInterpolationTest() {
        splitTest(
            """
            val x = 42
            "${"$"}{x}"
            ^^^^^
            "42"
        """.trimIndent()
        )
    }

    @Test
    fun longStringInterpolationTest() {
        splitTest(
            """
            val x = 42s64
            "${"$"}{x}"
            ^^^^^
            "42s64"
        """.trimIndent()
        )
    }

    @Test
    fun byteStringInterpolationTest() {
        splitTest(
            """
            val x = 42u8
            "${"$"}{x}"
            ^^^^^
            "42u8"
        """.trimIndent()
        )
    }

    @Test
    fun uShortStringInterpolationTest() {
        splitTest(
            """
            val x = 42u16
            "${"$"}{x}"
            ^^^^^
            "42u16"
        """.trimIndent()
        )
    }

    @Test
    fun uIntStringInterpolationTest() {
        splitTest(
            """
            val x = 42u32
            "${"$"}{x}"
            ^^^^^
            "42u32"
        """.trimIndent()
        )
    }

    @Test
    fun uLongStringInterpolationTest() {
        splitTest(
            """
            val x = 42u64
            "${"$"}{x}"
            ^^^^^
            "42u64"
        """.trimIndent()
        )
    }

    @Test
    fun unitStringInterpolationTest() {
        splitTest(
            """
            val x = Unit
            "${"$"}{x}"
            ^^^^^
            "Unit"
        """.trimIndent()
        )
    }

    @Test
    fun decimalStringInterpolationTest() {
        splitTest(
            """
            val x = 5.5
            "${"$"}{x}"
            ^^^^^
            "5.5"
        """.trimIndent()
        )
    }

    @Test
    fun booleanFalseStringInterpolationTest() {
        splitTest(
            """
            val x = false
            "${"$"}{x}"
            ^^^^^
            "false"
        """.trimIndent()
        )
    }

    @Test
    fun booleanTrueStringInterpolationTest() {
        splitTest(
            """
            val x = true
            "${"$"}{x}"
            ^^^^^
            "true"
        """.trimIndent()
        )
    }

    @Test
    fun stringStringInterpolationTest() {
        splitTest(
            """
            val s = "Hello world!"
            "${"$"}{s}"
            ^^^^^
            "Hello world!"
        """.trimIndent()
        )
    }

    @Test
    fun charStringInterpolationTest() {
        splitTest(
            """
            val s = 'c'
            "${"$"}{s}"
            ^^^^^
            "c"
        """.trimIndent()
        )
    }

    @Test
    fun stringToCharListTest() {
        splitTest(
            """
            val s = "Hel"
            s.toCharList()
            ^^^^^
            List('H', 'e', 'l')
        """.trimIndent()
        )
    }

    @Test
    fun stringSizeTest() {
        splitTest(
            """
            val s = "Hello world!"
            s.size
            ^^^^^
            12
        """.trimIndent()
        )
    }

    @Test
    fun s8PlusStringInterp() {
        splitTest(""""${"$"}{5s8 + 6s8}"^^^^^"${"$"}{11s8}"""")
    }

    @Test
    fun s8MinusStringInterp() {
        splitTest(""""${"$"}{5s8 - 1s8}"^^^^^"${"$"}{4s8}"""")
    }

    @Test
    fun s8MulStringInterp() {
        splitTest(""""${"$"}{5s8 * 7s8}"^^^^^"${"$"}{35s8}"""")
    }

    @Test
    fun s8DivStringInterp() {
        splitTest(""""${"$"}{10s8 / 5s8}"^^^^^"${"$"}{2s8}"""")
    }

    @Test
    fun s8ModStringInterp() {
        splitTest(""""${"$"}{5s8 % 2s8}"^^^^^"${"$"}{1s8}"""")
    }

    @Test
    fun s8OrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5s8 + 2s8 * 6s8 + 9s8 / 3s8}"^^^^^"${"$"}{20s8}"""")
    }

    @Test
    fun s8TestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5s8 > 6s8}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun s8TestLessThanStringInterp() {
        splitTest(""""${"$"}{5s8 < 6s8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s8TestAndStringInterp() {
        splitTest(""""${"$"}{5s8 < 6s8 && 7s8 < 8s8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s8TestOrStringInterp() {
        splitTest(""""${"$"}{5s8 < 6s8 || 9s8 < 8s8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s8TestComplexExprStringInterp() {
        splitTest(""""${"$"}{5s8 + 2s8 * 3s8 < 7s8 * 8s8 + 9s8 && false || 7s8 + 3s8 / 1s8 > 45s8 / 9s8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s16PlusStringInterp() {
        splitTest(""""${"$"}{5s16 + 6s16}"^^^^^"${"$"}{11s16}"""")
    }

    @Test
    fun s16MinusStringInterp() {
        splitTest(""""${"$"}{5s16 - 1s16}"^^^^^"${"$"}{4s16}"""")
    }

    @Test
    fun s16MulStringInterp() {
        splitTest(""""${"$"}{5s16 * 7s16}"^^^^^"${"$"}{35s16}"""")
    }

    @Test
    fun s16DivStringInterp() {
        splitTest(""""${"$"}{10s16 / 5s16}"^^^^^"${"$"}{2s16}"""")
    }

    @Test
    fun s16ModStringInterp() {
        splitTest(""""${"$"}{5s16 % 2s16}"^^^^^"${"$"}{1s16}"""")
    }

    @Test
    fun s16OrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5s16 + 2s16 * 6s16 + 9s16 / 3s16}"^^^^^"${"$"}{20s16}"""")
    }

    @Test
    fun s16TestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5s16 > 6s16}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun s16TestLessThanStringInterp() {
        splitTest(""""${"$"}{5s16 < 6s16}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s16TestAndStringInterp() {
        splitTest(""""${"$"}{5s16 < 6s16 && 7s16 < 8s16}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s16TestOrStringInterp() {
        splitTest(""""${"$"}{5s16 < 6s16 || 9s16 < 8s16}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s16TestComplexExprStringInterp() {
        splitTest(""""${"$"}{5s16 + 2s16 * 3s16 < 7s16 * 8s16 + 9s16 && false || 7s16 + 3s16 / 1s16 > 45s16 / 9s16}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun PlusStringInterp() {
        splitTest(""""${"$"}{5 + 6}"^^^^^"${"$"}{11}"""")
    }

    @Test
    fun MinusStringInterp() {
        splitTest(""""${"$"}{5 - 1}"^^^^^"${"$"}{4}"""")
    }

    @Test
    fun MulStringInterp() {
        splitTest(""""${"$"}{5 * 7}"^^^^^"${"$"}{35}"""")
    }

    @Test
    fun DivStringInterp() {
        splitTest(""""${"$"}{10 / 5}"^^^^^"${"$"}{2}"""")
    }

    @Test
    fun ModStringInterp() {
        splitTest(""""${"$"}{5 % 2}"^^^^^"${"$"}{1}"""")
    }

    @Test
    fun OrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5 + 2 * 6 + 9 / 3}"^^^^^"${"$"}{20}"""")
    }

    @Test
    fun TestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5 > 6}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun TestLessThanStringInterp() {
        splitTest(""""${"$"}{5 < 6}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun TestAndStringInterp() {
        splitTest(""""${"$"}{5 < 6 && 7 < 8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun TestOrStringInterp() {
        splitTest(""""${"$"}{5 < 6 || 9 < 8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun TestComplexExprStringInterp() {
        splitTest(""""${"$"}{5 + 2 * 3 < 7 * 8 + 9 && false || 7 + 3 / 1 > 45 / 9}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s64PlusStringInterp() {
        splitTest(""""${"$"}{5s64 + 6s64}"^^^^^"${"$"}{11s64}"""")
    }

    @Test
    fun s64MinusStringInterp() {
        splitTest(""""${"$"}{5s64 - 1s64}"^^^^^"${"$"}{4s64}"""")
    }

    @Test
    fun s64MulStringInterp() {
        splitTest(""""${"$"}{5s64 * 7s64}"^^^^^"${"$"}{35s64}"""")
    }

    @Test
    fun s64DivStringInterp() {
        splitTest(""""${"$"}{10s64 / 5s64}"^^^^^"${"$"}{2s64}"""")
    }

    @Test
    fun s64ModStringInterp() {
        splitTest(""""${"$"}{5s64 % 2s64}"^^^^^"${"$"}{1s64}"""")
    }

    @Test
    fun s64OrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5s64 + 2s64 * 6s64 + 9s64 / 3s64}"^^^^^"${"$"}{20s64}"""")
    }

    @Test
    fun s64TestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5s64 > 6s64}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun s64TestLessThanStringInterp() {
        splitTest(""""${"$"}{5s64 < 6s64}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s64TestAndStringInterp() {
        splitTest(""""${"$"}{5s64 < 6s64 && 7s64 < 8s64}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s64TestOrStringInterp() {
        splitTest(""""${"$"}{5s64 < 6s64 || 9s64 < 8s64}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun s64TestComplexExprStringInterp() {
        splitTest(""""${"$"}{5s64 + 2s64 * 3s64 < 7s64 * 8s64 + 9s64 && false || 7s64 + 3s64 / 1s64 > 45s64 / 9s64}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u8PlusStringInterp() {
        splitTest(""""${"$"}{5u8 + 6u8}"^^^^^"${"$"}{11u8}"""")
    }

    @Test
    fun u8MinusStringInterp() {
        splitTest(""""${"$"}{5u8 - 1u8}"^^^^^"${"$"}{4u8}"""")
    }

    @Test
    fun u8MulStringInterp() {
        splitTest(""""${"$"}{5u8 * 7u8}"^^^^^"${"$"}{35u8}"""")
    }

    @Test
    fun u8DivStringInterp() {
        splitTest(""""${"$"}{10u8 / 5u8}"^^^^^"${"$"}{2u8}"""")
    }

    @Test
    fun u8ModStringInterp() {
        splitTest(""""${"$"}{5u8 % 2u8}"^^^^^"${"$"}{1u8}"""")
    }

    @Test
    fun u8OrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5u8 + 2u8 * 6u8 + 9u8 / 3u8}"^^^^^"${"$"}{20u8}"""")
    }

    @Test
    fun u8TestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5u8 > 6u8}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun u8TestLessThanStringInterp() {
        splitTest(""""${"$"}{5u8 < 6u8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u8TestAndStringInterp() {
        splitTest(""""${"$"}{5u8 < 6u8 && 7u8 < 8u8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u8TestOrStringInterp() {
        splitTest(""""${"$"}{5u8 < 6u8 || 9u8 < 8u8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u8TestComplexExprStringInterp() {
        splitTest(""""${"$"}{5u8 + 2u8 * 3u8 < 7u8 * 8u8 + 9u8 && false || 7u8 + 3u8 / 1u8 > 45u8 / 9u8}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u16PlusStringInterp() {
        splitTest(""""${"$"}{5u16 + 6u16}"^^^^^"${"$"}{11u16}"""")
    }

    @Test
    fun u16MinusStringInterp() {
        splitTest(""""${"$"}{5u16 - 1u16}"^^^^^"${"$"}{4u16}"""")
    }

    @Test
    fun u16MulStringInterp() {
        splitTest(""""${"$"}{5u16 * 7u16}"^^^^^"${"$"}{35u16}"""")
    }

    @Test
    fun u16DivStringInterp() {
        splitTest(""""${"$"}{10u16 / 5u16}"^^^^^"${"$"}{2u16}"""")
    }

    @Test
    fun u16ModStringInterp() {
        splitTest(""""${"$"}{5u16 % 2u16}"^^^^^"${"$"}{1u16}"""")
    }

    @Test
    fun u16OrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5u16 + 2u16 * 6u16 + 9u16 / 3u16}"^^^^^"${"$"}{20u16}"""")
    }

    @Test
    fun u16TestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5u16 > 6u16}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun u16TestLessThanStringInterp() {
        splitTest(""""${"$"}{5u16 < 6u16}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u16TestAndStringInterp() {
        splitTest(""""${"$"}{5u16 < 6u16 && 7u16 < 8u16}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u16TestOrStringInterp() {
        splitTest(""""${"$"}{5u16 < 6u16 || 9u16 < 8u16}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u16TestComplexExprStringInterp() {
        splitTest(""""${"$"}{5u16 + 2u16 * 3u16 < 7u16 * 8u16 + 9u16 && false || 7u16 + 3u16 / 1u16 > 45u16 / 9u16}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u32PlusStringInterp() {
        splitTest(""""${"$"}{5u32 + 6u32}"^^^^^"${"$"}{11u32}"""")
    }

    @Test
    fun u32MinusStringInterp() {
        splitTest(""""${"$"}{5u32 - 1u32}"^^^^^"${"$"}{4u32}"""")
    }

    @Test
    fun u32MulStringInterp() {
        splitTest(""""${"$"}{5u32 * 7u32}"^^^^^"${"$"}{35u32}"""")
    }

    @Test
    fun u32DivStringInterp() {
        splitTest(""""${"$"}{10u32 / 5u32}"^^^^^"${"$"}{2u32}"""")
    }

    @Test
    fun u32ModStringInterp() {
        splitTest(""""${"$"}{5u32 % 2u32}"^^^^^"${"$"}{1u32}"""")
    }

    @Test
    fun u32OrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5u32 + 2u32 * 6u32 + 9u32 / 3u32}"^^^^^"${"$"}{20u32}"""")
    }

    @Test
    fun u32TestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5u32 > 6u32}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun u32TestLessThanStringInterp() {
        splitTest(""""${"$"}{5u32 < 6u32}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u32TestAndStringInterp() {
        splitTest(""""${"$"}{5u32 < 6u32 && 7u32 < 8u32}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u32TestOrStringInterp() {
        splitTest(""""${"$"}{5u32 < 6u32 || 9u32 < 8u32}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u32TestComplexExprStringInterp() {
        splitTest(""""${"$"}{5u32 + 2u32 * 3u32 < 7u32 * 8u32 + 9u32 && false || 7u32 + 3u32 / 1u32 > 45u32 / 9u32}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u64PlusStringInterp() {
        splitTest(""""${"$"}{5u64 + 6u64}"^^^^^"${"$"}{11u64}"""")
    }

    @Test
    fun u64MinusStringInterp() {
        splitTest(""""${"$"}{5u64 - 1u64}"^^^^^"${"$"}{4u64}"""")
    }

    @Test
    fun u64MulStringInterp() {
        splitTest(""""${"$"}{5u64 * 7u64}"^^^^^"${"$"}{35u64}"""")
    }

    @Test
    fun u64DivStringInterp() {
        splitTest(""""${"$"}{10u64 / 5u64}"^^^^^"${"$"}{2u64}"""")
    }

    @Test
    fun u64ModStringInterp() {
        splitTest(""""${"$"}{5u64 % 2u64}"^^^^^"${"$"}{1u64}"""")
    }

    @Test
    fun u64OrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5u64 + 2u64 * 6u64 + 9u64 / 3u64}"^^^^^"${"$"}{20u64}"""")
    }

    @Test
    fun u64TestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5u64 > 6u64}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun u64TestLessThanStringInterp() {
        splitTest(""""${"$"}{5u64 < 6u64}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u64TestAndStringInterp() {
        splitTest(""""${"$"}{5u64 < 6u64 && 7u64 < 8u64}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u64TestOrStringInterp() {
        splitTest(""""${"$"}{5u64 < 6u64 || 9u64 < 8u64}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun u64TestComplexExprStringInterp() {
        splitTest(""""${"$"}{5u64 + 2u64 * 3u64 < 7u64 * 8u64 + 9u64 && false || 7u64 + 3u64 / 1u64 > 45u64 / 9u64}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun decPlusStringInterp() {
        splitTest(""""${"$"}{5.0 + 6.0}"^^^^^"${"$"}{11.0}"""")
    }

    @Test
    fun decMinusStringInterp() {
        splitTest(""""${"$"}{5.0 - 1.0}"^^^^^"${"$"}{4.0}"""")
    }

    @Test
    fun decMulStringInterp() {
        splitTest(""""${"$"}{5.0 * 7.0}"^^^^^"${"$"}{35.0}"""")
    }

    @Test
    fun decDivStringInterp() {
        splitTest(""""${"$"}{10.0 / 5.0}"^^^^^"${"$"}{2.0}"""")
    }

    @Test
    fun decModStringInterp() {
        splitTest(""""${"$"}{5.0 % 2.0}"^^^^^"${"$"}{1.0}"""")
    }

    @Test
    fun decOrderOfOperationsStringInterp() {
        splitTest(""""${"$"}{5.0 + 2.0 * 6.0 + 9.0 / 3.0}"^^^^^"${"$"}{20.0}"""")
    }

    @Test
    fun decTestGreaterThanStringInterp() {
        splitTest(""""${"$"}{5.0 > 6.0}"^^^^^"${"$"}{false}"""")
    }

    @Test
    fun decTestLessThanStringInterp() {
        splitTest(""""${"$"}{5.0 < 6.0}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun decTestAndStringInterp() {
        splitTest(""""${"$"}{5.0 < 6.0 && 7.0 < 8.0}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun decTestOrStringInterp() {
        splitTest(""""${"$"}{5.0 < 6.0 || 9.0 < 8.0}"^^^^^"${"$"}{true}"""")
    }

    @Test
    fun decTestComplexExprStringInterp() {
        splitTest(""""${"$"}{5.0 + 2.0 * 3.0 < 7.0 * 8.0 + 9.0 && false || 7.0 + 3.0 / 1.0 > 45.0 / 9.0}"^^^^^"${"$"}{true}"""")
    }
}