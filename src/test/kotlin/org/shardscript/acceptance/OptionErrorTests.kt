package org.shardscript.acceptance

import org.junit.jupiter.api.Test
import org.shardscript.semantics.core.DuplicateCaseDetected
import org.shardscript.semantics.core.MissingMatchCase
import org.shardscript.semantics.core.TypeMismatch
import org.shardscript.semantics.core.UnknownCaseDetected

class OptionErrorTests {
    @Test
    fun someOptionTypeMismatchTest() {
        failTest(
            """
            def f(x: Decimal<16>): Option<Int> {
                Some(x)
            }
            
            val o = f(13.3)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }

    @Test
    fun matchMissingCase() {
        failTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            match(o) {
                case Some { 10 }
            }
        """.trimIndent(), 1
        ) {
            it.error is MissingMatchCase
        }
    }

    @Test
    fun matchDuplicateCase() {
        failTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            match(o) {
                case Some { 10 }
                case None { 5 }
                case None { 6 }
            }
        """.trimIndent(), 1
        ) {
            it.error is DuplicateCaseDetected
        }
    }

    @Test
    fun matchUnknownCase() {
        failTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            match(o) {
                case Some { 10 }
                case None { 5 }
                case Hello { 6 }
            }
        """.trimIndent(), 1
        ) {
            it.error is UnknownCaseDetected
        }
    }
}