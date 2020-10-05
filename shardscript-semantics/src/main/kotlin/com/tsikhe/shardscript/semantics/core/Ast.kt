package com.tsikhe.shardscript.semantics.core

import java.math.BigDecimal

sealed class Ast : LanguageElement {
    private var type: Symbol? = null

    fun readType(): Symbol = type!!

    fun assignType(errors: LanguageErrors, symbol: Symbol) {
        type = filterValidTypes(ctx, errors, symbol)
    }

    override var ctx: SourceContext = NotInSource
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

data class SByteLiteralAst(val canonicalForm: Byte) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ShortLiteralAst(val canonicalForm: Short) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IntLiteralAst(val canonicalForm: Int) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LongLiteralAst(val canonicalForm: Long) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class ByteLiteralAst(val canonicalForm: UByte) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class UShortLiteralAst(val canonicalForm: UShort) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class UIntLiteralAst(val canonicalForm: UInt) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class ULongLiteralAst(val canonicalForm: ULong) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DecimalLiteralAst(val canonicalForm: BigDecimal) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BooleanLiteralAst(val canonicalForm: Boolean) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class CharLiteralAst(val canonicalForm: Char) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringLiteralAst(val canonicalForm: String) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class StringInterpolationAst(val components: List<Ast>) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class LetAst(
    val gid: GroundIdentifier,
    val ofType: Identifier,
    val rhs: Ast,
    val mutable: Boolean
) : SymbolRefAst() {
    lateinit var ofTypeSymbol: Symbol

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RefAst(val gid: GroundIdentifier) : SymbolRefAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FileAst(val lines: List<Ast>) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class BlockAst(val lines: MutableList<Ast>) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FunctionAst(
    val gid: GroundIdentifier,
    val typeParams: List<GroundIdentifier>,
    val formalParams: List<Binder>,
    val returnType: Identifier,
    val body: BlockAst
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class RecordDefinitionAst(
    val gid: GroundIdentifier,
    val typeParams: List<GroundIdentifier>,
    val fields: List<FieldDef>
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ObjectDefinitionAst(
    val gid: GroundIdentifier
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class EnumDefinitionAst(
    val gid: GroundIdentifier,
    val typeParams: List<GroundIdentifier>,
    val records: List<RecordDefinitionAst>,
    val objects: List<ObjectDefinitionAst>
) : DefinitionAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAst(
    val lhs: Ast,
    val gid: GroundIdentifier
) : SymbolRefAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class GroundApplyAst(
    val identifier: Identifier,
    override val args: List<Ast>
) : ApplyAst() {
    lateinit var tti: TerminalTextIdentifier

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotApplyAst(
    val lhs: Ast,
    val identifier: Identifier,
    override val args: List<Ast>
) : ApplyAst() {
    lateinit var tti: TerminalTextIdentifier

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class ForEachAst(
    val gid: GroundIdentifier,
    val ofType: Identifier,
    val source: Ast,
    val body: Ast
) : Ast() {
    lateinit var ofTypeSymbol: Symbol
    lateinit var sourceTypeSymbol: Symbol
    lateinit var sourceOmicronSymbol: Symbol

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class MapAst(
    val gid: GroundIdentifier,
    val ofType: Identifier,
    val source: Ast,
    val body: Ast
) : Ast() {
    lateinit var ofTypeSymbol: Symbol
    lateinit var sourceTypeSymbol: Symbol
    lateinit var sourceOmicronSymbol: Symbol
    lateinit var bodyTypeSymbol: Symbol

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class FlatMapAst(
    val gid: GroundIdentifier,
    val ofType: Identifier,
    val source: Ast,
    val body: Ast
) : Ast() {
    lateinit var ofTypeSymbol: Symbol
    lateinit var sourceTypeSymbol: Symbol
    lateinit var sourceOmicronSymbol: Symbol
    lateinit var bodyTypeSymbol: Symbol

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AssignAst(
    val gid: GroundIdentifier,
    val rhs: Ast
) : SymbolRefAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class DotAssignAst(
    val lhs: Ast,
    val gid: GroundIdentifier,
    val rhs: Ast
) : SymbolRefAst() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IfAst(
    val condition: Ast,
    val trueBranch: Ast,
    val falseBranch: Ast
) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class SwitchAst(
    val source: Ast,
    val cases: List<CaseBranch>
) : Ast() {
    lateinit var sourceTypeSymbol: Symbol

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class AsAst(
    val lhs: Ast,
    val identifier: Identifier
) : Ast() {
    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}

data class IsAst(
    val lhs: Ast,
    val identifier: Identifier
) : Ast() {
    lateinit var result: BooleanValue
    lateinit var identifierSymbol: Symbol

    override fun <R> accept(visitor: AstVisitor<R>): R =
        visitor.visit(this)

    override fun <P, R> accept(visitor: ParameterizedAstVisitor<P, R>, param: P): R =
        visitor.visit(this, param)
}