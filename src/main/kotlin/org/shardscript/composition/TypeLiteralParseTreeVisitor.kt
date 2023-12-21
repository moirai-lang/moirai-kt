package org.shardscript.composition

import org.shardscript.grammar.ShardScriptParser
import org.shardscript.grammar.ShardScriptParserBaseVisitor
import org.shardscript.semantics.phases.parse.*

internal class TypeLiteralParseTreeVisitor(private val fileName: String) : ShardScriptParserBaseVisitor<PostParseSignifier>() {
    override fun visitMultiTypePath(ctx: ShardScriptParser.MultiTypePathContext): PostParseSignifier {
        val res = PostParsePathSignifier(createContext(fileName, ctx.start), ctx.IDENTIFIER().map {
            PostParseIdentifier(createContext(fileName, it.symbol), it.text)
        })
        return res
    }

    override fun visitSingleTypePath(ctx: ShardScriptParser.SingleTypePathContext): PostParseSignifier {
        val res = PostParseIdentifier(createContext(fileName, ctx.IDENTIFIER().symbol), ctx.IDENTIFIER().text.toString())
        return res
    }

    override fun visitGroundType(ctx: ShardScriptParser.GroundTypeContext): PostParseSignifier {
        return visit(ctx.id)
    }

    override fun visitParameterizedType(ctx: ShardScriptParser.ParameterizedTypeContext): PostParseSignifier {
        val tti = visit(ctx.id) as PostParseTerminalSignifier

        val args: MutableList<PostParseSignifier> = ArrayList()
        ctx.params.restrictedTypeExprOrLiteral().forEach {
            val param = visit(it)
            args.add(param)
        }

        val res = PostParseParameterizedSignifier(createContext(fileName, ctx.start), tti, args)
        return res
    }

    override fun visitRestrictedGroundType(ctx: ShardScriptParser.RestrictedGroundTypeContext): PostParseSignifier {
        return visit(ctx.id)
    }

    override fun visitRestrictedParameterizedType(ctx: ShardScriptParser.RestrictedParameterizedTypeContext): PostParseSignifier {
        val tti = visit(ctx.id) as PostParseTerminalSignifier

        val args: MutableList<PostParseSignifier> = ArrayList()
        ctx.params.restrictedTypeExprOrLiteral().forEach {
            val param = visit(it)
            args.add(param)
        }

        val res = PostParseParameterizedSignifier(createContext(fileName, ctx.start), tti, args)
        return res
    }

    override fun visitFinLiteral(ctx: ShardScriptParser.FinLiteralContext): PostParseSignifier {
        val res = PostParseFinLiteral(createContext(fileName, ctx.magnitude), ctx.magnitude.text.toLong())
        return res
    }

    override fun visitNoFin(ctx: ShardScriptParser.NoFinContext): PostParseSignifier {
        return visit(ctx.te)
    }

    override fun visitMultiParamFunctionType(ctx: ShardScriptParser.MultiParamFunctionTypeContext): PostParseSignifier {
        val params: MutableList<PostParseSignifier> = ArrayList()
        val ret = visit(ctx.ret)

        ctx.params.restrictedTypeExpr().forEach {
            val param = visit(it)
            params.add(param)
        }

        val res = PostParseFunctionTypeLiteral(createContext(fileName, ctx.start), params, ret)
        return res
    }

    override fun visitNoParamFunctionType(ctx: ShardScriptParser.NoParamFunctionTypeContext): PostParseSignifier {
        val params: MutableList<PostParseSignifier> = ArrayList()
        val ret = visit(ctx.ret)

        val res = PostParseFunctionTypeLiteral(createContext(fileName, ctx.start), params, ret)
        return res
    }

    override fun visitOneParamFunctionType(ctx: ShardScriptParser.OneParamFunctionTypeContext): PostParseSignifier {
        val params: MutableList<PostParseSignifier> = ArrayList()
        val ret = visit(ctx.ret)

        val input = visit(ctx.input)
        params.add(input)

        val res = PostParseFunctionTypeLiteral(createContext(fileName, ctx.start), params, ret)
        return res
    }
}
