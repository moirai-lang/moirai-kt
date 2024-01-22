package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.*

object ListTypes {
    val listGet = createGetFunction()
    val mutableListGet = createMutableListGetFunction()
    val mutableListAdd = createAddFunction()
    val mutableListSet = createSetFunction()
    val removeAtFunction = createRemoveAtFunction()
    val mutableListToList = createToImmutableListPlugin()

    private fun createGetFunction(): ParameterizedMemberPluginSymbol {
        val getId = Identifier(NotInSource, CollectionMethods.IndexLookup.idStr)
        val getMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.listType,
            getId,
            SingleParentArgInstantiation
        )
        getMemberFunction.typeParams = listOf(Lang.listElementTypeParam)
        getMemberFunction.costExpression = ConstantFinTypeSymbol
        val getFormalParamId = Identifier(NotInSource, "index")
        val getFormalParam = FunctionFormalParameterSymbol(getMemberFunction, getFormalParamId, Lang.intType)
        getMemberFunction.define(getFormalParamId, getFormalParam)

        getMemberFunction.formalParams = listOf(getFormalParam)
        getMemberFunction.returnType = Lang.listElementTypeParam
        Lang.listType.define(getId, getMemberFunction)
        return getMemberFunction
    }

    private fun createMutableListGetFunction(): ParameterizedMemberPluginSymbol {
        val getId = Identifier(NotInSource, CollectionMethods.IndexLookup.idStr)
        val getMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableListType,
            getId,
            SingleParentArgInstantiation
        )
        getMemberFunction.typeParams = listOf(Lang.mutableListElementTypeParam)
        getMemberFunction.costExpression = ConstantFinTypeSymbol
        val getFormalParamId = Identifier(NotInSource, "index")
        val getFormalParam = FunctionFormalParameterSymbol(getMemberFunction, getFormalParamId, Lang.intType)
        getMemberFunction.define(getFormalParamId, getFormalParam)

        getMemberFunction.formalParams = listOf(getFormalParam)
        getMemberFunction.returnType = Lang.mutableListElementTypeParam
        Lang.mutableListType.define(getId, getMemberFunction)
        return getMemberFunction
    }

    private fun createAddFunction(): ParameterizedMemberPluginSymbol {
        val addId = Identifier(NotInSource, CollectionMethods.InsertElement.idStr)
        val addMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableListType,
            addId,
            SingleParentArgInstantiation
        )
        addMemberFunction.typeParams = listOf(Lang.mutableListElementTypeParam)
        addMemberFunction.costExpression = ConstantFinTypeSymbol
        val addFormalParamId = Identifier(NotInSource, "element")
        val addFormalParam = FunctionFormalParameterSymbol(addMemberFunction, addFormalParamId, Lang.mutableListElementTypeParam)
        addMemberFunction.define(addFormalParamId, addFormalParam)

        addMemberFunction.formalParams = listOf(addFormalParam)
        addMemberFunction.returnType = Lang.unitObject
        Lang.mutableListType.define(addId, addMemberFunction)
        return addMemberFunction
    }

    private fun createSetFunction(): ParameterizedMemberPluginSymbol {
        val setId = Identifier(NotInSource, CollectionMethods.IndexAssign.idStr)
        val setMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableListType,
            setId,
            SingleParentArgInstantiation
        )
        setMemberFunction.typeParams = listOf(Lang.mutableListElementTypeParam)
        setMemberFunction.costExpression = ConstantFinTypeSymbol
        val indexFormalParamId = Identifier(NotInSource, "index")
        val indexFormalParam = FunctionFormalParameterSymbol(setMemberFunction, indexFormalParamId, Lang.intType)
        setMemberFunction.define(indexFormalParamId, indexFormalParam)

        val valueFormalParamId = Identifier(NotInSource, "value")
        val valueFormalParam =
            FunctionFormalParameterSymbol(setMemberFunction, valueFormalParamId, Lang.mutableListElementTypeParam)
        setMemberFunction.define(valueFormalParamId, valueFormalParam)

        setMemberFunction.formalParams = listOf(indexFormalParam, valueFormalParam)
        setMemberFunction.returnType = Lang.unitObject
        Lang.mutableListType.define(setId, setMemberFunction)
        return setMemberFunction
    }

    private fun createRemoveAtFunction(): GroundMemberPluginSymbol {
        val removeAtId = Identifier(NotInSource, CollectionMethods.RemoveAtIndex.idStr)
        val removeAtMemberFunction = GroundMemberPluginSymbol(
            Lang.mutableListType,
            removeAtId
        )
        removeAtMemberFunction.costExpression = ConstantFinTypeSymbol
        val removeAtFormalParamId = Identifier(NotInSource, "index")
        val removeAtFormalParam = FunctionFormalParameterSymbol(removeAtMemberFunction, removeAtFormalParamId, Lang.intType)
        removeAtMemberFunction.define(removeAtFormalParamId, removeAtFormalParam)

        removeAtMemberFunction.formalParams = listOf(removeAtFormalParam)
        removeAtMemberFunction.returnType = Lang.unitObject
        Lang.mutableListType.define(removeAtId, removeAtMemberFunction)
        return removeAtMemberFunction
    }

    private fun createToImmutableListPlugin(): ParameterizedMemberPluginSymbol {
        val plugin = ParameterizedMemberPluginSymbol(
            Lang.mutableListType,
            Identifier(NotInSource, CollectionMethods.ToImmutableList.idStr),
            DoubleParentArgInstantiation
        )
        plugin.typeParams = listOf(Lang.mutableListElementTypeParam, Lang.mutableSetFinTypeParam)
        plugin.formalParams = listOf()
        val outputSubstitution = Substitution(Lang.listType.typeParams, listOf(Lang.mutableListElementTypeParam, Lang.mutableSetFinTypeParam))
        val outputType = outputSubstitution.apply(Lang.listType)
        plugin.returnType = outputType

        plugin.costExpression =
            ProductCostExpression(
                listOf(
                    CommonCostExpressions.twoPass,
                    Lang.mutableSetFinTypeParam
                )
            )
        Lang.mutableListType.define(plugin.identifier, plugin)
        return plugin
    }

    fun listCollectionType() {
        Lang.listType.define(Lang.listElementTypeId, Lang.listElementTypeParam)
        Lang.listType.define(Lang.listFinTypeId, Lang.listFinTypeParam)
        Lang.listType.typeParams = listOf(Lang.listElementTypeParam, Lang.listFinTypeParam)
        Lang.listType.modeSelector = { _ ->
            ImmutableBasicTypeMode
        }

        val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
        val sizeFieldSymbol = PlatformFieldSymbol(
            Lang.listType,
            sizeId,
            Lang.intType
        ) { value ->
            (value as ListValue).fieldSize()
        }

        Lang.listType.define(sizeId, sizeFieldSymbol)
        Lang.listType.fields = listOf(sizeFieldSymbol)
    }

    fun mutableListCollectionType() {
        Lang.mutableListType.define(Lang.mutableListElementTypeId, Lang.mutableListElementTypeParam)
        Lang.mutableListType.define(Lang.mutableListFinTypeId, Lang.mutableListFinTypeParam)
        Lang.mutableListType.typeParams = listOf(Lang.mutableListElementTypeParam, Lang.mutableListFinTypeParam)
        Lang.mutableListType.modeSelector = { args ->
            when (val fin = args[1]) {
                is FinTypeSymbol -> {
                    MutableBasicTypeMode(fin.magnitude)
                }

                else -> {
                    ImmutableBasicTypeMode
                }
            }
        }

        val sizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
        val sizeFieldSymbol = PlatformFieldSymbol(
            Lang.mutableListType,
            sizeId,
            Lang.intType
        ) { value ->
            (value as ListValue).fieldSize()
        }

        Lang.mutableListType.define(sizeId, sizeFieldSymbol)
        Lang.mutableListType.fields = listOf(sizeFieldSymbol)
    }
}