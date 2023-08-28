package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.DualOmicronPluginInstantiation
import org.shardscript.semantics.infer.SingleParentArgInstantiation
import org.shardscript.semantics.infer.Substitution

object DecimalMathOpMembers {
    fun members(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter
    ): Map<String, Symbol> = mapOf(
        BinaryOperator.Add.idStr to pluginAdd(decimalType, decimalTypeParam),
        BinaryOperator.Sub.idStr to pluginSub(decimalType, decimalTypeParam),
        BinaryOperator.Mul.idStr to pluginMul(decimalType, decimalTypeParam),
        BinaryOperator.Div.idStr to pluginDiv(decimalType, decimalTypeParam),
        BinaryOperator.Mod.idStr to pluginMod(decimalType, decimalTypeParam),
        UnaryOperator.Negate.idStr to pluginNegate(decimalType, decimalTypeParam)
    )

    private fun pluginAdd(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.Add.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as MathValue).evalAdd(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        val outputSubstitution = Substitution(listOf(decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginSub(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.Sub.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as MathValue).evalSub(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        val outputSubstitution = Substitution(listOf(decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginMul(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.Mul.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as MathValue).evalMul(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        val outputSubstitution = Substitution(listOf(decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginDiv(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.Div.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as MathValue).evalDiv(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = MaxCostExpression(
            listOf(
                decimalTypeParam,
                inputTypeArg
            )
        )
        val outputSubstitution = Substitution(listOf(decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginMod(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.Mod.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as MathValue).evalMod(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = MaxCostExpression(
            listOf(
                decimalTypeParam,
                inputTypeArg
            )
        )
        val outputSubstitution = Substitution(listOf(decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginNegate(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(UnaryOperator.Negate.idStr),
            SingleParentArgInstantiation,
        { t: Value, _: List<Value> ->
            (t as MathValue).evalNegate()
        })
        res.typeParams = listOf(decimalTypeParam)
        res.formalParams = listOf()

        val outputSubstitution = Substitution(listOf(decimalTypeParam), listOf(decimalTypeParam))
        val outputType = outputSubstitution.apply(decimalType)
        res.returnType = outputType
        res.costExpression = decimalTypeParam
        return res
    }
}

object DecimalOrderOpMembers {
    fun members(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): Map<String, ParameterizedMemberPluginSymbol> = mapOf(
        BinaryOperator.GreaterThan.idStr to pluginGreaterThan(decimalType, decimalTypeParam, booleanType),
        BinaryOperator.GreaterThanEqual.idStr to pluginGreaterThanOrEquals(decimalType, decimalTypeParam, booleanType),
        BinaryOperator.LessThan.idStr to pluginLessThan(decimalType, decimalTypeParam, booleanType),
        BinaryOperator.LessThanEqual.idStr to pluginLessThanOrEquals(decimalType, decimalTypeParam, booleanType)
    )

    private fun pluginGreaterThan(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.GreaterThan.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThan(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        res.returnType = booleanType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginGreaterThanOrEquals(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.GreaterThanEqual.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThanOrEquals(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        res.returnType = booleanType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginLessThan(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.LessThan.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThan(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        res.returnType = booleanType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginLessThanOrEquals(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.LessThanEqual.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThanOrEquals(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
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

object DecimalEqualityOpMembers {
    fun members(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): Map<String, ParameterizedMemberPluginSymbol> = mapOf(
        BinaryOperator.Equal.idStr to pluginEquals(decimalType, decimalTypeParam, booleanType),
        BinaryOperator.NotEqual.idStr to pluginNotEquals(decimalType, decimalTypeParam, booleanType)
    )

    private fun pluginEquals(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.Equal.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
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
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableOmicronTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(BinaryOperator.NotEqual.idStr),
            DualOmicronPluginInstantiation,
        { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        })
        val inputTypeArg = ImmutableOmicronTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        decimalTypeParam,
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