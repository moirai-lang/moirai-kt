package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.SingleParentArgInstantiation
import org.shardscript.semantics.infer.Substitution

fun insertSByteToStringMember(
    sByteType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        sByteType,
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as SByteValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.sByteFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    sByteType.define(res.identifier, res)
}

fun insertShortToStringMember(
    shortType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        shortType,
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as ShortValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.shortFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    shortType.define(res.identifier, res)
}

fun insertIntegerToStringMember(
    integerType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        integerType,
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as IntValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.intFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    integerType.define(res.identifier, res)
}

fun insertLongToStringMember(
    longType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        longType,
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as LongValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.longFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    longType.define(res.identifier, res)
}

fun insertByteToStringMember(
    sByteType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        sByteType,
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as ByteValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.byteFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    sByteType.define(res.identifier, res)
}

fun insertUShortToStringMember(
    shortType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        shortType,
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as UShortValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.uShortFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    shortType.define(res.identifier, res)
}

fun insertUIntToStringMember(
    integerType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        integerType,
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as UIntValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.uIntFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    integerType.define(res.identifier, res)
}

fun insertULongToStringMember(
    longType: BasicTypeSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        longType,
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as ULongValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.uLongFin)
    res.costExpression = fin

    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    longType.define(res.identifier, res)
}

fun insertUnitToStringMember(
    unitType: ObjectSymbol,
    stringType: ParameterizedBasicTypeSymbol
) {
    val res = GroundMemberPluginSymbol(
        unitType,
        Identifier(StringMethods.ToString.idStr),
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
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as BooleanValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.booleanFin)
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
        Identifier(StringMethods.ToString.idStr),
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
        Identifier(StringMethods.ToString.idStr),
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
        Identifier(StringMethods.ToString.idStr),
    { t: Value, _: List<Value> ->
        (t as CharValue).evalToString()
    })
    val fin = FinTypeSymbol(Lang.charFin)
    res.costExpression = fin
    val substitution = Substitution(stringType.typeParams, listOf(fin))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    charType.define(res.identifier, res)
}