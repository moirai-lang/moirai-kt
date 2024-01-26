package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.*

object DictionaryTypes {
    val getFunction = createGetFunction()
    val mutableGetFunction = createMutableGetFunction()
    val containsFunction = createContainsFunction()
    val mutableContainsFunction = createMutableContainsFunction()
    val setFunction = createSetFunction()
    val removeFunction = createRemoveFunction()
    val mutableDictionaryToDictionary = createToImmutableDictionaryPlugin()

    private fun createGetFunction(): ParameterizedMemberPluginSymbol {
        val getId = Identifier(NotInSource, CollectionMethods.KeyLookup.idStr)
        val getMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.dictionaryType,
            getId,
            DoubleParentArgInstantiation
        )
        getMemberFunction.typeParams = listOf(Lang.dictionaryKeyTypeParam, Lang.dictionaryValueTypeParam)
        getMemberFunction.costExpression = ConstantFinTypeSymbol
        val getFormalParamId = Identifier(NotInSource, "key")
        val getFormalParam = FunctionFormalParameterSymbol(getMemberFunction, getFormalParamId, Lang.dictionaryKeyTypeParam)
        getMemberFunction.define(getFormalParamId, getFormalParam)

        getMemberFunction.formalParams = listOf(getFormalParam)
        getMemberFunction.returnType = Lang.dictionaryValueTypeParam
        Lang.dictionaryType.define(getId, getMemberFunction)
        return getMemberFunction
    }

    private fun createMutableGetFunction(): ParameterizedMemberPluginSymbol {
        val getId = Identifier(NotInSource, CollectionMethods.KeyLookup.idStr)
        val getMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableDictionaryType,
            getId,
            DoubleParentArgInstantiation
        )
        getMemberFunction.typeParams = listOf(Lang.mutableDictionaryKeyTypeParam, Lang.mutableDictionaryValueTypeParam)
        getMemberFunction.costExpression = ConstantFinTypeSymbol
        val getFormalParamId = Identifier(NotInSource, "key")
        val getFormalParam = FunctionFormalParameterSymbol(getMemberFunction, getFormalParamId, Lang.mutableDictionaryKeyTypeParam)
        getMemberFunction.define(getFormalParamId, getFormalParam)

        getMemberFunction.formalParams = listOf(getFormalParam)
        getMemberFunction.returnType = Lang.mutableDictionaryValueTypeParam
        Lang.mutableDictionaryType.define(getId, getMemberFunction)
        return getMemberFunction
    }

    private fun createContainsFunction(): ParameterizedMemberPluginSymbol {
        val containsId = Identifier(NotInSource, CollectionMethods.Contains.idStr)
        val containsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.dictionaryType,
            containsId,
            SingleParentArgInstantiation
        )
        containsMemberFunction.typeParams = listOf(Lang.dictionaryKeyTypeParam)
        containsMemberFunction.costExpression = ConstantFinTypeSymbol
        val containsFormalParamId = Identifier(NotInSource, "key")
        val containsFormalParam =
            FunctionFormalParameterSymbol(containsMemberFunction, containsFormalParamId, Lang.dictionaryKeyTypeParam)
        containsMemberFunction.define(containsFormalParamId, containsFormalParam)

        containsMemberFunction.formalParams = listOf(containsFormalParam)
        containsMemberFunction.returnType = Lang.booleanType
        Lang.dictionaryType.define(containsId, containsMemberFunction)
        return containsMemberFunction
    }

    private fun createMutableContainsFunction(): ParameterizedMemberPluginSymbol {
        val containsId = Identifier(NotInSource, CollectionMethods.Contains.idStr)
        val containsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableDictionaryType,
            containsId,
            SingleParentArgInstantiation
        )
        containsMemberFunction.typeParams = listOf(Lang.mutableDictionaryKeyTypeParam)
        containsMemberFunction.costExpression = ConstantFinTypeSymbol
        val containsFormalParamId = Identifier(NotInSource, "key")
        val containsFormalParam =
            FunctionFormalParameterSymbol(containsMemberFunction, containsFormalParamId, Lang.mutableDictionaryKeyTypeParam)
        containsMemberFunction.define(containsFormalParamId, containsFormalParam)

        containsMemberFunction.formalParams = listOf(containsFormalParam)
        containsMemberFunction.returnType = Lang.booleanType
        Lang.mutableDictionaryType.define(containsId, containsMemberFunction)
        return containsMemberFunction
    }

    private fun createSetFunction(): ParameterizedMemberPluginSymbol {
        val setId = Identifier(NotInSource, CollectionMethods.KeyAssign.idStr)
        val setMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableDictionaryType,
            setId,
            DoubleParentArgInstantiation
        )
        setMemberFunction.typeParams = listOf(Lang.mutableDictionaryKeyTypeParam, Lang.mutableDictionaryValueTypeParam)
        setMemberFunction.costExpression = ConstantFinTypeSymbol
        val keyFormalParamId = Identifier(NotInSource, "key")
        val keyFormalParam = FunctionFormalParameterSymbol(setMemberFunction, keyFormalParamId, Lang.mutableDictionaryKeyTypeParam)
        setMemberFunction.define(keyFormalParamId, keyFormalParam)

        val valueFormalParamId = Identifier(NotInSource, "value")
        val valueFormalParam =
            FunctionFormalParameterSymbol(setMemberFunction, valueFormalParamId, Lang.mutableDictionaryValueTypeParam)
        setMemberFunction.define(valueFormalParamId, valueFormalParam)

        setMemberFunction.formalParams = listOf(keyFormalParam, valueFormalParam)
        setMemberFunction.returnType = Lang.unitObject
        Lang.mutableDictionaryType.define(setId, setMemberFunction)
        return setMemberFunction
    }

    private fun createRemoveFunction(): ParameterizedMemberPluginSymbol {
        val removeId = Identifier(NotInSource, CollectionMethods.Remove.idStr)
        val removeMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableDictionaryType,
            removeId,
            SingleParentArgInstantiation
        )
        removeMemberFunction.typeParams = listOf(Lang.mutableDictionaryKeyTypeParam)
        removeMemberFunction.costExpression = ConstantFinTypeSymbol
        val removeFormalParamId = Identifier(NotInSource, "key")
        val removeFormalParam =
            FunctionFormalParameterSymbol(removeMemberFunction, removeFormalParamId, Lang.mutableDictionaryKeyTypeParam)
        removeMemberFunction.define(removeFormalParamId, removeFormalParam)

        removeMemberFunction.formalParams = listOf(removeFormalParam)
        removeMemberFunction.returnType = Lang.unitObject
        Lang.mutableDictionaryType.define(removeId, removeMemberFunction)
        return removeMemberFunction
    }

    fun createToImmutableDictionaryPlugin(): ParameterizedMemberPluginSymbol {
        val plugin = ParameterizedMemberPluginSymbol(
            Lang.mutableDictionaryType,
            Identifier(NotInSource, CollectionMethods.ToImmutableDictionary.idStr),
            TripleParentArgInstantiation
        )
        plugin.typeParams = listOf(
            Lang.mutableDictionaryKeyTypeParam,
            Lang.mutableDictionaryValueTypeParam,
            Lang.mutableDictionaryFinTypeParam
        )
        plugin.formalParams = listOf()
        val outputSubstitution = Substitution(
            Lang.dictionaryType.typeParams,
            listOf(
                Lang.mutableDictionaryKeyTypeParam,
                Lang.mutableDictionaryValueTypeParam,
                Lang.mutableDictionaryFinTypeParam
            )
        )
        val outputType = outputSubstitution.apply(Lang.dictionaryType)
        plugin.returnType = outputType

        plugin.costExpression = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                Lang.mutableDictionaryFinTypeParam
            )
        )
        Lang.mutableDictionaryType.define(plugin.identifier, plugin)
        return plugin
    }

    private val dictionarySizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val dictionarySizeFieldSymbol = PlatformFieldSymbol(
        Lang.dictionaryType,
        dictionarySizeId,
        Lang.intType
    )

    private val mutableSizeId = Identifier(NotInSource, CollectionFields.Size.idStr)
    val mutableSizeFieldSymbol = PlatformFieldSymbol(
        Lang.mutableDictionaryType,
        mutableSizeId,
        Lang.intType
    )

    fun dictionaryCollectionType() {
        Lang.dictionaryType.define(dictionarySizeId, dictionarySizeFieldSymbol)
        Lang.dictionaryType.fields = listOf(dictionarySizeFieldSymbol)
    }

    fun mutableDictionaryCollectionType() {
        Lang.mutableDictionaryType.define(mutableSizeId, mutableSizeFieldSymbol)
        Lang.mutableDictionaryType.fields = listOf(mutableSizeFieldSymbol)
    }
}