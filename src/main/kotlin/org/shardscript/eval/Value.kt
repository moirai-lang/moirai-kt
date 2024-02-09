package org.shardscript.eval

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.prelude.*
import java.math.BigDecimal
import kotlin.random.Random

sealed class Value

data object UnitValue : Value() {
    fun evalToString(): Value {
        return strToStringValue(Lang.unitId.name)
    }
}

data class GroundMemberPlugin(private val plugin: (Value, List<Value>) -> Value) {
    fun invoke(t: Value, args: List<Value>): Value = plugin(t, args)
}

data class PlatformField(private val accessor: (Value) -> Value) {
    fun invoke(t: Value): Value = accessor(t)
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
        StringOpMembers.integerToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as IntValue).evalToString()
        },
        StringOpMembers.unitToStringMember to GroundMemberPlugin { _: Value, _: List<Value> ->
            UnitValue.evalToString()
        },
        StringOpMembers.booleanToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as BooleanValue).evalToString()
        },
        StringOpMembers.charToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as CharValue).evalToString()
        }
    )

    val parameterizedMemberPlugins: Map<ParameterizedMemberPluginSymbol, GroundMemberPlugin> = mapOf(
        StringOpMembers.decimalToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as DecimalValue).evalToString()
        },
        StringOpMembers.stringToStringMember to GroundMemberPlugin { t: Value, _: List<Value> ->
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
        },
        SetTypes.setContains to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as SetValue).evalContains(args.first())
        },
        SetTypes.mutableSetContains to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as SetValue).evalContains(args.first())
        },
        SetTypes.mutableSetAdd to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as SetValue).evalAdd(args.first())
        },
        SetTypes.mutableSetRemove to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as SetValue).evalRemove(args.first())
        },
        SetTypes.mutableSetToSet to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as SetValue).evalToSet()
        },
        ListTypes.listGet to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as ListValue).evalGet(args.first())
        },
        ListTypes.mutableListGet to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as ListValue).evalGet(args.first())
        },
        ListTypes.mutableListAdd to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as ListValue).evalAdd(args.first())
        },
        ListTypes.mutableListSet to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as ListValue).evalSet(args.first(), args[1])
        },
        ListTypes.mutableListToList to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as ListValue).evalToList()
        },
        DictionaryTypes.getFunction to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as DictionaryValue).evalGet(args.first())
        },
        DictionaryTypes.mutableGetFunction to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as DictionaryValue).evalGet(args.first())
        },
        DictionaryTypes.containsFunction to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as DictionaryValue).evalContains(args.first())
        },
        DictionaryTypes.mutableContainsFunction to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as DictionaryValue).evalContains(args.first())
        },
        DictionaryTypes.setFunction to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as DictionaryValue).evalSet(args.first(), args[1])
        },
        DictionaryTypes.removeFunction to GroundMemberPlugin { t: Value, args: List<Value> ->
            (t as DictionaryValue).evalRemove(args.first())
        },
        DictionaryTypes.mutableDictionaryToDictionary to GroundMemberPlugin { t: Value, _: List<Value> ->
            (t as DictionaryValue).evalToDictionary()
        }
    )

    val staticPlugins: Map<ParameterizedStaticPluginSymbol, PluginValue> = mapOf(
        StaticPlugins.rangePlugin to PluginValue { args ->
            val originalLowerBound = args[0] as IntValue
            val originalUpperBound = args[1] as IntValue
            if (originalLowerBound.canonicalForm < originalUpperBound.canonicalForm) {
                val lowerBound = originalLowerBound.canonicalForm
                val upperBound = originalUpperBound.canonicalForm - 1
                val list: MutableList<Value> = (lowerBound..upperBound).toList().map {
                    IntValue(it)
                }.toMutableList()
                ListValue(list, list.size.toLong(), false)
            } else {
                val lowerBound = originalUpperBound.canonicalForm + 1
                val upperBound = originalLowerBound.canonicalForm
                val list: MutableList<Value> = (lowerBound..upperBound).toList().map {
                    IntValue(it)
                }.toMutableList()
                list.reverse()
                ListValue(list, list.size.toLong(), false)
            }
        },
        StaticPlugins.randomPlugin to PluginValue { args ->
            when (val first = args.first()) {
                is IntValue -> {
                    var lowerBound = first.canonicalForm
                    var upperBound = (args[1] as IntValue).canonicalForm
                    var lowerBoundInclusive = true
                    var upperBoundInclusive = false

                    if (lowerBound > upperBound) {
                        val temp = lowerBound
                        lowerBound = upperBound
                        upperBound = temp
                        lowerBoundInclusive = false
                        upperBoundInclusive = true
                    }

                    var offset = 0
                    if (lowerBound < 0) {
                        offset = lowerBound
                        lowerBound += -offset
                        upperBound += -offset
                    }

                    if (!lowerBoundInclusive) {
                        lowerBound += 1
                    }
                    if (upperBoundInclusive) {
                        upperBound += 1
                    }

                    var res = Random.nextInt(lowerBound, upperBound)
                    res += offset
                    IntValue(res)
                }

                else -> {
                    langThrow(NotInSource, TypeSystemBug)
                }
            }
        }
    )

    val fields: Map<PlatformFieldSymbol, PlatformField> = mapOf(
        StringTypes.sizeFieldSymbol to PlatformField { value ->
            (value as StringValue).fieldSize()
        },
        SetTypes.setSizeFieldSymbol to PlatformField { value ->
            (value as SetValue).fieldSize()
        },
        SetTypes.mutableSizeFieldSymbol to PlatformField { value ->
            (value as SetValue).fieldSize()
        },
        ListTypes.listSizeFieldSymbol to PlatformField { value ->
            (value as ListValue).fieldSize()
        },
        ListTypes.mutableSizeFieldSymbol to PlatformField { value ->
            (value as ListValue).fieldSize()
        },
        DictionaryTypes.dictionarySizeFieldSymbol to PlatformField { value ->
            (value as DictionaryValue).fieldSize()
        },
        DictionaryTypes.mutableSizeFieldSymbol to PlatformField { value ->
            (value as DictionaryValue).fieldSize()
        }
    )
}

class ObjectValue(
    val symbol: ObjectType
) : Value() {
    val path = getQualifiedName(symbol)

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
) : Value()

class RecordValue(type: Type, val fields: ValueTable, val substitutions: Map<Type, Type>) : Value() {
    lateinit var scope: Scope
    val path = getQualifiedName(type)

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

    fun evalToString(): Value = strToStringValue(canonicalForm.toString())
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

    fun evalToString(): Value = strToStringValue(canonicalForm.toString())
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

    fun evalToString(): Value = strToStringValue(canonicalForm.stripTrailingZeros().toPlainString())
}

data class CharValue(val canonicalForm: Char) : Value(), EqualityValue {
    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as CharValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value =
        BooleanValue(canonicalForm != (other as CharValue).canonicalForm)

    fun evalToString(): Value = strToStringValue(canonicalForm.toString())
}

fun strToStringValue(str: String): StringValue {
    val fin = Fin(str.length.toLong())
    return StringValue(str, mapOf(Lang.stringTypeParam to fin))
}

data class StringValue(val canonicalForm: String, val substitutions: Map<Type, Type>) : Value(), EqualityValue {
    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as StringValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value =
        BooleanValue(canonicalForm != (other as StringValue).canonicalForm)

    fun fieldSize(): Value {
        return IntValue(canonicalForm.length)
    }

    fun evalToCharArray(): Value {
        val elements = canonicalForm.toCharArray().map { CharValue(it) }
        return ListValue(elements.toMutableList(), canonicalForm.length.toLong(), false)
    }

    fun evalToString(): Value = strToStringValue(canonicalForm)

    fun evalAdd(arg: Value): Value {
        val other = arg as StringValue
        return strToStringValue(canonicalForm + other.canonicalForm)
    }
}

data class ListValue(val elements: MutableList<Value>, val fin: Long, val mutable: Boolean) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(elements.size)
    }

    fun evalToList(): Value {
        if (mutable) {
            return ListValue(elements.toList().toMutableList(), fin, false)
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
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
        if (mutable) {
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
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
        }
    }

    fun evalRemoveAt(value: Value): Value {
        if (mutable) {
            val index = (value as IntValue).canonicalForm
            elements.removeAt(index)
            return UnitValue
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
        }
    }

    fun evalAdd(value: Value): Value {
        if (mutable) {
            elements.add(value)
            if (elements.size.toLong() > fin) {
                langThrow(NotInSource, RuntimeFinViolation(fin, elements.size.toLong()))
            }
            return UnitValue
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
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

data class SetValue(val elements: MutableSet<Value>, val fin: Long, val mutable: Boolean) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(elements.size)
    }

    fun evalToSet(): Value {
        if (mutable) {
            return SetValue(elements.toSet().toMutableSet(), fin, false)
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
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
        if (mutable) {
            elements.remove(value)
            return UnitValue
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
        }
    }

    fun evalAdd(value: Value): Value {
        if (mutable) {
            elements.add(value)
            if (elements.size.toLong() > fin) {
                langThrow(NotInSource, RuntimeFinViolation(fin, elements.size.toLong()))
            }
            return UnitValue
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
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
    val dictionary: MutableMap<Value, Value>,
    val fin: Long,
    val mutable: Boolean
) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(dictionary.size)
    }

    fun evalToDictionary(): Value {
        if (mutable) {
            return DictionaryValue(dictionary.toMap().toMutableMap(), fin, false)
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
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
        if (mutable) {
            dictionary[key] = value
            if (dictionary.size.toLong() > fin) {
                langThrow(NotInSource, RuntimeFinViolation(fin, dictionary.size.toLong()))
            }
            return UnitValue
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
        }
    }

    fun evalRemove(key: Value): Value {
        if (mutable) {
            dictionary.remove(key)
            return UnitValue
        } else {
            langThrow(NotInSource, RuntimeImmutableViolation)
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

interface ValueScope {
    fun define(identifier: Identifier, definition: Value)
    fun exists(signifier: Signifier): Boolean
    fun existsHere(signifier: Signifier): Boolean
    fun fetch(signifier: Signifier): Value
    fun fetchHere(signifier: Signifier): Value
}

object NullValueTable : ValueScope {
    override fun define(identifier: Identifier, definition: Value) {
        langThrow(NotInSource, IdentifierCouldNotBeDefined(identifier))
    }

    override fun exists(signifier: Signifier): Boolean = false

    override fun existsHere(signifier: Signifier): Boolean = false

    override fun fetch(signifier: Signifier): Value {
        langThrow(NotInSource, IdentifierNotFound(signifier))
    }

    override fun fetchHere(signifier: Signifier): Value {
        langThrow(NotInSource, IdentifierNotFound(signifier))
    }

}

class ValueTable(private val parent: ValueScope) : ValueScope {
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
