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
    val omicron = OmicronTypeSymbol(Lang.sByteOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
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
    val omicron = OmicronTypeSymbol(Lang.shortOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
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
    val omicron = OmicronTypeSymbol(Lang.intOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
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
    val omicron = OmicronTypeSymbol(Lang.longOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    longType.define(res.identifier, res)
}

@UseExperimental(ExperimentalUnsignedTypes::class)
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
    val omicron = OmicronTypeSymbol(Lang.byteOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    sByteType.define(res.identifier, res)
}

@UseExperimental(ExperimentalUnsignedTypes::class)
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
    val omicron = OmicronTypeSymbol(Lang.uShortOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    shortType.define(res.identifier, res)
}

@UseExperimental(ExperimentalUnsignedTypes::class)
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
    val omicron = OmicronTypeSymbol(Lang.uIntOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    integerType.define(res.identifier, res)
}

@UseExperimental(ExperimentalUnsignedTypes::class)
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
    val omicron = OmicronTypeSymbol(Lang.uLongOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
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
    val omicron = OmicronTypeSymbol(Lang.unitOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
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
    val omicron = OmicronTypeSymbol(Lang.booleanOmicron)
    res.costExpression = omicron

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
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
    val omicron = decimalType.typeParams.first()
    res.costExpression = omicron as ImmutableOmicronTypeParameter
    res.typeParams = listOf(omicron)

    val substitution = Substitution(stringType.typeParams, listOf(omicron))
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
    val omicron = stringType.typeParams.first()
    res.costExpression = omicron as ImmutableOmicronTypeParameter
    res.typeParams = listOf(omicron)

    res.formalParams = listOf()
    val substitution = Substitution(stringType.typeParams, listOf(omicron))
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
    val omicron = OmicronTypeSymbol(Lang.charOmicron)
    res.costExpression = omicron
    val substitution = Substitution(stringType.typeParams, listOf(omicron))
    val stringInstantiation = substitution.apply(stringType)
    res.formalParams = listOf()
    res.returnType = stringInstantiation
    charType.define(res.identifier, res)
}