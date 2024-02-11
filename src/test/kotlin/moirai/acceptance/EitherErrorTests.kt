package moirai.acceptance

import org.junit.jupiter.api.Test
import moirai.semantics.core.TypeMismatch

class EitherErrorTests {
    @Test
    fun leftEitherTypeMismatchTest() {
        failTest(
            """
            def f(x: Decimal<16>): Either<Int, Decimal<16>> {
                Left(x)
            }
            
            val o = f(13.3)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }

    @Test
    fun rightEitherTypeMismatchTest() {
        failTest(
            """
            def f(x: Int): Either<Int, Decimal<16>> {
                Right(x)
            }
            
            val o = f(13)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }
}