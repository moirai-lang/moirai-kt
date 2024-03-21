package moirai.composition

import moirai.semantics.core.*
import moirai.semantics.infer.Constraint
import moirai.semantics.infer.Substitution
import moirai.semantics.infer.constrainSymbol
import moirai.semantics.infer.createSubstitution

internal class PluginInstantiationValidation : GroundInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    private fun instantiateFunction(
        ctx: SourceContext,
        args: List<Ast>,
        parameterizedStaticPluginSymbol: ParameterizedStaticPluginSymbol,
        errors: LanguageErrors
    ): SymbolInstantiation {
        val inOrderParameters = parameterizedStaticPluginSymbol.typeParams
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
            return substitution.apply(parameterizedStaticPluginSymbol)
        } else {
            errors.add(
                ctx,
                IncorrectNumberOfArgs(parameterizedStaticPluginSymbol.formalParams.size, args.size)
            )
        }
        val substitution = Substitution(parameterizedStaticPluginSymbol.typeParams, listOf())
        return substitution.apply(parameterizedStaticPluginSymbol)
    }


    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        if (explicitTypeArgs.isNotEmpty()) {
            if (explicitTypeArgs.size == terminus.typeParams.size) {
                val substitution = Substitution(terminus.typeParams, explicitTypeArgs)
                return substitution.apply(terminus)
            } else {
                errors.add(ctx, IncorrectNumberOfTypeArgs(terminus.typeParams.size, explicitTypeArgs.size))
                throw LanguageException(errors.toSet())
            }
        } else {
            return instantiateFunction(ctx, args, terminus as ParameterizedStaticPluginSymbol, errors)
        }
    }
}