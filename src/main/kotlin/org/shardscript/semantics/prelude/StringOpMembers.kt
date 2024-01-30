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

    private fun pluginToCharArray(): ParameterizedMemberPluginSymbol {
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
        val inputTypeArg = ImmutableFinTypeParameter(
            "${Lang.stringId.name}.${BinaryOperator.Add.idStr}.${Lang.stringInputTypeId.name}",
            Lang.stringInputTypeId
        )
        res.defineType(inputTypeArg.identifier, inputTypeArg)
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
        val inputTypeArg = ImmutableFinTypeParameter(
            "${Lang.stringId.name}.${BinaryOperator.Equal.idStr}.${Lang.stringInputTypeId.name}",
            Lang.stringInputTypeId
        )
        res.defineType(inputTypeArg.identifier, inputTypeArg)
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
        val inputTypeArg = ImmutableFinTypeParameter(
            "${Lang.stringId.name}.${BinaryOperator.NotEqual.idStr}.${Lang.stringInputTypeId.name}",
            Lang.stringInputTypeId
        )
        res.defineType(inputTypeArg.identifier, inputTypeArg)
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

    val integerToStringMember = insertIntegerToStringMember()
    val unitToStringMember = insertUnitToStringMember()
    val booleanToStringMember = insertBooleanToStringMember()
    val charToStringMember = insertCharToStringMember()
    val decimalToStringMember = insertDecimalToStringMember()
    val stringToStringMember = insertStringToStringMember()

    private fun insertIntegerToStringMember(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, StringMethods.ToString.idStr)
        )
        val fin = FinTypeSymbol(Lang.INT_FIN)
        res.costExpression = fin

        val substitution = Substitution(Lang.stringType.typeParams, listOf(fin))
        val stringInstantiation = substitution.apply(Lang.stringType)
        res.formalParams = listOf()
        res.returnType = stringInstantiation
        Lang.intType.define(res.identifier, res)
        return res
    }

    private fun insertUnitToStringMember(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.unitObject,
            Identifier(NotInSource, StringMethods.ToString.idStr)
        )
        val fin = FinTypeSymbol(Lang.unitFin)
        res.costExpression = fin

        val substitution = Substitution(Lang.stringType.typeParams, listOf(fin))
        val stringInstantiation = substitution.apply(Lang.stringType)
        res.formalParams = listOf()
        res.returnType = stringInstantiation
        Lang.unitObject.define(res.identifier, res)
        return res
    }

    private fun insertBooleanToStringMember(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.booleanType,
            Identifier(NotInSource, StringMethods.ToString.idStr)
        )
        val fin = FinTypeSymbol(Lang.BOOL_FIN)
        res.costExpression = fin

        val substitution = Substitution(Lang.stringType.typeParams, listOf(fin))
        val stringInstantiation = substitution.apply(Lang.stringType)
        res.formalParams = listOf()
        res.returnType = stringInstantiation
        Lang.booleanType.define(res.identifier, res)
        return res
    }

    private fun insertCharToStringMember(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.charType,
            Identifier(NotInSource, StringMethods.ToString.idStr)
        )
        val fin = FinTypeSymbol(Lang.CHAR_FIN)
        res.costExpression = fin
        val substitution = Substitution(Lang.stringType.typeParams, listOf(fin))
        val stringInstantiation = substitution.apply(Lang.stringType)
        res.formalParams = listOf()
        res.returnType = stringInstantiation
        Lang.charType.define(res.identifier, res)
        return res
    }

    private fun insertDecimalToStringMember(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.decimalType,
            Identifier(NotInSource, StringMethods.ToString.idStr),
            SingleParentArgInstantiation)
        val fin = Lang.decimalType.typeParams.first()
        res.costExpression = fin as ImmutableFinTypeParameter
        res.typeParams = listOf(fin)

        val substitution = Substitution(Lang.stringType.typeParams, listOf(fin))
        val stringInstantiation = substitution.apply(Lang.stringType)
        res.formalParams = listOf()
        res.returnType = stringInstantiation
        Lang.decimalType.define(res.identifier, res)
        return res
    }

    private fun insertStringToStringMember(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.stringType,
            Identifier(NotInSource, StringMethods.ToString.idStr),
            SingleParentArgInstantiation)
        val fin = Lang.stringType.typeParams.first()
        res.costExpression = fin as ImmutableFinTypeParameter
        res.typeParams = listOf(fin)

        res.formalParams = listOf()
        val substitution = Substitution(Lang.stringType.typeParams, listOf(fin))
        val outputType = substitution.apply(Lang.stringType)
        res.returnType = outputType
        Lang.stringType.define(res.identifier, res)
        return res
    }
}