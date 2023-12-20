package org.shardscript.semantics.phases.parse

import org.shardscript.semantics.core.Type

sealed class PostParseSymbol

sealed interface PostParseSymbolTable {
    fun defineType(name: String, type: Type)

    fun fetchType(name: String): Type
}