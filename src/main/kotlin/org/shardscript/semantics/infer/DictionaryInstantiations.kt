package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class DictionaryInstantiation(private val pairTypeSymbol: ParameterizedRecordTypeSymbol) :
    SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawSymbol: RawSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
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
                val substitution = Substitution(rawSymbol.typeParams, listOf())
                return substitution.apply(rawSymbol)
            } else {
                validateSubstitution(ctx, errors, rawSymbol.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, rawSymbol.typeParams[1], explicitTypeArgs[1])
                validateSubstitution(ctx, errors, rawSymbol.typeParams[2], explicitTypeArgs[2])
                if (explicitTypeArgs[2] is FinTypeSymbol) {
                    val fin = explicitTypeArgs[2] as FinTypeSymbol
                    if (args.size.toLong() > fin.magnitude) {
                        errors.add(ctx, TooManyElements(fin.magnitude, args.size.toLong()))
                    }
                }
                val substitution = Substitution(rawSymbol.typeParams, explicitTypeArgs)
                return substitution.apply(rawSymbol)
            }
        } else {
            val inOrderParameters = rawSymbol.typeParams
            val parameterSet = inOrderParameters.toSet()
            if (args.isNotEmpty()) {
                val constraints: MutableList<Constraint<TypeParameter, Type>> = ArrayList()
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
                            Right(FinTypeSymbol(args.size.toLong()))
                        )
                    )
                    val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
                    return substitution.apply(rawSymbol)
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

class MutableDictionaryInstantiation(private val pairTypeSymbol: ParameterizedRecordTypeSymbol) :
    SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawSymbol: RawSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
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
                val substitution = Substitution(rawSymbol.typeParams, listOf())
                return substitution.apply(rawSymbol)
            } else {
                validateSubstitution(ctx, errors, rawSymbol.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, rawSymbol.typeParams[1], explicitTypeArgs[1])
                validateSubstitution(ctx, errors, rawSymbol.typeParams[2], explicitTypeArgs[2])
                if (explicitTypeArgs[2] is FinTypeSymbol) {
                    val fin = explicitTypeArgs[2] as FinTypeSymbol
                    if (args.size.toLong() > fin.magnitude) {
                        errors.add(ctx, TooManyElements(fin.magnitude, args.size.toLong()))
                    }
                }
                val substitution = Substitution(rawSymbol.typeParams, explicitTypeArgs)
                return substitution.apply(rawSymbol)
            }
        } else {
            langThrow(ctx, TypeRequiresExplicit(identifier))
        }
    }
}