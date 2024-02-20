package moirai.semantics.infer

import moirai.semantics.core.*

internal object DualFinPluginInstantiation : TwoTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        existingInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstFin = (existingInstantiation.substitutionChain).replayArgs().first()
            val secondFin = when (val argType = args.first().readType()) {
                is TypeInstantiation -> {
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
            return substitution.apply(terminus)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(terminus.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

internal object DoubleParentSingleFinPluginInstantiation : TwoTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        existingInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstElementType = (existingInstantiation.substitutionChain).replayArgs().first()
            val firstFin = (existingInstantiation.substitutionChain).replayArgs()[1]
            val secondFin = when (val argType = args.first().readType()) {
                is TypeInstantiation -> {
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
            return substitution.apply(terminus)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(terminus.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

internal object TripleParentSingleFinPluginInstantiation : TwoTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        existingInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstKeyType = (existingInstantiation.substitutionChain).replayArgs().first()
            val firstValueType = (existingInstantiation.substitutionChain).replayArgs()[1]
            val firstFin = (existingInstantiation.substitutionChain).replayArgs()[2]
            val secondFin = when (val argType = args.first().readType()) {
                is TypeInstantiation -> {
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
            return substitution.apply(terminus)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(terminus.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

internal object AscribeInstantiation : TwoTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        existingInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                val substitution = Substitution(terminus.typeParams, listOf())
                return substitution.apply(terminus)
            } else {
                if (explicitTypeArgs[0] is Fin) {
                    val firstFin = (existingInstantiation.substitutionChain).replayArgs().first()
                    val substitution = Substitution(terminus.typeParams, listOf(firstFin, explicitTypeArgs[0]))
                    return substitution.apply(terminus)
                } else {
                    langThrow(ctx, TypeRequiresExplicitFin(identifier))
                }
            }
        } else {
            langThrow(ctx, TypeRequiresExplicitFin(identifier))
        }
    }
}

internal object SingleParentArgInstantiation : TwoTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        existingInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstFin = (existingInstantiation.substitutionChain).replayArgs().first()
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstFin)
            )
            return substitution.apply(terminus)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(terminus.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

internal object DoubleParentArgInstantiation : TwoTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        existingInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstType = (existingInstantiation.substitutionChain).replayArgs().first()
            val secondType = (existingInstantiation.substitutionChain).replayArgs()[1]
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstType, secondType)
            )
            return substitution.apply(terminus)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(terminus.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}

internal object TripleParentArgInstantiation : TwoTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        existingInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstType = (existingInstantiation.substitutionChain).replayArgs().first()
            val secondType = (existingInstantiation.substitutionChain).replayArgs()[1]
            val thirdType = (existingInstantiation.substitutionChain).replayArgs()[2]
            val substitution = Substitution(
                inOrderParameters,
                listOf(firstType, secondType, thirdType)
            )
            return substitution.apply(terminus)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(terminus.formalParams.size, args.size)
            )
            throw LanguageException(errors.toSet())
        }
    }
}