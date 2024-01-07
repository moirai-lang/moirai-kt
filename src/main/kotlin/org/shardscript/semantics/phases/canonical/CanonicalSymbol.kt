package org.shardscript.semantics.phases.canonical

import org.shardscript.semantics.core.*

sealed class CanonicalSymbol

class FunctionDefinitionSymbol(val name: String, val definition: FunctionCanonicalAst): CanonicalSymbol()

class ObjectDefinitionSymbol(val name: String, val definition: ObjectDefinitionCanonicalAst): CanonicalSymbol()

class RecordDefinitionSymbol(val name: String, val definition: RecordDefinitionCanonicalAst): CanonicalSymbol()

class LocalVariableSymbol(val name: String, val definition: LetCanonicalAst): CanonicalSymbol()

class TypeParameterSymbol(val name: String, val definition: TypeParameterDefinition): CanonicalSymbol()

class FormalParameterSymbol(val name: String, val definition: Binder): CanonicalSymbol()

class SystemSymbol(val name: String): CanonicalSymbol()

data object ErrorSymbol: CanonicalSymbol()

class CanonicalSymbolTable {
    private val identifierTable: MutableMap<String, CanonicalSymbol> = HashMap()

    fun define(errors: LanguageErrors, identifier: CanonicalIdentifier, definition: CanonicalSymbol) {
        if (identifierTable.containsKey(identifier.name)) {
            errors.add(identifier.ctx, IdentifierAlreadyExists(identifier.ctx, identifier.name))
        } else {
            identifierTable[identifier.name] = definition
        }
    }

    fun existsHere(identifier: CanonicalIdentifier): Boolean {
        return identifierTable.containsKey(identifier.name)
    }

    fun fetch(errors: LanguageErrors, identifier: CanonicalIdentifier): CanonicalSymbol {
        return if (identifierTable.containsKey(identifier.name)) {
            identifierTable[identifier.name]!!
        } else {
            errors.add(identifier.ctx, IdentifierNotFound(identifier.ctx, identifier.name))
            ErrorSymbol
        }
    }
}

// Up-direction symbol table navigation for variable name masking in code blocks.
sealed class CanonicalScope {
    abstract fun define(errors: LanguageErrors, identifier: CanonicalIdentifier, definition: CanonicalSymbol)

    abstract fun fetch(errors: LanguageErrors, identifier: CanonicalIdentifier): CanonicalSymbol
}

data object NullCanonicalScope: CanonicalScope() {
    override fun define(errors: LanguageErrors, identifier: CanonicalIdentifier, definition: CanonicalSymbol) {
        errors.add(identifier.ctx, IdentifierCouldNotBeDefined(identifier.ctx, identifier.name))
    }

    override fun fetch(errors: LanguageErrors, identifier: CanonicalIdentifier): CanonicalSymbol {
        errors.add(identifier.ctx, IdentifierNotFound(identifier.ctx, identifier.name))
        return ErrorSymbol
    }
}

class LocalCanonicalScope(private val parent: CanonicalScope): CanonicalScope() {
    private val symbolTable = CanonicalSymbolTable()

    override fun define(errors: LanguageErrors, identifier: CanonicalIdentifier, definition: CanonicalSymbol) {
        symbolTable.define(errors, identifier, definition)
    }

    override fun fetch(errors: LanguageErrors, identifier: CanonicalIdentifier): CanonicalSymbol {
        return if (symbolTable.existsHere(identifier)) {
            symbolTable.fetch(errors, identifier)
        } else {
            parent.fetch(errors, identifier)
        }
    }
}
