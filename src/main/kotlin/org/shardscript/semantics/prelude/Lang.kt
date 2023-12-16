package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.Identifier
import org.shardscript.semantics.core.NotInSource

object Lang {
    val unitId = Identifier(NotInSource, "Unit")
    val booleanId = Identifier(NotInSource, "Boolean")
    val charId = Identifier(NotInSource, "Char")
    val stringId = Identifier(NotInSource, "String")
    val decimalId = Identifier(NotInSource, "Decimal")

    val listId = Identifier(NotInSource, "List")
    val mutableListId = Identifier(NotInSource, "MutableList")
}