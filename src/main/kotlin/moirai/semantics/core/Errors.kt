package moirai.semantics.core

sealed class SourceContext

data class InNamedSource(val fileName: String, val line: Int, val char: Int) : SourceContext()

data class InUnnamedSource(val line: Int, val char: Int) : SourceContext()
data object NotInSource : SourceContext()

sealed class ErrorKind

data class TypeErrorString(val value: String, val isError: Boolean = false)
data class SymbolErrorString(val value: String, val isError: Boolean = false)
data class SignifierErrorString(val value: String)

internal fun toError(type: Type): TypeErrorString {
    return TypeErrorString(
        when (type) {
            is BasicType -> type.identifier.name
            is ConstantFin -> "default"
            is Fin -> type.magnitude.toString()
            is FinTypeParameter -> type.identifier.name
            is ParameterHashCodeCost -> "HashCodeCost(${toError(type.typeParameter)})"
            is InstantiationHashCodeCost -> "HashCodeCost(${toError(type.instantiation)})"
            is MaxCostExpression -> "Max(${type.children.map { toError(it).value }.joinToString { "," }})"
            is ProductCostExpression -> "Mul(${type.children.map { toError(it).value }.joinToString { "," }})"
            is SumCostExpression -> "Sum(${type.children.map { toError(it).value }.joinToString { "," }})"
            ErrorType -> "error"
            is FunctionType -> "(${
                type.formalParamTypes.map { toError(it).value }.joinToString { "," }
            }) -> ${toError(type.returnType).value}"

            is GroundRecordType -> type.identifier.name
            is ObjectType -> type.identifier.name
            is PlatformObjectType -> type.identifier.name
            is PlatformSumObjectType -> type.identifier.name
            is StandardTypeParameter -> type.identifier.name
            is ParameterizedBasicType -> type.identifier.name
            is ParameterizedRecordType -> type.identifier.name
            is PlatformSumRecordType -> type.identifier.name
            is PlatformSumType -> type.identifier.name
            is TypeInstantiation -> "${toError(type.substitutionChain.terminus).value}<${
                type.substitutionChain.replayArgs().map { toError(it).value }.joinToString { "," }
            }>"
        }, type is ErrorType
    )
}

internal fun toError(symbol: Symbol): SymbolErrorString {
    return SymbolErrorString(
        when (symbol) {
            ErrorSymbol -> "error"
            is SymbolInstantiation -> "${toError(symbol.substitutionChain.terminus).value}<${
                symbol.substitutionChain.replayArgs().map { toError(it).value }.joinToString { "," }
            }>"

            is LambdaSymbol -> "lambda"
            is FieldSymbol -> symbol.identifier.name
            is FunctionFormalParameterSymbol -> symbol.identifier.name
            is LocalVariableSymbol -> symbol.identifier.name
            is GroundFunctionSymbol -> symbol.identifier.name
            is GroundMemberPluginSymbol -> symbol.identifier.name
            is ParameterizedFunctionSymbol -> symbol.identifier.name
            is ParameterizedMemberPluginSymbol -> symbol.identifier.name
            is GroundStaticPluginSymbol -> symbol.identifier.name
            is ParameterizedStaticPluginSymbol -> symbol.identifier.name
            is PlatformFieldSymbol -> symbol.identifier.name
            TypePlaceholder -> "_"
        }, symbol is ErrorSymbol
    )
}

internal fun toError(signifier: Signifier): SignifierErrorString {
    return SignifierErrorString(
        when (signifier) {
            is FunctionTypeLiteral -> "(${
                signifier.formalParamTypes.map { toError(it).value }.joinToString { "," }
            }) -> ${toError(signifier.returnType).value}"

            is ParameterizedSignifier -> "${toError(signifier.tti).value}<${
                signifier.args.map { toError(it).value }.joinToString { "," }
            }>"

            is FinLiteral -> signifier.magnitude.toString()
            is ImplicitTypeLiteral -> "_"
            is Identifier -> signifier.name
            is InvokeSignifier -> "${signifier.op.idStr}(${
                signifier.args.map { toError(it).value }.joinToString { "," }
            })"
        }
    )
}

interface SymbolHostErrorType {
    val symbols: List<SymbolErrorString>
}

interface TypeHostErrorType {
    val types: List<TypeErrorString>
}

// Frontend Errors
data object InvalidAssign : ErrorKind()
data class InvalidIntegerLiteral(val typeId: String, val text: String) : ErrorKind()
data class InvalidFinLiteral(val text: String) : ErrorKind()
data class DuplicateImport(val import: List<String>) : ErrorKind()
data class NoSuchFile(val import: List<String>) : ErrorKind()
data object SelfImport : ErrorKind()
data object RecursiveNamespaceDetected : ErrorKind()

data class ImpossibleState(val msg: String) : ErrorKind()

// Define errors
data class IdentifierCouldNotBeDefined(val identifier: SignifierErrorString) : ErrorKind()
data class IdentifierAlreadyExists(val identifier: SignifierErrorString) : ErrorKind()

// Fetch errors
data class IdentifierNotFound(val signifier: SignifierErrorString) : ErrorKind()
data class SymbolHasNoParameters(val identifier: SignifierErrorString) : ErrorKind()
data class SymbolHasNoFields(val signifier: SignifierErrorString, val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class SymbolHasNoMembers(val signifier: SignifierErrorString, val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

// TypeErrorString errors
data class TypeMismatch(val expected: TypeErrorString, val actual: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(expected, actual)
}

data class FinMismatch(val expected: Long, val actual: Long) : ErrorKind()

data class IncorrectNumberOfArgs(val expected: Int, val actual: Int) : ErrorKind()
data class IncorrectNumberOfTypeArgs(val expected: Int, val actual: Int) : ErrorKind()
data class InvalidRef(val symbol: SymbolErrorString) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<SymbolErrorString> = listOf(symbol)
}

data object CannotInstantiate : ErrorKind()
data class TypeInferenceFailed(val typeParam: TypeErrorString) : ErrorKind()

data class CannotUseRawType(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class CannotUsePlatformSumTypeMember(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class CannotFindBestType(override val types: List<TypeErrorString>) : ErrorKind(), TypeHostErrorType

data class SumTypeRequired(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class DuplicateCaseDetected(val name: String): ErrorKind()

data class MissingMatchCase(val name: String): ErrorKind()

data class UnknownCaseDetected(val name: String): ErrorKind()

// General Errors
data class SyntaxError(val msg: String) : ErrorKind()
data object TypeSystemBug : ErrorKind()

data object RuntimeCostExpressionEvalFailed : ErrorKind()
data object ExpectOtherError : ErrorKind()
data object CostOverLimit : ErrorKind()
data object InvalidCostUpperLimit: ErrorKind()
data class SymbolCouldNotBeApplied(val signifier: SignifierErrorString) : ErrorKind()
data class SymbolIsNotAField(val signifier: SignifierErrorString) : ErrorKind()
data class InvalidSource(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class IndexOutOfBounds(val index: Int, val size: Int) : ErrorKind()

data class ParameterizedGroundMismatch(val ground: SignifierErrorString, val parameterized: SignifierErrorString) : ErrorKind()

data class RecursiveFunctionDetected(val symbol: SymbolErrorString) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<SymbolErrorString> = listOf(symbol)
}

data class RecursiveRecordDetected(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class DictionaryArgsMustBePairs(val actual: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(actual)
}

data class ImmutableAssign(val symbol: SymbolErrorString) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<SymbolErrorString> = listOf(symbol)
}

data class InvalidDefinitionLocation(val identifier: SignifierErrorString) : ErrorKind()
data class InvalidPluginLocation(val identifier: SignifierErrorString) : ErrorKind()
data class IncompatibleString(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data object InvalidRangeArg : ErrorKind()
data object RandomRequiresIntLong : ErrorKind()

// Runtime errors
data class RuntimeFinViolation(val fin: Long, val elements: Long) : ErrorKind()
data object RuntimeImmutableViolation : ErrorKind()
data object DecimalInfiniteDivide : ErrorKind()

// TypeErrorString Parameter Errors
data class DuplicateTypeParameter(val identifier: SignifierErrorString) : ErrorKind()
data class TypeRequiresExplicit(val identifier: SignifierErrorString) : ErrorKind()
data class TypeRequiresExplicitFin(val identifier: SignifierErrorString) : ErrorKind()
data class TooManyElements(val fin: Long, val elements: Long) : ErrorKind()

data class MaskingTypeParameter(val identifier: SignifierErrorString) : ErrorKind()
data class CannotExplicitlyInstantiate(val symbol: SymbolErrorString) : ErrorKind(), SymbolHostErrorType {
    override val symbols: List<SymbolErrorString> = listOf(symbol)
}

// Fin Errors
data object CalculateCostFailed : ErrorKind()
data object NegativeFin : ErrorKind()

// Bans
data class CannotRefFunctionParam(val identifier: SignifierErrorString) : ErrorKind()
data class FunctionReturnType(val identifier: SignifierErrorString) : ErrorKind()
data class FunctionAssign(val identifier: SignifierErrorString) : ErrorKind()
data class RecordFieldFunctionType(val record: SignifierErrorString, val field: SignifierErrorString) : ErrorKind()
data class InvalidFinTypeSub(val typeParam: TypeErrorString, val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class InvalidStandardTypeSub(val typeParam: TypeErrorString, val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class SecondDegreeHigherOrderFunction(val identifier: SignifierErrorString) : ErrorKind()

// Feature Flags
data class ForEachFeatureBan(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class RecordFieldFeatureBan(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class ReturnTypeFeatureBan(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class FormalParamFeatureBan(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class TypeArgFeatureBan(val type: TypeErrorString) : ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}

data class InvalidCostExpressionFunctionName(val name: String): ErrorKind()
data object InvalidNamedCostExpressionArgs: ErrorKind()
data class TypeMustBeCostExpression(val type: TypeErrorString): ErrorKind(), TypeHostErrorType {
    override val types: List<TypeErrorString> = listOf(type)
}
data class PluginAlreadyExists(val name: String): ErrorKind()

data object ExpectedNamedScript: ErrorKind()
data class ExpectedTransientScript(val name: String): ErrorKind()

data class LanguageError(
    val ctx: SourceContext,
    val error: ErrorKind
) {
    val callStack: StackTraceElement = Exception().stackTrace[3]
}

class LanguageErrors {
    private val collectedErrors: MutableSet<LanguageError> = HashSet()

    internal fun add(ctx: SourceContext, error: ErrorKind) {
        collectedErrors.add(LanguageError(ctx, error))
    }

    internal fun addAll(ctx: SourceContext, errors: Set<LanguageError>) {
        collectedErrors.addAll(mapErrorContexts(ctx, errors))
    }

    fun toSet() = collectedErrors.toSet()
}

data class LanguageException(val errors: Set<LanguageError>) : Exception()

internal fun langThrow(ctx: SourceContext, error: ErrorKind): Nothing {
    throw LanguageException(setOf(LanguageError(ctx, error)))
}

internal fun langThrow(error: ErrorKind): Nothing {
    throw LanguageException(setOf(LanguageError(NotInSource, error)))
}

internal fun filterThrow(errors: Set<LanguageError>): Nothing {
    val filtered = errors.filter {
        when (it.error) {
            is SymbolHostErrorType -> {
                it.error.symbols.all { symbol -> !symbol.isError }
            }

            is TypeHostErrorType -> {
                it.error.types.all { type -> !type.isError }
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

internal fun mapErrorContexts(ctx: SourceContext, errors: Set<LanguageError>): Set<LanguageError> =
    errors.map {
        if (it.ctx == NotInSource) {
            LanguageError(ctx, it.error)
        } else {
            it
        }
    }.toSet()
