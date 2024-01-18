package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.*

object ListTypes {
    fun createGetFunction(
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

    fun createAddFunction(
        costExpression: CostExpression,
        listType: ParameterizedBasicTypeSymbol,
        unitType: PlatformObjectSymbol,
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

    fun createSetFunction(
        costExpression: CostExpression,
        listType: ParameterizedBasicTypeSymbol,
        unitType: PlatformObjectSymbol,
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
        val valueFormalParam =
            FunctionFormalParameterSymbol(setMemberFunction, valueFormalParamId, listElementTypeParam)
        setMemberFunction.define(valueFormalParamId, valueFormalParam)

        setMemberFunction.formalParams = listOf(indexFormalParam, valueFormalParam)
        setMemberFunction.returnType = unitType
        listType.define(setId, setMemberFunction)
    }

    val removeAtFunction = createRemoveAtFunction()

    private fun createRemoveAtFunction(): GroundMemberPluginSymbol {
        val removeAtId = Identifier(NotInSource, CollectionMethods.RemoveAtIndex.idStr)
        val removeAtMemberFunction = GroundMemberPluginSymbol(
            Lang.listType,
            removeAtId
        )
        removeAtMemberFunction.costExpression = ConstantFinTypeSymbol
        val removeAtFormalParamId = Identifier(NotInSource, "index")
        val removeAtFormalParam = FunctionFormalParameterSymbol(removeAtMemberFunction, removeAtFormalParamId, Lang.intType)
        removeAtMemberFunction.define(removeAtFormalParamId, removeAtFormalParam)

        removeAtMemberFunction.formalParams = listOf(removeAtFormalParam)
        removeAtMemberFunction.returnType = Lang.unitObject
        Lang.listType.define(removeAtId, removeAtMemberFunction)
        return removeAtMemberFunction
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

    fun listCollectionType() {
        Lang.listType.define(Lang.listElementTypeId, Lang.listElementTypeParam)
        Lang.listType.define(Lang.listFinTypeId, Lang.listFinTypeParam)
        Lang.listType.typeParams = listOf(Lang.listElementTypeParam, Lang.listFinTypeParam)
        Lang.listType.modeSelector = { _ ->
            ImmutableBasicTypeMode
        }
        createGetFunction(
            ConstantFinTypeSymbol,
            Lang.listType,
            Lang.intType,
            Lang.listElementTypeParam
        )

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

        createListEqualsMember(Lang.listType, Lang.listElementTypeParam, Lang.listFinTypeParam, Lang.booleanType)
        createListNotEqualsMember(Lang.listType, Lang.listElementTypeParam, Lang.listFinTypeParam, Lang.booleanType)
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

        createGetFunction(
            ConstantFinTypeSymbol,
            Lang.mutableListType,
            Lang.intType,
            Lang.mutableListElementTypeParam
        )

        createAddFunction(
            ConstantFinTypeSymbol,
            Lang.mutableListType,
            Lang.unitObject,
            Lang.mutableListElementTypeParam
        )

        createSetFunction(
            ConstantFinTypeSymbol,
            Lang.mutableListType,
            Lang.unitObject,
            Lang.intType,
            Lang.mutableListElementTypeParam
        )

        createToImmutableListPlugin(
            Lang.mutableListType,
            Lang.mutableListElementTypeParam,
            Lang.mutableListFinTypeParam,
            Lang.listType
        )

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

        createMutableListEqualsMember(
            Lang.mutableListType,
            Lang.mutableListElementTypeParam,
            Lang.mutableListFinTypeParam,
            Lang.booleanType
        )
        createMutableListNotEqualsMember(
            Lang.mutableListType,
            Lang.mutableListElementTypeParam,
            Lang.mutableListFinTypeParam,
            Lang.booleanType
        )
    }
}