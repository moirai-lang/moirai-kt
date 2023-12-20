package org.shardscript.composition

import org.shardscript.grammar.ShardScriptParser
import org.shardscript.grammar.ShardScriptParserBaseVisitor
import org.shardscript.semantics.core.*
import org.shardscript.semantics.phases.parse.*
import org.shardscript.semantics.prelude.*
import java.math.BigDecimal

internal class AstParseTreeVisitor(private val fileName: String, val errors: LanguageErrors) :
    ShardScriptParserBaseVisitor<PostParseAst>() {
    private val typeVisitor = TypeLiteralParseTreeVisitor(fileName)

    override fun visitFile(ctx: ShardScriptParser.FileContext): PostParseAst {
        val stats = ctx.stat().map { visit(it) }

        val res = FilePostParseAst(createContext(fileName, ctx.start), stats)
        return res
    }

    override fun visitBlock(ctx: ShardScriptParser.BlockContext): PostParseAst {
        val stats = ctx.stat().map { visit(it) }

        val res = BlockPostParseAst(createContext(fileName, ctx.start), stats.toMutableList())
        return res
    }

    override fun visitBlockExpr(ctx: ShardScriptParser.BlockExprContext): PostParseAst {
        val stats = ctx.stat().map { visit(it) }

        val res = BlockPostParseAst(createContext(fileName, ctx.start), stats.toMutableList())
        return res
    }

    override fun visitMutableLet(ctx: ShardScriptParser.MutableLetContext): PostParseAst {
        val right = visit(ctx.right)
        val identifier = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral(NotInSource)
        }

        val res = LetPostParseAst(createContext(fileName, ctx.MUTABLE().symbol), identifier, of, right, true)
        return res
    }

    override fun visitImmutableLet(ctx: ShardScriptParser.ImmutableLetContext): PostParseAst {
        val right = visit(ctx.right)
        val identifier = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral(NotInSource)
        }

        val res = LetPostParseAst(createContext(fileName, ctx.VAL().symbol), identifier, of, right, false)
        return res
    }

    override fun visitRefExpr(ctx: ShardScriptParser.RefExprContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.start)
        val identifier = Identifier(sourceContext, ctx.text)

        val res = RefPostParseAst(sourceContext, identifier)
        return res
    }

    override fun visitParenExpr(ctx: ShardScriptParser.ParenExprContext): PostParseAst {
        return visit(ctx.inner)
    }

    override fun visitUnaryNot(ctx: ShardScriptParser.UnaryNotContext): PostParseAst {
        val op = UnaryOperator.Not
        val right = visit(ctx.right)
        val args: List<PostParseAst> = listOf()

        val res = DotApplyPostParseAst(
            createContext(fileName, ctx.op),
            right,
            Identifier(createContext(fileName, ctx.right.start), op.idStr),
            args
        )
        return res
    }

    override fun visitUnaryNegate(ctx: ShardScriptParser.UnaryNegateContext): PostParseAst {
        val op = UnaryOperator.Negate
        val right = visit(ctx.right)
        val args: List<PostParseAst> = listOf()

        val res = DotApplyPostParseAst(createContext(fileName, ctx.op), right, Identifier(createContext(fileName, ctx.right.start), op.idStr), args)
        return res
    }

    override fun visitInfixMulDivMod(ctx: ShardScriptParser.InfixMulDivModContext): PostParseAst {
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

    override fun visitInfixAddSub(ctx: ShardScriptParser.InfixAddSubContext): PostParseAst {
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

    override fun visitInfixOrder(ctx: ShardScriptParser.InfixOrderContext): PostParseAst {
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

    override fun visitInfixEquality(ctx: ShardScriptParser.InfixEqualityContext): PostParseAst {
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

    override fun visitInfixAnd(ctx: ShardScriptParser.InfixAndContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.op)
        val op = BinaryOperator.And

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitInfixOr(ctx: ShardScriptParser.InfixOrContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.op)
        val op = BinaryOperator.Or

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitLiteralInt(ctx: ShardScriptParser.LiteralIntContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            val bigDecimal = BigDecimal(ctx.value.text)
            NumberLiteralPostParseAst(sourceContext, bigDecimal)
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidNumberLiteral(Lang.decimalId.name, ctx.value.text))
            NumberLiteralPostParseAst(sourceContext, BigDecimal.ZERO)
        }
        return res
    }

    override fun visitLiteralBool(ctx: ShardScriptParser.LiteralBoolContext): PostParseAst {
        val res = BooleanLiteralPostParseAst(createContext(fileName, ctx.value), ctx.value.text!!.toBoolean())
        return res
    }

    override fun visitLiteralDecimal(ctx: ShardScriptParser.LiteralDecimalContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            val bigDecimal = BigDecimal(ctx.value.text)
            NumberLiteralPostParseAst(sourceContext, bigDecimal)
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidNumberLiteral(Lang.decimalId.name, ctx.value.text))
            NumberLiteralPostParseAst(sourceContext, BigDecimal.ZERO)
        }
        return res
    }

    override fun visitNonEmptyString(ctx: ShardScriptParser.NonEmptyStringContext): PostParseAst {
        val parts = ctx.parts.children.map { visit(it) }

        return if (parts.size == 1 && parts.first() is StringLiteralPostParseAst) {
            parts.first()
        } else {
            val res = StringInterpolationPostParseAst(createContext(fileName, ctx.start), parts.toMutableList())
            res
        }
    }

    override fun visitEmptyString(ctx: ShardScriptParser.EmptyStringContext): PostParseAst {
        val res = StringLiteralPostParseAst(createContext(fileName, ctx.start), "")
        return res
    }

    override fun visitStringChars(ctx: ShardScriptParser.StringCharsContext): PostParseAst {
        val chars = ctx.chars
        val res = StringLiteralPostParseAst(createContext(fileName, ctx.chars), resurrectString(chars.text))
        return res
    }

    override fun visitStringInterp(ctx: ShardScriptParser.StringInterpContext): PostParseAst {
        return visit(ctx.interp)
    }

    override fun visitStringExpr(ctx: ShardScriptParser.StringExprContext): PostParseAst {
        return visit(ctx.value)
    }

    override fun visitFunDefStat(ctx: ShardScriptParser.FunDefStatContext): PostParseAst {
        val typeParams: MutableList<TypeParameterDefinition> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is ShardScriptParser.IdentifierTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.IDENTIFIER().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }

                    is ShardScriptParser.FinTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.FIN().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Fin))
                    }
                }
            }
        }

        val params: MutableList<Binder> = ArrayList()
        if (ctx.params != null) {
            ctx.params.paramDef().forEach {
                val id = Identifier(createContext(fileName, it.id), it.id.text)
                val of = typeVisitor.visit(it.of)
                params.add(Binder(id, of))
            }
        }

        val ret = if (ctx.ret != null) {
            typeVisitor.visit(ctx.ret)
        } else {
            Lang.unitId
        }
        val body = visit(ctx.body) as BlockPostParseAst

        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = FunctionPostParseAst(createContext(fileName, ctx.id), id, typeParams, params, ret, body)
        return res
    }

    override fun visitLambdaDef(ctx: ShardScriptParser.LambdaDefContext): PostParseAst {
        val params: MutableList<Binder> = ArrayList()
        if (ctx.params != null) {
            ctx.params.restrictedParamDef().forEach {
                val id = Identifier(createContext(fileName, it.id), it.id.text)
                val of = typeVisitor.visit(it.of)
                params.add(Binder(id, of))
            }
        }

        val body = visit(ctx.body)

        val res = LambdaPostParseAst(createContext(fileName, ctx.op), params, body)
        return res
    }

    override fun visitApplyExpr(ctx: ShardScriptParser.ApplyExprContext): PostParseAst {
        val args: MutableList<PostParseAst> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val sourceContext = createContext(fileName, ctx.id)
        val id = Identifier(sourceContext, ctx.id.text)
        val res = GroundApplyPostParseAst(sourceContext, id, args)
        return res
    }

    override fun visitParamApplyExpr(ctx: ShardScriptParser.ParamApplyExprContext): PostParseAst {
        val args: MutableList<PostParseAst> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val typeArgs = ctx.params.restrictedTypeExprOrLiteral().map {
            typeVisitor.visit(it)
        }

        val sourceContext = createContext(fileName, ctx.id)
        val id = ParameterizedSignifier(sourceContext, Identifier(sourceContext, ctx.id.text), typeArgs)
        val res = GroundApplyPostParseAst(sourceContext, id, args)
        return res
    }

    override fun visitObjectDefStat(ctx: ShardScriptParser.ObjectDefStatContext): PostParseAst {
        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = ObjectDefinitionPostParseAst(createContext(fileName, ctx.OBJECT().symbol), id)
        return res
    }

    override fun visitRecordDefStat(ctx: ShardScriptParser.RecordDefStatContext): PostParseAst {
        val typeParams: MutableList<TypeParameterDefinition> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is ShardScriptParser.IdentifierTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.IDENTIFIER().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }
                    is ShardScriptParser.FinTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.FIN().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }
                }
            }
        }

        val fields: MutableList<FieldDef> = ArrayList()
        ctx.fields.fieldDef().forEach {
            when (it) {
                is ShardScriptParser.ImmutableFieldContext -> {
                    val id = Identifier(createContext(fileName, it.id), it.id.text)
                    val of = typeVisitor.visit(it.of)
                    val mutable = false
                    fields.add(FieldDef(id, of, mutable))
                }
                is ShardScriptParser.MutableFieldContext -> {
                    val id = Identifier(createContext(fileName, it.id), it.id.text)
                    val of = typeVisitor.visit(it.of)
                    val mutable = true
                    fields.add(FieldDef(id, of, mutable))
                }
            }
        }

        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = RecordDefinitionPostParseAst(
            createContext(fileName, ctx.id),
            id,
            typeParams,
            fields
        )
        return res
    }

    override fun visitDotExpr(ctx: ShardScriptParser.DotExprContext): PostParseAst {
        val lhs = visit(ctx.left)
        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = DotPostParseAst(createContext(fileName, ctx.DOT().symbol), lhs, id)
        return res
    }

    override fun visitParamDotApply(ctx: ShardScriptParser.ParamDotApplyContext): PostParseAst {
        val lhs = visit(ctx.left)
        val args: MutableList<PostParseAst> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val typeArgs = ctx.params.restrictedTypeExprOrLiteral().map {
            typeVisitor.visit(it)
        }

        val id = ParameterizedSignifier(createContext(fileName, ctx.id), Identifier(createContext(fileName, ctx.id), ctx.id.text), typeArgs)
        val res = DotApplyPostParseAst(createContext(fileName, ctx.DOT().symbol), lhs, id, args)
        return res
    }

    override fun visitDotApply(ctx: ShardScriptParser.DotApplyContext): PostParseAst {
        val lhs = visit(ctx.left)
        val args: MutableList<PostParseAst> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = DotApplyPostParseAst(createContext(fileName, ctx.DOT().symbol), lhs, id, args)
        return res
    }

    override fun visitIndexExpr(ctx: ShardScriptParser.IndexExprContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.start)
        val method = CollectionMethods.IndexLookup

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), method, sourceContext)
    }

    override fun visitForStat(ctx: ShardScriptParser.ForStatContext): PostParseAst {
        val right = visit(ctx.source)
        val identifier = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral(NotInSource)
        }

        val body = visit(ctx.body)

        val res = ForEachPostParseAst(createContext(fileName, ctx.FOR().symbol), identifier, of, right, body)
        return res
    }

    override fun visitAssignStat(ctx: ShardScriptParser.AssignStatContext): PostParseAst {
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
                val gid = Identifier(createContext(fileName, lhs.id), lhs.id.text)
                return DotAssignPostParseAst(createContext(fileName, ctx.ASSIGN().symbol), childLeft, gid, rhs)
            }
            is ShardScriptParser.RefExprContext -> {
                val gid = Identifier(createContext(fileName, lhs.id), lhs.id.text)
                return AssignPostParseAst(createContext(fileName, ctx.ASSIGN().symbol), gid, rhs)
            }
            else -> {
                val lhsCtx = createContext(fileName, ctx.left.start)
                errors.add(lhsCtx, InvalidAssign)
                rhs
            }
        }
    }

    override fun visitAnyIf(ctx: ShardScriptParser.AnyIfContext): PostParseAst {
        return visit(ctx.anyif)
    }

    override fun visitIfElseIfExpr(ctx: ShardScriptParser.IfElseIfExprContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = visit(ctx.trueb)
        val falseBranch = visit(ctx.elif)
        val res = IfPostParseAst(sourceContext, condition, trueBranch, falseBranch)
        return res
    }

    override fun visitIfElseExpr(ctx: ShardScriptParser.IfElseExprContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = visit(ctx.trueb)
        val falseBranch = visit(ctx.falseb)
        val res = IfPostParseAst(sourceContext, condition, trueBranch, falseBranch)
        return res
    }

    override fun visitStandaloneIfExpr(ctx: ShardScriptParser.StandaloneIfExprContext): PostParseAst {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = BlockPostParseAst(NotInSource, mutableListOf(visit(ctx.trueb), RefPostParseAst(NotInSource, Lang.unitId)))
        val falseBranch = BlockPostParseAst(NotInSource, mutableListOf(RefPostParseAst(NotInSource, Lang.unitId)))
        val res = IfPostParseAst(sourceContext, condition, trueBranch, falseBranch)
        return res
    }
}
