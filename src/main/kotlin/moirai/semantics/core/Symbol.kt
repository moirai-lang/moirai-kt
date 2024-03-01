package moirai.semantics.core

import moirai.semantics.infer.SubstitutionChain

/**
 * Core Primitives
 */
internal sealed class Symbol

internal data object ErrorSymbol: Symbol()

internal data object TypePlaceholder: Symbol()

internal sealed class SymbolTableElement: Symbol() {
    abstract val parent: Scope
}

internal sealed class NamedSymbolTableElement: SymbolTableElement() {
    abstract val identifier: Identifier
}

internal sealed class NamedSymbolWithMembers(
    override val parent: Scope,
    private val symbolTable: SymbolTable = SymbolTable(parent)
): NamedSymbolTableElement(), Scope by symbolTable

internal sealed class RawTerminusSymbol(
    override val parent: Scope
): NamedSymbolWithMembers(parent), RawTerminus

internal class SymbolInstantiation(
    val substitutionChain: SubstitutionChain<RawTerminusSymbol>
) : Symbol()

internal class LocalVariableSymbol(
    override val parent: Scope,
    override val identifier: Identifier,
    val ofTypeSymbol: Type,
    val mutable: Boolean
) : NamedSymbolTableElement()

internal class FunctionFormalParameterSymbol(
    override val parent: Scope,
    override val identifier: Identifier,
    val ofTypeSymbol: Type
) : NamedSymbolTableElement() {
    var costMultiplier: CostExpression = CommonCostExpressions.defaultMultiplier
}

internal class GroundFunctionSymbol(
    override val parent: Scope,
    override val identifier: Identifier,
    val body: Ast
) : NamedSymbolWithMembers(parent) {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionType(formalParams.map { it.ofTypeSymbol }, returnType)
}

internal class LambdaSymbol(
    override val parent: Scope,
    private val symbolTable: SymbolTable = SymbolTable(parent)
): SymbolTableElement(), Scope by symbolTable {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionType(formalParams.map { it.ofTypeSymbol }, returnType)
}

internal class ParameterizedFunctionSymbol(
    override val parent: Scope,
    override val identifier: Identifier,
    val body: Ast
) : RawTerminusSymbol(parent) {
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionType(formalParams.map { it.ofTypeSymbol }, returnType)
}

internal class FieldSymbol(
    override val parent: Scope,
    override val identifier: Identifier,
    val ofTypeSymbol: Type,
    val mutable: Boolean
) : NamedSymbolTableElement()

internal data class PlatformFieldSymbol(
    override val parent: Scope,
    override val identifier: Identifier,
    val ofTypeSymbol: BasicType
) : NamedSymbolTableElement()

/**
 * Plugins
 */
internal data class GroundMemberPluginSymbol(
    override val parent: Scope,
    override val identifier: Identifier
) : NamedSymbolWithMembers(parent) {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionType(formalParams.map { it.ofTypeSymbol }, returnType)
}

internal data class ParameterizedMemberPluginSymbol(
    override val parent: Scope,
    override val identifier: Identifier,
    val instantiationValidation: DotInstantiationValidation<RawTerminusSymbol, SymbolInstantiation>
) : RawTerminusSymbol(parent) {
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionType(formalParams.map { it.ofTypeSymbol }, returnType)
}

internal data class ParameterizedStaticPluginSymbol(
    override val parent: Scope,
    override val identifier: Identifier,
    val instantiationValidation: GroundInstantiationValidation<RawTerminusSymbol, SymbolInstantiation>,
) : RawTerminusSymbol(parent) {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionType(formalParams.map { it.ofTypeSymbol }, returnType)
}
