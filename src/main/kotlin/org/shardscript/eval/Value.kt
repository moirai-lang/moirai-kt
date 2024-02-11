package org.shardscript.eval

import org.shardscript.semantics.core.*
import org.shardscript.semantics.prelude.*
import java.math.BigDecimal

sealed class Value

data object UnitValue : Value() {
    fun evalToString(): Value {
        return StringValue(Lang.unitId.name)
    }
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

class SumObjectValue(
    val symbol: PlatformSumObjectType
) : Value() {
    val path = getQualifiedName(symbol)

    override fun equals(other: Any?): Boolean {
        if (other != null && other is SumObjectValue) {
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

class RecordValue(type: Type, val fields: ValueTable, val substitutions: Map<TypeParameter, Type>) : Value() {
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

class SumRecordValue(val instantiation: TypeInstantiation, val type: PlatformSumRecordType, val fields: ValueTable, val substitutions: Map<TypeParameter, Type>) : Value() {
    lateinit var scope: Scope
    val path = getQualifiedName(type)

    override fun equals(other: Any?): Boolean {
        if (other != null && other is SumRecordValue) {
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
    fun evalAscribe(subs: Map<TypeParameter, Type>): Value {
        if (subs.size != 2) {
            langThrow(NotInSource, TypeSystemBug)
        }

        val sub = subs.filter {
            (it.key as FinTypeParameter).qualifiedName == Lang.ascribeFinTypeParameterQualifiedName
        }.values.first()
        if (sub is Fin) {
            val s = canonicalForm.toString()
            if (s.length > sub.magnitude) {
                langThrow(NotInSource, RuntimeFinViolation(sub.magnitude, s.length.toLong()))
            }
            return DecimalValue(canonicalForm)
        } else {
            langThrow(NotInSource, TypeSystemBug)
        }
    }
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
        val fin = Fin(canonicalForm.length.toLong())
        return ListValue(elements.toMutableList(), mapOf(Lang.listFinTypeParam to fin), fin.magnitude, false)
    }

    fun evalToString(): Value = StringValue(canonicalForm)

    fun evalAdd(arg: Value): Value {
        val other = arg as StringValue
        return StringValue(canonicalForm + other.canonicalForm)
    }
}

data class ListValue(
    val elements: MutableList<Value>,
    val substitutions: Map<TypeParameter, Type>,
    val fin: Long,
    val mutable: Boolean
) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(elements.size)
    }

    fun evalToList(): Value {
        if (mutable) {
            return ListValue(elements.toList().toMutableList(), substitutions, fin, false)
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

data class SetValue(
    val elements: MutableSet<Value>,
    val substitutions: Map<TypeParameter, Type>,
    val fin: Long,
    val mutable: Boolean
) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(elements.size)
    }

    fun evalToSet(): Value {
        if (mutable) {
            return SetValue(elements.toSet().toMutableSet(), substitutions, fin, false)
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
    val substitutions: Map<TypeParameter, Type>,
    val fin: Long,
    val mutable: Boolean
) : Value(), EqualityValue {
    fun fieldSize(): Value {
        return IntValue(dictionary.size)
    }

    fun evalToDictionary(): Value {
        if (mutable) {
            return DictionaryValue(dictionary.toMap().toMutableMap(), substitutions, fin, false)
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
