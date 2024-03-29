package moirai.semantics.prelude

import moirai.semantics.core.*

internal object StringTypes {
    private val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        Lang.stringType,
        sizeId,
        Lang.intType
    )

    fun stringType() {
        Lang.stringType.defineType(Lang.stringTypeId, Lang.stringTypeParam)
        Lang.stringType.typeParams = listOf(Lang.stringTypeParam)

        StringOpMembers.members()
            .forEach { (name, plugin) ->
                Lang.stringType.define(Identifier(NotInSource, name), plugin)
            }

        Lang.stringType.define(Identifier(NotInSource, StringMethods.ToCharArray.idStr), StringOpMembers.toCharArray)

        Lang.stringType.define(sizeId, sizeFieldSymbol)
        Lang.stringType.fields = listOf(sizeFieldSymbol)
    }
}