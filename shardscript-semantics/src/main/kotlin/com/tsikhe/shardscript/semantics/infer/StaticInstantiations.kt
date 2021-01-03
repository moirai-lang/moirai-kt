package com.tsikhe.shardscript.semantics.infer

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.prelude.Lang
import kotlin.math.abs

object RangeInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            errors.add(ctx, CannotExplicitlyInstantiate(parameterized))
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
                val omicron = OmicronTypeSymbol(abs(max - min).toLong())
                val substitution = Substitution(parameterized.typeParams, listOf(omicron))
                return substitution.apply(parameterized)
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
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        val parameterizedStaticPluginSymbol = parameterized as ParameterizedStaticPluginSymbol
        val res = if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                throw LanguageException(errors.toSet())
            } else {
                validateSubstitution(ctx, errors, parameterized.typeParams.first(), explicitTypeArgs.first())
                val substitution = Substitution(parameterized.typeParams, explicitTypeArgs)
                substitution.apply(parameterized)
            }
        } else {
            val inOrderParameters = parameterized.typeParams
            val parameterSet = inOrderParameters.toSet()
            if (parameterizedStaticPluginSymbol.formalParams.size == args.size) {
                val constraints: MutableList<Constraint<TypeParameter, Symbol>> = ArrayList()
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
                substitution.apply(parameterized)
            } else {
                errors.add(
                    ctx,
                    IncorrectNumberOfArgs(parameterizedStaticPluginSymbol.formalParams.size, args.size)
                )
                throw LanguageException(errors.toSet())
            }
        }
        return when (generatePath((res.substitutionChain).replayArgs().first())) {
            listOf(Lang.shardId.name, Lang.langId.name, Lang.intId.name) -> res
            listOf(Lang.shardId.name, Lang.langId.name, Lang.longId.name) -> res
            else -> {
                errors.add(ctx, RandomRequiresIntLong)
                throw LanguageException(errors.toSet())
            }
        }
    }
}

object ExplicitInstantiation : SingleTypeInstantiation {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Symbol>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isEmpty()) {
            errors.add(ctx, MustExplicitlyInstantiate(parameterized))
        } else {
            val inOrderParameters = parameterized.typeParams
            if (inOrderParameters.size == explicitTypeArgs.size) {
                val substitution = Substitution(inOrderParameters, explicitTypeArgs)
                return substitution.apply(parameterized)
            } else {
                errors.add(ctx, IncorrectNumberOfTypeArgs(inOrderParameters.size, explicitTypeArgs.size))
            }
        }
        throw LanguageException(errors.toSet())
    }
}