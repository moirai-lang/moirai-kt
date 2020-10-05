package com.tsikhe.shardscript.acceptance

import org.junit.Test

class EnumHappyTests {
    @Test
    fun someDeadStickOptionTest() {
        splitTest(
            """
                enum DeadStickOption<T> {
                    record DeadStickSome<T>(val t: T)
                    object DeadStickNone
                }
                
                def f(x: Int): DeadStickOption<Int> {
                    DeadStickSome(x)
                }
                
                val o = f(13)
                o
                ^^^^^
                enum DeadStickOption<T> {
                    record DeadStickSome<T>(val t: T)
                    object DeadStickNone
                }
                
                DeadStickSome(13)
            """.trimIndent()
        )
    }

    @Test
    fun noneDeadStickOptionTest() {
        splitTest(
            """
                enum DeadStickOption<T> {
                    record DeadStickSome<T>(val t: T)
                    object DeadStickNone
                }
                
                def f(x: Int): DeadStickOption<Int> {
                    DeadStickNone
                }
                
                val o = f(13)
                o
                ^^^^^
                enum DeadStickOption<T> {
                    record DeadStickSome<T>(val t: T)
                    object DeadStickNone
                }
                
                DeadStickNone
            """.trimIndent()
        )
    }

    @Test
    fun nestedCoproductExplicitTest() {
        splitTest(
            """
            enum DeadStickOption<T> {
                record DeadStickSome<T>(val t: T)
                object DeadStickNone
            }

            record B<T>(val z: T, val a: DeadStickOption<T>)
            
            val x = B<Int>(5, DeadStickSome(4))
            val y = B<Int>(7, DeadStickNone)
            
            x.z + y.z
            ^^^^^
            12
        """.trimIndent()
        )
    }

    @Test
    fun genericNestedEnumTest() {
        splitTest(
            """
            def wrap<T>(t: T): Silly<Silly<Silly<Silly<Silly<Silly<Silly<T>>>>>>> {
                A<Silly<Silly<Silly<Silly<Silly<Silly<T>>>>>>>(
                    A<Silly<Silly<Silly<Silly<Silly<T>>>>>>(
                        A<Silly<Silly<Silly<Silly<T>>>>>(
                            A<Silly<Silly<Silly<T>>>>(
                                A<Silly<Silly<T>>>(
                                    A<Silly<T>>(
                                        A(t)
                                )   )
                            )
                        )
                    )
                )
            }
            
            enum Silly<T> {
                record A<T>(val t: T)
                object B
            }
            
            val silly = wrap(5)
            silly
            ^^^^^
            enum Silly<T> {
                record A<T>(val t: T)
                object B
            }
            
            A<Silly<Silly<Silly<Silly<Silly<Silly<Int>>>>>>>(
                A<Silly<Silly<Silly<Silly<Silly<Int>>>>>>(
                    A<Silly<Silly<Silly<Silly<Int>>>>>(
                        A<Silly<Silly<Silly<Int>>>>(
                            A<Silly<Silly<Int>>>(
                                A<Silly<Int>>(
                                    A(5)
                            )   )
                        )
                    )
                )
            )
        """.trimIndent()
        )
    }

    @Test
    fun basicEnumTypeCompatibilityTest() {
        splitTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                def f(x: Int): E<Int> {
                    A(x)
                }
                
                val e = f(13)
                e is E<Int>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun genericEnumTypeCompatibilityTest() {
        splitTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                def f<T>(x: T): E<T> {
                    A(x)
                }
                
                val e = f(13)
                e is E<Int>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun basicObjectEnumTypeCompatibilityTest() {
        splitTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                def f(x: Int): E<Int> {
                    B
                }
                
                val e = f(13)
                e is E<Int>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun genericObjectEnumTypeCompatibilityTest() {
        splitTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                def f<T>(x: T): E<T> {
                    B
                }
                
                val e = f(13)
                e is E<Int>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun basicEnumTypeIncompatibilityTest() {
        splitTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                val e = A(13)
                e is E<Int>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun basicObjectEnumTypeIncompatibilityTest() {
        splitTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                val e = B
                e is E<Int>
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun basicEnumTypeSwitchTest() {
        splitTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                val e = A(13)
                val s = switch(e as E<Int>) {
                    case A {
                        4
                    }
                    case B {
                        5
                    }
                }
                s
                ^^^^^
                4
            """.trimIndent()
        )
    }

    @Test
    fun basicEnumObjectTypeSwitchTest() {
        splitTest(
            """
                enum E<T> {
                    record A<T>(val t: T)
                    object B
                }
                
                val e = B
                val s = switch(e as E<Int>) {
                    case A {
                        4
                    }
                    case B {
                        5
                    }
                }
                s
                ^^^^^
                5
            """.trimIndent()
        )
    }

    @Test
    fun basicGroundEnumTypeSwitchTest() {
        splitTest(
            """
                enum E {
                    record A(val x: Int)
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
                ^^^^^
                4
            """.trimIndent()
        )
    }

    @Test
    fun basicGroundEnumTypeIsTest() {
        splitTest(
            """
                enum E {
                    record A(val x: Int)
                    object B
                }
                
                val e = A(13)
                e is E
                ^^^^^
                true
            """.trimIndent()
        )
    }

    @Test
    fun basicGroundEnumObjectTypeSwitchTest() {
        splitTest(
            """
                enum E {
                    record A(val x: Int)
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
                ^^^^^
                5
            """.trimIndent()
        )
    }

    @Test
    fun basicGroundEnumObjectTypeIsTest() {
        splitTest(
            """
                enum E {
                    record A(val x: Int)
                    object B
                }
                
                val e = B
                e is E
                ^^^^^
                true
            """.trimIndent()
        )
    }
}