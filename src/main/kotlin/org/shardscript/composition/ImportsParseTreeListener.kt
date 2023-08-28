package org.shardscript.composition

import org.shardscript.grammar.ShardScriptParser
import org.shardscript.grammar.ShardScriptParserBaseListener
import org.shardscript.semantics.core.DuplicateImport
import org.shardscript.semantics.core.LanguageErrors
import org.shardscript.semantics.core.SelfImport
import org.shardscript.semantics.core.SourceContext

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