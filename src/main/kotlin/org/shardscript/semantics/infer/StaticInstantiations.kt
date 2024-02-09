package org.shardscript.semantics.infer

import org.shardscript.semantics.core.*
import org.shardscript.semantics.prelude.Lang
import kotlin.math.abs

object RangeInstantiation : SingleTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            errors.add(ctx, CannotExplicitlyInstantiate(terminus))
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
                val fin = Fin(abs(max - min).toLong())
                val substitution = Substitution(terminus.typeParams, listOf(fin))
                return substitution.apply(terminus)
            } else {
                errors.add(ctx, IncorrectNumberOfArgs(2, args.size))
            }
        }
        throw LanguageException(errors.toSet())
    }
}

object RandomInstantiation : SingleTypeInstantiation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        val parameterizedStaticPluginSymbol = terminus as ParameterizedStaticPluginSymbol
        val res = if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size != 1) {
                errors.add(ctx, IncorrectNumberOfTypeArgs(1, explicitTypeArgs.size))
                throw LanguageException(errors.toSet())
            } else {
                validateSubstitution(ctx, errors, terminus.typeParams.first(), explicitTypeArgs.first())
                val substitution = Substitution(terminus.typeParams, explicitTypeArgs)
                substitution.apply(terminus)
            }
        } else {
            val inOrderParameters = terminus.typeParams
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
                substitution.apply(terminus)
            } else {
                errors.add(
                    ctx,
                    IncorrectNumberOfArgs(parameterizedStaticPluginSymbol.formalParams.size, args.size)
                )
                throw LanguageException(errors.toSet())
            }
        }
        return when (getQualifiedName((res.substitutionChain).replayArgs().first())) {
            Lang.intId.name -> res
            else -> {
                errors.add(ctx, RandomRequiresIntLong)
                throw LanguageException(errors.toSet())
            }
        }
    }
}