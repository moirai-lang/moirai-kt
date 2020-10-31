package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*

internal fun createSomeType(
    optionType: Scope<Symbol>,
    optionTypeParam: StandardTypeParameter
): ParameterizedRecordTypeSymbol {
    val someType = ParameterizedRecordTypeSymbol(
        optionType,
        Lang.someId,
        noFeatureSupport
    )
    someType.typeParams = listOf(optionTypeParam)
    val someField = FieldSymbol(someType, Lang.someFieldId, optionTypeParam, mutable = false)
    someType.fields = listOf(someField)
    return someType
}

internal fun createNoneType(
    optionType: Scope<Symbol>
): ObjectSymbol {
    return ObjectSymbol(
        optionType,
        Lang.noneId,
        noFeatureSupport
    )
}

internal fun createOptionType(
    langNS: Namespace
): ParameterizedCoproductSymbol {
    val optionType = ParameterizedCoproductSymbol(
        langNS,
        Lang.optionId,
        coproductFeatureSupport
    )
    val optionTypeParam = StandardTypeParameter(optionType, Lang.optionTypeParamId)
    optionType.define(Lang.optionTypeParamId, optionTypeParam)
    optionType.typeParams = listOf(optionTypeParam)

    val someType = createSomeType(optionType, optionTypeParam)
    val noneType = createNoneType(optionType)

    optionType.define(someType.gid, someType)
    optionType.define(noneType.gid, noneType)
    optionType.alternatives = listOf(someType, noneType)
    optionType.sourceType = optionTypeParam
    optionType.replaceParameters = { newSourceType, _ ->
        listOf(newSourceType)
    }

    return optionType
}
