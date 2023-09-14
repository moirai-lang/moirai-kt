package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class Substitution(
    inOrderParameters: List<TypeParameter>,
    inOrderTypeArgs: List<Symbol>
) {
    private val solutions: Map<Symbol, Symbol>

    init {
        if (inOrderParameters.size != inOrderTypeArgs.size) {
            langThrow(TypeSystemBug)
        }
        solutions = inOrderParameters.zip(inOrderTypeArgs).toMap()
    }

    fun apply(parameterizedSymbol: ParameterizedSymbol): SymbolInstantiation {
        val original = TerminalChain(parameterizedSymbol)
        val chain = SubstitutionChain(this, original)
        return SymbolInstantiation(parameterizedSymbol.parent, chain)
    }

    fun apply(symbolInstantiation: SymbolInstantiation): SymbolInstantiation {
        val chain = SubstitutionChain(this, symbolInstantiation.substitutionChain)
        return SymbolInstantiation(symbolInstantiation.parent, chain)
    }

    fun applyFunctionType(typeSymbol: FunctionTypeSymbol): FunctionTypeSymbol {
        val formalParams = typeSymbol.formalParamTypes.map { applySymbol(it) }
        val returnType = applySymbol(typeSymbol.returnType)
        return FunctionTypeSymbol(
            formalParams,
            returnType
        )
    }

    fun applySymbol(symbol: Symbol): Symbol =
        when (symbol) {
            is FunctionTypeSymbol -> applyFunctionType(symbol)
            is ParameterizedRecordTypeSymbol -> apply(symbol)
            is ParameterizedBasicTypeSymbol -> apply(symbol)
            is SymbolInstantiation -> apply(symbol)
            is StandardTypeParameter -> {
                val res = if (solutions.containsKey(symbol)) {
                    solutions[symbol]!!
                } else {
                    symbol
                }
                res
            }
            is ImmutableFinTypeParameter -> {
                val res = if (solutions.containsKey(symbol)) {
                    solutions[symbol]!!
                } else {
                    symbol
                }
                res
            }
            is CostExpression -> {
                applyCost(symbol).symbolically
            }
            else -> symbol
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
            is SumCostExpression -> SumCostExpression(costExpression.children.map { applyCost(it) })
            is ProductCostExpression -> ProductCostExpression(costExpression.children.map { applyCost(it) })
            is MaxCostExpression -> MaxCostExpression(costExpression.children.map { applyCost(it) })
            else -> langThrow(TypeSystemBug)
        }
    }
}