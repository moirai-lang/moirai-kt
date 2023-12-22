package org.shardscript.semantics.phases.parse

import org.shardscript.semantics.core.*
import java.math.BigDecimal

sealed class PostParseAst(override val ctx: SourceContext) : LanguageElement {
    abstract fun <R> accept(visitor: PostParseAstVisitor<R>): R
    abstract fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R
}

sealed class SymbolRefPostParseAst(override val ctx: SourceContext) : PostParseAst(ctx)

sealed class DefinitionPostParseAst(override val ctx: SourceContext) : PostParseAst(ctx)

sealed class ApplyPostParseAst(override val ctx: SourceContext) : SymbolRefPostParseAst(ctx) {
    abstract val args: List<PostParseAst>
}

data class NumberLiteralPostParseAst(override val ctx: SourceContext, val canonicalForm: BigDecimal) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BooleanLiteralPostParseAst(override val ctx: SourceContext, val canonicalForm: Boolean) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringLiteralPostParseAst(override val ctx: SourceContext, val canonicalForm: String) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringInterpolationPostParseAst(override val ctx: SourceContext, val components: List<PostParseAst>) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LetPostParseAst(
    override val ctx: SourceContext,
    val identifier: PostParseIdentifier,
    val ofType: PostParseSignifier,
    val rhs: PostParseAst,
    val mutable: Boolean
) : SymbolRefPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RefPostParseAst(override val ctx: SourceContext, val identifier: PostParseIdentifier) : SymbolRefPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FilePostParseAst(override val ctx: SourceContext, val lines: List<PostParseAst>) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BlockPostParseAst(override val ctx: SourceContext, val lines: List<PostParseAst>) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FunctionPostParseAst(
    override val ctx: SourceContext,
    val identifier: PostParseIdentifier,
    val typeParams: List<TypeParameterDefinition>,
    val formalParams: List<Binder>,
    val returnType: PostParseSignifier,
    val body: BlockPostParseAst
) : DefinitionPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LambdaPostParseAst(
    override val ctx: SourceContext,
    val formalParams: List<Binder>,
    val body: PostParseAst
) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RecordDefinitionPostParseAst(
    override val ctx: SourceContext,
    val identifier: PostParseIdentifier,
    val typeParams: List<TypeParameterDefinition>,
    val fields: List<FieldDef>
) : DefinitionPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ObjectDefinitionPostParseAst(
    override val ctx: SourceContext,
    val identifier: PostParseIdentifier
) : DefinitionPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotPostParseAst(
    override val ctx: SourceContext,
    val lhs: PostParseAst,
    val identifier: PostParseIdentifier
) : SymbolRefPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class GroundApplyPostParseAst(
    override val ctx: SourceContext,
    val signifier: PostParseSignifier,
    override val args: List<PostParseAst>
) : ApplyPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotApplyPostParseAst(
    override val ctx: SourceContext,
    val lhs: PostParseAst,
    val signifier: PostParseSignifier,
    override val args: List<PostParseAst>
) : ApplyPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ForEachPostParseAst(
    override val ctx: SourceContext,
    val identifier: PostParseIdentifier,
    val ofType: PostParseSignifier,
    val source: PostParseAst,
    val body: PostParseAst
) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AssignPostParseAst(
    override val ctx: SourceContext,
    val identifier: PostParseIdentifier,
    val rhs: PostParseAst
) : SymbolRefPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAssignPostParseAst(
    override val ctx: SourceContext,
    val lhs: PostParseAst,
    val identifier: PostParseIdentifier,
    val rhs: PostParseAst
) : SymbolRefPostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IfPostParseAst(
    override val ctx: SourceContext,
    val condition: PostParseAst,
    val trueBranch: PostParseAst,
    val falseBranch: PostParseAst
) : PostParseAst(ctx) {
    override fun <R> accept(visitor: PostParseAstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedPostParseAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}