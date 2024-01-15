package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.*

private fun createContainsFunction(
    costExpression: CostExpression,
    setType: ParameterizedBasicTypeSymbol,
    booleanType: BasicTypeSymbol,
    setElementTypeParam: StandardTypeParameter
) {
    val containsId = Identifier(NotInSource, CollectionMethods.Contains.idStr)
    val containsMemberFunction = ParameterizedMemberPluginSymbol(
        setType,
        containsId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as SetValue).evalContains(args.first())
    }
    containsMemberFunction.typeParams = listOf(setElementTypeParam)
    containsMemberFunction.costExpression = costExpression
    val containsFormalParamId = Identifier(NotInSource, "element")
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
    val addId = Identifier(NotInSource, CollectionMethods.InsertElement.idStr)
    val addMemberFunction = ParameterizedMemberPluginSymbol(
        setType,
        addId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as SetValue).evalAdd(args.first())
    }
    addMemberFunction.typeParams = listOf(setElementTypeParam)
    addMemberFunction.costExpression = costExpression
    val addFormalParamId = Identifier(NotInSource, "element")
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
    val removeId = Identifier(NotInSource, CollectionMethods.Remove.idStr)
    val removeMemberFunction = ParameterizedMemberPluginSymbol(
        setType,
        removeId,
        SingleParentArgInstantiation
    ) { t: Value, args: List<Value> ->
        (t as SetValue).evalRemove(args.first())
    }
    removeMemberFunction.typeParams = listOf(setElementTypeParam)
    removeMemberFunction.costExpression = costExpression
    val removeFormalParamId = Identifier(NotInSource, "element")
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
    fin: MutableFinTypeParameter,
    setType: ParameterizedBasicTypeSymbol
) {
    val plugin = ParameterizedMemberPluginSymbol(
        mutableSetType,
        Identifier(NotInSource, CollectionMethods.ToImmutableSet.idStr),
        DoubleParentArgInstantiation
    ) { t: Value, _: List<Value> ->
        (t as SetValue).evalToSet()
    }
    plugin.typeParams = listOf(elementType, fin)
    plugin.formalParams = listOf()
    val outputSubstitution = Substitution(setType.typeParams, listOf(elementType, fin))
    val outputType = outputSubstitution.apply(setType)
    plugin.returnType = outputType

    plugin.costExpression =
        ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                fin
            )
        )
    mutableSetType.define(plugin.identifier, plugin)
}

fun setCollectionType(
    architecture: Architecture,
    langNS: Scope<Symbol>,
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
    val setFinTypeParam = ImmutableFinTypeParameter(setType, Lang.setFinTypeId)
    setType.define(Lang.setFinTypeId, setFinTypeParam)
    setType.typeParams = listOf(setElementTypeParam, setFinTypeParam)
    setType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    createContainsFunction(
        FinTypeSymbol(architecture.defaultNodeCost),
        setType,
        booleanType,
        setElementTypeParam
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        setType,
        sizeId,
        intType
    ) { value ->
        (value as SetValue).fieldSize()
    }

    setType.define(sizeId, sizeFieldSymbol)
    setType.fields = listOf(sizeFieldSymbol)

    createSetEqualsMember(setType, setElementTypeParam, setFinTypeParam, booleanType)
    createSetNotEqualsMember(setType, setElementTypeParam, setFinTypeParam, booleanType)

    return setType
}

fun mutableSetCollectionType(
    architecture: Architecture,
    langNS: Scope<Symbol>,
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
    val mutableSetFinTypeParam = MutableFinTypeParameter(mutableSetType, Lang.mutableSetFinTypeId)
    mutableSetType.define(Lang.mutableSetFinTypeId, mutableSetFinTypeParam)
    mutableSetType.typeParams = listOf(mutableSetElementTypeParam, mutableSetFinTypeParam)
    mutableSetType.modeSelector = { args ->
        when (val fin = args[1]) {
            is FinTypeSymbol -> {
                MutableBasicTypeMode(fin.magnitude)
            }
            else -> {
                ImmutableBasicTypeMode
            }
        }
    }

    val constantFin = FinTypeSymbol(architecture.defaultNodeCost)

    createContainsFunction(
        constantFin,
        mutableSetType,
        booleanType,
        mutableSetElementTypeParam
    )

    createAddFunction(
        constantFin,
        mutableSetType,
        unitType,
        mutableSetElementTypeParam
    )

    createRemoveFunction(
        constantFin,
        mutableSetType,
        unitType,
        mutableSetElementTypeParam
    )

    createToImmutableSetPlugin(
        mutableSetType,
        mutableSetElementTypeParam,
        mutableSetFinTypeParam,
        setType
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        mutableSetType,
        sizeId,
        intType
    ) { value ->
        (value as SetValue).fieldSize()
    }

    mutableSetType.define(sizeId, sizeFieldSymbol)
    mutableSetType.fields = listOf(sizeFieldSymbol)

    createMutableSetEqualsMember(mutableSetType, mutableSetElementTypeParam, mutableSetFinTypeParam, booleanType)
    createMutableSetNotEqualsMember(mutableSetType, mutableSetElementTypeParam, mutableSetFinTypeParam, booleanType)

    return mutableSetType
}