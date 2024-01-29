package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class ListInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawSymbol: ParameterizedSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 2) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(2, explicitTypeArgs.size))
                val substitution = Substitution(rawSymbol.typeParams, listOf())
                return substitution.apply(rawSymbol)
            } else {
                validateSubstitution(ctx, errors, rawSymbol.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, rawSymbol.typeParams[1], explicitTypeArgs[1])
                if (explicitTypeArgs[1] is FinTypeSymbol) {
                    val fin = explicitTypeArgs[1] as FinTypeSymbol
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
                constraints.addAll(
                    constrainSymbol(
                        ctx,
                        parameterSet,
                        inOrderParameters.first(),
                        args.first().readType(),
                        errors
                    )
                )
                constraints.add(
                    Constraint(
                        Left(inOrderParameters[1]),
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
            throw LanguageException(errors.toSet())
        }
    }
}

class MutableListInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawSymbol: ParameterizedSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 2) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(2, explicitTypeArgs.size))
                val substitution = Substitution(rawSymbol.typeParams, listOf())
                return substitution.apply(rawSymbol)
            } else {
                validateSubstitution(
                    ctx,
                    errors,
                    rawSymbol.typeParams.first(),
                    explicitTypeArgs.first()
                )
                validateSubstitution(ctx, errors, rawSymbol.typeParams[1], explicitTypeArgs[1])
                if (explicitTypeArgs[1] is FinTypeSymbol) {
                    val fin = explicitTypeArgs[1] as FinTypeSymbol
                    if (args.size.toLong() > fin.magnitude) {
                        errors.add(ctx, TooManyElements(fin.magnitude, args.size.toLong()))
                    }
                }
                val substitution = Substitution(rawSymbol.typeParams, explicitTypeArgs)
                return substitution.apply(rawSymbol)
            }
        } else {
            langThrow(ctx, TypeRequiresExplicit(rawSymbol.identifier))
        }
    }
}