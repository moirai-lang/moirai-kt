package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*

object Lang {
    val prelude = SymbolTable(NullSymbolTable)

    val unitId = Identifier(NotInSource, "Unit")
    val booleanId = Identifier(NotInSource, "Boolean")
    val intId = Identifier(NotInSource, "Int")
    val charId = Identifier(NotInSource, "Char")
    val stringId = Identifier(NotInSource, "String")
    val stringTypeId = Identifier(NotInSource, "O")
    val stringInputTypeId = Identifier(NotInSource, "P")

    val decimalId = Identifier(NotInSource, "Decimal")
    val decimalTypeId = Identifier(NotInSource, "O")
    val decimalInputTypeId = Identifier(NotInSource, "P")

    val listId = Identifier(NotInSource, "List")
    val listElementTypeId = Identifier(NotInSource, "E")
    val listFinTypeId = Identifier(NotInSource, "O")
    val listInputFinTypeId = Identifier(NotInSource, "P")

    val mutableListId = Identifier(NotInSource, "MutableList")
    val mutableListElementTypeId = Identifier(NotInSource, "E")
    val mutableListFinTypeId = Identifier(NotInSource, "O")
    val mutableListInputFinTypeId = Identifier(NotInSource, "P")

    val pairId = Identifier(NotInSource, "Pair")
    private val pairFirstTypeId = Identifier(NotInSource, "A")
    private val pairSecondTypeId = Identifier(NotInSource, "B")
    val pairFirstId = Identifier(NotInSource, "first")
    val pairSecondId = Identifier(NotInSource, "second")

    val dictionaryId = Identifier(NotInSource, "Dictionary")
    val dictionaryKeyTypeId = Identifier(NotInSource, "K")
    val dictionaryValueTypeId = Identifier(NotInSource, "V")
    val dictionaryFinTypeId = Identifier(NotInSource, "O")
    val dictionaryInputFinTypeId = Identifier(NotInSource, "P")

    val mutableDictionaryId = Identifier(NotInSource, "MutableDictionary")
    val mutableDictionaryKeyTypeId = Identifier(NotInSource, "K")
    val mutableDictionaryValueTypeId = Identifier(NotInSource, "V")
    val mutableDictionaryFinTypeId = Identifier(NotInSource, "O")
    val mutableDictionaryInputFinTypeId = Identifier(NotInSource, "P")

    val setId = Identifier(NotInSource, "Set")
    val setElementTypeId = Identifier(NotInSource, "E")
    val setFinTypeId = Identifier(NotInSource, "O")
    val setInputFinTypeId = Identifier(NotInSource, "P")

    val mutableSetId = Identifier(NotInSource, "MutableSet")
    val mutableSetElementTypeId = Identifier(NotInSource, "E")
    val mutableSetFinTypeId = Identifier(NotInSource, "O")
    val mutableSetInputFinTypeId = Identifier(NotInSource, "P")

    val rangeId = Identifier(NotInSource, "range")
    val rangeTypeId = Identifier(NotInSource, "O")
    val randomId = Identifier(NotInSource, "random")
    val randomTypeId = Identifier(NotInSource, "A")

    const val INT_FIN: Long = (Int.MIN_VALUE.toString().length).toLong()
    val unitFin: Long = unitId.name.length.toLong()
    const val BOOL_FIN: Long = false.toString().length.toLong()
    const val CHAR_FIN: Long = 1L

    // Unit
    val unitObject = PlatformObjectSymbol(
        prelude,
        unitId,
        userTypeFeatureSupport
    )

    // Boolean
    val booleanType = BasicTypeSymbol(
        prelude,
        booleanId
    )

    // Integer
    val intType = BasicTypeSymbol(
        prelude,
        intId
    )

    // Char
    val charType = BasicTypeSymbol(
        prelude,
        charId
    )

    // Decimal
    val decimalType = decimalType(decimalId, booleanType, prelude)

    init {
        IntegerMathOpMembers.members().forEach { (name, plugin) ->
            intType.define(Identifier(NotInSource, name), plugin)
        }
        IntegerOrderOpMembers.members().forEach { (name, plugin) ->
            intType.define(Identifier(NotInSource, name), plugin)
        }
        IntegerEqualityOpMembers.members().forEach { (name, plugin) ->
            intType.define(Identifier(NotInSource, name), plugin)
        }

        BooleanEqualityOpMembers.members().forEach { (name, plugin) ->
            booleanType.define(Identifier(NotInSource, name), plugin)
        }
        ValueLogicalOpMembers.members().forEach { (name, plugin) ->
            booleanType.define(Identifier(NotInSource, name), plugin)
        }

        CharEqualityOpMembers.members().forEach { (name, plugin) ->
            charType.define(Identifier(NotInSource, name), plugin)
        }

        // List
        val listType = listCollectionType(prelude, intType, booleanType)

        // MutableList
        val mutableListType =
            mutableListCollectionType(prelude, intType, unitObject, booleanType, listType)

        // String
        val stringType = stringType(booleanType, intType, charType, listType, prelude)

        // ToString
        insertIntegerToStringMember(intType, stringType)
        insertUnitToStringMember(unitObject, stringType)
        insertBooleanToStringMember(booleanType, stringType)
        insertDecimalToStringMember(decimalType, stringType)
        insertCharToStringMember(charType, stringType)
        insertStringToStringMember(stringType)

        // Pair
        val pairType = ParameterizedRecordTypeSymbol(
            prelude,
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
        val dictionaryType = dictionaryCollectionType(prelude, booleanType, intType, pairType)

        // MutableDictionary
        val mutableDictionaryType =
            mutableDictionaryCollectionType(
                prelude,
                booleanType,
                intType,
                unitObject,
                pairType,
                dictionaryType
            )

        // Set
        val setType = setCollectionType(prelude, booleanType, intType)

        // MutableSet
        val mutableSetType = mutableSetCollectionType(prelude, booleanType, intType, unitObject, setType)

        // Static
        val rangePlugin = createRangePlugin(prelude, intType, listType)
        val randomPlugin = createRandomPlugin(prelude, ConstantFinTypeSymbol)

        // Compose output
        prelude.define(unitId, unitObject)
        prelude.define(booleanId, booleanType)
        prelude.define(intId, intType)
        prelude.define(decimalId, decimalType)
        prelude.define(listId, listType)
        prelude.define(mutableListId, mutableListType)
        prelude.define(pairId, pairType)
        prelude.define(dictionaryId, dictionaryType)
        prelude.define(mutableDictionaryId, mutableDictionaryType)
        prelude.define(setId, setType)
        prelude.define(mutableSetId, mutableSetType)
        prelude.define(charId, charType)
        prelude.define(stringId, stringType)
        prelude.define(rangeId, rangePlugin)
        prelude.define(randomId, randomPlugin)
    }
}
