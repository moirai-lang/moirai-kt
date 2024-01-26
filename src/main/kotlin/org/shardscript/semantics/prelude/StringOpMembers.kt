package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.DualFinPluginInstantiation
import org.shardscript.semantics.infer.SingleParentArgInstantiation
import org.shardscript.semantics.infer.Substitution

object StringOpMembers {
    val toCharArray = pluginToCharArray()
    val add = pluginAdd()
    val equals = pluginEquals()
    val notEquals = pluginNotEquals()

    fun pluginToCharArray(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.stringType,
            Identifier(NotInSource, StringMethods.ToCharArray.idStr),
            SingleParentArgInstantiation
        )
        res.typeParams = listOf(Lang.stringTypeParam)
        res.formalParams = listOf()
        val outputSubstitution = Substitution(Lang.listType.typeParams, listOf(Lang.charType, Lang.stringTypeParam))
        val outputType = outputSubstitution.apply(Lang.listType)
        res.returnType = outputType

        res.costExpression = Lang.stringTypeParam
        return res
    }

    fun members(): Map<String, ParameterizedMemberPluginSymbol> = mapOf(
        BinaryOperator.Equal.idStr to pluginEquals(),
        BinaryOperator.NotEqual.idStr to pluginNotEquals(),
        BinaryOperator.Add.idStr to pluginAdd()
    )

    private fun pluginAdd(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.stringType,
            Identifier(NotInSource, BinaryOperator.Add.idStr),
            DualFinPluginInstantiation
        )
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.stringInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(Lang.stringTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(Lang.stringType.typeParams, listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(Lang.stringType)
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg =
            SumCostExpression(
                listOf(
                    Lang.stringTypeParam,
                    inputTypeArg
                )
            )
        val outputSubstitution = Substitution(listOf(Lang.stringTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(Lang.stringType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginEquals(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.stringType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DualFinPluginInstantiation
        )
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.stringInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(Lang.stringTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(Lang.stringType.typeParams, listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(Lang.stringType)
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg =
            ProductCostExpression(
                listOf(
                    CommonCostExpressions.twoPass,
                    MaxCostExpression(
                        listOf(
                            Lang.stringTypeParam,
                            inputTypeArg
                        )
                    )
                )
            )
        res.returnType = Lang.booleanType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginNotEquals(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.stringType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DualFinPluginInstantiation
        )
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.stringInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(Lang.stringTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(Lang.stringType.typeParams, listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(Lang.stringType)
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg =
            ProductCostExpression(
                listOf(
                    CommonCostExpressions.twoPass,
                    MaxCostExpression(
                        listOf(
                            Lang.stringTypeParam,
                            inputTypeArg
                        )
                    )
                )
            )
        res.returnType = Lang.booleanType

        res.costExpression = outputTypeArg
        return res
    }
}