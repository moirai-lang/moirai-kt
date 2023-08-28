package org.shardscript.semantics.core

sealed class Signifier : LanguageElement {
    override var ctx: SourceContext = NotInSource
}

sealed class TerminalSignifier : Signifier()
sealed class TerminalTextSignifier : TerminalSignifier()

data class FunctionTypeLiteral(
    val formalParamTypes: List<Signifier>,
    val returnType: Signifier
) : Signifier()

class ImplicitTypeLiteral : TerminalSignifier() {
    private object HashCodeHelper

    override fun equals(other: Any?): Boolean = other != null && other is ImplicitTypeLiteral
    override fun hashCode(): Int = HashCodeHelper.hashCode()
}

data class ParameterizedSignifier(val tti: TerminalTextSignifier, val args: List<Signifier>) : Signifier()
data class PathSignifier(val elements: List<Identifier>) : TerminalTextSignifier()
data class Identifier(val name: String) : TerminalTextSignifier()
data class FinLiteral(val magnitude: Long) : TerminalSignifier()

fun printIdentifier(signifier: Signifier): String =
    when (signifier) {
        is PathSignifier -> signifier.elements.joinToString(".", transform = { it.name })
        is Identifier -> signifier.name
        is ImplicitTypeLiteral -> langThrow(TypeSystemBug)
        is FinLiteral -> signifier.magnitude.toString()
        is FunctionTypeLiteral -> "(${
            signifier.formalParamTypes.joinToString(
                ", ",
                transform = { printIdentifier(it) })
        }) -> ${printIdentifier(signifier.returnType)}"
        is ParameterizedSignifier -> "${printIdentifier(signifier)}<${
            signifier.args.joinToString(
                ", ",
                transform = { printIdentifier(it) })
        }>"
    }

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
            is PathSignifier -> {
                res.add(signifier)
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
