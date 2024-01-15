package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.*

private fun createGetFunction(
    costExpression: CostExpression,
    dictionaryType: ParameterizedBasicTypeSymbol,
    dictionaryKeyTypeParam: StandardTypeParameter,
    dictionaryValueTypeParam: StandardTypeParameter
) {
    val getId = Identifier(NotInSource, CollectionMethods.KeyLookup.idStr)
    val getMemberFunction = ParameterizedMemberPluginSymbol(
        dictionaryType,
        getId,
        DoubleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as DictionaryValue).evalGet(args.first())
    }
    getMemberFunction.typeParams = listOf(dictionaryKeyTypeParam, dictionaryValueTypeParam)
    getMemberFunction.costExpression = costExpression
    val getFormalParamId = Identifier(NotInSource, "key")
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
    val containsId = Identifier(NotInSource, CollectionMethods.Contains.idStr)
    val containsMemberFunction = ParameterizedMemberPluginSymbol(
        dictionaryType,
        containsId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as DictionaryValue).evalContains(args.first())
    }
    containsMemberFunction.typeParams = listOf(dictionaryKeyTypeParam)
    containsMemberFunction.costExpression = costExpression
    val containsFormalParamId = Identifier(NotInSource, "key")
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
    val setId = Identifier(NotInSource, CollectionMethods.KeyAssign.idStr)
    val setMemberFunction = ParameterizedMemberPluginSymbol(
        dictionaryType,
        setId,
        DoubleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as DictionaryValue).evalSet(args.first(), args[1])
    }
    setMemberFunction.typeParams = listOf(dictionaryKeyTypeParam, dictionaryValueTypeParam)
    setMemberFunction.costExpression = costExpression
    val keyFormalParamId = Identifier(NotInSource, "key")
    val keyFormalParam = FunctionFormalParameterSymbol(setMemberFunction, keyFormalParamId, dictionaryKeyTypeParam)
    setMemberFunction.define(keyFormalParamId, keyFormalParam)

    val valueFormalParamId = Identifier(NotInSource, "value")
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
    val removeId = Identifier(NotInSource, CollectionMethods.Remove.idStr)
    val removeMemberFunction = ParameterizedMemberPluginSymbol(
        dictionaryType,
        removeId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as DictionaryValue).evalRemove(args.first())
    }
    removeMemberFunction.typeParams = listOf(dictionaryKeyTypeParam)
    removeMemberFunction.costExpression = costExpression
    val removeFormalParamId = Identifier(NotInSource, "key")
    val removeFormalParam =
        FunctionFormalParameterSymbol(removeMemberFunction, removeFormalParamId, dictionaryKeyTypeParam)
    removeMemberFunction.define(removeFormalParamId, removeFormalParam)

    removeMemberFunction.formalParams = listOf(removeFormalParam)
    removeMemberFunction.returnType = unitType
    dictionaryType.define(removeId, removeMemberFunction)
}

fun createToImmutableDictionaryPlugin(
    mutableDictionaryType: ParameterizedBasicTypeSymbol,
    keyType: StandardTypeParameter,
    valueType: StandardTypeParameter,
    fin: MutableFinTypeParameter,
    dictionaryType: ParameterizedBasicTypeSymbol
) {
    val plugin = ParameterizedMemberPluginSymbol(
        mutableDictionaryType,
        Identifier(NotInSource, CollectionMethods.ToImmutableDictionary.idStr),
        TripleParentArgInstantiation
    ) { t: Value, _: List<Value> ->
        (t as DictionaryValue).evalToDictionary()
    }
    plugin.typeParams = listOf(keyType, valueType, fin)
    plugin.formalParams = listOf()
    val outputSubstitution = Substitution(dictionaryType.typeParams, listOf(keyType, valueType, fin))
    val outputType = outputSubstitution.apply(dictionaryType)
    plugin.returnType = outputType

    plugin.costExpression = ProductCostExpression(
        listOf(
            CommonCostExpressions.twoPass,
            fin
        )
    )
    mutableDictionaryType.define(plugin.identifier, plugin)
}

fun dictionaryCollectionType(
    architecture: Architecture,
    langNS: Scope<Symbol>,
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
    val dictionaryFinTypeParam = ImmutableFinTypeParameter(dictionaryType, Lang.dictionaryFinTypeId)
    dictionaryType.define(Lang.dictionaryFinTypeId, dictionaryFinTypeParam)
    dictionaryType.typeParams = listOf(dictionaryKeyTypeParam, dictionaryValueTypeParam, dictionaryFinTypeParam)
    dictionaryType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    createGetFunction(
        FinTypeSymbol(architecture.defaultNodeCost),
        dictionaryType,
        dictionaryKeyTypeParam,
        dictionaryValueTypeParam
    )

    createContainsFunction(
        FinTypeSymbol(architecture.defaultNodeCost),
        dictionaryType,
        dictionaryKeyTypeParam,
        booleanType
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
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
        dictionaryFinTypeParam,
        booleanType
    )
    createDictionaryNotEqualsMember(
        dictionaryType,
        dictionaryKeyTypeParam,
        dictionaryValueTypeParam,
        dictionaryFinTypeParam,
        booleanType
    )

    return dictionaryType
}

fun mutableDictionaryCollectionType(
    architecture: Architecture,
    langNS: Scope<Symbol>,
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
    val mutableDictionaryFinTypeParam =
        MutableFinTypeParameter(mutableDictionaryType, Lang.mutableDictionaryFinTypeId)
    mutableDictionaryType.define(Lang.mutableDictionaryFinTypeId, mutableDictionaryFinTypeParam)
    mutableDictionaryType.typeParams =
        listOf(mutableDictionaryKeyTypeParam, mutableDictionaryValueTypeParam, mutableDictionaryFinTypeParam)
    mutableDictionaryType.modeSelector = { args ->
        when (val fin = args[2]) {
            is FinTypeSymbol -> {
                MutableBasicTypeMode(fin.magnitude)
            }
            else -> {
                ImmutableBasicTypeMode
            }
        }
    }

    val constantFin = FinTypeSymbol(architecture.defaultNodeCost)
    createGetFunction(
        constantFin,
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam
    )

    createContainsFunction(
        FinTypeSymbol(architecture.defaultNodeCost),
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        booleanType
    )

    createRemoveFunction(
        constantFin,
        mutableDictionaryType,
        unitType,
        mutableDictionaryKeyTypeParam
    )

    createSetFunction(
        constantFin,
        mutableDictionaryType,
        unitType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam
    )

    createToImmutableDictionaryPlugin(
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam,
        mutableDictionaryFinTypeParam,
        dictionaryType
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
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
        mutableDictionaryFinTypeParam,
        booleanType
    )
    createMutableDictionaryNotEqualsMember(
        mutableDictionaryType,
        mutableDictionaryKeyTypeParam,
        mutableDictionaryValueTypeParam,
        mutableDictionaryFinTypeParam,
        booleanType
    )

    return mutableDictionaryType
}