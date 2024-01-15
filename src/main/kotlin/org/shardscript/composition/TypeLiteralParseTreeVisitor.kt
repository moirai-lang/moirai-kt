package org.shardscript.composition

import org.shardscript.grammar.ShardScriptParser
import org.shardscript.grammar.ShardScriptParserBaseVisitor
import org.shardscript.semantics.core.*

internal class TypeLiteralParseTreeVisitor(private val fileName: String) : ShardScriptParserBaseVisitor<Signifier>() {
    override fun visitGroundType(ctx: ShardScriptParser.GroundTypeContext): Signifier {
        val res = Identifier(ctx.IDENTIFIER().text.toString())
        res.ctx = createContext(fileName, ctx.IDENTIFIER().symbol)
        return res
    }

    override fun visitParameterizedType(ctx: ShardScriptParser.ParameterizedTypeContext): Signifier {
        val tti = Identifier(ctx.IDENTIFIER().text.toString())
        tti.ctx = createContext(fileName, ctx.start)

        val args: MutableList<Signifier> = ArrayList()
        ctx.params.restrictedTypeExprOrLiteral().forEach {
            val param = visit(it)
            args.add(param)
        }

        val res = ParameterizedSignifier(tti, args)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitRestrictedGroundType(ctx: ShardScriptParser.RestrictedGroundTypeContext): Signifier {
        val res = Identifier(ctx.IDENTIFIER().text.toString())
        res.ctx = createContext(fileName, ctx.IDENTIFIER().symbol)
        return res
    }

    override fun visitRestrictedParameterizedType(ctx: ShardScriptParser.RestrictedParameterizedTypeContext): Signifier {
        val tti = Identifier(ctx.IDENTIFIER().text.toString())
        tti.ctx = createContext(fileName, ctx.start)

        val args: MutableList<Signifier> = ArrayList()
        ctx.params.restrictedTypeExprOrLiteral().forEach {
            val param = visit(it)
            args.add(param)
        }

        val res = ParameterizedSignifier(tti, args)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitFinLiteral(ctx: ShardScriptParser.FinLiteralContext): Signifier {
        val res = FinLiteral(ctx.magnitude.text.toLong())
        res.ctx = createContext(fileName, ctx.magnitude)
        return res
    }

    override fun visitNoFin(ctx: ShardScriptParser.NoFinContext): Signifier {
        return visit(ctx.te)
    }

    override fun visitMultiParamFunctionType(ctx: ShardScriptParser.MultiParamFunctionTypeContext): Signifier {
        val params: MutableList<Signifier> = ArrayList()
        val ret = visit(ctx.ret)

        ctx.params.restrictedTypeExpr().forEach {
            val param = visit(it)
            params.add(param)
        }

        val res = FunctionTypeLiteral(params, ret)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitNoParamFunctionType(ctx: ShardScriptParser.NoParamFunctionTypeContext): Signifier {
        val params: MutableList<Signifier> = ArrayList()
        val ret = visit(ctx.ret)

        val res = FunctionTypeLiteral(params, ret)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitOneParamFunctionType(ctx: ShardScriptParser.OneParamFunctionTypeContext): Signifier {
        val params: MutableList<Signifier> = ArrayList()
        val ret = visit(ctx.ret)

        val input = visit(ctx.input)
        params.add(input)

        val res = FunctionTypeLiteral(params, ret)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }
}
