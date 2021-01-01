package com.tsikhe.shardscript.semantics.infer

import com.tsikhe.shardscript.semantics.core.*

object DualOmicronPluginInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstOmicron = (existingInstantiation.substitutionChain).replayArgs().first()
            val secondOmicron = when (val argType = args.first().readType()) {
                is SymbolInstantiation -> {
                    (argType.substitutionChain).replayArgs().first()
                }
                else -> {
                    errors.add(ctx, TypeMismatch(existingInstantiation.substitutionChain.originalSymbol, argType))
                    throw LanguageException(errors.toSet())
                }
            }
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstOmicron, secondOmicron)
            )
            return substitution.apply(parameterized)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(parameterized.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

object DoubleParentSingleOmicronPluginInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstElementType = (existingInstantiation.substitutionChain).replayArgs().first()
            val firstOmicron = (existingInstantiation.substitutionChain).replayArgs()[1]
            val secondOmicron = when (val argType = args.first().readType()) {
                is SymbolInstantiation -> {
                    (argType.substitutionChain).replayArgs()[1]
                }
                else -> {
                    errors.add(ctx, TypeMismatch(existingInstantiation.substitutionChain.originalSymbol, argType))
                    throw LanguageException(errors.toSet())
                }
            }
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstElementType, firstOmicron, secondOmicron)
            )
            return substitution.apply(parameterized)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(parameterized.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

object TripleParentSingleOmicronPluginInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstKeyType = (existingInstantiation.substitutionChain).replayArgs().first()
            val firstValueType = (existingInstantiation.substitutionChain).replayArgs()[1]
            val firstOmicron = (existingInstantiation.substitutionChain).replayArgs()[2]
            val secondOmicron = when (val argType = args.first().readType()) {
                is SymbolInstantiation -> {
                    (argType.substitutionChain).replayArgs()[2]
                }
                else -> {
                    errors.add(ctx, TypeMismatch(existingInstantiation.substitutionChain.originalSymbol, argType))
                    throw LanguageException(errors.toSet())
                }
            }
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstKeyType, firstValueType, firstOmicron, secondOmicron)
            )
            return substitution.apply(parameterized)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(parameterized.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

object SingleParentArgInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstOmicron = (existingInstantiation.substitutionChain).replayArgs().first()
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstOmicron)
            )
            return substitution.apply(parameterized)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(parameterized.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

object DoubleParentArgInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstType = (existingInstantiation.substitutionChain).replayArgs().first()
            val secondType = (existingInstantiation.substitutionChain).replayArgs()[1]
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstType, secondType)
            )
            return substitution.apply(parameterized)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(parameterized.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

object TripleParentArgInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstType = (existingInstantiation.substitutionChain).replayArgs().first()
            val secondType = (existingInstantiation.substitutionChain).replayArgs()[1]
            val thirdType = (existingInstantiation.substitutionChain).replayArgs()[2]
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstType, secondType, thirdType)
            )
            return substitution.apply(parameterized)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(parameterized.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}