package moirai.acceptance

import org.junit.jupiter.api.Test

class StringTests {
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