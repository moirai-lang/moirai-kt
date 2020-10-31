package com.tsikhe.shardscript.acceptance

import org.junit.Test

class ImportHappyTests {
    @Test
    fun basicImportTest() {
        splitTest(
            """
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
            import test.imported
            
            val r: test.imported.ImportedRecord = test.imported.ImportedRecord(3, 4)
            r.a
            ^^^^^
            3
        """.trimIndent()
        )
    }
}
