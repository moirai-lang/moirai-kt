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

class SymbolTable(private val parent: Scope<Symbol>) : Scope<Symbol> {
    private val identifierTable: MutableMap<Identifier, Symbol> = HashMap()

    fun toMap(): Map<Identifier, Symbol> = identifierTable.toMap()

    private fun toType(signifier: Signifier, symbol: Symbol): Type {
        if (symbol is Type) {
            return symbol
        } else {
            langThrow(SymbolIsNotAType(signifier))
        }
    }

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
            is FunctionTypeLiteral -> signifier.formalParamTypes.all { exists(it) } && exists(signifier.returnType)
            is ParameterizedSignifier -> exists(signifier.tti) && signifier.args.all { exists(it) }
            is ImplicitTypeLiteral -> false
            is FinLiteral -> false
        }

    override fun existsHere(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> identifierTable.containsKey(signifier)
            is FunctionTypeLiteral -> signifier.formalParamTypes.all { exists(it) } && exists(signifier.returnType)
            is ParameterizedSignifier -> existsHere(signifier.tti) && signifier.args.all { exists(it) }
            is ImplicitTypeLiteral -> false
            is FinLiteral -> false
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
            is FunctionTypeLiteral -> FunctionTypeSymbol(
                signifier.formalParamTypes.map { toType(signifier, fetch(it)) },
                toType(signifier, fetch(signifier.returnType))
            )
            is ParameterizedSignifier -> {
                when (val symbol = fetch(signifier.tti)) {
                    is ParameterizedRecordTypeSymbol -> {
                        val typeArgs = signifier.args.map { toType(signifier, fetch(it)) }
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
                        val typeArgs = signifier.args.map { toType(signifier, fetch(it)) }
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
            is FinLiteral -> FinTypeSymbol(signifier.magnitude)
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
            is FunctionTypeLiteral -> FunctionTypeSymbol(
                signifier.formalParamTypes.map { toType(signifier, fetch(it)) },
                toType(signifier, fetch(signifier.returnType))
            )
            is ParameterizedSignifier -> {
                when (val symbol = fetchHere(signifier.tti)) {
                    is ParameterizedRecordTypeSymbol -> {
                        val typeArgs = signifier.args.map { toType(signifier, fetch(it)) }
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
                        val typeArgs = signifier.args.map { toType(signifier, fetch(it)) }
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
            is FinLiteral -> FinTypeSymbol(signifier.magnitude)
        }
}