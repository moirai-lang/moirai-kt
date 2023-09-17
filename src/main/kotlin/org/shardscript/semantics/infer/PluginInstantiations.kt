package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

object DualFinPluginInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstFin = (existingInstantiation.substitutionChain).replayArgs().first()
            val secondFin = when (val argType = args.first().readType()) {
                is SymbolInstantiation -> {
                    (argType.substitutionChain).replayArgs().first()
                }
                else -> {
                    errors.add(ctx, TypeMismatch(existingInstantiation, argType))
                    throw LanguageException(errors.toSet())
                }
            }
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstFin, secondFin)
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

object DoubleParentSingleFinPluginInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstElementType = (existingInstantiation.substitutionChain).replayArgs().first()
            val firstFin = (existingInstantiation.substitutionChain).replayArgs()[1]
            val secondFin = when (val argType = args.first().readType()) {
                is SymbolInstantiation -> {
                    (argType.substitutionChain).replayArgs()[1]
                }
                else -> {
                    errors.add(ctx, TypeMismatch(existingInstantiation, argType))
                    throw LanguageException(errors.toSet())
                }
            }
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstElementType, firstFin, secondFin)
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

object TripleParentSingleFinPluginInstantiation : TwoTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstKeyType = (existingInstantiation.substitutionChain).replayArgs().first()
            val firstValueType = (existingInstantiation.substitutionChain).replayArgs()[1]
            val firstFin = (existingInstantiation.substitutionChain).replayArgs()[2]
            val secondFin = when (val argType = args.first().readType()) {
                is SymbolInstantiation -> {
                    (argType.substitutionChain).replayArgs()[2]
                }
                else -> {
                    errors.add(ctx, TypeMismatch(existingInstantiation, argType))
                    throw LanguageException(errors.toSet())
                }
            }
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstKeyType, firstValueType, firstFin, secondFin)
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
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = parameterized as ParameterizedMemberPluginSymbol
        val inOrderParameters = parameterized.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstFin = (existingInstantiation.substitutionChain).replayArgs().first()
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstFin)
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
        explicitTypeArgs: List<Type>
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
        explicitTypeArgs: List<Type>
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