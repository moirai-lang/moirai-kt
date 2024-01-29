package org.shardscript.semantics.core

// This interface is an orthogonal concept to both Symbol and Type. This interface abstracts the entity
// which lies at the end of instantiation chains. The end of such chains might be a Symbol or a Type.
// Or perhaps in the future it might be something that is neither a symbol nor a type.
sealed interface RawTerminus {
    val typeParams: List<TypeParameter>
}