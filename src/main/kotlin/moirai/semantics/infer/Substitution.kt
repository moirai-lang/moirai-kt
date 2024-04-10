package moirai.semantics.infer

import moirai.semantics.core.*
import moirai.semantics.prelude.StringMethods

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

    private fun costExpressionFromAnyType(type: Type): CostExpression {
        fun fromToString(member: Symbol): CostExpression {
            return when (member) {
                is GroundMemberPluginSymbol -> {
                    member.costExpression
                }

                is ParameterizedMemberPluginSymbol -> {
                    member.costExpression
                }

                else -> langThrow(TypeSystemBug)
            }
        }

        return when (type) {
            is ConstantFin -> type
            is Fin -> type
            is FinTypeParameter -> type
            is MaxCostExpression -> MaxCostExpression(type.children.map { costExpressionFromAnyType(it) })
            is ProductCostExpression -> ProductCostExpression(type.children.map { costExpressionFromAnyType(it) })
            is SumCostExpression -> SumCostExpression(type.children.map { costExpressionFromAnyType(it) })
            is ParameterHashCodeCost -> type
            is InstantiationHashCodeCost -> costExpressionFromAnyType(type.instantiation)

            is PlatformObjectType -> {
                fromToString(type.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr)))
            }

            is BasicType -> {
                fromToString(type.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr)))
            }

            is TypeInstantiation -> {
                when (val parameterizedType = type.substitutionChain.terminus) {
                    is ParameterizedBasicType -> {
                        fromToString(parameterizedType.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr)))
                    }

                    is ParameterHashCodeCost -> {
                        costExpressionFromAnyType(type.substitutionChain.replay(parameterizedType.typeParameter))
                    }

                    is ParameterizedRecordType -> {
                        SumCostExpression(parameterizedType.fields.map {
                            costExpressionFromAnyType(type.substitutionChain.replay(it.ofTypeSymbol))
                        })
                    }

                    is PlatformSumRecordType -> {
                        SumCostExpression(parameterizedType.fields.map {
                            costExpressionFromAnyType(type.substitutionChain.replay(it.ofTypeSymbol))
                        })
                    }

                    is PlatformSumType -> {
                        SumCostExpression(parameterizedType.memberTypes.map {
                            costExpressionFromAnyType(
                                type.substitutionChain.replay(
                                    when (it) {
                                        is PlatformSumObjectType -> it
                                        is PlatformSumRecordType -> it
                                    }
                                )
                            )
                        })
                    }
                }
            }

            is GroundRecordType -> SumCostExpression(type.fields.map { costExpressionFromAnyType(it.ofTypeSymbol) })
            is ObjectType -> Fin(type.identifier.name.length.toLong())
            is PlatformSumObjectType -> Fin(type.identifier.name.length.toLong())
            is StandardTypeParameter -> ParameterHashCodeCost(type)

            else -> langThrow(TypeSystemBug)
        }
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
            is ParameterHashCodeCost -> {
                val res = if (solutions.containsKey(costExpression)) {
                    val solution = solutions[costExpression]!!
                    costExpressionFromAnyType(solution)
                } else {
                    costExpression
                }
                res
            }

            is InstantiationHashCodeCost -> costExpressionFromAnyType(apply(costExpression.instantiation))
        }
    }
}