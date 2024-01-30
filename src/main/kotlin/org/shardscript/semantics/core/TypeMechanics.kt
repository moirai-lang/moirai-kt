package org.shardscript.semantics.core

import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.prelude.Lang

fun filterValidTypes(ctx: SourceContext, errors: LanguageErrors, type: Type): Type =
    when (type) {
        ErrorSymbol,
        is GroundRecordTypeSymbol,
        is BasicTypeSymbol,
        is ObjectSymbol,
        is PlatformObjectSymbol,
        is StandardTypeParameter,
        is FunctionTypeSymbol -> type

        is TypeInstantiation -> {
            type.substitutionChain.terminus.typeParams.forEach {
                validateSubstitution(ctx, errors, it, type.substitutionChain.replay(it))
            }
            type
        }

        is FinTypeSymbol,
        is ConstantFinTypeSymbol,
        is ImmutableFinTypeParameter,
        is MutableFinTypeParameter -> {
            errors.add(ctx, ExpectOtherError)
            ErrorSymbol
        }

        is ParameterizedBasicTypeSymbol,
        is ParameterizedRecordTypeSymbol -> {
            errors.add(ctx, CannotUseRawType(type))
            ErrorSymbol
        }

        is MaxCostExpression,
        is ProductCostExpression,
        is SumCostExpression -> {
            errors.add(ctx, TypeSystemBug)
            ErrorSymbol
        }
    }

fun filterValidGroundApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    symbol: Symbol,
    signifier: Signifier
): Symbol =
    when (symbol) {
        ErrorSymbol,
        is FunctionFormalParameterSymbol,
        is GroundFunctionSymbol,
        is GroundRecordTypeSymbol,
        is ParameterizedStaticPluginSymbol,
        is ParameterizedBasicTypeSymbol,
        is ParameterizedFunctionSymbol,
        is ParameterizedRecordTypeSymbol -> symbol
        is TypeInstantiation -> {
            when (symbol.substitutionChain.terminus) {
                is ParameterizedBasicTypeSymbol,
                is ParameterizedRecordTypeSymbol -> {
                    symbol.substitutionChain.terminus.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, symbol.substitutionChain.replay(it))
                    }
                    symbol
                }
            }
        }
        is SymbolInstantiation -> {
            when (symbol.substitutionChain.terminus) {
                is ParameterizedStaticPluginSymbol,
                is ParameterizedFunctionSymbol -> {
                    symbol.substitutionChain.terminus.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, symbol.substitutionChain.replay(it))
                    }
                    symbol
                }

                else -> {
                    errors.add(ctx, SymbolCouldNotBeApplied(signifier))
                    ErrorSymbol
                }
            }
        }
        is FunctionTypeSymbol,
        is ParameterizedMemberPluginSymbol,
        is FinTypeSymbol,
        is ConstantFinTypeSymbol,
        is ImmutableFinTypeParameter,
        is MutableFinTypeParameter,
        is GroundMemberPluginSymbol,
        is BasicTypeSymbol,
        is ObjectSymbol,
        is PlatformObjectSymbol,
        is StandardTypeParameter,
        is Block,
        is SumCostExpression,
        is ProductCostExpression,
        is MaxCostExpression,
        is FieldSymbol,
        is PlatformFieldSymbol,
        is LambdaSymbol,
        is LocalVariableSymbol -> {
            errors.add(ctx, SymbolCouldNotBeApplied(signifier))
            ErrorSymbol
        }
    }

fun filterValidDotApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    symbol: Symbol,
    signifier: Signifier
): Symbol =
    when (symbol) {
        ErrorSymbol,
        is GroundFunctionSymbol,
        is GroundRecordTypeSymbol,
        is GroundMemberPluginSymbol,
        is ParameterizedStaticPluginSymbol,
        is ParameterizedBasicTypeSymbol,
        is ParameterizedFunctionSymbol,
        is ParameterizedRecordTypeSymbol -> symbol

        is TypeInstantiation -> {
            when (symbol.substitutionChain.terminus) {
                is ParameterizedBasicTypeSymbol,
                is ParameterizedRecordTypeSymbol -> {
                    symbol.substitutionChain.terminus.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, symbol.substitutionChain.replay(it))
                    }
                    symbol
                }
            }
        }

        is SymbolInstantiation -> {
            when (symbol.substitutionChain.terminus) {
                is ParameterizedStaticPluginSymbol,
                is ParameterizedFunctionSymbol,
                is ParameterizedMemberPluginSymbol -> {
                    symbol.substitutionChain.terminus.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, symbol.substitutionChain.replay(it))
                    }
                    symbol
                }
            }
        }

        is FunctionTypeSymbol,
        is ParameterizedMemberPluginSymbol,
        is FinTypeSymbol,
        is ConstantFinTypeSymbol,
        is ImmutableFinTypeParameter,
        is MutableFinTypeParameter,
        is BasicTypeSymbol,
        is ObjectSymbol,
        is PlatformObjectSymbol,
        is StandardTypeParameter,
        is Block,
        is SumCostExpression,
        is ProductCostExpression,
        is MaxCostExpression,
        is FunctionFormalParameterSymbol,
        is FieldSymbol,
        is PlatformFieldSymbol,
        is LambdaSymbol,
        is LocalVariableSymbol -> {
            errors.add(ctx, SymbolCouldNotBeApplied(signifier))
            ErrorSymbol
        }
    }

fun symbolToType(errors: LanguageErrors, ctx: SourceContext, symbol: Symbol, signifier: Signifier): Type {
    return if (symbol is Type) {
        symbol
    } else {
        errors.add(ctx, SymbolIsNotAType(signifier))
        ErrorSymbol
    }
}

fun getQualifiedName(symbol: Symbol): String {
    return when (symbol) {
        is GroundRecordTypeSymbol -> {
            symbol.qualifiedName
        }
        is ParameterizedRecordTypeSymbol -> {
            symbol.qualifiedName
        }
        is TypeInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.terminus) {
                is ParameterizedBasicTypeSymbol -> {
                    parameterizedType.identifier.name
                }
                is ParameterizedRecordTypeSymbol -> {
                    parameterizedType.qualifiedName
                }
            }
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.terminus) {
                is ParameterizedFunctionSymbol -> {
                    parameterizedType.identifier.name
                }

                is ParameterizedMemberPluginSymbol -> {
                    parameterizedType.identifier.name
                }

                is ParameterizedStaticPluginSymbol -> {
                    parameterizedType.identifier.name
                }
            }
        }
        is ParameterizedBasicTypeSymbol -> {
            symbol.identifier.name
        }
        is ParameterizedStaticPluginSymbol -> {
            symbol.identifier.name
        }
        is BasicTypeSymbol -> {
            symbol.identifier.name
        }
        is ObjectSymbol -> {
            symbol.qualifiedName
        }
        is PlatformObjectSymbol -> {
            symbol.identifier.name
        }
        is StandardTypeParameter -> {
            symbol.qualifiedName
        }
        is ImmutableFinTypeParameter -> {
            symbol.qualifiedName
        }
        is MutableFinTypeParameter -> {
            symbol.qualifiedName
        }
        else -> ""
    }
}

fun checkTypes(
    ctx: SourceContext,
    prelude: Scope,
    errors: LanguageErrors,
    expected: Type,
    actual: Type
) {
    when {
        expected is FunctionTypeSymbol && actual is FunctionTypeSymbol -> {
            checkTypes(ctx, prelude, errors, expected.formalParamTypes, actual.formalParamTypes)
            checkTypes(ctx, prelude, errors, expected.returnType, actual.returnType)
        }
        expected is FunctionTypeSymbol && actual !is FunctionTypeSymbol -> {
            errors.add(ctx, TypeMismatch(expected, actual))
        }
        expected !is FunctionTypeSymbol && actual is FunctionTypeSymbol -> {
            errors.add(ctx, TypeMismatch(expected, actual))
        }
        expected is FinTypeSymbol && actual is FinTypeSymbol -> {
            if (actual.magnitude > expected.magnitude) {
                errors.add(ctx, FinMismatch(expected.magnitude, actual.magnitude))
            }
        }
        expected is TypeInstantiation && actual is TypeInstantiation -> {
            val expectedParameterized = expected.substitutionChain.terminus
            val actualParameterized = actual.substitutionChain.terminus
            when {
                expectedParameterized is ParameterizedRecordTypeSymbol && actualParameterized is ParameterizedRecordTypeSymbol -> {
                    checkTypes(ctx, prelude, errors, expectedParameterized, actualParameterized)
                    checkTypes(
                        ctx,
                        prelude,
                        errors,
                        expected.substitutionChain.replayArgs(),
                        actual.substitutionChain.replayArgs()
                    )
                }
                expectedParameterized is ParameterizedBasicTypeSymbol && actualParameterized is ParameterizedBasicTypeSymbol -> {
                    checkTypes(ctx, prelude, errors, expectedParameterized, actualParameterized)
                    checkTypes(
                        ctx,
                        prelude,
                        errors,
                        expected.substitutionChain.replayArgs(),
                        actual.substitutionChain.replayArgs()
                    )
                }
            }
        }
        else -> {
            val expectedPath = getQualifiedName(expected as Symbol)
            val actualPath = getQualifiedName(actual as Symbol)
            if (expectedPath != actualPath) {
                errors.add(ctx, TypeMismatch(expected, actual))
            }
        }
    }
}

fun checkTypes(
    ctx: SourceContext,
    prelude: Scope,
    errors: LanguageErrors,
    expectedTypeArgs: List<Type>,
    actualTypeArgs: List<Type>
) {
    if (expectedTypeArgs.size != actualTypeArgs.size) {
        errors.add(ctx, TypeSystemBug)
    } else {
        expectedTypeArgs.zip(actualTypeArgs).forEach {
            checkTypes(ctx, prelude, errors, it.first, it.second)
        }
    }
}

fun checkApply(prelude: Scope, errors: LanguageErrors, ast: ApplyAst) {
    when (val symbol = ast.symbolRef) {
        is GroundFunctionSymbol -> {
            checkArgs(prelude, errors, symbol.type(), ast)
        }

        is GroundRecordTypeSymbol -> {
            checkArgs(prelude, errors, symbol, ast)
        }

        is FunctionFormalParameterSymbol -> when (val ofTypeSymbol = symbol.ofTypeSymbol) {
            is FunctionTypeSymbol -> {
                checkArgs(prelude, errors, ofTypeSymbol, ast)
            }

            else -> errors.add(ast.ctx, TypeSystemBug)
        }

        is GroundMemberPluginSymbol -> {
            checkArgs(prelude, errors, symbol.type(), ast)
        }

        is TypeInstantiation -> {
            when (val parameterizedSymbol = symbol.substitutionChain.terminus) {
                is ParameterizedRecordTypeSymbol -> {
                    checkArgs(prelude, errors, symbol, parameterizedSymbol, ast)
                }

                is ParameterizedBasicTypeSymbol -> {
                    checkArgs(prelude, errors, symbol, parameterizedSymbol, ast)
                }
            }
        }

        is SymbolInstantiation -> {
            when (val parameterizedSymbol = symbol.substitutionChain.terminus) {
                is ParameterizedMemberPluginSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast
                    )
                }

                is ParameterizedFunctionSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast
                    )
                }

                is ParameterizedStaticPluginSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast
                    )
                }
            }
        }

        else -> errors.add(ast.ctx, TypeSystemBug)
    }
}

fun checkArgs(prelude: Scope, errors: LanguageErrors, type: FunctionTypeSymbol, ast: ApplyAst) {
    if (type.formalParamTypes.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.formalParamTypes.size, ast.args.size))
    } else {
        type.formalParamTypes.zip(ast.args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first, it.second.readType())
        }
        checkTypes(ast.ctx, prelude, errors, type.returnType, ast.readType())
    }
}

fun checkArgs(prelude: Scope, errors: LanguageErrors, type: GroundRecordTypeSymbol, ast: ApplyAst) {
    if (type.fields.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.fields.size, ast.args.size))
    } else {
        type.fields.zip(ast.args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first.ofTypeSymbol, it.second.readType())
        }
    }
}

fun checkArgs(
    prelude: Scope,
    errors: LanguageErrors,
    instantiation: TypeInstantiation,
    parameterizedType: ParameterizedRecordTypeSymbol,
    ast: ApplyAst
) {
    if (parameterizedType.fields.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(parameterizedType.fields.size, ast.args.size))
    } else {
        parameterizedType.fields.zip(ast.args).forEach {
            val ofTypeSymbol = instantiation.substitutionChain.replay(it.first.ofTypeSymbol)
            checkTypes(it.second.ctx, prelude, errors, ofTypeSymbol, it.second.readType())
        }
    }
}

fun checkArgs(
    prelude: Scope,
    errors: LanguageErrors,
    instantiation: TypeInstantiation,
    parameterizedType: ParameterizedBasicTypeSymbol,
    ast: ApplyAst
) {
    if (parameterizedType.identifier == Lang.dictionaryId || parameterizedType.identifier == Lang.mutableDictionaryId) {
        val pairType = prelude.fetch(Lang.pairId) as ParameterizedRecordTypeSymbol
        val pairSubstitution = Substitution(
            pairType.typeParams,
            listOf(
                instantiation.substitutionChain.replayArgs().first(),
                instantiation.substitutionChain.replayArgs()[1]
            )
        )
        val pairInstantiation = pairSubstitution.apply(pairType)
        ast.args.forEach {
            checkTypes(it.ctx, prelude, errors, pairInstantiation, it.readType())
        }
    } else {
        ast.args.forEach {
            checkTypes(
                it.ctx,
                prelude,
                errors,
                instantiation.substitutionChain.replayArgs().first(),
                it.readType()
            )
        }
    }
}

private fun <T> transpose(table: List<List<T>>): List<List<T>> {
    val res: MutableList<MutableList<T>> = ArrayList()
    val n: Int = table.first().size
    for (i in 0 until n) {
        val col: MutableList<T> = ArrayList()
        for (row in table) {
            col.add(row[i])
        }
        res.add(col)
    }
    return res
}

fun findBestType(ctx: SourceContext, errors: LanguageErrors, types: List<Type>): Type {
    if (types.isEmpty()) {
        errors.add(ctx, TypeSystemBug)
        return ErrorSymbol
    }
    val first = types.first()
    val firstPath = getQualifiedName(first as Symbol)
    return when (first) {
        is GroundRecordTypeSymbol -> {
            when {
                types.all { it is GroundRecordTypeSymbol && firstPath == getQualifiedName(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorSymbol
                }
            }
        }
        is ObjectSymbol -> {
            when {
                types.all { it is ObjectSymbol && firstPath == getQualifiedName(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorSymbol
                }
            }
        }
        is PlatformObjectSymbol -> {
            when {
                types.all { it is PlatformObjectSymbol && firstPath == getQualifiedName(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorSymbol
                }
            }
        }
        is TypeInstantiation -> {
            when (val parameterizedType = first.substitutionChain.terminus) {
                is ParameterizedRecordTypeSymbol -> {
                    if (types.all {
                            it is TypeInstantiation &&
                                    it.substitutionChain.terminus is ParameterizedRecordTypeSymbol &&
                                    firstPath == getQualifiedName(it.substitutionChain.terminus) &&
                                    first.substitutionChain.replayArgs().size ==
                                    it.substitutionChain.replayArgs().size
                        }) {
                        val typeArgs = transpose(types.map {
                            (it as TypeInstantiation).substitutionChain.replayArgs()
                        }).map {
                            findBestType(ctx, errors, it)
                        }
                        val substitution = Substitution(parameterizedType.typeParams, typeArgs)
                        substitution.apply(parameterizedType)
                    } else {
                        errors.add(ctx, CannotFindBestType(types))
                        ErrorSymbol
                    }
                }
                is ParameterizedBasicTypeSymbol -> {
                    if (types.all {
                            it is TypeInstantiation &&
                                    it.substitutionChain.terminus is ParameterizedBasicTypeSymbol &&
                                    firstPath == getQualifiedName(it.substitutionChain.terminus) &&
                                    first.substitutionChain.replayArgs().size ==
                                    it.substitutionChain.replayArgs().size
                        }) {
                        val typeArgs = transpose(types.map {
                            (it as TypeInstantiation).substitutionChain.replayArgs()
                        }).map {
                            findBestType(ctx, errors, it)
                        }
                        val substitution = Substitution(parameterizedType.typeParams, typeArgs)
                        substitution.apply(parameterizedType)
                    } else {
                        errors.add(ctx, CannotFindBestType(types))
                        ErrorSymbol
                    }
                }
                is CostExpression -> {
                    if (types.all { it is CostExpression }) {
                        MaxCostExpression(
                            types.map { (it as CostExpression) }
                        )
                    } else {
                        errors.add(ctx, CannotFindBestType(types))
                        ErrorSymbol
                    }
                }
            }
        }
        is BasicTypeSymbol -> {
            if (types.all { it is BasicTypeSymbol && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        is StandardTypeParameter -> {
            if (types.all { it is StandardTypeParameter && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        is FinTypeSymbol -> {
            if (types.all { it is FinTypeSymbol }) {
                FinTypeSymbol(types.maxOf { (it as FinTypeSymbol).magnitude })
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        is ImmutableFinTypeParameter -> {
            if (types.all { it is ImmutableFinTypeParameter && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        is MutableFinTypeParameter -> {
            if (types.all { it is MutableFinTypeParameter && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        else -> {
            errors.add(ctx, CannotFindBestType(types))
            ErrorSymbol
        }
    }
}

fun validateSubstitution(
    ctx: SourceContext,
    errors: LanguageErrors,
    typeParameter: TypeParameter,
    substitutedType: Type
) {
    when (typeParameter) {
        is StandardTypeParameter -> when (substitutedType) {
            is GroundRecordTypeSymbol,
            is BasicTypeSymbol,
            is StandardTypeParameter -> Unit
            is TypeInstantiation -> {
                when (val parameterizedType = substitutedType.substitutionChain.terminus) {
                    is ParameterizedBasicTypeSymbol -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(substitutedType))
                    }
                    is ParameterizedRecordTypeSymbol -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(substitutedType))
                    }
                }
            }
            is ObjectSymbol -> if (!substitutedType.featureSupport.typeArg) {
                errors.add(ctx, TypeArgFeatureBan(substitutedType))
            }
            else -> errors.add(ctx, InvalidStandardTypeSub(typeParameter, substitutedType))
        }
        is ImmutableFinTypeParameter -> when (substitutedType) {
            is CostExpression -> Unit
            else -> errors.add(ctx, InvalidFinTypeSub(typeParameter, substitutedType))
        }
        is MutableFinTypeParameter -> when (substitutedType) {
            is FinTypeSymbol -> Unit
            else -> errors.add(ctx, InvalidFinTypeSub(typeParameter, substitutedType))
        }
    }
}