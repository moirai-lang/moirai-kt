package org.shardscript.semantics.core

sealed class Signifier(override val ctx: SourceContext) : LanguageElement

sealed class TerminalSignifier(override val ctx: SourceContext) : Signifier(ctx)
sealed class TerminalTextSignifier(override val ctx: SourceContext) : TerminalSignifier(ctx)

data class FunctionTypeLiteral(
    override val ctx: SourceContext,
    val formalParamTypes: List<Signifier>,
    val returnType: Signifier
) : Signifier(ctx)

class ImplicitTypeLiteral(override val ctx: SourceContext) : TerminalSignifier(ctx) {
    private object HashCodeHelper
    override fun equals(other: Any?): Boolean = other != null && other is ImplicitTypeLiteral
    override fun hashCode(): Int = HashCodeHelper.hashCode()
}

data class ParameterizedSignifier(
    override val ctx: SourceContext,
    val tti: TerminalTextSignifier,
    val args: List<Signifier>
) : Signifier(ctx)

data class PathSignifier(override val ctx: SourceContext, val elements: List<Identifier>) : TerminalTextSignifier(ctx)
data class Identifier(override val ctx: SourceContext, val name: String) : TerminalTextSignifier(ctx)
data class FinLiteral(override val ctx: SourceContext, val magnitude: Long) : TerminalSignifier(ctx)