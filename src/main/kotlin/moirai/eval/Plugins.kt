package moirai.eval

import moirai.semantics.core.*
import moirai.semantics.prelude.*
import kotlin.random.Random

data class GroundMemberPlugin(private val plugin: (Value, List<Value>, Map<TypeParameter, Type>) -> Value) {
    fun invoke(t: Value, args: List<Value>, subs: Map<TypeParameter, Type>): Value = plugin(t, args, subs)
}

data class PlatformField(private val accessor: (Value) -> Value) {
    fun invoke(t: Value): Value = accessor(t)
}

object Plugins {
    val groundMemberPlugins: Map<GroundMemberPluginSymbol, GroundMemberPlugin> = mapOf(
        IntegerMathOpMembers.add to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalAdd(args.first())
        },
        IntegerMathOpMembers.sub to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalSub(args.first())
        },
        IntegerMathOpMembers.mul to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalMul(args.first())
        },
        IntegerMathOpMembers.div to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalDiv(args.first())
        },
        IntegerMathOpMembers.mod to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalMod(args.first())
        },
        IntegerMathOpMembers.negate to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalNegate()
        },
        IntegerOrderOpMembers.greaterThan to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as OrderValue).evalGreaterThan(args.first())
        },
        IntegerOrderOpMembers.greaterThanOrEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as OrderValue).evalGreaterThanOrEquals(args.first())
        },
        IntegerOrderOpMembers.lessThan to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as OrderValue).evalLessThan(args.first())
        },
        IntegerOrderOpMembers.lessThanOrEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as OrderValue).evalLessThanOrEquals(args.first())
        },
        IntegerEqualityOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        IntegerEqualityOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        BooleanEqualityOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        BooleanEqualityOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        CharEqualityOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        CharEqualityOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        ValueLogicalOpMembers.and to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as LogicalValue).evalAnd(args.first())
        },
        ValueLogicalOpMembers.or to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as LogicalValue).evalOr(args.first())
        },
        ValueLogicalOpMembers.not to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as LogicalValue).evalNot()
        },
        ListTypes.removeAtFunction to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as ListValue).evalRemoveAt(args.first())
        },
        StringOpMembers.integerToStringMember to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as IntValue).evalToString()
        },
        StringOpMembers.unitToStringMember to GroundMemberPlugin { _: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            UnitValue.evalToString()
        },
        StringOpMembers.booleanToStringMember to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as BooleanValue).evalToString()
        },
        StringOpMembers.charToStringMember to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as CharValue).evalToString()
        }
    )

    val parameterizedMemberPlugins: Map<ParameterizedMemberPluginSymbol, GroundMemberPlugin> = mapOf(
        StringOpMembers.decimalToStringMember to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as DecimalValue).evalToString()
        },
        StringOpMembers.stringToStringMember to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as StringValue).evalToString()
        },
        StringOpMembers.toCharArray to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as StringValue).evalToCharArray()
        },
        StringOpMembers.add to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as StringValue).evalAdd(args.first())
        },
        StringOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        StringOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        DecimalMathOpMembers.add to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalAdd(args.first())
        },
        DecimalMathOpMembers.sub to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalSub(args.first())
        },
        DecimalMathOpMembers.mul to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalMul(args.first())
        },
        DecimalMathOpMembers.div to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalDiv(args.first())
        },
        DecimalMathOpMembers.mod to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalMod(args.first())
        },
        DecimalMathOpMembers.negate to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as MathValue).evalNegate()
        },
        DecimalOrderOpMembers.greaterThan to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as OrderValue).evalGreaterThan(args.first())
        },
        DecimalOrderOpMembers.greaterThanOrEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as OrderValue).evalGreaterThanOrEquals(args.first())
        },
        DecimalOrderOpMembers.lessThan to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as OrderValue).evalLessThan(args.first())
        },
        DecimalOrderOpMembers.lessThanOrEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as OrderValue).evalLessThanOrEquals(args.first())
        },
        DecimalEqualityOpMembers.equals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        DecimalEqualityOpMembers.notEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        DecimalMethodMembers.ascribe to GroundMemberPlugin { t: Value, _: List<Value>, subs: Map<TypeParameter, Type> ->
            (t as DecimalValue).evalAscribe(subs)
        },
        EqualityMembers.listEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.listNotEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.mutableListEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.mutableListNotEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.dictionaryEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.dictionaryNotEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.mutableDictionaryEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.mutableDictionaryNotEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.setEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.setNotEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        EqualityMembers.mutableSetEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalEquals(args.first())
        },
        EqualityMembers.mutableSetNotEquals to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as EqualityValue).evalNotEquals(args.first())
        },
        SetTypes.setContains to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as SetValue).evalContains(args.first())
        },
        SetTypes.mutableSetContains to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as SetValue).evalContains(args.first())
        },
        SetTypes.mutableSetAdd to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as SetValue).evalAdd(args.first())
        },
        SetTypes.mutableSetRemove to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as SetValue).evalRemove(args.first())
        },
        SetTypes.mutableSetToSet to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as SetValue).evalToSet()
        },
        ListTypes.listGet to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as ListValue).evalGet(args.first())
        },
        ListTypes.mutableListGet to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as ListValue).evalGet(args.first())
        },
        ListTypes.mutableListAdd to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as ListValue).evalAdd(args.first())
        },
        ListTypes.mutableListSet to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as ListValue).evalSet(args.first(), args[1])
        },
        ListTypes.mutableListToList to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
            (t as ListValue).evalToList()
        },
        DictionaryTypes.getFunction to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as DictionaryValue).evalGet(args.first())
        },
        DictionaryTypes.mutableGetFunction to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as DictionaryValue).evalGet(args.first())
        },
        DictionaryTypes.containsFunction to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as DictionaryValue).evalContains(args.first())
        },
        DictionaryTypes.mutableContainsFunction to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as DictionaryValue).evalContains(args.first())
        },
        DictionaryTypes.setFunction to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as DictionaryValue).evalSet(args.first(), args[1])
        },
        DictionaryTypes.removeFunction to GroundMemberPlugin { t: Value, args: List<Value>, _: Map<TypeParameter, Type> ->
            (t as DictionaryValue).evalRemove(args.first())
        },
        DictionaryTypes.mutableDictionaryToDictionary to GroundMemberPlugin { t: Value, _: List<Value>, _: Map<TypeParameter, Type> ->
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
                val fin = list.size.toLong()
                val substitutions = mapOf<TypeParameter, Type>(Lang.listFinTypeParam to Fin(fin))
                ListValue(list, substitutions, fin, false)
            } else {
                val lowerBound = originalUpperBound.canonicalForm + 1
                val upperBound = originalLowerBound.canonicalForm
                val list: MutableList<Value> = (lowerBound..upperBound).toList().map {
                    IntValue(it)
                }.toMutableList()
                list.reverse()
                val fin = list.size.toLong()
                val substitutions = mapOf<TypeParameter, Type>(Lang.listFinTypeParam to Fin(fin))
                ListValue(list, substitutions, fin, false)
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