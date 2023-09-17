package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*

object Lang {
    val shardId = Identifier("shard")
    val langId = Identifier("lang")
    val unitId = Identifier("Unit")
    val booleanId = Identifier("Boolean")
    val intId = Identifier("Int")
    val charId = Identifier("Char")
    val stringId = Identifier("String")
    val stringTypeId = Identifier("O")
    val stringInputTypeId = Identifier("P")

    val decimalId = Identifier("Decimal")
    val decimalTypeId = Identifier("O")
    val decimalInputTypeId = Identifier("P")

    val listId = Identifier("List")
    val listElementTypeId = Identifier("E")
    val listFinTypeId = Identifier("O")
    val listInputFinTypeId = Identifier("P")

    val mutableListId = Identifier("MutableList")
    val mutableListElementTypeId = Identifier("E")
    val mutableListFinTypeId = Identifier("O")
    val mutableListInputFinTypeId = Identifier("P")

    val pairId = Identifier("Pair")
    private val pairFirstTypeId = Identifier("A")
    private val pairSecondTypeId = Identifier("B")
    val pairFirstId = Identifier("first")
    val pairSecondId = Identifier("second")

    val dictionaryId = Identifier("Dictionary")
    val dictionaryKeyTypeId = Identifier("K")
    val dictionaryValueTypeId = Identifier("V")
    val dictionaryFinTypeId = Identifier("O")
    val dictionaryInputFinTypeId = Identifier("P")

    val mutableDictionaryId = Identifier("MutableDictionary")
    val mutableDictionaryKeyTypeId = Identifier("K")
    val mutableDictionaryValueTypeId = Identifier("V")
    val mutableDictionaryFinTypeId = Identifier("O")
    val mutableDictionaryInputFinTypeId = Identifier("P")

    val setId = Identifier("Set")
    val setElementTypeId = Identifier("E")
    val setFinTypeId = Identifier("O")
    val setInputFinTypeId = Identifier("P")

    val mutableSetId = Identifier("MutableSet")
    val mutableSetElementTypeId = Identifier("E")
    val mutableSetFinTypeId = Identifier("O")
    val mutableSetInputFinTypeId = Identifier("P")

    val rangeId = Identifier("range")
    val rangeTypeId = Identifier("O")
    val randomId = Identifier("random")
    val randomTypeId = Identifier("A")

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
            booleanType.define(Identifier(name), plugin)
        }

        ValueLogicalOpMembers.members(booleanType, constantFin).forEach { (name, plugin) ->
            booleanType.define(Identifier(name), plugin)
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
