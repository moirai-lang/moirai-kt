package moirai.semantics.infer

import moirai.semantics.core.*

internal class DictionaryInstantiation(private val pairTypeSymbol: ParameterizedRecordType) :
    SingleTypeInstantiation<TerminusType, TypeInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: TerminusType,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): TypeInstantiation {
        args.forEach {
            val argType = it.readType()
            if (argType !is TypeInstantiation || argType.substitutionChain.terminus != pairTypeSymbol) {
                errors.add(ctx, DictionaryArgsMustBePairs(argType))
            }
        }
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 3) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(3, explicitTypeArgs.size))
                val substitution = Substitution(terminus.typeParams, listOf())
                return substitution.apply(terminus)
            } else {
                validateSubstitution(ctx, errors, terminus.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, terminus.typeParams[1], explicitTypeArgs[1])
                validateSubstitution(ctx, errors, terminus.typeParams[2], explicitTypeArgs[2])
                if (explicitTypeArgs[2] is Fin) {
                    val fin = explicitTypeArgs[2] as Fin
                    if (args.size.toLong() > fin.magnitude) {
                        errors.add(ctx, TooManyElements(fin.magnitude, args.size.toLong()))
                    }
                }
                val substitution = Substitution(terminus.typeParams, explicitTypeArgs)
                return substitution.apply(terminus)
            }
        } else {
            val inOrderParameters = terminus.typeParams
            val parameterSet = inOrderParameters.toSet()
            if (args.isNotEmpty()) {
                val constraints: MutableList<Constraint<TypeParameter, Type>> = ArrayList()
                val firstType = args.first().readType()
                if (firstType is TypeInstantiation && firstType.substitutionChain.terminus == pairTypeSymbol) {
                    constraints.addAll(
                        constrainSymbol(
                            ctx,
                            parameterSet,
                            inOrderParameters.first(),
                            (firstType.substitutionChain).replayArgs().first(),
                            errors
                        )
                    )
                    constraints.addAll(
                        constrainSymbol(
                            ctx,
                            parameterSet,
                            inOrderParameters[1],
                            (firstType.substitutionChain).replayArgs()[1],
                            errors
                        )
                    )
                    constraints.add(
                        Constraint(
                            Left(inOrderParameters[2]),
                            Right(Fin(args.size.toLong()))
                        )
                    )
                    val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
                    return substitution.apply(terminus)
                } else {
                    inOrderParameters.forEach {
                        errors.add(
                            ctx,
                            TypeInferenceFailed(it)
                        )
                    }
                }
            } else {
                inOrderParameters.forEach {
                    errors.add(
                        ctx,
                        TypeInferenceFailed(it)
                    )
                }
            }
            throw LanguageException(errors.toSet())
        }
    }
}

internal class MutableDictionaryInstantiation(private val pairTypeSymbol: ParameterizedRecordType) :
    SingleTypeInstantiation<TerminusType, TypeInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: TerminusType,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): TypeInstantiation {
        args.forEach {
            val argType = it.readType()
            if (argType !is TypeInstantiation || argType.substitutionChain.terminus != pairTypeSymbol) {
                errors.add(ctx, DictionaryArgsMustBePairs(argType))
            }
        }
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 3) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(3, explicitTypeArgs.size))
                val substitution = Substitution(terminus.typeParams, listOf())
                return substitution.apply(terminus)
            } else {
                validateSubstitution(ctx, errors, terminus.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, terminus.typeParams[1], explicitTypeArgs[1])
                validateSubstitution(ctx, errors, terminus.typeParams[2], explicitTypeArgs[2])
                if (explicitTypeArgs[2] is Fin) {
                    val fin = explicitTypeArgs[2] as Fin
                    if (args.size.toLong() > fin.magnitude) {
                        errors.add(ctx, TooManyElements(fin.magnitude, args.size.toLong()))
                    }
                }
                val substitution = Substitution(terminus.typeParams, explicitTypeArgs)
                return substitution.apply(terminus)
            }
        } else {
            langThrow(ctx, TypeRequiresExplicit(identifier))
        }
    }
}