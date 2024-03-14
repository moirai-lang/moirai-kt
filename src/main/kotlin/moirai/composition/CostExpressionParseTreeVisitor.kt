package moirai.composition

import moirai.grammar.MoiraiParser
import moirai.grammar.MoiraiParserBaseVisitor
import moirai.semantics.core.*

internal class CostExpressionParseTreeVisitor(
    private val fileName: String,
    private val errors: LanguageErrors
) : MoiraiParserBaseVisitor<CostExpression>() {
    override fun visitCostApply(ctx: MoiraiParser.CostApplyContext): CostExpression {
        val args = ctx.args.costExpr().map { visit(it) }
        return when(val id = ctx.id.text) {
            CostOperator.Sum.idStr -> {
                SumCostExpression(args)
            }
            CostOperator.Mul.idStr -> {
                ProductCostExpression(args)
            }
            CostOperator.Max.idStr -> {
                MaxCostExpression(args)
            }
            else -> {
                val sourceContext = createContext(fileName, ctx.id)
                errors.add(sourceContext, InvalidCostExpressionFunctionName(id))
                Fin(0)
            }
        }
    }

    override fun visitCostMag(ctx: MoiraiParser.CostMagContext): CostExpression {
        val res = try {
            Fin(ctx.value.text.toInt().toLong())
        } catch (_: Exception) {
            val sourceContext = createContext(fileName, ctx.value)
            errors.add(sourceContext, InvalidFinLiteral(ctx.value.text))
            Fin(0)
        }
        return res
    }

    override fun visitCostIdent(ctx: MoiraiParser.CostIdentContext): CostExpression {
        return FinTypeParameter(ctx.id.text, Identifier(createContext(fileName, ctx.id), ctx.id.text))
    }
}