package org.shardscript.semantics.phases.parse

import org.shardscript.semantics.core.*
import java.math.BigDecimal

sealed class ScopedAst(override val ctx: SourceContext) : LanguageElement {
    abstract val localScope: LocalPostParseScope
    abstract fun <R> accept(visitor: ScopedAstVisitor<R>): R
    abstract fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R
}

sealed class SymbolRefScopedAst(override val ctx: SourceContext) : ScopedAst(ctx)

sealed class DefinitionScopedAst(override val ctx: SourceContext) : ScopedAst(ctx)

sealed class ApplyScopedAst(override val ctx: SourceContext) : SymbolRefScopedAst(ctx) {
    abstract val args: List<ScopedAst>
}

data class NumberLiteralScopedAst(override val ctx: SourceContext, override val localScope: LocalPostParseScope, val canonicalForm: BigDecimal) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BooleanLiteralScopedAst(override val ctx: SourceContext, override val localScope: LocalPostParseScope, val canonicalForm: Boolean) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringLiteralScopedAst(override val ctx: SourceContext, override val localScope: LocalPostParseScope, val canonicalForm: String) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringInterpolationScopedAst(override val ctx: SourceContext, override val localScope: LocalPostParseScope, val components: List<ScopedAst>) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LetScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val identifier: PostParseIdentifier,
    val ofType: PostParseSignifier,
    val rhs: ScopedAst,
    val mutable: Boolean
) : SymbolRefScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RefScopedAst(override val ctx: SourceContext, override val localScope: LocalPostParseScope, val identifier: PostParseIdentifier) : SymbolRefScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FileScopedAst(override val ctx: SourceContext, override val localScope: LocalPostParseScope, val lines: List<ScopedAst>) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BlockScopedAst(override val ctx: SourceContext, override val localScope: LocalPostParseScope, val blockScope: LocalPostParseScope, val lines: List<ScopedAst>) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FunctionScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val bodyScope: LocalPostParseScope,
    val identifier: PostParseIdentifier,
    val typeParams: List<TypeParameterDefinition>,
    val formalParams: List<Binder>,
    val returnType: PostParseSignifier,
    val body: BlockScopedAst
) : DefinitionScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LambdaScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val bodyScope: LocalPostParseScope,
    val formalParams: List<Binder>,
    val body: ScopedAst
) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RecordDefinitionScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val bodyScope: LocalPostParseScope,
    val identifier: PostParseIdentifier,
    val typeParams: List<TypeParameterDefinition>,
    val fields: List<FieldDef>
) : DefinitionScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ObjectDefinitionScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val identifier: PostParseIdentifier
) : DefinitionScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val lhs: ScopedAst,
    val identifier: PostParseIdentifier
) : SymbolRefScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class GroundApplyScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val signifier: PostParseSignifier,
    override val args: List<ScopedAst>
) : ApplyScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotApplyScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val lhs: ScopedAst,
    val signifier: PostParseSignifier,
    override val args: List<ScopedAst>
) : ApplyScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ForEachScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val bodyScope: LocalPostParseScope,
    val identifier: PostParseIdentifier,
    val ofType: PostParseSignifier,
    val source: ScopedAst,
    val body: ScopedAst
) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AssignScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val identifier: PostParseIdentifier,
    val rhs: ScopedAst
) : SymbolRefScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAssignScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val lhs: ScopedAst,
    val identifier: PostParseIdentifier,
    val rhs: ScopedAst
) : SymbolRefScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IfScopedAst(
    override val ctx: SourceContext,
    override val localScope: LocalPostParseScope,
    val trueBranchScope: LocalPostParseScope,
    val falseBranchScope: LocalPostParseScope,
    val condition: ScopedAst,
    val trueBranch: ScopedAst,
    val falseBranch: ScopedAst
) : ScopedAst(ctx) {
    override fun <R> accept(visitor: ScopedAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedScopedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}