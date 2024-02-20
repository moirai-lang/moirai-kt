package moirai.composition

sealed class ScriptType {
    abstract fun fileName(): String
}

sealed class NamedScriptBase : ScriptType() {
    abstract val nameParts: List<String>
    override fun fileName() = nameParts.joinToString(".")
}

// A named artifact that can contain multiple imports, and can itself be imported.
data class NamedScript(override val nameParts: List<String>) : NamedScriptBase()

// An unnamed artifact that can contain exactly one import and cannot itself be imported.
data class TransientScript(override val nameParts: List<String>) : NamedScriptBase()

// An unnamed artifact with no imports and cannot itself be imported.
data object PureTransient : ScriptType() {
    override fun fileName() = "transient"
}