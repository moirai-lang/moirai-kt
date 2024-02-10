package org.shardscript.semantics.core

import org.shardscript.semantics.infer.SubstitutionChain

sealed interface Type

sealed interface TerminusType: Type, RawTerminus

data object ErrorType : Type

class FunctionType(
    val formalParamTypes: List<Type>,
    val returnType: Type
) : Type

sealed class TypeParameter : Type

class StandardTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), Type

class FinTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class Fin(val magnitude: Long) : CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data object ConstantFin : CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class SumCostExpression(val children: List<CostExpression>) : CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ProductCostExpression(val children: List<CostExpression>) : CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class MaxCostExpression(val children: List<CostExpression>) : CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class TypeInstantiation(
    val substitutionChain: SubstitutionChain<TerminusType>
) : Type

class PlatformObjectType(
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Type, Scope by symbolTable

class ObjectType(
    val qualifiedName: String,
    val identifier: Identifier,
    val featureSupport: FeatureSupport
) : Type

class GroundRecordType(
    definitionScopeForTypeChecking: Scope,
    val qualifiedName: String,
    val identifier: Identifier,
    private val symbolTable: SymbolTable = SymbolTable(definitionScopeForTypeChecking)
) : Type, Scope by symbolTable {
    lateinit var fields: List<FieldSymbol>
}

class ParameterizedRecordType(
    definitionScopeForTypeChecking: Scope,
    val qualifiedName: String,
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(definitionScopeForTypeChecking)
) : TerminusType, Scope by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<FieldSymbol>
}

class BasicType(
    val identifier: Identifier,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Type, Scope by symbolTable

class ParameterizedBasicType(
    val identifier: Identifier,
    val instantiation: SingleTypeInstantiation<TerminusType, TypeInstantiation>,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : TerminusType, Scope by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<PlatformFieldSymbol>
}

class PlatformSumType(
    val identifier: Identifier,
    val featureSupport: FeatureSupport
) : TerminusType {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var memberTypes: List<PlatformSumMember>
}

sealed interface PlatformSumMember

class PlatformSumRecordType(
    definitionScopeForTypeChecking: Scope,
    val sumType: PlatformSumType,
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(definitionScopeForTypeChecking)
) : TerminusType, PlatformSumMember, Scope by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<FieldSymbol>
}

class PlatformSumObjectType(
    val sumType: PlatformSumType,
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Type, PlatformSumMember, Scope by symbolTable