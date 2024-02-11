package org.shardscript.acceptance

import org.junit.jupiter.api.Test
import org.shardscript.semantics.core.RuntimeFinViolation

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
}