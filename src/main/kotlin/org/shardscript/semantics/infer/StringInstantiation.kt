package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class StringInstantiation : SingleTypeInstantiation {
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
            when {
                args.size == 1 && args.first() is StringLiteralAst -> {
                    val stringAst = args.first() as StringLiteralAst
                    val omicron = OmicronTypeSymbol(stringAst.canonicalForm.length.toLong())
                    val substitution = Substitution(inOrderParameters, listOf(omicron))
                    return substitution.apply(parameterized)
                }
                args.isEmpty() -> {
                    inOrderParameters.forEach {
                        errors.add(
                            ctx,
                            TypeInferenceFailed(it)
                        )
                    }
                }
                else -> {
                    val children: MutableList<CostExpression> = ArrayList()
                    args.forEach {
                        if (isValidStringType(it.readType())) {
                            children.add(costExpressionFromValidStringType(it.readType()))
                        } else {
                            errors.add(it.ctx, IncompatibleString(it.readType()))
                        }
                    }
                    val costExpression = SumCostExpression(children)
                    val substitution = Substitution(inOrderParameters, listOf(costExpression))
                    return substitution.apply(parameterized)
                }
            }
            throw LanguageException(errors.toSet())
        }
    }
}