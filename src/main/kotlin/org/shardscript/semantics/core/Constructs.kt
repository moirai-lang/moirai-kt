package org.shardscript.semantics.core

interface LanguageElement {
    val ctx: SourceContext
}

data class Binder(val identifier: Identifier, val ofType: Signifier) {
    lateinit var symbol: Symbol
}

data class FieldDef(val identifier: Identifier, val ofType: Signifier, val mutable: Boolean) {
    lateinit var symbol: Symbol
}

enum class TypeParameterKind {
    Type,
    Fin
}

data class TypeParameterDefinition(val identifier: Identifier, val type: TypeParameterKind)

interface SingleTypeInstantiation<T: RawTerminus, S> {
    fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: T,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): S
}

interface TwoTypeInstantiation<T: RawTerminus, S> {
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
