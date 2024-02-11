package moirai.semantics.core

sealed class Signifier : LanguageElement

sealed class TerminalSignifier : Signifier()
sealed class TerminalTextSignifier : TerminalSignifier()

data class FunctionTypeLiteral(
    override val ctx: SourceContext,
    val formalParamTypes: List<Signifier>,
    val returnType: Signifier
) : Signifier()

class ImplicitTypeLiteral(override val ctx: SourceContext) : TerminalSignifier() {
    private object HashCodeHelper

    override fun equals(other: Any?): Boolean = other != null && other is ImplicitTypeLiteral
    override fun hashCode(): Int = HashCodeHelper.hashCode()
}

data class ParameterizedSignifier(
    override val ctx: SourceContext,
    val tti: TerminalTextSignifier,
    val args: List<Signifier>
) : Signifier()

data class Identifier(override val ctx: SourceContext, val name: String) : TerminalTextSignifier()
data class FinLiteral(override val ctx: SourceContext, val magnitude: Long) : TerminalSignifier()

fun linearizeIdentifiers(signifiers: List<Signifier>): List<TerminalSignifier> {
    val res: MutableList<TerminalSignifier> = ArrayList()
    fun linearizeIdentifiersAux(signifier: Signifier) {
        when (signifier) {
            is FunctionTypeLiteral -> {
                signifier.formalParamTypes.forEach { linearizeIdentifiersAux(it) }
                linearizeIdentifiersAux(signifier.returnType)
            }
            is ImplicitTypeLiteral -> {
                res.add(signifier)
            }
            is ParameterizedSignifier -> {
                res.add(signifier.tti)
                signifier.args.forEach {
                    linearizeIdentifiersAux(it)
                }
            }
            is Identifier -> {
                res.add(signifier)
            }
            is FinLiteral -> {
                res.add(signifier)
            }
        }
    }
    signifiers.forEach {
        linearizeIdentifiersAux(it)
    }
    return res
}
