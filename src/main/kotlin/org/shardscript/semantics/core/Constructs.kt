package org.shardscript.semantics.core

interface LanguageElement {
    val ctx: SourceContext
}
data class Binder(val identifier: Identifier, val ofType: Signifier)
data class FieldDef(val identifier: Identifier, val ofType: Signifier, val mutable: Boolean)
enum class TypeParameterKind {
    Type,
    Fin
}
data class TypeParameterDefinition(val identifier: Identifier, val type: TypeParameterKind)
