package moirai.semantics.infer

import moirai.semantics.core.*

internal class ListInstantiation : SingleTypeInstantiation<TerminusType, TypeInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: TerminusType,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): TypeInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 2) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(2, explicitTypeArgs.size))
                val substitution = Substitution(terminus.typeParams, listOf())
                return substitution.apply(terminus)
            } else {
                validateSubstitution(ctx, errors, terminus.typeParams.first(), explicitTypeArgs.first())
                validateSubstitution(ctx, errors, terminus.typeParams[1], explicitTypeArgs[1])
                if (explicitTypeArgs[1] is Fin) {
                    val fin = explicitTypeArgs[1] as Fin
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
                        Right(Fin(args.size.toLong()))
                    )
                )
                val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
                return substitution.apply(terminus)
            } else {
                inOrderParameters.forEach {
                    errors.add(
                        ctx,
                        TypeInferenceFailed(toError(it))
                    )
                }
            }
            throw LanguageException(errors.toSet())
        }
    }
}

internal class MutableListInstantiation : SingleTypeInstantiation<TerminusType, TypeInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: TerminusType,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): TypeInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 2) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(2, explicitTypeArgs.size))
                val substitution = Substitution(terminus.typeParams, listOf())
                return substitution.apply(terminus)
            } else {
                validateSubstitution(
                    ctx,
                    errors,
                    terminus.typeParams.first(),
                    explicitTypeArgs.first()
                )
                validateSubstitution(ctx, errors, terminus.typeParams[1], explicitTypeArgs[1])
                if (explicitTypeArgs[1] is Fin) {
                    val fin = explicitTypeArgs[1] as Fin
                    if (args.size.toLong() > fin.magnitude) {
                        errors.add(ctx, TooManyElements(fin.magnitude, args.size.toLong()))
                    }
                }
                val substitution = Substitution(terminus.typeParams, explicitTypeArgs)
                return substitution.apply(terminus)
            }
        } else {
            langThrow(ctx, TypeRequiresExplicit(toError(identifier)))
        }
    }
}