package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*

object IntegerMathOpMembers {
    fun members(valueType: BasicTypeSymbol, costExpression: CostExpression): Map<String, GroundMemberPluginSymbol> = mapOf(
        BinaryOperator.Add.idStr to pluginAdd(valueType, costExpression),
        BinaryOperator.Sub.idStr to pluginSub(valueType, costExpression),
        BinaryOperator.Mul.idStr to pluginMul(valueType, costExpression),
        BinaryOperator.Div.idStr to pluginDiv(valueType, costExpression),
        BinaryOperator.Mod.idStr to pluginMod(valueType, costExpression),
        UnaryOperator.Negate.idStr to pluginNegate(valueType, costExpression)
    )

    private fun pluginAdd(valueType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(BinaryOperator.Add.idStr),
        { t: Value, args: List<Value> ->
            (t as MathValue).evalAdd(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = valueType
        return res
    }

    private fun pluginSub(valueType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(BinaryOperator.Sub.idStr),
        { t: Value, args: List<Value> ->
            (t as MathValue).evalSub(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = valueType
        return res
    }

    private fun pluginMul(valueType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(BinaryOperator.Mul.idStr),
        { t: Value, args: List<Value> ->
            (t as MathValue).evalMul(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = valueType
        return res
    }

    private fun pluginDiv(valueType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(BinaryOperator.Div.idStr),
        { t: Value, args: List<Value> ->
            (t as MathValue).evalDiv(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = valueType
        return res
    }

    private fun pluginMod(valueType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(BinaryOperator.Mod.idStr),
        { t: Value, args: List<Value> ->
            (t as MathValue).evalMod(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, valueType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = valueType
        return res
    }

    private fun pluginNegate(valueType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            valueType,
            Identifier(UnaryOperator.Negate.idStr),
        { t: Value, _: List<Value> ->
            (t as MathValue).evalNegate()
        })
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = valueType
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
            Identifier(BinaryOperator.GreaterThan.idStr),
        { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThan(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
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
            Identifier(BinaryOperator.GreaterThanEqual.idStr),
        { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThanOrEquals(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
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
            Identifier(BinaryOperator.LessThan.idStr),
        { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThan(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
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
            Identifier(BinaryOperator.LessThanEqual.idStr),
        { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThanOrEquals(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
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
            Identifier(BinaryOperator.Equal.idStr),
        { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
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
            Identifier(BinaryOperator.NotEqual.idStr),
        { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
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
            Identifier(BinaryOperator.And.idStr),
        { t: Value, args: List<Value> ->
            (t as LogicalValue).evalAnd(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, logicalType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = logicalType
        return res
    }

    private fun pluginOr(logicalType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            logicalType,
            Identifier(BinaryOperator.Or.idStr),
        { t: Value, args: List<Value> ->
            (t as LogicalValue).evalOr(args.first())
        })
        res.costExpression = costExpression
        val formalParamId = Identifier("other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, logicalType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = logicalType
        return res
    }

    private fun pluginNot(logicalType: BasicTypeSymbol, costExpression: CostExpression): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            logicalType,
            Identifier(UnaryOperator.Not.idStr),
        { t: Value, _: List<Value> ->
            (t as LogicalValue).evalNot()
        })
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = logicalType
        return res
    }
}
