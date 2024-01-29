package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*
import org.shardscript.semantics.prelude.Lang
import kotlin.math.abs

object RangeInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawSymbol: ParameterizedSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            errors.add(ctx, CannotExplicitlyInstantiate(rawSymbol))
        }
        val allValid = args.all {
            val valid = it is IntLiteralAst
            if (!valid) {
                errors.add(it.ctx, InvalidRangeArg)
            }
            valid
        }
        if (allValid) {
            val intArgs = args.map { it as IntLiteralAst }
            if (intArgs.size == 2) {
                val first = intArgs[0].canonicalForm
                val second = intArgs[1].canonicalForm
                val min = first.coerceAtMost(second)
                val max = first.coerceAtLeast(second)
                val fin = FinTypeSymbol(abs(max - min).toLong())
                val substitution = Substitution(rawSymbol.typeParams, listOf(fin))
                return substitution.apply(rawSymbol)
            } else {
                errors.add(ctx, IncorrectNumberOfArgs(2, args.size))
            }
        }
        throw LanguageException(errors.toSet())
    }
}

object RandomInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        rawSymbol: ParameterizedSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedStaticPluginSymbol = rawSymbol as ParameterizedStaticPluginSymbol
        val res = if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                throw LanguageException(errors.toSet())
            } else {
                validateSubstitution(ctx, errors, rawSymbol.typeParams.first(), explicitTypeArgs.first())
                val substitution = Substitution(rawSymbol.typeParams, explicitTypeArgs)
                substitution.apply(rawSymbol)
            }
        } else {
            val inOrderParameters = rawSymbol.typeParams
            val parameterSet = inOrderParameters.toSet()
            if (parameterizedStaticPluginSymbol.formalParams.size == args.size) {
                val constraints: MutableList<Constraint<TypeParameter, Type>> = ArrayList()
                parameterizedStaticPluginSymbol.formalParams.zip(args).forEach {
                    constraints.addAll(
                        constrainSymbol(
                            ctx,
                            parameterSet,
                            it.first.ofTypeSymbol,
                            it.second.readType(),
                            errors
                        )
                    )
                }
                val substitution = createSubstitution(ctx, constraints, parameterSet, inOrderParameters, errors)
                substitution.apply(rawSymbol)
            } else {
                errors.add(
                    ctx,
                    IncorrectNumberOfArgs(parameterizedStaticPluginSymbol.formalParams.size, args.size)
                )
                throw LanguageException(errors.toSet())
            }
        }
        return when (generatePath((res.substitutionChain).replayArgs().first() as Symbol)) {
            listOf(Lang.intId.name) -> res
            else -> {
                errors.add(ctx, RandomRequiresIntLong)
                throw LanguageException(errors.toSet())
            }
        }
    }
}