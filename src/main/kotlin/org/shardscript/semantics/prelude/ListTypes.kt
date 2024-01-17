package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.*

private fun createGetFunction(
    costExpression: CostExpression,
    listType: ParameterizedBasicTypeSymbol,
    intType: BasicTypeSymbol,
    listElementTypeParam: StandardTypeParameter
) {
    val getId = Identifier(NotInSource, CollectionMethods.IndexLookup.idStr)
    val getMemberFunction = ParameterizedMemberPluginSymbol(
        listType,
        getId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as ListValue).evalGet(args.first())
    }
    getMemberFunction.typeParams = listOf(listElementTypeParam)
    getMemberFunction.costExpression = costExpression
    val getFormalParamId = Identifier(NotInSource, "index")
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
    val addId = Identifier(NotInSource, CollectionMethods.InsertElement.idStr)
    val addMemberFunction = ParameterizedMemberPluginSymbol(
        listType,
        addId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as ListValue).evalAdd(args.first())
    }
    addMemberFunction.typeParams = listOf(listElementTypeParam)
    addMemberFunction.costExpression = costExpression
    val addFormalParamId = Identifier(NotInSource, "element")
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
    val setId = Identifier(NotInSource, CollectionMethods.IndexAssign.idStr)
    val setMemberFunction = ParameterizedMemberPluginSymbol(
        listType,
        setId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as ListValue).evalSet(args.first(), args[1])
    }
    setMemberFunction.typeParams = listOf(listElementTypeParam)
    setMemberFunction.costExpression = costExpression
    val indexFormalParamId = Identifier(NotInSource, "index")
    val indexFormalParam = FunctionFormalParameterSymbol(setMemberFunction, indexFormalParamId, intType)
    setMemberFunction.define(indexFormalParamId, indexFormalParam)

    val valueFormalParamId = Identifier(NotInSource, "value")
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
    val removeAtId = Identifier(NotInSource, CollectionMethods.RemoveAtIndex.idStr)
    val removeAtMemberFunction = GroundMemberPluginSymbol(
        listType,
        removeAtId
    ) { t: Value, args: List<Value> ->
        (t as ListValue).evalRemoveAt(args.first())
    }
    removeAtMemberFunction.costExpression = costExpression
    val removeAtFormalParamId = Identifier(NotInSource, "index")
    val removeAtFormalParam = FunctionFormalParameterSymbol(removeAtMemberFunction, removeAtFormalParamId, intType)
    removeAtMemberFunction.define(removeAtFormalParamId, removeAtFormalParam)

    removeAtMemberFunction.formalParams = listOf(removeAtFormalParam)
    removeAtMemberFunction.returnType = unitType
    listType.define(removeAtId, removeAtMemberFunction)
}

fun createToImmutableListPlugin(
    mutableListType: ParameterizedBasicTypeSymbol,
    elementType: StandardTypeParameter,
    fin: MutableFinTypeParameter,
    listType: ParameterizedBasicTypeSymbol
) {
    val plugin = ParameterizedMemberPluginSymbol(
        mutableListType,
        Identifier(NotInSource, CollectionMethods.ToImmutableList.idStr),
        DoubleParentArgInstantiation
    ) { t: Value, _: List<Value> ->
        (t as ListValue).evalToList()
    }
    plugin.typeParams = listOf(elementType, fin)
    plugin.formalParams = listOf()
    val outputSubstitution = Substitution(listType.typeParams, listOf(elementType, fin))
    val outputType = outputSubstitution.apply(listType)
    plugin.returnType = outputType

    plugin.costExpression =
        ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                fin
            )
        )
    mutableListType.define(plugin.identifier, plugin)
}

fun listCollectionType(
    langNS: Scope<Symbol>,
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
    val listFinTypeParam = ImmutableFinTypeParameter(listType, Lang.listFinTypeId)
    listType.define(Lang.listFinTypeId, listFinTypeParam)
    listType.typeParams = listOf(listElementTypeParam, listFinTypeParam)
    listType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }
    createGetFunction(
        ConstantFinTypeSymbol,
        listType,
        intType,
        listElementTypeParam
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        listType,
        sizeId,
        intType
    ) { value ->
        (value as ListValue).fieldSize()
    }

    listType.define(sizeId, sizeFieldSymbol)
    listType.fields = listOf(sizeFieldSymbol)

    createListEqualsMember(listType, listElementTypeParam, listFinTypeParam, booleanType)
    createListNotEqualsMember(listType, listElementTypeParam, listFinTypeParam, booleanType)

    return listType
}

fun mutableListCollectionType(
    langNS: Scope<Symbol>,
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
    val mutableListFinTypeParam = MutableFinTypeParameter(mutableListType, Lang.mutableListFinTypeId)
    mutableListType.define(Lang.mutableListFinTypeId, mutableListFinTypeParam)
    mutableListType.typeParams = listOf(mutableListElementTypeParam, mutableListFinTypeParam)
    mutableListType.modeSelector = { args ->
        when (val fin = args[1]) {
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
        mutableListType,
        intType,
        mutableListElementTypeParam
    )

    createAddFunction(
        constantFin,
        mutableListType,
        unitType,
        mutableListElementTypeParam
    )

    createRemoveAtFunction(
        constantFin,
        mutableListType,
        unitType,
        intType
    )

    createSetFunction(
        constantFin,
        mutableListType,
        unitType,
        intType,
        mutableListElementTypeParam
    )

    createToImmutableListPlugin(
        mutableListType,
        mutableListElementTypeParam,
        mutableListFinTypeParam,
        listType
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
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
        mutableListFinTypeParam,
        booleanType
    )
    createMutableListNotEqualsMember(
        mutableListType,
        mutableListElementTypeParam,
        mutableListFinTypeParam,
        booleanType
    )

    return mutableListType
}