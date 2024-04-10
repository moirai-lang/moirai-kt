package moirai.semantics.core

import moirai.semantics.infer.SubstitutionChain

internal sealed interface Type

internal sealed interface TerminusType: Type, RawTerminus

internal data object ErrorType : Type

internal class FunctionType(
    val formalParamTypes: List<Type>,
    val returnType: Type
) : Type

internal sealed class TypeParameter : Type

internal class StandardTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), Type

internal class FinTypeParameter(
    val qualifiedName: String,
    val identifier: Identifier
) : TypeParameter(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

internal class ParameterHashCodeCost(
    val typeParameter: StandardTypeParameter
) : CostExpression, TerminusType {
    override val typeParams: List<TypeParameter> = listOf(typeParameter)

    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

internal class InstantiationHashCodeCost(
    val instantiation: TypeInstantiation
) : CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

internal class Fin(val magnitude: Long) : CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

internal data object ConstantFin : CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

internal class SumCostExpression(args: List<CostExpression>) : CostExpression {
    val children: List<CostExpression> = sortCanonical(args)

    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

internal class ProductCostExpression(args: List<CostExpression>) : CostExpression {
    val children: List<CostExpression> = sortCanonical(args)

    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

internal class MaxCostExpression(args: List<CostExpression>) : CostExpression {
    val children: List<CostExpression> = sortCanonical(args)

    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

internal class TypeInstantiation(
    val substitutionChain: SubstitutionChain<TerminusType>
) : Type

internal class PlatformObjectType(
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Type, Scope by symbolTable

internal class ObjectType(
    val qualifiedName: String,
    val identifier: Identifier,
    val featureSupport: FeatureSupport
) : Type

internal class GroundRecordType(
    definitionScopeForTypeChecking: Scope,
    val qualifiedName: String,
    val identifier: Identifier,
    private val symbolTable: SymbolTable = SymbolTable(definitionScopeForTypeChecking)
) : Type, Scope by symbolTable {
    lateinit var fields: List<FieldSymbol>
}

internal class ParameterizedRecordType(
    definitionScopeForTypeChecking: Scope,
    val qualifiedName: String,
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(definitionScopeForTypeChecking)
) : TerminusType, Scope by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<FieldSymbol>
}

internal class BasicType(
    val identifier: Identifier,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Type, Scope by symbolTable

internal class ParameterizedBasicType(
    val identifier: Identifier,
    val instantiation: GroundInstantiationValidation<TerminusType, TypeInstantiation>,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : TerminusType, Scope by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<PlatformFieldSymbol>
}

internal class PlatformSumType(
    val identifier: Identifier,
    val featureSupport: FeatureSupport
) : TerminusType {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var memberTypes: List<PlatformSumMember>
}

internal sealed interface PlatformSumMember

internal class PlatformSumRecordType(
    definitionScopeForTypeChecking: Scope,
    val sumType: PlatformSumType,
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(definitionScopeForTypeChecking)
) : TerminusType, PlatformSumMember, Scope by symbolTable {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<FieldSymbol>
}

internal class PlatformSumObjectType(
    val sumType: PlatformSumType,
    val identifier: Identifier,
    val featureSupport: FeatureSupport,
    private val symbolTable: SymbolTable = SymbolTable(NullSymbolTable)
) : Type, PlatformSumMember, Scope by symbolTable