package moirai.eval

interface UserPlugin {
    val key: String
    fun evaluate(args: List<Value>): Value
}