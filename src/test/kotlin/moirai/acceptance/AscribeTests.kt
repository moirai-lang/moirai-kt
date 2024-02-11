package moirai.acceptance

import org.junit.jupiter.api.Test
import moirai.semantics.core.RuntimeFinViolation

class AscribeTests {
    @Test
    fun decAscribeHappyTest() {
        splitTest(
            """
               val d = 123456789.123456789
               d.ascribeFin<64>()
               ^^^^^
               123456789.123456789
        """.trimIndent(), LargeComputationArchitecture
        )
    }

    @Test
    fun decAscribeFailureTest() {
        failTest(
            """
               val d = 123456789.123456789
               d.ascribeFin<6>()
        """.trimIndent(), 1
        ) {
            it.error is RuntimeFinViolation
        }
    }

    @Test
    fun decAscribeFunctionHappyTest() {
        splitTest(
            """
               def f<P: Fin>(d: Decimal<P>): Decimal<64> {
                   d.ascribeFin<64>()
               }
                
               f(123456789.123456789)
               ^^^^^
               123456789.123456789
        """.trimIndent(), LargeComputationArchitecture
        )
    }

    @Test
    fun decAscribeFunctionFailureTest() {
        failTest(
            """
               def f<P: Fin>(d: Decimal<P>): Decimal<6> {
                   d.ascribeFin<6>()
               }
                
               f(123456789.123456789)
        """.trimIndent(), 1
        ) {
            it.error is RuntimeFinViolation
        }
    }
}