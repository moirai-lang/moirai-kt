package com.tsikhe.shardscript.semantics.core

import com.tsikhe.shardscript.semantics.prelude.Lang
import java.math.BigDecimal

sealed class Value

class NamespaceValue(
    val router: SymbolRouterValueTable
) : Value()

object UnitValue : Value() {
    fun evalToString(): Value {
        return StringValue(Lang.unitId.name)
    }
}

class ObjectValue(
    val symbol: ObjectSymbol
) : Value() {
    val path = generatePath(symbol)

    fun evalEquals(other: Value): Value {
        return BooleanValue(this == other)
    }

    fun evalNotEquals(other: Value): Value {
        return BooleanValue(this != other)
    }

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
    lateinit var globalScope: ValueTable
    lateinit var evalCallback: (Ast, ValueTable) -> Value

    fun invoke(args: List<Value>): Value {
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

    fun evalEquals(other: Value): Value {
        return BooleanValue(this == other)
    }

    fun evalNotEquals(other: Value): Value {
        return BooleanValue(this != other)
    }

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

data class RecordConstructorValue(val prelude: PreludeTable, val symbol: Symbol) : Value() {
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

data class ListConstructorValue(val modeSelector: (List<Symbol>) -> BasicTypeMode) :
    Value() {
    fun apply(typeArgs: List<Symbol>, elements: List<Value>): ListValue {
        return ListValue(modeSelector(typeArgs), elements.toMutableList())
    }
}

data class SetConstructorValue(val modeSelector: (List<Symbol>) -> BasicTypeMode) :
    Value() {
    fun apply(typeArgs: List<Symbol>, elements: List<Value>): SetValue {
        return SetValue(modeSelector(typeArgs), elements.toMutableSet())
    }
}

data class DictionaryConstructorValue(val modeSelector: (List<Symbol>) -> BasicTypeMode) :
    Value() {
    fun apply(typeArgs: List<Symbol>, elements: List<Value>): DictionaryValue {
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

interface IntegerConversionsValue {
    fun evalToSigned8(): Value
    fun evalToSigned16(): Value
    fun evalToSigned32(): Value
    fun evalToSigned64(): Value
    fun evalToUnsigned8(): Value
    fun evalToUnsigned16(): Value
    fun evalToUnsigned32(): Value
    fun evalToUnsigned64(): Value
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

@UseExperimental(ExperimentalUnsignedTypes::class)
data class SByteValue(val canonicalForm: Byte) : Value(), MathValue, OrderValue, EqualityValue,
    IntegerConversionsValue {
    override fun evalAdd(other: Value): Value =
        SByteValue((canonicalForm + (other as SByteValue).canonicalForm).toByte())

    override fun evalSub(other: Value): Value =
        SByteValue((canonicalForm - (other as SByteValue).canonicalForm).toByte())

    override fun evalMul(other: Value): Value =
        SByteValue((canonicalForm * (other as SByteValue).canonicalForm).toByte())

    override fun evalDiv(other: Value): Value =
        SByteValue((canonicalForm / (other as SByteValue).canonicalForm).toByte())

    override fun evalMod(other: Value): Value =
        SByteValue((canonicalForm % (other as SByteValue).canonicalForm).toByte())

    override fun evalGreaterThan(other: Value): Value =
        BooleanValue(canonicalForm > (other as SByteValue).canonicalForm)

    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as SByteValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as SByteValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as SByteValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as SByteValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value = BooleanValue(canonicalForm != (other as SByteValue).canonicalForm)
    override fun evalNegate(): Value = SByteValue((-canonicalForm).toByte())

    fun evalToString(): Value = StringValue(canonicalForm.toString() + Lang.sByteSuffix)

    override fun evalToSigned8(): Value = langThrow(TypeSystemBug)
    override fun evalToSigned16(): Value = ShortValue(canonicalForm.toShort())
    override fun evalToSigned32(): Value = IntValue(canonicalForm.toInt())
    override fun evalToSigned64(): Value = LongValue(canonicalForm.toLong())
    override fun evalToUnsigned8(): Value = ByteValue(canonicalForm.toUByte())
    override fun evalToUnsigned16(): Value = UShortValue(canonicalForm.toUShort())
    override fun evalToUnsigned32(): Value = UIntValue(canonicalForm.toUInt())
    override fun evalToUnsigned64(): Value = ULongValue(canonicalForm.toULong())
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class ShortValue(val canonicalForm: Short) : Value(), MathValue, OrderValue, EqualityValue,
    IntegerConversionsValue {
    override fun evalAdd(other: Value): Value =
        ShortValue((canonicalForm + (other as ShortValue).canonicalForm).toShort())

    override fun evalSub(other: Value): Value =
        ShortValue((canonicalForm - (other as ShortValue).canonicalForm).toShort())

    override fun evalMul(other: Value): Value =
        ShortValue((canonicalForm * (other as ShortValue).canonicalForm).toShort())

    override fun evalDiv(other: Value): Value =
        ShortValue((canonicalForm / (other as ShortValue).canonicalForm).toShort())

    override fun evalMod(other: Value): Value =
        ShortValue((canonicalForm % (other as ShortValue).canonicalForm).toShort())

    override fun evalGreaterThan(other: Value): Value =
        BooleanValue(canonicalForm > (other as ShortValue).canonicalForm)

    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as ShortValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as ShortValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as ShortValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as ShortValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value = BooleanValue(canonicalForm != (other as ShortValue).canonicalForm)
    override fun evalNegate(): Value = ShortValue((-canonicalForm).toShort())

    fun evalToString(): Value = StringValue(canonicalForm.toString() + Lang.shortSuffix)

    override fun evalToSigned8(): Value = SByteValue(canonicalForm.toByte())
    override fun evalToSigned16(): Value = langThrow(TypeSystemBug)
    override fun evalToSigned32(): Value = IntValue(canonicalForm.toInt())
    override fun evalToSigned64(): Value = LongValue(canonicalForm.toLong())
    override fun evalToUnsigned8(): Value = ByteValue(canonicalForm.toUByte())
    override fun evalToUnsigned16(): Value = UShortValue(canonicalForm.toUShort())
    override fun evalToUnsigned32(): Value = UIntValue(canonicalForm.toUInt())
    override fun evalToUnsigned64(): Value = ULongValue(canonicalForm.toULong())
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class IntValue(val canonicalForm: Int) : Value(), MathValue, OrderValue, EqualityValue, IntegerConversionsValue {
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

    override fun evalToSigned8(): Value = SByteValue(canonicalForm.toByte())
    override fun evalToSigned16(): Value = ShortValue(canonicalForm.toShort())
    override fun evalToSigned32(): Value = langThrow(TypeSystemBug)
    override fun evalToSigned64(): Value = LongValue(canonicalForm.toLong())
    override fun evalToUnsigned8(): Value = ByteValue(canonicalForm.toUByte())
    override fun evalToUnsigned16(): Value = UShortValue(canonicalForm.toUShort())
    override fun evalToUnsigned32(): Value = UIntValue(canonicalForm.toUInt())
    override fun evalToUnsigned64(): Value = ULongValue(canonicalForm.toULong())
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class LongValue(val canonicalForm: Long) : Value(), MathValue, OrderValue, EqualityValue, IntegerConversionsValue {
    override fun evalAdd(other: Value): Value = LongValue((canonicalForm + (other as LongValue).canonicalForm))
    override fun evalSub(other: Value): Value = LongValue((canonicalForm - (other as LongValue).canonicalForm))
    override fun evalMul(other: Value): Value = LongValue((canonicalForm * (other as LongValue).canonicalForm))
    override fun evalDiv(other: Value): Value = LongValue((canonicalForm / (other as LongValue).canonicalForm))
    override fun evalMod(other: Value): Value = LongValue((canonicalForm % (other as LongValue).canonicalForm))
    override fun evalGreaterThan(other: Value): Value = BooleanValue(canonicalForm > (other as LongValue).canonicalForm)
    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as LongValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as LongValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as LongValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as LongValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value = BooleanValue(canonicalForm != (other as LongValue).canonicalForm)
    override fun evalNegate(): Value = LongValue((-canonicalForm))

    fun evalToString(): Value = StringValue(canonicalForm.toString() + Lang.longSuffix)

    override fun evalToSigned8(): Value = SByteValue(canonicalForm.toByte())
    override fun evalToSigned16(): Value = ShortValue(canonicalForm.toShort())
    override fun evalToSigned32(): Value = IntValue(canonicalForm.toInt())
    override fun evalToSigned64(): Value = langThrow(TypeSystemBug)
    override fun evalToUnsigned8(): Value = ByteValue(canonicalForm.toUByte())
    override fun evalToUnsigned16(): Value = UShortValue(canonicalForm.toUShort())
    override fun evalToUnsigned32(): Value = UIntValue(canonicalForm.toUInt())
    override fun evalToUnsigned64(): Value = ULongValue(canonicalForm.toULong())
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class ByteValue(val canonicalForm: UByte) : Value(), MathValue, OrderValue, EqualityValue,
    IntegerConversionsValue {
    override fun evalAdd(other: Value): Value =
        ByteValue((canonicalForm + (other as ByteValue).canonicalForm).toUByte())

    override fun evalSub(other: Value): Value =
        ByteValue((canonicalForm - (other as ByteValue).canonicalForm).toUByte())

    override fun evalMul(other: Value): Value =
        ByteValue((canonicalForm * (other as ByteValue).canonicalForm).toUByte())

    override fun evalDiv(other: Value): Value =
        ByteValue((canonicalForm / (other as ByteValue).canonicalForm).toUByte())

    override fun evalMod(other: Value): Value =
        ByteValue((canonicalForm % (other as ByteValue).canonicalForm).toUByte())

    override fun evalGreaterThan(other: Value): Value = BooleanValue(canonicalForm > (other as ByteValue).canonicalForm)
    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as ByteValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as ByteValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as ByteValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as ByteValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value = BooleanValue(canonicalForm != (other as ByteValue).canonicalForm)
    override fun evalNegate(): Value = langThrow(TypeSystemBug)

    fun evalToString(): Value = StringValue(canonicalForm.toString() + Lang.byteSuffix)

    override fun evalToSigned8(): Value = SByteValue(canonicalForm.toByte())
    override fun evalToSigned16(): Value = ShortValue(canonicalForm.toShort())
    override fun evalToSigned32(): Value = IntValue(canonicalForm.toInt())
    override fun evalToSigned64(): Value = LongValue(canonicalForm.toLong())
    override fun evalToUnsigned8(): Value = langThrow(TypeSystemBug)
    override fun evalToUnsigned16(): Value = UShortValue(canonicalForm.toUShort())
    override fun evalToUnsigned32(): Value = UIntValue(canonicalForm.toUInt())
    override fun evalToUnsigned64(): Value = ULongValue(canonicalForm.toULong())
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class UShortValue(val canonicalForm: UShort) : Value(), MathValue, OrderValue, EqualityValue,
    IntegerConversionsValue {
    override fun evalAdd(other: Value): Value =
        UShortValue((canonicalForm + (other as UShortValue).canonicalForm).toUShort())

    override fun evalSub(other: Value): Value =
        UShortValue((canonicalForm - (other as UShortValue).canonicalForm).toUShort())

    override fun evalMul(other: Value): Value =
        UShortValue((canonicalForm * (other as UShortValue).canonicalForm).toUShort())

    override fun evalDiv(other: Value): Value =
        UShortValue((canonicalForm / (other as UShortValue).canonicalForm).toUShort())

    override fun evalMod(other: Value): Value =
        UShortValue((canonicalForm % (other as UShortValue).canonicalForm).toUShort())

    override fun evalGreaterThan(other: Value): Value =
        BooleanValue(canonicalForm > (other as UShortValue).canonicalForm)

    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as UShortValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as UShortValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as UShortValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as UShortValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value =
        BooleanValue(canonicalForm != (other as UShortValue).canonicalForm)

    override fun evalNegate(): Value = langThrow(TypeSystemBug)

    fun evalToString(): Value = StringValue(canonicalForm.toString() + Lang.uShortSuffix)

    override fun evalToSigned8(): Value = SByteValue(canonicalForm.toByte())
    override fun evalToSigned16(): Value = ShortValue(canonicalForm.toShort())
    override fun evalToSigned32(): Value = IntValue(canonicalForm.toInt())
    override fun evalToSigned64(): Value = LongValue(canonicalForm.toLong())
    override fun evalToUnsigned8(): Value = ByteValue(canonicalForm.toUByte())
    override fun evalToUnsigned16(): Value = langThrow(TypeSystemBug)
    override fun evalToUnsigned32(): Value = UIntValue(canonicalForm.toUInt())
    override fun evalToUnsigned64(): Value = ULongValue(canonicalForm.toULong())
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class UIntValue(val canonicalForm: UInt) : Value(), MathValue, OrderValue, EqualityValue, IntegerConversionsValue {
    override fun evalAdd(other: Value): Value = UIntValue(canonicalForm + (other as UIntValue).canonicalForm)
    override fun evalSub(other: Value): Value = UIntValue(canonicalForm - (other as UIntValue).canonicalForm)
    override fun evalMul(other: Value): Value = UIntValue(canonicalForm * (other as UIntValue).canonicalForm)
    override fun evalDiv(other: Value): Value = UIntValue(canonicalForm / (other as UIntValue).canonicalForm)
    override fun evalMod(other: Value): Value = UIntValue(canonicalForm % (other as UIntValue).canonicalForm)
    override fun evalGreaterThan(other: Value): Value = BooleanValue(canonicalForm > (other as UIntValue).canonicalForm)
    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as UIntValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as UIntValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as UIntValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as UIntValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value = BooleanValue(canonicalForm != (other as UIntValue).canonicalForm)
    override fun evalNegate(): Value = langThrow(TypeSystemBug)

    fun evalToString(): Value = StringValue(canonicalForm.toString() + Lang.uIntSuffix)

    override fun evalToSigned8(): Value = SByteValue(canonicalForm.toByte())
    override fun evalToSigned16(): Value = ShortValue(canonicalForm.toShort())
    override fun evalToSigned32(): Value = IntValue(canonicalForm.toInt())
    override fun evalToSigned64(): Value = LongValue(canonicalForm.toLong())
    override fun evalToUnsigned8(): Value = ByteValue(canonicalForm.toUByte())
    override fun evalToUnsigned16(): Value = UShortValue(canonicalForm.toUShort())
    override fun evalToUnsigned32(): Value = langThrow(TypeSystemBug)
    override fun evalToUnsigned64(): Value = ULongValue(canonicalForm.toULong())
}

@UseExperimental(ExperimentalUnsignedTypes::class)
data class ULongValue(val canonicalForm: ULong) : Value(), MathValue, OrderValue, EqualityValue,
    IntegerConversionsValue {
    override fun evalAdd(other: Value): Value = ULongValue((canonicalForm + (other as ULongValue).canonicalForm))
    override fun evalSub(other: Value): Value = ULongValue((canonicalForm - (other as ULongValue).canonicalForm))
    override fun evalMul(other: Value): Value = ULongValue((canonicalForm * (other as ULongValue).canonicalForm))
    override fun evalDiv(other: Value): Value = ULongValue((canonicalForm / (other as ULongValue).canonicalForm))
    override fun evalMod(other: Value): Value = ULongValue((canonicalForm % (other as ULongValue).canonicalForm))
    override fun evalGreaterThan(other: Value): Value =
        BooleanValue(canonicalForm > (other as ULongValue).canonicalForm)

    override fun evalGreaterThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm >= (other as ULongValue).canonicalForm)

    override fun evalLessThan(other: Value): Value = BooleanValue(canonicalForm < (other as ULongValue).canonicalForm)
    override fun evalLessThanOrEquals(other: Value): Value =
        BooleanValue(canonicalForm <= (other as ULongValue).canonicalForm)

    override fun evalEquals(other: Value): Value = BooleanValue(canonicalForm == (other as ULongValue).canonicalForm)
    override fun evalNotEquals(other: Value): Value = BooleanValue(canonicalForm != (other as ULongValue).canonicalForm)
    override fun evalNegate(): Value = langThrow(TypeSystemBug)

    fun evalToString(): Value = StringValue(canonicalForm.toString() + Lang.uLongSuffix)

    override fun evalToSigned8(): Value = SByteValue(canonicalForm.toByte())
    override fun evalToSigned16(): Value = ShortValue(canonicalForm.toShort())
    override fun evalToSigned32(): Value = IntValue(canonicalForm.toInt())
    override fun evalToSigned64(): Value = LongValue(canonicalForm.toLong())
    override fun evalToUnsigned8(): Value = ByteValue(canonicalForm.toUByte())
    override fun evalToUnsigned16(): Value = UShortValue(canonicalForm.toUShort())
    override fun evalToUnsigned32(): Value = UIntValue(canonicalForm.toUInt())
    override fun evalToUnsigned64(): Value = langThrow(TypeSystemBug)
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
                if (elements.size.toLong() > mode.omicron) {
                    langThrow(NotInSource, RuntimeOmicronViolation(mode.omicron, elements.size.toLong()))
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
                if (elements.size.toLong() > mode.omicron) {
                    langThrow(NotInSource, RuntimeOmicronViolation(mode.omicron, elements.size.toLong()))
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
                if (dictionary.size.toLong() > mode.omicron) {
                    langThrow(NotInSource, RuntimeOmicronViolation(mode.omicron, dictionary.size.toLong()))
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

class SymbolRouterValueTable(private val prelude: PreludeTable, private val symbols: Scope<Symbol>) : Scope<Value> {
    lateinit var initFunctionCallback: (FunctionValue) -> Unit

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
                initFunctionCallback(fv)
                fv
            }
            is ParameterizedFunctionSymbol -> {
                val fv = FunctionValue(res.formalParams, res.body)
                initFunctionCallback(fv)
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
            is ObjectSymbol -> {
                when (generatePath(res)) {
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.unitId.name) -> UnitValue
                    else -> ObjectValue(res)
                }
            }
            is Namespace -> {
                val router = SymbolRouterValueTable(prelude, res)
                router.initFunctionCallback = initFunctionCallback
                NamespaceValue(router)
            }
            else -> langThrow(signifier.ctx, IdentifierNotFound(signifier))
        }

    override fun fetchHere(signifier: Signifier): Value =
        when (val res = symbols.fetchHere(signifier)) {
            is GroundFunctionSymbol -> {
                val fv = FunctionValue(res.formalParams, res.body)
                initFunctionCallback(fv)
                fv
            }
            is ParameterizedFunctionSymbol -> {
                val fv = FunctionValue(res.formalParams, res.body)
                initFunctionCallback(fv)
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
            is Namespace -> {
                val router = SymbolRouterValueTable(prelude, res)
                router.initFunctionCallback = initFunctionCallback
                NamespaceValue(router)
            }
            else -> langThrow(signifier.ctx, IdentifierNotFound(signifier))
        }
}

object NullValueTable : Scope<Value> {
    override fun define(identifier: Identifier, definition: Value) {
        langThrow(identifier.ctx, IdentifierCouldNotBeDefined(identifier))
    }

    override fun exists(signifier: Signifier): Boolean = false

    override fun existsHere(signifier: Signifier): Boolean = false

    override fun fetch(signifier: Signifier): Value {
        langThrow(signifier.ctx, IdentifierNotFound(signifier))
    }

    override fun fetchHere(signifier: Signifier): Value {
        langThrow(signifier.ctx, IdentifierNotFound(signifier))
    }
}

class ValueTable(private val parent: Scope<Value>) : Scope<Value> {
    data class ScopeSlot(var value: Value)

    private val slotTable: MutableMap<Identifier, ScopeSlot> = HashMap()

    fun valuesHere() = slotTable.map { Pair(it.key, it.value.value) }.toMap()

    override fun define(identifier: Identifier, definition: Value) {
        slotTable[identifier] = ScopeSlot(definition)
    }

    fun assign(identifier: Identifier, definition: Value) {
        val slot = fetchSlot(identifier)
        slot.value = definition
    }

    private fun fetchSlot(identifier: Identifier): ScopeSlot =
        if (slotTable.containsKey(identifier)) {
            slotTable[identifier]!!
        } else {
            if (parent is ValueTable) {
                parent.fetchSlot(identifier)
            } else {
                langThrow(identifier.ctx, TypeSystemBug)
            }
        }

    override fun exists(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> slotTable.containsKey(signifier) || parent.exists(signifier)
            else -> false
        }

    override fun existsHere(signifier: Signifier): Boolean =
        when (signifier) {
            is Identifier -> slotTable.containsKey(signifier)
            else -> false
        }

    override fun fetch(signifier: Signifier): Value =
        when (signifier) {
            is Identifier -> {
                if (slotTable.containsKey(signifier)) {
                    slotTable[signifier]!!.value
                } else {
                    parent.fetch(signifier)
                }
            }
            else -> langThrow(signifier.ctx, TypeSystemBug)
        }

    override fun fetchHere(signifier: Signifier): Value =
        when (signifier) {
            is Identifier -> {
                if (slotTable.containsKey(signifier)) {
                    slotTable[signifier]!!.value
                } else {
                    langThrow(signifier.ctx, IdentifierNotFound(signifier))
                }
            }
            else -> langThrow(signifier.ctx, TypeSystemBug)
        }
}
