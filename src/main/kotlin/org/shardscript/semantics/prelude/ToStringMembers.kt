package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.SingleParentArgInstantiation
import org.shardscript.semantics.infer.Substitution

object ToStringMembers {
    val integerToStringMember = insertIntegerToStringMember()
    val unitToStringMember = insertUnitToStringMember()
    val booleanToStringMember = insertBooleanToStringMember()
    val charToStringMember = insertCharToStringMember()

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

    fun insertDecimalToStringMember(
        decimalType: ParameterizedBasicTypeSymbol,
        stringType: ParameterizedBasicTypeSymbol
    ) {
        val res = ParameterizedMemberPluginSymbol(
            decimalType,
            Identifier(NotInSource, StringMethods.ToString.idStr),
            SingleParentArgInstantiation,
            { t: Value, _: List<Value> ->
                (t as DecimalValue).evalToString()
            })
        val fin = decimalType.typeParams.first()
        res.costExpression = fin as ImmutableFinTypeParameter
        res.typeParams = listOf(fin)

        val substitution = Substitution(stringType.typeParams, listOf(fin))
        val stringInstantiation = substitution.apply(stringType)
        res.formalParams = listOf()
        res.returnType = stringInstantiation
        decimalType.define(res.identifier, res)
    }

    fun insertStringToStringMember(
        stringType: ParameterizedBasicTypeSymbol
    ) {
        val res = ParameterizedMemberPluginSymbol(
            stringType,
            Identifier(NotInSource, StringMethods.ToString.idStr),
            SingleParentArgInstantiation,
            { t: Value, _: List<Value> ->
                (t as StringValue).evalToString()
            })
        val fin = stringType.typeParams.first()
        res.costExpression = fin as ImmutableFinTypeParameter
        res.typeParams = listOf(fin)

        res.formalParams = listOf()
        val substitution = Substitution(stringType.typeParams, listOf(fin))
        val outputType = substitution.apply(stringType)
        res.returnType = outputType
        stringType.define(res.identifier, res)
    }
}