package com.tsikhe.shardscript.semantics.infer

import com.tsikhe.shardscript.semantics.core.*

class ListInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 2) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(2, explicitTypeArgs.size))
                val substitution = Substitution(parameterized.typeParams, listOf())
                return substitution.apply(parameterized)
            } else {
                validateSubstitution(ctx, errors, parameterized.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, parameterized.typeParams[1], explicitTypeArgs[1])
                if (explicitTypeArgs[1] is OmicronTypeSymbol) {
                    val omicron = explicitTypeArgs[1] as OmicronTypeSymbol
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
            throw LanguageException(errors.toSet())
        }
    }
}

class MutableListInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 2) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(2, explicitTypeArgs.size))
                val substitution = Substitution(parameterized.typeParams, listOf())
                return substitution.apply(parameterized)
            } else {
                validateSubstitution(
                    ctx,
                    errors,
                    parameterized.typeParams.first(),
                    explicitTypeArgs.first()
                )
                validateSubstitution(ctx, errors, parameterized.typeParams[1], explicitTypeArgs[1])
                if (explicitTypeArgs[1] is OmicronTypeSymbol) {
                    val omicron = explicitTypeArgs[1] as OmicronTypeSymbol
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