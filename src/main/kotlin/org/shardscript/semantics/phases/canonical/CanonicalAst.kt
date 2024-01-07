package org.shardscript.semantics.phases.canonical

import org.shardscript.semantics.core.*
import java.math.BigDecimal

sealed class CanonicalAst(override val ctx: SourceContext) : LanguageElement {
    abstract val localScope: LocalCanonicalScope
    abstract fun <R> accept(visitor: CanonicalAstVisitor<R>): R
    abstract fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R
}

sealed class SymbolRefCanonicalAst(override val ctx: SourceContext) : CanonicalAst(ctx)

sealed class DefinitionCanonicalAst(override val ctx: SourceContext) : CanonicalAst(ctx)

sealed class ApplyCanonicalAst(override val ctx: SourceContext) : SymbolRefCanonicalAst(ctx) {
    abstract val args: List<CanonicalAst>
}

data class NumberLiteralCanonicalAst(override val ctx: SourceContext, override val localScope: LocalCanonicalScope, val canonicalForm: BigDecimal) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BooleanLiteralCanonicalAst(override val ctx: SourceContext, override val localScope: LocalCanonicalScope, val canonicalForm: Boolean) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringLiteralCanonicalAst(override val ctx: SourceContext, override val localScope: LocalCanonicalScope, val canonicalForm: String) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringInterpolationCanonicalAst(override val ctx: SourceContext, override val localScope: LocalCanonicalScope, val components: List<CanonicalAst>) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LetCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val identifier: CanonicalIdentifier,
    val ofType: CanonicalSignifier,
    val rhs: CanonicalAst,
    val mutable: Boolean
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RefCanonicalAst(override val ctx: SourceContext, override val localScope: LocalCanonicalScope, val identifier: CanonicalIdentifier) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ExactRefCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val identifier: CanonicalIdentifier,
    val path: List<CanonicalIdentifier>
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FileCanonicalAst(override val ctx: SourceContext, override val localScope: LocalCanonicalScope, val lines: List<CanonicalAst>) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BlockCanonicalAst(override val ctx: SourceContext, override val localScope: LocalCanonicalScope, val blockScope: LocalCanonicalScope, val lines: List<CanonicalAst>) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FunctionCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val bodyScope: LocalCanonicalScope,
    val identifier: CanonicalIdentifier,
    val typeParams: List<TypeParameterDefinition>,
    val formalParams: List<Binder>,
    val returnType: CanonicalSignifier,
    val body: BlockCanonicalAst
) : DefinitionCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LambdaCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val bodyScope: LocalCanonicalScope,
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
    override val localScope: LocalCanonicalScope,
    val bodyScope: LocalCanonicalScope,
    val identifier: CanonicalIdentifier,
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
    override val localScope: LocalCanonicalScope,
    val identifier: CanonicalIdentifier
) : DefinitionCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val lhs: CanonicalAst,
    val identifier: CanonicalIdentifier
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class GroundApplyCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val signifier: CanonicalSignifier,
    override val args: List<CanonicalAst>
) : ApplyCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotApplyCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val lhs: CanonicalAst,
    val signifier: CanonicalSignifier,
    override val args: List<CanonicalAst>
) : ApplyCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ForEachCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val bodyScope: LocalCanonicalScope,
    val identifier: CanonicalIdentifier,
    val ofType: CanonicalSignifier,
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
    override val localScope: LocalCanonicalScope,
    val identifier: CanonicalIdentifier,
    val rhs: CanonicalAst
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAssignCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val lhs: CanonicalAst,
    val identifier: CanonicalIdentifier,
    val rhs: CanonicalAst
) : SymbolRefCanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IfCanonicalAst(
    override val ctx: SourceContext,
    override val localScope: LocalCanonicalScope,
    val trueBranchScope: LocalCanonicalScope,
    val falseBranchScope: LocalCanonicalScope,
    val condition: CanonicalAst,
    val trueBranch: CanonicalAst,
    val falseBranch: CanonicalAst
) : CanonicalAst(ctx) {
    override fun <R> accept(visitor: CanonicalAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedCanonicalAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}