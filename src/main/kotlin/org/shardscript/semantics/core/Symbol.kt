package org.shardscript.semantics.core

import org.shardscript.semantics.infer.SubstitutionChain

/**
 * Core Primitives
 */
sealed class Symbol

sealed class SymbolTableElement: Symbol() {
    abstract val parent: Scope<Symbol>
}

sealed class NamedSymbolTableElement: SymbolTableElement() {
    abstract val identifier: Identifier
}

sealed class NamedSymbolWithMembers(
    override val parent: Scope<Symbol>,
    private val symbolTable: SymbolTable = SymbolTable(parent)
): NamedSymbolTableElement(), Scope<Symbol> by symbolTable

class Block(
    override val parent: Scope<Symbol>,
    private val symbolTable: SymbolTable = SymbolTable(parent)
) : SymbolTableElement(), Scope<Symbol> by symbolTable

class LocalVariableSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type,
    val mutable: Boolean
) : NamedSymbolTableElement()

class FunctionFormalParameterSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type
) : NamedSymbolTableElement() {
    var costMultiplier: CostExpression = CommonCostExpressions.defaultMultiplier
}

class GroundFunctionSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val originalCtx: SourceContext,
    val body: Ast
) : NamedSymbolWithMembers(parent) {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

class LambdaSymbol(
    override val parent: Scope<Symbol>,
    private val symbolTable: SymbolTable = SymbolTable(parent)
): SymbolTableElement(), Scope<Symbol> by symbolTable {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

class ParameterizedFunctionSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val originalCtx: SourceContext,
    val body: Ast
) : NamedSymbolWithMembers(parent), RawTerminus {
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

class FieldSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type,
    val mutable: Boolean
) : NamedSymbolTableElement()

data class PlatformFieldSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: BasicTypeSymbol
) : NamedSymbolTableElement()

/**
 * Plugins
 */
data class GroundMemberPluginSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier
) : NamedSymbolWithMembers(parent) {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedMemberPluginSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val instantiation: TwoTypeInstantiation
) : NamedSymbolWithMembers(parent), RawTerminus {
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedStaticPluginSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val instantiation: SingleTypeInstantiation,
) : NamedSymbolWithMembers(parent), RawTerminus {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}
