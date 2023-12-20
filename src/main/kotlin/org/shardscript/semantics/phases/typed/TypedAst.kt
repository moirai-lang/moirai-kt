package org.shardscript.semantics.phases.typed

import org.shardscript.semantics.core.*
import java.math.BigDecimal

sealed class TypedAst(override val ctx: SourceContext) : LanguageElement {
    abstract fun <R> accept(visitor: TypedAstVisitor<R>): R
    abstract fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R
}

sealed class SymbolRefTypedAst(override val ctx: SourceContext) : TypedAst(ctx)

sealed class ApplyTypedAst(override val ctx: SourceContext) : SymbolRefTypedAst(ctx) {
    abstract val args: List<TypedAst>
}

data class NumberLiteralTypedAst(override val ctx: SourceContext, val canonicalForm: BigDecimal) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BooleanLiteralTypedAst(override val ctx: SourceContext, val canonicalForm: Boolean) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringLiteralTypedAst(override val ctx: SourceContext, val canonicalForm: String) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringInterpolationTypedAst(override val ctx: SourceContext, val components: List<TypedAst>) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LetTypedAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val ofType: Signifier,
    val rhs: TypedAst,
    val mutable: Boolean
) : SymbolRefTypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RefTypedAst(override val ctx: SourceContext, val identifier: Identifier) : SymbolRefTypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FileTypedAst(override val ctx: SourceContext, val lines: List<TypedAst>) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BlockTypedAst(override val ctx: SourceContext, val lines: MutableList<TypedAst>) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LambdaTypedAst(
    override val ctx: SourceContext,
    val formalParams: List<Binder>,
    val body: TypedAst
) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotTypedAst(
    override val ctx: SourceContext,
    val lhs: TypedAst,
    val identifier: Identifier
) : SymbolRefTypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class GroundApplyTypedAst(
    override val ctx: SourceContext,
    val signifier: Signifier,
    override val args: List<TypedAst>
) : ApplyTypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotApplyTypedAst(
    override val ctx: SourceContext,
    val lhs: TypedAst,
    val signifier: Signifier,
    override val args: List<TypedAst>
) : ApplyTypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ForEachTypedAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val ofType: Signifier,
    val source: TypedAst,
    val body: TypedAst
) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AssignTypedAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val rhs: TypedAst
) : SymbolRefTypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAssignTypedAst(
    override val ctx: SourceContext,
    val lhs: TypedAst,
    val identifier: Identifier,
    val rhs: TypedAst
) : SymbolRefTypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IfTypedAst(
    override val ctx: SourceContext,
    val condition: TypedAst,
    val trueBranch: TypedAst,
    val falseBranch: TypedAst
) : TypedAst(ctx) {
    override fun <R> accept(visitor: TypedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedTypedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}