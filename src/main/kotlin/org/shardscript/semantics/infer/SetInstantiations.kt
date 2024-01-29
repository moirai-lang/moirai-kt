package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class SetInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawTerminus: RawTerminus,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 2) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(2, explicitTypeArgs.size))
                val substitution = Substitution(rawTerminus.typeParams, listOf())
                return substitution.apply(rawTerminus)
            } else {
                validateSubstitution(ctx, errors, rawTerminus.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, rawTerminus.typeParams[1], explicitTypeArgs[1])
                if (explicitTypeArgs[1] is FinTypeSymbol) {
                    val fin = explicitTypeArgs[1] as FinTypeSymbol
                    if (args.size.toLong() > fin.magnitude) {
                        errors.add(ctx, TooManyElements(fin.magnitude, args.size.toLong()))
                    }
                }
                val substitution = Substitution(rawTerminus.typeParams, explicitTypeArgs)
                return substitution.apply(rawTerminus)
            }
        } else {
            val inOrderParameters = rawTerminus.typeParams
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
                return substitution.apply(rawTerminus)
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

class MutableSetInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawTerminus: RawTerminus,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 2) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(2, explicitTypeArgs.size))
                val substitution = Substitution(rawTerminus.typeParams, listOf())
                return substitution.apply(rawTerminus)
            } else {
                validateSubstitution(
                    ctx,
                    errors,
                    rawTerminus.typeParams.first(),
                    explicitTypeArgs.first()
                )
                validateSubstitution(ctx, errors, rawTerminus.typeParams[1], explicitTypeArgs[1])
                if (explicitTypeArgs[1] is FinTypeSymbol) {
                    val fin = explicitTypeArgs[1] as FinTypeSymbol
                    if (args.size.toLong() > fin.magnitude) {
                        errors.add(ctx, TooManyElements(fin.magnitude, args.size.toLong()))
                    }
                }
                val substitution = Substitution(rawTerminus.typeParams, explicitTypeArgs)
                return substitution.apply(rawTerminus)
            }
        } else {
            langThrow(ctx, TypeRequiresExplicit(identifier))
        }
    }
}