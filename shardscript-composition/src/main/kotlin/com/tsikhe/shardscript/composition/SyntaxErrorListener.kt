package com.tsikhe.shardscript.composition

import com.tsikhe.shardscript.semantics.core.InSource
import com.tsikhe.shardscript.semantics.core.LanguageErrors
import com.tsikhe.shardscript.semantics.core.NotInSource
import com.tsikhe.shardscript.semantics.core.SyntaxError
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

internal class SyntaxErrorListener(private val fileName: String? = null) : BaseErrorListener() {
    val errors = LanguageErrors()

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        val errorMsg = msg ?: "Syntax error"
        val ctx = if (fileName != null) {
            InSource(fileName, line, charPositionInLine)
        } else {
            NotInSource
        }
        errors.add(ctx, SyntaxError(errorMsg))
    }
}
