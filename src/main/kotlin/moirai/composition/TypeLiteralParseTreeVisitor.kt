package moirai.composition

import moirai.grammar.MoiraiParser
import moirai.grammar.MoiraiParserBaseVisitor
import moirai.semantics.core.*

internal class TypeLiteralParseTreeVisitor(
    private val fileName: String,
    private val errors: LanguageErrors
) : MoiraiParserBaseVisitor<Signifier>() {
    override fun visitGroundType(ctx: MoiraiParser.GroundTypeContext): Signifier {
        val res = Identifier(createContext(fileName, ctx.IDENTIFIER().symbol), ctx.IDENTIFIER().text.toString())
        return res
    }

    override fun visitParameterizedType(ctx: MoiraiParser.ParameterizedTypeContext): Signifier {
        val tti = Identifier(createContext(fileName, ctx.start), ctx.IDENTIFIER().text.toString())

        val args: MutableList<Signifier> = ArrayList()
        ctx.params.restrictedTypeExprOrCostExpr().forEach {
            val param = visit(it)
            args.add(param)
        }

        val res = ParameterizedSignifier(createContext(fileName, ctx.start), tti, args)
        return res
    }

    override fun visitRestrictedGroundType(ctx: MoiraiParser.RestrictedGroundTypeContext): Signifier {
        val res = Identifier(createContext(fileName, ctx.IDENTIFIER().symbol), ctx.IDENTIFIER().text.toString())
        return res
    }

    override fun visitRestrictedParameterizedType(ctx: MoiraiParser.RestrictedParameterizedTypeContext): Signifier {
        val tti = Identifier(createContext(fileName, ctx.start), ctx.IDENTIFIER().text.toString())

        val args: MutableList<Signifier> = ArrayList()
        ctx.params.restrictedTypeExprOrCostExpr().forEach {
            val param = visit(it)
            args.add(param)
        }

        val res = ParameterizedSignifier(createContext(fileName, ctx.start), tti, args)
        return res
    }

    override fun visitMultiParamFunctionType(ctx: MoiraiParser.MultiParamFunctionTypeContext): Signifier {
        val params: MutableList<Signifier> = ArrayList()
        val ret = visit(ctx.ret)

        ctx.params.restrictedTypeExpr().forEach {
            val param = visit(it)
            params.add(param)
        }

        val res = FunctionTypeLiteral(createContext(fileName, ctx.start), params, ret)
        return res
    }

    override fun visitNoParamFunctionType(ctx: MoiraiParser.NoParamFunctionTypeContext): Signifier {
        val params: MutableList<Signifier> = ArrayList()
        val ret = visit(ctx.ret)

        val res = FunctionTypeLiteral(createContext(fileName, ctx.start), params, ret)
        return res
    }

    override fun visitOneParamFunctionType(ctx: MoiraiParser.OneParamFunctionTypeContext): Signifier {
        val params: MutableList<Signifier> = ArrayList()
        val ret = visit(ctx.ret)

        val input = visit(ctx.input)
        params.add(input)

        val res = FunctionTypeLiteral(createContext(fileName, ctx.start), params, ret)
        return res
    }

    override fun visitCostApply(ctx: MoiraiParser.CostApplyContext): Signifier {
        val args = ctx.args.costExpr().map { visit(it) }
        val sourceContext = createContext(fileName, ctx.id)
        return when (val id = ctx.id.text) {
            CostOperator.Sum.idStr -> {
                InvokeSignifier(sourceContext, CostOperator.Sum, args)
            }

            CostOperator.Mul.idStr -> {
                InvokeSignifier(sourceContext, CostOperator.Mul, args)
            }

            CostOperator.Max.idStr -> {
                InvokeSignifier(sourceContext, CostOperator.Max, args)
            }

            else -> {

                errors.add(sourceContext, InvalidCostExpressionFunctionName(id))
                FinLiteral(sourceContext, 0)
            }
        }
    }

    override fun visitCostMag(ctx: MoiraiParser.CostMagContext): Signifier {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            FinLiteral(sourceContext, ctx.value.text.toLong())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidFinLiteral(ctx.value.text))
            FinLiteral(sourceContext, 0)
        }
        return res
    }

    override fun visitCostIdent(ctx: MoiraiParser.CostIdentContext): Signifier {
        return Identifier(createContext(fileName, ctx.id), ctx.id.text)
    }
}
