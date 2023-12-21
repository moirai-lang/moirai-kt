package org.shardscript.semantics.phases.parse

import org.shardscript.semantics.core.Type

sealed class PostParseSymbol

class FunctionDefinitionSymbol(val name: String, val definition: FunctionPostParseAst): PostParseSymbol()

class ObjectDefinitionSymbol(val name: String, val definition: ObjectDefinitionPostParseAst): PostParseSymbol()

class RecordDefinitionSymbol(val name: String, val definition: RecordDefinitionPostParseAst): PostParseSymbol()

class LocalVariableSymbol(val name: String, val definition: LetPostParseAst): PostParseSymbol()

sealed interface PostParseSymbolTable {
    fun defineType(name: String, type: Type)

    fun fetchType(name: String): Type
}