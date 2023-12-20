package org.shardscript.semantics.phases.canonical

import org.shardscript.semantics.core.*
import java.math.BigDecimal

sealed class CanonicalAst(override val ctx: SourceContext) : LanguageElement {
    abstract fun <R> accept(visitor: CanonicalAstVisitor<R>): R
    abstract fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R
}

sealed class SymbolRefCanonicalAst(override val ctx: SourceContext) : CanonicalAst(ctx)

sealed class DefinitionCanonicalAst(override val ctx: SourceContext) : CanonicalAst(ctx)

sealed class ApplyCanonicalAst(override val ctx: SourceContext) : SymbolRefCanonicalAst(ctx) {
    abstract val args: List<CanonicalAst>
}

data class NumberLiteralCanonicalAst(override val ctx: SourceContext, val canonicalForm: BigDecimal) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BooleanLiteralCanonicalAst(override val ctx: SourceContext, val canonicalForm: Boolean) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringLiteralCanonicalAst(override val ctx: SourceContext, val canonicalForm: String) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringInterpolationCanonicalAst(override val ctx: SourceContext, val components: List<CanonicalAst>) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LetCanonicalAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val ofType: Signifier,
    val rhs: CanonicalAst,
    val mutable: Boolean
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RefCanonicalAst(override val ctx: SourceContext, val identifier: Identifier) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FileCanonicalAst(override val ctx: SourceContext, val lines: List<CanonicalAst>) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BlockCanonicalAst(override val ctx: SourceContext, val lines: MutableList<CanonicalAst>) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FunctionCanonicalAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val typeParams: List<TypeParameterDefinition>,
    val formalParams: List<Binder>,
    val returnType: Signifier,
    val body: BlockCanonicalAst
) : DefinitionCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LambdaCanonicalAst(
    override val ctx: SourceContext,
    val formalParams: List<Binder>,
    val body: CanonicalAst
) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RecordDefinitionCanonicalAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val typeParams: List<TypeParameterDefinition>,
    val fields: List<FieldDef>
) : DefinitionCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ObjectDefinitionCanonicalAst(
    override val ctx: SourceContext,
    val identifier: Identifier
) : DefinitionCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotCanonicalAst(
    override val ctx: SourceContext,
    val lhs: CanonicalAst,
    val identifier: Identifier
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class GroundApplyCanonicalAst(
    override val ctx: SourceContext,
    val signifier: Signifier,
    override val args: List<CanonicalAst>
) : ApplyCanonicalAst(ctx) {
    lateinit var tti: TerminalTextSignifier

    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotApplyCanonicalAst(
    override val ctx: SourceContext,
    val lhs: CanonicalAst,
    val signifier: Signifier,
    override val args: List<CanonicalAst>
) : ApplyCanonicalAst(ctx) {
    lateinit var tti: TerminalTextSignifier

    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ForEachCanonicalAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val ofType: Signifier,
    val source: CanonicalAst,
    val body: CanonicalAst
) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AssignCanonicalAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val rhs: CanonicalAst
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAssignCanonicalAst(
    override val ctx: SourceContext,
    val lhs: CanonicalAst,
    val identifier: Identifier,
    val rhs: CanonicalAst
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IfCanonicalAst(
    override val ctx: SourceContext,
    val condition: CanonicalAst,
    val trueBranch: CanonicalAst,
    val falseBranch: CanonicalAst
) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}