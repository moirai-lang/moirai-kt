package moirai.semantics.core

sealed class SourceContext

data class InNamedSource(val fileName: String, val line: Int, val char: Int) : SourceContext()

data class InUnnamedSource(val line: Int, val char: Int) : SourceContext()
data object NotInSource : SourceContext()

sealed class ErrorKind
interface SymbolHostErrorType {
    val symbols: List<Symbol>
}

interface TypeHostErrorType {
    val types: List<Type>
}

// Frontend Errors
data object InvalidAssign : ErrorKind()
data class InvalidIntegerLiteral(val typeId: String, val text: String) : ErrorKind()
data class DuplicateImport(val import: List<String>) : ErrorKind()
data class NoSuchFile(val import: List<String>) : ErrorKind()
data object SelfImport : ErrorKind()
data object RecursiveNamespaceDetected : ErrorKind()

data class ImpossibleState(val msg: String) : ErrorKind()

// Define errors
data class IdentifierCouldNotBeDefined(val identifier: Identifier) : ErrorKind()
data class IdentifierAlreadyExists(val identifier: Identifier) : ErrorKind()

// Fetch errors
data class IdentifierNotFound(val signifier: Signifier) : ErrorKind()
data class SymbolHasNoParameters(val identifier: ParameterizedSignifier) : ErrorKind()
data class SymbolHasNoFields(val signifier: Signifier, val symbol: Symbol) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class SymbolHasNoMembers(val signifier: Signifier, val symbol: Symbol) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

// Type errors
data class TypeMismatch(val expected: Type, val actual: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(expected, actual)
}

data class FinMismatch(val expected: Long, val actual: Long) : ErrorKind()

data class IncorrectNumberOfArgs(val expected: Int, val actual: Int) : ErrorKind()
data class IncorrectNumberOfTypeArgs(val expected: Int, val actual: Int) : ErrorKind()
data class InvalidRef(val symbol: Symbol) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data object CannotInstantiate : ErrorKind()
data class TypeInferenceFailed(val typeParam: TypeParameter) : ErrorKind()

data class CannotUseRawType(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class CannotUsePlatformSumTypeMember(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class CannotFindBestType(override val types: List<Type>) : ErrorKind(), TypeHostErrorType

data class SumTypeRequired(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class DuplicateCaseDetected(val name: String): ErrorKind()

data class MissingMatchCase(val name: String): ErrorKind()

data class UnknownCaseDetected(val name: String): ErrorKind()

// General Errors
data class SyntaxError(val msg: String) : ErrorKind()
data object ResurrectWhitelistError : ErrorKind()
data object TypeSystemBug : ErrorKind()

data object RuntimeCostExpressionEvalFailed : ErrorKind()
data object ExpectOtherError : ErrorKind()
data object CostOverLimit : ErrorKind()
data object InvalidCostUpperLimit: ErrorKind()
data class SymbolCouldNotBeApplied(val signifier: Signifier) : ErrorKind()
data class SymbolIsNotAField(val signifier: Signifier) : ErrorKind()
data class InvalidSource(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class IndexOutOfBounds(val index: Int, val size: Int) : ErrorKind()

data class ParameterizedGroundMismatch(val ground: Identifier, val parameterized: Identifier) : ErrorKind()

data class RecursiveFunctionDetected(val symbol: Symbol) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class RecursiveRecordDetected(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class DictionaryArgsMustBePairs(val actual: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(actual)
}

data class ImmutableAssign(val symbol: Symbol) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

data class InvalidDefinitionLocation(val identifier: Identifier) : ErrorKind()
data class IncompatibleString(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data object InvalidRangeArg : ErrorKind()
data object RandomRequiresIntLong : ErrorKind()

// Runtime errors
data class RuntimeFinViolation(val fin: Long, val elements: Long) : ErrorKind()
data object RuntimeImmutableViolation : ErrorKind()
data object DecimalInfiniteDivide : ErrorKind()

// Type Parameter Errors
data class DuplicateTypeParameter(val identifier: Identifier) : ErrorKind()
data class TypeRequiresExplicit(val identifier: Identifier) : ErrorKind()
data class TypeRequiresExplicitFin(val identifier: Identifier) : ErrorKind()
data class TooManyElements(val fin: Long, val elements: Long) : ErrorKind()

data class MaskingTypeParameter(val identifier: Identifier) : ErrorKind()
data class CannotExplicitlyInstantiate(val symbol: Symbol) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<Symbol> = listOf(symbol)
}

// Fin Errors
data object CalculateCostFailed : ErrorKind()
data object NegativeFin : ErrorKind()

// Bans
data class CannotRefFunctionParam(val identifier: Identifier) : ErrorKind()
data class FunctionReturnType(val identifier: Identifier) : ErrorKind()
data class FunctionAssign(val identifier: Identifier) : ErrorKind()
data class RecordFieldFunctionType(val record: Identifier, val field: Identifier) : ErrorKind()
data class InvalidFinTypeSub(val typeParam: TypeParameter, val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class InvalidStandardTypeSub(val typeParam: TypeParameter, val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class InvalidAsCast(val signifier: Signifier) : ErrorKind()
data class InvalidIsCheck(val signifier: Signifier) : ErrorKind()

data class SecondDegreeHigherOrderFunction(val identifier: Identifier) : ErrorKind()

// Feature Flags
data class ForEachFeatureBan(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class RecordFieldFeatureBan(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class ReturnTypeFeatureBan(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class FormalParamFeatureBan(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class TypeArgFeatureBan(val type: Type) : ErrorKind(), TypeHostErrorType {
    override val types: List<Type> = listOf(type)
}

data class LanguageError(
    val ctx: SourceContext,
    val error: ErrorKind
) {
    val callStack: StackTraceElement = Exception().stackTrace[3]
}

class LanguageErrors {
    private val collectedErrors: MutableSet<LanguageError> = HashSet()

    fun add(ctx: SourceContext, error: ErrorKind) {
        collectedErrors.add(LanguageError(ctx, error))
    }

    fun addAll(ctx: SourceContext, errors: Set<LanguageError>) {
        collectedErrors.addAll(mapErrorContexts(ctx, errors))
    }

    fun toSet() = collectedErrors.toSet()
}

data class LanguageException(val errors: Set<LanguageError>) : Exception()

fun langThrow(ctx: SourceContext, error: ErrorKind): Nothing {
    throw LanguageException(setOf(LanguageError(ctx, error)))
}

fun langThrow(error: ErrorKind): Nothing {
    throw LanguageException(setOf(LanguageError(NotInSource, error)))
}

fun filterThrow(errors: Set<LanguageError>): Nothing {
    val filtered = errors.filter {
        when (it.error) {
            is SymbolHostErrorType -> {
                it.error.symbols.all { symbol -> symbol !is ErrorSymbol }
            }

            is TypeHostErrorType -> {
                it.error.types.all { type -> type !is ErrorType }
            }

            else -> {
                true
            }
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
