package moirai.acceptance

import moirai.composition.CompilerFrontend
import moirai.composition.UserPluginSource
import moirai.eval.*
import moirai.semantics.core.Architecture
import moirai.transport.ApplyTransportAst
import moirai.transport.IntLiteralTransportAst
import moirai.transport.TransportAst
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TransportTests {
    private fun testTransportAst(transportAst: TransportAst, architecture: Architecture): Value {
        val sourceStore = LocalSourceStore()

        sourceStore.addArtifacts(
            listOf("my", "library"),
            """
                script my.library
    
                record R(val x: Int, val l: List<Int, 5>)
    
                def f(r: R): Int {
                    mutable res = 0
                    for(r.l) {
                        res = res + (r.x * it)
                    }
                    res
                }
            """.trimIndent()
        )

        val pluginSource = """
        plugin def simplePlugin {
            signature (Int, Int) -> Int
            cost Sum(5, 5)
        }
    """.trimIndent()

        val userPlugins: MutableList<UserPlugin> = mutableListOf()
        userPlugins.add(
            TestUserPlugin("simplePlugin") {
                val first = it.first() as IntValue
                val last = it.last() as IntValue
                IntValue(first.canonicalForm + last.canonicalForm)
            }
        )

        val executionCache = LocalExecutionCache()
        val frontend = CompilerFrontend(architecture, sourceStore, UserPluginSource(pluginSource))

        return evalWithCost("my.library", transportAst, frontend, executionCache, userPlugins).value
    }

    @Test
    fun testTransportAst() {
        val transportAst = ApplyTransportAst(
            "f", listOf(
                ApplyTransportAst(
                    "R", listOf(
                        IntLiteralTransportAst(5),
                        ApplyTransportAst(
                            "List", listOf(
                                IntLiteralTransportAst(3),
                                IntLiteralTransportAst(4),
                                IntLiteralTransportAst(5)
                            )
                        )
                    )
                )
            )
        )
        val actual = testTransportAst(transportAst, TestArchitecture)

        if (actual is IntValue) {
            Assertions.assertEquals(60, actual.canonicalForm)
        } else {
            Assertions.fail()
        }
    }
}