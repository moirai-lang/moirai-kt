package moirai.acceptance

import moirai.semantics.core.DuplicateImport
import moirai.semantics.core.IdentifierNotFound
import org.junit.jupiter.api.Test
import moirai.semantics.core.IdentifierAlreadyExists

class ImportErrorTests {
    @Test
    fun duplicateImportTest() {
        failTest(
            """
            script test.ids.errors
            import test.imported
            import test.imported
            
            duplicateFunction(3, 4)
        """.trimIndent(), 1
        ) {
            it.error is DuplicateImport
        }
    }

    @Test
    fun ambiguousSymbolTest() {
        failTest(
            """
            script test.ids.errors
            import test.imported
            import test.duplicates
            
            duplicateFunction(3, 4)
        """.trimIndent(), 1
        ) {
            it.error is IdentifierAlreadyExists
        }
    }

    @Test
    fun deepImportTestTest() {
        failTest(
            """
            script test.ids.errors
            import test.imported
            
            deepLeft(3, 4)
        """.trimIndent(), 1
        ) {
            it.error is IdentifierNotFound
        }
    }
}