package org.shardscript.semantics.core

sealed class SourceContext

data class InSource(val fileName: String, val line: Int, val char: Int) : SourceContext()
object NotInSource : SourceContext()

sealed class ErrorType
interface SymbolHostErrorType {
    val symbols: List<Symbol>
}

// Frontend Errors
object InvalidAssign : ErrorType()
object InvalidEnumMember : ErrorType()
data class InvalidIntegerLiteral(val typeId: String, val text: String) : ErrorType()
data class DuplicateImport(val import: List<String>) : ErrorType()
data class NoSuchFile(val import: List<String>) : ErrorType()
data class AmbiguousSymbol(val signifier: Signifier) : ErrorType()
object SelfImport : ErrorType()
object RecursiveNamespaceDetected : ErrorType()
data class TransientSymbolBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class ImpossibleState(val msg: String) : ErrorType()
object CouldNotAcquireLock : ErrorType()

// Define errors
data class IdentifierCouldNotBeDefined(val identifier: Identifier) : ErrorType()
data class IdentifierAlreadyExists(val identifier: Identifier) : ErrorType()
data class SystemReservedNamespace(val identifier: Identifier) : ErrorType()

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
data class TypeMismatch(val expected: Symbol, val actual: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(expected, actual)
}

data class OmicronMismatch(val expected: Long, val actual: Long) : ErrorType()
data class IncorrectNumberOfArgs(val expected: Int, val actual: Int) : ErrorType()
data class IncorrectNumberOfTypeArgs(val expected: Int, val actual: Int) : ErrorType()
data class InvalidRef(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

object CannotInstantiate : ErrorType()
data class TypeInferenceFailed(val typeParam: TypeParameter) : ErrorType()
data class InvalidBodyType(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class CannotUseRawType(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class CannotFindBestType(override val symbols: List<Symbol>) : ErrorType(), SymbolHostErrorType

// General Errors
data class SyntaxError(val msg: String) : ErrorType()
object ResurrectWhitelistError : ErrorType()
object NoOwnerAccess : ErrorType()
object TypeSystemBug : ErrorType()
object ExpectOtherError : ErrorType()
object FilesMustHaveNamespace : ErrorType()
object CostOverLimit : ErrorType()
object InvalidCostUpperLimit: ErrorType()
data class SymbolCouldNotBeApplied(val signifier: Signifier) : ErrorType()
data class SymbolIsNotAMember(val signifier: Signifier) : ErrorType()
data class SymbolIsNotAField(val signifier: Signifier) : ErrorType()
data class InvalidNamespaceDot(val signifier: Signifier) : ErrorType()
data class PreludeScopeAlreadyExists(val signifier: Signifier) : ErrorType()
data class ImportScopeAlreadyExists(val signifier: Signifier) : ErrorType()
data class InvalidSource(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class IndexOutOfBounds(val index: Int, val size: Int) : ErrorType()

data class ParameterizedGroundMismatch(val ground: Identifier, val parameterized: Identifier) : ErrorType()

data class InvalidSwitchSource(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class RecursiveFunctionDetected(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class InvalidCase(val identifier: Identifier) : ErrorType()
data class InvalidSwitchIdentifier(val id: Signifier) : ErrorType()
data class DuplicateCase(val identifier: Identifier) : ErrorType()
data class DuplicateElse(val elseCount: Int) : ErrorType()
data class MissingCase(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

object UnnecessaryElse : ErrorType()

data class RecursiveRecordDetected(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class DictionaryArgsMustBePairs(val actual: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(actual)
}

data class ImmutableAssign(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class InvalidDefinitionLocation(val identifier: Identifier) : ErrorType()
data class IncompatibleString(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

object InvalidRangeArg : ErrorType()
object RandomRequiresIntLong : ErrorType()

// Runtime errors
data class RuntimeOmicronViolation(val omicron: Long, val elements: Long) : ErrorType()
object RuntimeImmutableViolation : ErrorType()
object RuntimeIntegerConversion : ErrorType()
object DecimalInfiniteDivide : ErrorType()
data class NamespaceNotAvailable(val namespace: List<String>) : ErrorType()

// Type Parameter Errors
data class DuplicateTypeParameter(val identifier: Identifier) : ErrorType()
data class TypeRequiresExplicit(val identifier: Identifier) : ErrorType()
data class TooManyElements(val omicron: Long, val elements: Long) : ErrorType()
data class CannotPartialApply(val typeParam: TypeParameter) : ErrorType()
data class EnumRecordTypeParamMissing(val identifier: Identifier) : ErrorType()
data class NotATypeParameter(val actual: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(actual)
}

data class ForeignTypeParameter(val identifier: Identifier) : ErrorType()

data class MaskingTypeParameter(val identifier: Identifier) : ErrorType()
data class CannotExplicitlyInstantiate(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class MustExplicitlyInstantiate(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

// Omicron Errors
object CalculateCostFailed : ErrorType()
object NegativeOmicron : ErrorType()

// Bans
data class CannotRefFunctionParam(val identifier: Identifier) : ErrorType()
data class FunctionReturnType(val identifier: Identifier) : ErrorType()
data class FunctionAssign(val identifier: Identifier) : ErrorType()
data class RecordFieldFunctionType(val record: Identifier, val field: Identifier) : ErrorType()
data class InvalidOmicronTypeSub(val typeParam: TypeParameter, val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class InvalidStandardTypeSub(val typeParam: TypeParameter, val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class InvalidAsCast(val signifier: Signifier) : ErrorType()
data class InvalidIsCheck(val signifier: Signifier) : ErrorType()

data class SecondDegreeHigherOrderFunction(val identifier: Identifier) : ErrorType()

// Feature Flags
data class SwitchFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class ForEachFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class MapFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class FlatMapFeatureBan(val symbol: Symbol) : ErrorType(), SymbolHostErrorType {
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
    val callStack: StackTraceElement = Exception().stackTrace[2]
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
