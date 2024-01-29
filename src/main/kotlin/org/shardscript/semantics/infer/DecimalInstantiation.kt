package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class DecimalInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawSymbol: ParameterizedSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            return if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                val substitution = Substitution(rawSymbol.typeParams, listOf())
                substitution.apply(rawSymbol)
            } else {
                validateSubstitution(ctx, errors, rawSymbol.typeParams.first(), explicitTypeArgs.first())
                val substitution = Substitution(rawSymbol.typeParams, explicitTypeArgs)
                substitution.apply(rawSymbol)
            }
        } else {
            val inOrderParameters = rawSymbol.typeParams
            if (args.isNotEmpty()) {
                val decimalAst = args.first() as DecimalLiteralAst
                val decimalString = decimalAst.canonicalForm.toPlainString()
                val fin = FinTypeSymbol(decimalString.length.toLong())
                val substitution = Substitution(inOrderParameters, listOf(fin))
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