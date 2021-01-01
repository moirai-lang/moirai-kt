package com.tsikhe.shardscript.composition

import com.tsikhe.shardscript.grammar.ShardScriptParser
import com.tsikhe.shardscript.grammar.ShardScriptParserBaseListener
import com.tsikhe.shardscript.semantics.core.DuplicateImport
import com.tsikhe.shardscript.semantics.core.LanguageErrors
import com.tsikhe.shardscript.semantics.core.SelfImport
import com.tsikhe.shardscript.semantics.core.SourceContext

data class ImportStat(val path: List<String>)

internal class ImportsParseTreeListener(private val fileName: String, val errors: LanguageErrors) :
    ShardScriptParserBaseListener() {
    private val wildcardImports: MutableMap<List<String>, SourceContext> = HashMap()
    private val fileNamespace = fileName.split(".")

    override fun enterImportStat(ctx: ShardScriptParser.ImportStatContext) {
        val import = ctx.importIdSeq().IDENTIFIER().map { it.symbol.text }
        val sourceContext = createContext(fileName, ctx.start)
        if (wildcardImports.contains(import)) {
            errors.add(sourceContext, DuplicateImport(import))
        } else {
            wildcardImports[import] = sourceContext
        }
    }

    fun listImports(): List<ImportStat> {
        val res: MutableList<ImportStat> = ArrayList()
        wildcardImports.forEach {
            if (fileNamespace == it.key) {
                errors.add(it.value, SelfImport)
            }
            res.add(ImportStat(it.key))
        }
        return res
    }
}