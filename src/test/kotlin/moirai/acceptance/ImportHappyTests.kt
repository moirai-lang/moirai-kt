package moirai.acceptance

import org.junit.jupiter.api.Test

class ImportHappyTests {
    @Test
    fun basicImportTest() {
        splitTest(
            """
            script test.ids.happy
            import test.imported
            
            importedFunction(3, 4)
            ^^^^^
            12
        """.trimIndent()
        )
    }

    @Test
    fun basicImportTestGradual() {
        splitTestGradual(
            """
            script test.ids.happy
            import test.imported
            
            importedFunction(3, 4)
            ^^^^^
            12
        """.trimIndent()
        )
    }

    @Test
    fun transientImportTest() {
        splitTestTransient(
            """
            transient script my.library
            
            f(R(5, List(3, 4, 5)))
            ^^^^^
            60
        """.trimIndent()
        )
    }
}