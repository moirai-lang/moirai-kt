package moirai.semantics.core

import moirai.semantics.infer.Substitution
import moirai.semantics.prelude.Lang

internal fun filterValidTypes(ctx: SourceContext, errors: LanguageErrors, type: Type): Type =
    when (type) {
        ErrorType,
        is GroundRecordType,
        is BasicType,
        is ObjectType,
        is PlatformObjectType,
        is PlatformSumObjectType,
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
        is ParameterizedRecordType,
        is PlatformSumType,
        is PlatformSumRecordType -> {
            errors.add(ctx, CannotUseRawType(toError(type)))
            ErrorType
        }

        is MaxCostExpression,
        is ProductCostExpression,
        is SumCostExpression -> {
            errors.add(ctx, TypeSystemBug)
            ErrorType
        }
    }

internal fun filterValidGroundApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    symbol: Symbol,
    signifier: Signifier
): Symbol =
    when (symbol) {
        is FunctionFormalParameterSymbol,
        is GroundFunctionSymbol,
        is GroundStaticPluginSymbol,
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
                    errors.add(ctx, SymbolCouldNotBeApplied(toError(signifier)))
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
            errors.add(ctx, SymbolCouldNotBeApplied(toError(signifier)))
            ErrorSymbol
        }
    }

internal fun filterValidGroundApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    type: Type,
    signifier: Signifier
): Type =
    when (type) {
        ErrorType,
        is GroundRecordType,
        is ParameterizedBasicType,
        is ParameterizedRecordType,
        is PlatformSumRecordType -> type

        is TypeInstantiation -> {
            when (type.substitutionChain.terminus) {
                is ParameterizedBasicType,
                is ParameterizedRecordType,
                is PlatformSumRecordType -> {
                    type.substitutionChain.terminus.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, type.substitutionChain.replay(it))
                    }
                    type
                }
                is PlatformSumType -> {
                    errors.add(ctx, SymbolCouldNotBeApplied(toError(signifier)))
                    ErrorType
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
        is PlatformSumObjectType,
        is PlatformSumType,
        is StandardTypeParameter,
        is SumCostExpression,
        is ProductCostExpression,
        is MaxCostExpression -> {
            errors.add(ctx, SymbolCouldNotBeApplied(toError(signifier)))
            ErrorType
        }
    }

internal fun filterValidDotApply(
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
                    errors.add(ctx, SymbolCouldNotBeApplied(toError(signifier)))
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
        is GroundStaticPluginSymbol,
        is ParameterizedStaticPluginSymbol,
        is ParameterizedFunctionSymbol -> {
            errors.add(ctx, SymbolCouldNotBeApplied(toError(signifier)))
            ErrorSymbol
        }
    }

internal fun getQualifiedName(type: Type): String {
    return when (type) {
        is GroundRecordType -> {
            type.qualifiedName
        }

        is ParameterizedRecordType -> {
            type.qualifiedName
        }

        is PlatformSumType -> {
            type.identifier.name
        }

        is PlatformSumRecordType -> {
            type.identifier.name
        }

        is TypeInstantiation -> {
            when (val parameterizedType = type.substitutionChain.terminus) {
                is ParameterizedBasicType -> {
                    parameterizedType.identifier.name
                }

                is ParameterizedRecordType -> {
                    parameterizedType.qualifiedName
                }

                is PlatformSumType -> {
                    parameterizedType.identifier.name
                }

                is PlatformSumRecordType -> {
                    parameterizedType.identifier.name
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

        is PlatformSumObjectType -> {
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

internal fun checkTypes(
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
            errors.add(ctx, TypeMismatch(toError(expected), toError(actual)))
        }

        expected !is FunctionType && actual is FunctionType -> {
            errors.add(ctx, TypeMismatch(toError(expected), toError(actual)))
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

                expectedParameterized is PlatformSumRecordType && actualParameterized is PlatformSumRecordType -> {
                    checkTypes(ctx, prelude, errors, expectedParameterized, actualParameterized)
                    checkTypes(
                        ctx,
                        prelude,
                        errors,
                        expected.substitutionChain.replayArgs(),
                        actual.substitutionChain.replayArgs()
                    )
                }

                expectedParameterized is PlatformSumType && actualParameterized is PlatformSumType -> {
                    checkTypes(ctx, prelude, errors, expectedParameterized, actualParameterized)
                    checkTypes(
                        ctx,
                        prelude,
                        errors,
                        expected.substitutionChain.replayArgs(),
                        actual.substitutionChain.replayArgs()
                    )
                }

                expectedParameterized is PlatformSumType && actualParameterized is PlatformSumRecordType -> {
                    checkTypes(ctx, prelude, errors, expectedParameterized, actualParameterized)
                    val allArgs = expectedParameterized.typeParams.zip(expected.substitutionChain.replayArgs()).toMap()
                    val selectedArgs = actualParameterized.typeParams.map { allArgs[it]!! }
                    checkTypes(
                        ctx,
                        prelude,
                        errors,
                        selectedArgs,
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

        expected is PlatformSumType && actual is PlatformSumRecordType -> {
            val expectedPath = getQualifiedName(expected)
            val actualPath = getQualifiedName(actual.sumType)
            if (expectedPath != actualPath) {
                errors.add(ctx, TypeMismatch(toError(expected), toError(actual)))
            }
        }

        expected is TypeInstantiation && actual is PlatformSumObjectType -> {
            val expectedPath = getQualifiedName(expected.substitutionChain.terminus)
            val actualPath = getQualifiedName(actual.sumType)
            if (expectedPath != actualPath) {
                errors.add(ctx, TypeMismatch(toError(expected), toError(actual)))
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
                errors.add(ctx, TypeMismatch(toError(expected), toError(actual)))
            }
        }
    }
}

internal fun checkTypes(
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

internal fun checkApply(prelude: Scope, errors: LanguageErrors, ast: DotApplyAst) {
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

internal fun checkApply(prelude: Scope, errors: LanguageErrors, ast: GroundApplyAst, args: List<Ast>) {
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

        is GroundApplySlotGSPS -> {
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
                    checkFields(prelude, errors, type, parameterizedSymbol.fields, ast, args)
                }

                is ParameterizedBasicType -> {
                    checkArgs(prelude, errors, type, parameterizedSymbol, args)
                }

                is PlatformSumRecordType -> {
                    checkFields(prelude, errors, type, parameterizedSymbol.fields, ast, args)
                }

                is PlatformSumType -> errors.add(ast.ctx, TypeSystemBug)
            }
        }
    }
}

internal fun checkArgs(prelude: Scope, errors: LanguageErrors, type: FunctionType, ast: Ast, args: List<Ast>) {
    if (type.formalParamTypes.size != args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.formalParamTypes.size, args.size))
    } else {
        type.formalParamTypes.zip(args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first, it.second.readType())
        }
        checkTypes(ast.ctx, prelude, errors, type.returnType, ast.readType())
    }
}

internal fun checkArgs(prelude: Scope, errors: LanguageErrors, type: GroundRecordType, ast: Ast, args: List<Ast>) {
    if (type.fields.size != args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.fields.size, args.size))
    } else {
        type.fields.zip(args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first.ofTypeSymbol, it.second.readType())
        }
    }
}

internal fun checkFields(
    prelude: Scope,
    errors: LanguageErrors,
    instantiation: TypeInstantiation,
    fields: List<FieldSymbol>,
    ast: Ast,
    args: List<Ast>
) {
    if (fields.size != args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(fields.size, args.size))
    } else {
        fields.zip(args).forEach {
            val ofTypeSymbol = instantiation.substitutionChain.replay(it.first.ofTypeSymbol)
            checkTypes(it.second.ctx, prelude, errors, ofTypeSymbol, it.second.readType())
        }
    }
}

internal fun checkArgs(
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

internal fun findBestType(ctx: SourceContext, errors: LanguageErrors, types: List<Type>): Type {
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
                    errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
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
                    errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
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
                    errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
                    ErrorType
                }
            }
        }

        is PlatformSumObjectType -> {
            val sumPath = getQualifiedName(first.sumType)
            handleSumType(types, sumPath, ctx, errors)
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
                        errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
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
                        errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
                        ErrorType
                    }
                }

                is CostExpression -> {
                    if (types.all { it is CostExpression }) {
                        MaxCostExpression(
                            types.map { (it as CostExpression) }
                        )
                    } else {
                        errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
                        ErrorType
                    }
                }

                is PlatformSumRecordType -> {
                    val sumPath = getQualifiedName(parameterizedType.sumType)
                    handleSumType(types, sumPath, ctx, errors)
                }

                is PlatformSumType -> {
                    val sumPath = getQualifiedName(parameterizedType)
                    handleSumType(types, sumPath, ctx, errors)
                }
            }
        }

        is BasicType -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is BasicType && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
                ErrorType
            }
        }

        is StandardTypeParameter -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is StandardTypeParameter && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
                ErrorType
            }
        }

        is Fin -> {
            if (types.all { it is Fin }) {
                Fin(types.maxOf { (it as Fin).magnitude })
            } else {
                errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
                ErrorType
            }
        }

        is FinTypeParameter -> {
            val firstPath = getQualifiedName(first)
            if (types.all { it is FinTypeParameter && firstPath == getQualifiedName(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
                ErrorType
            }
        }

        else -> {
            errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
            ErrorType
        }
    }
}

private fun handleSumType(
    types: List<Type>,
    sumPath: String,
    ctx: SourceContext,
    errors: LanguageErrors
): Type {
    return if (types.any { isCorrectSumRecord(it, sumPath) || isCorrectSumType(it, sumPath) }) {
        if (types.all {
                it is PlatformSumObjectType && sumPath == getQualifiedName(it.sumType) || isCorrectSumRecord(
                    it,
                    sumPath
                ) || isCorrectSumType(it, sumPath)
            }) {
            // We know that we have nothing but objects and instantiations of compatible sum types
            val instantiations = types.filterIsInstance<TypeInstantiation>()
            val firstTerminus = instantiations.first().substitutionChain.terminus
            val sumType = if (firstTerminus is PlatformSumType) {
                firstTerminus
            } else {
                (firstTerminus as PlatformSumRecordType).sumType
            }

            val sumTypeParamTable = sumType.typeParams.associateWith { mutableListOf<Type>() }
            instantiations.map {
                it.substitutionChain.terminus.typeParams.zip(it.substitutionChain.replayArgs())
            }.forEach { allSubs ->
                allSubs.forEach { thisSub -> sumTypeParamTable[thisSub.first]!!.add(thisSub.second) }
            }

            val typeArgs = sumType.typeParams.map {
                findBestType(ctx, errors, sumTypeParamTable[it]!!.toList())
            }
            val substitution = Substitution(sumType.typeParams, typeArgs)
            substitution.apply(sumType)
        } else {
            errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
            ErrorType
        }
    } else {
        errors.add(ctx, CannotFindBestType(types.map { toError(it) }))
        ErrorType
    }
}

private fun isCorrectSumType(it: Type, sumPath: String): Boolean =
    it is TypeInstantiation && it.substitutionChain.terminus is PlatformSumType && sumPath == getQualifiedName(
        it.substitutionChain.terminus
    )

private fun isCorrectSumRecord(
    it: Type,
    sumPath: String
): Boolean = it is TypeInstantiation && it.substitutionChain.terminus is PlatformSumRecordType &&
        sumPath == getQualifiedName(it.substitutionChain.terminus.sumType)

internal fun validateSubstitution(
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
                        errors.add(ctx, TypeArgFeatureBan(toError(substitutedType)))
                    }

                    is ParameterizedRecordType -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(toError(substitutedType)))
                    }

                    is PlatformSumRecordType -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(toError(substitutedType)))
                    }

                    is PlatformSumType -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(toError(substitutedType)))
                    }
                }
            }
            is ObjectType -> if (!substitutedType.featureSupport.typeArg) {
                errors.add(ctx, TypeArgFeatureBan(toError(substitutedType)))
            }
            is PlatformObjectType -> if (!substitutedType.featureSupport.typeArg) {
                errors.add(ctx, TypeArgFeatureBan(toError(substitutedType)))
            }
            is PlatformSumObjectType -> if (!substitutedType.featureSupport.typeArg) {
                errors.add(ctx, TypeArgFeatureBan(toError(substitutedType)))
            }
            else -> errors.add(ctx, InvalidStandardTypeSub(toError(typeParameter), toError(substitutedType)))
        }
        is FinTypeParameter -> when (substitutedType) {
            is CostExpression -> Unit
            else -> errors.add(ctx, InvalidFinTypeSub(toError(typeParameter), toError(substitutedType)))
        }
    }
}