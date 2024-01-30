package org.shardscript.composition

import org.shardscript.grammar.ShardScriptLexer
import org.shardscript.grammar.ShardScriptParser
import org.shardscript.semantics.core.*
import org.shardscript.semantics.prelude.BinaryOperator
import org.shardscript.semantics.prelude.CollectionMethods
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import org.apache.commons.lang3.StringEscapeUtils

data class Parser(val grammar: ShardScriptParser, val listener: SyntaxErrorListener)

fun createParser(contents: String): Parser {
    val syntaxListener = SyntaxErrorListener()
    val stream = CharStreams.fromString(contents)
    val lexer = ShardScriptLexer(stream)
    lexer.removeErrorListeners()
    lexer.addErrorListener(syntaxListener)
    val grammar = ShardScriptParser(CommonTokenStream(lexer))
    grammar.removeErrorListeners()
    grammar.addErrorListener(syntaxListener)
    return Parser(grammar, syntaxListener)
}

internal fun createContext(fileName: String, token: Token): SourceContext =
    InNamedSource(fileName, token.line, token.charPositionInLine)

internal fun rewriteAsGroundApply(
    args: List<Ast>,
    gid: Identifier,
    sourceContext: SourceContext
): GroundApplyAst {
    val res = GroundApplyAst(sourceContext, gid, args)
    return res
}

internal fun rewriteAsDotApply(
    left: Ast,
    args: List<Ast>,
    op: BinaryOperator,
    sourceContext: SourceContext
): DotApplyAst {
    val res = DotApplyAst(sourceContext, left, Identifier(NotInSource, op.idStr), args)
    return res
}

internal fun rewriteAsDotApply(
    left: Ast,
    args: List<Ast>,
    collectionMethod: CollectionMethods,
    sourceContext: SourceContext
): DotApplyAst {
    val res = DotApplyAst(sourceContext, left, Identifier(NotInSource, collectionMethod.idStr), args)
    return res
}

fun resurrectString(original: String): String {
    val trimmed = original.replace(Regex.fromLiteral("^\""), "").replace(Regex.fromLiteral("\"$"), "")
    return StringEscapeUtils.unescapeJava(trimmed)
}

fun resurrectChar(original: String): Char {
    val trimmed = original.removeSurrounding("'")
    return StringEscapeUtils.unescapeJava(trimmed).toCharArray().first()
}
