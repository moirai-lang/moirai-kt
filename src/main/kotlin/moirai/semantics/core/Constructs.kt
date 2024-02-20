package moirai.semantics.core

internal interface LanguageElement {
    val ctx: SourceContext
}

internal data class Binder(val identifier: Identifier, val ofType: Signifier) {
    lateinit var symbol: Symbol
}

internal data class CaseBlock(val identifier: Identifier, val block: BlockAst) {
    lateinit var member: PlatformSumMember
    var itType: Type = ErrorType
}

internal data class FieldDef(val identifier: Identifier, val ofType: Signifier, val mutable: Boolean) {
    lateinit var symbol: Symbol
}

internal enum class TypeParameterKind {
    Type,
    Fin
}

internal data class TypeParameterDefinition(val identifier: Identifier, val type: TypeParameterKind)

internal interface SingleTypeInstantiation<T: RawTerminus, S> {
    fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: T,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): S
}

internal interface TwoTypeInstantiation<T: RawTerminus, S> {
    fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: T,
        identifier: Identifier,
        existingInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): S
}
