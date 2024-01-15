package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.StringInstantiation

fun stringType(
    booleanType: BasicTypeSymbol,
    intType: BasicTypeSymbol,
    charType: BasicTypeSymbol,
    listType: ParameterizedBasicTypeSymbol,
    langNS: Scope<Symbol>
): ParameterizedBasicTypeSymbol {
    val stringType = ParameterizedBasicTypeSymbol(
        langNS,
        Lang.stringId,
        StringInstantiation(),
        userTypeFeatureSupport
    )

    val stringTypeParam = ImmutableFinTypeParameter(stringType, Lang.stringTypeId)
    stringType.define(Lang.stringTypeId, stringTypeParam)
    stringType.typeParams = listOf(stringTypeParam)
    stringType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    StringEqualityOpMembers.members(stringType, stringTypeParam, booleanType).forEach { (name, plugin) ->
        stringType.define(Identifier(NotInSource, name), plugin)
    }

    val toCharArray = pluginToCharArray(stringType, stringTypeParam, charType, listType)
    stringType.define(Identifier(NotInSource, StringMethods.ToCharArray.idStr), toCharArray)

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        stringType,
        sizeId,
        intType
    ) { value ->
        (value as StringValue).fieldSize()
    }

    stringType.define(sizeId, sizeFieldSymbol)
    stringType.fields = listOf(sizeFieldSymbol)

    return stringType
}