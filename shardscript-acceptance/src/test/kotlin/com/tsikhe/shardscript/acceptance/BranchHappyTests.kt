package com.tsikhe.shardscript.acceptance

import org.junit.Test

class BranchHappyTests {
    @Test
    fun ifExpressionTrueBranchTest() {
        splitTest(
            """
            def max(x: Int, y: Int): Int {
                if(x >= y) {
                    x
                } else {
                    y
                }
            }
            
            max(8, 6)
            ^^^^^
            8
        """.trimIndent()
        )
    }

    @Test
    fun ifExpressionFalseBranchTest() {
        splitTest(
            """
            def max(x: Int, y: Int): Int {
                if(x >= y) {
                    x
                } else {
                    y
                }
            }
            
            max(6, 8)
            ^^^^^
            8
        """.trimIndent()
        )
    }

    @Test
    fun switchOptionAllTest() {
        splitTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            switch(o) {
                case Some {
                    4
                }
                case None {
                    5
                }
            }
            ^^^^^
            4
        """.trimIndent()
        )
    }

    @Test
    fun switchOptionSomeTest() {
        splitTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            switch(o) {
                case Some {
                    4
                }
                else {
                    5
                }
            }
            ^^^^^
            4
        """.trimIndent()
        )
    }

    @Test
    fun switchOptionNoneTest() {
        splitTest(
            """
            def f(x: Int): Option<Int> {
                Some(x)
            }
            
            val o = f(13)
            switch(o) {
                case None {
                    4
                }
                else {
                    5
                }
            }
            ^^^^^
            5
        """.trimIndent()
        )
    }

    @Test
    fun switchDeadStickOptionAllTest() {
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
            switch(o) {
                case DeadStickSome {
                    4
                }
                case DeadStickNone {
                    5
                }
            }
            ^^^^^
            4
        """.trimIndent()
        )
    }

    @Test
    fun switchDeadStickOptionSomeTest() {
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
            switch(o) {
                case DeadStickSome {
                    4
                }
                else {
                    5
                }
            }
            ^^^^^
            4
        """.trimIndent()
        )
    }

    @Test
    fun switchDeadStickOptionNoneTest() {
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
            switch(o) {
                case DeadStickNone {
                    4
                }
                else {
                    5
                }
            }
            ^^^^^
            5
        """.trimIndent()
        )
    }

    @Test
    fun ifElseIfTest() {
        splitTest(
            """
                val x = 5
                val y = 7
                if(x > 10) {
                    20
                } else if(y < 10) {
                    3
                } else {
                    84
                }
                ^^^^^
                3
            """.trimIndent()
        )
    }

    @Test
    fun enumBranchTest() {
        splitTest(
            """
                enum E {
                    object A
                    object B
                }
                
                val guess = 7
                val answer = if(guess < 10) {
                    A
                } else {
                    B
                }
                switch(answer) {
                    case A {
                        14
                    }
                    case B {
                        16
                    }
                }
                ^^^^^
                14
            """.trimIndent()
        )
    }
}