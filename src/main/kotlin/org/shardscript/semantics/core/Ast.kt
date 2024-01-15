package org.shardscript.semantics.core

import java.math.BigDecimal

sealed class Ast : LanguageElement {
    private var type: Type? = null

    fun readType(): Type = type!!

    fun assignType(errors: LanguageErrors, t: Type) {
        type = filterValidTypes(ctx, errors, t)
    }

    lateinit var scope: Scope<Symbol>
    lateinit var costExpression: CostExpression
    var approach: Boolean = true

    abstract fun <R> accept(visitor: AstVisitor<R>): R
    abstract fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R
}

sealed class SymbolRefAst : Ast() {
    lateinit var symbolRef: Symbol
}

sealed class DefinitionAst : Ast() {
    lateinit var definitionSpace: Scope<Symbol>
}

sealed class ApplyAst : SymbolRefAst() {
    abstract val args: List<Ast>
}

data class IntLiteralAst(override val ctx: SourceContext, val canonicalForm: Int) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DecimalLiteralAst(override val ctx: SourceContext, val canonicalForm: BigDecimal) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BooleanLiteralAst(override val ctx: SourceContext, val canonicalForm: Boolean) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class CharLiteralAst(override val ctx: SourceContext, val canonicalForm: Char) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringLiteralAst(override val ctx: SourceContext, val canonicalForm: String) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringInterpolationAst(override val ctx: SourceContext, val components: List<Ast>) : Ast() {
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
) : SymbolRefAst() {
    lateinit var ofTypeSymbol: Type

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RefAst(override val ctx: SourceContext, val identifier: Identifier) : SymbolRefAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FileAst(override val ctx: SourceContext, val lines: List<Ast>) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BlockAst(override val ctx: SourceContext, val lines: MutableList<Ast>) : Ast() {
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
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LambdaAst(
    override val ctx: SourceContext,
    val formalParams: List<Binder>,
    val body: Ast
) : Ast() {
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
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ObjectDefinitionAst(
    override val ctx: SourceContext,
    val identifier: Identifier
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val identifier: Identifier
) : SymbolRefAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class GroundApplyAst(
    override val ctx: SourceContext,
    val signifier: Signifier,
    override val args: List<Ast>
) : ApplyAst() {
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
) : ApplyAst() {
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
) : Ast() {
    lateinit var ofTypeSymbol: Type
    lateinit var sourceTypeSymbol: Type
    lateinit var sourceFinSymbol: Type

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AssignAst(
    override val ctx: SourceContext,
    val identifier: Identifier,
    val rhs: Ast
) : SymbolRefAst() {
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
) : SymbolRefAst() {
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
) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AsAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val signifier: Signifier
) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IsAst(
    override val ctx: SourceContext,
    val lhs: Ast,
    val signifier: Signifier
) : Ast() {
    lateinit var result: BooleanValue
    lateinit var identifierSymbol: Type

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}