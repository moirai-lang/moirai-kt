package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.DecimalInstantiation

fun intType(
    numericId: Identifier,
    booleanType: BasicTypeSymbol,
    langNS: Scope<Symbol>,
    filters: Set<String>
): BasicTypeSymbol {
    val intType = BasicTypeSymbol(
        langNS,
        numericId
    )
    val constantFin = ConstantFinTypeSymbol
    IntegerMathOpMembers.members(intType, constantFin).forEach { (name, plugin) ->
        if (!filters.contains(name)) {
            intType.define(Identifier(NotInSource, name), plugin)
        }
    }
    IntegerOrderOpMembers.members(intType, constantFin, booleanType).forEach { (name, plugin) ->
        if (!filters.contains(name)) {
            intType.define(Identifier(NotInSource, name), plugin)
        }
    }
    ValueEqualityOpMembers.members(intType, constantFin, booleanType).forEach { (name, plugin) ->
        if (!filters.contains(name)) {
            intType.define(Identifier(NotInSource, name), plugin)
        }
    }
    return intType
}

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
