package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.infer.DualOmicronPluginInstantiation
import com.tsikhe.shardscript.semantics.infer.SingleParentArgInstantiation
import com.tsikhe.shardscript.semantics.infer.Substitution

internal fun pluginToCharArray(
    stringType: ParameterizedBasicTypeSymbol,
    stringTypeParam: ImmutableOmicronTypeParameter,
    charType: BasicTypeSymbol,
    listType: ParameterizedBasicTypeSymbol
): ParameterizedMemberPluginSymbol {
    val res = ParameterizedMemberPluginSymbol(
        stringType,
        GroundIdentifier(StringMethods.ToCharArray.idStr),
        SingleParentArgInstantiation
    ) { t: Value, _: List<Value> ->
        (t as StringValue).evalToCharArray()
    }
    res.typeParams = listOf(stringTypeParam)
    res.formalParams = listOf()
    val outputSubstitution = Substitution(listType.typeParams, listOf(charType, stringTypeParam))
    val outputType = outputSubstitution.apply(listType)
    res.returnType = outputType

    res.costExpression = stringTypeParam
    return res
}

internal object StringEqualityOpMembers {
    fun members(
        stringType: ParameterizedBasicTypeSymbol,
        stringTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): Map<String, ParameterizedMemberPluginSymbol> = mapOf(
        BinaryOperator.Equal.idStr to pluginEquals(stringType, stringTypeParam, booleanType),
        BinaryOperator.NotEqual.idStr to pluginNotEquals(stringType, stringTypeParam, booleanType),
        BinaryOperator.Add.idStr to pluginAdd(stringType, stringTypeParam)
    )

    private fun pluginAdd(
        stringType: ParameterizedBasicTypeSymbol,
        stringTypeParam: ImmutableOmicronTypeParameter
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            stringType,
            GroundIdentifier(BinaryOperator.Add.idStr),
            DualOmicronPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as StringValue).evalAdd(args.first())
        }
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.stringInputTypeId)
        res.define(inputTypeArg.gid, inputTypeArg)
        res.typeParams = listOf(stringTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(stringType.typeParams, listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(stringType)
        val formalParamId = GroundIdentifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg =
            SumCostExpression(
                listOf(
                    stringTypeParam,
                    inputTypeArg
                )
            )
        val outputSubstitution = Substitution(listOf(stringTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(stringType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginEquals(
        stringType: ParameterizedBasicTypeSymbol,
        stringTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            stringType,
            GroundIdentifier(BinaryOperator.Equal.idStr),
            DualOmicronPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        }
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.stringInputTypeId)
        res.define(inputTypeArg.gid, inputTypeArg)
        res.typeParams = listOf(stringTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(stringType.typeParams, listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(stringType)
        val formalParamId = GroundIdentifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg =
            ProductCostExpression(
                listOf(
                    CommonCostExpressions.twoPass,
                    MaxCostExpression(
                        listOf(
                            stringTypeParam,
                            inputTypeArg
                        )
                    )
                )
            )
        res.returnType = booleanType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginNotEquals(
        stringType: ParameterizedBasicTypeSymbol,
        stringTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            stringType,
            GroundIdentifier(BinaryOperator.NotEqual.idStr),
            DualOmicronPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        }
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.stringInputTypeId)
        res.define(inputTypeArg.gid, inputTypeArg)
        res.typeParams = listOf(stringTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(stringType.typeParams, listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(stringType)
        val formalParamId = GroundIdentifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg =
            ProductCostExpression(
                listOf(
                    CommonCostExpressions.twoPass,
                    MaxCostExpression(
                        listOf(
                            stringTypeParam,
                            inputTypeArg
                        )
                    )
                )
            )
        res.returnType = booleanType

        res.costExpression = outputTypeArg
        return res
    }
}