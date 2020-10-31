package com.tsikhe.shardscript.composition

import com.tsikhe.shardscript.semantics.core.NoSuchFile
import com.tsikhe.shardscript.semantics.core.langThrow

/**
 * Can be used in tests and tools. Should not be used in the prod interpreter.
 */
class LocalSourceStore : SourceStore {
    private val fileNamespace = listOf("local", "source")
    private val fileText = """
        def slope(m: Int, x: Int, b: Int): Int {
            m * x + b
        }
        
        val exported = 21
    """.trimIndent()

    private val fetchDict: MutableMap<List<String>, String> = mutableMapOf(fileNamespace to fileText)

    override fun fetchSourceText(namespace: List<String>): String {
        if (fetchDict.containsKey(namespace)) {
            return fetchDict[namespace]!!
        }
        langThrow(NoSuchFile(namespace))
    }

    fun addArtifacts(namespace: List<String>, sourceText: String) {
        fetchDict[namespace] = sourceText
    }
}
