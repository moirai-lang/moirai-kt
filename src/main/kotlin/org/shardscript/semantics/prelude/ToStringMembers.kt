package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.SingleParentArgInstantiation
import org.shardscript.semantics.infer.Substitution

fun insertIntegerToStringMember(
    integerType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        integerType,
        Identifier(NotInSource, StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as IntValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.INT_FIN)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    integerType.define(res.identifier, res)
}

fun insertUnitToStringMember(
    unitType: PlatformObjectSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        unitType,
        Identifier(NotInSource, StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as UnitValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.unitFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    unitType.define(res.identifier, res)
}

fun insertBooleanToStringMember(
    booleanType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        booleanType,
        Identifier(NotInSource, StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as BooleanValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.BOOL_FIN)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    booleanType.define(res.identifier, res)
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

fun insertCharToStringMember(
    charType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        charType,
        Identifier(NotInSource, StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as CharValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.CHAR_FIN)
    res.costExpression = fin
    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    charType.define(res.identifier, res)
}