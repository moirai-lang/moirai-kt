package org.shardscript.semantics.core

import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.prelude.Lang

fun filterValidTypes(ctx: SourceContext, errors: LanguageErrors, symbol: Symbol): Symbol =
    when (symbol) {
        ErrorSymbol,
        is GroundRecordTypeSymbol,
        is Namespace,
        is BasicTypeSymbol,
        is ObjectSymbol,
        is StandardTypeParameter,
        is FunctionTypeSymbol -> symbol
        is SymbolInstantiation -> {
            when (symbol.substitutionChain.originalSymbol) {
                is ParameterizedFunctionSymbol,
                is ParameterizedMemberPluginSymbol,
                is ParameterizedStaticPluginSymbol -> {
                    errors.add(ctx, TypeSystemBug)
                    ErrorSymbol
                }
                else -> {
                    symbol.substitutionChain.originalSymbol.typeParams.forEach {
                        validateSubstitution(ctx, errors, it, symbol.substitutionChain.replay(it))
                    }
                    symbol
                }
            }
        }
        is ParameterizedBasicTypeSymbol,
        is ParameterizedRecordTypeSymbol -> {
            errors.add(ctx, CannotUseRawType(symbol))
            ErrorSymbol
        }
        is FinTypeSymbol,
        is ImmutableFinTypeParameter,
        is MutableFinTypeParameter -> {
            errors.add(ctx, ExpectOtherError)
            ErrorSymbol
        }
        is SystemRootNamespace,
        is UserRootNamespace,
        is Block,
        is SumCostExpression,
        is ProductCostExpression,
        is MaxCostExpression,
        is GroundFunctionSymbol,
        is LambdaSymbol,
        is ParameterizedFunctionSymbol,
        is GroundMemberPluginSymbol,
        is ParameterizedMemberPluginSymbol,
        is ParameterizedStaticPluginSymbol,
        is PreludeTable,
        is ImportTable,
        is FunctionFormalParameterSymbol,
        is FieldSymbol,
        is PlatformFieldSymbol,
        is LocalVariableSymbol -> {
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
        is ImmutableFinTypeParameter,
        is MutableFinTypeParameter,
        is GroundMemberPluginSymbol,
        is BasicTypeSymbol,
        is ObjectSymbol,
        is StandardTypeParameter,
        is Namespace,
        is SystemRootNamespace,
        is UserRootNamespace,
        is Block,
        is SumCostExpression,
        is ProductCostExpression,
        is MaxCostExpression,
        is PreludeTable,
        is ImportTable,
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
        is ImmutableFinTypeParameter,
        is MutableFinTypeParameter,
        is BasicTypeSymbol,
        is ObjectSymbol,
        is StandardTypeParameter,
        is Namespace,
        is SystemRootNamespace,
        is UserRootNamespace,
        is Block,
        is SumCostExpression,
        is ProductCostExpression,
        is MaxCostExpression,
        is PreludeTable,
        is ImportTable,
        is FunctionFormalParameterSymbol,
        is FieldSymbol,
        is PlatformFieldSymbol,
        is LambdaSymbol,
        is LocalVariableSymbol -> {
            errors.add(ctx, SymbolCouldNotBeApplied(signifier))
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
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is ParameterizedRecordTypeSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.originalSymbol) {
                is ParameterizedBasicTypeSymbol -> {
                    if (symbol.parent is Symbol) {
                        inlineGeneratePath(symbol.parent, path)
                    }
                    path.add(parameterizedType.identifier.name)
                }
                is ParameterizedRecordTypeSymbol -> {
                    if (symbol.parent is Symbol) {
                        inlineGeneratePath(symbol.parent, path)
                    }
                    path.add(parameterizedType.identifier.name)
                }
                is ParameterizedStaticPluginSymbol -> {
                    if (symbol.parent is Symbol) {
                        inlineGeneratePath(symbol.parent, path)
                    }
                    path.add(parameterizedType.identifier.name)
                }
                else -> Unit
            }
        }
        is ParameterizedBasicTypeSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is ParameterizedStaticPluginSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is Namespace -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is BasicTypeSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is ObjectSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is StandardTypeParameter -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is ImmutableFinTypeParameter -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        is MutableFinTypeParameter -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent, path)
            }
            path.add(symbol.identifier.name)
        }
        else -> Unit
    }
}

fun checkTypes(
    ctx: SourceContext,
    prelude: PreludeTable,
    errors: LanguageErrors,
    expected: Symbol,
    actual: Symbol
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
            val expectedPath = generatePath(expected)
            val actualPath = generatePath(actual)
            if (expectedPath != actualPath) {
                errors.add(ctx, TypeMismatch(expected, actual))
            }
        }
    }
}

fun checkTypes(
    ctx: SourceContext,
    prelude: PreludeTable,
    errors: LanguageErrors,
    expectedTypeArgs: List<Symbol>,
    actualTypeArgs: List<Symbol>
) {
    if (expectedTypeArgs.size != actualTypeArgs.size) {
        errors.add(ctx, TypeSystemBug)
    } else {
        expectedTypeArgs.zip(actualTypeArgs).forEach {
            checkTypes(ctx, prelude, errors, it.first, it.second)
        }
    }
}

fun checkApply(prelude: PreludeTable, errors: LanguageErrors, ast: ApplyAst) {
    when (val symbol = ast.symbolRef) {
        is GroundFunctionSymbol -> {
            checkArgs(prelude, errors, symbol.type(), ast)
        }
        is GroundRecordTypeSymbol -> {
            checkArgs(prelude, errors, symbol, ast)
        }
        is FunctionFormalParameterSymbol -> when (val ofTypeSymbol = symbol.ofTypeSymbol) {
            is GroundFunctionSymbol -> {
                checkArgs(prelude, errors, ofTypeSymbol.type(), ast)
            }
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

fun checkArgs(prelude: PreludeTable, errors: LanguageErrors, type: FunctionTypeSymbol, ast: ApplyAst) {
    if (type.formalParamTypes.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.formalParamTypes.size, ast.args.size))
    } else {
        type.formalParamTypes.zip(ast.args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first, it.second.readType())
        }
        checkTypes(ast.ctx, prelude, errors, type.returnType, ast.readType())
    }
}

fun checkArgs(prelude: PreludeTable, errors: LanguageErrors, type: GroundRecordTypeSymbol, ast: ApplyAst) {
    if (type.fields.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.fields.size, ast.args.size))
    } else {
        type.fields.zip(ast.args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first.ofTypeSymbol, it.second.readType())
        }
    }
}

fun checkArgs(
    prelude: PreludeTable,
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
    prelude: PreludeTable,
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

fun findBestType(ctx: SourceContext, errors: LanguageErrors, types: List<Symbol>): Symbol {
    if (types.isEmpty()) {
        errors.add(ctx, TypeSystemBug)
        return ErrorSymbol
    }
    val first = types.first()
    val firstPath = generatePath(first)
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
                FinTypeSymbol(types.map { (it as FinTypeSymbol).magnitude }.max()!!)
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

fun validateExplicitSymbol(
    ctx: SourceContext,
    errors: LanguageErrors,
    signifier: Signifier,
    scope: Scope<Symbol>
) {
    fun isValidTarget(candidate: Symbol): Boolean {
        return when (candidate) {
            is ParameterizedRecordTypeSymbol -> true
            else -> false
        }
    }
    if (scope is SymbolTableElement) {
        val linearized = linearizeIdentifiers(listOf(signifier))
        linearized.forEach { identifier ->
            if (identifier is Identifier) {
                when (scope.fetch(identifier)) {
                    is StandardTypeParameter,
                    is ImmutableFinTypeParameter,
                    is MutableFinTypeParameter -> {
                        var targetParent: SymbolTableElement = scope
                        while (!isValidTarget(targetParent)) {
                            targetParent = targetParent.parent as SymbolTableElement
                        }
                        when (targetParent) {
                            is ParameterizedRecordTypeSymbol -> {
                                if (!targetParent.existsHere(identifier)) {
                                    errors.add(ctx, ForeignTypeParameter(identifier))
                                }
                            }
                            else -> errors.add(ctx, TypeSystemBug)
                        }
                    }
                    else -> Unit
                }
            }
        }
    } else {
        errors.add(ctx, TypeSystemBug)
    }
}

fun validateSubstitution(
    ctx: SourceContext,
    errors: LanguageErrors,
    typeParameter: TypeParameter,
    substitutedSymbol: Symbol
) {
    when (typeParameter) {
        is StandardTypeParameter -> when (substitutedSymbol) {
            is GroundRecordTypeSymbol,
            is BasicTypeSymbol,
            is StandardTypeParameter -> Unit
            is SymbolInstantiation -> {
                when (val parameterizedType = substitutedSymbol.substitutionChain.originalSymbol) {
                    is ParameterizedBasicTypeSymbol -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(substitutedSymbol))
                    }
                    is ParameterizedRecordTypeSymbol -> if (!parameterizedType.featureSupport.typeArg) {
                        errors.add(ctx, TypeArgFeatureBan(substitutedSymbol))
                    }
                    else -> Unit
                }
            }
            is ObjectSymbol -> if (!substitutedSymbol.featureSupport.typeArg) {
                errors.add(ctx, TypeArgFeatureBan(substitutedSymbol))
            }
            else -> errors.add(ctx, InvalidStandardTypeSub(typeParameter, substitutedSymbol))
        }
        is ImmutableFinTypeParameter -> when (substitutedSymbol) {
            is CostExpression -> Unit
            else -> errors.add(ctx, InvalidFinTypeSub(typeParameter, substitutedSymbol))
        }
        is MutableFinTypeParameter -> when (substitutedSymbol) {
            is FinTypeSymbol -> Unit
            else -> errors.add(ctx, InvalidFinTypeSub(typeParameter, substitutedSymbol))
        }
    }
}