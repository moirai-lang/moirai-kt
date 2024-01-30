package org.shardscript.semantics.core

import org.shardscript.semantics.infer.Substitution

interface Scope {
    fun define(identifier: Identifier, definition: Symbol)
    fun exists(signifier: Signifier): Boolean
    fun existsHere(signifier: Signifier): Boolean
    fun fetch(signifier: Signifier): Symbol
    fun fetchHere(signifier: Signifier): Symbol
    fun defineType(identifier: Identifier, definition: Type)
    fun typeExists(signifier: Signifier): Boolean
    fun typeExistsHere(signifier: Signifier): Boolean
    fun fetchType(signifier: Signifier): Type
    fun fetchTypeHere(signifier: Signifier): Type
}

object NullSymbolTable : Scope {
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

    override fun defineType(identifier: Identifier, definition: Type) {
        langThrow(identifier.ctx, IdentifierCouldNotBeDefined(identifier))
    }

    override fun typeExists(signifier: Signifier): Boolean = false

    override fun typeExistsHere(signifier: Signifier): Boolean = false

    override fun fetchType(signifier: Signifier): Type {
        langThrow(signifier.ctx, IdentifierNotFound(signifier))
    }

    override fun fetchTypeHere(signifier: Signifier): Type {
        langThrow(signifier.ctx, IdentifierNotFound(signifier))
    }
}

class SymbolTable(private val parent: Scope) : Scope {
    private val symbolTable: MutableMap<String, Symbol> = HashMap()
    private val typeTable: MutableMap<String, Type> = HashMap()

    fun symbolsToMap(): Map<String, Symbol> = symbolTable.toMap()
    fun typesToMap(): Map<String, Type> = typeTable.toMap()

    private fun toType(signifier: Signifier, symbol: Symbol): Type {
        if (symbol is Type) {
            return symbol
        } else {
            langThrow(SymbolIsNotAType(signifier))
        }
    }

    override fun define(identifier: Identifier, definition: Symbol) {
        if (symbolTable.containsKey(identifier.name) || typeTable.containsKey(identifier.name)) {
            langThrow(identifier.ctx, IdentifierAlreadyExists(identifier))
        } else {
            symbolTable[identifier.name] = definition
            typeTable[identifier.name] = ErrorType
        }
    }

    override fun exists(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> symbolTable.containsKey(signifier.name) || parent.exists(signifier)
            is FunctionTypeLiteral -> signifier.formalParamTypes.all { exists(it) } && exists(signifier.returnType)
            is ParameterizedSignifier -> exists(signifier.tti) && signifier.args.all { exists(it) }
            is ImplicitTypeLiteral -> false
            is FinLiteral -> false
        }

    override fun existsHere(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> symbolTable.containsKey(signifier.name)
            is FunctionTypeLiteral -> signifier.formalParamTypes.all { exists(it) } && exists(signifier.returnType)
            is ParameterizedSignifier -> existsHere(signifier.tti) && signifier.args.all { exists(it) }
            is ImplicitTypeLiteral -> false
            is FinLiteral -> false
        }

    override fun fetch(signifier: Signifier): Symbol =
        when (signifier) {
            is Identifier -> {
                if (symbolTable.containsKey(signifier.name)) {
                    symbolTable[signifier.name]!!
                } else {
                    parent.fetch(signifier)
                }
            }
            is FunctionTypeLiteral -> TypePlaceholder
            is ParameterizedSignifier -> TypePlaceholder
            is ImplicitTypeLiteral -> langThrow(signifier.ctx, TypeSystemBug)
            is FinLiteral -> TypePlaceholder
        }

    override fun fetchHere(signifier: Signifier): Symbol =
        when (signifier) {
            is Identifier -> {
                if (symbolTable.containsKey(signifier.name)) {
                    symbolTable[signifier.name]!!
                } else {
                    langThrow(signifier.ctx, IdentifierNotFound(signifier))
                }
            }

            is FunctionTypeLiteral -> TypePlaceholder

            is ParameterizedSignifier -> TypePlaceholder

            is ImplicitTypeLiteral -> langThrow(signifier.ctx, TypeSystemBug)
            is FinLiteral -> TypePlaceholder
        }

    override fun defineType(identifier: Identifier, definition: Type) {
        if (symbolTable.containsKey(identifier.name) || typeTable.containsKey(identifier.name)) {
            langThrow(identifier.ctx, IdentifierAlreadyExists(identifier))
        } else {
            symbolTable[identifier.name] = TypePlaceholder
            typeTable[identifier.name] = definition
        }
    }

    override fun typeExists(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> typeTable.containsKey(signifier.name) || parent.typeExists(signifier)
            is FunctionTypeLiteral -> signifier.formalParamTypes.all { typeExists(it) } && typeExists(signifier.returnType)
            is ParameterizedSignifier -> typeExists(signifier.tti) && signifier.args.all { typeExists(it) }
            is ImplicitTypeLiteral -> false
            is FinLiteral -> false
        }

    override fun typeExistsHere(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> typeTable.containsKey(signifier.name)
            is FunctionTypeLiteral -> signifier.formalParamTypes.all { typeExists(it) } && typeExists(signifier.returnType)
            is ParameterizedSignifier -> typeExistsHere(signifier.tti) && signifier.args.all { typeExists(it) }
            is ImplicitTypeLiteral -> false
            is FinLiteral -> false
        }

    override fun fetchType(signifier: Signifier): Type =
        when (signifier) {
            is Identifier -> {
                if (typeTable.containsKey(signifier.name)) {
                    typeTable[signifier.name]!!
                } else {
                    parent.fetchType(signifier)
                }
            }

            is FunctionTypeLiteral -> FunctionTypeSymbol(
                signifier.formalParamTypes.map { fetchType(it) },
                fetchType(signifier.returnType)
            )

            is ParameterizedSignifier -> {
                when (val symbol = fetchType(signifier.tti)) {
                    is ParameterizedRecordTypeSymbol -> {
                        val typeArgs = signifier.args.map { fetchType(it) }
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
                        val typeArgs = signifier.args.map { fetchType(it) }
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

    override fun fetchTypeHere(signifier: Signifier): Type =
        when (signifier) {
            is Identifier -> {
                if (typeTable.containsKey(signifier.name)) {
                    typeTable[signifier.name]!!
                } else {
                    langThrow(signifier.ctx, IdentifierNotFound(signifier))
                }
            }

            is FunctionTypeLiteral -> FunctionTypeSymbol(
                signifier.formalParamTypes.map { fetchType(it) },
                fetchType(signifier.returnType)
            )

            is ParameterizedSignifier -> {
                when (val symbol = fetchTypeHere(signifier.tti)) {
                    is ParameterizedRecordTypeSymbol -> {
                        val typeArgs = signifier.args.map { fetchType(it) }
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
                        val typeArgs = signifier.args.map { fetchType(it) }
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