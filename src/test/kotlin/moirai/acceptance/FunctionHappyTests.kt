package moirai.acceptance

import org.junit.jupiter.api.Test

class FunctionHappyTests {
    @Test
    fun applyTest() {
        splitTest(
            """
            def f(x: Int, y: Int): Int {
                x + y
            }
            
            f(5, 6)
            ^^^^^
            11
        """.trimIndent()
        )
    }

    @Test
    fun typeParamTest() {
        splitTest(
            """
            def f<T>(x: T, y: T): T {
                x
            }
            
            f(5, 6)
            ^^^^^
            5
        """.trimIndent()
        )
    }

    @Test
    fun typeExplicitParamTest() {
        splitTest(
            """
            def f<T>(x: T, y: T): T {
                x
            }
            
            f<Int>(5, 6)
            ^^^^^
            5
        """.trimIndent()
        )
    }

    @Test
    fun functionUnitReturnType() {
        splitTest(
            """
                def f() {
                    1 + 1
                }
                
                f()
                ^^^^^
                Unit
            """.trimIndent()
        )
    }

    @Test
    fun failedDemoInferenceTest() {
        splitTest(
            """
                def f<T>(x: T, y: T): T {
                    y
                }
                
                f("Hello", "World")
                ^^^^^
                "World"
            """.trimIndent()
        )
    }
}