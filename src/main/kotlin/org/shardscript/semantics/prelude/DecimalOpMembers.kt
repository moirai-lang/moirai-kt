package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.DualFinPluginInstantiation
import org.shardscript.semantics.infer.SingleParentArgInstantiation
import org.shardscript.semantics.infer.Substitution

object DecimalMathOpMembers {
    val add = pluginAdd()
    val sub = pluginSub()
    val mul = pluginMul()
    val div = pluginDiv()
    val mod = pluginMod()
    val negate = pluginNegate()

    fun members(): Map<String, Symbol> = mapOf(
        BinaryOperator.Add.idStr to add,
        BinaryOperator.Sub.idStr to sub,
        BinaryOperator.Mul.idStr to mul,
        BinaryOperator.Div.idStr to div,
        BinaryOperator.Mod.idStr to mod,
        UnaryOperator.Negate.idStr to negate
    )

    private fun pluginAdd(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.decimalType,
            Identifier(NotInSource, BinaryOperator.Add.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalAdd(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(Lang.decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(Lang.decimalType)
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        Lang.decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        val outputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(Lang.decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginSub(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.decimalType,
            Identifier(NotInSource, BinaryOperator.Sub.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalSub(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(Lang.decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(Lang.decimalType)
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        Lang.decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        val outputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(Lang.decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginMul(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.decimalType,
            Identifier(NotInSource, BinaryOperator.Mul.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalMul(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(Lang.decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(Lang.decimalType)
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = ProductCostExpression(
            listOf(
                CommonCostExpressions.twoPass,
                MaxCostExpression(
                    listOf(
                        Lang.decimalTypeParam,
                        inputTypeArg
                    )
                )
            )
        )
        val outputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(Lang.decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginDiv(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.decimalType,
            Identifier(NotInSource, BinaryOperator.Div.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalDiv(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(Lang.decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(Lang.decimalType)
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = MaxCostExpression(
            listOf(
                Lang.decimalTypeParam,
                inputTypeArg
            )
        )
        val outputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(Lang.decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginMod(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.decimalType,
            Identifier(NotInSource, BinaryOperator.Mod.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalMod(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(Lang.decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(Lang.decimalType)
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, inputType)
        res.define(formalParamId, formalParam)
        res.formalParams = listOf(formalParam)

        val outputTypeArg = MaxCostExpression(
            listOf(
                Lang.decimalTypeParam,
                inputTypeArg
            )
        )
        val outputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(outputTypeArg))
        val outputType = outputSubstitution.apply(Lang.decimalType)
        res.returnType = outputType

        res.costExpression = outputTypeArg
        return res
    }

    private fun pluginNegate(): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            Lang.decimalType,
            Identifier(NotInSource, UnaryOperator.Negate.idStr),
            SingleParentArgInstantiation
        ) { t: Value, _: List<Value> ->
            (t as MathValue).evalNegate()
        }
        res.typeParams = listOf(Lang.decimalTypeParam)
        res.formalParams = listOf()

        val outputSubstitution = Substitution(listOf(Lang.decimalTypeParam), listOf(Lang.decimalTypeParam))
        val outputType = outputSubstitution.apply(Lang.decimalType)
        res.returnType = outputType
        res.costExpression = Lang.decimalTypeParam
        return res
    }
}

object DecimalOrderOpMembers {
    fun members(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableFinTypeParameter,
        booleanType: BasicTypeSymbol
    ): Map<String, ParameterizedMemberPluginSymbol> = mapOf(
        BinaryOperator.GreaterThan.idStr to pluginGreaterThan(decimalType, decimalTypeParam, booleanType),
        BinaryOperator.GreaterThanEqual.idStr to pluginGreaterThanOrEquals(decimalType, decimalTypeParam, booleanType),
        BinaryOperator.LessThan.idStr to pluginLessThan(decimalType, decimalTypeParam, booleanType),
        BinaryOperator.LessThanEqual.idStr to pluginLessThanOrEquals(decimalType, decimalTypeParam, booleanType)
    )

    private fun pluginGreaterThan(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableFinTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(NotInSource, BinaryOperator.GreaterThan.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThan(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier(NotInSource, "other")
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
        decimalTypeParam: ImmutableFinTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(NotInSource, BinaryOperator.GreaterThanEqual.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThanOrEquals(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier(NotInSource, "other")
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
        decimalTypeParam: ImmutableFinTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(NotInSource, BinaryOperator.LessThan.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThan(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier(NotInSource, "other")
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
        decimalTypeParam: ImmutableFinTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(NotInSource, BinaryOperator.LessThanEqual.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThanOrEquals(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier(NotInSource, "other")
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
        decimalTypeParam: ImmutableFinTypeParameter,
        booleanType: BasicTypeSymbol
    ): Map<String, ParameterizedMemberPluginSymbol> = mapOf(
        BinaryOperator.Equal.idStr to pluginEquals(decimalType, decimalTypeParam, booleanType),
        BinaryOperator.NotEqual.idStr to pluginNotEquals(decimalType, decimalTypeParam, booleanType)
    )

    private fun pluginEquals(
        decimalType: ParameterizedBasicTypeSymbol,
        decimalTypeParam: ImmutableFinTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier(NotInSource, "other")
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
        decimalTypeParam: ImmutableFinTypeParameter,
        booleanType: BasicTypeSymbol
    ): ParameterizedMemberPluginSymbol {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DualFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        }
        val inputTypeArg = ImmutableFinTypeParameter(res, Lang.decimalInputTypeId)
        res.define(inputTypeArg.identifier, inputTypeArg)
        res.typeParams = listOf(decimalTypeParam, inputTypeArg)

        val inputSubstitution = Substitution(listOf(decimalTypeParam), listOf(inputTypeArg))
        val inputType = inputSubstitution.apply(decimalType)
        val formalParamId = Identifier(NotInSource, "other")
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