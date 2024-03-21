package moirai.composition

import moirai.semantics.core.*

internal class PluginInstantiationValidation : GroundInstantiationValidation<RawTerminusSymbol, SymbolInstantiation> {
    override fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: RawTerminusSymbol,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation {
        TODO("Not yet implemented")
    }
}