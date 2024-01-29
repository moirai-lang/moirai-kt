package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class DecimalInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawTerminus: RawTerminus,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            return if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                val substitution = Substitution(rawTerminus.typeParams, listOf())
                substitution.apply(rawTerminus)
            } else {
                validateSubstitution(ctx, errors, rawTerminus.typeParams.first(), explicitTypeArgs.first())
                val substitution = Substitution(rawTerminus.typeParams, explicitTypeArgs)
                substitution.apply(rawTerminus)
            }
        } else {
            val inOrderParameters = rawTerminus.typeParams
            if (args.isNotEmpty()) {
                val decimalAst = args.first() as DecimalLiteralAst
                val decimalString = decimalAst.canonicalForm.toPlainString()
                val fin = FinTypeSymbol(decimalString.length.toLong())
                val substitution = Substitution(inOrderParameters, listOf(fin))
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