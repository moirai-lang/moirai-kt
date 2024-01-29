package org.shardscript.semantics.core

import org.shardscript.semantics.infer.SubstitutionChain

/**
 * Core Primitives
 */
sealed class Symbol

sealed interface Type

sealed class SymbolTableElement: Symbol() {
    abstract val parent: Scope<Symbol>
}

sealed class NamedSymbolTableElement: SymbolTableElement() {
    abstract val identifier: Identifier
}

sealed class NamedSymbolWithMembers(
    override val parent: Scope<Symbol>,
    private val symbolTable: SymbolTable = SymbolTable(parent)
): NamedSymbolTableElement(), Scope<Symbol> by symbolTable

data object ErrorSymbol : Symbol(), Type

data class Block(
    override val parent: Scope<Symbol>,
    private val symbolTable: SymbolTable = SymbolTable(parent)
) : SymbolTableElement(), Scope<Symbol> by symbolTable

data class LocalVariableSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type,
    val mutable: Boolean
) : NamedSymbolTableElement()

/**
 * Function Primitives
 */
data class FunctionTypeSymbol(
    val formalParamTypes: List<Type>,
    val returnType: Type
) : Symbol(), Type

data class FunctionFormalParameterSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type
) : NamedSymbolTableElement() {
    var costMultiplier: CostExpression = CommonCostExpressions.defaultMultiplier
}

/**
 * Type/Fin Primitives
 */
sealed class TypeParameter : Symbol(), Type

data class StandardTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), Type

data class ImmutableFinTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class MutableFinTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class FinTypeSymbol(val magnitude: Long) : Symbol(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data object ConstantFinTypeSymbol : Symbol(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class SumCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class ProductCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class MaxCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

/**
 * Generic Primitives
 */
sealed interface RawSymbol {
    val typeParams: List<TypeParameter>
}

data class SymbolInstantiation(
    val substitutionChain: SubstitutionChain
) : Symbol(), Type

/**
 * Function Types
 */
data class GroundFunctionSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val originalCtx: SourceContext,
    val body: Ast
) : NamedSymbolWithMembers(parent) {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

data class LambdaSymbol(
    override val parent: Scope<Symbol>,
    private val symbolTable: SymbolTable = SymbolTable(parent)
): SymbolTableElement(), Scope<Symbol> by symbolTable {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedFunctionSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val originalCtx: SourceContext,
    val body: Ast
) : NamedSymbolWithMembers(parent), RawSymbol {
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

/**
 * Data Type Primitives
 */

data class FieldSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type,
    val mutable: Boolean
) : NamedSymbolTableElement()

data class PlatformFieldSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: BasicTypeSymbol
) : NamedSymbolTableElement()

/**
 * Data Types
 */
data class PlatformObjectSymbol(
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Symbol(), Type, Scope<Symbol> by symbolTable

data class ObjectSymbol(
    val qualifiedName: String,
    val identifier: Identifier,
    val featureSupport: FeatureSupport
) : Symbol(), Type

data class GroundRecordTypeSymbol(
    override val parent: Scope<Symbol>,
    val qualifiedName: String,
    override val identifier: Identifier,
    val featureSupport: FeatureSupport
) : NamedSymbolWithMembers(parent), Type {
    lateinit var fields: List<FieldSymbol>
}

data class ParameterizedRecordTypeSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val featureSupport: FeatureSupport
) : NamedSymbolWithMembers(parent), RawSymbol, Type {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<FieldSymbol>
}

/**
 * Basic Types
 */
data class BasicTypeSymbol(
    val identifier: Identifier,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Symbol(), Type, Scope<Symbol> by symbolTable

data class ParameterizedBasicTypeSymbol(
    val identifier: Identifier,
    val instantiation: SingleTypeInstantiation,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Symbol(), RawSymbol, Type, Scope<Symbol> by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var modeSelector: (List<Type>) -> BasicTypeMode
    lateinit var fields: List<PlatformFieldSymbol>
}

/**
 * Plugins
 */
data class GroundMemberPluginSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier
) : NamedSymbolWithMembers(parent) {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedMemberPluginSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val instantiation: TwoTypeInstantiation
) : NamedSymbolWithMembers(parent), RawSymbol {
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedStaticPluginSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val instantiation: SingleTypeInstantiation,
) : NamedSymbolWithMembers(parent), RawSymbol {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}
