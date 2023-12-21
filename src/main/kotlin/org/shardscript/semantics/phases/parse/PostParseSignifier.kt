package org.shardscript.semantics.phases.parse

import org.shardscript.semantics.core.LanguageElement
import org.shardscript.semantics.core.SourceContext

sealed class PostParseSignifier(override val ctx: SourceContext) : LanguageElement

sealed class PostParseTerminalSignifier(override val ctx: SourceContext) : PostParseSignifier(ctx)

data class PostParseFunctionTypeLiteral(
    override val ctx: SourceContext,
    val formalParamTypes: List<PostParseSignifier>,
    val returnType: PostParseSignifier
) : PostParseSignifier(ctx)

data class PostParseParameterizedSignifier(
    override val ctx: SourceContext,
    val tti: PostParseTerminalSignifier,
    val args: List<PostParseSignifier>
) : PostParseSignifier(ctx)

class PostParseImplicitTypeLiteral(override val ctx: SourceContext) : PostParseTerminalSignifier(ctx) {
    private object HashCodeHelper
    override fun equals(other: Any?): Boolean = other != null && other is PostParseImplicitTypeLiteral
    override fun hashCode(): Int = HashCodeHelper.hashCode()
}

data class PostParsePathSignifier(override val ctx: SourceContext, val elements: List<PostParseIdentifier>) : PostParseTerminalSignifier(ctx)
data class PostParseIdentifier(override val ctx: SourceContext, val name: String) : PostParseTerminalSignifier(ctx)
data class PostParseFinLiteral(override val ctx: SourceContext, val magnitude: Long) : PostParseTerminalSignifier(ctx)