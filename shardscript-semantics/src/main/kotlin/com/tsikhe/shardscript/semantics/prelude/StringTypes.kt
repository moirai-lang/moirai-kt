package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.infer.StringInstantiation

fun stringType(
    booleanType: BasicTypeSymbol,
    intType: BasicTypeSymbol,
    charType: BasicTypeSymbol,
    listType: ParameterizedBasicTypeSymbol,
    langNS: Namespace
): ParameterizedBasicTypeSymbol {
    val stringType = ParameterizedBasicTypeSymbol(
        langNS,
        Lang.stringId,
        StringInstantiation(),
        userTypeFeatureSupport
    )

    val stringTypeParam = ImmutableOmicronTypeParameter(stringType, Lang.stringTypeId)
    stringType.define(Lang.stringTypeId, stringTypeParam)
    stringType.typeParams = listOf(stringTypeParam)
    stringType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    StringEqualityOpMembers.members(stringType, stringTypeParam, booleanType).forEach { (name, plugin) ->
        stringType.define(GroundIdentifier(name), plugin)
    }

    val toCharArray = pluginToCharArray(stringType, stringTypeParam, charType, listType)
    stringType.define(GroundIdentifier(StringMethods.ToCharArray.idStr), toCharArray)

    val sizeId = GroundIdentifier(CollectionFields.Size.idStr)
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