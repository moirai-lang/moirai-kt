package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.infer.*

private fun createGetFunction(
    costExpression: CostExpression,
    listType: ParameterizedBasicTypeSymbol,
    intType: BasicTypeSymbol,
    listElementTypeParam: StandardTypeParameter
) {
    val getId = Identifier(CollectionMethods.IndexLookup.idStr)
    val getMemberFunction = ParameterizedMemberPluginSymbol(
        listType,
        getId,
        SingleParentArgInstantiation,
    { t: Value, args: List<Value> ->
        (t as ListValue).evalGet(args.first())
    })
    getMemberFunction.typeParams = listOf(listElementTypeParam)
    getMemberFunction.costExpression = costExpression
    val getFormalParamId = Identifier("index")
    val getFormalParam = FunctionFormalParameterSymbol(getMemberFunction, getFormalParamId, intType)
    getMemberFunction.define(getFormalParamId, getFormalParam)

    getMemberFunction.formalParams = listOf(getFormalParam)
    getMemberFunction.returnType = listElementTypeParam
    listType.define(getId, getMemberFunction)
}

private fun createAddFunction(
    costExpression: CostExpression,
    listType: ParameterizedBasicTypeSymbol,
    unitType: ObjectSymbol,
    listElementTypeParam: StandardTypeParameter
) {
    val addId = Identifier(CollectionMethods.InsertElement.idStr)
    val addMemberFunction = ParameterizedMemberPluginSymbol(
        listType,
        addId,
        SingleParentArgInstantiation,
    { t: Value, args: List<Value> ->
        (t as ListValue).evalAdd(args.first())
    })
    addMemberFunction.typeParams = listOf(listElementTypeParam)
    addMemberFunction.costExpression = costExpression
    val addFormalParamId = Identifier("element")
    val addFormalParam = FunctionFormalParameterSymbol(addMemberFunction, addFormalParamId, listElementTypeParam)
    addMemberFunction.define(addFormalParamId, addFormalParam)

    addMemberFunction.formalParams = listOf(addFormalParam)
    addMemberFunction.returnType = unitType
    listType.define(addId, addMemberFunction)
}

private fun createSetFunction(
    costExpression: CostExpression,
    listType: ParameterizedBasicTypeSymbol,
    unitType: ObjectSymbol,
    intType: BasicTypeSymbol,
    listElementTypeParam: StandardTypeParameter
) {
    val setId = Identifier(CollectionMethods.IndexAssign.idStr)
    val setMemberFunction = ParameterizedMemberPluginSymbol(
        listType,
        setId,
        SingleParentArgInstantiation,
    { t: Value, args: List<Value> ->
        (t as ListValue).evalSet(args.first(), args[1])
    })
    setMemberFunction.typeParams = listOf(listElementTypeParam)
    setMemberFunction.costExpression = costExpression
    val indexFormalParamId = Identifier("index")
    val indexFormalParam = FunctionFormalParameterSymbol(setMemberFunction, indexFormalParamId, intType)
    setMemberFunction.define(indexFormalParamId, indexFormalParam)

    val valueFormalParamId = Identifier("value")
    val valueFormalParam = FunctionFormalParameterSymbol(setMemberFunction, valueFormalParamId, listElementTypeParam)
    setMemberFunction.define(valueFormalParamId, valueFormalParam)

    setMemberFunction.formalParams = listOf(indexFormalParam, valueFormalParam)
    setMemberFunction.returnType = unitType
    listType.define(setId, setMemberFunction)
}

private fun createRemoveAtFunction(
    costExpression: CostExpression,
    listType: ParameterizedBasicTypeSymbol,
    unitType: ObjectSymbol,
    intType: BasicTypeSymbol
) {
    val removeAtId = Identifier(CollectionMethods.RemoveAtIndex.idStr)
    val removeAtMemberFunction = GroundMemberPluginSymbol(
        listType,
        removeAtId,
    { t: Value, args: List<Value> ->
        (t as ListValue).evalRemoveAt(args.first())
    })
    removeAtMemberFunction.costExpression = costExpression
    val removeAtFormalParamId = Identifier("index")
    val removeAtFormalParam = FunctionFormalParameterSymbol(removeAtMemberFunction, removeAtFormalParamId, intType)
    removeAtMemberFunction.define(removeAtFormalParamId, removeAtFormalParam)

    removeAtMemberFunction.formalParams = listOf(removeAtFormalParam)
    removeAtMemberFunction.returnType = unitType
    listType.define(removeAtId, removeAtMemberFunction)
}

fun createToImmutableListPlugin(
    mutableListType: ParameterizedBasicTypeSymbol,
    elementType: StandardTypeParameter,
    omicron: MutableOmicronTypeParameter,
    listType: ParameterizedBasicTypeSymbol
) {
    val plugin = ParameterizedMemberPluginSymbol(
        mutableListType,
        Identifier(CollectionMethods.ToImmutableList.idStr),
        DoubleParentArgInstantiation,
    { t: Value, _: List<Value> ->
        (t as ListValue).evalToList()
    })
    plugin.typeParams = listOf(elementType, omicron)
    plugin.formalParams = listOf()
    val outputSubstitution = Substitution(listType.typeParams, listOf(elementType, omicron))
    val outputType = outputSubstitution.apply(listType)
    plugin.returnType = outputType

    plugin.costExpression =
        ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                omicron
            )
        )
    mutableListType.define(plugin.identifier, plugin)
}

fun listCollectionType(
    architecture: Architecture,
    langNS: Namespace,
    intType: BasicTypeSymbol,
    booleanType: BasicTypeSymbol
): ParameterizedBasicTypeSymbol {
    val listType = ParameterizedBasicTypeSymbol(
        langNS,
        Lang.listId,
        ListInstantiation(),
        immutableOrderedFeatureSupport
    )
    val listElementTypeParam = StandardTypeParameter(listType, Lang.listElementTypeId)
    listType.define(Lang.listElementTypeId, listElementTypeParam)
    val listOmicronTypeParam = ImmutableOmicronTypeParameter(listType, Lang.listOmicronTypeId)
    listType.define(Lang.listOmicronTypeId, listOmicronTypeParam)
    listType.typeParams = listOf(listElementTypeParam, listOmicronTypeParam)
    listType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }
    createGetFunction(
        OmicronTypeSymbol(architecture.defaultNodeCost),
        listType,
        intType,
        listElementTypeParam
    )

    val sizeId = Identifier(CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        listType,
        sizeId,
        intType
    ) { value ->
        (value as ListValue).fieldSize()
    }

    listType.define(sizeId, sizeFieldSymbol)
    listType.fields = listOf(sizeFieldSymbol)

    createListEqualsMember(listType, listElementTypeParam, listOmicronTypeParam, booleanType)
    createListNotEqualsMember(listType, listElementTypeParam, listOmicronTypeParam, booleanType)

    return listType
}

fun mutableListCollectionType(
    architecture: Architecture,
    langNS: Namespace,
    intType: BasicTypeSymbol,
    unitType: ObjectSymbol,
    booleanType: BasicTypeSymbol,
    listType: ParameterizedBasicTypeSymbol
): ParameterizedBasicTypeSymbol {
    val mutableListType = ParameterizedBasicTypeSymbol(
        langNS,
        Lang.mutableListId,
        MutableListInstantiation(),
        noFeatureSupport
    )
    val mutableListElementTypeParam = StandardTypeParameter(mutableListType, Lang.mutableListElementTypeId)
    mutableListType.define(Lang.mutableListElementTypeId, mutableListElementTypeParam)
    val mutableListOmicronTypeParam = MutableOmicronTypeParameter(mutableListType, Lang.mutableListOmicronTypeId)
    mutableListType.define(Lang.mutableListOmicronTypeId, mutableListOmicronTypeParam)
    mutableListType.typeParams = listOf(mutableListElementTypeParam, mutableListOmicronTypeParam)
    mutableListType.modeSelector = { args ->
        when (val omicron = args[1]) {
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
        mutableListType,
        intType,
        mutableListElementTypeParam
    )

    createAddFunction(
        constantOmicron,
        mutableListType,
        unitType,
        mutableListElementTypeParam
    )

    createRemoveAtFunction(
        constantOmicron,
        mutableListType,
        unitType,
        intType
    )

    createSetFunction(
        constantOmicron,
        mutableListType,
        unitType,
        intType,
        mutableListElementTypeParam
    )

    createToImmutableListPlugin(
        mutableListType,
        mutableListElementTypeParam,
        mutableListOmicronTypeParam,
        listType
    )

    val sizeId = Identifier(CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        mutableListType,
        sizeId,
        intType
    ) { value ->
        (value as ListValue).fieldSize()
    }

    mutableListType.define(sizeId, sizeFieldSymbol)
    mutableListType.fields = listOf(sizeFieldSymbol)

    createMutableListEqualsMember(
        mutableListType,
        mutableListElementTypeParam,
        mutableListOmicronTypeParam,
        booleanType
    )
    createMutableListNotEqualsMember(
        mutableListType,
        mutableListElementTypeParam,
        mutableListOmicronTypeParam,
        booleanType
    )

    return mutableListType
}