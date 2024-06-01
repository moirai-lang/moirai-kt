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

            CostOperator.Named.idStr -> {
                errors.add(sourceContext, InvalidNamedCostExpressionArgs)
                FinLiteral(sourceContext, 0)
            }

            else -> {

                errors.add(sourceContext, InvalidCostExpressionFunctionName(id))
                FinLiteral(sourceContext, 0)
            }
        }
    }

    override fun visitCostNamed(ctx: MoiraiParser.CostNamedContext): Signifier {
        val sourceContext = createContext(fileName, ctx.id)
        return when (val id = ctx.id.text) {
            CostOperator.Sum.idStr -> {
                errors.add(sourceContext, InvalidSumCostExpressionArgs)
                FinLiteral(sourceContext, 0)
            }

            CostOperator.Mul.idStr -> {
                errors.add(sourceContext, InvalidMulCostExpressionArgs)
                FinLiteral(sourceContext, 0)
            }

            CostOperator.Max.idStr -> {
                errors.add(sourceContext, InvalidMaxCostExpressionArgs)
                FinLiteral(sourceContext, 0)
            }

            CostOperator.Named.idStr -> {
                visit(ctx.arg)
            }

            else -> {

                errors.add(sourceContext, InvalidCostExpressionFunctionName(id))
                FinLiteral(sourceContext, 0)
            }
        }
    }

    override fun visitNonEmptyString(ctx: MoiraiParser.NonEmptyStringContext): Signifier {
        val sourceContext = createContext(fileName, ctx.start)

        if (ctx.parts.children.size != 1) {
            errors.add(sourceContext, InvalidNamedCostExpressionArgs)
            return NamedCost(sourceContext, "")
        }

        val first = ctx.parts.children.first()

        if (first !is MoiraiParser.StringCharsContext) {
            errors.add(sourceContext, InvalidNamedCostExpressionArgs)
            return NamedCost(sourceContext, "")
        }

        val chars = first.chars
        val str = resurrectString(chars.text)
        return NamedCost(sourceContext, str)
    }

    override fun visitEmptyString(ctx: MoiraiParser.EmptyStringContext): Signifier {
        val sourceContext = createContext(fileName, ctx.start)
        errors.add(sourceContext, InvalidNamedCostExpressionArgs)
        return NamedCost(sourceContext, "")
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
