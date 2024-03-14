package moirai.eval

/**
 * Whereas PluginDefLiteral represents the parse tree output of a plugin syntax file, this
 * interface abstracts the associated value-to-value mapping or implementation. Each existing
 * PluginDefLiteral must have exactly one implementation of UserPlugin, where the name field
 * on the former exactly matches the key field on the latter.
 */
interface UserPlugin {
    val key: String
    fun evaluate(args: List<Value>): Value
}