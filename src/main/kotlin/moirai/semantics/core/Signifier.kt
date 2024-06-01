package moirai.semantics.core

internal sealed class Signifier : LanguageElement

internal sealed class TerminalSignifier : Signifier()
internal sealed class TerminalTextSignifier : TerminalSignifier()

internal data class FunctionTypeLiteral(
    override val ctx: SourceContext,
    val formalParamTypes: List<Signifier>,
    val returnType: Signifier
) : Signifier()

internal class ImplicitTypeLiteral(override val ctx: SourceContext) : TerminalSignifier() {
    private object HashCodeHelper

    override fun equals(other: Any?): Boolean = other != null && other is ImplicitTypeLiteral
    override fun hashCode(): Int = HashCodeHelper.hashCode()
}

internal data class ParameterizedSignifier(
    override val ctx: SourceContext,
    val tti: TerminalTextSignifier,
    val args: List<Signifier>
) : Signifier()

internal data class InvokeSignifier(
    override val ctx: SourceContext,
    val op: CostOperator,
    val args: List<Signifier>
) : Signifier()

internal data class Identifier(override val ctx: SourceContext, val name: String) : TerminalTextSignifier()
internal data class NamedCost(override val ctx: SourceContext, val name: String) : TerminalTextSignifier()
internal data class FinLiteral(override val ctx: SourceContext, val magnitude: Long) : TerminalSignifier()

internal fun linearizeIdentifiers(signifiers: List<Signifier>): List<TerminalSignifier> {
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

            is InvokeSignifier -> {
                signifier.args.forEach {
                    linearizeIdentifiersAux(it)
                }
            }

            is NamedCost -> Unit
        }
    }
    signifiers.forEach {
        linearizeIdentifiersAux(it)
    }
    return res
}
