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
    val decimalType = ParameterizedBasicTypeSymbol(
        prelude,
        decimalId,
        DecimalInstantiation(),
        userTypeFeatureSupport
    )
    val decimalTypeParam = ImmutableFinTypeParameter("${decimalId.name}.${decimalTypeId.name}", decimalTypeId)

    // List
    val listType = ParameterizedBasicTypeSymbol(
        prelude,
        listId,
        ListInstantiation(),
        immutableOrderedFeatureSupport
    )
    val listElementTypeParam = StandardTypeParameter("${listId.name}.${listElementTypeId.name}", listElementTypeId)
    val listFinTypeParam = ImmutableFinTypeParameter("${listId.name}.${listFinTypeId.name}", listFinTypeId)

    // MutableList
    val mutableListType = ParameterizedBasicTypeSymbol(
        prelude,
        mutableListId,
        MutableListInstantiation(),
        noFeatureSupport
    )
    val mutableListElementTypeParam = StandardTypeParameter("${mutableListId.name}.${mutableListElementTypeId.name}", mutableListElementTypeId)
    val mutableListFinTypeParam = MutableFinTypeParameter("${mutableListId.name}.${mutableListFinTypeId.name}", mutableListFinTypeId)

    // String
    val stringType = ParameterizedBasicTypeSymbol(
        prelude,
        stringId,
        StringInstantiation(),
        userTypeFeatureSupport
    )

    val stringTypeParam = ImmutableFinTypeParameter("${stringId.name}.${stringTypeId.name}", stringTypeId)

    // Pair
    val pairType = ParameterizedRecordTypeSymbol(
        prelude,
        pairId,
        userTypeFeatureSupport
    )
    val pairFirstType = StandardTypeParameter("${pairId.name}.${pairFirstTypeId.name}", pairFirstTypeId)
    val pairSecondType = StandardTypeParameter("${pairId.name}.${pairSecondTypeId.name}", pairSecondTypeId)

    val dictionaryType = ParameterizedBasicTypeSymbol(
        prelude,
        dictionaryId,
        DictionaryInstantiation(pairType),
        immutableUnorderedFeatureSupport
    )
    val dictionaryKeyTypeParam = StandardTypeParameter("${dictionaryId.name}.${dictionaryKeyTypeId.name}", dictionaryKeyTypeId)
    val dictionaryValueTypeParam = StandardTypeParameter("${dictionaryId.name}.${dictionaryValueTypeId.name}", dictionaryValueTypeId)
    val dictionaryFinTypeParam = ImmutableFinTypeParameter("${dictionaryId.name}.${dictionaryFinTypeId.name}", dictionaryFinTypeId)

    val mutableDictionaryType = ParameterizedBasicTypeSymbol(
        prelude,
        mutableDictionaryId,
        MutableDictionaryInstantiation(pairType),
        noFeatureSupport
    )
    val mutableDictionaryKeyTypeParam = StandardTypeParameter("${mutableDictionaryId.name}.${mutableDictionaryKeyTypeId.name}", mutableDictionaryKeyTypeId)
    val mutableDictionaryValueTypeParam = StandardTypeParameter("${mutableDictionaryId.name}.${mutableDictionaryValueTypeId.name}", mutableDictionaryValueTypeId)
    val mutableDictionaryFinTypeParam = MutableFinTypeParameter("${mutableDictionaryId.name}.${mutableDictionaryFinTypeId.name}", mutableDictionaryFinTypeId)

    val setType = ParameterizedBasicTypeSymbol(
        prelude,
        setId,
        SetInstantiation(),
        immutableUnorderedFeatureSupport
    )
    val setElementTypeParam = StandardTypeParameter("${setId.name}.${setElementTypeId.name}", setElementTypeId)
    val setFinTypeParam = ImmutableFinTypeParameter("${setId.name}.${setFinTypeId.name}", setFinTypeId)

    val mutableSetType = ParameterizedBasicTypeSymbol(
        prelude,
        mutableSetId,
        MutableSetInstantiation(),
        noFeatureSupport
    )
    val mutableSetElementTypeParam = StandardTypeParameter("${mutableSetId.name}.${mutableSetElementTypeId.name}", mutableSetElementTypeId)
    val mutableSetFinTypeParam = MutableFinTypeParameter("${mutableSetId.name}.${mutableSetFinTypeId.name}", mutableSetFinTypeId)

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

        decimalType.define(decimalTypeId, decimalTypeParam)
        decimalType.typeParams = listOf(decimalTypeParam)
        decimalType.modeSelector = { _ ->
            ImmutableBasicTypeMode
        }
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
        listType.define(listElementTypeId, listElementTypeParam)
        listType.define(listFinTypeId, listFinTypeParam)
        listType.typeParams = listOf(listElementTypeParam, listFinTypeParam)
        listType.modeSelector = { _ ->
            ImmutableBasicTypeMode
        }
        
        ListTypes.listCollectionType()
        
        // MutableList
        mutableListType.define(mutableListElementTypeId, mutableListElementTypeParam)
        mutableListType.define(mutableListFinTypeId, mutableListFinTypeParam)
        mutableListType.typeParams = listOf(mutableListElementTypeParam, mutableListFinTypeParam)
        mutableListType.modeSelector = { args ->
            when (val fin = args[1]) {
                is FinTypeSymbol -> {
                    MutableBasicTypeMode(fin.magnitude)
                }

                else -> {
                    ImmutableBasicTypeMode
                }
            }
        }
        
        ListTypes.mutableListCollectionType()

        // Dictionary
        dictionaryType.define(dictionaryKeyTypeId, dictionaryKeyTypeParam)
        dictionaryType.define(dictionaryValueTypeId, dictionaryValueTypeParam)
        dictionaryType.define(dictionaryFinTypeId, dictionaryFinTypeParam)
        dictionaryType.typeParams =
            listOf(dictionaryKeyTypeParam, dictionaryValueTypeParam, dictionaryFinTypeParam)
        dictionaryType.modeSelector = { _ ->
            ImmutableBasicTypeMode
        }

        DictionaryTypes.dictionaryCollectionType()

        // MutableDictionary
        mutableDictionaryType.define(mutableDictionaryKeyTypeId, mutableDictionaryKeyTypeParam)
        mutableDictionaryType.define(mutableDictionaryValueTypeId, mutableDictionaryValueTypeParam)
        mutableDictionaryType.define(mutableDictionaryFinTypeId, mutableDictionaryFinTypeParam)
        mutableDictionaryType.typeParams =
            listOf(
                mutableDictionaryKeyTypeParam,
                mutableDictionaryValueTypeParam,
                mutableDictionaryFinTypeParam
            )
        mutableDictionaryType.modeSelector = { args ->
            when (val fin = args[2]) {
                is FinTypeSymbol -> {
                    MutableBasicTypeMode(fin.magnitude)
                }

                else -> {
                    ImmutableBasicTypeMode
                }
            }
        }

        DictionaryTypes.mutableDictionaryCollectionType()

        // Set
        setType.define(setElementTypeId, setElementTypeParam)
        setType.define(setFinTypeId, setFinTypeParam)
        setType.typeParams = listOf(setElementTypeParam, setFinTypeParam)
        setType.modeSelector = { _ ->
            ImmutableBasicTypeMode
        }
        
        SetTypes.setCollectionType()

        // MutableSet
        mutableSetType.define(mutableSetElementTypeId, mutableSetElementTypeParam)
        mutableSetType.define(mutableSetFinTypeId, mutableSetFinTypeParam)
        mutableSetType.typeParams = listOf(mutableSetElementTypeParam, mutableSetFinTypeParam)
        mutableSetType.modeSelector = { args ->
            when (val fin = args[1]) {
                is FinTypeSymbol -> {
                    MutableBasicTypeMode(fin.magnitude)
                }

                else -> {
                    ImmutableBasicTypeMode
                }
            }
        }
        
        SetTypes.mutableSetCollectionType()

        StringTypes.stringType()

        pairType.typeParams = listOf(pairFirstType, pairSecondType)
        val pairFirstField = FieldSymbol(pairType, pairFirstId, pairFirstType, mutable = false)
        val pairSecondField = FieldSymbol(pairType, pairSecondId, pairSecondType, mutable = false)
        pairType.fields = listOf(pairFirstField, pairSecondField)
        pairType.define(pairFirstId, pairFirstField)
        pairType.define(pairSecondId, pairSecondField)

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
        prelude.define(rangeId, StaticPlugins.rangePlugin)
        prelude.define(randomId, StaticPlugins.randomPlugin)
    }
}
