package com.tsikhe.shardscript.semantics.core

import com.tsikhe.shardscript.semantics.infer.Substitution
import com.tsikhe.shardscript.semantics.prelude.Lang

internal fun filterValidTypes(ctx: SourceContext, errors: LanguageErrors, symbol: Symbol): Symbol =
    when (symbol) {
        ErrorSymbol,
        is GroundRecordTypeSymbol,
        is GroundCoproductSymbol,
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
        is ParameterizedCoproductSymbol,
        is ParameterizedRecordTypeSymbol -> {
            errors.add(ctx, CannotUseRawType(symbol))
            ErrorSymbol
        }
        is OmicronTypeSymbol,
        is ImmutableOmicronTypeParameter,
        is MutableOmicronTypeParameter -> {
            errors.add(ctx, ExpectOtherError)
            ErrorSymbol
        }
        NullSymbolTable,
        is SystemRootNamespace,
        is UserRootNamespace,
        is Block,
        is SumCostExpression,
        is ProductCostExpression,
        is MaxCostExpression,
        is GroundFunctionSymbol,
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

internal fun filterValidGroundApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    symbol: Symbol,
    identifier: Identifier
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
                    errors.add(ctx, SymbolCouldNotBeApplied(identifier))
                    ErrorSymbol
                }
            }
        }
        is FunctionTypeSymbol,
        is ParameterizedMemberPluginSymbol,
        is ParameterizedCoproductSymbol,
        is OmicronTypeSymbol,
        is ImmutableOmicronTypeParameter,
        is MutableOmicronTypeParameter,
        NullSymbolTable,
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
        is GroundCoproductSymbol,
        is PreludeTable,
        is ImportTable,
        is FieldSymbol,
        is PlatformFieldSymbol,
        is LocalVariableSymbol -> {
            errors.add(ctx, SymbolCouldNotBeApplied(identifier))
            ErrorSymbol
        }
    }

internal fun filterValidDotApply(
    ctx: SourceContext,
    errors: LanguageErrors,
    symbol: Symbol,
    identifier: Identifier
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
                else -> {
                    errors.add(ctx, SymbolCouldNotBeApplied(identifier))
                    ErrorSymbol
                }
            }
        }
        is FunctionTypeSymbol,
        is ParameterizedMemberPluginSymbol,
        is ParameterizedCoproductSymbol,
        is OmicronTypeSymbol,
        is ImmutableOmicronTypeParameter,
        is MutableOmicronTypeParameter,
        NullSymbolTable,
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
        is GroundCoproductSymbol,
        is PreludeTable,
        is ImportTable,
        is FunctionFormalParameterSymbol,
        is FieldSymbol,
        is PlatformFieldSymbol,
        is LocalVariableSymbol -> {
            errors.add(ctx, SymbolCouldNotBeApplied(identifier))
            ErrorSymbol
        }
    }

fun generatePath(symbol: Symbol): List<String> {
    val inline: MutableList<String> = ArrayList()
    inlineGeneratePath(symbol, inline)
    return inline.toList()
}

internal fun inlineGeneratePath(symbol: Symbol, path: MutableList<String>) {
    when (symbol) {
        is GroundRecordTypeSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is ParameterizedRecordTypeSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.originalSymbol) {
                is ParameterizedBasicTypeSymbol -> {
                    if (symbol.parent is Symbol) {
                        inlineGeneratePath(symbol.parent as Symbol, path)
                    }
                    path.add(parameterizedType.gid.name)
                }
                is ParameterizedRecordTypeSymbol -> {
                    if (symbol.parent is Symbol) {
                        inlineGeneratePath(symbol.parent as Symbol, path)
                    }
                    path.add(parameterizedType.gid.name)
                }
                is ParameterizedCoproductSymbol -> {
                    if (symbol.parent is Symbol) {
                        inlineGeneratePath(symbol.parent as Symbol, path)
                    }
                    path.add(parameterizedType.gid.name)
                }
                is ParameterizedStaticPluginSymbol -> {
                    if (symbol.parent is Symbol) {
                        inlineGeneratePath(symbol.parent as Symbol, path)
                    }
                    path.add(parameterizedType.gid.name)
                }
                else -> Unit
            }
        }
        is ParameterizedBasicTypeSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is ParameterizedStaticPluginSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is Namespace -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is BasicTypeSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is ObjectSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is ParameterizedCoproductSymbol -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is StandardTypeParameter -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is ImmutableOmicronTypeParameter -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        is MutableOmicronTypeParameter -> {
            if (symbol.parent is Symbol) {
                inlineGeneratePath(symbol.parent as Symbol, path)
            }
            path.add(symbol.gid.name)
        }
        else -> Unit
    }
}

internal fun checkTypes(
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
        expected is OmicronTypeSymbol && actual is OmicronTypeSymbol -> {
            if (actual.magnitude > expected.magnitude) {
                errors.add(ctx, OmicronMismatch(expected.magnitude, actual.magnitude))
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
                expectedParameterized is ParameterizedCoproductSymbol && actualParameterized is ParameterizedCoproductSymbol -> {
                    checkTypes(ctx, prelude, errors, expectedParameterized, actualParameterized)
                    checkTypes(
                        ctx,
                        prelude,
                        errors,
                        expected.substitutionChain.replayArgs(),
                        actual.substitutionChain.replayArgs()
                    )
                }
                expectedParameterized is ParameterizedCoproductSymbol && actualParameterized is ParameterizedRecordTypeSymbol -> {
                    if (expectedParameterized.existsHere(actualParameterized.gid)) {
                        val member = expectedParameterized.fetchHere(actualParameterized.gid)
                        if (member is ParameterizedRecordTypeSymbol) {
                            checkTypes(ctx, prelude, errors, member, actualParameterized)
                            member.typeParams.map {
                                expected.substitutionChain.replay(it)
                            }.zip(actual.substitutionChain.replayArgs()).forEach {
                                checkTypes(ctx, prelude, errors, it.first, it.second)
                            }
                        } else {
                            errors.add(ctx, TypeMismatch(expectedParameterized, actualParameterized))
                        }
                    } else {
                        errors.add(ctx, TypeMismatch(expectedParameterized, actualParameterized))
                    }
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
        expected is SymbolInstantiation && actual is ObjectSymbol -> {
            when (val expectedParameterized = expected.substitutionChain.originalSymbol) {
                is ParameterizedCoproductSymbol -> {
                    if (expectedParameterized.existsHere(actual.gid)) {
                        val member = expectedParameterized.fetchHere(actual.gid)
                        if (member is ObjectSymbol) {
                            checkTypes(ctx, prelude, errors, member, actual)
                        } else {
                            errors.add(ctx, TypeMismatch(expectedParameterized, actual))
                        }
                    } else {
                        errors.add(ctx, TypeMismatch(expectedParameterized, actual))
                    }
                }
                else -> Unit
            }
        }
        expected is GroundCoproductSymbol && actual is GroundRecordTypeSymbol -> {
            if (expected.existsHere(actual.gid)) {
                val member = expected.fetchHere(actual.gid)
                if (member is GroundRecordTypeSymbol) {
                    checkTypes(ctx, prelude, errors, member, actual)
                } else {
                    errors.add(ctx, TypeMismatch(expected, actual))
                }
            } else {
                errors.add(ctx, TypeMismatch(expected, actual))
            }
        }
        expected is GroundCoproductSymbol && actual is ObjectSymbol -> {
            if (expected.existsHere(actual.gid)) {
                val member = expected.fetchHere(actual.gid)
                if (member is ObjectSymbol) {
                    checkTypes(ctx, prelude, errors, member, actual)
                } else {
                    errors.add(ctx, TypeMismatch(expected, actual))
                }
            } else {
                errors.add(ctx, TypeMismatch(expected, actual))
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

internal fun checkTypes(
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

internal fun checkApply(prelude: PreludeTable, errors: LanguageErrors, ast: ApplyAst) {
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
                        symbol.substitutionChain.replay(parameterizedSymbol.type()) as FunctionTypeSymbol,
                        ast
                    )
                }
                is ParameterizedFunctionSymbol -> {
                    checkArgs(
                        prelude,
                        errors,
                        symbol.substitutionChain.replay(parameterizedSymbol.type()) as FunctionTypeSymbol,
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
                        symbol.substitutionChain.replay(parameterizedSymbol.type()) as FunctionTypeSymbol,
                        ast
                    )
                }
            }
        }
        else -> errors.add(ast.ctx, TypeSystemBug)
    }
}

internal fun checkArgs(prelude: PreludeTable, errors: LanguageErrors, type: FunctionTypeSymbol, ast: ApplyAst) {
    if (type.formalParamTypes.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.formalParamTypes.size, ast.args.size))
    } else {
        type.formalParamTypes.zip(ast.args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first, it.second.readType())
        }
        checkTypes(ast.ctx, prelude, errors, type.returnType, ast.readType())
    }
}

internal fun checkArgs(prelude: PreludeTable, errors: LanguageErrors, type: GroundRecordTypeSymbol, ast: ApplyAst) {
    if (type.fields.size != ast.args.size) {
        errors.add(ast.ctx, IncorrectNumberOfArgs(type.fields.size, ast.args.size))
    } else {
        type.fields.zip(ast.args).forEach {
            checkTypes(it.second.ctx, prelude, errors, it.first.ofTypeSymbol, it.second.readType())
        }
    }
}

internal fun checkArgs(
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

internal fun checkArgs(
    prelude: PreludeTable,
    errors: LanguageErrors,
    instantiation: SymbolInstantiation,
    parameterizedType: ParameterizedBasicTypeSymbol,
    ast: ApplyAst
) {
    if (parameterizedType.gid == Lang.dictionaryId || parameterizedType.gid == Lang.mutableDictionaryId) {
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

private fun extractCommonCoproduct(
    types: List<Symbol>,
    errors: LanguageErrors,
    ctx: SourceContext
): Symbol {
    val groundCoproducts: MutableList<Symbol> = ArrayList()
    types.forEach {
        when (it) {
            is GroundRecordTypeSymbol -> if (it.parent is GroundCoproductSymbol) {
                groundCoproducts.add(it.parent as GroundCoproductSymbol)
            } else {
                errors.add(ctx, CannotFindBestType(types))
            }
            is ObjectSymbol -> if (it.parent is GroundCoproductSymbol) {
                groundCoproducts.add(it.parent as GroundCoproductSymbol)
            } else {
                errors.add(ctx, CannotFindBestType(types))
            }
            is GroundCoproductSymbol -> groundCoproducts.add(it)
            else -> errors.add(ctx, TypeSystemBug)
        }
    }
    return if (groundCoproducts.isNotEmpty()) {
        val firstCoproduct = groundCoproducts.first()
        val firstCoproductPath = generatePath(firstCoproduct)
        if (groundCoproducts.all { firstCoproductPath == generatePath(it) }) {
            firstCoproduct
        } else {
            errors.add(ctx, CannotFindBestType(types))
            ErrorSymbol
        }
    } else {
        errors.add(ctx, CannotFindBestType(types))
        ErrorSymbol
    }
}

internal fun findBestType(ctx: SourceContext, errors: LanguageErrors, types: List<Symbol>): Symbol {
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
                types.all { it is GroundRecordTypeSymbol || it is ObjectSymbol || it is GroundCoproductSymbol } -> {
                    extractCommonCoproduct(types, errors, ctx)
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
                types.all { it is GroundRecordTypeSymbol || it is ObjectSymbol || it is GroundCoproductSymbol } -> {
                    extractCommonCoproduct(types, errors, ctx)
                }
                else -> {
                    errors.add(ctx, CannotFindBestType(types))
                    ErrorSymbol
                }
            }
        }
        is GroundCoproductSymbol -> {
            if (types.all { it is GroundCoproductSymbol && firstPath == generatePath(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
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
                is ParameterizedCoproductSymbol -> {
                    if (types.all {
                            it is SymbolInstantiation &&
                                    it.substitutionChain.originalSymbol is ParameterizedCoproductSymbol &&
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
        is OmicronTypeSymbol -> {
            if (types.all { it is OmicronTypeSymbol }) {
                OmicronTypeSymbol(types.map { (it as OmicronTypeSymbol).magnitude }.max()!!)
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        is ImmutableOmicronTypeParameter -> {
            if (types.all { it is ImmutableOmicronTypeParameter && firstPath == generatePath(it) }) {
                first
            } else {
                errors.add(ctx, CannotFindBestType(types))
                ErrorSymbol
            }
        }
        is MutableOmicronTypeParameter -> {
            if (types.all { it is MutableOmicronTypeParameter && firstPath == generatePath(it) }) {
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

internal fun validateExplicitSymbol(
    ctx: SourceContext,
    errors: LanguageErrors,
    identifier: Identifier,
    scope: Scope<Symbol>
) {
    fun isValidTarget(candidate: Symbol): Boolean {
        return when (candidate) {
            is ParameterizedRecordTypeSymbol,
            is ParameterizedCoproductSymbol -> true
            else -> false
        }
    }
    if (scope is Symbol) {
        val linearized = linearizeIdentifiers(listOf(identifier))
        linearized.forEach { gid ->
            if (gid is GroundIdentifier) {
                when (scope.fetch(gid)) {
                    is StandardTypeParameter,
                    is ImmutableOmicronTypeParameter,
                    is MutableOmicronTypeParameter -> {
                        var targetParent: Symbol = scope
                        while (targetParent !is NullSymbolTable && !isValidTarget(targetParent)) {
                            targetParent = targetParent.parent as Symbol
                        }
                        when (targetParent) {
                            is ParameterizedRecordTypeSymbol -> {
                                if (!targetParent.existsHere(gid)) {
                                    errors.add(ctx, ForeignTypeParameter(gid))
                                }
                            }
                            is ParameterizedCoproductSymbol -> {
                                if (!targetParent.existsHere(gid)) {
                                    errors.add(ctx, ForeignTypeParameter(gid))
                                }
                            }
                            is NullSymbolTable -> {
                                errors.add(ctx, TypeSystemBug)
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

internal fun validateSubstitution(
    ctx: SourceContext,
    errors: LanguageErrors,
    typeParameter: TypeParameter,
    substitutedSymbol: Symbol
) {
    when (typeParameter) {
        is StandardTypeParameter -> when (substitutedSymbol) {
            is GroundRecordTypeSymbol,
            is GroundCoproductSymbol,
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
                    is ParameterizedCoproductSymbol -> if (!parameterizedType.featureSupport.typeArg) {
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
        is ImmutableOmicronTypeParameter -> when (substitutedSymbol) {
            is CostExpression -> Unit
            else -> errors.add(ctx, InvalidOmicronTypeSub(typeParameter, substitutedSymbol))
        }
        is MutableOmicronTypeParameter -> when (substitutedSymbol) {
            is OmicronTypeSymbol -> Unit
            else -> errors.add(ctx, InvalidOmicronTypeSub(typeParameter, substitutedSymbol))
        }
    }
}
