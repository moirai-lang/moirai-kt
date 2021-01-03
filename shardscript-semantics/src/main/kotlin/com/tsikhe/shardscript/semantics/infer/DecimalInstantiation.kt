package com.tsikhe.shardscript.semantics.infer

import com.tsikhe.shardscript.semantics.core.*

class DecimalInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            return if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                val substitution = Substitution(parameterized.typeParams, listOf())
                substitution.apply(parameterized)
            } else {
                validateSubstitution(ctx, errors, parameterized.typeParams.first(), explicitTypeArgs.first())
                val substitution = Substitution(parameterized.typeParams, explicitTypeArgs)
                substitution.apply(parameterized)
            }
        } else {
            val inOrderParameters = parameterized.typeParams
            if (args.isNotEmpty()) {
                val decimalAst = args.first() as DecimalLiteralAst
                val decimalString = decimalAst.canonicalForm.toPlainString()
                val omicron = OmicronTypeSymbol(decimalString.length.toLong())
                val substitution = Substitution(inOrderParameters, listOf(omicron))
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