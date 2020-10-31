package com.tsikhe.shardscript.acceptance

import org.junit.Test

class TutorialTests {
    @Test
    fun blankTest() {
        splitTest(
            """
                1 + 1
                ^^^^^
                2
            """.trimIndent()
        )
    }

    @Test
    fun mutableListToImmutableTest() {
        splitTest(
            """
                val x = MutableList<Int, 10>(1, 2, 3, 4, 5)
                x[1] = 9
                val y = x.toList()
                y[1]
                ^^^^^
                9
            """.trimIndent()
        )
    }

    @Test
    fun dictionaryExampleTest() {
        splitTest(
            """
                val d = Dictionary(1 to 2, 3 to 4, 5 to 6)
                val y = 6
                
                d[1] + y
                ^^^^^
                8
            """.trimIndent()
        )
    }

    @Test
    fun mutableDictionaryExampleTest() {
        splitTest(
            """
                val m = MutableDictionary<Int, Int, 50>(1 to 2, 3 to 4, 5 to 6)
                
                m[1] = 3
                m.remove(1)
                ^^^^^
                Unit
            """.trimIndent()
        )
    }

    @Test
    fun enumExampleTest() {
        splitTest(
            """
                enum E {
                    object A
                    object B
                }
            
                val e = A
                ^^^^^
                Unit
            """.trimIndent()
        )
    }

    @Test
    fun enumRecordTest() {
        splitTest(
            """
                enum E {
                    object A
                    record B(val x: Int, val y: Int)
                }
            
                val e = B(5, 6)
                ^^^^^
                Unit
            """.trimIndent()
        )
    }

    @Test
    fun parameterizedEnumExampleTest() {
        splitTest(
            """
                enum E<T> {
                    object A
                    record B<T>(val x: T, val y: T)
                }
            
                val e = B(5, 6)
                ^^^^^
                Unit
            """.trimIndent()
        )
    }

    @Test
    fun forLoopTest() {
        splitTest(
            """
                val list = List(4, 7, 2, 1, 9, 8)
                mutable max = 0
                for(item in list) {
                    if(item > max) {
                        max = item
                    }
                }
                max
                ^^^^^
                9
            """.trimIndent()
        )
    }

    @Test
    fun forLoopOptionTest() {
        splitTest(
            """
                val o: Option<Int> = Some(10)
                mutable value = 0
                for(x in o) {
                    value = x
                }
                value
                ^^^^^
                10
            """.trimIndent()
        )
    }

    @Test
    fun functionTest() {
        splitTest(
            """
                def f(x: Int, y: Int): Int {
                    x + y
                }
                
                f(1, 2)
                ^^^^^
                3
            """.trimIndent()
        )
    }

    @Test
    fun parameterizedFunctionTest() {
        splitTest(
            """
                def second<T>(x: T, y: T): T {
                    y
                }
                
                second<Int>(1, 2)
                ^^^^^
                2
            """.trimIndent()
        )
    }

    @Test
    fun parameterizedFunctionInferTest() {
        splitTest(
            """
                def second<T>(x: T, y: T): T {
                    y
                }
                
                second<Int>(1, 2)
                ^^^^^
                2
            """.trimIndent()
        )
    }

    @Test
    fun higherOrderFunctionExampleTest() {
        splitTest(
            """
                def f(g: (Int, Int) -> Int, x: Int, y: Int): Int {
                    g(x + y, x - y)
                }
                
                def h(x: Int, y: Int): Int {
                    x * y
                }
                
                f(h, 4, 3)
                ^^^^^
                7
            """.trimIndent()
        )
    }

    @Test
    fun higherOrderSingleFunctionExampleTest() {
        splitTest(
            """
                def f(g: Int -> Int, x: Int): Int {
                    g(x + 1)
                }
                
                def h(x: Int): Int {
                    x * 20
                }
                
                f(h, 4)
                ^^^^^
                100
            """.trimIndent()
        )
    }

    @Test
    fun higherOrderEmptyFunctionExampleTest() {
        splitTest(
            """
                def f(g: () -> Int): Int {
                    g()
                }
                
                def h(): Int {
                    20
                }
                
                f(h)
                ^^^^^
                20
            """.trimIndent()
        )
    }

    @Test
    fun parameterizedHigherOrderFunctionExampleTest() {
        splitTest(
            """
                def f<T>(g: (T, T) -> T, x: T, y: T): T {
                    g(y, x)
                }
                
                def h(x: Int, y: Int): Int {
                    x * y
                }
                
                f(h, 4, 3)
                ^^^^^
                12
            """.trimIndent()
        )
    }

    @Test
    fun ifExampleTest() {
        splitTest(
            """
                val x = 2
                val y = 3
                val z = if(y > x) {
                    7
                } else {
                    8
                }
                z
                ^^^^^
                7
            """.trimIndent()
        )
    }

    @Test
    fun listExampleTest() {
        splitTest(
            """
                val l = List(1, 2, 3, 4, 5)
                val n = MutableList<Int, 50>(1, 2, 3, 4, 5)
                
                n[0] = 8
                l[0] + n[0]
                ^^^^^
                9
            """.trimIndent()
        )
    }

    @Test
    fun listGroundFunctionTest() {
        splitTest(
            """
                def maxOf(list: List<Int, 10>): Int {
                    mutable max = 0
                    for(item in list) {
                        if(item > max) {
                            max = item
                        }
                    }
                    max
                }
                
                val list = List(4, 7, 2, 1, 9, 8)
                maxOf(list)
                ^^^^^
                9
            """.trimIndent()
        )
    }

    @Test
    fun listParameterizedFunctionTest() {
        splitTest(
            """
                def maxOf<#O>(list: List<Int, #O>): Int {
                    mutable max = 0
                    for(item in list) {
                        if(item > max) {
                            max = item
                        }
                    }
                    max
                }
                
                val list = List(4, 7, 2, 1, 9, 8)
                maxOf(list)
                ^^^^^
                9
            """.trimIndent()
        )
    }

    @Test
    fun toCharListExampleTest() {
        splitTest(
            """
                val s = "Hello World!"
                s.toCharList()
                ^^^^^
                List('H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!')
            """.trimIndent()
        )
    }

    @Test
    fun randomExampleTest() {
        splitTest(
            """
                val x = random(0, 10)
                val y = random(0s64, 10s64)
                ^^^^^
                Unit
            """.trimIndent()
        )
    }

    @Test
    fun rangeExampleTest() {
        splitTest(
            """
                val l = range(0, 10)
                mutable x = 0
                for(i in l) {
                    x = x + i
                }
                x
                ^^^^^
                45
            """.trimIndent()
        )
    }

    @Test
    fun objectsExampleTest() {
        splitTest(
            """
                object B
                
                val b = B
                ^^^^^
                Unit
            """.trimIndent()
        )
    }

    @Test
    fun resultExampleTest() {
        splitTest(
            """
                def guess(x: Int): Result<String<100>, Int> {
                    if(x == 5) {
                        Success(x) as Result<String<100>, Int>
                    } else {
                        Failure("You guessed wrong!") as Result<String<100>, Int>
                    }
                }
                
                guess(5)
                ^^^^^
                Success(5)
            """.trimIndent()
        )
    }

    @Test
    fun stringAddExample() {
        splitTest(
            """
                val s = "Hello "
                val t = "World!"
                s + t
                ^^^^^
                "Hello World!"
            """.trimIndent()
        )
    }
}
