package org.shardscript.semantics.core

import org.shardscript.semantics.phases.parse.PostParseIdentifier
import org.shardscript.semantics.phases.parse.PostParseSignifier

interface LanguageElement {
    val ctx: SourceContext
}
data class Binder(val identifier: PostParseIdentifier, val ofType: PostParseSignifier)
data class FieldDef(val identifier: PostParseIdentifier, val ofType: PostParseSignifier, val mutable: Boolean)
enum class TypeParameterKind {
    Type,
    Fin
}
data class TypeParameterDefinition(val identifier: PostParseIdentifier, val type: TypeParameterKind)
