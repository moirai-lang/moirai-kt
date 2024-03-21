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
        ctx.params.restrictedTypeExprOrLiteral().forEach {
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
        ctx.params.restrictedTypeExprOrLiteral().forEach {
            val param = visit(it)
            args.add(param)
        }

        val res = ParameterizedSignifier(createContext(fileName, ctx.start), tti, args)
        return res
    }

    override fun visitFinLiteral(ctx: MoiraiParser.FinLiteralContext): Signifier {
        val sourceContext = createContext(fileName, ctx.magnitude)
        val res = try {
            FinLiteral(sourceContext, ctx.magnitude.text.toLong())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidFinLiteral(ctx.magnitude.text))
            FinLiteral(sourceContext, 0)
        }
        return res
    }

    override fun visitNoFin(ctx: MoiraiParser.NoFinContext): Signifier {
        return visit(ctx.te)
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
}
