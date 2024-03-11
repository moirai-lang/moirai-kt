package moirai.composition

import moirai.grammar.MoiraiParser
import moirai.grammar.MoiraiParserBaseVisitor
import moirai.semantics.core.*
import moirai.semantics.prelude.*
import java.math.BigDecimal

internal class AstParseTreeVisitor(private val fileName: String, val errors: LanguageErrors) :
    MoiraiParserBaseVisitor<Ast>() {
    private val typeVisitor = TypeLiteralParseTreeVisitor(fileName)

    override fun visitFile(ctx: MoiraiParser.FileContext): Ast {
        val stats = ctx.stat().map { visit(it) }

        val res = FileAst(createContext(fileName, ctx.start), stats)
        return res
    }

    override fun visitBlock(ctx: MoiraiParser.BlockContext): Ast {
        val stats = ctx.stat().map { visit(it) }

        val res = BlockAst(createContext(fileName, ctx.start), stats.toMutableList())
        return res
    }

    override fun visitBlockExpr(ctx: MoiraiParser.BlockExprContext): Ast {
        val stats = ctx.stat().map { visit(it) }

        val res = BlockAst(createContext(fileName, ctx.start), stats.toMutableList())
        return res
    }

    override fun visitMutableLet(ctx: MoiraiParser.MutableLetContext): Ast {
        val right = visit(ctx.right)
        val identifier = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral(NotInSource)
        }

        val res = LetAst(createContext(fileName, ctx.id), identifier, of, right, true)
        return res
    }

    override fun visitImmutableLet(ctx: MoiraiParser.ImmutableLetContext): Ast {
        val right = visit(ctx.right)
        val identifier = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral(NotInSource)
        }

        val res = LetAst(createContext(fileName, ctx.id), identifier, of, right, false)
        return res
    }

    override fun visitRefExpr(ctx: MoiraiParser.RefExprContext): Ast {
        val identifier = Identifier(createContext(fileName, ctx.start), ctx.text)

        val res = RefAst(createContext(fileName, ctx.start), identifier)
        return res
    }

    override fun visitParenExpr(ctx: MoiraiParser.ParenExprContext): Ast {
        return visit(ctx.inner)
    }

    override fun visitUnaryNot(ctx: MoiraiParser.UnaryNotContext): Ast {
        val op = UnaryOperator.Not
        val right = visit(ctx.right)
        val args: List<Ast> = listOf()

        val res = DotApplyAst(createContext(fileName, ctx.op), right, Identifier(NotInSource, op.idStr), args)
        return res
    }

    override fun visitUnaryNegate(ctx: MoiraiParser.UnaryNegateContext): Ast {
        val op = UnaryOperator.Negate
        val right = visit(ctx.right)
        val args: List<Ast> = listOf()

        val res = DotApplyAst(createContext(fileName, ctx.op), right, Identifier(NotInSource, op.idStr), args)
        return res
    }

    override fun visitInfixMulDivMod(ctx: MoiraiParser.InfixMulDivModContext): Ast {
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

    override fun visitInfixAddSub(ctx: MoiraiParser.InfixAddSubContext): Ast {
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

    override fun visitInfixOrder(ctx: MoiraiParser.InfixOrderContext): Ast {
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

    override fun visitInfixEquality(ctx: MoiraiParser.InfixEqualityContext): Ast {
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

    override fun visitInfixAnd(ctx: MoiraiParser.InfixAndContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val op = BinaryOperator.And

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitInfixOr(ctx: MoiraiParser.InfixOrContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)
        val op = BinaryOperator.Or

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), op, sourceContext)
    }

    override fun visitLiteralInt(ctx: MoiraiParser.LiteralIntContext): Ast {
        val sourceContext = createContext(fileName, ctx.value)
        val res = try {
            IntLiteralAst(sourceContext, ctx.value.text.toInt())
        } catch (_: Exception) {
            errors.add(sourceContext, InvalidIntegerLiteral(Lang.intId.name, ctx.value.text))
            IntLiteralAst(sourceContext, 0)
        }
        return res
    }

    override fun visitLiteralBool(ctx: MoiraiParser.LiteralBoolContext): Ast {
        val res = BooleanLiteralAst(createContext(fileName, ctx.value), ctx.value.text!!.toBoolean())
        return res
    }

    override fun visitLiteralDecimal(ctx: MoiraiParser.LiteralDecimalContext): Ast {
        val bigDecimal = BigDecimal(ctx.value.text)

        val res = DecimalLiteralAst(createContext(fileName, ctx.value), bigDecimal)
        return res
    }

    override fun visitLiteralChar(ctx: MoiraiParser.LiteralCharContext): Ast {
        val char = resurrectChar(ctx.value.text)

        val res = CharLiteralAst(createContext(fileName, ctx.value), char)
        return res
    }

    override fun visitNonEmptyString(ctx: MoiraiParser.NonEmptyStringContext): Ast {
        val parts = ctx.parts.children.map { visit(it) }

        return if (parts.size == 1 && parts.first() is StringLiteralAst) {
            parts.first()
        } else {
            val res = StringInterpolationAst(createContext(fileName, ctx.start), parts.toMutableList())
            res
        }
    }

    override fun visitEmptyString(ctx: MoiraiParser.EmptyStringContext): Ast {
        val res = StringLiteralAst(createContext(fileName, ctx.start), "")
        return res
    }

    override fun visitStringChars(ctx: MoiraiParser.StringCharsContext): Ast {
        val chars = ctx.chars
        val res = StringLiteralAst(createContext(fileName, ctx.chars), resurrectString(chars.text))
        return res
    }

    override fun visitStringInterp(ctx: MoiraiParser.StringInterpContext): Ast {
        return visit(ctx.interp)
    }

    override fun visitStringExpr(ctx: MoiraiParser.StringExprContext): Ast {
        return visit(ctx.value)
    }

    override fun visitFunDefStat(ctx: MoiraiParser.FunDefStatContext): Ast {
        val typeParams: MutableList<TypeParameterDefinition> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is MoiraiParser.IdentifierTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.IDENTIFIER().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }

                    is MoiraiParser.FinTypeParamContext -> {
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
        val body = visit(ctx.body) as BlockAst
        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = FunctionAst(createContext(fileName, ctx.id), id, typeParams, params, ret, body)
        return res
    }

    override fun visitPluginFunDefStat(ctx: MoiraiParser.PluginFunDefStatContext): Ast {
        langThrow(createContext(fileName, ctx.start), InvalidPluginLocation(SignifierErrorString(ctx.id.text)))
    }

    override fun visitLambdaDef(ctx: MoiraiParser.LambdaDefContext): Ast {
        val params: MutableList<Binder> = ArrayList()
        if (ctx.params != null) {
            ctx.params.restrictedParamDef().forEach {
                val id = Identifier(createContext(fileName, it.id), it.id.text)
                val of = typeVisitor.visit(it.of)
                params.add(Binder(id, of))
            }
        }

        val body = visit(ctx.body)

        val res = LambdaAst(createContext(fileName, ctx.op), params, body)
        return res
    }

    override fun visitApplyExpr(ctx: MoiraiParser.ApplyExprContext): Ast {
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = GroundApplyAst(createContext(fileName, ctx.id), id, args)
        return res
    }

    override fun visitParamApplyExpr(ctx: MoiraiParser.ParamApplyExprContext): Ast {
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val typeArgs = ctx.params.restrictedTypeExprOrLiteral().map {
            typeVisitor.visit(it)
        }

        val id = ParameterizedSignifier(
            createContext(fileName, ctx.id),
            Identifier(
                createContext(fileName, ctx.id),
                ctx.id.text
            ),
            typeArgs
        )
        val res = GroundApplyAst(createContext(fileName, ctx.id), id, args)
        return res
    }

    override fun visitObjectDefStat(ctx: MoiraiParser.ObjectDefStatContext): Ast {
        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = ObjectDefinitionAst(createContext(fileName, ctx.id), id)
        return res
    }

    override fun visitRecordDefStat(ctx: MoiraiParser.RecordDefStatContext): Ast {
        val typeParams: MutableList<TypeParameterDefinition> = ArrayList()
        if (ctx.tp != null) {
            ctx.tp.typeParam().forEach {
                when (it) {
                    is MoiraiParser.IdentifierTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.IDENTIFIER().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }

                    is MoiraiParser.FinTypeParamContext -> {
                        val typeParam = Identifier(createContext(fileName, it.FIN().symbol), it.id.text)
                        typeParams.add(TypeParameterDefinition(typeParam, TypeParameterKind.Type))
                    }
                }
            }
        }

        val fields: MutableList<FieldDef> = ArrayList()
        ctx.fields.fieldDef().forEach {
            when (it) {
                is MoiraiParser.ImmutableFieldContext -> {
                    val id = Identifier(createContext(fileName, it.id), it.id.text)
                    val of = typeVisitor.visit(it.of)
                    val mutable = false
                    fields.add(FieldDef(id, of, mutable))
                }

                is MoiraiParser.MutableFieldContext -> {
                    val id = Identifier(createContext(fileName, it.id), it.id.text)
                    val of = typeVisitor.visit(it.of)
                    val mutable = true
                    fields.add(FieldDef(id, of, mutable))
                }
            }
        }

        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = RecordDefinitionAst(
            createContext(fileName, ctx.id),
            id,
            typeParams,
            fields
        )
        return res
    }

    override fun visitDotExpr(ctx: MoiraiParser.DotExprContext): Ast {
        val lhs = visit(ctx.left)
        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = DotAst(createContext(fileName, ctx.id), lhs, id)
        return res
    }

    override fun visitParamDotApply(ctx: MoiraiParser.ParamDotApplyContext): Ast {
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

        val id = ParameterizedSignifier(
            createContext(fileName, ctx.id),
            Identifier(
                createContext(fileName, ctx.id),
                ctx.id.text
            ),
            typeArgs
        )
        val res = DotApplyAst(createContext(fileName, ctx.id), lhs, id, args)
        return res
    }

    override fun visitDotApply(ctx: MoiraiParser.DotApplyContext): Ast {
        val lhs = visit(ctx.left)
        val args: MutableList<Ast> = ArrayList()
        if (ctx.args != null) {
            ctx.args.expr().forEach {
                args.add(visit(it))
            }
        }

        val id = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val res = DotApplyAst(createContext(fileName, ctx.id), lhs, id, args)
        return res
    }

    override fun visitIndexExpr(ctx: MoiraiParser.IndexExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.start)
        val method = CollectionMethods.IndexLookup

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsDotApply(left, listOf(right), method, sourceContext)
    }

    override fun visitForStat(ctx: MoiraiParser.ForStatContext): Ast {
        val right = visit(ctx.source)
        val identifier = Identifier(createContext(fileName, ctx.id), ctx.id.text)
        val of = if (ctx.of != null) {
            typeVisitor.visit(ctx.of)
        } else {
            ImplicitTypeLiteral(NotInSource)
        }

        val body = visit(ctx.body)

        val res = ForEachAst(createContext(fileName, ctx.id), identifier, of, right, body)
        return res
    }

    override fun visitAssignStat(ctx: MoiraiParser.AssignStatContext): Ast {
        val rhs = visit(ctx.right)
        val sourceContext = createContext(fileName, ctx.start)
        return when (val lhs = ctx.left) {
            is MoiraiParser.IndexExprContext -> {
                val op = CollectionMethods.IndexAssign

                val childLeft = visit(lhs.left)
                val childIndex = visit(lhs.right)
                return rewriteAsDotApply(childLeft, listOf(childIndex, rhs), op, sourceContext)
            }

            is MoiraiParser.DotExprContext -> {
                val childLeft = visit(lhs.left)
                val gid = Identifier(createContext(fileName, lhs.id), lhs.id.text)
                return DotAssignAst(createContext(fileName, lhs.DOT().symbol), childLeft, gid, rhs)
            }

            is MoiraiParser.RefExprContext -> {
                val gid = Identifier(createContext(fileName, lhs.id), lhs.id.text)
                return AssignAst(createContext(fileName, lhs.id), gid, rhs)
            }

            else -> {
                val lhsCtx = createContext(fileName, ctx.left.start)
                errors.add(lhsCtx, InvalidAssign)
                rhs
            }
        }
    }

    override fun visitToExpr(ctx: MoiraiParser.ToExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val left = visit(ctx.left)
        val right = visit(ctx.right)
        return rewriteAsGroundApply(listOf(left, right), Lang.pairId, sourceContext)
    }

    override fun visitAnyIf(ctx: MoiraiParser.AnyIfContext): Ast {
        return visit(ctx.anyif)
    }

    override fun visitIfElseIfExpr(ctx: MoiraiParser.IfElseIfExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = visit(ctx.trueb)
        val falseBranch = visit(ctx.elif)
        val res = IfAst(sourceContext, condition, trueBranch, falseBranch)
        return res
    }

    override fun visitIfElseExpr(ctx: MoiraiParser.IfElseExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = visit(ctx.trueb)
        val falseBranch = visit(ctx.falseb)
        val res = IfAst(sourceContext, condition, trueBranch, falseBranch)
        return res
    }

    override fun visitStandaloneIfExpr(ctx: MoiraiParser.StandaloneIfExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val trueBranch = visit(ctx.trueb)
        val falseBranch = BlockAst(NotInSource, mutableListOf(RefAst(NotInSource, Lang.unitId)))
        val res = IfAst(sourceContext, condition, trueBranch, falseBranch)
        return res
    }

    override fun visitAnyMatch(ctx: MoiraiParser.AnyMatchContext): Ast {
        return visit(ctx.anymatch)
    }

    override fun visitMatchExpr(ctx: MoiraiParser.MatchExprContext): Ast {
        val sourceContext = createContext(fileName, ctx.op)

        val condition = visit(ctx.condition)
        val cases: MutableList<CaseBlock> = mutableListOf()
        ctx.cases.caseStat().forEach { caseStatCtx ->
            val id = Identifier(createContext(fileName, caseStatCtx.id), caseStatCtx.id.text)
            val stats = caseStatCtx.stat().map { visit(it) }
            val block = BlockAst(createContext(fileName, caseStatCtx.LCURLY().symbol), stats.toMutableList())
            cases.add(CaseBlock(id, block))
        }

        val res = MatchAst(sourceContext, condition, cases)
        return res
    }
}
