package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*

class StringInstantiation : SingleTypeInstantiation {
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
            when {
                args.size == 1 && args.first() is StringLiteralAst -> {
                    val stringAst = args.first() as StringLiteralAst
                    val fin = FinTypeSymbol(stringAst.canonicalForm.length.toLong())
                    val substitution = Substitution(inOrderParameters, listOf(fin))
                    return substitution.apply(rawTerminus)
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
                    return substitution.apply(rawTerminus)
                }
            }
            throw LanguageException(errors.toSet())
        }
    }
}