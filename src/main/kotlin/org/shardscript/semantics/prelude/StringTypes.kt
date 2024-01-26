package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*

object StringTypes {
    private val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        Lang.stringType,
        sizeId,
        Lang.intType
    )

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

        Lang.stringType.define(sizeId, sizeFieldSymbol)
        Lang.stringType.fields = listOf(sizeFieldSymbol)
    }
}