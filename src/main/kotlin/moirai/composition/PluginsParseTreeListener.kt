package moirai.composition

import moirai.grammar.MoiraiParser
import moirai.grammar.MoiraiParserBaseListener
import moirai.semantics.core.*

internal data class PluginDefLiteral(
    val id: Identifier,
    val typeParams: List<TypeParameterDefinition>,
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

        val typeParams: MutableList<TypeParameterDefinition> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is MoiraiParser.IdentifierTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.IDENTIFIER().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }

                    is MoiraiParser.FinTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.FIN().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Fin))
                    }
                }
            }
        }

        val ft = typeVisitor.visit(ctx.ft) as FunctionTypeLiteral
        val ce = costVisitor.visit(ctx.ce)
        accumulatedPlugins.add(PluginDefLiteral(id, typeParams, ft, ce))
    }

    fun listPlugins(): List<PluginDefLiteral> = accumulatedPlugins.toList()
}