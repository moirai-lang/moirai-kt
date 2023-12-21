package org.shardscript.semantics.core

sealed class SourceContext

data class InNamedSource(val fileName: String, val line: Int, val char: Int) : SourceContext()

data class InUnnamedSource(val line: Int, val char: Int) : SourceContext()
data object NotInSource : SourceContext()

sealed class ErrorType

// Frontend Errors
data object InvalidAssign : ErrorType()
data class InvalidNumberLiteral(val typeId: String, val text: String) : ErrorType()
data class DuplicateImport(val import: List<String>) : ErrorType()
data object SelfImport : ErrorType()

// General Errors
data class SyntaxError(val msg: String) : ErrorType()

// Feature Flags
data class LanguageError(
    val ctx: SourceContext,
    val error: ErrorType
) {
    val callStack: StackTraceElement = Exception().stackTrace[3]
}

class LanguageErrors {
    private val collectedErrors: MutableSet<LanguageError> = HashSet()

    fun add(ctx: SourceContext, error: ErrorType) {
        collectedErrors.add(LanguageError(ctx, error))
    }

    fun addAll(ctx: SourceContext, errors: Set<LanguageError>) {
        collectedErrors.addAll(mapErrorContexts(ctx, errors))
    }

    fun toSet() = collectedErrors.toSet()
}

data class LanguageException(val errors: Set<LanguageError>) : Exception()

fun langThrow(ctx: SourceContext, error: ErrorType): Nothing {
    throw LanguageException(setOf(LanguageError(ctx, error)))
}

fun langThrow(error: ErrorType): Nothing {
    throw LanguageException(setOf(LanguageError(NotInSource, error)))
}

fun mapErrorContexts(ctx: SourceContext, errors: Set<LanguageError>): Set<LanguageError> =
    errors.map {
        if (it.ctx == NotInSource) {
            LanguageError(ctx, it.error)
        } else {
            it
        }
    }.toSet()
