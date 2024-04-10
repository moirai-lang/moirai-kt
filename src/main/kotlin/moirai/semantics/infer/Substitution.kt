package moirai.semantics.infer

import moirai.semantics.core.*

internal class Substitution(
    inOrderParameters: List<TypeParameter>,
    inOrderTypeArgs: List<Type>
) {
    private val solutions: Map<Type, Type>

    init {
        if (inOrderParameters.size != inOrderTypeArgs.size) {
            langThrow(TypeSystemBug)
        }
        solutions = inOrderParameters.zip(inOrderTypeArgs).toMap()
    }

    fun apply(terminusType: TerminusType): TypeInstantiation {
        val original = TerminalChain(terminusType)
        val chain = SubstitutionChain(this, original)
        return TypeInstantiation(chain)
    }

    fun apply(terminusSymbol: RawTerminusSymbol): SymbolInstantiation {
        val original = TerminalChain(terminusSymbol)
        val chain = SubstitutionChain(this, original)
        return SymbolInstantiation(chain)
    }

    fun apply(typeInstantiation: TypeInstantiation): TypeInstantiation {
        val chain = SubstitutionChain(this, typeInstantiation.substitutionChain)
        return TypeInstantiation(chain)
    }

    fun applyFunctionType(typeSymbol: FunctionType): FunctionType {
        val formalParams = typeSymbol.formalParamTypes.map { applySymbol(it) }
        val returnType = applySymbol(typeSymbol.returnType)
        return FunctionType(
            formalParams,
            returnType
        )
    }

    fun applySymbol(type: Type): Type =
        when (type) {
            is FunctionType -> applyFunctionType(type)
            is ParameterizedRecordType -> apply(type)
            is PlatformSumRecordType -> apply(type)
            is PlatformSumType -> apply(type)
            is ParameterizedBasicType -> apply(type)
            is TypeInstantiation -> apply(type)
            is StandardTypeParameter -> {
                val res = if (solutions.containsKey(type)) {
                    solutions[type]!!
                } else {
                    type
                }
                res
            }
            is FinTypeParameter -> {
                val res = if (solutions.containsKey(type)) {
                    solutions[type]!!
                } else {
                    type
                }
                res
            }
            is CostExpression -> {
                applyCost(type)
            }
            else -> type
        }

    fun applyCost(costExpression: CostExpression): CostExpression {
        return when (costExpression) {
            is FinTypeParameter -> {
                val res = if (solutions.containsKey(costExpression)) {
                    when (val solution = solutions[costExpression]!!) {
                        is CostExpression -> solution
                        else -> costExpression
                    }
                } else {
                    costExpression
                }
                res
            }
            is Fin -> costExpression
            is ConstantFin -> costExpression
            is SumCostExpression -> SumCostExpression(costExpression.children.map { applyCost(it) })
            is ProductCostExpression -> ProductCostExpression(costExpression.children.map { applyCost(it) })
            is MaxCostExpression -> MaxCostExpression(costExpression.children.map { applyCost(it) })
            is HashCodeCost -> {
                val res = if (solutions.containsKey(costExpression)) {
                    when (val solution = solutions[costExpression]!!) {
                        is StandardTypeParameter -> HashCodeCost(solution)
                        else -> costExpression
                    }
                } else {
                    costExpression
                }
                res
            }
        }
    }
}