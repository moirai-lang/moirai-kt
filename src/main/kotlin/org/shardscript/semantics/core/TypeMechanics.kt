package org.shardscript.semantics.core

import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.prelude.Lang

fun filterValidTypes(ctx: SourceContext, errors: LanguageErrors, type: Type): Type =
    when (type) {
        ErrorType,
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
            ErrorType
        }

        is ParameterizedBasicTypeSymbol,
        is ParameterizedRecordTypeSymbol -> {
            errors.add(ctx, CannotUseRawType(type))
            ErrorType
        }

        is MaxCostExpression,
        is ProductCostExpression,
        is SumCostExpression -> {
            errors.add(ctx, TypeSystemBug)
            ErrorType
        }
    }

fun filterValidGroundApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    symbol: Symbol,
    signifier: Signifier
): Symbol =
    when (symbol) {
        is FunctionFormalParameterSymbol,
        is GroundFunctionSymbol,
        is ParameterizedStaticPluginSymbol,
        is ParameterizedFunctionSymbol -> symbol

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

        is ErrorSymbol,
        is TypePlaceholder,
        is ParameterizedMemberPluginSymbol,
        is GroundMemberPluginSymbol,
        is FieldSymbol,
        is PlatformFieldSymbol,
        is LambdaSymbol,
        is LocalVariableSymbol -> {
            errors.add(ctx, SymbolCouldNotBeApplied(signifier))
            ErrorSymbol
        }
    }

fun filterValidGroundApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    type: Type,
    signifier: Signifier
): Type =
    when (type) {
        ErrorType,
        is GroundRecordTypeSymbol,
        is ParameterizedBasicTypeSymbol,
        is ParameterizedRecordTypeSymbol -> type

        is TypeInstantiation -> {
            when (type.substitutionChain.terminus) {
                is ParameterizedBasicTypeSymbol,
                is ParameterizedRecordTypeSymbol -> {
                    type.substitutionChain.terminus.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, type.substitutionChain.replay(it))
                    }
                    type
                }
            }
        }

        is FunctionTypeSymbol,
        is FinTypeSymbol,
        is ConstantFinTypeSymbol,
        is ImmutableFinTypeParameter,
        is MutableFinTypeParameter,
        is BasicTypeSymbol,
        is ObjectSymbol,
        is PlatformObjectSymbol,
        is StandardTypeParameter,
        is SumCostExpression,
        is ProductCostExpression,
        is MaxCostExpression -> {
            errors.add(ctx, SymbolCouldNotBeApplied(signifier))
            ErrorType
        }
    }

fun filterValidDotApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    symbol: Symbol,
    signifier: Signifier
): Symbol =
    when (symbol) {
        is GroundFunctionSymbol,
        is GroundMemberPluginSymbol -> symbol

        is SymbolInstantiation -> {
            when (symbol.substitutionChain.terminus) {
                is ParameterizedMemberPluginSymbol -> {
                    symbol.substitutionChain.terminus.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, symbol.substitutionChain.replay(it))
                    }
                    symbol
                }

                is ParameterizedStaticPluginSymbol,
                is ParameterizedFunctionSymbol -> {
                    errors.add(ctx, SymbolCouldNotBeApplied(signifier))
                    ErrorSymbol
                }
            }
        }

        is ErrorSymbol,
        is TypePlaceholder,
        is ParameterizedMemberPluginSymbol,
        is FunctionFormalParameterSymbol,
        is FieldSymbol,
        is PlatformFieldSymbol,
        is LambdaSymbol,
        is LocalVariableSymbol,
        is ParameterizedStaticPluginSymbol,
        is ParameterizedFunctionSymbol -> {
            errors.add(ctx, SymbolCouldNotBeApplied(signifier))
            ErrorSymbol
        }
    }

fun getQualifiedName(type: Type): String {
    return when (type) {
        is GroundRecordTypeSymbol -> {
            type.qualifiedName
        }

        is ParameterizedRecordTypeSymbol -> {
            type.qualifiedName
        }

        is TypeInstantiation -> {
            when (val parameterizedType = type.substitutionChain.terminus) {
                is ParameterizedBasicTypeSymbol -> {
                    parameterizedType.identifier.name
                }

                is ParameterizedRecordTypeSymbol -> {
                    parameterizedType.qualifiedName
                }
            }
        }

        is ParameterizedBasicTypeSymbol -> {
            type.identifier.name
        }

        is BasicTypeSymbol -> {
            type.identifier.name
        }

        is ObjectSymbol -> {
            type.qualifiedName
        }

        is PlatformObjectSymbol -> {
            type.identifier.name
        }

        is StandardTypeParameter -> {
            type.qualifiedName
        }

        is ImmutableFinTypeParameter -> {
            type.qualifiedName
        }

        is MutableFinTypeParameter -> {
            type.qualifiedName
        }

        ConstantFinTypeSymbol,
        is FinTypeSymbol,
        is MaxCostExpression,
        is ProductCostExpression,
        is SumCostExpression,
        ErrorType,
        is FunctionTypeSymbol -> {
            langThrow(TypeSystemBug)
        }
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

        expected is ConstantFinTypeSymbol && actual is ConstantFinTypeSymbol -> Unit
        expected is MaxCostExpression && actual is MaxCostExpression -> {
            checkTypes(ctx, prelude, errors, expected.children, actual.children)
        }

        expected is ProductCostExpression && actual is ProductCostExpression -> {
            checkTypes(ctx, prelude, errors, expected.children, actual.children)
        }

        expected is SumCostExpression && actual is SumCostExpression -> {
            checkTypes(ctx, prelude, errors, expected.children, actual.children)
        }

        // We seem to hit this case during string interpolation, and the actual resolution happens during
        // the CostExpressionAstVisitor phase
        expected is CostExpression && actual is CostExpression -> Unit

        else -> {
            val expectedPath = getQualifiedName(expected)
            val actualPath = getQualifiedName(actual)
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

fun checkApply(prelude: Scope, errors: LanguageErrors, ast: DotApplyAst) {
    when (val dotApplySlot = ast.dotApplySlot) {
        is DotApplySlotGF -> {
            checkArgs(prelude, errors, dotApplySlot.payload.type(), ast, ast.args)
        }

        is DotApplySlotGMP -> {
            checkArgs(prelude, errors, dotApplySlot.payload.type(), ast, ast.args)
        }

        is DotApplySlotSI -> {
            val symbol = dotApplySlot.payload
            when (val parameterizedSymbol = symbol.substitutionChain.terminus) {
                is ParameterizedMemberPluginSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast,
                        ast.args
                    )
                }

                is ParameterizedFunctionSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast,
                        ast.args
                    )
                }

                is ParameterizedStaticPluginSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast,
                        ast.args
                    )
                }
            }
        }

        else -> Unit
    }
}

fun checkApply(prelude: Scope, errors: LanguageErrors, ast: GroundApplyAst, args: List<Ast>) {
    when (val groundApplySlot = ast.groundApplySlot) {
        GroundApplySlotError -> langThrow(NotInSource, TypeSystemBug)
        is GroundApplySlotFormal -> {
            val symbol = groundApplySlot.payload
            when (val ofTypeSymbol = symbol.ofTypeSymbol) {
                is FunctionTypeSymbol -> {
                    checkArgs(prelude, errors, ofTypeSymbol, ast, args)
                }

                else -> errors.add(ast.ctx, TypeSystemBug)
            }
        }

        is GroundApplySlotGF -> {
            val symbol = groundApplySlot.payload
            checkArgs(prelude, errors, symbol.type(), ast, args)
        }

        is GroundApplySlotGRT -> {
            val type = groundApplySlot.payload
            checkArgs(prelude, errors, type, ast, args)
        }

        is GroundApplySlotSI -> {
            val symbol = groundApplySlot.payload
            when (val parameterizedSymbol = symbol.substitutionChain.terminus) {
                is ParameterizedMemberPluginSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast,
                        args
                    )
                }

                is ParameterizedFunctionSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast,
                        args
                    )
                }

                is ParameterizedStaticPluginSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()),
                        ast,
                        args
                    )
                }
            }
        }

        is GroundApplySlotTI -> {
            val type = groundApplySlot.payload
            when (val parameterizedSymbol = type.substitutionChain.terminus) {
                is ParameterizedRecordTypeSymbol -> {
                    checkArgs(prelude, errors, type, parameterizedSymbol, ast, args)
                }

                is ParameterizedBasicTypeSymbol -> {
                    checkArgs(prelude, errors, type, parameterizedSymbol, args)
                }
            }
        }
    }
}

fun checkArgs(prelude: Scope, errors: LanguageErrors, type: FunctionTypeSymbol, ast: Ast, args: List<Ast>) {
    if (type.formalParamTypes.size != args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.formalParamTypes.size, args.size))
    } else {
        type.formalParamTypes.zip(args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first, it.second.readType())
        }
        checkTypes(ast.ctx, prelude, errors, type.returnType, ast.readType())
    }
}

fun checkArgs(prelude: Scope, errors: LanguageErrors, type: GroundRecordTypeSymbol, ast: Ast, args: List<Ast>) {
    if (type.fields.size != args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.fields.size, args.size))
    } else {
        type.fields.zip(args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first.ofTypeSymbol, it.second.readType())
        }
    }
}

fun checkArgs(
    prelude: Scope,
    errors: LanguageErrors,
    instantiation: TypeInstantiation,
    parameterizedType: ParameterizedRecordTypeSymbol,
    ast: Ast,
    args: List<Ast>
) {
    if (parameterizedType.fields.size != args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(parameterizedType.fields.size, args.size))
    } else {
        parameterizedType.fields.zip(args).forEach {
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
    args: List<Ast>
) {
    if (parameterizedType.identifier == Lang.dictionaryId || parameterizedType.identifier == Lang.mutableDictionaryId) {
        val pairType = prelude.fetchType(Lang.pairId) as ParameterizedRecordTypeSymbol
        val pairSubstitution = Substitution(
            pairType.typeParams,
            listOf(
                instantiation.substitutionChain.replayArgs().first(),
                instantiation.substitutionChain.replayArgs()[1]
            )
        )
        val pairInstantiation = pairSubstitution.apply(pairType)
        args.forEach {
            checkTypes(it.ctx, prelude, errors, pairInstantiation, it.readType())
        }
    } else {
        args.forEach {
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
        return ErrorType
    }
    return when (val first = types.first()) {
        is GroundRecordTypeSymbol -> {
            val firstPath = getQualifiedName(first)
            when {
                types.all { it is GroundRecordTypeSymbol && firstPath == getQualifiedName(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorType
                }
            }
        }
        is ObjectSymbol -> {
            val firstPath = getQualifiedName(first)
            when {
                types.all { it is ObjectSymbol && firstPath == getQualifiedName(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorType
                }
            }
        }
        is PlatformObjectSymbol -> {
            val firstPath = getQualifiedName(first)
            when {
                types.all { it is PlatformObjectSymbol && firstPath == getQualifiedName(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorType
                }
            }
        }
        is TypeInstantiation -> {
            val firstPath = getQualifiedName(first)
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
                        ErrorType
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
                        ErrorType
                    }
                }
                is CostExpression -> {
                    if (types.all { it is CostExpression }) {
                        MaxCostExpression(
                            types.map { (it as CostExpression) }
                        )
                    } else {
                        errors.add(ctx, CannotFindBestType(types))
                        ErrorType
                    }
                }
            }
        }
        is BasicTypeSymbol -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is BasicTypeSymbol && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorType
            }
        }
        is StandardTypeParameter -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is StandardTypeParameter && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorType
            }
        }
        is FinTypeSymbol -> {
            if (types.all { it is FinTypeSymbol }) {
                FinTypeSymbol(types.maxOf { (it as FinTypeSymbol).magnitude })
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorType
            }
        }
        is ImmutableFinTypeParameter -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is ImmutableFinTypeParameter && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorType
            }
        }
        is MutableFinTypeParameter -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is MutableFinTypeParameter && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorType
            }
        }
        else -> {
            errors.add(ctx, CannotFindBestType(types))
            ErrorType
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