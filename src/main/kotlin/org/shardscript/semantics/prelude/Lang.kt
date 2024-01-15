package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*

object Lang {
    val shardId = Identifier(NotInSource, "shard")
    val langId = Identifier(NotInSource, "lang")
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

    fun isUnitExactly(type: Type): Boolean =
        when (generatePath(type as Symbol)) {
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
            booleanType.define(Identifier(NotInSource, name), plugin)
        }

        ValueLogicalOpMembers.members(booleanType, constantFin).forEach { (name, plugin) ->
            booleanType.define(Identifier(NotInSource, name), plugin)
        }

        // Integer
        val intType = intType(architecture, intId, booleanType, langNS, setOf())

        // Decimal
        val decimalType = decimalType(decimalId, booleanType, langNS)

        // Char
        val charType = BasicTypeSymbol(
            langNS,
            charId
        )
        ValueEqualityOpMembers.members(charType, constantFin, charType).forEach { (name, plugin) ->
            charType.define(Identifier(NotInSource, name), plugin)
        }

        // List
        val listType = listCollectionType(architecture, langNS, intType, booleanType)

        // MutableList
        val mutableListType =
            mutableListCollectionType(architecture, langNS, intType, unitObject, booleanType, listType)

        // String
        val stringType = stringType(booleanType, intType, charType, listType, langNS)

        // ToString
        insertIntegerToStringMember(intType, stringType)
        insertUnitToStringMember(unitObject, stringType)
        insertBooleanToStringMember(booleanType, stringType)
        insertDecimalToStringMember(decimalType, stringType)
        insertCharToStringMember(charType, stringType)
        insertStringToStringMember(stringType)

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
        prelude.register(rangeId, langNS)
        prelude.register(randomId, langNS)
    }
}
