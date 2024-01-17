package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.DecimalInstantiation

fun decimalType(
    numericId: Identifier,
    booleanType: BasicTypeSymbol,
    langNS: Scope<Symbol>
): ParameterizedBasicTypeSymbol {
    val decimalType = ParameterizedBasicTypeSymbol(
        langNS,
        numericId,
        DecimalInstantiation(),
        userTypeFeatureSupport
    )

    val decimalTypeParam = ImmutableFinTypeParameter(decimalType, Lang.decimalTypeId)
    decimalType.define(Lang.decimalTypeId, decimalTypeParam)
    decimalType.typeParams = listOf(decimalTypeParam)
    decimalType.modeSelector = { _ ->
        ImmutableBasicTypeMode
    }
    decimalType.fields = listOf()

    DecimalMathOpMembers.members(decimalType, decimalTypeParam).forEach { (name, plugin) ->
        decimalType.define(Identifier(NotInSource, name), plugin)
    }
    DecimalOrderOpMembers.members(decimalType, decimalTypeParam, booleanType).forEach { (name, plugin) ->
        decimalType.define(Identifier(NotInSource, name), plugin)
    }
    DecimalEqualityOpMembers.members(decimalType, decimalTypeParam, booleanType).forEach { (name, plugin) ->
        decimalType.define(Identifier(NotInSource, name), plugin)
    }
    return decimalType
}
