package org.shardscript.acceptance

import org.junit.jupiter.api.Test

class ImportHappyTests {
    @Test
    fun basicImportTest() {
        splitTest(
            """
            shard test.ids.happy
            import test.imported
            
            importedFunction(3, 4)
            ^^^^^
            12
        """.trimIndent()
        )
    }
}