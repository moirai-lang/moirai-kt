package org.shardscript.composition

sealed class ScriptType {
    abstract fun fileName(): String
}

sealed class NamedScriptType : ScriptType() {
    abstract val nameParts: List<String>
    override fun fileName() = nameParts.joinToString(".")
}

// A named artifact that can contain multiple imports, and can itself be imported.
data class NamedArtifact(override val nameParts: List<String>) : NamedScriptType()

// An unnamed artifact that can contain exactly one import and cannot itself be imported.
data class TransientImport(override val nameParts: List<String>) : NamedScriptType()

// An unnamed artifact with no imports and cannot itself be imported.
data object PureTransient : ScriptType() {
    override fun fileName() = "transient"
}