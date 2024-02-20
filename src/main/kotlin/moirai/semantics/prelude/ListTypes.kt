package moirai.semantics.prelude

import moirai.semantics.core.*
import moirai.semantics.infer.*

internal object ListTypes {
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
        getMemberFunction.costExpression = ConstantFin
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
        getMemberFunction.costExpression = ConstantFin
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
        addMemberFunction.costExpression = ConstantFin
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
        setMemberFunction.costExpression = ConstantFin
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
        removeAtMemberFunction.costExpression = ConstantFin
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

    private val listSizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val listSizeFieldSymbol = PlatformFieldSymbol(
        Lang.listType,
        listSizeId,
        Lang.intType
    )

    private val mutableSizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val mutableSizeFieldSymbol = PlatformFieldSymbol(
        Lang.mutableListType,
        mutableSizeId,
        Lang.intType
    )

    fun listCollectionType() {
        Lang.listType.define(listSizeId, listSizeFieldSymbol)
        Lang.listType.fields = listOf(listSizeFieldSymbol)
    }

    fun mutableListCollectionType() {
        Lang.mutableListType.define(mutableSizeId, mutableSizeFieldSymbol)
        Lang.mutableListType.fields = listOf(mutableSizeFieldSymbol)
    }
}