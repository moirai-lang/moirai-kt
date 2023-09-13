package org.shardscript.acceptance

import org.junit.jupiter.api.Test

class ImportHappyTests {
    @Test
    fun basicImportTest() {
        splitTest(
            """
            artifact test.ids.happy
            import test.imported
            
            importedFunction(3, 4)
            ^^^^^
            12
        """.trimIndent()
        )
    }

    @Test
    fun basicNamespaceTest() {
        splitTest(
            """
            artifact test.ids.happy
            import test.imported
            import test.duplicates
            
            test.imported.duplicateFunction(3, 4)
            ^^^^^
            12
        """.trimIndent()
        )
    }

    @Test
    fun basicPathTypeTest() {
        splitTest(
            """
            artifact test.ids.happy
            import test.imported
            
            val r: test.imported.ImportedRecord = test.imported.ImportedRecord(3, 4)
            r.a
            ^^^^^
            3
        """.trimIndent()
        )
    }
}