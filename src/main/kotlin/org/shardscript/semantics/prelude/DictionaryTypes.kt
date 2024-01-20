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
    unitType: PlatformObjectSymbol,
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
    unitType: PlatformObjectSymbol,
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

fun dictionaryCollectionType() {
    Lang.dictionaryType.define(Lang.dictionaryKeyTypeId, Lang.dictionaryKeyTypeParam)
    Lang.dictionaryType.define(Lang.dictionaryValueTypeId, Lang.dictionaryValueTypeParam)
    Lang.dictionaryType.define(Lang.dictionaryFinTypeId, Lang.dictionaryFinTypeParam)
    Lang.dictionaryType.typeParams =
        listOf(Lang.dictionaryKeyTypeParam, Lang.dictionaryValueTypeParam, Lang.dictionaryFinTypeParam)
    Lang.dictionaryType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    createGetFunction(
        ConstantFinTypeSymbol,
        Lang.dictionaryType,
        Lang.dictionaryKeyTypeParam,
        Lang.dictionaryValueTypeParam
    )

    createContainsFunction(
        ConstantFinTypeSymbol,
        Lang.dictionaryType,
        Lang.dictionaryKeyTypeParam,
        Lang.booleanType
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        Lang.dictionaryType,
        sizeId,
        Lang.intType
    ) { value ->
        (value as DictionaryValue).fieldSize()
    }

    Lang.dictionaryType.define(sizeId, sizeFieldSymbol)
    Lang.dictionaryType.fields = listOf(sizeFieldSymbol)
}

fun mutableDictionaryCollectionType() {
    Lang.mutableDictionaryType.define(Lang.mutableDictionaryKeyTypeId, Lang.mutableDictionaryKeyTypeParam)
    Lang.mutableDictionaryType.define(Lang.mutableDictionaryValueTypeId, Lang.mutableDictionaryValueTypeParam)
    Lang.mutableDictionaryType.define(Lang.mutableDictionaryFinTypeId, Lang.mutableDictionaryFinTypeParam)
    Lang.mutableDictionaryType.typeParams =
        listOf(Lang.mutableDictionaryKeyTypeParam, Lang.mutableDictionaryValueTypeParam, Lang.mutableDictionaryFinTypeParam)
    Lang.mutableDictionaryType.modeSelector = { args ->
        when (val fin = args[2]) {
            is FinTypeSymbol -> {
                MutableBasicTypeMode(fin.magnitude)
            }
            else -> {
                ImmutableBasicTypeMode
            }
        }
    }

    val constantFin = ConstantFinTypeSymbol
    createGetFunction(
        constantFin,
        Lang.mutableDictionaryType,
        Lang.mutableDictionaryKeyTypeParam,
        Lang.mutableDictionaryValueTypeParam
    )

    createContainsFunction(
        ConstantFinTypeSymbol,
        Lang.mutableDictionaryType,
        Lang.mutableDictionaryKeyTypeParam,
        Lang.booleanType
    )

    createRemoveFunction(
        constantFin,
        Lang.mutableDictionaryType,
        Lang.unitObject,
        Lang.mutableDictionaryKeyTypeParam
    )

    createSetFunction(
        constantFin,
        Lang.mutableDictionaryType,
        Lang.unitObject,
        Lang.mutableDictionaryKeyTypeParam,
        Lang.mutableDictionaryValueTypeParam
    )

    createToImmutableDictionaryPlugin(
        Lang.mutableDictionaryType,
        Lang.mutableDictionaryKeyTypeParam,
        Lang.mutableDictionaryValueTypeParam,
        Lang.mutableDictionaryFinTypeParam,
        Lang.dictionaryType
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        Lang.mutableDictionaryType,
        sizeId,
        Lang.intType
    ) { value ->
        (value as DictionaryValue).fieldSize()
    }

    Lang.mutableDictionaryType.define(sizeId, sizeFieldSymbol)
    Lang.mutableDictionaryType.fields = listOf(sizeFieldSymbol)
}