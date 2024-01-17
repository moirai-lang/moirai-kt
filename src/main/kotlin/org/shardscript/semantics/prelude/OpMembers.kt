package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*

object IntegerMathOpMembers {
    val add = pluginAdd()
    val sub = pluginSub()
    val mul = pluginMul()
    val div = pluginDiv()
    val mod = pluginMod()
    val negate = pluginNegate()

    fun members(): Map<String, GroundMemberPluginSymbol> = mapOf(
        BinaryOperator.Add.idStr to add,
        BinaryOperator.Sub.idStr to sub,
        BinaryOperator.Mul.idStr to mul,
        BinaryOperator.Div.idStr to div,
        BinaryOperator.Mod.idStr to mod,
        UnaryOperator.Negate.idStr to negate
    )

    private fun pluginAdd(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.Add.idStr)
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalAdd(args.first())
        }
        res.costExpression = ConstantFinTypeSymbol
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.intType
        return res
    }

    private fun pluginSub(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.Sub.idStr)
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalSub(args.first())
        }
        res.costExpression = ConstantFinTypeSymbol
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.intType
        return res
    }

    private fun pluginMul(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.Mul.idStr)
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalMul(args.first())
        }
        res.costExpression = ConstantFinTypeSymbol
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.intType
        return res
    }

    private fun pluginDiv(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.Div.idStr)
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalDiv(args.first())
        }
        res.costExpression = ConstantFinTypeSymbol
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.intType
        return res
    }

    private fun pluginMod(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.Mod.idStr)
        ) { t: Value, args: List<Value> ->
            (t as MathValue).evalMod(args.first())
        }
        res.costExpression = ConstantFinTypeSymbol
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.intType
        return res
    }

    private fun pluginNegate(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, UnaryOperator.Negate.idStr)
        ) { t: Value, _: List<Value> ->
            (t as MathValue).evalNegate()
        }
        res.costExpression = ConstantFinTypeSymbol
        res.formalParams = listOf()
        res.returnType = Lang.intType
        return res
    }
}

object IntegerOrderOpMembers {
    fun members(
        valueType: BasicTypeSymbol,
        costExpression: CostExpression,
        booleanType: BasicTypeSymbol
    ): Map<String, GroundMemberPluginSymbol> = mapOf(
        BinaryOperator.GreaterThan.idStr to pluginGreaterThan(valueType, costExpression, booleanType),
        BinaryOperator.GreaterThanEqual.idStr to pluginGreaterThanOrEquals(valueType, costExpression, booleanType),
        BinaryOperator.LessThan.idStr to pluginLessThan(valueType, costExpression, booleanType),
        BinaryOperator.LessThanEqual.idStr to pluginLessThanOrEquals(valueType, costExpression, booleanType)
    )

    private fun pluginGreaterThan(
        valueType: BasicTypeSymbol,
        costExpression: CostExpression,
        booleanType: BasicTypeSymbol
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(NotInSource, BinaryOperator.GreaterThan.idStr)
        ) { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThan(args.first())
        }
        res.costExpression = costExpression
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = booleanType
        return res
    }

    private fun pluginGreaterThanOrEquals(
        valueType: BasicTypeSymbol,
        costExpression: CostExpression,
        booleanType: BasicTypeSymbol
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(NotInSource, BinaryOperator.GreaterThanEqual.idStr)
        ) { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThanOrEquals(args.first())
        }
        res.costExpression = costExpression
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = booleanType
        return res
    }

    private fun pluginLessThan(
        valueType: BasicTypeSymbol,
        costExpression: CostExpression,
        booleanType: BasicTypeSymbol
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(NotInSource, BinaryOperator.LessThan.idStr)
        ) { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThan(args.first())
        }
        res.costExpression = costExpression
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = booleanType
        return res
    }

    private fun pluginLessThanOrEquals(
        valueType: BasicTypeSymbol,
        costExpression: CostExpression,
        booleanType: BasicTypeSymbol
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(NotInSource, BinaryOperator.LessThanEqual.idStr)
        ) { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThanOrEquals(args.first())
        }
        res.costExpression = costExpression
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = booleanType
        return res
    }
}

object ValueEqualityOpMembers {
    fun members(
        valueType: BasicTypeSymbol,
        costExpression: CostExpression,
        booleanType: BasicTypeSymbol
    ): Map<String, GroundMemberPluginSymbol> = mapOf(
        BinaryOperator.Equal.idStr to pluginEquals(valueType, costExpression, booleanType),
        BinaryOperator.NotEqual.idStr to pluginNotEquals(valueType, costExpression, booleanType)
    )

    private fun pluginEquals(
        valueType: BasicTypeSymbol,
        costExpression: CostExpression,
        booleanType: BasicTypeSymbol
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr)
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        }
        res.costExpression = costExpression
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = booleanType
        return res
    }

    private fun pluginNotEquals(
        valueType: BasicTypeSymbol,
        costExpression: CostExpression,
        booleanType: BasicTypeSymbol
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr)
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        }
        res.costExpression = costExpression
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = booleanType
        return res
    }
}

object ValueLogicalOpMembers {
    fun members(logicalType: BasicTypeSymbol, costExpression: CostExpression): Map<String, GroundMemberPluginSymbol> =
        mapOf(
            BinaryOperator.And.idStr to pluginAnd(logicalType, costExpression),
            BinaryOperator.Or.idStr to pluginOr(logicalType, costExpression),
            UnaryOperator.Not.idStr to pluginNot(logicalType, costExpression)
        )

    private fun pluginAnd(logicalType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            logicalType,
            Identifier(NotInSource, BinaryOperator.And.idStr)
        ) { t: Value, args: List<Value> ->
            (t as LogicalValue).evalAnd(args.first())
        }
        res.costExpression = costExpression
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, logicalType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = logicalType
        return res
    }

    private fun pluginOr(logicalType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            logicalType,
            Identifier(NotInSource, BinaryOperator.Or.idStr)
        ) { t: Value, args: List<Value> ->
            (t as LogicalValue).evalOr(args.first())
        }
        res.costExpression = costExpression
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, logicalType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = logicalType
        return res
    }

    private fun pluginNot(logicalType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            logicalType,
            Identifier(NotInSource, UnaryOperator.Not.idStr)
        ) { t: Value, _: List<Value> ->
            (t as LogicalValue).evalNot()
        }
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = logicalType
        return res
    }
}
