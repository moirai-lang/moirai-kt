package org.shardscript.composition

import org.shardscript.semantics.core.InSource
import org.shardscript.semantics.core.LanguageErrors
import org.shardscript.semantics.core.NotInSource
import org.shardscript.semantics.core.SyntaxError
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

class SyntaxErrorListener : BaseErrorListener() {
    private data class SyntaxErrorInternal(val msg: String, val line: Int, val charPositionInLine: Int)

    private val errorsInternal: MutableList<SyntaxErrorInternal> = ArrayList()

    var fileName: String? = null

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

    fun populateErrors(): LanguageErrors {
        val errors = LanguageErrors()
        errorsInternal.forEach { e ->
            errors.add(fileName?.let { InSource(it, e.line, e.charPositionInLine) } ?: NotInSource, SyntaxError(e.msg))
        }
        return errors
    }
}