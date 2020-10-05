package com.tsikhe.shardscript.semantics.core

import java.math.BigInteger

sealed class Identifier : LanguageElement {
    override var ctx: SourceContext = NotInSource

    companion object {
        fun thisId() = GroundIdentifier("this")
    }
}

sealed class TerminalIdentifier : Identifier()
sealed class TerminalTextIdentifier : TerminalIdentifier()

data class FunctionTypeLiteral(
    val formalParamTypes: List<Identifier>,
    val returnType: Identifier
) : Identifier()

class ImplicitTypeLiteral : TerminalIdentifier() {
    private object HashCodeHelper

    override fun equals(other: Any?): Boolean = other != null && other is ImplicitTypeLiteral
    override fun hashCode(): Int = HashCodeHelper.hashCode()
}

data class ParameterizedIdentifier(val tti: TerminalTextIdentifier, val args: List<Identifier>) : Identifier()
data class PathIdentifier(val elements: List<GroundIdentifier>) : TerminalTextIdentifier()
data class GroundIdentifier(val name: String) : TerminalTextIdentifier()
data class OmicronLiteral(val magnitude: BigInteger) : TerminalIdentifier()

fun printIdentifier(identifier: Identifier): String =
    when (identifier) {
        is PathIdentifier -> identifier.elements.joinToString(".", transform = { it.name })
        is GroundIdentifier -> identifier.name
        is ImplicitTypeLiteral -> langThrow(TypeSystemBug)
        is OmicronLiteral -> identifier.magnitude.toString()
        is FunctionTypeLiteral -> "(${
            identifier.formalParamTypes.joinToString(
                ", ",
                transform = { printIdentifier(it) })
        }) -> ${printIdentifier(identifier.returnType)}"
        is ParameterizedIdentifier -> "${printIdentifier(identifier)}<${
            identifier.args.joinToString(
                ", ",
                transform = { printIdentifier(it) })
        }>"
    }

fun linearizeIdentifiers(identifiers: List<Identifier>): List<TerminalIdentifier> {
    val res: MutableList<TerminalIdentifier> = ArrayList()
    fun linearizeIdentifiersAux(identifier: Identifier) {
        when (identifier) {
            is FunctionTypeLiteral -> {
                identifier.formalParamTypes.forEach { linearizeIdentifiersAux(it) }
                linearizeIdentifiersAux(identifier.returnType)
            }
            is ImplicitTypeLiteral -> {
                res.add(identifier)
            }
            is ParameterizedIdentifier -> {
                res.add(identifier.tti)
                identifier.args.forEach {
                    linearizeIdentifiersAux(it)
                }
            }
            is PathIdentifier -> {
                res.add(identifier)
            }
            is GroundIdentifier -> {
                res.add(identifier)

            }
            is OmicronLiteral -> {
                res.add(identifier)
            }
        }
    }
    identifiers.forEach {
        linearizeIdentifiersAux(it)
    }
    return res
}
