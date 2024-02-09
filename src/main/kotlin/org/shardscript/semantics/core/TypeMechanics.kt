package org.shardscript.semantics.core

import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.prelude.Lang

fun filterValidTypes(ctx: SourceContext, errors: LanguageErrors, type: Type): Type =
    when (type) {
        ErrorType,
        is GroundRecordType,
        is BasicType,
        is ObjectType,
        is PlatformObjectType,
        is StandardTypeParameter,
        is FunctionType -> type

        is TypeInstantiation -> {
            type.substitutionChain.terminus.typeParams.forEach {
                validateSubstitution(ctx, errors, it, type.substitutionChain.replay(it))
            }
            type
        }

        is Fin,
        is ConstantFin,
        is FinTypeParameter -> {
            errors.add(ctx, ExpectOtherError)
            ErrorType
        }

        is ParameterizedBasicType,
        is ParameterizedRecordType -> {
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
        is GroundRecordType,
        is ParameterizedBasicType,
        is ParameterizedRecordType -> type

        is TypeInstantiation -> {
            when (type.substitutionChain.terminus) {
                is ParameterizedBasicType,
                is ParameterizedRecordType -> {
                    type.substitutionChain.terminus.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, type.substitutionChain.replay(it))
                    }
                    type
                }
            }
        }

        is FunctionType,
        is Fin,
        is ConstantFin,
        is FinTypeParameter,
        is BasicType,
        is ObjectType,
        is PlatformObjectType,
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
        is GroundRecordType -> {
            type.qualifiedName
        }

        is ParameterizedRecordType -> {
            type.qualifiedName
        }

        is TypeInstantiation -> {
            when (val parameterizedType = type.substitutionChain.terminus) {
                is ParameterizedBasicType -> {
                    parameterizedType.identifier.name
                }

                is ParameterizedRecordType -> {
                    parameterizedType.qualifiedName
                }
            }
        }

        is ParameterizedBasicType -> {
            type.identifier.name
        }

        is BasicType -> {
            type.identifier.name
        }

        is ObjectType -> {
            type.qualifiedName
        }

        is PlatformObjectType -> {
            type.identifier.name
        }

        is StandardTypeParameter -> {
            type.qualifiedName
        }

        is FinTypeParameter -> {
            type.qualifiedName
        }

        ConstantFin,
        is Fin,
        is MaxCostExpression,
        is ProductCostExpression,
        is SumCostExpression,
        ErrorType,
        is FunctionType -> {
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
        expected is FunctionType && actual is FunctionType -> {
            checkTypes(ctx, prelude, errors, expected.formalParamTypes, actual.formalParamTypes)
            checkTypes(ctx, prelude, errors, expected.returnType, actual.returnType)
        }

        expected is FunctionType && actual !is FunctionType -> {
            errors.add(ctx, TypeMismatch(expected, actual))
        }

        expected !is FunctionType && actual is FunctionType -> {
            errors.add(ctx, TypeMismatch(expected, actual))
        }

        expected is Fin && actual is Fin -> {
            if (actual.magnitude > expected.magnitude) {
                errors.add(ctx, FinMismatch(expected.magnitude, actual.magnitude))
            }
        }

        expected is TypeInstantiation && actual is TypeInstantiation -> {
            val expectedParameterized = expected.substitutionChain.terminus
            val actualParameterized = actual.substitutionChain.terminus
            when {
                expectedParameterized is ParameterizedRecordType && actualParameterized is ParameterizedRecordType -> {
                    checkTypes(ctx, prelude, errors, expectedParameterized, actualParameterized)
                    checkTypes(
                        ctx,
                        prelude,
                        errors,
                        expected.substitutionChain.replayArgs(),
                        actual.substitutionChain.replayArgs()
                    )
                }

                expectedParameterized is ParameterizedBasicType && actualParameterized is ParameterizedBasicType -> {
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

        expected is ConstantFin && actual is ConstantFin -> Unit
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
                is FunctionType -> {
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
                is ParameterizedRecordType -> {
                    checkArgs(prelude, errors, type, parameterizedSymbol, ast, args)
                }

                is ParameterizedBasicType -> {
                    checkArgs(prelude, errors, type, parameterizedSymbol, args)
                }
            }
        }
    }
}

fun checkArgs(prelude: Scope, errors: LanguageErrors, type: FunctionType, ast: Ast, args: List<Ast>) {
    if (type.formalParamTypes.size != args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.formalParamTypes.size, args.size))
    } else {
        type.formalParamTypes.zip(args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first, it.second.readType())
        }
        checkTypes(ast.ctx, prelude, errors, type.returnType, ast.readType())
    }
}

fun checkArgs(prelude: Scope, errors: LanguageErrors, type: GroundRecordType, ast: Ast, args: List<Ast>) {
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
    parameterizedType: ParameterizedRecordType,
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
    parameterizedType: ParameterizedBasicType,
    args: List<Ast>
) {
    if (parameterizedType.identifier == Lang.dictionaryId || parameterizedType.identifier == Lang.mutableDictionaryId) {
        val pairType = prelude.fetchType(Lang.pairId) as ParameterizedRecordType
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
        is GroundRecordType -> {
            val firstPath = getQualifiedName(first)
            when {
                types.all { it is GroundRecordType && firstPath == getQualifiedName(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorType
                }
            }
        }
        is ObjectType -> {
            val firstPath = getQualifiedName(first)
            when {
                types.all { it is ObjectType && firstPath == getQualifiedName(it) } -> {
                    first
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorType
                }
            }
        }
        is PlatformObjectType -> {
            val firstPath = getQualifiedName(first)
            when {
                types.all { it is PlatformObjectType && firstPath == getQualifiedName(it) } -> {
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
                is ParameterizedRecordType -> {
                    if (types.all {
                            it is TypeInstantiation &&
                                    it.substitutionChain.terminus is ParameterizedRecordType &&
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
                is ParameterizedBasicType -> {
                    if (types.all {
                            it is TypeInstantiation &&
                                    it.substitutionChain.terminus is ParameterizedBasicType &&
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
        is BasicType -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is BasicType && firstPath == getQualifiedName(it) }) {
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
        is Fin -> {
            if (types.all { it is Fin }) {
                Fin(types.maxOf { (it as Fin).magnitude })
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorType
            }
        }
        is FinTypeParameter -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is FinTypeParameter && firstPath == getQualifiedName(it) }) {
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
            is GroundRecordType,
            is BasicType,
            is StandardTypeParameter -> Unit
            is TypeInstantiation -> {
                when (val parameterizedType = substitutedType.substitutionChain.terminus) {
                    is ParameterizedBasicType -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(substitutedType))
                    }
                    is ParameterizedRecordType -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(substitutedType))
                    }
                }
            }
            is ObjectType -> if (!substitutedType.featureSupport.typeArg) {
                errors.add(ctx, TypeArgFeatureBan(substitutedType))
            }
            else -> errors.add(ctx, InvalidStandardTypeSub(typeParameter, substitutedType))
        }
        is FinTypeParameter -> when (substitutedType) {
            is CostExpression -> Unit
            else -> errors.add(ctx, InvalidFinTypeSub(typeParameter, substitutedType))
        }
    }
}