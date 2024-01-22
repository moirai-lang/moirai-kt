package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*

object StringTypes {
    fun stringType() {
        Lang.stringType.define(Lang.stringTypeId, Lang.stringTypeParam)
        Lang.stringType.typeParams = listOf(Lang.stringTypeParam)
        Lang.stringType.modeSelector = { _ ->
            ImmutableBasicTypeMode
        }

        StringOpMembers.members()
            .forEach { (name, plugin) ->
                Lang.stringType.define(Identifier(NotInSource, name), plugin)
            }

        Lang.stringType.define(Identifier(NotInSource, StringMethods.ToCharArray.idStr), StringOpMembers.toCharArray)

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
}