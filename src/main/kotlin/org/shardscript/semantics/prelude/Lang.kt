package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.*

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
    private val decimalTypeId = Identifier(NotInSource, "O")
    val decimalInputTypeId = Identifier(NotInSource, "P")

    val listId = Identifier(NotInSource, "List")
    private val listElementTypeId = Identifier(NotInSource, "E")
    private val listFinTypeId = Identifier(NotInSource, "O")
    val listInputFinTypeId = Identifier(NotInSource, "P")

    val mutableListId = Identifier(NotInSource, "MutableList")
    private val mutableListElementTypeId = Identifier(NotInSource, "E")
    private val mutableListFinTypeId = Identifier(NotInSource, "O")
    val mutableListInputFinTypeId = Identifier(NotInSource, "P")

    val pairId = Identifier(NotInSource, "Pair")
    private val pairFirstTypeId = Identifier(NotInSource, "A")
    private val pairSecondTypeId = Identifier(NotInSource, "B")
    val pairFirstId = Identifier(NotInSource, "first")
    val pairSecondId = Identifier(NotInSource, "second")

    val dictionaryId = Identifier(NotInSource, "Dictionary")
    private val dictionaryKeyTypeId = Identifier(NotInSource, "K")
    private val dictionaryValueTypeId = Identifier(NotInSource, "V")
    private val dictionaryFinTypeId = Identifier(NotInSource, "O")
    val dictionaryInputFinTypeId = Identifier(NotInSource, "P")

    val mutableDictionaryId = Identifier(NotInSource, "MutableDictionary")
    private val mutableDictionaryKeyTypeId = Identifier(NotInSource, "K")
    private val mutableDictionaryValueTypeId = Identifier(NotInSource, "V")
    private val mutableDictionaryFinTypeId = Identifier(NotInSource, "O")
    val mutableDictionaryInputFinTypeId = Identifier(NotInSource, "P")

    val setId = Identifier(NotInSource, "Set")
    private val setElementTypeId = Identifier(NotInSource, "E")
    private val setFinTypeId = Identifier(NotInSource, "O")
    val setInputFinTypeId = Identifier(NotInSource, "P")

    val mutableSetId = Identifier(NotInSource, "MutableSet")
    private val mutableSetElementTypeId = Identifier(NotInSource, "E")
    private val mutableSetFinTypeId = Identifier(NotInSource, "O")
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
    val unitObject = PlatformObjectType(
        unitId
    )

    // Boolean
    val booleanType = BasicType(
        booleanId
    )

    // Integer
    val intType = BasicType(
        intId
    )

    // Char
    val charType = BasicType(
        charId
    )

    // Decimal
    val decimalType = ParameterizedBasicType(
        decimalId,
        DecimalInstantiation(),
        userTypeFeatureSupport
    )
    val decimalTypeParam = FinTypeParameter("${decimalId.name}.${decimalTypeId.name}", decimalTypeId)

    // List
    val listType = ParameterizedBasicType(
        listId,
        ListInstantiation(),
        immutableOrderedFeatureSupport
    )
    val listElementTypeParam = StandardTypeParameter("${listId.name}.${listElementTypeId.name}", listElementTypeId)
    val listFinTypeParam = FinTypeParameter("${listId.name}.${listFinTypeId.name}", listFinTypeId)

    // MutableList
    val mutableListType = ParameterizedBasicType(
        mutableListId,
        MutableListInstantiation(),
        immutableOrderedFeatureSupport
    )
    val mutableListElementTypeParam = StandardTypeParameter("${mutableListId.name}.${mutableListElementTypeId.name}", mutableListElementTypeId)
    val mutableListFinTypeParam = FinTypeParameter("${mutableListId.name}.${mutableListFinTypeId.name}", mutableListFinTypeId)

    // String
    val stringType = ParameterizedBasicType(
        stringId,
        StringInstantiation(),
        userTypeFeatureSupport
    )

    val stringTypeParam = FinTypeParameter("${stringId.name}.${stringTypeId.name}", stringTypeId)

    // Pair
    private val pairType = ParameterizedRecordType(
        prelude,
        pairId.name,
        pairId,
        userTypeFeatureSupport
    )
    private val pairFirstType = StandardTypeParameter("${pairId.name}.${pairFirstTypeId.name}", pairFirstTypeId)
    private val pairSecondType = StandardTypeParameter("${pairId.name}.${pairSecondTypeId.name}", pairSecondTypeId)

    val dictionaryType = ParameterizedBasicType(
        dictionaryId,
        DictionaryInstantiation(pairType),
        immutableUnorderedFeatureSupport
    )
    val dictionaryKeyTypeParam = StandardTypeParameter("${dictionaryId.name}.${dictionaryKeyTypeId.name}", dictionaryKeyTypeId)
    val dictionaryValueTypeParam = StandardTypeParameter("${dictionaryId.name}.${dictionaryValueTypeId.name}", dictionaryValueTypeId)
    val dictionaryFinTypeParam = FinTypeParameter("${dictionaryId.name}.${dictionaryFinTypeId.name}", dictionaryFinTypeId)

    val mutableDictionaryType = ParameterizedBasicType(
        mutableDictionaryId,
        MutableDictionaryInstantiation(pairType),
        immutableUnorderedFeatureSupport
    )
    val mutableDictionaryKeyTypeParam = StandardTypeParameter("${mutableDictionaryId.name}.${mutableDictionaryKeyTypeId.name}", mutableDictionaryKeyTypeId)
    val mutableDictionaryValueTypeParam = StandardTypeParameter("${mutableDictionaryId.name}.${mutableDictionaryValueTypeId.name}", mutableDictionaryValueTypeId)
    val mutableDictionaryFinTypeParam = FinTypeParameter("${mutableDictionaryId.name}.${mutableDictionaryFinTypeId.name}", mutableDictionaryFinTypeId)

    val setType = ParameterizedBasicType(
        setId,
        SetInstantiation(),
        immutableUnorderedFeatureSupport
    )
    val setElementTypeParam = StandardTypeParameter("${setId.name}.${setElementTypeId.name}", setElementTypeId)
    val setFinTypeParam = FinTypeParameter("${setId.name}.${setFinTypeId.name}", setFinTypeId)

    val mutableSetType = ParameterizedBasicType(
        mutableSetId,
        MutableSetInstantiation(),
        immutableUnorderedFeatureSupport
    )
    val mutableSetElementTypeParam = StandardTypeParameter("${mutableSetId.name}.${mutableSetElementTypeId.name}", mutableSetElementTypeId)
    val mutableSetFinTypeParam = FinTypeParameter("${mutableSetId.name}.${mutableSetFinTypeId.name}", mutableSetFinTypeId)

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

        decimalType.defineType(decimalTypeId, decimalTypeParam)
        decimalType.typeParams = listOf(decimalTypeParam)
        decimalType.fields = listOf()

        DecimalMathOpMembers.members().forEach { (name, plugin) ->
            decimalType.define(Identifier(NotInSource, name), plugin)
        }
        DecimalOrderOpMembers.members().forEach { (name, plugin) ->
            decimalType.define(Identifier(NotInSource, name), plugin)
        }
        DecimalEqualityOpMembers.members().forEach { (name, plugin) ->
            decimalType.define(Identifier(NotInSource, name), plugin)
        }

        // List
        listType.defineType(listElementTypeId, listElementTypeParam)
        listType.defineType(listFinTypeId, listFinTypeParam)
        listType.typeParams = listOf(listElementTypeParam, listFinTypeParam)
        
        ListTypes.listCollectionType()
        
        // MutableList
        mutableListType.defineType(mutableListElementTypeId, mutableListElementTypeParam)
        mutableListType.defineType(mutableListFinTypeId, mutableListFinTypeParam)
        mutableListType.typeParams = listOf(mutableListElementTypeParam, mutableListFinTypeParam)
        
        ListTypes.mutableListCollectionType()

        // Dictionary
        dictionaryType.defineType(dictionaryKeyTypeId, dictionaryKeyTypeParam)
        dictionaryType.defineType(dictionaryValueTypeId, dictionaryValueTypeParam)
        dictionaryType.defineType(dictionaryFinTypeId, dictionaryFinTypeParam)
        dictionaryType.typeParams =
            listOf(dictionaryKeyTypeParam, dictionaryValueTypeParam, dictionaryFinTypeParam)

        DictionaryTypes.dictionaryCollectionType()

        // MutableDictionary
        mutableDictionaryType.defineType(mutableDictionaryKeyTypeId, mutableDictionaryKeyTypeParam)
        mutableDictionaryType.defineType(mutableDictionaryValueTypeId, mutableDictionaryValueTypeParam)
        mutableDictionaryType.defineType(mutableDictionaryFinTypeId, mutableDictionaryFinTypeParam)
        mutableDictionaryType.typeParams =
            listOf(
                mutableDictionaryKeyTypeParam,
                mutableDictionaryValueTypeParam,
                mutableDictionaryFinTypeParam
            )

        DictionaryTypes.mutableDictionaryCollectionType()

        // Set
        setType.defineType(setElementTypeId, setElementTypeParam)
        setType.defineType(setFinTypeId, setFinTypeParam)
        setType.typeParams = listOf(setElementTypeParam, setFinTypeParam)
        
        SetTypes.setCollectionType()

        // MutableSet
        mutableSetType.defineType(mutableSetElementTypeId, mutableSetElementTypeParam)
        mutableSetType.defineType(mutableSetFinTypeId, mutableSetFinTypeParam)
        mutableSetType.typeParams = listOf(mutableSetElementTypeParam, mutableSetFinTypeParam)
        
        SetTypes.mutableSetCollectionType()

        StringTypes.stringType()

        pairType.typeParams = listOf(pairFirstType, pairSecondType)
        val pairFirstField = FieldSymbol(pairType, pairFirstId, pairFirstType, mutable = false)
        val pairSecondField = FieldSymbol(pairType, pairSecondId, pairSecondType, mutable = false)
        pairType.fields = listOf(pairFirstField, pairSecondField)
        pairType.define(pairFirstId, pairFirstField)
        pairType.define(pairSecondId, pairSecondField)

        // Compose output
        prelude.defineType(unitId, unitObject)
        prelude.defineType(booleanId, booleanType)
        prelude.defineType(intId, intType)
        prelude.defineType(decimalId, decimalType)
        prelude.defineType(listId, listType)
        prelude.defineType(mutableListId, mutableListType)
        prelude.defineType(pairId, pairType)
        prelude.defineType(dictionaryId, dictionaryType)
        prelude.defineType(mutableDictionaryId, mutableDictionaryType)
        prelude.defineType(setId, setType)
        prelude.defineType(mutableSetId, mutableSetType)
        prelude.defineType(charId, charType)
        prelude.defineType(stringId, stringType)
        prelude.define(rangeId, StaticPlugins.rangePlugin)
        prelude.define(randomId, StaticPlugins.randomPlugin)
    }
}
