package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

fun createSubstitution(
    ctx: SourceContext,
    constraints: MutableList<Constraint<TypeParameter, Symbol>>,
    parameterSet: Set<TypeParameter>,
    inOrderParameters: List<TypeParameter>,
    errors: LanguageErrors
): Substitution {
    val instantiations: MutableMap<TypeParameter, Symbol> = HashMap()
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

fun instantiateFunction(
    ctx: SourceContext,
    args: List<Ast>,
    parameterizedFunctionSymbol: ParameterizedFunctionSymbol,
    errors: LanguageErrors
): SymbolInstantiation {
    val inOrderParameters = parameterizedFunctionSymbol.typeParams
    val parameterSet = inOrderParameters.toSet()
    if (parameterizedFunctionSymbol.formalParams.size == args.size) {
        val constraints: MutableList<Constraint<TypeParameter, Symbol>> = ArrayList()
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

fun instantiateRecord(
    ctx: SourceContext,
    args: List<Ast>,
    parameterizedRecordTypeSymbol: ParameterizedRecordTypeSymbol,
    errors: LanguageErrors
): SymbolInstantiation {
    val inOrderParameters = parameterizedRecordTypeSymbol.typeParams
    val parameterSet = inOrderParameters.toSet()
    if (parameterizedRecordTypeSymbol.fields.size == args.size) {
        val constraints: MutableList<Constraint<TypeParameter, Symbol>> = ArrayList()
        parameterizedRecordTypeSymbol.fields.zip(args).forEach {
            val expected = it.first.ofTypeSymbol
            constraints.addAll(constrainSymbol(ctx, parameterSet, expected, it.second.readType(), errors))
        }
        val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
        return substitution.apply(parameterizedRecordTypeSymbol)
    } else {
        errors.add(
            ctx,
            IncorrectNumberOfArgs(parameterizedRecordTypeSymbol.fields.size, args.size)
        )
    }
    val substitution = Substitution(parameterizedRecordTypeSymbol.typeParams, listOf())
    return substitution.apply(parameterizedRecordTypeSymbol)
}

fun constrainSymbol(
    ctx: SourceContext,
    typeParams: Set<TypeParameter>,
    expected: Symbol,
    actual: Symbol,
    errors: LanguageErrors
): List<Constraint<TypeParameter, Symbol>> =
    when (expected) {
        is BasicTypeSymbol -> listOf()
        is ObjectSymbol -> listOf()
        is FunctionTypeSymbol -> when (actual) {
            is FunctionTypeSymbol -> {
                val constraints: MutableList<Constraint<TypeParameter, Symbol>> = ArrayList()
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
        is SymbolInstantiation -> when (actual) {
            is SymbolInstantiation -> {
                val constraints: MutableList<Constraint<TypeParameter, Symbol>> = ArrayList()
                if (expected.substitutionChain.originalSymbol == actual.substitutionChain.originalSymbol) {
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

fun constrainCost(
    ctx: SourceContext,
    typeParams: Set<TypeParameter>,
    expected: CostExpression,
    actual: CostExpression,
    errors: LanguageErrors
): List<Constraint<TypeParameter, Symbol>> =
    when (expected) {
        is OmicronTypeSymbol -> listOf()
        is ImmutableOmicronTypeParameter -> if (typeParams.contains(expected)) {
            listOf(
                Constraint(
                    Left<TypeParameter>(expected),
                    Right(actual.symbolically)
                )
            )
        } else {
            errors.add(ctx, TypeSystemBug)
            listOf()
        }
        is MutableOmicronTypeParameter -> if (typeParams.contains(expected)) {
            listOf(
                Constraint(
                    Left<TypeParameter>(expected),
                    Right(actual.symbolically)
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
                        actual.symbolically
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
                        actual.symbolically
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
                        actual.symbolically
                    )
                )
                listOf()
            }
        }
        else -> langThrow(TypeSystemBug)
    }