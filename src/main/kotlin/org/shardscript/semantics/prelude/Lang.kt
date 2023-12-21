package org.shardscript.semantics.prelude

import org.shardscript.semantics.phases.parse.PostParseIdentifier
import org.shardscript.semantics.core.NotInSource

object Lang {
    val unitId = PostParseIdentifier(NotInSource, "Unit")
    val booleanId = PostParseIdentifier(NotInSource, "Boolean")
    val charId = PostParseIdentifier(NotInSource, "Char")
    val stringId = PostParseIdentifier(NotInSource, "String")
    val decimalId = PostParseIdentifier(NotInSource, "Decimal")

    val listId = PostParseIdentifier(NotInSource, "List")
    val mutableListId = PostParseIdentifier(NotInSource, "MutableList")
}