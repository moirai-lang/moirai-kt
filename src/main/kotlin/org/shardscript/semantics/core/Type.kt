package org.shardscript.semantics.core

import org.shardscript.semantics.infer.SubstitutionChain

sealed interface Type

data object ErrorSymbol : Symbol(), Type

class FunctionTypeSymbol(
    val formalParamTypes: List<Type>,
    val returnType: Type
) : Symbol(), Type

sealed class TypeParameter : Symbol(), Type

class StandardTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), Type

class ImmutableFinTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class MutableFinTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class FinTypeSymbol(val magnitude: Long) : Symbol(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data object ConstantFinTypeSymbol : Symbol(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class SumCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ProductCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class MaxCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class SymbolInstantiation(
    val substitutionChain: SubstitutionChain
) : Symbol(), Type

class PlatformObjectSymbol(
    val identifier: Identifier,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Symbol(), Type, Scope by symbolTable

class ObjectSymbol(
    val qualifiedName: String,
    val identifier: Identifier,
    val featureSupport: FeatureSupport
) : Symbol(), Type

class GroundRecordTypeSymbol(
    definitionScopeForTypeChecking: Scope,
    val qualifiedName: String,
    val identifier: Identifier,
    private val symbolTable: SymbolTable = SymbolTable(definitionScopeForTypeChecking)
) : Symbol(), Type, Scope by symbolTable {
    lateinit var fields: List<FieldSymbol>
}

class ParameterizedRecordTypeSymbol(
    definitionScopeForTypeChecking: Scope,
    val qualifiedName: String,
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(definitionScopeForTypeChecking)
) : Symbol(), RawTerminus, Type, Scope by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<FieldSymbol>
}

class BasicTypeSymbol(
    val identifier: Identifier,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Symbol(), Type, Scope by symbolTable

class ParameterizedBasicTypeSymbol(
    val identifier: Identifier,
    val instantiation: SingleTypeInstantiation,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Symbol(), RawTerminus, Type, Scope by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var modeSelector: (List<Type>) -> BasicTypeMode
    lateinit var fields: List<PlatformFieldSymbol>
}

