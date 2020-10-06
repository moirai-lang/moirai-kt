/**
 * Copyright 2020 Bryan Croteau
 */
package com.tsikhe.shardscript.composition

import com.tsikhe.shardscript.grammar.ShardScriptParser
import com.tsikhe.shardscript.grammar.ShardScriptParserBaseVisitor
import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.prelude.*
import java.math.BigDecimal

internal class AstParseTreeVisitor(private val fileName: String, val errors: LanguageErrors) :
    ShardScriptParserBaseVisitor<Ast>() {
    private val typeVisitor = TypeLiteralParseTreeVisitor(fileName)

    override fun visitFile(ctx: ShardScriptParser.FileContext): Ast {
        val stats = ctx.stat().map { visit(it) }

        val res = FileAst(stats)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitBlock(ctx: ShardScriptParser.BlockContext): Ast {
        val stats = ctx.stat().map { visit(it) }

        val res = BlockAst(stats.toMutableList())
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitEnumDefBody(ctx: ShardScriptParser.EnumDefBodyContext): Ast {
        val stats = ctx.enumDefBodyStat().map { visit(it) }

        val res = BlockAst(stats.toMutableList())
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitMutableLet(ctx: ShardScriptParser.MutableLetContext): Ast {
        val right = visit(ctx.right)
        val identifier = GroundIdentifier(ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral()
        }

        val res = LetAst(identifier, of, right, true)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitImmutableLet(ctx: ShardScriptParser.ImmutableLetContext): Ast {
        val right = visit(ctx.right)
        val identifier = GroundIdentifier(ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral()
        }

        val res = LetAst(identifier, of, right, false)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitRefExpr(ctx: ShardScriptParser.RefExprContext): Ast {
        val identifier = GroundIdentifier(ctx.text)

        val res = RefAst(identifier)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitParenExpr(ctx: ShardScriptParser.ParenExprContext): Ast {
        return visit(ctx.inner)
    }

    override fun visitUnaryNot(ctx: ShardScriptParser.UnaryNotContext): Ast {
        val op = UnaryOperator.Not
        val right = visit(ctx.right)
        val args: List<Ast> = listOf()

        val res = DotApplyAst(right, GroundIdentifier(op.idStr), args)
        res.ctx = createContext(fileName, ctx.op)
        return res
    }

    override fun visitUnaryNegate(ctx: ShardScriptParser.UnaryNegateContext): Ast {
        val op = UnaryOperator.Negate
        val right = visit(ctx.right)
        val args: List<Ast> = listOf()

        val res = DotApplyAst(right, GroundIdentifier(op.idStr), args)
        res.ctx = createContext(fileName, ctx.op)
        return res
    }

    override fun visitInfixMulDivMod(ctx: ShardScriptParser.InfixMulDivModContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val op = when (ctx.op.text) {
            BinaryOperator.Mul.opStr -> BinaryOperator.Mul
            BinaryOperator.Div.opStr -> BinaryOperator.Div
            else -> BinaryOperator.Mod
        }

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitInfixAddSub(ctx: ShardScriptParser.InfixAddSubContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val op = if (ctx.op.text == BinaryOperator.Add.opStr) {
            BinaryOperator.Add
        } else {
            BinaryOperator.Sub
        }

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitInfixOrder(ctx: ShardScriptParser.InfixOrderContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val op = when (ctx.op.text) {
            BinaryOperator.GreaterThan.opStr -> BinaryOperator.GreaterThan
            BinaryOperator.GreaterThanEqual.opStr -> BinaryOperator.GreaterThanEqual
            BinaryOperator.LessThan.opStr -> BinaryOperator.LessThan
            else -> BinaryOperator.LessThanEqual
        }

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitTypeRelation(ctx: ShardScriptParser.TypeRelationContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val left = visit(ctx.left)
        return when (ctx.op.text) {
            TypeRelations.As.idStr -> {
                val res = AsAst(left, typeVisitor.visit(ctx.id))
                res.ctx = sourceContext
                res
            }
            else -> {
                val res = IsAst(left, typeVisitor.visit(ctx.id))
                res.ctx = sourceContext
                res
            }
        }
    }

    override fun visitInfixEquality(ctx: ShardScriptParser.InfixEqualityContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val op = if (ctx.op.text == BinaryOperator.Equal.opStr) {
            BinaryOperator.Equal
        } else {
            BinaryOperator.NotEqual
        }

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitInfixAnd(ctx: ShardScriptParser.InfixAndContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val op = BinaryOperator.And

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitInfixOr(ctx: ShardScriptParser.InfixOrContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val op = BinaryOperator.Or

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitLiteralSByte(ctx: ShardScriptParser.LiteralSByteContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            SByteLiteralAst(ctx.value.text.replace(Lang.sByteSuffix, "").toByte())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.sByteId.name, ctx.value.text))
            SByteLiteralAst(0)
        }
        res.ctx = sourceContext
        return res
    }

    override fun visitLiteralShort(ctx: ShardScriptParser.LiteralShortContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            ShortLiteralAst(ctx.value.text.replace(Lang.shortSuffix, "").toShort())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.shortId.name, ctx.value.text))
            ShortLiteralAst(0)
        }
        res.ctx = sourceContext
        return res
    }

    override fun visitLiteralInt(ctx: ShardScriptParser.LiteralIntContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            IntLiteralAst(ctx.value.text.toInt())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.intId.name, ctx.value.text))
            IntLiteralAst(0)
        }
        res.ctx = sourceContext
        return res
    }

    override fun visitLiteralLong(ctx: ShardScriptParser.LiteralLongContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            LongLiteralAst(ctx.value.text.replace(Lang.longSuffix, "").toLong())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.longId.name, ctx.value.text))
            LongLiteralAst(0)
        }
        res.ctx = sourceContext
        return res
    }

    @UseExperimental(ExperimentalUnsignedTypes::class)
    override fun visitLiteralByte(ctx: ShardScriptParser.LiteralByteContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            ByteLiteralAst(ctx.value.text.replace(Lang.byteSuffix, "").toUByte())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.byteId.name, ctx.value.text))
            ByteLiteralAst((0).toUByte())
        }
        res.ctx = sourceContext
        return res
    }

    @UseExperimental(ExperimentalUnsignedTypes::class)
    override fun visitLiteralUShort(ctx: ShardScriptParser.LiteralUShortContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            UShortLiteralAst(ctx.value.text.replace(Lang.uShortSuffix, "").toUShort())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.uShortId.name, ctx.value.text))
            UShortLiteralAst((0).toUShort())
        }
        res.ctx = sourceContext
        return res
    }

    @UseExperimental(ExperimentalUnsignedTypes::class)
    override fun visitLiteralUInt(ctx: ShardScriptParser.LiteralUIntContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            UIntLiteralAst(ctx.value.text.replace(Lang.uIntSuffix, "").toUInt())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.uIntId.name, ctx.value.text))
            UIntLiteralAst((0).toUInt())
        }
        res.ctx = sourceContext
        return res
    }

    @UseExperimental(ExperimentalUnsignedTypes::class)
    override fun visitLiteralULong(ctx: ShardScriptParser.LiteralULongContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            ULongLiteralAst(ctx.value.text.replace(Lang.uLongSuffix, "").toULong())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.uLongId.name, ctx.value.text))
            ULongLiteralAst((0).toULong())
        }
        res.ctx = sourceContext
        return res
    }

    override fun visitLiteralBool(ctx: ShardScriptParser.LiteralBoolContext): Ast {
        val res = BooleanLiteralAst(ctx.value.text!!.toBoolean())
        res.ctx = createContext(fileName, ctx.value)
        return res
    }

    override fun visitLiteralDecimal(ctx: ShardScriptParser.LiteralDecimalContext): Ast {
        val bigDecimal = BigDecimal(ctx.value.text)

        val res = DecimalLiteralAst(bigDecimal)
        res.ctx = createContext(fileName, ctx.value)
        return res
    }

    override fun visitLiteralChar(ctx: ShardScriptParser.LiteralCharContext): Ast {
        val char = resurrectChar(ctx.value.text)

        val res = CharLiteralAst(char)
        res.ctx = createContext(fileName, ctx.value)
        return res
    }

    override fun visitNonEmptyString(ctx: ShardScriptParser.NonEmptyStringContext): Ast {
        val parts = ctx.parts.children.map { visit(it) }

        return if (parts.size == 1 && parts.first() is StringLiteralAst) {
            parts.first()
        } else {
            val res = StringInterpolationAst(parts.toMutableList())
            res.ctx = createContext(fileName, ctx.start)
            res
        }
    }

    override fun visitEmptyString(ctx: ShardScriptParser.EmptyStringContext): Ast {
        val res = StringLiteralAst("")
        res.ctx = createContext(fileName, ctx.start)
        return res
    }

    override fun visitStringChars(ctx: ShardScriptParser.StringCharsContext): Ast {
        val chars = ctx.chars
        val res = StringLiteralAst(resurrectString(chars.text))
        res.ctx = createContext(fileName, ctx.chars)
        return res
    }

    override fun visitStringInterp(ctx: ShardScriptParser.StringInterpContext): Ast {
        return visit(ctx.interp)
    }

    override fun visitStringExpr(ctx: ShardScriptParser.StringExprContext): Ast {
        return visit(ctx.value)
    }

    override fun visitFunDefStat(ctx: ShardScriptParser.FunDefStatContext): Ast {
        val typeParams: MutableList<GroundIdentifier> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is ShardScriptParser.IdentifierTypeParamContext -> {
                        val typeParam = GroundIdentifier(it.contextualId().text)
                        typeParam.ctx = createContext(fileName, it.contextualId().start)
                        typeParams.add(typeParam)
                    }
                    is ShardScriptParser.OmicronTypeParamContext -> {
                        val typeParam = GroundIdentifier(it.OMICRON().text)
                        typeParam.ctx = createContext(fileName, it.OMICRON().symbol)
                        typeParams.add(typeParam)
                    }
                }
            }
        }

        val params: MutableList<Binder> = ArrayList()
        if(ctx.params != null) {
            ctx.params.paramDef().forEach {
                val id = GroundIdentifier(it.id.text)
                val of = typeVisitor.visit(it.of)
                params.add(Binder(id, of))
            }
        }

        val ret = if(ctx.ret != null) {
            typeVisitor.visit(ctx.ret)
        } else {
            Lang.unitId
        }
        val body = visit(ctx.body) as BlockAst

        val id = GroundIdentifier(ctx.id.text)
        id.ctx = createContext(fileName, ctx.id.start)

        val res = FunctionAst(id, typeParams, params, ret, body)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitApplyExpr(ctx: ShardScriptParser.ApplyExprContext): Ast {
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val id = GroundIdentifier(ctx.id.text)
        val res = GroundApplyAst(id, args)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitParamApplyExpr(ctx: ShardScriptParser.ParamApplyExprContext): Ast {
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val typeArgs = ctx.params.typeExprWithOmicron().map {
            typeVisitor.visit(it)
        }

        val id = ParameterizedIdentifier(GroundIdentifier(ctx.id.text), typeArgs)
        val res = GroundApplyAst(id, args)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitEnumDefStat(ctx: ShardScriptParser.EnumDefStatContext): Ast {
        val typeParams: MutableList<GroundIdentifier> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is ShardScriptParser.IdentifierTypeParamContext -> {
                        val typeParam = GroundIdentifier(it.contextualId().text)
                        typeParam.ctx = createContext(fileName, it.contextualId().start)
                        typeParams.add(typeParam)
                    }
                    is ShardScriptParser.OmicronTypeParamContext -> {
                        val typeParam = GroundIdentifier(it.OMICRON().text)
                        typeParam.ctx = createContext(fileName, it.OMICRON().symbol)
                        typeParams.add(typeParam)
                    }
                }
            }
        }

        val records: MutableList<RecordDefinitionAst> = ArrayList()
        val objects: MutableList<ObjectDefinitionAst> = ArrayList()
        if (ctx.body != null) {
            val block = visit(ctx.body) as BlockAst
            block.lines.forEach {
                when (it) {
                    is RecordDefinitionAst -> records.add(it)
                    is ObjectDefinitionAst -> objects.add(it)
                    else -> errors.add(it.ctx, InvalidEnumMember)
                }
            }
        }

        val id = GroundIdentifier(ctx.id.text)
        val res = EnumDefinitionAst(
            id,
            typeParams,
            records,
            objects
        )
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitObjectDefStat(ctx: ShardScriptParser.ObjectDefStatContext): Ast {
        val id = GroundIdentifier(ctx.id.text)
        val res = ObjectDefinitionAst(id)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitRecordDefStat(ctx: ShardScriptParser.RecordDefStatContext): Ast {
        val typeParams: MutableList<GroundIdentifier> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is ShardScriptParser.IdentifierTypeParamContext -> {
                        val typeParam = GroundIdentifier(it.contextualId().text)
                        typeParam.ctx = createContext(fileName, it.contextualId().start)
                        typeParams.add(typeParam)
                    }
                    is ShardScriptParser.OmicronTypeParamContext -> {
                        val typeParam = GroundIdentifier(it.OMICRON().text)
                        typeParam.ctx = createContext(fileName, it.OMICRON().symbol)
                        typeParams.add(typeParam)
                    }
                }
            }
        }

        val fields: MutableList<FieldDef> = ArrayList()
        ctx.fields.fieldDef().forEach {
            when (it) {
                is ShardScriptParser.ImmutableFieldContext -> {
                    val id = GroundIdentifier(it.id.text)
                    val of = typeVisitor.visit(it.of)
                    val mutable = false
                    fields.add(FieldDef(id, of, mutable))
                }
                is ShardScriptParser.MutableFieldContext -> {
                    val id = GroundIdentifier(it.id.text)
                    val of = typeVisitor.visit(it.of)
                    val mutable = true
                    fields.add(FieldDef(id, of, mutable))
                }
            }
        }

        val id = GroundIdentifier(ctx.id.text)
        val res = RecordDefinitionAst(
            id,
            typeParams,
            fields
        )
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitDotExpr(ctx: ShardScriptParser.DotExprContext): Ast {
        val lhs = visit(ctx.left)
        val id = GroundIdentifier(ctx.id.text)
        val res = DotAst(lhs, id)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitParamDotApply(ctx: ShardScriptParser.ParamDotApplyContext): Ast {
        val lhs = visit(ctx.left)
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val typeArgs = ctx.params.typeExprWithOmicron().map {
            typeVisitor.visit(it)
        }

        val id = ParameterizedIdentifier(GroundIdentifier(ctx.id.text), typeArgs)
        val res = DotApplyAst(lhs, id, args)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitDotApply(ctx: ShardScriptParser.DotApplyContext): Ast {
        val lhs = visit(ctx.left)
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val id = GroundIdentifier(ctx.id.text)
        val res = DotApplyAst(lhs, id, args)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitIndexExpr(ctx: ShardScriptParser.IndexExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.start)
        val method = CollectionMethods.IndexLookup

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), method, sourceContext)
    }

    override fun visitForStat(ctx: ShardScriptParser.ForStatContext): Ast {
        val right = visit(ctx.source)
        val identifier = GroundIdentifier(ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral()
        }

        val body = visit(ctx.body)

        val res = ForEachAst(identifier, of, right, body)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitMapExpr(ctx: ShardScriptParser.MapExprContext): Ast {
        val right = visit(ctx.source)
        val identifier = GroundIdentifier(ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral()
        }

        val body = visit(ctx.body)

        val res = MapAst(identifier, of, right, body)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitFlatMapExpr(ctx: ShardScriptParser.FlatMapExprContext): Ast {
        val right = visit(ctx.source)
        val identifier = GroundIdentifier(ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral()
        }

        val body = visit(ctx.body)

        val res = FlatMapAst(identifier, of, right, body)
        res.ctx = createContext(fileName, ctx.id.start)
        return res
    }

    override fun visitAssignStat(ctx: ShardScriptParser.AssignStatContext): Ast {
        val rhs = visit(ctx.right)
        val sourceContext = createContext(fileName, ctx.start)
        return when (val lhs = ctx.left) {
            is ShardScriptParser.IndexExprContext -> {
                val op = CollectionMethods.IndexAssign

                val childLeft = visit(lhs.left)
                val childIndex = visit(lhs.right)
                return rewriteAsDotApply(childLeft, listOf(childIndex, rhs), op, sourceContext)
            }
            is ShardScriptParser.DotExprContext -> {
                val childLeft = visit(lhs.left)
                val gid = GroundIdentifier(lhs.id.text)
                return DotAssignAst(childLeft, gid, rhs)
            }
            is ShardScriptParser.RefExprContext -> {
                val gid = GroundIdentifier(lhs.id.text)
                return AssignAst(gid, rhs)
            }
            else -> {
                val lhsCtx = createContext(fileName, ctx.left.start)
                errors.add(lhsCtx, InvalidAssign)
                rhs
            }
        }
    }

    override fun visitToExpr(ctx: ShardScriptParser.ToExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsGroundApply(listOf(left, right), Lang.pairId, sourceContext)
    }

    override fun visitAnyIf(ctx: ShardScriptParser.AnyIfContext): Ast {
        return visit(ctx.anyif)
    }

    override fun visitIfElseIfExpr(ctx: ShardScriptParser.IfElseIfExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = visit(ctx.trueb)
        val falseBranch = visit(ctx.elif)
        val res = IfAst(condition, trueBranch, falseBranch)
        res.ctx = sourceContext
        return res
    }

    override fun visitIfElseExpr(ctx: ShardScriptParser.IfElseExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = visit(ctx.trueb)
        val falseBranch = visit(ctx.falseb)
        val res = IfAst(condition, trueBranch, falseBranch)
        res.ctx = sourceContext
        return res
    }

    override fun visitStandaloneIfExpr(ctx: ShardScriptParser.StandaloneIfExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = visit(ctx.trueb)
        val falseBranch = BlockAst(mutableListOf(RefAst(Lang.unitId)))
        val res = IfAst(condition, trueBranch, falseBranch)
        res.ctx = sourceContext
        return res
    }

    override fun visitSwitchExpr(ctx: ShardScriptParser.SwitchExprContext): Ast {
        val right = visit(ctx.source)

        val cases: MutableList<CaseBranch> = ArrayList()
        ctx.alternatives.enumCase().forEach {
            val branchCtx = createContext(fileName, it.id.start)
            val body = visit(it.body) as BlockAst
            val gid = GroundIdentifier(it.id.text)
            gid.ctx = branchCtx
            cases.add(CoproductBranch(branchCtx, gid, body))
        }
        if (ctx.alternatives.elseCase() != null) {
            val elseCase = ctx.alternatives.elseCase()
            val branchCtx = createContext(fileName, elseCase.start)
            val body = visit(elseCase.body) as BlockAst
            cases.add(ElseBranch(branchCtx, body))
        }

        val res = SwitchAst(right, cases)
        res.ctx = createContext(fileName, ctx.start)
        return res
    }
}
