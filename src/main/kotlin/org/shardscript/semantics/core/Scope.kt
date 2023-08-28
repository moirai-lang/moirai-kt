package org.shardscript.semantics.core

import org.shardscript.semantics.infer.Substitution

interface Scope<T> {
    fun define(identifier: Identifier, definition: T)
    fun exists(signifier: Signifier): Boolean
    fun existsHere(signifier: Signifier): Boolean
    fun fetch(signifier: Signifier): T
    fun fetchHere(signifier: Signifier): T
}

object NullSymbolTable : Scope<Symbol> {
    override fun define(identifier: Identifier, definition: Symbol) {
        langThrow(identifier.ctx, IdentifierCouldNotBeDefined(identifier))
    }

    override fun exists(signifier: Signifier): Boolean = false

    override fun existsHere(signifier: Signifier): Boolean = false

    override fun fetch(signifier: Signifier): Symbol {
        langThrow(signifier.ctx, IdentifierNotFound(signifier))
    }

    override fun fetchHere(signifier: Signifier): Symbol {
        langThrow(signifier.ctx, IdentifierNotFound(signifier))
    }
}

class SymbolTable(val parent: Scope<Symbol>) : Scope<Symbol> {
    private val identifierTable: MutableMap<Identifier, Symbol> = HashMap()

    fun toMap(): Map<Identifier, Symbol> = identifierTable.toMap()

    override fun define(identifier: Identifier, definition: Symbol) {
        if (identifierTable.containsKey(identifier)) {
            langThrow(identifier.ctx, IdentifierAlreadyExists(identifier))
        } else {
            identifierTable[identifier] = definition
        }
    }

    override fun exists(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> identifierTable.containsKey(signifier) || parent.exists(signifier)
            is PathSignifier -> {
                val first = signifier.elements.first()
                if (identifierTable.containsKey(first)) {
                    val pathRes = identifierTable[first]!!
                    if (pathRes is Namespace) {
                        val rest = signifier.elements.toMutableList()
                        rest.removeAt(0)
                        val next = if (rest.size == 1) {
                            rest.first()
                        } else {
                            PathSignifier(rest.toList())
                        }
                        pathRes.existsHere(next) || parent.exists(signifier)
                    } else {
                        parent.exists(signifier)
                    }
                } else {
                    parent.exists(signifier)
                }
            }
            is FunctionTypeLiteral -> signifier.formalParamTypes.all { exists(it) } && exists(signifier.returnType)
            is ParameterizedSignifier -> exists(signifier.tti) && signifier.args.all { exists(it) }
            is ImplicitTypeLiteral -> false
            is OmicronLiteral -> false
        }

    override fun existsHere(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> identifierTable.containsKey(signifier)
            is PathSignifier -> {
                val first = signifier.elements.first()
                if (identifierTable.containsKey(first)) {
                    val pathRes = identifierTable[first]!!
                    if (pathRes is Namespace) {
                        val rest = signifier.elements.toMutableList()
                        rest.removeAt(0)
                        val next = if (rest.size == 1) {
                            rest.first()
                        } else {
                            PathSignifier(rest.toList())
                        }
                        pathRes.existsHere(next)
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
            is FunctionTypeLiteral -> signifier.formalParamTypes.all { exists(it) } && exists(signifier.returnType)
            is ParameterizedSignifier -> existsHere(signifier.tti) && signifier.args.all { exists(it) }
            is ImplicitTypeLiteral -> false
            is OmicronLiteral -> false
        }

    override fun fetch(signifier: Signifier): Symbol =
        when (signifier) {
            is Identifier -> {
                if (identifierTable.containsKey(signifier)) {
                    identifierTable[signifier]!!
                } else {
                    parent.fetch(signifier)
                }
            }
            is PathSignifier -> {
                val first = signifier.elements.first()
                if (identifierTable.containsKey(first)) {
                    val pathRes = identifierTable[first]!!
                    if (pathRes is Namespace) {
                        val rest = signifier.elements.toMutableList()
                        rest.removeAt(0)
                        val next = if (rest.size == 1) {
                            rest.first()
                        } else {
                            PathSignifier(rest.toList())
                        }
                        pathRes.fetchHere(next)
                    } else {
                        parent.fetch(signifier)
                    }
                } else {
                    parent.fetch(signifier)
                }
            }
            is FunctionTypeLiteral -> FunctionTypeSymbol(
                signifier.formalParamTypes.map { fetch(it) },
                fetch(signifier.returnType)
            )
            is ParameterizedSignifier -> {
                when (val symbol = fetch(signifier.tti)) {
                    is ParameterizedRecordTypeSymbol -> {
                        val typeArgs = signifier.args.map { fetch(it) }
                        if (typeArgs.size != symbol.typeParams.size) {
                            langThrow(
                                signifier.ctx,
                                IncorrectNumberOfTypeArgs(symbol.typeParams.size, typeArgs.size)
                            )
                        } else {
                            val substitution = Substitution(symbol.typeParams, typeArgs)
                            substitution.apply(symbol)
                        }
                    }
                    is ParameterizedBasicTypeSymbol -> {
                        val typeArgs = signifier.args.map { fetch(it) }
                        if (typeArgs.size != symbol.typeParams.size) {
                            langThrow(
                                signifier.ctx,
                                IncorrectNumberOfTypeArgs(symbol.typeParams.size, typeArgs.size)
                            )
                        } else {
                            val substitution = Substitution(symbol.typeParams, typeArgs)
                            substitution.apply(symbol)
                        }
                    }
                    else -> langThrow(signifier.ctx, SymbolHasNoParameters(signifier))
                }
            }
            is ImplicitTypeLiteral -> langThrow(signifier.ctx, TypeSystemBug)
            is OmicronLiteral -> OmicronTypeSymbol(signifier.magnitude)
        }

    override fun fetchHere(signifier: Signifier): Symbol =
        when (signifier) {
            is Identifier -> {
                if (identifierTable.containsKey(signifier)) {
                    identifierTable[signifier]!!
                } else {
                    langThrow(signifier.ctx, IdentifierNotFound(signifier))
                }
            }
            is PathSignifier -> {
                val first = signifier.elements.first()
                if (identifierTable.containsKey(first)) {
                    val pathRes = identifierTable[first]!!
                    if (pathRes is Namespace) {
                        val rest = signifier.elements.toMutableList()
                        rest.removeAt(0)
                        val next = if (rest.size == 1) {
                            rest.first()
                        } else {
                            PathSignifier(rest.toList())
                        }
                        pathRes.fetchHere(next)
                    } else {
                        langThrow(signifier.ctx, IdentifierNotFound(signifier))
                    }
                } else {
                    langThrow(signifier.ctx, IdentifierNotFound(signifier))
                }
            }
            is FunctionTypeLiteral -> FunctionTypeSymbol(
                signifier.formalParamTypes.map { fetch(it) },
                fetch(signifier.returnType)
            )
            is ParameterizedSignifier -> {
                when (val symbol = fetchHere(signifier.tti)) {
                    is ParameterizedRecordTypeSymbol -> {
                        val typeArgs = signifier.args.map { fetch(it) }
                        if (typeArgs.size != symbol.typeParams.size) {
                            langThrow(
                                signifier.ctx,
                                IncorrectNumberOfTypeArgs(symbol.typeParams.size, typeArgs.size)
                            )
                        } else {
                            val substitution = Substitution(symbol.typeParams, typeArgs)
                            substitution.apply(symbol)
                        }
                    }
                    is ParameterizedBasicTypeSymbol -> {
                        val typeArgs = signifier.args.map { fetch(it) }
                        if (typeArgs.size != symbol.typeParams.size) {
                            langThrow(
                                signifier.ctx,
                                IncorrectNumberOfTypeArgs(symbol.typeParams.size, typeArgs.size)
                            )
                        } else {
                            val substitution = Substitution(symbol.typeParams, typeArgs)
                            substitution.apply(symbol)
                        }
                    }
                    else -> langThrow(signifier.ctx, SymbolHasNoParameters(signifier))
                }
            }
            is ImplicitTypeLiteral -> langThrow(signifier.ctx, TypeSystemBug)
            is OmicronLiteral -> OmicronTypeSymbol(signifier.magnitude)
        }
}