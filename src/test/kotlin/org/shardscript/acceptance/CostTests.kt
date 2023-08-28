package org.shardscript.acceptance

import org.shardscript.semantics.core.CostOverLimit
import org.junit.jupiter.api.Test

class CostTests {
    @Test
    fun nestedLoopTest() {
        failTest(
            """
            val x = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            
            for(a in x) {
                for(b in x) {
                    for(c in x) {
                        for(d in x) {
                            a + b + c + d
                        }
                    }
                }
            }
        """.trimIndent(), 1
        ) {
            it.error is CostOverLimit
        }
    }

    @Test
    fun nestedLoopHigherOrderTest() {
        failTest(
            """
            def f(g: (Int, Int) -> Int): Int {
                val x = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                for(c in x) {
                    for(d in x) {
                        g(c, d)
                    }
                }
                x[0]
            }
            
            def g(c: Int, d: Int): Int {
                val x = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                for(a in x) {
                    for(b in x) {
                        a + b + c + d
                    }
                }
                x[0]
            }
            
            f(g)
        """.trimIndent(), 1
        ) {
            it.error is CostOverLimit
        }
    }
}