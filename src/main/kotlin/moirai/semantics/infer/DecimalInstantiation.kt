package moirai.semantics.infer

import moirai.semantics.core.*

internal class DecimalInstantiation : SingleTypeInstantiation<TerminusType, TypeInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: TerminusType,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): TypeInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            return if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                val substitution = Substitution(terminus.typeParams, listOf())
                substitution.apply(terminus)
            } else {
                validateSubstitution(ctx, errors, terminus.typeParams.first(), explicitTypeArgs.first())
                val substitution = Substitution(terminus.typeParams, explicitTypeArgs)
                substitution.apply(terminus)
            }
        } else {
            val inOrderParameters = terminus.typeParams
            if (args.isNotEmpty()) {
                val decimalAst = args.first() as DecimalLiteralAst
                val decimalString = decimalAst.canonicalForm.toPlainString()
                val fin = Fin(decimalString.length.toLong())
                val substitution = Substitution(inOrderParameters, listOf(fin))
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