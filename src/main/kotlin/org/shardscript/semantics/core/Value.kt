package org.shardscript.semantics.core

import org.shardscript.semantics.prelude.*
import java.math.BigDecimal

sealed class Value

data object UnitValue : Value() {
    fun evalToString(): Value {
        return StringValue(Lang.unitId.name)
    }
}

data class GroundMemberPlugin(private val plugin: (Value, List<Value>) -> Value) {
    fun invoke(t: Value, args: List<Value>): Value = plugin(t, args)
}

object Plugins {
    val groundMemberPlugins: Map<GroundMemberPluginSymbol, GroundMemberPlugin> = mapOf(
        IntegerMathOpMembers.add to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalAdd(args.first())
        },
        IntegerMathOpMembers.sub to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalSub(args.first())
        },
        IntegerMathOpMembers.mul to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalMul(args.first())
        },
        IntegerMathOpMembers.div to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalDiv(args.first())
        },
        IntegerMathOpMembers.mod to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalMod(args.first())
        },
        IntegerMathOpMembers.negate to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as MathValue).evalNegate()
        },
        IntegerOrderOpMembers.greaterThan to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThan(args.first())
        },
        IntegerOrderOpMembers.greaterThanOrEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThanOrEquals(args.first())
        },
        IntegerOrderOpMembers.lessThan to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThan(args.first())
        },
        IntegerOrderOpMembers.lessThanOrEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThanOrEquals(args.first())
        },
        IntegerEqualityOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        IntegerEqualityOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        BooleanEqualityOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        BooleanEqualityOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        CharEqualityOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        CharEqualityOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        ValueLogicalOpMembers.and to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as LogicalValue).evalAnd(args.first())
        },
        ValueLogicalOpMembers.or to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as LogicalValue).evalOr(args.first())
        },
        ValueLogicalOpMembers.not to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as LogicalValue).evalNot()
        },
        ListTypes.removeAtFunction to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as ListValue).evalRemoveAt(args.first())
        },
        ToStringMembers.integerToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as IntValue).evalToString()
        },
        ToStringMembers.unitToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as UnitValue).evalToString()
        },
        ToStringMembers.booleanToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as BooleanValue).evalToString()
        },
        ToStringMembers.charToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as CharValue).evalToString()
        }
    )

    val parameterizedMemberPlugins: Map<ParameterizedMemberPluginSymbol, GroundMemberPlugin> = mapOf(
        ToStringMembers.decimalToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as DecimalValue).evalToString()
        },
        ToStringMembers.stringToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as StringValue).evalToString()
        },
        StringOpMembers.toCharArray to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as StringValue).evalToCharArray()
        },
        StringOpMembers.add to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as StringValue).evalAdd(args.first())
        },
        StringOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        StringOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        DecimalMathOpMembers.add to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalAdd(args.first())
        },
        DecimalMathOpMembers.sub to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalSub(args.first())
        },
        DecimalMathOpMembers.mul to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalMul(args.first())
        },
        DecimalMathOpMembers.div to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalDiv(args.first())
        },
        DecimalMathOpMembers.mod to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as MathValue).evalMod(args.first())
        },
        DecimalMathOpMembers.negate to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as MathValue).evalNegate()
        },
        DecimalOrderOpMembers.greaterThan to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThan(args.first())
        },
        DecimalOrderOpMembers.greaterThanOrEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as OrderValue).evalGreaterThanOrEquals(args.first())
        },
        DecimalOrderOpMembers.lessThan to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThan(args.first())
        },
        DecimalOrderOpMembers.lessThanOrEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as OrderValue).evalLessThanOrEquals(args.first())
        },
        DecimalEqualityOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        DecimalEqualityOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.listEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.listNotEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.mutableListEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.mutableListNotEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.dictionaryEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.dictionaryNotEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.mutableDictionaryEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.mutableDictionaryNotEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.setEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.setNotEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.mutableSetEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.mutableSetNotEquals to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        }
    )
}

class ObjectValue(
    val symbol: ObjectSymbol
) : Value() {
    val path = generatePath(symbol)

    override fun equals(other: Any?): Boolean {
        if (other != null && other is ObjectValue) {
            return path == other.path
        }
        return false
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}

data class PluginValue(
    val plugin: (List<Value>) -> Value
) : Value() {
    fun invoke(args: List<Value>): Value {
        return plugin(args)
    }
}

data class FunctionValue(
    val formalParams: List<FunctionFormalParameterSymbol>,
    val body: Ast
) : Value() {
    fun invoke(args: List<Value>, globalScope: ValueTable, evalCallback: (Ast, ValueTable) -> Value): Value {
        val functionScope = ValueTable(globalScope)
        formalParams.zip(args).forEach {
            functionScope.define(it.first.identifier, it.second)
        }
        return evalCallback(body, functionScope)
    }
}

class RecordValue(val symbol: Symbol, val fields: ValueTable) : Value() {
    lateinit var scope: Scope<Symbol>
    val path = generatePath(symbol)

    override fun equals(other: Any?): Boolean {
        if (other != null && other is RecordValue) {
            if (path == other.path) {
                val valuesHere = fields.valuesHere()
                val otherValues = other.fields.valuesHere()
                return valuesHere.keys.all {
                    valuesHere[it]!! == otherValues[it]!!
                }
            }
        }
        return false
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}

data class RecordConstructorValue(val prelude: Scope<Symbol>, val symbol: Symbol) : Value() {
    fun apply(args: List<Value>): RecordValue {
        return when (symbol) {
            is GroundRecordTypeSymbol -> {
                val fields = ValueTable(
                    SymbolRouterValueTable(prelude, symbol)
                )
                symbol.fields.zip(args).forEach {
                    fields.define(it.first.identifier, it.second)
                }
                val res = RecordValue(symbol, fields)
                res.scope = symbol
                res
            }
            is ParameterizedRecordTypeSymbol -> {
                val fields = ValueTable(
                    SymbolRouterValueTable(prelude, symbol)
                )
                symbol.fields.zip(args).forEach {
                    fields.define(it.first.identifier, it.second)
                }
                val res = RecordValue(symbol, fields)
                res.scope = symbol
                res
            }
            else -> langThrow(NotInSource, TypeSystemBug)
        }
    }
}

data class ListConstructorValue(val modeSelector: (List<Type>) -> BasicTypeMode) :
    Value() {
    fun apply(typeArgs: List<Type>, elements: List<Value>): ListValue {
        return ListValue(modeSelector(typeArgs), elements.toMutableList())
    }
}

data class SetConstructorValue(val modeSelector: (List<Type>) -> BasicTypeMode) :
    Value() {
    fun apply(typeArgs: List<Type>, elements: List<Value>): SetValue {
        return SetValue(modeSelector(typeArgs), elements.toMutableSet())
    }
}

data class DictionaryConstructorValue(val modeSelector: (List<Type>) -> BasicTypeMode) :
    Value() {
    fun apply(typeArgs: List<Type>, elements: List<Value>): DictionaryValue {
        val pairs = elements.map {
            it as RecordValue
        }.map {
            Pair(
                it.fields.fetchHere(Lang.pairFirstId),
                it.fields.fetchHere(Lang.pairSecondId)
            )
        }
        return DictionaryValue(modeSelector(typeArgs), pairs.toMap().toMutableMap())
    }
}

interface MathValue {
    fun evalAdd(other: Value): Value
    fun evalSub(other: Value): Value
    fun evalMul(other: Value): Value
    fun evalDiv(other: Value): Value
    fun evalMod(other: Value): Value
    fun evalNegate(): Value
}

interface OrderValue {
    fun evalGreaterThan(other: Value): Value
    fun evalGreaterThanOrEquals(other: Value): Value
    fun evalLessThan(other: Value): Value
    fun evalLessThanOrEquals(other: Value): Value
}

interface EqualityValue {
    fun evalEquals(other: Value): Value
    fun evalNotEquals(other: Value): Value
}

interface LogicalValue {
    fun evalAnd(other: Value): Value
    fun evalOr(other: Value): Value
    fun evalNot(): Value
}

data class BooleanValue(val canonicalForm: Boolean) : Value(), EqualityValue, LogicalValue {
    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as BooleanValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value =
        BooleanValue(canonicalForm != (other as BooleanValue).canonicalForm)

    override fun evalAnd(other: Value): Value = BooleanValue(canonicalForm && (other as BooleanValue).canonicalForm)
    override fun evalOr(other: Value): Value = BooleanValue(canonicalForm || (other as BooleanValue).canonicalForm)
    override fun evalNot(): Value = BooleanValue(!canonicalForm)

    fun evalToString(): Value = StringValue(canonicalForm.toString())
}

data class IntValue(val canonicalForm: Int) : Value(), MathValue, OrderValue, EqualityValue {
    override fun evalAdd(other: Value): Value = IntValue(canonicalForm + (other as IntValue).canonicalForm)
    override fun evalSub(other: Value): Value = IntValue(canonicalForm - (other as IntValue).canonicalForm)
    override fun evalMul(other: Value): Value = IntValue(canonicalForm * (other as IntValue).canonicalForm)
    override fun evalDiv(other: Value): Value = IntValue(canonicalForm / (other as IntValue).canonicalForm)
    override fun evalMod(other: Value): Value = IntValue(canonicalForm % (other as IntValue).canonicalForm)
    override fun evalGreaterThan(other: Value): Value = BooleanValue(canonicalForm > (other as IntValue).canonicalForm)
    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as IntValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as IntValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as IntValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as IntValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value = BooleanValue(canonicalForm != (other as IntValue).canonicalForm)
    override fun evalNegate(): Value = IntValue(-canonicalForm)

    fun evalToString(): Value = StringValue(canonicalForm.toString())
}

data class DecimalValue(val canonicalForm: BigDecimal) : Value(), MathValue, OrderValue, EqualityValue {
    override fun evalAdd(other: Value): Value =
        DecimalValue(canonicalForm + (other as DecimalValue).canonicalForm)

    override fun evalSub(other: Value): Value =
        DecimalValue(canonicalForm - (other as DecimalValue).canonicalForm)

    override fun evalMul(other: Value): Value =
        DecimalValue(canonicalForm * (other as DecimalValue).canonicalForm)

    override fun evalDiv(other: Value): Value =
        DecimalValue(canonicalForm / (other as DecimalValue).canonicalForm)

    override fun evalMod(other: Value): Value =
        DecimalValue(canonicalForm % (other as DecimalValue).canonicalForm)

    override fun evalGreaterThan(other: Value): Value =
        BooleanValue(canonicalForm > (other as DecimalValue).canonicalForm)

    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as DecimalValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as DecimalValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as DecimalValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as DecimalValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value =
        BooleanValue(canonicalForm != (other as DecimalValue).canonicalForm)

    override fun evalNegate(): Value = DecimalValue(canonicalForm.negate())

    fun evalToString(): Value = StringValue(canonicalForm.stripTrailingZeros().toPlainString())
}

data class CharValue(val canonicalForm: Char) : Value(), EqualityValue {
    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as CharValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value =
        BooleanValue(canonicalForm != (other as CharValue).canonicalForm)

    fun evalToString(): Value = StringValue(canonicalForm.toString())
}

data class StringValue(val canonicalForm: String) : Value(), EqualityValue {
    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as StringValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value =
        BooleanValue(canonicalForm != (other as StringValue).canonicalForm)

    fun fieldSize(): Value {
        return IntValue(canonicalForm.length)
    }

    fun evalToCharArray(): Value {
        val elements = canonicalForm.toCharArray().map { CharValue(it) }
        return ListValue(ImmutableBasicTypeMode, elements.toMutableList())
    }

    fun evalToString(): Value = StringValue(canonicalForm)

    fun evalAdd(arg: Value): Value {
        val other = arg as StringValue
        return StringValue(canonicalForm + other.canonicalForm)
    }
}

data class ListValue(val mode: BasicTypeMode, val elements: MutableList<Value>) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(elements.size)
    }

    fun evalToList(): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                return ListValue(ImmutableBasicTypeMode, elements.toList().toMutableList())
            }
        }
    }

    override fun evalEquals(other: Value): Value {
        return BooleanValue(this == other)
    }

    override fun evalNotEquals(other: Value): Value {
        return BooleanValue(this != other)
    }

    fun evalGet(index: Value): Value {
        val indexInt = index as IntValue
        return elements[indexInt.canonicalForm]
    }

    fun evalSet(index: Value, value: Value): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                val indexInt = index as IntValue
                when {
                    index.canonicalForm < elements.size -> {
                        elements[indexInt.canonicalForm] = value
                    }
                    index.canonicalForm == elements.size -> {
                        elements.add(value)
                    }
                    else -> {
                        langThrow(IndexOutOfBounds(index.canonicalForm, elements.size))
                    }
                }
                return UnitValue
            }
        }
    }

    fun evalRemoveAt(value: Value): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                val index = (value as IntValue).canonicalForm
                elements.removeAt(index)
                return UnitValue
            }
        }
    }

    fun evalAdd(value: Value): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                elements.add(value)
                if (elements.size.toLong() > mode.fin) {
                    langThrow(NotInSource, RuntimeFinViolation(mode.fin, elements.size.toLong()))
                }
                return UnitValue
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is ListValue) {
            return elements == other.elements
        }
        return false
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }
}

data class SetValue(val mode: BasicTypeMode, val elements: MutableSet<Value>) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(elements.size)
    }

    fun evalToSet(): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                return SetValue(ImmutableBasicTypeMode, elements.toSet().toMutableSet())
            }
        }
    }

    override fun evalEquals(other: Value): Value {
        return BooleanValue(this == other)
    }

    override fun evalNotEquals(other: Value): Value {
        return BooleanValue(this != other)
    }

    fun evalContains(value: Value): Value {
        return BooleanValue(elements.contains(value))
    }

    fun evalRemove(value: Value): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                elements.remove(value)
                return UnitValue
            }
        }
    }

    fun evalAdd(value: Value): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                elements.add(value)
                if (elements.size.toLong() > mode.fin) {
                    langThrow(NotInSource, RuntimeFinViolation(mode.fin, elements.size.toLong()))
                }
                return UnitValue
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is SetValue) {
            return elements == other.elements
        }
        return false
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }
}

data class DictionaryValue(
    val mode: BasicTypeMode,
    val dictionary: MutableMap<Value, Value>
) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(dictionary.size)
    }

    fun evalToDictionary(): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                return DictionaryValue(ImmutableBasicTypeMode, dictionary.toMap().toMutableMap())
            }
        }
    }

    override fun evalEquals(other: Value): Value {
        return BooleanValue(this == other)
    }

    override fun evalNotEquals(other: Value): Value {
        return BooleanValue(this != other)
    }

    fun evalGet(key: Value): Value {
        return dictionary[key]!!
    }

    fun evalContains(key: Value): Value {
        return BooleanValue(dictionary.containsKey(key))
    }

    fun evalSet(key: Value, value: Value): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                dictionary[key] = value
                if (dictionary.size.toLong() > mode.fin) {
                    langThrow(NotInSource, RuntimeFinViolation(mode.fin, dictionary.size.toLong()))
                }
                return UnitValue
            }
        }
    }

    fun evalRemove(key: Value): Value {
        when (mode) {
            is ImmutableBasicTypeMode -> {
                langThrow(NotInSource, RuntimeImmutableViolation)
            }
            is MutableBasicTypeMode -> {
                dictionary.remove(key)
                return UnitValue
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is DictionaryValue) {
            return dictionary == other.dictionary
        }
        return false
    }

    override fun hashCode(): Int {
        return dictionary.hashCode()
    }
}

class SymbolRouterValueTable(private val prelude: Scope<Symbol>, private val symbols: Scope<Symbol>) : Scope<Value> {
    override fun define(identifier: Identifier, definition: Value) {
        langThrow(identifier.ctx, IdentifierCouldNotBeDefined(identifier))
    }

    override fun exists(signifier: Signifier): Boolean =
        symbols.exists(signifier)

    override fun existsHere(signifier: Signifier): Boolean =
        symbols.existsHere(signifier)

    override fun fetch(signifier: Signifier): Value =
        when (val res = symbols.fetch(signifier)) {
            is GroundFunctionSymbol -> {
                val fv = FunctionValue(res.formalParams, res.body)
                fv
            }

            is ParameterizedFunctionSymbol -> {
                val fv = FunctionValue(res.formalParams, res.body)
                fv
            }

            is ParameterizedStaticPluginSymbol -> PluginValue(res.plugin)
            is GroundRecordTypeSymbol -> RecordConstructorValue(prelude, res)
            is ParameterizedRecordTypeSymbol -> RecordConstructorValue(prelude, res)
            is ParameterizedBasicTypeSymbol -> when (res.identifier) {
                Lang.listId, Lang.mutableListId -> ListConstructorValue(res.modeSelector)
                Lang.dictionaryId, Lang.mutableDictionaryId -> DictionaryConstructorValue(res.modeSelector)
                Lang.setId, Lang.mutableSetId -> SetConstructorValue(res.modeSelector)
                else -> langThrow(signifier.ctx, IdentifierNotFound(signifier))
            }

            is PlatformObjectSymbol -> if (res.identifier == Lang.unitId) {
                UnitValue
            } else {
                langThrow(signifier.ctx, TypeSystemBug)
            }

            is ObjectSymbol -> ObjectValue(res)
            else -> langThrow(signifier.ctx, IdentifierNotFound(signifier))
        }

    override fun fetchHere(signifier: Signifier): Value =
        when (val res = symbols.fetchHere(signifier)) {
            is GroundFunctionSymbol -> {
                val fv = FunctionValue(res.formalParams, res.body)
                fv
            }
            is ParameterizedFunctionSymbol -> {
                val fv = FunctionValue(res.formalParams, res.body)
                fv
            }
            is ParameterizedStaticPluginSymbol -> PluginValue(res.plugin)
            is GroundRecordTypeSymbol -> RecordConstructorValue(prelude, res)
            is ParameterizedRecordTypeSymbol -> RecordConstructorValue(prelude, res)
            is ParameterizedBasicTypeSymbol -> when (res.identifier) {
                Lang.listId, Lang.mutableListId -> ListConstructorValue(res.modeSelector)
                Lang.dictionaryId, Lang.mutableDictionaryId -> DictionaryConstructorValue(res.modeSelector)
                Lang.setId, Lang.mutableSetId -> SetConstructorValue(res.modeSelector)
                else -> langThrow(signifier.ctx, IdentifierNotFound(signifier))
            }
            is ObjectSymbol -> ObjectValue(res)
            else -> langThrow(signifier.ctx, IdentifierNotFound(signifier))
        }
}

class ValueTable(private val parent: Scope<Value>) : Scope<Value> {
    data class ScopeSlot(var value: Value)

    private val slotTable: MutableMap<String, ScopeSlot> = HashMap()

    fun valuesHere() = slotTable.map { Pair(it.key, it.value.value) }.toMap()

    override fun define(identifier: Identifier, definition: Value) {
        slotTable[identifier.name] = ScopeSlot(definition)
    }

    fun assign(identifier: Identifier, definition: Value) {
        val slot = fetchSlot(identifier)
        slot.value = definition
    }

    private fun fetchSlot(identifier: Identifier): ScopeSlot =
        if (slotTable.containsKey(identifier.name)) {
            slotTable[identifier.name]!!
        } else {
            if (parent is ValueTable) {
                parent.fetchSlot(identifier)
            } else {
                langThrow(identifier.ctx, TypeSystemBug)
            }
        }

    override fun exists(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> slotTable.containsKey(signifier.name) || parent.exists(signifier)
            else -> false
        }

    override fun existsHere(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> slotTable.containsKey(signifier.name)
            else -> false
        }

    override fun fetch(signifier: Signifier): Value =
        when (signifier) {
            is Identifier -> {
                if (slotTable.containsKey(signifier.name)) {
                    slotTable[signifier.name]!!.value
                } else {
                    parent.fetch(signifier)
                }
            }
            else -> langThrow(signifier.ctx, TypeSystemBug)
        }

    override fun fetchHere(signifier: Signifier): Value =
        when (signifier) {
            is Identifier -> {
                if (slotTable.containsKey(signifier.name)) {
                    slotTable[signifier.name]!!.value
                } else {
                    langThrow(signifier.ctx, IdentifierNotFound(signifier))
                }
            }
            else -> langThrow(signifier.ctx, TypeSystemBug)
        }
}
