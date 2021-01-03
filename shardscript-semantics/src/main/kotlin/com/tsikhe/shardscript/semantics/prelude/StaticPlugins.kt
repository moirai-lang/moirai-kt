package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.infer.RandomInstantiation
import com.tsikhe.shardscript.semantics.infer.RangeInstantiation
import com.tsikhe.shardscript.semantics.infer.Substitution
import kotlin.random.Random

fun createRangePlugin(
    langNS: Namespace,
    intType: BasicTypeSymbol,
    listType: ParameterizedBasicTypeSymbol
): ParameterizedStaticPluginSymbol {
    val rangePlugin = ParameterizedStaticPluginSymbol(
        langNS,
        Lang.rangeId,
        RangeInstantiation,
    { args ->
        val originalLowerBound = args[0] as IntValue
        val originalUpperBound = args[1] as IntValue
        if (originalLowerBound.canonicalForm < originalUpperBound.canonicalForm) {
            val lowerBound = originalLowerBound.canonicalForm
            val upperBound = originalUpperBound.canonicalForm - 1
            val list = (lowerBound..upperBound).toList().map {
                IntValue(it) as Value
            }.toMutableList()
            ListValue(ImmutableBasicTypeMode, list)
        } else {
            val lowerBound = originalUpperBound.canonicalForm + 1
            val upperBound = originalLowerBound.canonicalForm
            val list = (lowerBound..upperBound).toList().map {
                IntValue(it) as Value
            }.toMutableList()
            list.reverse()
            ListValue(ImmutableBasicTypeMode, list)
        }
    })

    val rangeTypeParam = ImmutableOmicronTypeParameter(rangePlugin, Lang.rangeTypeId)
    rangePlugin.define(Lang.rangeTypeId, rangeTypeParam)
    rangePlugin.typeParams = listOf(rangeTypeParam)

    val beginFormalParamId = Identifier("begin")
    val beginFormalParam = FunctionFormalParameterSymbol(rangePlugin, beginFormalParamId, intType)
    rangePlugin.define(beginFormalParamId, beginFormalParam)

    val endFormalParamId = Identifier("end")
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
    langNS: Namespace,
    omicron: CostExpression
): ParameterizedStaticPluginSymbol {
    val randomPlugin = ParameterizedStaticPluginSymbol(
        langNS,
        Lang.randomId,
        RandomInstantiation,
    { args ->
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
                    val tempInclusive = lowerBoundInclusive
                    lowerBoundInclusive = upperBoundInclusive
                    upperBoundInclusive = tempInclusive
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
                var lowerBound = (first as LongValue).canonicalForm
                var upperBound = (args[1] as LongValue).canonicalForm
                var lowerBoundInclusive = true
                var upperBoundInclusive = false

                if (lowerBound > upperBound) {
                    val temp = lowerBound
                    lowerBound = upperBound
                    upperBound = temp
                    val tempInclusive = lowerBoundInclusive
                    lowerBoundInclusive = upperBoundInclusive
                    upperBoundInclusive = tempInclusive
                }

                var offset = 0L
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

                var res = Random.nextLong(lowerBound, upperBound)
                res += offset
                LongValue(res)
            }
        }
    })

    val randomTypeParam = StandardTypeParameter(randomPlugin, Lang.randomTypeId)
    randomPlugin.define(Lang.randomTypeId, randomTypeParam)
    randomPlugin.typeParams = listOf(randomTypeParam)

    val beginFormalParamId = Identifier("offset")
    val beginFormalParam = FunctionFormalParameterSymbol(randomPlugin, beginFormalParamId, randomTypeParam)
    randomPlugin.define(beginFormalParamId, beginFormalParam)

    val endFormalParamId = Identifier("limit")
    val endFormalParam = FunctionFormalParameterSymbol(randomPlugin, endFormalParamId, randomTypeParam)
    randomPlugin.define(endFormalParamId, endFormalParam)
    randomPlugin.formalParams = listOf(beginFormalParam, endFormalParam)
    randomPlugin.returnType = randomTypeParam

    randomPlugin.costExpression = omicron
    return randomPlugin
}