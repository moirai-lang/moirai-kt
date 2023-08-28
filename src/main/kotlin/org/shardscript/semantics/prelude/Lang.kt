package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*

object Lang {
    val shardId = Identifier("shard")
    val langId = Identifier("lang")
    val unitId = Identifier("Unit")
    val booleanId = Identifier("Boolean")

    val sByteId = Identifier("SByte")
    const val sByteSuffix = "s8"
    val shortId = Identifier("Short")
    const val shortSuffix = "s16"
    val intId = Identifier("Int")
    val longId = Identifier("Long")
    const val longSuffix = "s64"

    val byteId = Identifier("Byte")
    const val byteSuffix = "u8"
    val uShortId = Identifier("UShort")
    const val uShortSuffix = "u16"
    val uIntId = Identifier("UInt")
    const val uIntSuffix = "u32"
    val uLongId = Identifier("ULong")
    const val uLongSuffix = "u64"

    val charId = Identifier("Char")
    val stringId = Identifier("String")
    val stringTypeId = Identifier("O: Fin")
    val stringInputTypeId = Identifier("P: Fin")

    val decimalId = Identifier("Decimal")
    val decimalTypeId = Identifier("O: Fin")
    val decimalInputTypeId = Identifier("P: Fin")

    val listId = Identifier("List")
    val listElementTypeId = Identifier("E")
    val listFinTypeId = Identifier("O: Fin")
    val listInputFinTypeId = Identifier("P: Fin")

    val mutableListId = Identifier("MutableList")
    val mutableListElementTypeId = Identifier("E")
    val mutableListFinTypeId = Identifier("O: Fin")
    val mutableListInputFinTypeId = Identifier("P: Fin")

    val pairId = Identifier("Pair")
    val pairFirstTypeId = Identifier("A")
    val pairSecondTypeId = Identifier("B")
    val pairFirstId = Identifier("first")
    val pairSecondId = Identifier("second")

    val dictionaryId = Identifier("Dictionary")
    val dictionaryKeyTypeId = Identifier("K")
    val dictionaryValueTypeId = Identifier("V")
    val dictionaryFinTypeId = Identifier("O: Fin")
    val dictionaryInputFinTypeId = Identifier("P: Fin")

    val mutableDictionaryId = Identifier("MutableDictionary")
    val mutableDictionaryKeyTypeId = Identifier("K")
    val mutableDictionaryValueTypeId = Identifier("V")
    val mutableDictionaryFinTypeId = Identifier("O: Fin")
    val mutableDictionaryInputFinTypeId = Identifier("P: Fin")

    val setId = Identifier("Set")
    val setElementTypeId = Identifier("E")
    val setFinTypeId = Identifier("O: Fin")
    val setInputFinTypeId = Identifier("P: Fin")

    val mutableSetId = Identifier("MutableSet")
    val mutableSetElementTypeId = Identifier("E")
    val mutableSetFinTypeId = Identifier("O: Fin")
    val mutableSetInputFinTypeId = Identifier("P: Fin")

    val rangeId = Identifier("range")
    val rangeTypeId = Identifier("O: Fin")
    val randomId = Identifier("random")
    val randomTypeId = Identifier("A")

    val sByteFin: Long = (Byte.MIN_VALUE.toString().length + sByteSuffix.length).toLong()
    val shortFin: Long = (Short.MIN_VALUE.toString().length + shortSuffix.length).toLong()
    val intFin: Long = (Int.MIN_VALUE.toString().length).toLong()
    val longFin: Long = (Long.MIN_VALUE.toString().length + longSuffix.length).toLong()
    val byteFin: Long = (UByte.MIN_VALUE.toString().length + byteSuffix.length).toLong()
    val uShortFin: Long = (UShort.MIN_VALUE.toString().length + uShortSuffix.length).toLong()
    val uIntFin: Long = (UInt.MIN_VALUE.toString().length + uIntSuffix.length).toLong()
    val uLongFin: Long = (ULong.MIN_VALUE.toString().length + uLongSuffix.length).toLong()
    val unitFin: Long = unitId.name.length.toLong()
    val booleanFin: Long = false.toString().length.toLong()
    val charFin: Long = 1L

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
        val constantFin = FinTypeSymbol(architecture.defaultNodeCost)
        ValueEqualityOpMembers.members(booleanType, constantFin, booleanType).forEach { (name, plugin) ->
            booleanType.define(Identifier(name), plugin)
        }

        ValueLogicalOpMembers.members(booleanType, constantFin).forEach { (name, plugin) ->
            booleanType.define(Identifier(name), plugin)
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
        ValueEqualityOpMembers.members(charType, constantFin, charType).forEach { (name, plugin) ->
            charType.define(Identifier(name), plugin)
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
            sByteType.identifier to sByteType,
            shortType.identifier to shortType,
            intType.identifier to intType,
            longType.identifier to longType,
            byteType.identifier to byteType,
            uShortType.identifier to uShortType,
            uIntType.identifier to uIntType,
            uLongType.identifier to uLongType
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

        // Static
        val rangePlugin = createRangePlugin(langNS, intType, listType)
        val randomPlugin = createRandomPlugin(langNS, constantFin)

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
