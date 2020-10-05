package com.tsikhe.shardscript.semantics.infer

import com.tsikhe.shardscript.semantics.core.*

internal class DictionaryInstantiation(private val pairTypeSymbol: ParameterizedRecordTypeSymbol) :
    SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        args.forEach {
            val argType = it.readType()
            if (argType !is SymbolInstantiation || argType.substitutionChain.originalSymbol != pairTypeSymbol) {
                errors.add(ctx, DictionaryArgsMustBePairs(argType))
            }
        }
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 3) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(3, explicitTypeArgs.size))
                val substitution = Substitution(parameterized.typeParams, listOf())
                return substitution.apply(parameterized)
            } else {
                validateSubstitution(ctx, errors, parameterized.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, parameterized.typeParams[1], explicitTypeArgs[1])
                validateSubstitution(ctx, errors, parameterized.typeParams[2], explicitTypeArgs[2])
                if (explicitTypeArgs[2] is OmicronTypeSymbol) {
                    val omicron = explicitTypeArgs[2] as OmicronTypeSymbol
                    if (args.size.toBigInteger() > omicron.magnitude) {
                        errors.add(ctx, TooManyElements(omicron.magnitude, args.size.toBigInteger()))
                    }
                }
                val substitution = Substitution(parameterized.typeParams, explicitTypeArgs)
                return substitution.apply(parameterized)
            }
        } else {
            val inOrderParameters = parameterized.typeParams
            val parameterSet = inOrderParameters.toSet()
            if (args.isNotEmpty()) {
                val constraints: MutableList<Constraint<TypeParameter, Symbol>> = ArrayList()
                val firstType = args.first().readType()
                if (firstType is SymbolInstantiation && firstType.substitutionChain.originalSymbol == pairTypeSymbol) {
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
                            Right(OmicronTypeSymbol(args.size.toBigInteger()))
                        )
                    )
                    val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
                    return substitution.apply(parameterized)
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

internal class MutableDictionaryInstantiation(private val pairTypeSymbol: ParameterizedRecordTypeSymbol) :
    SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        args.forEach {
            val argType = it.readType()
            if (argType !is SymbolInstantiation || argType.substitutionChain.originalSymbol != pairTypeSymbol) {
                errors.add(ctx, DictionaryArgsMustBePairs(argType))
            }
        }
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 3) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(3, explicitTypeArgs.size))
                val substitution = Substitution(parameterized.typeParams, listOf())
                return substitution.apply(parameterized)
            } else {
                validateSubstitution(ctx, errors, parameterized.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, parameterized.typeParams[1], explicitTypeArgs[1])
                validateSubstitution(ctx, errors, parameterized.typeParams[2], explicitTypeArgs[2])
                if (explicitTypeArgs[2] is OmicronTypeSymbol) {
                    val omicron = explicitTypeArgs[2] as OmicronTypeSymbol
                    if (args.size.toBigInteger() > omicron.magnitude) {
                        errors.add(ctx, TooManyElements(omicron.magnitude, args.size.toBigInteger()))
                    }
                }
                val substitution = Substitution(parameterized.typeParams, explicitTypeArgs)
                return substitution.apply(parameterized)
            }
        } else {
            langThrow(ctx, TypeRequiresExplicit(parameterized.gid))
        }
    }
}