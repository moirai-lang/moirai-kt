package moirai.semantics.infer

import moirai.semantics.core.*

internal fun createSubstitution(
    ctx: SourceContext,
    constraints: MutableList<Constraint<TypeParameter, Type>>,
    parameterSet: Set<TypeParameter>,
    inOrderParameters: List<TypeParameter>,
    errors: LanguageErrors
): Substitution {
    val instantiations: MutableMap<TypeParameter, Type> = HashMap()
    val relations = equivalenceRelations(constraints.toSet())
    relations.forEach { relation ->
        val typeParam = relation.someItem.value
        if (instantiations.containsKey(typeParam)) {
            errors.add(ctx, CannotInstantiate)
        }
        when {
            relation.equivalences.isEmpty() -> {
                errors.add(ctx, TypeInferenceFailed(typeParam))
            }
            relation.equivalences.size > 1 -> {
                instantiations[relation.someItem.value] = findBestType(ctx, errors, relation.equivalences.map {
                    it.value
                })
            }
            else -> {
                instantiations[relation.someItem.value] = relation.equivalences.first().value
            }
        }
    }

    return if (instantiations.isNotEmpty() && instantiations.all { parameterSet.contains(it.key) }) {
        val typeArgs = parameterSet.flatMap {
            if (!instantiations.containsKey(it)) {
                errors.add(ctx, TypeInferenceFailed(it))
                listOf()
            } else {
                listOf(instantiations[it]!!)
            }
        }
        Substitution(inOrderParameters, typeArgs)
    } else {
        errors.add(ctx, TypeSystemBug)
        Substitution(listOf(), listOf())
    }
}

internal fun instantiateFunction(
    ctx: SourceContext,
    args: List<Ast>,
    parameterizedFunctionSymbol: ParameterizedFunctionSymbol,
    errors: LanguageErrors
): SymbolInstantiation {
    val inOrderParameters = parameterizedFunctionSymbol.typeParams
    val parameterSet = inOrderParameters.toSet()
    if (parameterizedFunctionSymbol.formalParams.size == args.size) {
        val constraints: MutableList<Constraint<TypeParameter, Type>> = ArrayList()
        parameterizedFunctionSymbol.formalParams.zip(args).forEach {
            constraints.addAll(constrainSymbol(ctx, parameterSet, it.first.ofTypeSymbol, it.second.readType(), errors))
        }
        val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
        return substitution.apply(parameterizedFunctionSymbol)
    } else {
        errors.add(
            ctx,
            IncorrectNumberOfArgs(parameterizedFunctionSymbol.formalParams.size, args.size)
        )
    }
    val substitution = Substitution(parameterizedFunctionSymbol.typeParams, listOf())
    return substitution.apply(parameterizedFunctionSymbol)
}

internal fun instantiateRecord(
    ctx: SourceContext,
    args: List<Ast>,
    parameterizedRecordType: ParameterizedRecordType,
    errors: LanguageErrors
): TypeInstantiation {
    val inOrderParameters = parameterizedRecordType.typeParams
    val parameterSet = inOrderParameters.toSet()
    if (parameterizedRecordType.fields.size == args.size) {
        val constraints: MutableList<Constraint<TypeParameter, Type>> = ArrayList()
        parameterizedRecordType.fields.zip(args).forEach {
            val expected = it.first.ofTypeSymbol
            constraints.addAll(constrainSymbol(ctx, parameterSet, expected, it.second.readType(), errors))
        }
        val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
        return substitution.apply(parameterizedRecordType)
    } else {
        errors.add(
            ctx,
            IncorrectNumberOfArgs(parameterizedRecordType.fields.size, args.size)
        )
    }
    val substitution = Substitution(parameterizedRecordType.typeParams, listOf())
    return substitution.apply(parameterizedRecordType)
}

internal fun instantiatePlatformSumRecord(
    ctx: SourceContext,
    args: List<Ast>,
    platformSumRecordType: PlatformSumRecordType,
    errors: LanguageErrors
): TypeInstantiation {
    val inOrderParameters = platformSumRecordType.typeParams
    val parameterSet = inOrderParameters.toSet()
    if (platformSumRecordType.fields.size == args.size) {
        val constraints: MutableList<Constraint<TypeParameter, Type>> = ArrayList()
        platformSumRecordType.fields.zip(args).forEach {
            val expected = it.first.ofTypeSymbol
            constraints.addAll(constrainSymbol(ctx, parameterSet, expected, it.second.readType(), errors))
        }
        val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
        return substitution.apply(platformSumRecordType)
    } else {
        errors.add(
            ctx,
            IncorrectNumberOfArgs(platformSumRecordType.fields.size, args.size)
        )
    }
    val substitution = Substitution(platformSumRecordType.typeParams, listOf())
    return substitution.apply(platformSumRecordType)
}

internal fun constrainSymbol(
    ctx: SourceContext,
    typeParams: Set<TypeParameter>,
    expected: Type,
    actual: Type,
    errors: LanguageErrors
): List<Constraint<TypeParameter, Type>> =
    when (expected) {
        is BasicType -> listOf()
        is ObjectType -> listOf()
        is FunctionType -> when (actual) {
            is FunctionType -> {
                val constraints: MutableList<Constraint<TypeParameter, Type>> = ArrayList()
                if (expected.formalParamTypes.size == actual.formalParamTypes.size) {
                    expected.formalParamTypes.zip(actual.formalParamTypes).forEach {
                        constraints.addAll(constrainSymbol(ctx, typeParams, it.first, it.second, errors))
                    }
                    constraints.addAll(constrainSymbol(ctx, typeParams, expected.returnType, actual.returnType, errors))
                } else {
                    errors.add(ctx, TypeMismatch(expected, actual))
                }
                constraints
            }
            else -> {
                errors.add(ctx, TypeMismatch(expected, actual))
                listOf()
            }
        }
        is TypeInstantiation -> when (actual) {
            is TypeInstantiation -> {
                val constraints: MutableList<Constraint<TypeParameter, Type>> = ArrayList()
                if (expected.substitutionChain.terminus == actual.substitutionChain.terminus) {
                    val expectedChain = expected.substitutionChain
                    val actualChain = actual.substitutionChain
                    expectedChain.replayArgs().zip(actualChain.replayArgs()).forEach {
                        constraints.addAll(constrainSymbol(ctx, typeParams, it.first, it.second, errors))
                    }
                    constraints
                } else {
                    errors.add(ctx, TypeMismatch(expected, actual))
                    constraints
                }
            }
            else -> {
                errors.add(ctx, TypeMismatch(expected, actual))
                listOf()
            }
        }
        is CostExpression -> when (actual) {
            is CostExpression -> {
                constrainCost(ctx, typeParams, expected, actual, errors)
            }
            else -> {
                errors.add(ctx, TypeMismatch(expected, actual))
                listOf()
            }
        }
        is TypeParameter -> if (typeParams.contains(expected)) {
            listOf(
                Constraint(
                    Left(expected),
                    Right(actual)
                )
            )
        } else {
            errors.add(ctx, TypeSystemBug)
            listOf()
        }
        else -> {
            errors.add(ctx, TypeSystemBug)
            listOf()
        }
    }

internal fun constrainCost(
    ctx: SourceContext,
    typeParams: Set<TypeParameter>,
    expected: CostExpression,
    actual: CostExpression,
    errors: LanguageErrors
): List<Constraint<TypeParameter, Type>> =
    when (expected) {
        is Fin -> listOf()
        is ConstantFin -> listOf()
        is FinTypeParameter -> if (typeParams.contains(expected)) {
            listOf(
                Constraint(
                    Left<TypeParameter>(expected),
                    Right(actual)
                )
            )
        } else {
            errors.add(ctx, TypeSystemBug)
            listOf()
        }
        is SumCostExpression -> when (actual) {
            is SumCostExpression -> {
                if (expected.children.size == actual.children.size) {
                    expected.children.zip(actual.children).flatMap {
                        constrainCost(ctx, typeParams, it.first, it.second, errors)
                    }
                } else {
                    errors.add(
                        ctx, TypeMismatch(
                            expected,
                            actual
                        )
                    )
                    listOf()
                }
            }
            else -> {
                errors.add(
                    ctx, TypeMismatch(
                        expected,
                        actual
                    )
                )
                listOf()
            }
        }
        is ProductCostExpression -> when (actual) {
            is ProductCostExpression -> {
                if (expected.children.size == actual.children.size) {
                    expected.children.zip(actual.children).flatMap {
                        constrainCost(ctx, typeParams, it.first, it.second, errors)
                    }
                } else {
                    errors.add(
                        ctx, TypeMismatch(
                            expected,
                            actual
                        )
                    )
                    listOf()
                }
            }
            else -> {
                errors.add(
                    ctx, TypeMismatch(
                        expected,
                        actual
                    )
                )
                listOf()
            }
        }
        is MaxCostExpression -> when (actual) {
            is MaxCostExpression -> {
                if (expected.children.size == actual.children.size) {
                    expected.children.zip(actual.children).flatMap {
                        constrainCost(ctx, typeParams, it.first, it.second, errors)
                    }
                } else {
                    errors.add(
                        ctx, TypeMismatch(
                            expected,
                            actual
                        )
                    )
                    listOf()
                }
            }
            else -> {
                errors.add(
                    ctx, TypeMismatch(
                        expected,
                        actual
                    )
                )
                listOf()
            }
        }
    }