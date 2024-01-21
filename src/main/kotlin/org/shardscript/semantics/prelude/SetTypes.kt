package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.*

object SetTypes {
    val setContains = createSetContainsFunction()
    val mutableSetContains = createMutableSetContainsFunction()

    private fun createSetContainsFunction(): ParameterizedMemberPluginSymbol {
        val containsId = Identifier(NotInSource, CollectionMethods.Contains.idStr)
        val containsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.setType,
            containsId,
            SingleParentArgInstantiation
        )
        containsMemberFunction.typeParams = listOf(Lang.setElementTypeParam)
        containsMemberFunction.costExpression = ConstantFinTypeSymbol
        val containsFormalParamId = Identifier(NotInSource, "element")
        val containsFormalParam =
            FunctionFormalParameterSymbol(containsMemberFunction, containsFormalParamId, Lang.setElementTypeParam)
        containsMemberFunction.define(containsFormalParamId, containsFormalParam)

        containsMemberFunction.formalParams = listOf(containsFormalParam)
        containsMemberFunction.returnType = Lang.booleanType
        Lang.setType.define(containsId, containsMemberFunction)
        return containsMemberFunction
    }

    private fun createMutableSetContainsFunction(): ParameterizedMemberPluginSymbol {
        val containsId = Identifier(NotInSource, CollectionMethods.Contains.idStr)
        val containsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableSetType,
            containsId,
            SingleParentArgInstantiation
        )
        containsMemberFunction.typeParams = listOf(Lang.mutableSetElementTypeParam)
        containsMemberFunction.costExpression = ConstantFinTypeSymbol
        val containsFormalParamId = Identifier(NotInSource, "element")
        val containsFormalParam =
            FunctionFormalParameterSymbol(containsMemberFunction, containsFormalParamId, Lang.mutableSetElementTypeParam)
        containsMemberFunction.define(containsFormalParamId, containsFormalParam)

        containsMemberFunction.formalParams = listOf(containsFormalParam)
        containsMemberFunction.returnType = Lang.booleanType
        Lang.mutableSetType.define(containsId, containsMemberFunction)
        return containsMemberFunction
    }

    private fun createMutableSetAddFunction(): ParameterizedMemberPluginSymbol {
        val addId = Identifier(NotInSource, CollectionMethods.InsertElement.idStr)
        val addMemberFunction = ParameterizedMemberPluginSymbol(
            setType,
            addId,
            SingleParentArgInstantiation
        ) { t: Value, args: List<Value> ->
            (t as SetValue).evalAdd(args.first())
        }
        addMemberFunction.typeParams = listOf(setElementTypeParam)
        addMemberFunction.costExpression = ConstantFinTypeSymbol
        val addFormalParamId = Identifier(NotInSource, "element")
        val addFormalParam = FunctionFormalParameterSymbol(addMemberFunction, addFormalParamId, setElementTypeParam)
        addMemberFunction.define(addFormalParamId, addFormalParam)

        addMemberFunction.formalParams = listOf(addFormalParam)
        addMemberFunction.returnType = unitType
        setType.define(addId, addMemberFunction)
        return addMemberFunction
    }

    private fun createMutableSetRemoveFunction(): ParameterizedMemberPluginSymbol {
        val removeId = Identifier(NotInSource, CollectionMethods.Remove.idStr)
        val removeMemberFunction = ParameterizedMemberPluginSymbol(
            setType,
            removeId,
            SingleParentArgInstantiation
        ) { t: Value, args: List<Value> ->
            (t as SetValue).evalRemove(args.first())
        }
        removeMemberFunction.typeParams = listOf(setElementTypeParam)
        removeMemberFunction.costExpression = ConstantFinTypeSymbol
        val removeFormalParamId = Identifier(NotInSource, "element")
        val removeFormalParam =
            FunctionFormalParameterSymbol(removeMemberFunction, removeFormalParamId, setElementTypeParam)
        removeMemberFunction.define(removeFormalParamId, removeFormalParam)

        removeMemberFunction.formalParams = listOf(removeFormalParam)
        removeMemberFunction.returnType = unitType
        setType.define(removeId, removeMemberFunction)
        return removeMemberFunction
    }

    fun createMutableSetToImmutableSetPlugin(): ParameterizedMemberPluginSymbol {
        val plugin = ParameterizedMemberPluginSymbol(
            Lang.mutableSetType,
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
        return plugin
    }

    fun setCollectionType() {
        Lang.setType.define(Lang.setElementTypeId, Lang.setElementTypeParam)
        Lang.setType.define(Lang.setFinTypeId, Lang.setFinTypeParam)
        Lang.setType.typeParams = listOf(Lang.setElementTypeParam, Lang.setFinTypeParam)
        Lang.setType.modeSelector = { _ ->
            ImmutableBasicTypeMode
        }

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
}