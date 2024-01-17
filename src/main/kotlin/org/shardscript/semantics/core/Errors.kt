package org.shardscript.semantics.core

sealed class SourceContext

data class InNamedSource(val fileName: String, val line: Int, val char: Int) : SourceContext()

data class InUnnamedSource(val line: Int, val char: Int) : SourceContext()
data object NotInSource : SourceContext()

sealed class ErrorType
interface SymbolHostErrorType {
    val symbols: List<Symbol>
}

interface TypeHostErrorType {
    val types: List<Type>
}

// Frontend Errors
data object InvalidAssign : ErrorType()
data class InvalidIntegerLiteral(val typeId: String, val text: String) : ErrorType()
data class DuplicateImport(val import: List<String>) : ErrorType()
data class NoSuchFile(val import: List<String>) : ErrorType()
data class AmbiguousSymbol(val signifier: Signifier) : ErrorType()
data object SelfImport : ErrorType()
data object RecursiveNamespaceDetected : ErrorType()

data class ImpossibleState(val msg: String) : ErrorType()

// Define errors
data class IdentifierCouldNotBeDefined(val identifier: Identifier) : ErrorType()
data class IdentifierAlreadyExists(val identifier: Identifier) : ErrorType()

// Fetch errors
data class IdentifierNotFound(val signifier: Signifier) : ErrorType()
data class SymbolHasNoParameters(val identifier: ParameterizedSignifier) : ErrorType()
data class SymbolHasNoFields(val signifier: Signifier, val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class SymbolHasNoMembers(val signifier: Signifier, val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

// Type errors
data class TypeMismatch(val expected: Type, val actual: Type) : ErrorType(), TypeHostErrorType {
    override val types: List<Type> = listOf(expected, actual)
}

data class FinMismatch(val expected: Long, val actual: Long) : ErrorType()
data class IncorrectNumberOfArgs(val expected: Int, val actual: Int) : ErrorType()
data class IncorrectNumberOfTypeArgs(val expected: Int, val actual: Int) : ErrorType()
data class InvalidRef(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data object CannotInstantiate : ErrorType()
data class TypeInferenceFailed(val typeParam: TypeParameter) : ErrorType()

data class CannotUseRawType(val type: Type) : ErrorType(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class CannotUseRawSymbol(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class CannotFindBestType(override val types: List<Type>) : ErrorType(), TypeHostErrorType

// General Errors
data class SyntaxError(val msg: String) : ErrorType()
data object ResurrectWhitelistError : ErrorType()
data object TypeSystemBug : ErrorType()
data object ExpectOtherError : ErrorType()
data object CostOverLimit : ErrorType()
data object InvalidCostUpperLimit: ErrorType()
data class SymbolCouldNotBeApplied(val signifier: Signifier) : ErrorType()
data class SymbolIsNotAField(val signifier: Signifier) : ErrorType()
data class SymbolIsNotAType(val signifier: Signifier) : ErrorType()
data class InvalidSource(val type: Type) : ErrorType(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class IndexOutOfBounds(val index: Int, val size: Int) : ErrorType()

data class ParameterizedGroundMismatch(val ground: Identifier, val parameterized: Identifier) : ErrorType()

data class RecursiveFunctionDetected(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class RecursiveRecordDetected(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class DictionaryArgsMustBePairs(val actual: Type) : ErrorType(), TypeHostErrorType {
    override val types: List<Type> = listOf(actual)
}

data class ImmutableAssign(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class InvalidDefinitionLocation(val identifier: Identifier) : ErrorType()
data class IncompatibleString(val type: Type) : ErrorType(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data object InvalidRangeArg : ErrorType()
data object RandomRequiresIntLong : ErrorType()

// Runtime errors
data class RuntimeFinViolation(val fin: Long, val elements: Long) : ErrorType()
data object RuntimeImmutableViolation : ErrorType()
data object DecimalInfiniteDivide : ErrorType()

// Type Parameter Errors
data class DuplicateTypeParameter(val identifier: Identifier) : ErrorType()
data class TypeRequiresExplicit(val identifier: Identifier) : ErrorType()
data class TooManyElements(val fin: Long, val elements: Long) : ErrorType()

data class ForeignTypeParameter(val identifier: Identifier) : ErrorType()

data class MaskingTypeParameter(val identifier: Identifier) : ErrorType()
data class CannotExplicitlyInstantiate(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

// Fin Errors
data object CalculateCostFailed : ErrorType()
data object NegativeFin : ErrorType()

// Bans
data class CannotRefFunctionParam(val identifier: Identifier) : ErrorType()
data class FunctionReturnType(val identifier: Identifier) : ErrorType()
data class FunctionAssign(val identifier: Identifier) : ErrorType()
data class RecordFieldFunctionType(val record: Identifier, val field: Identifier) : ErrorType()
data class InvalidFinTypeSub(val typeParam: TypeParameter, val type: Type) : ErrorType(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class InvalidStandardTypeSub(val typeParam: TypeParameter, val type: Type) : ErrorType(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class InvalidAsCast(val signifier: Signifier) : ErrorType()
data class InvalidIsCheck(val signifier: Signifier) : ErrorType()

data class SecondDegreeHigherOrderFunction(val identifier: Identifier) : ErrorType()

// Feature Flags
data class ForEachFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class RecordFieldFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class ReturnTypeFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class FormalParamFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class TypeArgFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

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

fun filterThrow(errors: Set<LanguageError>): Nothing {
    val filtered = errors.filter {
        if (it.error is SymbolHostErrorType) {
            it.error.symbols.all { symbol -> symbol !is ErrorSymbol }
        } else {
            true
        }
    }
    if (filtered.isEmpty()) {
        langThrow(TypeSystemBug)
    } else {
        throw LanguageException(filtered.toSet())
    }
}

fun mapErrorContexts(ctx: SourceContext, errors: Set<LanguageError>): Set<LanguageError> =
    errors.map {
        if (it.ctx == NotInSource) {
            LanguageError(ctx, it.error)
        } else {
            it
        }
    }.toSet()
