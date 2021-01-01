package com.tsikhe.shardscript.semantics.core

import com.tsikhe.shardscript.semantics.infer.Substitution
import com.tsikhe.shardscript.semantics.infer.SubstitutionChain
import com.tsikhe.shardscript.semantics.prelude.Lang
import java.math.BigInteger

/**
 * Core Primitives
 */
sealed class Symbol {
    abstract val parent: Scope<Symbol>
}

object ErrorSymbol : Symbol() {
    override val parent: Scope<Symbol>
        get() = langThrow(NoOwnerAccess)
}

object NullSymbolTable : Symbol(), Scope<Symbol> {
    override val parent: Scope<Symbol>
        get() = langThrow(NoOwnerAccess)

    override fun define(gid: GroundIdentifier, definition: Symbol) {
        langThrow(gid.ctx, IdentifierCouldNotBeDefined(gid))
    }

    override fun exists(identifier: Identifier): Boolean = false

    override fun existsHere(identifier: Identifier): Boolean = false

    override fun fetch(identifier: Identifier): Symbol {
        langThrow(identifier.ctx, IdentifierNotFound(identifier))
    }

    override fun fetchHere(identifier: Identifier): Symbol {
        langThrow(identifier.ctx, IdentifierNotFound(identifier))
    }
}

sealed class SymbolTable : Symbol(), Scope<Symbol> {
    private val gidTable: MutableMap<GroundIdentifier, Symbol> = HashMap()

    fun toMap(): Map<GroundIdentifier, Symbol> = gidTable.toMap()

    override fun define(gid: GroundIdentifier, definition: Symbol) {
        if (gidTable.containsKey(gid)) {
            langThrow(gid.ctx, IdentifierAlreadyExists(gid))
        } else {
            gidTable[gid] = definition
        }
    }

    override fun exists(identifier: Identifier): Boolean =
        when (identifier) {
            is GroundIdentifier -> gidTable.containsKey(identifier) || parent.exists(identifier)
            is PathIdentifier -> {
                val first = identifier.elements.first()
                if (gidTable.containsKey(first)) {
                    val pathRes = gidTable[first]!!
                    if (pathRes is Namespace) {
                        val rest = identifier.elements.toMutableList()
                        rest.removeAt(0)
                        val next = if (rest.size == 1) {
                            rest.first()
                        } else {
                            PathIdentifier(rest.toList())
                        }
                        pathRes.existsHere(next) || parent.exists(identifier)
                    } else {
                        parent.exists(identifier)
                    }
                } else {
                    parent.exists(identifier)
                }
            }
            is FunctionTypeLiteral -> identifier.formalParamTypes.all { exists(it) } && exists(identifier.returnType)
            is ParameterizedIdentifier -> exists(identifier.tti) && identifier.args.all { exists(it) }
            is ImplicitTypeLiteral -> false
            is OmicronLiteral -> false
        }

    override fun existsHere(identifier: Identifier): Boolean =
        when (identifier) {
            is GroundIdentifier -> gidTable.containsKey(identifier)
            is PathIdentifier -> {
                val first = identifier.elements.first()
                if (gidTable.containsKey(first)) {
                    val pathRes = gidTable[first]!!
                    if (pathRes is Namespace) {
                        val rest = identifier.elements.toMutableList()
                        rest.removeAt(0)
                        val next = if (rest.size == 1) {
                            rest.first()
                        } else {
                            PathIdentifier(rest.toList())
                        }
                        pathRes.existsHere(next)
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
            is FunctionTypeLiteral -> identifier.formalParamTypes.all { exists(it) } && exists(identifier.returnType)
            is ParameterizedIdentifier -> existsHere(identifier.tti) && identifier.args.all { exists(it) }
            is ImplicitTypeLiteral -> false
            is OmicronLiteral -> false
        }

    override fun fetch(identifier: Identifier): Symbol =
        when (identifier) {
            is GroundIdentifier -> {
                if (gidTable.containsKey(identifier)) {
                    gidTable[identifier]!!
                } else {
                    parent.fetch(identifier)
                }
            }
            is PathIdentifier -> {
                val first = identifier.elements.first()
                if (gidTable.containsKey(first)) {
                    val pathRes = gidTable[first]!!
                    if (pathRes is Namespace) {
                        val rest = identifier.elements.toMutableList()
                        rest.removeAt(0)
                        val next = if (rest.size == 1) {
                            rest.first()
                        } else {
                            PathIdentifier(rest.toList())
                        }
                        pathRes.fetchHere(next)
                    } else {
                        parent.fetch(identifier)
                    }
                } else {
                    parent.fetch(identifier)
                }
            }
            is FunctionTypeLiteral -> FunctionTypeSymbol(
                NullSymbolTable,
                identifier.formalParamTypes.map { fetch(it) },
                fetch(identifier.returnType)
            )
            is ParameterizedIdentifier -> {
                when (val symbol = fetch(identifier.tti)) {
                    is ParameterizedRecordTypeSymbol -> {
                        val typeArgs = identifier.args.map { fetch(it) }
                        if (typeArgs.size != symbol.typeParams.size) {
                            langThrow(
                                identifier.ctx,
                                IncorrectNumberOfTypeArgs(symbol.typeParams.size, typeArgs.size)
                            )
                        } else {
                            val substitution = Substitution(symbol.typeParams, typeArgs)
                            substitution.apply(symbol)
                        }
                    }
                    is ParameterizedBasicTypeSymbol -> {
                        val typeArgs = identifier.args.map { fetch(it) }
                        if (typeArgs.size != symbol.typeParams.size) {
                            langThrow(
                                identifier.ctx,
                                IncorrectNumberOfTypeArgs(symbol.typeParams.size, typeArgs.size)
                            )
                        } else {
                            val substitution = Substitution(symbol.typeParams, typeArgs)
                            substitution.apply(symbol)
                        }
                    }
                    else -> langThrow(identifier.ctx, SymbolHasNoParameters(identifier))
                }
            }
            is ImplicitTypeLiteral -> langThrow(identifier.ctx, TypeSystemBug)
            is OmicronLiteral -> OmicronTypeSymbol(identifier.magnitude)
        }

    override fun fetchHere(identifier: Identifier): Symbol =
        when (identifier) {
            is GroundIdentifier -> {
                if (gidTable.containsKey(identifier)) {
                    gidTable[identifier]!!
                } else {
                    langThrow(identifier.ctx, IdentifierNotFound(identifier))
                }
            }
            is PathIdentifier -> {
                val first = identifier.elements.first()
                if (gidTable.containsKey(first)) {
                    val pathRes = gidTable[first]!!
                    if (pathRes is Namespace) {
                        val rest = identifier.elements.toMutableList()
                        rest.removeAt(0)
                        val next = if (rest.size == 1) {
                            rest.first()
                        } else {
                            PathIdentifier(rest.toList())
                        }
                        pathRes.fetchHere(next)
                    } else {
                        langThrow(identifier.ctx, IdentifierNotFound(identifier))
                    }
                } else {
                    langThrow(identifier.ctx, IdentifierNotFound(identifier))
                }
            }
            is FunctionTypeLiteral -> FunctionTypeSymbol(
                NullSymbolTable,
                identifier.formalParamTypes.map { fetch(it) },
                fetch(identifier.returnType)
            )
            is ParameterizedIdentifier -> {
                when (val symbol = fetchHere(identifier.tti)) {
                    is ParameterizedRecordTypeSymbol -> {
                        val typeArgs = identifier.args.map { fetch(it) }
                        if (typeArgs.size != symbol.typeParams.size) {
                            langThrow(
                                identifier.ctx,
                                IncorrectNumberOfTypeArgs(symbol.typeParams.size, typeArgs.size)
                            )
                        } else {
                            val substitution = Substitution(symbol.typeParams, typeArgs)
                            substitution.apply(symbol)
                        }
                    }
                    is ParameterizedBasicTypeSymbol -> {
                        val typeArgs = identifier.args.map { fetch(it) }
                        if (typeArgs.size != symbol.typeParams.size) {
                            langThrow(
                                identifier.ctx,
                                IncorrectNumberOfTypeArgs(symbol.typeParams.size, typeArgs.size)
                            )
                        } else {
                            val substitution = Substitution(symbol.typeParams, typeArgs)
                            substitution.apply(symbol)
                        }
                    }
                    else -> langThrow(identifier.ctx, SymbolHasNoParameters(identifier))
                }
            }
            is ImplicitTypeLiteral -> langThrow(identifier.ctx, TypeSystemBug)
            is OmicronLiteral -> OmicronTypeSymbol(identifier.magnitude)
        }
}

class PreludeTable(
    override val parent: Scope<Symbol>,
    private val scopeTable: MutableMap<Identifier, Scope<Symbol>> = HashMap()
) : Symbol(), Scope<Symbol> {
    fun register(identifier: Identifier, scope: Scope<Symbol>) {
        if (scopeTable.containsKey(identifier)) {
            langThrow(identifier.ctx, PreludeScopeAlreadyExists(identifier))
        } else {
            scopeTable[identifier] = scope
        }
    }

    override fun define(gid: GroundIdentifier, definition: Symbol) {
        parent.define(gid, definition)
    }

    override fun exists(identifier: Identifier): Boolean {
        return if (scopeTable.containsKey(identifier)) {
            scopeTable[identifier]!!.exists(identifier)
        } else {
            parent.exists(identifier)
        }
    }

    override fun existsHere(identifier: Identifier): Boolean {
        return if (scopeTable.containsKey(identifier)) {
            scopeTable[identifier]!!.existsHere(identifier)
        } else {
            parent.existsHere(identifier)
        }
    }

    override fun fetch(identifier: Identifier): Symbol {
        return if (scopeTable.containsKey(identifier)) {
            scopeTable[identifier]!!.fetch(identifier)
        } else {
            parent.fetch(identifier)
        }
    }

    override fun fetchHere(identifier: Identifier): Symbol {
        return if (scopeTable.containsKey(identifier)) {
            scopeTable[identifier]!!.fetchHere(identifier)
        } else {
            parent.fetchHere(identifier)
        }
    }
}

class ImportTable(
    override val parent: Scope<Symbol>,
    private val scopeTable: MutableMap<Identifier, MutableList<Scope<Symbol>>> = HashMap()
) : Symbol(), Scope<Symbol> {
    fun addAll(other: ImportTable) {
        scopeTable.putAll(other.scopeTable)
    }

    fun register(identifier: Identifier, scope: Scope<Symbol>) {
        if (scopeTable.containsKey(identifier)) {
            scopeTable[identifier]!!.add(scope)
        } else {
            scopeTable[identifier] = mutableListOf(scope)
        }
    }

    override fun define(gid: GroundIdentifier, definition: Symbol) {
        parent.define(gid, definition)
    }

    override fun exists(identifier: Identifier): Boolean {
        return if (scopeTable.containsKey(identifier)) {
            val scopes = scopeTable[identifier]!!
            if (scopes.size > 1) {
                langThrow(identifier.ctx, AmbiguousSymbol(identifier))
            }
            scopes.first().exists(identifier)
        } else {
            parent.exists(identifier)
        }
    }

    override fun existsHere(identifier: Identifier): Boolean {
        return if (scopeTable.containsKey(identifier)) {
            val scopes = scopeTable[identifier]!!
            if (scopes.size > 1) {
                langThrow(identifier.ctx, AmbiguousSymbol(identifier))
            }
            scopes.first().existsHere(identifier)
        } else {
            parent.existsHere(identifier)
        }
    }

    override fun fetch(identifier: Identifier): Symbol {
        return if (scopeTable.containsKey(identifier)) {
            val scopes = scopeTable[identifier]!!
            if (scopes.size > 1) {
                langThrow(identifier.ctx, AmbiguousSymbol(identifier))
            }
            scopes.first().fetch(identifier)
        } else {
            parent.fetch(identifier)
        }
    }

    override fun fetchHere(identifier: Identifier): Symbol {
        return if (scopeTable.containsKey(identifier)) {
            val scopes = scopeTable[identifier]!!
            if (scopes.size > 1) {
                langThrow(identifier.ctx, AmbiguousSymbol(identifier))
            }
            scopes.first().fetchHere(identifier)
        } else {
            parent.fetchHere(identifier)
        }
    }
}

sealed class NamespaceBase : SymbolTable() {
    override fun define(gid: GroundIdentifier, definition: Symbol) {
        when (definition) {
            is NamespaceBase -> {
                if (existsHere(gid)) {
                    when (val existing = fetchHere(gid)) {
                        is NamespaceBase -> {
                            definition.toMap().entries.forEach {
                                existing.define(it.key, it.value)
                            }
                        }
                        else -> {
                            super.define(gid, definition)
                        }
                    }
                } else {
                    super.define(gid, definition)
                }
            }
            else -> {
                super.define(gid, definition)
            }
        }
    }
}

data class SystemRootNamespace(
    override val parent: Scope<Symbol>
) : NamespaceBase()

data class UserRootNamespace(
    override val parent: Scope<Symbol>
) : NamespaceBase() {
    override fun define(gid: GroundIdentifier, definition: Symbol) {
        if (gid == Lang.shardId) {
            langThrow(gid.ctx, SystemReservedNamespace(gid))
        }
        super.define(gid, definition)
    }
}

data class Namespace(
    override val parent: Scope<Symbol>,
    val gid: GroundIdentifier
) : NamespaceBase()

data class Block(
    override val parent: Scope<Symbol>
) : SymbolTable()

data class LocalVariableSymbol(
    override val parent: Scope<Symbol>,
    val gid: GroundIdentifier,
    val ofTypeSymbol: Symbol,
    val mutable: Boolean
) : Symbol()

sealed class NamedSymbolTable : SymbolTable() {
    abstract val gid: GroundIdentifier
}

/**
 * Function Primitives
 */
data class FunctionTypeSymbol(
    override val parent: Scope<Symbol>,
    val formalParamTypes: List<Symbol>,
    val returnType: Symbol
) : Symbol()

data class FunctionFormalParameterSymbol(
    override val parent: Scope<Symbol>,
    val gid: GroundIdentifier,
    val ofTypeSymbol: Symbol
) : Symbol() {
    var costMultiplier: CostExpression = CommonCostExpressions.defaultMultiplier
}

/**
 * Type/Omicron Primitives
 */
sealed class TypeParameter : Symbol() {
    abstract val gid: GroundIdentifier
}

data class StandardTypeParameter(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier
) : TypeParameter()

data class ImmutableOmicronTypeParameter(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier
) : TypeParameter(), CostExpression

data class MutableOmicronTypeParameter(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier
) : TypeParameter(), CostExpression

data class OmicronTypeSymbol(val magnitude: BigInteger) : Symbol(), CostExpression {
    override val parent: Scope<Symbol> = NullSymbolTable
}

data class SumCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override val parent: Scope<Symbol> = NullSymbolTable
}

data class ProductCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override val parent: Scope<Symbol> = NullSymbolTable
}

data class MaxCostExpression(val children: List<CostExpression>) : Symbol(), CostExpression {
    override val parent: Scope<Symbol> = NullSymbolTable
}

/**
 * Generic Primitives
 */
sealed class ParameterizedSymbol : NamedSymbolTable() {
    abstract val typeParams: List<TypeParameter>
}

data class SymbolInstantiation(
    override val parent: Scope<Symbol>,
    val substitutionChain: SubstitutionChain
) : Symbol()

/**
 * Function Types
 */
data class GroundFunctionSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val originalCtx: SourceContext,
    val body: Ast
) : NamedSymbolTable() {
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Symbol
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(NullSymbolTable, formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedFunctionSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val originalCtx: SourceContext,
    val body: Ast
) : ParameterizedSymbol() {
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Symbol
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(NullSymbolTable, formalParams.map { it.ofTypeSymbol }, returnType)
}

/**
 * Data Type Primitives
 */

data class FieldSymbol(
    override val parent: Scope<Symbol>,
    val gid: GroundIdentifier,
    val ofTypeSymbol: Symbol,
    val mutable: Boolean
) : Symbol()

data class PlatformFieldSymbol(
    override val parent: Scope<Symbol>,
    val gid: GroundIdentifier,
    val ofTypeSymbol: BasicTypeSymbol,
    val accessor: (Value) -> Value
) : Symbol()

/**
 * Data Types
 */
data class ObjectSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val featureSupport: FeatureSupport
) : NamedSymbolTable()

data class GroundRecordTypeSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val featureSupport: FeatureSupport
) : NamedSymbolTable() {
    lateinit var fields: List<FieldSymbol>
}

data class ParameterizedRecordTypeSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val featureSupport: FeatureSupport
) : ParameterizedSymbol() {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var fields: List<FieldSymbol>
}

/**
 * Basic Types
 */
data class BasicTypeSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier
) : NamedSymbolTable()

data class ParameterizedBasicTypeSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val instantiation: SingleTypeInstantiation,
    val featureSupport: FeatureSupport
) : ParameterizedSymbol() {
    override lateinit var typeParams: List<TypeParameter>
    lateinit var modeSelector: (List<Symbol>) -> BasicTypeMode
    lateinit var fields: List<PlatformFieldSymbol>
}

/**
 * Plugins
 */
data class GroundMemberPluginSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val plugin: (Value, List<Value>) -> Value
) : NamedSymbolTable() {
    fun invoke(t: Value, args: List<Value>): Value = plugin(t, args)

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Symbol
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(NullSymbolTable, formalParams.map { it.ofTypeSymbol }, returnType)
}

data class ParameterizedMemberPluginSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val instantiation: TwoTypeInstantiation,
    val plugin: (Value, List<Value>) -> Value
) : ParameterizedSymbol() {
    fun invoke(t: Value, args: List<Value>): Value = plugin(t, args)
    override lateinit var typeParams: List<TypeParameter>

    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Symbol
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(NullSymbolTable, formalParams.map { it.ofTypeSymbol }, returnType)

}

data class ParameterizedStaticPluginSymbol(
    override val parent: Scope<Symbol>,
    override val gid: GroundIdentifier,
    val instantiation: SingleTypeInstantiation,
    val plugin: (List<Value>) -> Value
) : ParameterizedSymbol() {
    fun invoke(args: List<Value>): Value = plugin(args)
    override lateinit var typeParams: List<TypeParameter>
    lateinit var formalParams: List<FunctionFormalParameterSymbol>
    lateinit var returnType: Symbol
    lateinit var costExpression: CostExpression

    fun type() = FunctionTypeSymbol(NullSymbolTable, formalParams.map { it.ofTypeSymbol }, returnType)
}
