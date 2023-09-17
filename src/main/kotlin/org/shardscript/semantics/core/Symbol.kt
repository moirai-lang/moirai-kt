package org.shardscript.semantics.core

import org.shardscript.semantics.infer.SubstitutionChain
import org.shardscript.semantics.prelude.Lang

/**
 * Core Primitives
 */
sealed class Symbol

sealed interface Type

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

data object ErrorSymbol : Symbol(), Type

class PreludeTable(
    override val parent: Scope<Symbol>,
    private val scopeTable: MutableMap<Signifier, Scope<Symbol>> = HashMap()
) : SymbolTableElement(), Scope<Symbol> {
    fun register(signifier: Signifier, scope: Scope<Symbol>) {
        if (scopeTable.containsKey(signifier)) {
            langThrow(signifier.ctx, PreludeScopeAlreadyExists(signifier))
        } else {
            scopeTable[signifier] = scope
        }
    }

    override fun define(identifier: Identifier, definition: Symbol) {
        parent.define(identifier, definition)
    }

    override fun exists(signifier: Signifier): Boolean {
        return if (scopeTable.containsKey(signifier)) {
            scopeTable[signifier]!!.exists(signifier)
        } else {
            parent.exists(signifier)
        }
    }

    override fun existsHere(signifier: Signifier): Boolean {
        return if (scopeTable.containsKey(signifier)) {
            scopeTable[signifier]!!.existsHere(signifier)
        } else {
            parent.existsHere(signifier)
        }
    }

    override fun fetch(signifier: Signifier): Symbol {
        return if (scopeTable.containsKey(signifier)) {
            scopeTable[signifier]!!.fetch(signifier)
        } else {
            parent.fetch(signifier)
        }
    }

    override fun fetchHere(signifier: Signifier): Symbol {
        return if (scopeTable.containsKey(signifier)) {
            scopeTable[signifier]!!.fetchHere(signifier)
        } else {
            parent.fetchHere(signifier)
        }
    }
}

class ImportTable(
    override val parent: Scope<Symbol>,
    private val scopeTable: MutableMap<Signifier, MutableList<Scope<Symbol>>> = HashMap()
) : SymbolTableElement(), Scope<Symbol> {
    fun addAll(other: ImportTable) {
        scopeTable.putAll(other.scopeTable)
    }

    fun register(signifier: Signifier, scope: Scope<Symbol>) {
        if (scopeTable.containsKey(signifier)) {
            scopeTable[signifier]!!.add(scope)
        } else {
            scopeTable[signifier] = mutableListOf(scope)
        }
    }

    override fun define(identifier: Identifier, definition: Symbol) {
        parent.define(identifier, definition)
    }

    override fun exists(signifier: Signifier): Boolean {
        return if (scopeTable.containsKey(signifier)) {
            val scopes = scopeTable[signifier]!!
            if (scopes.size > 1) {
                langThrow(signifier.ctx, AmbiguousSymbol(signifier))
            }
            scopes.first().exists(signifier)
        } else {
            parent.exists(signifier)
        }
    }

    override fun existsHere(signifier: Signifier): Boolean {
        return if (scopeTable.containsKey(signifier)) {
            val scopes = scopeTable[signifier]!!
            if (scopes.size > 1) {
                langThrow(signifier.ctx, AmbiguousSymbol(signifier))
            }
            scopes.first().existsHere(signifier)
        } else {
            parent.existsHere(signifier)
        }
    }

    override fun fetch(signifier: Signifier): Symbol {
        return if (scopeTable.containsKey(signifier)) {
            val scopes = scopeTable[signifier]!!
            if (scopes.size > 1) {
                langThrow(signifier.ctx, AmbiguousSymbol(signifier))
            }
            scopes.first().fetch(signifier)
        } else {
            parent.fetch(signifier)
        }
    }

    override fun fetchHere(signifier: Signifier): Symbol {
        return if (scopeTable.containsKey(signifier)) {
            val scopes = scopeTable[signifier]!!
            if (scopes.size > 1) {
                langThrow(signifier.ctx, AmbiguousSymbol(signifier))
            }
            scopes.first().fetchHere(signifier)
        } else {
            parent.fetchHere(signifier)
        }
    }
}

sealed class NamespaceBase(
    override val parent: Scope<Symbol>,
    val symbolTable: SymbolTable = SymbolTable(parent)
) : SymbolTableElement(), Scope<Symbol> by symbolTable {
    override fun define(identifier: Identifier, definition: Symbol) {
        when (definition) {
            is NamespaceBase -> {
                if (existsHere(identifier)) {
                    when (val existing = fetchHere(identifier)) {
                        is NamespaceBase -> {
                            definition.symbolTable.toMap().entries.forEach {
                                existing.define(it.key, it.value)
                            }
                        }
                        else -> {
                            symbolTable.define(identifier, definition)
                        }
                    }
                } else {
                    symbolTable.define(identifier, definition)
                }
            }
            else -> {
                symbolTable.define(identifier, definition)
            }
        }
    }
}

data class SystemRootNamespace(
    override val parent: Scope<Symbol>
) : NamespaceBase(parent)

data class UserRootNamespace(
    override val parent: Scope<Symbol>
) : NamespaceBase(parent) {
    override fun define(identifier: Identifier, definition: Symbol) {
        if (identifier == Lang.shardId) {
            langThrow(identifier.ctx, SystemReservedNamespace(identifier))
        }
        super.define(identifier, definition)
    }
}

data class Namespace(
    override val parent: Scope<Symbol>,
    val identifier: Identifier
) : NamespaceBase(parent)

data class Block(
    override val parent: Scope<Symbol>,
    private val symbolTable: SymbolTable = SymbolTable(parent)
) : SymbolTableElement(), Scope<Symbol> by symbolTable

data class LocalVariableSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type,
    val mutable: Boolean
) : NamedSymbolTableElement()

/**
 * Function Primitives
 */
data class FunctionTypeSymbol(
    val formalParamTypes: List<Type>,
    val returnType: Type
) : Symbol(), Type

data class FunctionFormalParameterSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type
) : NamedSymbolTableElement() {
    var costMultiplier: CostExpression = CommonCostExpressions.defaultMultiplier
}

/**
 * Type/Fin Primitives
 */
sealed class TypeParameter : NamedSymbolTableElement(), Type

data class StandardTypeParameter(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier
) : TypeParameter(), Type

data class ImmutableFinTypeParameter(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier
) : TypeParameter(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class MutableFinTypeParameter(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier
) : TypeParameter(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class FinTypeSymbol(val magnitude: Long) : Symbol(), CostExpression, Type {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class SumCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class ProductCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

data class MaxCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override fun <R> accept(visitor: CostExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

/**
 * Generic Primitives
 */
sealed class ParameterizedSymbol(override val parent: Scope<Symbol>) : NamedSymbolWithMembers(parent) {
    abstract val typeParams: List<TypeParameter>
}

data class SymbolInstantiation(
    override val parent: Scope<Symbol>,
    val substitutionChain: SubstitutionChain,
    private val symbolTable: SymbolTable = SymbolTable(parent)
) : SymbolTableElement(), Scope<Symbol> by symbolTable, Type

/**
 * Function Types
 */
data class GroundFunctionSymbol(
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

data class LambdaSymbol(
    override val parent: Scope<Symbol>,
    private val symbolTable: SymbolTable = SymbolTable(parent)
): SymbolTableElement(), Scope<Symbol> by symbolTable {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedFunctionSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val originalCtx: SourceContext,
    val body: Ast
) : ParameterizedSymbol(parent) {
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

/**
 * Data Type Primitives
 */

data class FieldSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: Type,
    val mutable: Boolean
) : NamedSymbolTableElement()

data class PlatformFieldSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val ofTypeSymbol: BasicTypeSymbol,
    val accessor: (Value) -> Value
) : NamedSymbolTableElement()

/**
 * Data Types
 */
data class ObjectSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val featureSupport: FeatureSupport
) : NamedSymbolWithMembers(parent), Type

data class GroundRecordTypeSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val featureSupport: FeatureSupport
) : NamedSymbolWithMembers(parent), Type {
    lateinit var fields: List<FieldSymbol>
}

data class ParameterizedRecordTypeSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val featureSupport: FeatureSupport
) : ParameterizedSymbol(parent), Type {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<FieldSymbol>
}

/**
 * Basic Types
 */
data class BasicTypeSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier
) : NamedSymbolWithMembers(parent), Type

data class ParameterizedBasicTypeSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val instantiation: SingleTypeInstantiation,
    val featureSupport: FeatureSupport
) : ParameterizedSymbol(parent), Type {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var modeSelector: (List<Type>) -> BasicTypeMode
    lateinit var fields: List<PlatformFieldSymbol>
}

/**
 * Plugins
 */
data class GroundMemberPluginSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val plugin: (Value, List<Value>) -> Value
) : NamedSymbolWithMembers(parent) {
    fun invoke(t: Value, args: List<Value>): Value = plugin(t, args)

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedMemberPluginSymbol(
    override val parent: Scope<Symbol>,
    override val identifier: Identifier,
    val instantiation: TwoTypeInstantiation,
    val plugin: (Value, List<Value>) -> Value
) : ParameterizedSymbol(parent) {
    fun invoke(t: Value, args: List<Value>): Value = plugin(t, args)
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
    val plugin: (List<Value>) -> Value
) : ParameterizedSymbol(parent) {
    fun invoke(args: List<Value>): Value = plugin(args)
    override lateinit var typeParams: List<TypeParameter>
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Type
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(formalParams.map { it.ofTypeSymbol }, returnType)
}
