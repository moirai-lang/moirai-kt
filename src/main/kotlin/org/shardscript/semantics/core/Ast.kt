package org.shardscript.semantics.core

import java.math.BigDecimal

sealed class Ast(override val ctx: SourceContext) : LanguageElement {
    abstract fun <R> accept(visitor: AstVisitor<R>): R
    abstract fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R
}

sealed class SymbolRefAst(override val ctx: SourceContext) : Ast(ctx)

sealed class DefinitionAst(override val ctx: SourceContext) : Ast(ctx)

sealed class ApplyAst(override val ctx: SourceContext) : SymbolRefAst(ctx) {
    abstract val args: List<Ast>
}

data class IntLiteralAst(override val ctx: SourceContext, val canonicalForm: Int) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DecimalLiteralAst(override val ctx: SourceContext, val canonicalForm: BigDecimal) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BooleanLiteralAst(override val ctx: SourceContext, val canonicalForm: Boolean) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class CharLiteralAst(override val ctx: SourceContext, val canonicalForm: Char) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringLiteralAst(override val ctx: SourceContext, val canonicalForm: String) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringInterpolationAst(override val ctx: SourceContext, val components: List<Ast>) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LetAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val ofType: Signifier,
    val rhs: Ast,
    val mutable: Boolean
) : SymbolRefAst(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RefAst(override val ctx: SourceContext, val identifier: Identifier) : SymbolRefAst(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FileAst(override val ctx: SourceContext, val lines: List<Ast>) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BlockAst(override val ctx: SourceContext, val lines: MutableList<Ast>) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FunctionAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val typeParams: List<TypeParameterDefinition>,
    val formalParams: List<Binder>,
    val returnType: Signifier,
    val body: BlockAst
) : DefinitionAst(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LambdaAst(
    override val ctx: SourceContext,
    val formalParams: List<Binder>,
    val body: Ast
) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RecordDefinitionAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val typeParams: List<TypeParameterDefinition>,
    val fields: List<FieldDef>
) : DefinitionAst(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ObjectDefinitionAst(
    override val ctx: SourceContext,
    val identifier: Identifier
) : DefinitionAst(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val identifier: Identifier
) : SymbolRefAst(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class GroundApplyAst(
    override val ctx: SourceContext,
    val signifier: Signifier,
    override val args: List<Ast>
) : ApplyAst(ctx) {
    lateinit var tti: TerminalTextSignifier

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotApplyAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val signifier: Signifier,
    override val args: List<Ast>
) : ApplyAst(ctx) {
    lateinit var tti: TerminalTextSignifier

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ForEachAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val ofType: Signifier,
    val source: Ast,
    val body: Ast
) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AssignAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val rhs: Ast
) : SymbolRefAst(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAssignAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val identifier: Identifier,
    val rhs: Ast
) : SymbolRefAst(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IfAst(
    override val ctx: SourceContext,
    val condition: Ast,
    val trueBranch: Ast,
    val falseBranch: Ast
) : Ast(ctx) {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}