package org.shardscript.semantics.phases.parse

import org.shardscript.semantics.core.*

sealed class PostParseSymbol

class NotANamespaceSymbol(val name: String): PostParseSymbol()

data object ErrorSymbol: PostParseSymbol()

class PostParseSymbolTable {
    private val identifierTable: MutableMap<String, PostParseSymbol> = HashMap()

    fun define(errors: LanguageErrors, identifier: PostParseIdentifier, definition: PostParseSymbol) {
        if (identifierTable.containsKey(identifier.name)) {
            errors.add(identifier.ctx, IdentifierAlreadyExists(identifier.ctx, identifier.name))
        } else {
            identifierTable[identifier.name] = definition
        }
    }

    fun existsHere(identifier: PostParseIdentifier): Boolean {
        return identifierTable.containsKey(identifier.name)
    }

    fun fetch(errors: LanguageErrors, identifier: PostParseIdentifier): PostParseSymbol {
        return if (identifierTable.containsKey(identifier.name)) {
            identifierTable[identifier.name]!!
        } else {
            errors.add(identifier.ctx, IdentifierNotFound(identifier.ctx, identifier.name))
            ErrorSymbol
        }
    }
}

// Down-direction symbol table navigation. Used to resolve dot operator namespace paths to canonical symbol references.
class Namespace(val name: String): PostParseSymbol() {
    val symbolTable = PostParseSymbolTable()
}

// Up-direction symbol table navigation for variable name masking in code blocks.
sealed class PostParseScope {
    abstract fun define(errors: LanguageErrors, identifier: PostParseIdentifier, definition: PostParseSymbol)

    abstract fun fetch(errors: LanguageErrors, identifier: PostParseIdentifier): PostParseSymbol
}

data object NullPostParseScope: PostParseScope() {
    override fun define(errors: LanguageErrors, identifier: PostParseIdentifier, definition: PostParseSymbol) {
        errors.add(identifier.ctx, IdentifierCouldNotBeDefined(identifier.ctx, identifier.name))
    }

    override fun fetch(errors: LanguageErrors, identifier: PostParseIdentifier): PostParseSymbol {
        errors.add(identifier.ctx, IdentifierNotFound(identifier.ctx, identifier.name))
        return ErrorSymbol
    }
}

class LocalPostParseScope(private val parent: PostParseScope): PostParseScope() {
    private val symbolTable = PostParseSymbolTable()

    override fun define(errors: LanguageErrors, identifier: PostParseIdentifier, definition: PostParseSymbol) {
        symbolTable.define(errors, identifier, definition)
    }

    override fun fetch(errors: LanguageErrors, identifier: PostParseIdentifier): PostParseSymbol {
        return if (symbolTable.existsHere(identifier)) {
            symbolTable.fetch(errors, identifier)
        } else {
            parent.fetch(errors, identifier)
        }
    }
}
