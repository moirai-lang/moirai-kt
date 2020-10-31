package com.tsikhe.shardscript.acceptance

import com.tsikhe.shardscript.semantics.core.AmbiguousSymbol
import com.tsikhe.shardscript.semantics.core.DuplicateImport
import com.tsikhe.shardscript.semantics.core.IdentifierNotFound
import org.junit.Test

class ImportErrorTests {
    @Test
    fun duplicateImportTest() {
        failTest(
            """
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
            import test.imported
            
            deepLeft(3, 4)
        """.trimIndent(), 1
        ) {
            it.error is IdentifierNotFound
        }
    }
}
