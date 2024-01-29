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

        is SymbolInstantiation -> {
            when (type.substitutionChain.originalSymbol) {
                is ParameterizedFunctionSymbol,
                is ParameterizedMemberPluginSymbol,
                is ParameterizedStaticPluginSymbol -> {
                    errors.add(ctx, TypeSystemBug)
                    ErrorSymbol
                }

                else -> {
                    type.substitutionChain.originalSymbol.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, type.substitutionChain.replay(it))
                    }
                    type
                }
            }
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
        is SymbolInstantiation -> {
            when (symbol.substitutionChain.originalSymbol) {
                is ParameterizedStaticPluginSymbol,
                is ParameterizedFunctionSymbol,
                is ParameterizedBasicTypeSymbol,
                is ParameterizedRecordTypeSymbol -> {
                    symbol.substitutionChain.originalSymbol.typeParams.forEach {
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
        is SymbolInstantiation -> {
            when (symbol.substitutionChain.originalSymbol) {
                is ParameterizedStaticPluginSymbol,
                is ParameterizedFunctionSymbol,
                is ParameterizedBasicTypeSymbol,
                is ParameterizedRecordTypeSymbol,
                is ParameterizedMemberPluginSymbol -> {
                    symbol.substitutionChain.originalSymbol.typeParams.forEach {
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

fun generatePath(symbol: Symbol): List<String> {
    val inline: MutableList<String> = ArrayList()
    inlineGeneratePath(symbol, inline)
    return inline.toList()
}

fun inlineGeneratePath(symbol: Symbol, path: MutableList<String>) {
    when (symbol) {
        is GroundRecordTypeSymbol -> {
            path.add(symbol.qualifiedName)
        }
        is ParameterizedRecordTypeSymbol -> {
            path.add(symbol.qualifiedName)
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.originalSymbol) {
                is ParameterizedBasicTypeSymbol -> {
                    path.add(parameterizedType.identifier.name)
                }
                is ParameterizedRecordTypeSymbol -> {
                    path.add(parameterizedType.qualifiedName)
                }
                is ParameterizedStaticPluginSymbol -> {
                    path.add(parameterizedType.identifier.name)
                }
                else -> Unit
            }
        }
        is ParameterizedBasicTypeSymbol -> {
            path.add(symbol.identifier.name)
        }
        is ParameterizedStaticPluginSymbol -> {
            path.add(symbol.identifier.name)
        }
        is BasicTypeSymbol -> {
            path.add(symbol.identifier.name)
        }
        is ObjectSymbol -> {
            path.add(symbol.qualifiedName)
        }
        is PlatformObjectSymbol -> {
            path.add(symbol.identifier.name)
        }
        is StandardTypeParameter -> {
            path.add(symbol.qualifiedName)
        }
        is ImmutableFinTypeParameter -> {
            path.add(symbol.qualifiedName)
        }
        is MutableFinTypeParameter -> {
            path.add(symbol.qualifiedName)
        }
        else -> Unit
    }
}

fun checkTypes(
    ctx: SourceContext,
    prelude: Scope<Symbol>,
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
        expected is SymbolInstantiation && actual is SymbolInstantiation -> {
            val expectedParameterized = expected.substitutionChain.originalSymbol
            val actualParameterized = actual.substitutionChain.originalSymbol
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
            val expectedPath = generatePath(expected as Symbol)
            val actualPath = generatePath(actual as Symbol)
            if (expectedPath != actualPath) {
                errors.add(ctx, TypeMismatch(expected, actual))
            }
        }
    }
}

fun checkTypes(
    ctx: SourceContext,
    prelude: Scope<Symbol>,
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

fun checkApply(prelude: Scope<Symbol>, errors: LanguageErrors, ast: ApplyAst) {
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
        is SymbolInstantiation -> {
            when (val parameterizedSymbol = symbol.substitutionChain.originalSymbol) {
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
                is ParameterizedRecordTypeSymbol -> {
                    checkArgs(prelude, errors, symbol, parameterizedSymbol, ast)
                }
                is ParameterizedBasicTypeSymbol -> {
                    checkArgs(prelude, errors, symbol, parameterizedSymbol, ast)
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

fun checkArgs(prelude: Scope<Symbol>, errors: LanguageErrors, type: FunctionTypeSymbol, ast: ApplyAst) {
    if (type.formalParamTypes.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.formalParamTypes.size, ast.args.size))
    } else {
        type.formalParamTypes.zip(ast.args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first, it.second.readType())
        }
        checkTypes(ast.ctx, prelude, errors, type.returnType, ast.readType())
    }
}

fun checkArgs(prelude: Scope<Symbol>, errors: LanguageErrors, type: GroundRecordTypeSymbol, ast: ApplyAst) {
    if (type.fields.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.fields.size, ast.args.size))
    } else {
        type.fields.zip(ast.args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first.ofTypeSymbol, it.second.readType())
        }
    }
}

fun checkArgs(
    prelude: Scope<Symbol>,
    errors: LanguageErrors,
    instantiation: SymbolInstantiation,
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
    prelude: Scope<Symbol>,
    errors: LanguageErrors,
    instantiation: SymbolInstantiation,
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
    val firstPath = generatePath(first as Symbol)
    return when (first) {
        is GroundRecordTypeSymbol -> {
            when {
                types.all { it is GroundRecordTypeSymbol && firstPath == generatePath(it) } -> {
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
                types.all { it is ObjectSymbol && firstPath == generatePath(it) } -> {
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
                types.all { it is PlatformObjectSymbol && firstPath == generatePath(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorSymbol
                }
            }
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = first.substitutionChain.originalSymbol) {
                is ParameterizedRecordTypeSymbol -> {
                    if (types.all {
                            it is SymbolInstantiation &&
                                    it.substitutionChain.originalSymbol is ParameterizedRecordTypeSymbol &&
                                    firstPath == generatePath(it.substitutionChain.originalSymbol) &&
                                    first.substitutionChain.replayArgs().size ==
                                    it.substitutionChain.replayArgs().size
                        }) {
                        val typeArgs = transpose(types.map {
                            (it as SymbolInstantiation).substitutionChain.replayArgs()
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
                            it is SymbolInstantiation &&
                                    it.substitutionChain.originalSymbol is ParameterizedBasicTypeSymbol &&
                                    firstPath == generatePath(it.substitutionChain.originalSymbol) &&
                                    first.substitutionChain.replayArgs().size ==
                                    it.substitutionChain.replayArgs().size
                        }) {
                        val typeArgs = transpose(types.map {
                            (it as SymbolInstantiation).substitutionChain.replayArgs()
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
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorSymbol
                }
            }
        }
        is BasicTypeSymbol -> {
            if (types.all { it is BasicTypeSymbol && firstPath == generatePath(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        is StandardTypeParameter -> {
            if (types.all { it is StandardTypeParameter && firstPath == generatePath(it) }) {
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
            if (types.all { it is ImmutableFinTypeParameter && firstPath == generatePath(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        is MutableFinTypeParameter -> {
            if (types.all { it is MutableFinTypeParameter && firstPath == generatePath(it) }) {
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
            is SymbolInstantiation -> {
                when (val parameterizedType = substitutedType.substitutionChain.originalSymbol) {
                    is ParameterizedBasicTypeSymbol -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(substitutedType))
                    }
                    is ParameterizedRecordTypeSymbol -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(substitutedType))
                    }
                    else -> Unit
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