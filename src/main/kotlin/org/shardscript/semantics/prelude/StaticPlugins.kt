package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.RandomInstantiation
import org.shardscript.semantics.infer.RangeInstantiation
import org.shardscript.semantics.infer.Substitution

object StaticPlugins {
    val rangePlugin = createRangePlugin()
    val randomPlugin = createRandomPlugin()

    private fun createRangePlugin(): ParameterizedStaticPluginSymbol {
        val rangePlugin = ParameterizedStaticPluginSymbol(
            Lang.prelude,
            Lang.rangeId,
            RangeInstantiation
        )

        val rangeTypeParam = ImmutableFinTypeParameter("${Lang.rangeId.name}.${Lang.rangeTypeId.name}", Lang.rangeTypeId)
        rangePlugin.defineType(Lang.rangeTypeId, rangeTypeParam)
        rangePlugin.typeParams = listOf(rangeTypeParam)

        val beginFormalParamId = Identifier(NotInSource, "begin")
        val beginFormalParam = FunctionFormalParameterSymbol(rangePlugin, beginFormalParamId, Lang.intType)
        rangePlugin.define(beginFormalParamId, beginFormalParam)

        val endFormalParamId = Identifier(NotInSource, "end")
        val endFormalParam = FunctionFormalParameterSymbol(rangePlugin, endFormalParamId, Lang.intType)
        rangePlugin.define(endFormalParamId, endFormalParam)
        rangePlugin.formalParams = listOf(beginFormalParam, endFormalParam)

        val outputSubstitution = Substitution(Lang.listType.typeParams, listOf(Lang.intType, rangeTypeParam))
        val outputType = outputSubstitution.apply(Lang.listType)
        rangePlugin.returnType = outputType

        rangePlugin.costExpression = rangeTypeParam
        return rangePlugin
    }

    private fun createRandomPlugin(): ParameterizedStaticPluginSymbol {
        val randomPlugin = ParameterizedStaticPluginSymbol(
            Lang.prelude,
            Lang.randomId,
            RandomInstantiation
        )

        val randomTypeParam = StandardTypeParameter("${Lang.randomId.name}.${Lang.randomTypeId.name}", Lang.randomTypeId)
        randomPlugin.defineType(Lang.randomTypeId, randomTypeParam)
        randomPlugin.typeParams = listOf(randomTypeParam)

        val beginFormalParamId = Identifier(NotInSource, "offset")
        val beginFormalParam = FunctionFormalParameterSymbol(randomPlugin, beginFormalParamId, randomTypeParam)
        randomPlugin.define(beginFormalParamId, beginFormalParam)

        val endFormalParamId = Identifier(NotInSource, "limit")
        val endFormalParam = FunctionFormalParameterSymbol(randomPlugin, endFormalParamId, randomTypeParam)
        randomPlugin.define(endFormalParamId, endFormalParam)
        randomPlugin.formalParams = listOf(beginFormalParam, endFormalParam)
        randomPlugin.returnType = randomTypeParam

        randomPlugin.costExpression = ConstantFin
        return randomPlugin
    }
}