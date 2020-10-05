package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.infer.*

private fun createGetFunction(
    costExpression: CostExpression,
    dictionaryType: ParameterizedBasicTypeSymbol,
    dictionaryKeyTypeParam: StandardTypeParameter,
    dictionaryValueTypeParam: StandardTypeParameter
) {
    val getId = GroundIdentifier(CollectionMethods.KeyLookup.idStr)
    val getMemberFunction = ParameterizedMemberPluginSymbol(
        dictionaryType,
        getId,
        DoubleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as DictionaryValue).evalGet(args.first())
    }
    getMemberFunction.typeParams = listOf(dictionaryKeyTypeParam, dictionaryValueTypeParam)
    getMemberFunction.costExpression = costExpression
    val getFormalParamId = GroundIdentifier("key")
    val getFormalParam = FunctionFormalParameterSymbol(getMemberFunction, getFormalParamId, dictionaryKeyTypeParam)
    getMemberFunction.define(getFormalParamId, getFormalParam)

    getMemberFunction.formalParams = listOf(getFormalParam)
    getMemberFunction.returnType = dictionaryValueTypeParam
    dictionaryType.define(getId, getMemberFunction)
}

private fun createContainsFunction(
    costExpression: CostExpression,
    dictionaryType: ParameterizedBasicTypeSymbol,
    dictionaryKeyTypeParam: StandardTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val containsId = GroundIdentifier(CollectionMethods.Contains.idStr)
    val containsMemberFunction = ParameterizedMemberPluginSymbol(
        dictionaryType,
        containsId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as DictionaryValue).evalContains(args.first())
    }
    containsMemberFunction.typeParams = listOf(dictionaryKeyTypeParam)
    containsMemberFunction.costExpression = costExpression
    val containsFormalParamId = GroundIdentifier("key")
    val containsFormalParam =
        FunctionFormalParameterSymbol(containsMemberFunction, containsFormalParamId, dictionaryKeyTypeParam)
    containsMemberFunction.define(containsFormalParamId, containsFormalParam)

    containsMemberFunction.formalParams = listOf(containsFormalParam)
    containsMemberFunction.returnType = booleanType
    dictionaryType.define(containsId, containsMemberFunction)
}

private fun createSetFunction(
    costExpression: CostExpression,
    dictionaryType: ParameterizedBasicTypeSymbol,
    unitType: ObjectSymbol,
    dictionaryKeyTypeParam: StandardTypeParameter,
    dictionaryValueTypeParam: StandardTypeParameter
) {
    val setId = GroundIdentifier(CollectionMethods.KeyAssign.idStr)
    val setMemberFunction = ParameterizedMemberPluginSymbol(
        dictionaryType,
        setId,
        DoubleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as DictionaryValue).evalSet(args.first(), args[1])
    }
    setMemberFunction.typeParams = listOf(dictionaryKeyTypeParam, dictionaryValueTypeParam)
    setMemberFunction.costExpression = costExpression
    val keyFormalParamId = GroundIdentifier("key")
    val keyFormalParam = FunctionFormalParameterSymbol(setMemberFunction, keyFormalParamId, dictionaryKeyTypeParam)
    setMemberFunction.define(keyFormalParamId, keyFormalParam)

    val valueFormalParamId = GroundIdentifier("value")
    val valueFormalParam =
        FunctionFormalParameterSymbol(setMemberFunction, valueFormalParamId, dictionaryValueTypeParam)
    setMemberFunction.define(valueFormalParamId, valueFormalParam)

    setMemberFunction.formalParams = listOf(keyFormalParam, valueFormalParam)
    setMemberFunction.returnType = unitType
    dictionaryType.define(setId, setMemberFunction)
}

private fun createRemoveFunction(
    costExpression: CostExpression,
    dictionaryType: ParameterizedBasicTypeSymbol,
    unitType: ObjectSymbol,
    dictionaryKeyTypeParam: StandardTypeParameter
) {
    val removeId = GroundIdentifier(CollectionMethods.Remove.idStr)
    val removeMemberFunction = ParameterizedMemberPluginSymbol(
        dictionaryType,
        removeId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as DictionaryValue).evalRemove(args.first())
    }
    removeMemberFunction.typeParams = listOf(dictionaryKeyTypeParam)
    removeMemberFunction.costExpression = costExpression
    val removeFormalParamId = GroundIdentifier("key")
    val removeFormalParam =
        FunctionFormalParameterSymbol(removeMemberFunction, removeFormalParamId, dictionaryKeyTypeParam)
    removeMemberFunction.define(removeFormalParamId, removeFormalParam)

    removeMemberFunction.formalParams = listOf(removeFormalParam)
    removeMemberFunction.returnType = unitType
    dictionaryType.define(removeId, removeMemberFunction)
}

internal fun createToImmutableDictionaryPlugin(
    mutableDictionaryType: ParameterizedBasicTypeSymbol,
    keyType: StandardTypeParameter,
    valueType: StandardTypeParameter,
    omicron: MutableOmicronTypeParameter,
    dictionaryType: ParameterizedBasicTypeSymbol
) {
    val plugin = ParameterizedMemberPluginSymbol(
        mutableDictionaryType,
        GroundIdentifier(CollectionMethods.ToImmutableDictionary.idStr),
        TripleParentArgInstantiation
    ) { t: Value, _: List<Value> ->
        (t as DictionaryValue).evalToDictionary()
    }
    plugin.typeParams = listOf(keyType, valueType, omicron)
    plugin.formalParams = listOf()
    val outputSubstitution = Substitution(dictionaryType.typeParams, listOf(keyType, valueType, omicron))
    val outputType = outputSubstitution.apply(dictionaryType)
    plugin.returnType = outputType

    plugin.costExpression = ProductCostExpression(
        listOf(
            CommonCostExpressions.twoPass,
            omicron
        )
    )
    mutableDictionaryType.define(plugin.gid, plugin)
}

internal fun dictionaryCollectionType(
    architecture: Architecture,
    langNS: Namespace,
    booleanType: BasicTypeSymbol,
    intType: BasicTypeSymbol,
    pairType: ParameterizedRecordTypeSymbol
): ParameterizedBasicTypeSymbol {
    val dictionaryType = ParameterizedBasicTypeSymbol(
        langNS,
        Lang.dictionaryId,
        DictionaryInstantiation(pairType),
        immutableUnorderedFeatureSupport
    )
    val dictionaryKeyTypeParam = StandardTypeParameter(dictionaryType, Lang.dictionaryKeyTypeId)
    dictionaryType.define(Lang.dictionaryKeyTypeId, dictionaryKeyTypeParam)
    val dictionaryValueTypeParam = StandardTypeParameter(dictionaryType, Lang.dictionaryValueTypeId)
    dictionaryType.define(Lang.dictionaryValueTypeId, dictionaryValueTypeParam)
    val dictionaryOmicronTypeParam = ImmutableOmicronTypeParameter(dictionaryType, Lang.dictionaryOmicronTypeId)
    dictionaryType.define(Lang.dictionaryOmicronTypeId, dictionaryOmicronTypeParam)
    dictionaryType.typeParams = listOf(dictionaryKeyTypeParam, dictionaryValueTypeParam, dictionaryOmicronTypeParam)
    dictionaryType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    createGetFunction(
        OmicronTypeSymbol(architecture.defaultNodeCost),
        dictionaryType,
        dictionaryKeyTypeParam,
        dictionaryValueTypeParam
    )

    createContainsFunction(
        OmicronTypeSymbol(architecture.defaultNodeCost),
        dictionaryType,
        dictionaryKeyTypeParam,
        booleanType
    )

    val sizeId = GroundIdentifier(CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        dictionaryType,
        sizeId,
        intType
    ) { value ->
        (value as DictionaryValue).fieldSize()
    }

    dictionaryType.define(sizeId, sizeFieldSymbol)
    dictionaryType.fields = listOf(sizeFieldSymbol)

    createDictionaryEqualsMember(
        dictionaryType,
        dictionaryKeyTypeParam,
        dictionaryValueTypeParam,
        dictionaryOmicronTypeParam,
        booleanType
    )
    createDictionaryNotEqualsMember(
        dictionaryType,
        dictionaryKeyTypeParam,
        dictionaryValueTypeParam,
        dictionaryOmicronTypeParam,
        booleanType
    )

    return dictionaryType
}

internal fun mutableDictionaryCollectionType(
    architecture: Architecture,
    langNS: Namespace,
    booleanType: BasicTypeSymbol,
    intType: BasicTypeSymbol,
    unitType: ObjectSymbol,
    pairType: ParameterizedRecordTypeSymbol,
    dictionaryType: ParameterizedBasicTypeSymbol
): ParameterizedBasicTypeSymbol {
    val mutableDictionaryType = ParameterizedBasicTypeSymbol(
        langNS,
        Lang.mutableDictionaryId,
        MutableDictionaryInstantiation(pairType),
        noFeatureSupport
    )
    val mutableDictionaryKeyTypeParam = StandardTypeParameter(mutableDictionaryType, Lang.mutableDictionaryKeyTypeId)
    mutableDictionaryType.define(Lang.mutableDictionaryKeyTypeId, mutableDictionaryKeyTypeParam)
    val mutableDictionaryValueTypeParam =
        StandardTypeParameter(mutableDictionaryType, Lang.mutableDictionaryValueTypeId)
    mutableDictionaryType.define(Lang.mutableDictionaryValueTypeId, mutableDictionaryValueTypeParam)
    val mutableDictionaryOmicronTypeParam =
        MutableOmicronTypeParameter(mutableDictionaryType, Lang.mutableDictionaryOmicronTypeId)
    mutableDictionaryType.define(Lang.mutableDictionaryOmicronTypeId, mutableDictionaryOmicronTypeParam)
    mutableDictionaryType.typeParams =
        listOf(mutableDictionaryKeyTypeParam, mutableDictionaryValueTypeParam, mutableDictionaryOmicronTypeParam)
    mutableDictionaryType.modeSelector = { args ->
        when (val omicron = args[2]) {
            is OmicronTypeSymbol -> {
                MutableBasicTypeMode(omicron.magnitude)
            }
            else -> {
                ImmutableBasicTypeMode
            }
        }
    }

    val constantOmicron = OmicronTypeSymbol(architecture.defaultNodeCost)
    createGetFunction(
        constantOmicron,
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam
    )

    createContainsFunction(
        OmicronTypeSymbol(architecture.defaultNodeCost),
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        booleanType
    )

    createRemoveFunction(
        constantOmicron,
        mutableDictionaryType,
        unitType,
        mutableDictionaryKeyTypeParam
    )

    createSetFunction(
        constantOmicron,
        mutableDictionaryType,
        unitType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam
    )

    createToImmutableDictionaryPlugin(
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam,
        mutableDictionaryOmicronTypeParam,
        dictionaryType
    )

    val sizeId = GroundIdentifier(CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        mutableDictionaryType,
        sizeId,
        intType
    ) { value ->
        (value as DictionaryValue).fieldSize()
    }

    mutableDictionaryType.define(sizeId, sizeFieldSymbol)
    mutableDictionaryType.fields = listOf(sizeFieldSymbol)

    createMutableDictionaryEqualsMember(
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam,
        mutableDictionaryOmicronTypeParam,
        booleanType
    )
    createMutableDictionaryNotEqualsMember(
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam,
        mutableDictionaryOmicronTypeParam,
        booleanType
    )

    return mutableDictionaryType
}