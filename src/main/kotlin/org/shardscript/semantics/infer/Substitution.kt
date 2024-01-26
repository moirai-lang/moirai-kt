package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class Substitution(
    inOrderParameters: List<TypeParameter>,
    inOrderTypeArgs: List<Type>
) {
    private val solutions: Map<Symbol, Type>

    init {
        if (inOrderParameters.size != inOrderTypeArgs.size) {
            langThrow(TypeSystemBug)
        }
        solutions = inOrderParameters.zip(inOrderTypeArgs).toMap()
    }

    fun apply(parameterizedSymbol: ParameterizedSymbol): SymbolInstantiation {
        val original = TerminalChain(parameterizedSymbol)
        val chain = SubstitutionChain(this, original)
        return SymbolInstantiation(chain)
    }

    fun apply(symbolInstantiation: SymbolInstantiation): SymbolInstantiation {
        val chain = SubstitutionChain(this, symbolInstantiation.substitutionChain)
        return SymbolInstantiation(chain)
    }

    fun applyFunctionType(typeSymbol: FunctionTypeSymbol): FunctionTypeSymbol {
        val formalParams = typeSymbol.formalParamTypes.map { applySymbol(it) }
        val returnType = applySymbol(typeSymbol.returnType)
        return FunctionTypeSymbol(
            formalParams,
            returnType
        )
    }

    fun applySymbol(type: Type): Type =
        when (type) {
            is FunctionTypeSymbol -> applyFunctionType(type)
            is ParameterizedRecordTypeSymbol -> apply(type)
            is ParameterizedBasicTypeSymbol -> apply(type)
            is SymbolInstantiation -> apply(type)
            is StandardTypeParameter -> {
                val res = if (solutions.containsKey(type)) {
                    solutions[type]!!
                } else {
                    type
                }
                res
            }
            is ImmutableFinTypeParameter -> {
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
            is ImmutableFinTypeParameter -> {
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
            is MutableFinTypeParameter -> {
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
            is FinTypeSymbol -> costExpression
            is ConstantFinTypeSymbol -> costExpression
            is SumCostExpression -> SumCostExpression(costExpression.children.map { applyCost(it) })
            is ProductCostExpression -> ProductCostExpression(costExpression.children.map { applyCost(it) })
            is MaxCostExpression -> MaxCostExpression(costExpression.children.map { applyCost(it) })
        }
    }
}