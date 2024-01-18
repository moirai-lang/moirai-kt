package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.StringInstantiation

fun stringType() {
    Lang.stringType.define(Lang.stringTypeId, Lang.stringTypeParam)
    Lang.stringType.typeParams = listOf(Lang.stringTypeParam)
    Lang.stringType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    StringEqualityOpMembers.members(Lang.stringType, Lang.stringTypeParam, Lang.booleanType).forEach { (name, plugin) ->
        Lang.stringType.define(Identifier(NotInSource, name), plugin)
    }

    val toCharArray = pluginToCharArray(Lang.stringType, Lang.stringTypeParam, Lang.charType, Lang.listType)
    Lang.stringType.define(Identifier(NotInSource, StringMethods.ToCharArray.idStr), toCharArray)

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        Lang.stringType,
        sizeId,
        Lang.intType
    ) { value ->
        (value as StringValue).fieldSize()
    }

    Lang.stringType.define(sizeId, sizeFieldSymbol)
    Lang.stringType.fields = listOf(sizeFieldSymbol)
}