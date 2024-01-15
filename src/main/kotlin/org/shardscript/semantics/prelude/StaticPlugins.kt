package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.RandomInstantiation
import org.shardscript.semantics.infer.RangeInstantiation
import org.shardscript.semantics.infer.Substitution
import kotlin.random.Random

fun createRangePlugin(
    langNS: Scope<Symbol>,
    intType: BasicTypeSymbol,
    listType: ParameterizedBasicTypeSymbol
): ParameterizedStaticPluginSymbol {
    val rangePlugin = ParameterizedStaticPluginSymbol(
        langNS,
        Lang.rangeId,
        RangeInstantiation
    ) { args ->
        val originalLowerBound = args[0] as IntValue
        val originalUpperBound = args[1] as IntValue
        if (originalLowerBound.canonicalForm < originalUpperBound.canonicalForm) {
            val lowerBound = originalLowerBound.canonicalForm
            val upperBound = originalUpperBound.canonicalForm - 1
            val list: MutableList<Value> = (lowerBound..upperBound).toList().map {
                IntValue(it)
            }.toMutableList()
            ListValue(ImmutableBasicTypeMode, list)
        } else {
            val lowerBound = originalUpperBound.canonicalForm + 1
            val upperBound = originalLowerBound.canonicalForm
            val list: MutableList<Value> = (lowerBound..upperBound).toList().map {
                IntValue(it)
            }.toMutableList()
            list.reverse()
            ListValue(ImmutableBasicTypeMode, list)
        }
    }

    val rangeTypeParam = ImmutableFinTypeParameter(rangePlugin, Lang.rangeTypeId)
    rangePlugin.define(Lang.rangeTypeId, rangeTypeParam)
    rangePlugin.typeParams = listOf(rangeTypeParam)

    val beginFormalParamId = Identifier(NotInSource, "begin")
    val beginFormalParam = FunctionFormalParameterSymbol(rangePlugin, beginFormalParamId, intType)
    rangePlugin.define(beginFormalParamId, beginFormalParam)

    val endFormalParamId = Identifier(NotInSource, "end")
    val endFormalParam = FunctionFormalParameterSymbol(rangePlugin, endFormalParamId, intType)
    rangePlugin.define(endFormalParamId, endFormalParam)
    rangePlugin.formalParams = listOf(beginFormalParam, endFormalParam)

    val outputSubstitution = Substitution(listType.typeParams, listOf(intType, rangeTypeParam))
    val outputType = outputSubstitution.apply(listType)
    rangePlugin.returnType = outputType

    rangePlugin.costExpression = rangeTypeParam
    return rangePlugin
}

fun createRandomPlugin(
    langNS: Scope<Symbol>,
    fin: CostExpression
): ParameterizedStaticPluginSymbol {
    val randomPlugin = ParameterizedStaticPluginSymbol(
        langNS,
        Lang.randomId,
        RandomInstantiation
    ) { args ->
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

    val randomTypeParam = StandardTypeParameter(randomPlugin, Lang.randomTypeId)
    randomPlugin.define(Lang.randomTypeId, randomTypeParam)
    randomPlugin.typeParams = listOf(randomTypeParam)

    val beginFormalParamId = Identifier(NotInSource, "offset")
    val beginFormalParam = FunctionFormalParameterSymbol(randomPlugin, beginFormalParamId, randomTypeParam)
    randomPlugin.define(beginFormalParamId, beginFormalParam)

    val endFormalParamId = Identifier(NotInSource, "limit")
    val endFormalParam = FunctionFormalParameterSymbol(randomPlugin, endFormalParamId, randomTypeParam)
    randomPlugin.define(endFormalParamId, endFormalParam)
    randomPlugin.formalParams = listOf(beginFormalParam, endFormalParam)
    randomPlugin.returnType = randomTypeParam

    randomPlugin.costExpression = fin
    return randomPlugin
}