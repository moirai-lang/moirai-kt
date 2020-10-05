package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*
import java.math.BigInteger

@UseExperimental(ExperimentalUnsignedTypes::class)
object Lang {
    val shardId = GroundIdentifier("shard")
    val langId = GroundIdentifier("lang")
    val unitId = GroundIdentifier("Unit")
    val booleanId = GroundIdentifier("Boolean")

    val sByteId = GroundIdentifier("SByte")
    const val sByteSuffix = "s8"
    val shortId = GroundIdentifier("Short")
    const val shortSuffix = "s16"
    val intId = GroundIdentifier("Int")
    val longId = GroundIdentifier("Long")
    const val longSuffix = "s64"

    val byteId = GroundIdentifier("Byte")
    const val byteSuffix = "u8"
    val uShortId = GroundIdentifier("UShort")
    const val uShortSuffix = "u16"
    val uIntId = GroundIdentifier("UInt")
    const val uIntSuffix = "u32"
    val uLongId = GroundIdentifier("ULong")
    const val uLongSuffix = "u64"

    val charId = GroundIdentifier("Char")
    val stringId = GroundIdentifier("String")
    val stringTypeId = GroundIdentifier("#O")
    val stringInputTypeId = GroundIdentifier("#P")

    val decimalId = GroundIdentifier("Decimal")
    val decimalTypeId = GroundIdentifier("#O")
    val decimalInputTypeId = GroundIdentifier("#P")

    val listId = GroundIdentifier("List")
    val listElementTypeId = GroundIdentifier("E")
    val listOmicronTypeId = GroundIdentifier("#O")
    val listInputOmicronTypeId = GroundIdentifier("#P")

    val mutableListId = GroundIdentifier("MutableList")
    val mutableListElementTypeId = GroundIdentifier("E")
    val mutableListOmicronTypeId = GroundIdentifier("#O")
    val mutableListInputOmicronTypeId = GroundIdentifier("#P")

    val pairId = GroundIdentifier("Pair")
    val pairFirstTypeId = GroundIdentifier("A")
    val pairSecondTypeId = GroundIdentifier("B")
    val pairFirstId = GroundIdentifier("first")
    val pairSecondId = GroundIdentifier("second")

    val dictionaryId = GroundIdentifier("Dictionary")
    val dictionaryKeyTypeId = GroundIdentifier("K")
    val dictionaryValueTypeId = GroundIdentifier("V")
    val dictionaryOmicronTypeId = GroundIdentifier("#O")
    val dictionaryInputOmicronTypeId = GroundIdentifier("#P")

    val mutableDictionaryId = GroundIdentifier("MutableDictionary")
    val mutableDictionaryKeyTypeId = GroundIdentifier("K")
    val mutableDictionaryValueTypeId = GroundIdentifier("V")
    val mutableDictionaryOmicronTypeId = GroundIdentifier("#O")
    val mutableDictionaryInputOmicronTypeId = GroundIdentifier("#P")

    val setId = GroundIdentifier("Set")
    val setElementTypeId = GroundIdentifier("E")
    val setOmicronTypeId = GroundIdentifier("#O")
    val setInputOmicronTypeId = GroundIdentifier("#P")

    val mutableSetId = GroundIdentifier("MutableSet")
    val mutableSetElementTypeId = GroundIdentifier("E")
    val mutableSetOmicronTypeId = GroundIdentifier("#O")
    val mutableSetInputOmicronTypeId = GroundIdentifier("#P")

    val optionId = GroundIdentifier("Option")
    val optionTypeParamId = GroundIdentifier("E")
    val someId = GroundIdentifier("Some")
    val someFieldId = GroundIdentifier("element")
    val noneId = GroundIdentifier("None")

    val resultId = GroundIdentifier("Result")
    val successId = GroundIdentifier("Success")
    val failureId = GroundIdentifier("Failure")
    val resultSuccessTypeParamId = GroundIdentifier("S")
    val resultFailureTypeParamId = GroundIdentifier("F")
    val successFieldId = GroundIdentifier("info")
    val failureFieldId = GroundIdentifier("info")

    val eitherId = GroundIdentifier("Either")
    val rightId = GroundIdentifier("Right")
    val leftId = GroundIdentifier("Left")
    val eitherRightTypeParamId = GroundIdentifier("R")
    val eitherLeftTypeParamId = GroundIdentifier("L")
    val rightFieldId = GroundIdentifier("value")
    val leftFieldId = GroundIdentifier("value")

    val rangeId = GroundIdentifier("range")
    val rangeTypeId = GroundIdentifier("#O")
    val randomId = GroundIdentifier("random")
    val randomTypeId = GroundIdentifier("A")

    val sByteOmicron: BigInteger = (Byte.MIN_VALUE.toString().length + sByteSuffix.length).toBigInteger()
    val shortOmicron: BigInteger = (Short.MIN_VALUE.toString().length + shortSuffix.length).toBigInteger()
    val intOmicron: BigInteger = (Int.MIN_VALUE.toString().length).toBigInteger()
    val longOmicron: BigInteger = (Long.MIN_VALUE.toString().length + longSuffix.length).toBigInteger()
    val byteOmicron: BigInteger = (UByte.MIN_VALUE.toString().length + byteSuffix.length).toBigInteger()
    val uShortOmicron: BigInteger = (UShort.MIN_VALUE.toString().length + uShortSuffix.length).toBigInteger()
    val uIntOmicron: BigInteger = (UInt.MIN_VALUE.toString().length + uIntSuffix.length).toBigInteger()
    val uLongOmicron: BigInteger = (ULong.MIN_VALUE.toString().length + uLongSuffix.length).toBigInteger()
    val unitOmicron: BigInteger = unitId.name.length.toBigInteger()
    val booleanOmicron: BigInteger = false.toString().length.toBigInteger()
    val charOmicron: BigInteger = BigInteger.ONE

    fun isUnitExactly(symbol: Symbol): Boolean =
        when (generatePath(symbol)) {
            listOf(shardId.name, langId.name, unitId.name) -> true
            else -> false
        }

    fun initNamespace(architecture: Architecture, prelude: PreludeTable, root: Scope<Symbol>) {
        // Top-level Namespace
        val shardNS = Namespace(
            root,
            shardId
        )

        // Lang Namespace
        val langNS = Namespace(
            shardNS,
            langId
        )

        // Unit
        val unitObject = ObjectSymbol(
            langNS,
            unitId,
            userTypeFeatureSupport
        )

        // Boolean
        val booleanType = BasicTypeSymbol(
            langNS,
            booleanId
        )
        val constantOmicron = OmicronTypeSymbol(architecture.defaultNodeCost)
        ValueEqualityOpMembers.members(booleanType, constantOmicron, booleanType).forEach { (name, plugin) ->
            booleanType.define(GroundIdentifier(name), plugin)
        }

        ValueLogicalOpMembers.members(booleanType, constantOmicron).forEach { (name, plugin) ->
            booleanType.define(GroundIdentifier(name), plugin)
        }

        // Integer Types
        val sByteType = intType(architecture, sByteId, booleanType, langNS, setOf())
        val shortType = intType(architecture, shortId, booleanType, langNS, setOf())
        val intType = intType(architecture, intId, booleanType, langNS, setOf())
        val longType = intType(architecture, longId, booleanType, langNS, setOf())

        val byteType = intType(architecture, byteId, booleanType, langNS, setOf(UnaryOperator.Negate.idStr))
        val uShortType = intType(architecture, uShortId, booleanType, langNS, setOf(UnaryOperator.Negate.idStr))
        val uIntType = intType(architecture, uIntId, booleanType, langNS, setOf(UnaryOperator.Negate.idStr))
        val uLongType = intType(architecture, uLongId, booleanType, langNS, setOf(UnaryOperator.Negate.idStr))

        // Decimal
        val decimalType = decimalType(decimalId, booleanType, langNS)

        // Char
        val charType = BasicTypeSymbol(
            langNS,
            charId
        )
        ValueEqualityOpMembers.members(charType, constantOmicron, charType).forEach { (name, plugin) ->
            charType.define(GroundIdentifier(name), plugin)
        }

        // List
        val listType = listCollectionType(architecture, langNS, intType, booleanType)

        // MutableList
        val mutableListType =
            mutableListCollectionType(architecture, langNS, intType, unitObject, booleanType, listType)

        // String
        val stringType = stringType(booleanType, intType, charType, listType, langNS)

        // ToString
        insertSByteToStringMember(sByteType, stringType)
        insertShortToStringMember(shortType, stringType)
        insertIntegerToStringMember(intType, stringType)
        insertLongToStringMember(longType, stringType)
        insertByteToStringMember(byteType, stringType)
        insertUShortToStringMember(uShortType, stringType)
        insertUIntToStringMember(uIntType, stringType)
        insertULongToStringMember(uLongType, stringType)
        insertUnitToStringMember(unitObject, stringType)
        insertBooleanToStringMember(booleanType, stringType)
        insertDecimalToStringMember(decimalType, stringType)
        insertCharToStringMember(charType, stringType)
        insertStringToStringMember(stringType)

        // Integer Conversions
        val integerTypes = mapOf(
            sByteType.gid to sByteType,
            shortType.gid to shortType,
            intType.gid to intType,
            longType.gid to longType,
            byteType.gid to byteType,
            uShortType.gid to uShortType,
            uIntType.gid to uIntType,
            uLongType.gid to uLongType
        )
        insertIntegerConversionMembers(architecture, sByteType, integerTypes)
        insertIntegerConversionMembers(architecture, shortType, integerTypes)
        insertIntegerConversionMembers(architecture, intType, integerTypes)
        insertIntegerConversionMembers(architecture, longType, integerTypes)
        insertIntegerConversionMembers(architecture, byteType, integerTypes)
        insertIntegerConversionMembers(architecture, uShortType, integerTypes)
        insertIntegerConversionMembers(architecture, uIntType, integerTypes)
        insertIntegerConversionMembers(architecture, uLongType, integerTypes)

        // Pair
        val pairType = ParameterizedRecordTypeSymbol(
            langNS,
            pairId,
            userTypeFeatureSupport
        )
        val pairFirstType = StandardTypeParameter(pairType, pairFirstTypeId)
        val pairSecondType = StandardTypeParameter(pairType, pairSecondTypeId)
        pairType.typeParams = listOf(pairFirstType, pairSecondType)
        val pairFirstField = FieldSymbol(pairType, pairFirstId, pairFirstType, mutable = false)
        val pairSecondField = FieldSymbol(pairType, pairSecondId, pairSecondType, mutable = false)
        pairType.fields = listOf(pairFirstField, pairSecondField)
        pairType.define(pairFirstId, pairFirstField)
        pairType.define(pairSecondId, pairSecondField)

        // Dictionary
        val dictionaryType = dictionaryCollectionType(architecture, langNS, booleanType, intType, pairType)

        // MutableDictionary
        val mutableDictionaryType =
            mutableDictionaryCollectionType(
                architecture,
                langNS,
                booleanType,
                intType,
                unitObject,
                pairType,
                dictionaryType
            )

        // Set
        val setType = setCollectionType(architecture, langNS, booleanType, intType)

        // MutableSet
        val mutableSetType = mutableSetCollectionType(architecture, langNS, booleanType, intType, unitObject, setType)

        // Option
        val optionType = createOptionType(langNS)

        // Either
        val eitherType = createEitherType(langNS)

        // Result
        val resultType = createResultType(langNS)

        // Static
        val rangePlugin = createRangePlugin(langNS, intType, listType)
        val randomPlugin = createRandomPlugin(langNS, constantOmicron)

        // Compose output
        langNS.define(unitId, unitObject)
        langNS.define(booleanId, booleanType)
        langNS.define(intId, intType)
        langNS.define(decimalId, decimalType)
        langNS.define(listId, listType)
        langNS.define(mutableListId, mutableListType)
        langNS.define(pairId, pairType)
        langNS.define(dictionaryId, dictionaryType)
        langNS.define(mutableDictionaryId, mutableDictionaryType)
        langNS.define(setId, setType)
        langNS.define(mutableSetId, mutableSetType)
        langNS.define(optionId, optionType)
        langNS.define(eitherId, eitherType)
        langNS.define(resultId, resultType)
        langNS.define(charId, charType)
        langNS.define(stringId, stringType)
        langNS.define(sByteId, sByteType)
        langNS.define(shortId, shortType)
        langNS.define(longId, longType)
        langNS.define(byteId, byteType)
        langNS.define(uShortId, uShortType)
        langNS.define(uIntId, uIntType)
        langNS.define(uLongId, uLongType)
        langNS.define(rangeId, rangePlugin)
        langNS.define(randomId, randomPlugin)
        shardNS.define(langId, langNS)

        root.define(shardId, shardNS)

        prelude.register(unitId, langNS)
        prelude.register(booleanId, langNS)
        prelude.register(intId, langNS)
        prelude.register(decimalId, langNS)
        prelude.register(listId, langNS)
        prelude.register(mutableListId, langNS)
        prelude.register(pairId, langNS)
        prelude.register(dictionaryId, langNS)
        prelude.register(mutableDictionaryId, langNS)
        prelude.register(setId, langNS)
        prelude.register(mutableSetId, langNS)
        prelude.register(optionId, langNS)
        prelude.register(someId, optionType)
        prelude.register(noneId, optionType)
        prelude.register(eitherId, langNS)
        prelude.register(rightId, eitherType)
        prelude.register(leftId, eitherType)
        prelude.register(resultId, langNS)
        prelude.register(successId, resultType)
        prelude.register(failureId, resultType)
        prelude.register(charId, langNS)
        prelude.register(stringId, langNS)
        prelude.register(sByteId, langNS)
        prelude.register(shortId, langNS)
        prelude.register(longId, langNS)
        prelude.register(byteId, langNS)
        prelude.register(uShortId, langNS)
        prelude.register(uIntId, langNS)
        prelude.register(uLongId, langNS)
        prelude.register(rangeId, langNS)
        prelude.register(randomId, langNS)
    }
}
