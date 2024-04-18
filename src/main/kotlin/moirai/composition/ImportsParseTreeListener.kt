package moirai.composition

import moirai.grammar.MoiraiParser
import moirai.grammar.MoiraiParserBaseListener
import moirai.semantics.core.DuplicateImport
import moirai.semantics.core.LanguageErrors
import moirai.semantics.core.SelfImport
import moirai.semantics.core.SourceContext

internal data class ImportStat(val path: List<String>)

internal class ImportsParseTreeListener(val errors: LanguageErrors) :
    MoiraiParserBaseListener() {
    private val accumulatedImports: MutableMap<List<String>, SourceContext> = HashMap()
    private var scriptType: ScriptType = PureTransient

    override fun enterTransientScript(ctx: MoiraiParser.TransientScriptContext) {
        val import = ctx.importIdSeq().IDENTIFIER().map { it.symbol.text }
        scriptType = TransientScript(import)
        val sourceContext = createContext(scriptType.fileName(), ctx.start)

        if (accumulatedImports.contains(import)) {
            errors.add(sourceContext, DuplicateImport(import))
        } else {
            accumulatedImports[import] = sourceContext
        }
    }

    override fun enterScriptStat(ctx: MoiraiParser.ScriptStatContext) {
        val nameParts = ctx.importIdSeq().IDENTIFIER().map { it.symbol.text }
        scriptType = NamedScript(nameParts)
    }

    override fun enterImportStat(ctx: MoiraiParser.ImportStatContext) {
        val st = scriptType
        if (st is NamedScript) {
            val import = ctx.importIdSeq().IDENTIFIER().map { it.symbol.text }
            val sourceContext = createContext(st.fileName(), ctx.start)
            if (accumulatedImports.contains(import)) {
                errors.add(sourceContext, DuplicateImport(import))
            } else {
                accumulatedImports[import] = sourceContext
            }
        }
    }

    fun scriptType(): ScriptType {
        return scriptType
    }

    fun listImports(): List<ImportStat> {
        val res: MutableList<ImportStat> = ArrayList()
        val st = scriptType

        if (st is NamedScriptBase) {
            accumulatedImports.forEach {
                if (st.nameParts == it.key) {
                    errors.add(it.value, SelfImport)
                }
                res.add(ImportStat(it.key))
            }
        }
        return res
    }
}