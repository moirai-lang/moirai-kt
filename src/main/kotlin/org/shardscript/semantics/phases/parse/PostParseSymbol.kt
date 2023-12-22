package org.shardscript.semantics.phases.parse

import org.shardscript.semantics.core.*

sealed class PostParseSymbol

class FunctionDefinitionSymbol(val name: String, val definition: FunctionPostParseAst): PostParseSymbol()

class ObjectDefinitionSymbol(val name: String, val definition: ObjectDefinitionPostParseAst): PostParseSymbol()

class RecordDefinitionSymbol(val name: String, val definition: RecordDefinitionPostParseAst): PostParseSymbol()

class LocalVariableSymbol(val name: String, val definition: LetPostParseAst): PostParseSymbol()

class TypeParameterSymbol(val name: String, val definition: TypeParameterDefinition): PostParseSymbol()

class FormalParameterSymbol(val name: String, val definition: Binder): PostParseSymbol()

class SystemSymbol(val name: String): PostParseSymbol()

data object ErrorSymbol: PostParseSymbol()

class PostParseSymbolTable {
    private val identifierTable: MutableMap<String, PostParseSymbol> = HashMap()

    fun define(errors: LanguageErrors, identifier: PostParseIdentifier, definition: PostParseSymbol) {
        if (identifierTable.containsKey(identifier.name)) {
            errors.add(identifier.ctx, IdentifierAlreadyExists(identifier))
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
            errors.add(identifier.ctx, IdentifierNotFound(identifier))
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
        errors.add(identifier.ctx, IdentifierCouldNotBeDefined(identifier))
    }

    override fun fetch(errors: LanguageErrors, identifier: PostParseIdentifier): PostParseSymbol {
        errors.add(identifier.ctx, IdentifierNotFound(identifier))
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
