package org.shardscript.composition

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.shardscript.semantics.core.*

class SyntaxErrorListener : BaseErrorListener() {
    private data class SyntaxErrorInternal(val msg: String, val line: Int, val charPositionInLine: Int)
    private val errorsInternal: MutableList<SyntaxErrorInternal> = ArrayList()

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        val errorMsg = msg ?: "Syntax error"
        errorsInternal.add(SyntaxErrorInternal(errorMsg, line, charPositionInLine))
    }

    fun hasErrors(): Boolean = errorsInternal.isNotEmpty()

    fun populateErrors(errors: LanguageErrors, fileName: String? = null) {
        errorsInternal.forEach { e ->
            errors.add(fileName?.let { InNamedSource(it, e.line, e.charPositionInLine) } ?: InUnnamedSource(e.line, e.charPositionInLine), SyntaxError(e.msg))
        }
    }
}