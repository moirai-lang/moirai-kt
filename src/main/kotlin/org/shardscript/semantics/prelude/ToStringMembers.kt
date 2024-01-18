package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.SingleParentArgInstantiation
import org.shardscript.semantics.infer.Substitution

object ToStringMembers {
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

    fun insertDecimalToStringMember(): ParameterizedMemberPluginSymbol {
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

    fun insertStringToStringMember(): ParameterizedMemberPluginSymbol {
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