package moirai.composition

import moirai.grammar.MoiraiParser
import moirai.grammar.MoiraiParserBaseListener
import moirai.semantics.core.*

internal data class PluginDefLiteral(
    val name: Identifier,
    val typeLiteral: FunctionTypeLiteral,
    val costExpression: CostExpression
)

internal class PluginsParseTreeListener(val fileName: String, val errors: LanguageErrors) :
    MoiraiParserBaseListener() {
    private val typeVisitor = TypeLiteralParseTreeVisitor(fileName, errors)
    private val costVisitor = CostExpressionParseTreeVisitor(fileName, errors)
    private val accumulatedPlugins: MutableList<PluginDefLiteral> = mutableListOf()
    override fun enterPluginFunDefStat(ctx: MoiraiParser.PluginFunDefStatContext) {
        val sourceContext = createContext(fileName, ctx.id)
        val id = Identifier(sourceContext, ctx.id.text)
        val ft = typeVisitor.visit(ctx.ft) as FunctionTypeLiteral
        val ce = costVisitor.visit(ctx.ce)
        accumulatedPlugins.add(PluginDefLiteral(id, ft, ce))
    }

    fun listPlugins(): List<PluginDefLiteral> = accumulatedPlugins.toList()
}