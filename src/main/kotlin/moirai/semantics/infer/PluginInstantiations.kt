package moirai.semantics.infer

import moirai.semantics.core.*

internal object DualFinPluginInstantiation : DotInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        lhsInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstFin = (lhsInstantiation.substitutionChain).replayArgs().first()
            val secondFin = when (val argType = args.first().readType()) {
                is TypeInstantiation -> {
                    (argType.substitutionChain).replayArgs().first()
                }
                else -> {
                    errors.add(ctx, TypeMismatch(toError(lhsInstantiation), toError(argType)))
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

internal object DoubleParentSingleFinPluginInstantiation : DotInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        lhsInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstElementType = (lhsInstantiation.substitutionChain).replayArgs().first()
            val firstFin = (lhsInstantiation.substitutionChain).replayArgs()[1]
            val secondFin = when (val argType = args.first().readType()) {
                is TypeInstantiation -> {
                    (argType.substitutionChain).replayArgs()[1]
                }
                else -> {
                    errors.add(ctx, TypeMismatch(toError(lhsInstantiation), toError(argType)))
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

internal object TripleParentSingleFinPluginInstantiation : DotInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        lhsInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstKeyType = (lhsInstantiation.substitutionChain).replayArgs().first()
            val firstValueType = (lhsInstantiation.substitutionChain).replayArgs()[1]
            val firstFin = (lhsInstantiation.substitutionChain).replayArgs()[2]
            val secondFin = when (val argType = args.first().readType()) {
                is TypeInstantiation -> {
                    (argType.substitutionChain).replayArgs()[2]
                }
                else -> {
                    errors.add(ctx, TypeMismatch(toError(lhsInstantiation), toError(argType)))
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

internal object AscribeInstantiation : DotInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        lhsInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                val substitution = Substitution(terminus.typeParams, listOf())
                return substitution.apply(terminus)
            } else {
                if (explicitTypeArgs[0] is Fin) {
                    val firstFin = (lhsInstantiation.substitutionChain).replayArgs().first()
                    val substitution = Substitution(terminus.typeParams, listOf(firstFin, explicitTypeArgs[0]))
                    return substitution.apply(terminus)
                } else {
                    langThrow(ctx, TypeRequiresExplicitFin(toError(identifier)))
                }
            }
        } else {
            langThrow(ctx, TypeRequiresExplicitFin(toError(identifier)))
        }
    }
}

internal object SingleParentArgInstantiation : DotInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        lhsInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstFin = (lhsInstantiation.substitutionChain).replayArgs().first()
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

internal object DoubleParentArgInstantiation : DotInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        lhsInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstType = (lhsInstantiation.substitutionChain).replayArgs().first()
            val secondType = (lhsInstantiation.substitutionChain).replayArgs()[1]
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

internal object TripleParentArgInstantiation : DotInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        lhsInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedMemberPluginSymbol = terminus as ParameterizedMemberPluginSymbol
        val inOrderParameters = terminus.typeParams
        if (parameterizedMemberPluginSymbol.formalParams.size == args.size) {
            val firstType = (lhsInstantiation.substitutionChain).replayArgs().first()
            val secondType = (lhsInstantiation.substitutionChain).replayArgs()[1]
            val thirdType = (lhsInstantiation.substitutionChain).replayArgs()[2]
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