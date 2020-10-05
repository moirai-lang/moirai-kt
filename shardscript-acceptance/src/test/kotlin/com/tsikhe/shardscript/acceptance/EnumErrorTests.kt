package com.tsikhe.shardscript.acceptance

import com.tsikhe.shardscript.semantics.core.*
import org.junit.Test

class EnumErrorTests {
    @Test
    fun someDeadStickOptionTypeMismatchTest() {
        failTest(
            """
            enum DeadStickOption<T> {
                record DeadStickSome<T>(val t: T)
                object DeadStickNone
            }
                
            def f(x: Decimal<16>): DeadStickOption<Int> {
                DeadStickSome(x)
            }
            
            val o = f(13.3)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }

    @Test
    fun leftDeadStickEitherTypeMismatchTest() {
        failTest(
            """
            enum DeadStickEither<A, B> {
                record DeadStickLeft<A>(val a: A)
                record DeadStickRight<B>(val b: B)
            }
                
            def f(x: Decimal<16>): DeadStickEither<Int, Decimal<16>> {
                DeadStickLeft(x)
            }
            
            val o = f(13.3)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }

    @Test
    fun rightDeadStickEitherTypeMismatchTest() {
        failTest(
            """
            enum DeadStickEither<A, B> {
                record DeadStickLeft<A>(val a: A)
                record DeadStickRight<B>(val b: B)
            }
                
            def f(x: Int): DeadStickEither<Int, Decimal<16>> {
                DeadStickRight(x)
            }
            
            val o = f(13)
            o
        """.trimIndent(), 1
        ) {
            it.error is TypeMismatch
        }
    }

    @Test
    fun nestedCoproductFieldType() {
        failTest(
            """
            enum DeadStickOption<T> {
                record DeadStickSome<T>(val t: T)
                object DeadStickNone
            }

            record B<T>(val z: T, val a: DeadStickOption<T>)
            
            val x = B<Int>(5, DeadStickSome(4))
            val y = B<Int>(7, DeadStickNone)
            
            x.a.t + y.z
        """.trimIndent(), 1
        ) {
            it.error is SymbolHasNoFields
        }
    }

    @Test
    fun recursiveEnumTest() {
        failTest(
            """
            enum DeadStick {
                record A(val d: DeadStick)
                record B(val a: A)
            }
        """.trimIndent(), 3
        ) {
            it.error is RecursiveRecordDetected
        }
    }

    @Test
    fun invalidTypeParameterTest() {
        failTest(
            """
            enum DeadStickEither<A, B> {
                record DeadStickLeft<A>(val a: A, val b: B)
                record DeadStickRight<B>(val b: B)
            }
        """.trimIndent(), 1
        ) {
            it.error is ForeignTypeParameter
        }
    }

    @Test
    fun invalidParameterizedEnumGroundRecordTest() {
        failTest(
            """
            enum DeadStickEither<A, B> {
                record DeadStickLeft(val a: Int)
                record DeadStickRight<B>(val b: B)
            }
        """.trimIndent(), 1
        ) {
            it.error is ParameterizedGroundMismatch
        }
    }

    @Test
    fun invalidGroundEnumParameterizedRecordTest() {
        failTest(
            """
            enum DeadStickEither {
                record DeadStickLeft(val a: Int)
                record DeadStickRight<B>(val b: B)
            }
        """.trimIndent(), 1
        ) {
            it.error is ParameterizedGroundMismatch
        }
    }

    @Test
    fun basicEnumTypeSwitchErrorTest() {
        failTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                val e = A(13)
                val s = switch(e) {
                    case A {
                        4
                    }
                    case B {
                        5
                    }
                }
                s
            """.trimIndent(), 1
        ) {
            it.error is InvalidSwitchSource
        }
    }

    @Test
    fun basicEnumObjectTypeSwitchErrorTest() {
        failTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                val e = B
                val s = switch(e) {
                    case A {
                        4
                    }
                    case B {
                        5
                    }
                }
                s
            """.trimIndent(), 1
        ) {
            it.error is SwitchFeatureBan
        }
    }
}