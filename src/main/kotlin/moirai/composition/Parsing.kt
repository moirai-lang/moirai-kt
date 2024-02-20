package moirai.composition

import moirai.grammar.MoiraiLexer
import moirai.grammar.MoiraiParser
import moirai.semantics.core.*
import moirai.semantics.prelude.BinaryOperator
import moirai.semantics.prelude.CollectionMethods
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import org.apache.commons.lang3.StringEscapeUtils

internal data class Parser(val grammar: MoiraiParser, val listener: SyntaxErrorListener)

internal fun createParser(contents: String): Parser {
    val syntaxListener = SyntaxErrorListener()
    val stream = CharStreams.fromString(contents)
    val lexer = MoiraiLexer(stream)
    lexer.removeErrorListeners()
    lexer.addErrorListener(syntaxListener)
    val grammar = MoiraiParser(CommonTokenStream(lexer))
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

internal fun resurrectString(original: String): String {
    val trimmed = original.replace(Regex.fromLiteral("^\""), "").replace(Regex.fromLiteral("\"$"), "")
    return StringEscapeUtils.unescapeJava(trimmed)
}

internal fun resurrectChar(original: String): Char {
    val trimmed = original.removeSurrounding("'")
    return StringEscapeUtils.unescapeJava(trimmed).toCharArray().first()
}
