package org.shardscript.semantics.phases.canonical

import org.shardscript.semantics.core.LanguageElement
import org.shardscript.semantics.core.SourceContext

sealed class CanonicalSignifier(override val ctx: SourceContext) : LanguageElement

sealed class CanonicalTerminalSignifier(override val ctx: SourceContext) : CanonicalSignifier(ctx)

data class CanonicalFunctionTypeLiteral(
    override val ctx: SourceContext,
    val formalParamTypes: List<CanonicalSignifier>,
    val returnType: CanonicalSignifier
) : CanonicalSignifier(ctx)

data class CanonicalParameterizedSignifier(
    override val ctx: SourceContext,
    val tti: CanonicalTerminalSignifier,
    val args: List<CanonicalSignifier>
) : CanonicalSignifier(ctx)

class CanonicalImplicitTypeLiteral(override val ctx: SourceContext) : CanonicalTerminalSignifier(ctx) {
    private object HashCodeHelper
    override fun equals(other: Any?): Boolean = other != null && other is CanonicalImplicitTypeLiteral
    override fun hashCode(): Int = HashCodeHelper.hashCode()
}

data class CanonicalIdentifier(override val ctx: SourceContext, val name: String) : CanonicalTerminalSignifier(ctx)
data class CanonicalFinLiteral(override val ctx: SourceContext, val magnitude: Long) : CanonicalTerminalSignifier(ctx)