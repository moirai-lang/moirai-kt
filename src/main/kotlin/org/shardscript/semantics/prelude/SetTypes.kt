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
    unitType: PlatformObjectSymbol,
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
    unitType: PlatformObjectSymbol,
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

fun setCollectionType() {
    Lang.setType.define(Lang.setElementTypeId, Lang.setElementTypeParam)
    Lang.setType.define(Lang.setFinTypeId, Lang.setFinTypeParam)
    Lang.setType.typeParams = listOf(Lang.setElementTypeParam, Lang.setFinTypeParam)
    Lang.setType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }

    createContainsFunction(
        ConstantFinTypeSymbol,
        Lang.setType,
        Lang.booleanType,
        Lang.setElementTypeParam
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        Lang.setType,
        sizeId,
        Lang.intType
    ) { value ->
        (value as SetValue).fieldSize()
    }

    Lang.setType.define(sizeId, sizeFieldSymbol)
    Lang.setType.fields = listOf(sizeFieldSymbol)
}

fun mutableSetCollectionType() {
    Lang.mutableSetType.define(Lang.mutableSetElementTypeId, Lang.mutableSetElementTypeParam)
    Lang.mutableSetType.define(Lang.mutableSetFinTypeId, Lang.mutableSetFinTypeParam)
    Lang.mutableSetType.typeParams = listOf(Lang.mutableSetElementTypeParam, Lang.mutableSetFinTypeParam)
    Lang.mutableSetType.modeSelector = { args ->
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

    createContainsFunction(
        constantFin,
        Lang.mutableSetType,
        Lang.booleanType,
        Lang.mutableSetElementTypeParam
    )

    createAddFunction(
        constantFin,
        Lang.mutableSetType,
        Lang.unitObject,
        Lang.mutableSetElementTypeParam
    )

    createRemoveFunction(
        constantFin,
        Lang.mutableSetType,
        Lang.unitObject,
        Lang.mutableSetElementTypeParam
    )

    createToImmutableSetPlugin(
        Lang.mutableSetType,
        Lang.mutableSetElementTypeParam,
        Lang.mutableSetFinTypeParam,
        Lang.setType
    )

    val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val sizeFieldSymbol = PlatformFieldSymbol(
        Lang.mutableSetType,
        sizeId,
        Lang.intType
    ) { value ->
        (value as SetValue).fieldSize()
    }

    Lang.mutableSetType.define(sizeId, sizeFieldSymbol)
    Lang.mutableSetType.fields = listOf(sizeFieldSymbol)
}