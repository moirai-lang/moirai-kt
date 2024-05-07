package moirai.semantics.core

import java.math.BigDecimal

internal sealed class Ast : LanguageElement {
    private var type: Type? = null

    fun readType(): Type = type!!

    fun assignType(errors: LanguageErrors, t: Type) {
        type = filterValidTypes(ctx, errors, t)
    }

    lateinit var scope: Scope
    lateinit var costExpression: CostExpression
    var approach: Boolean = true

    abstract fun <R> accept(visitor: AstVisitor<R>): R
    abstract fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R
}

internal sealed class DefinitionAst : Ast() {
    lateinit var definitionSpace: Scope
}

internal data class IntLiteralAst(override val ctx: SourceContext, val canonicalForm: Int) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class DecimalLiteralAst(override val ctx: SourceContext, val canonicalForm: BigDecimal) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class BooleanLiteralAst(override val ctx: SourceContext, val canonicalForm: Boolean) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class CharLiteralAst(override val ctx: SourceContext, val canonicalForm: Char) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class StringLiteralAst(override val ctx: SourceContext, val canonicalForm: String) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class StringInterpolationAst(override val ctx: SourceContext, val components: List<Ast>) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class LetAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val ofType: Signifier,
    val rhs: Ast,
    val mutable: Boolean
) : Ast() {
    lateinit var ofTypeSymbol: Type

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class RefAst(override val ctx: SourceContext, val identifier: Identifier) : Ast() {
    lateinit var refSlot: RefAstSymbolSlot

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class FileAst(override val ctx: SourceContext, val lines: List<Ast>) : Ast() {
    var cost: Long = 0

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class BlockAst(override val ctx: SourceContext, val lines: MutableList<Ast>) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class FunctionAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val typeParams: List<TypeParameterDefinition>,
    val formalParams: List<Binder>,
    val returnType: Signifier,
    val body: BlockAst
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class LambdaAst(
    override val ctx: SourceContext,
    val formalParams: List<Binder>,
    val body: Ast
) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class RecordDefinitionAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val typeParams: List<TypeParameterDefinition>,
    val fields: List<FieldDef>
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class ObjectDefinitionAst(
    override val ctx: SourceContext,
    val identifier: Identifier
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class DotAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val identifier: Identifier
) : Ast() {
    lateinit var dotSlot: DotAstSymbolSlot

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class GroundApplyAst(
    override val ctx: SourceContext,
    val signifier: Signifier,
    val args: List<Ast>
) : Ast() {
    lateinit var tti: TerminalTextSignifier
    lateinit var groundApplySlot: GroundApplyAstSymbolSlot

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class DotApplyAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val signifier: Signifier,
    val args: List<Ast>
) : Ast() {
    lateinit var tti: TerminalTextSignifier
    lateinit var dotApplySlot: DotApplyAstSymbolSlot

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class ForEachAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val ofType: Signifier,
    val source: Ast,
    val body: Ast
) : Ast() {
    lateinit var ofTypeSymbol: Type
    lateinit var sourceTypeSymbol: Type
    lateinit var sourceFinSymbol: Type

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class AssignAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val rhs: Ast
) : Ast() {
    lateinit var assignSlot: AssignAstSymbolSlot

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class DotAssignAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val identifier: Identifier,
    val rhs: Ast
) : Ast() {
    lateinit var dotAssignSlot: DotAssignAstSymbolSlot

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class IfAst(
    override val ctx: SourceContext,
    val condition: Ast,
    val trueBranch: Ast,
    val falseBranch: Ast
) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

internal data class MatchAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val condition: Ast,
    val cases: List<CaseBlock>
) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}