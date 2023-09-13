package org.shardscript.acceptance

import org.shardscript.semantics.core.AmbiguousSymbol
import org.shardscript.semantics.core.DuplicateImport
import org.shardscript.semantics.core.IdentifierNotFound
import org.junit.jupiter.api.Test

class ImportErrorTests {
    @Test
    fun duplicateImportTest() {
        failTest(
            """
            artifact test.ids.errors
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
            artifact test.ids.errors
            import test.imported
            import test.duplicates
            
            duplicateFunction(3, 4)
        """.trimIndent(), 1
        ) {
            it.error is AmbiguousSymbol
        }
    }

    @Test
    fun deepImportTestTest() {
        failTest(
            """
            artifact test.ids.errors
            import test.imported
            
            deepLeft(3, 4)
        """.trimIndent(), 1
        ) {
            it.error is IdentifierNotFound
        }
    }
}