package org.shardscript.composition

import org.shardscript.grammar.ShardScriptParser
import org.shardscript.grammar.ShardScriptParserBaseVisitor
import org.shardscript.semantics.core.*
import org.shardscript.semantics.prelude.*
import java.math.BigDecimal

internal class AstParseTreeVisitor(private val fileName: String, val errors: LanguageErrors) :
    ShardScriptParserBaseVisitor<Ast>() {
    private val typeVisitor = TypeLiteralParseTreeVisitor(fileName)

    override fun visitFile(ctx: ShardScriptParser.FileContext): Ast {
        val stats = ctx.stat().map { visit(it) }

        val res = FileAst(createContext(fileName, ctx.start), stats)
        return res
    }

    override fun visitBlock(ctx: ShardScriptParser.BlockContext): Ast {
        val stats = ctx.stat().map { visit(it) }

        val res = BlockAst(createContext(fileName, ctx.start), stats.toMutableList())
        return res
    }

    override fun visitBlockExpr(ctx: ShardScriptParser.BlockExprContext): Ast {
        val stats = ctx.stat().map { visit(it) }

        val res = BlockAst(createContext(fileName, ctx.start), stats.toMutableList())
        return res
    }

    override fun visitMutableLet(ctx: ShardScriptParser.MutableLetContext): Ast {
        val right = visit(ctx.right)
        val identifier = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral(NotInSource)
        }

        val res = LetAst(createContext(fileName, ctx.MUTABLE().symbol), identifier, of, right, true)
        return res
    }

    override fun visitImmutableLet(ctx: ShardScriptParser.ImmutableLetContext): Ast {
        val right = visit(ctx.right)
        val identifier = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral(NotInSource)
        }

        val res = LetAst(createContext(fileName, ctx.VAL().symbol), identifier, of, right, false)
        return res
    }

    override fun visitRefExpr(ctx: ShardScriptParser.RefExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.start)
        val identifier = Identifier(sourceContext, ctx.text)

        val res = RefAst(sourceContext, identifier)
        return res
    }

    override fun visitParenExpr(ctx: ShardScriptParser.ParenExprContext): Ast {
        return visit(ctx.inner)
    }

    override fun visitUnaryNot(ctx: ShardScriptParser.UnaryNotContext): Ast {
        val op = UnaryOperator.Not
        val right = visit(ctx.right)
        val args: List<Ast> = listOf()

        val res = DotApplyAst(
            createContext(fileName, ctx.op),
            right,
            Identifier(createContext(fileName, ctx.right.start), op.idStr),
            args
        )
        return res
    }

    override fun visitUnaryNegate(ctx: ShardScriptParser.UnaryNegateContext): Ast {
        val op = UnaryOperator.Negate
        val right = visit(ctx.right)
        val args: List<Ast> = listOf()

        val res = DotApplyAst(createContext(fileName, ctx.op), right, Identifier(createContext(fileName, ctx.right.start), op.idStr), args)
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

    override fun visitLiteralInt(ctx: ShardScriptParser.LiteralIntContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            IntLiteralAst(sourceContext, ctx.value.text.toInt())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.intId.name, ctx.value.text))
            IntLiteralAst(sourceContext, 0)
        }
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
        val typeParams: MutableList<TypeParameterDefinition> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is ShardScriptParser.IdentifierTypeParamContext -> {
                        val typeParam = Identifier(it.id.text)
                        typeParam.ctx = createContext(fileName, it.IDENTIFIER().symbol)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }

                    is ShardScriptParser.FinTypeParamContext -> {
                        val typeParam = Identifier(it.id.text)
                        typeParam.ctx = createContext(fileName, it.FIN().symbol)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Fin))
                    }
                }
            }
        }

        val params: MutableList<Binder> = ArrayList()
        if (ctx.params != null) {
            ctx.params.paramDef().forEach {
                val id = Identifier(it.id.text)
                val of = typeVisitor.visit(it.of)
                params.add(Binder(id, of))
            }
        }

        val ret = if (ctx.ret != null) {
            typeVisitor.visit(ctx.ret)
        } else {
            Lang.unitId
        }
        val body = visit(ctx.body) as BlockAst

        val id = Identifier(ctx.id.text)
        id.ctx = createContext(fileName, ctx.id)

        val res = FunctionAst(id, typeParams, params, ret, body)
        res.ctx = createContext(fileName, ctx.id)
        return res
    }

    override fun visitLambdaDef(ctx: ShardScriptParser.LambdaDefContext): Ast {
        val params: MutableList<Binder> = ArrayList()
        if (ctx.params != null) {
            ctx.params.restrictedParamDef().forEach {
                val id = Identifier(it.id.text)
                val of = typeVisitor.visit(it.of)
                params.add(Binder(id, of))
            }
        }

        val body = visit(ctx.body)

        val res = LambdaAst(params, body)
        res.ctx = createContext(fileName, ctx.op)
        return res
    }

    override fun visitApplyExpr(ctx: ShardScriptParser.ApplyExprContext): Ast {
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val id = Identifier(ctx.id.text)
        val res = GroundApplyAst(id, args)
        res.ctx = createContext(fileName, ctx.id)
        return res
    }

    override fun visitParamApplyExpr(ctx: ShardScriptParser.ParamApplyExprContext): Ast {
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val typeArgs = ctx.params.restrictedTypeExprOrLiteral().map {
            typeVisitor.visit(it)
        }

        val id = ParameterizedSignifier(Identifier(ctx.id.text), typeArgs)
        val res = GroundApplyAst(id, args)
        res.ctx = createContext(fileName, ctx.id)
        return res
    }

    override fun visitObjectDefStat(ctx: ShardScriptParser.ObjectDefStatContext): Ast {
        val id = Identifier(ctx.id.text)
        val res = ObjectDefinitionAst(id)
        res.ctx = createContext(fileName, ctx.id)
        return res
    }

    override fun visitRecordDefStat(ctx: ShardScriptParser.RecordDefStatContext): Ast {
        val typeParams: MutableList<TypeParameterDefinition> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is ShardScriptParser.IdentifierTypeParamContext -> {
                        val typeParam = Identifier(it.id.text)
                        typeParam.ctx = createContext(fileName, it.IDENTIFIER().symbol)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }
                    is ShardScriptParser.FinTypeParamContext -> {
                        val typeParam = Identifier(it.id.text)
                        typeParam.ctx = createContext(fileName, it.FIN().symbol)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }
                }
            }
        }

        val fields: MutableList<FieldDef> = ArrayList()
        ctx.fields.fieldDef().forEach {
            when (it) {
                is ShardScriptParser.ImmutableFieldContext -> {
                    val id = Identifier(it.id.text)
                    val of = typeVisitor.visit(it.of)
                    val mutable = false
                    fields.add(FieldDef(id, of, mutable))
                }
                is ShardScriptParser.MutableFieldContext -> {
                    val id = Identifier(it.id.text)
                    val of = typeVisitor.visit(it.of)
                    val mutable = true
                    fields.add(FieldDef(id, of, mutable))
                }
            }
        }

        val id = Identifier(ctx.id.text)
        val res = RecordDefinitionAst(
            id,
            typeParams,
            fields
        )
        res.ctx = createContext(fileName, ctx.id)
        return res
    }

    override fun visitDotExpr(ctx: ShardScriptParser.DotExprContext): Ast {
        val lhs = visit(ctx.left)
        val id = Identifier(ctx.id.text)
        val res = DotAst(lhs, id)
        res.ctx = createContext(fileName, ctx.id)
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

        val typeArgs = ctx.params.restrictedTypeExprOrLiteral().map {
            typeVisitor.visit(it)
        }

        val id = ParameterizedSignifier(Identifier(ctx.id.text), typeArgs)
        val res = DotApplyAst(lhs, id, args)
        res.ctx = createContext(fileName, ctx.id)
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

        val id = Identifier(ctx.id.text)
        val res = DotApplyAst(lhs, id, args)
        res.ctx = createContext(fileName, ctx.id)
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
        val identifier = Identifier(ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral()
        }

        val body = visit(ctx.body)

        val res = ForEachAst(identifier, of, right, body)
        res.ctx = createContext(fileName, ctx.id)
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
                val gid = Identifier(lhs.id.text)
                return DotAssignAst(childLeft, gid, rhs)
            }
            is ShardScriptParser.RefExprContext -> {
                val gid = Identifier(lhs.id.text)
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
}
