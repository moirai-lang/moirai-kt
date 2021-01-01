package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.infer.*

private fun createContainsFunction(
    costExpression: CostExpression,
    setType: ParameterizedBasicTypeSymbol,
    booleanType: BasicTypeSymbol,
    setElementTypeParam: StandardTypeParameter
) {
    val containsId = GroundIdentifier(CollectionMethods.Contains.idStr)
    val containsMemberFunction = ParameterizedMemberPluginSymbol(
        setType,
        containsId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as SetValue).evalContains(args.first())
    }
    containsMemberFunction.typeParams = listOf(setElementTypeParam)
    containsMemberFunction.costExpression = costExpression
    val containsFormalParamId = GroundIdentifier("element")
    val containsFormalParam =
        FunctionFormalParameterSymbol(containsMemberFunction, containsFormalParamId, setElementTypeParam)
    containsMemberFunction.define(containsFormalParamId, containsFormalParam)

    containsMemberFunction.formalParams = listOf(containsFormalParam)
    containsMemberFunction.returnType = booleanType
    setType.define(containsId, containsMemberFunction)
}

private fun createAddFunction(
    costExpression: CostExpression,
    setType: ParameterizedBasicTypeSymbol,
    unitType: ObjectSymbol,
    setElementTypeParam: StandardTypeParameter
) {
    val addId = GroundIdentifier(CollectionMethods.InsertElement.idStr)
    val addMemberFunction = ParameterizedMemberPluginSymbol(
        setType,
        addId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as SetValue).evalAdd(args.first())
    }
    addMemberFunction.typeParams = listOf(setElementTypeParam)
    addMemberFunction.costExpression = costExpression
    val addFormalParamId = GroundIdentifier("element")
    val addFormalParam = FunctionFormalParameterSymbol(addMemberFunction, addFormalParamId, setElementTypeParam)
    addMemberFunction.define(addFormalParamId, addFormalParam)

    addMemberFunction.formalParams = listOf(addFormalParam)
    addMemberFunction.returnType = unitType
    setType.define(addId, addMemberFunction)
}

private fun createRemoveFunction(
    costExpression: CostExpression,
    setType: ParameterizedBasicTypeSymbol,
    unitType: ObjectSymbol,
    setElementTypeParam: StandardTypeParameter
) {
    val removeId = GroundIdentifier(CollectionMethods.Remove.idStr)
    val removeMemberFunction = ParameterizedMemberPluginSymbol(
        setType,
        removeId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as SetValue).evalRemove(args.first())
    }
    removeMemberFunction.typeParams = listOf(setElementTypeParam)
    removeMemberFunction.costExpression = costExpression
    val removeFormalParamId = GroundIdentifier("element")
    val removeFormalParam =
        FunctionFormalParameterSymbol(removeMemberFunction, removeFormalParamId, setElementTypeParam)
    removeMemberFunction.define(removeFormalParamId, removeFormalParam)

    removeMemberFunction.formalParams = listOf(removeFormalParam)
    removeMemberFunction.returnType = unitType
    setType.define(removeId, removeMemberFunction)
}

fun createToImmutableSetPlugin(
    mutableSetType: ParameterizedBasicTypeSymbol,
    elementType: StandardTypeParameter,
    omicron: MutableOmicronTypeParameter,
    setType: ParameterizedBasicTypeSymbol
) {
    val plugin = ParameterizedMemberPluginSymbol(
        mutableSetType,
        GroundIdentifier(CollectionMethods.ToImmutableSet.idStr),
        DoubleParentArgInstantiation
    ) { t: Value, _: List<Value> ->
        (t as SetValue).evalToSet()
    }
    plugin.typeParams = listOf(elementType, omicron)
    plugin.formalParams = listOf()
    val outputSubstitution = Substitution(setType.typeParams, listOf(elementType, omicron))
    val outputType = outputSubstitution.apply(setType)
    plugin.returnType = outputType

    plugin.costExpression =
        ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                omicron
            )
        )
    mutableSetType.define(plugin.gid, plugin)
}

fun setCollectionType(
    architecture: Architecture,
    langNS: Namespace,
    booleanType: BasicTypeSymbol,
    intType: BasicTypeSymbol
): ParameterizedBasicTypeSymbol {
    val setType = ParameterizedBasicTypeSymbol(
        langNS,
        Lang.setId,
        SetInstantiation(),
        immutableUnorderedFeatureSupport
    )
    val setElementTypeParam = StandardTypeParameter(setType, Lang.setElementTypeId)
    setType.define(Lang.setElementTypeId, setElementTypeParam)
    val setOmicronTypeParam = ImmutableOmicronTypeParameter(setType, Lang.setOmicronTypeId)
    setType.define(Lang.setOmicronTypeId, setOmicronTypeParam)
    setType.typeParams = listOf(setElementTypeParam, setOmicronTypeParam)
    setType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    createContainsFunction(
        OmicronTypeSymbol(architecture.defaultNodeCost),
        setType,
        booleanType,
        setElementTypeParam
    )

    val sizeId = GroundIdentifier(CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        setType,
        sizeId,
        intType
    ) { value ->
        (value as SetValue).fieldSize()
    }

    setType.define(sizeId, sizeFieldSymbol)
    setType.fields = listOf(sizeFieldSymbol)

    createSetEqualsMember(setType, setElementTypeParam, setOmicronTypeParam, booleanType)
    createSetNotEqualsMember(setType, setElementTypeParam, setOmicronTypeParam, booleanType)

    return setType
}

fun mutableSetCollectionType(
    architecture: Architecture,
    langNS: Namespace,
    booleanType: BasicTypeSymbol,
    intType: BasicTypeSymbol,
    unitType: ObjectSymbol,
    setType: ParameterizedBasicTypeSymbol
): ParameterizedBasicTypeSymbol {
    val mutableSetType = ParameterizedBasicTypeSymbol(
        langNS,
        Lang.mutableSetId,
        MutableSetInstantiation(),
        noFeatureSupport
    )
    val mutableSetElementTypeParam = StandardTypeParameter(mutableSetType, Lang.mutableSetElementTypeId)
    mutableSetType.define(Lang.mutableSetElementTypeId, mutableSetElementTypeParam)
    val mutableSetOmicronTypeParam = MutableOmicronTypeParameter(mutableSetType, Lang.mutableSetOmicronTypeId)
    mutableSetType.define(Lang.mutableSetOmicronTypeId, mutableSetOmicronTypeParam)
    mutableSetType.typeParams = listOf(mutableSetElementTypeParam, mutableSetOmicronTypeParam)
    mutableSetType.modeSelector = { args ->
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

    createContainsFunction(
        constantOmicron,
        mutableSetType,
        booleanType,
        mutableSetElementTypeParam
    )

    createAddFunction(
        constantOmicron,
        mutableSetType,
        unitType,
        mutableSetElementTypeParam
    )

    createRemoveFunction(
        constantOmicron,
        mutableSetType,
        unitType,
        mutableSetElementTypeParam
    )

    createToImmutableSetPlugin(
        mutableSetType,
        mutableSetElementTypeParam,
        mutableSetOmicronTypeParam,
        setType
    )

    val sizeId = GroundIdentifier(CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        mutableSetType,
        sizeId,
        intType
    ) { value ->
        (value as SetValue).fieldSize()
    }

    mutableSetType.define(sizeId, sizeFieldSymbol)
    mutableSetType.fields = listOf(sizeFieldSymbol)

    createMutableSetEqualsMember(mutableSetType, mutableSetElementTypeParam, mutableSetOmicronTypeParam, booleanType)
    createMutableSetNotEqualsMember(mutableSetType, mutableSetElementTypeParam, mutableSetOmicronTypeParam, booleanType)

    return mutableSetType
}