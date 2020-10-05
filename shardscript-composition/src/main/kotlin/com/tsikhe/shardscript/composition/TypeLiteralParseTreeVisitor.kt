package com.tsikhe.shardscript.composition

import com.tsikhe.shardscript.grammar.ShardScriptParser
import com.tsikhe.shardscript.grammar.ShardScriptParserBaseVisitor
import com.tsikhe.shardscript.semantics.core.*

internal class TypeLiteralParseTreeVisitor(private val fileName: String) : ShardScriptParserBaseVisitor<Identifier>() {
    override fun visitMultiTypePath(ctx: ShardScriptParser.MultiTypePathContext): Identifier {
        val res = PathIdentifier(ctx.contextualId().map {
            val gid = GroundIdentifier(it.text)
            gid.ctx = createContext(fileName, it.start)
            gid
        })
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitSingleTypePath(ctx: ShardScriptParser.SingleTypePathContext): Identifier {
        val res = GroundIdentifier(ctx.contextualId().text.toString())
        res.ctx = createContext(fileName, ctx.contextualId().start)
        return res
    }

    override fun visitGroundType(ctx: ShardScriptParser.GroundTypeContext): Identifier {
        return visit(ctx.id)
    }

    override fun visitParameterizedType(ctx: ShardScriptParser.ParameterizedTypeContext): Identifier {
        val tti = visit(ctx.id) as TerminalTextIdentifier
        tti.ctx = createContext(fileName, ctx.id.start)

        val args: MutableList<Identifier> = ArrayList()
        ctx.params.typeExprWithOmicron().forEach {
            val param = visit(it)
            args.add(param)
        }

        val res = ParameterizedIdentifier(tti, args)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitOmicronLiteral(ctx: ShardScriptParser.OmicronLiteralContext): Identifier {
        val res = OmicronLiteral(ctx.magnitude.text.toBigInteger())
        res.ctx = createContext(fileName, ctx.magnitude)
        return res
    }

    override fun visitOmicronType(ctx: ShardScriptParser.OmicronTypeContext): Identifier {
        val res = GroundIdentifier(ctx.omicron.text.toString())
        res.ctx = createContext(fileName, ctx.omicron)
        return res
    }

    override fun visitNoOmicron(ctx: ShardScriptParser.NoOmicronContext): Identifier {
        return visit(ctx.te)
    }

    override fun visitMultiParamFunctionType(ctx: ShardScriptParser.MultiParamFunctionTypeContext): Identifier {
        val params: MutableList<Identifier> = ArrayList()
        val ret = visit(ctx.ret)

        ctx.params.typeExpr().forEach {
            val param = visit(it)
            params.add(param)
        }

        val res = FunctionTypeLiteral(params, ret)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitNoParamFunctionType(ctx: ShardScriptParser.NoParamFunctionTypeContext): Identifier {
        val params: MutableList<Identifier> = ArrayList()
        val ret = visit(ctx.ret)

        val res = FunctionTypeLiteral(params, ret)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitOneParamFunctionType(ctx: ShardScriptParser.OneParamFunctionTypeContext): Identifier {
        val params: MutableList<Identifier> = ArrayList()
        val ret = visit(ctx.ret)

        val input = visit(ctx.input)
        params.add(input)

        val res = FunctionTypeLiteral(params, ret)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }
}
