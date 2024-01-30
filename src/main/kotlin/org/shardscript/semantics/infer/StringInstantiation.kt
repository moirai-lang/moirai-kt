package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class StringInstantiation : SingleTypeInstantiation<TerminusType, TypeInstantiation> {
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
            when {
                args.size == 1 && args.first() is StringLiteralAst -> {
                    val stringAst = args.first() as StringLiteralAst
                    val fin = FinTypeSymbol(stringAst.canonicalForm.length.toLong())
                    val substitution = Substitution(inOrderParameters, listOf(fin))
                    return substitution.apply(terminus)
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
                    return substitution.apply(terminus)
                }
            }
            throw LanguageException(errors.toSet())
        }
    }
}