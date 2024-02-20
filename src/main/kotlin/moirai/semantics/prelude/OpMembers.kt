package moirai.semantics.prelude

import moirai.semantics.core.*

internal object IntegerMathOpMembers {
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
        )
        res.costExpression = ConstantFin
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
        )
        res.costExpression = ConstantFin
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
        )
        res.costExpression = ConstantFin
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
        )
        res.costExpression = ConstantFin
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
        )
        res.costExpression = ConstantFin
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
        )
        res.costExpression = ConstantFin
        res.formalParams = listOf()
        res.returnType = Lang.intType
        return res
    }
}

internal object IntegerOrderOpMembers {
    val greaterThan = pluginGreaterThan()
    val greaterThanOrEquals = pluginGreaterThanOrEquals()
    val lessThan = pluginLessThan()
    val lessThanOrEquals = pluginLessThanOrEquals()

    fun members(): Map<String, GroundMemberPluginSymbol> = mapOf(
        BinaryOperator.GreaterThan.idStr to greaterThan,
        BinaryOperator.GreaterThanEqual.idStr to greaterThanOrEquals,
        BinaryOperator.LessThan.idStr to lessThan,
        BinaryOperator.LessThanEqual.idStr to lessThanOrEquals
    )

    private fun pluginGreaterThan(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.GreaterThan.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }

    private fun pluginGreaterThanOrEquals(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.GreaterThanEqual.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }

    private fun pluginLessThan(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.LessThan.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }

    private fun pluginLessThanOrEquals(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.LessThanEqual.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }
}

internal object IntegerEqualityOpMembers {
    val equals = pluginEquals()
    val notEquals = pluginNotEquals()

    fun members(): Map<String, GroundMemberPluginSymbol> = mapOf(
        BinaryOperator.Equal.idStr to equals,
        BinaryOperator.NotEqual.idStr to notEquals
    )

    private fun pluginEquals(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }

    private fun pluginNotEquals(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.intType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.intType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }
}

internal object BooleanEqualityOpMembers {
    val equals = pluginEquals()
    val notEquals = pluginNotEquals()

    fun members(): Map<String, GroundMemberPluginSymbol> = mapOf(
        BinaryOperator.Equal.idStr to equals,
        BinaryOperator.NotEqual.idStr to notEquals
    )

    private fun pluginEquals(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.booleanType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.booleanType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }

    private fun pluginNotEquals(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.booleanType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.booleanType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }
}

internal object CharEqualityOpMembers {
    val equals = pluginEquals()
    val notEquals = pluginNotEquals()

    fun members(): Map<String, GroundMemberPluginSymbol> = mapOf(
        BinaryOperator.Equal.idStr to equals,
        BinaryOperator.NotEqual.idStr to notEquals
    )

    private fun pluginEquals(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.charType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.charType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }

    private fun pluginNotEquals(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.charType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.charType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }
}

internal object ValueLogicalOpMembers {
    val and = pluginAnd()
    val or = pluginOr()
    val not = pluginNot()

    fun members(): Map<String, GroundMemberPluginSymbol> =
        mapOf(
            BinaryOperator.And.idStr to and,
            BinaryOperator.Or.idStr to or,
            UnaryOperator.Not.idStr to not
        )

    private fun pluginAnd(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.booleanType,
            Identifier(NotInSource, BinaryOperator.And.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.booleanType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }

    private fun pluginOr(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.booleanType,
            Identifier(NotInSource, BinaryOperator.Or.idStr)
        )
        res.costExpression = ConstantFin
        val formalParamId = Identifier(NotInSource, "other")
        val formalParam = FunctionFormalParameterSymbol(res, formalParamId, Lang.booleanType)
        res.define(formalParamId, formalParam)

        res.formalParams = listOf(formalParam)
        res.returnType = Lang.booleanType
        return res
    }

    private fun pluginNot(): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            Lang.booleanType,
            Identifier(NotInSource, UnaryOperator.Not.idStr)
        )
        res.costExpression = ConstantFin
        res.formalParams = listOf()
        res.returnType = Lang.booleanType
        return res
    }
}
