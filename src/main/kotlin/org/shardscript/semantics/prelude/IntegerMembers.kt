package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*

fun insertIntegerConversionMembers(
    architecture: Architecture,
    integerType: BasicTypeSymbol,
    integerTypes: Map<Identifier, BasicTypeSymbol>
) {
    val filters = setOf(integerType.identifier.name)
    val constantOmicron = OmicronTypeSymbol(architecture.defaultNodeCost)
    IntegerConversionsMembers.members(integerType, constantOmicron, integerTypes).forEach { (name, plugin) ->
        if (!filters.contains(name)) {
            integerType.define(Identifier(name), plugin)
        }
    }
}

object IntegerConversionsMembers {
    fun members(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): Map<String, GroundMemberPluginSymbol> = mapOf(
        IntegerConversions.ToSigned8.idStr to pluginToSigned8(integerType, costExpression, integerTypes),
        IntegerConversions.ToSigned16.idStr to pluginToSigned16(integerType, costExpression, integerTypes),
        IntegerConversions.ToSigned32.idStr to pluginToSigned32(integerType, costExpression, integerTypes),
        IntegerConversions.ToSigned64.idStr to pluginToSigned64(integerType, costExpression, integerTypes),
        IntegerConversions.ToUnsigned8.idStr to pluginToUnsigned8(integerType, costExpression, integerTypes),
        IntegerConversions.ToUnsigned16.idStr to pluginToUnsigned16(integerType, costExpression, integerTypes),
        IntegerConversions.ToUnsigned32.idStr to pluginToUnsigned32(integerType, costExpression, integerTypes),
        IntegerConversions.ToUnsigned64.idStr to pluginToUnsigned64(integerType, costExpression, integerTypes)
    )

    private fun pluginToSigned8(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            integerType,
            Lang.sByteId,
        { t: Value, _: List<Value> ->
            try {
                (t as IntegerConversionsValue).evalToSigned8()
            } catch (_: Exception) {
                langThrow(NotInSource, RuntimeIntegerConversion)
            }
        })
        val outputType = integerTypes[res.identifier]!!
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = outputType
        return res
    }

    private fun pluginToSigned16(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            integerType,
            Lang.shortId,
        { t: Value, _: List<Value> ->
            try {
                (t as IntegerConversionsValue).evalToSigned16()
            } catch (_: Exception) {
                langThrow(NotInSource, RuntimeIntegerConversion)
            }
        })
        val outputType = integerTypes[res.identifier]!!
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = outputType
        return res
    }

    private fun pluginToSigned32(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            integerType,
            Lang.intId,
        { t: Value, _: List<Value> ->
            try {
                (t as IntegerConversionsValue).evalToSigned32()
            } catch (_: Exception) {
                langThrow(NotInSource, RuntimeIntegerConversion)
            }
        })
        val outputType = integerTypes[res.identifier]!!
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = outputType
        return res
    }

    private fun pluginToSigned64(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            integerType,
            Lang.longId,
        { t: Value, _: List<Value> ->
            try {
                (t as IntegerConversionsValue).evalToSigned64()
            } catch (_: Exception) {
                langThrow(NotInSource, RuntimeIntegerConversion)
            }
        })
        val outputType = integerTypes[res.identifier]!!
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = outputType
        return res
    }

    private fun pluginToUnsigned8(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            integerType,
            Lang.byteId,
        { t: Value, _: List<Value> ->
            try {
                (t as IntegerConversionsValue).evalToUnsigned8()
            } catch (_: Exception) {
                langThrow(NotInSource, RuntimeIntegerConversion)
            }
        })
        val outputType = integerTypes[res.identifier]!!
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = outputType
        return res
    }

    private fun pluginToUnsigned16(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            integerType,
            Lang.uShortId,
        { t: Value, _: List<Value> ->
            try {
                (t as IntegerConversionsValue).evalToUnsigned16()
            } catch (_: Exception) {
                langThrow(NotInSource, RuntimeIntegerConversion)
            }
        })
        val outputType = integerTypes[res.identifier]!!
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = outputType
        return res
    }

    private fun pluginToUnsigned32(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            integerType,
            Lang.uIntId,
        { t: Value, _: List<Value> ->
            try {
                (t as IntegerConversionsValue).evalToUnsigned32()
            } catch (_: Exception) {
                langThrow(NotInSource, RuntimeIntegerConversion)
            }
        })
        val outputType = integerTypes[res.identifier]!!
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = outputType
        return res
    }

    private fun pluginToUnsigned64(
        integerType: Scope<Symbol>,
        costExpression: CostExpression,
        integerTypes: Map<Identifier, BasicTypeSymbol>
    ): GroundMemberPluginSymbol {
        val res = GroundMemberPluginSymbol(
            integerType,
            Lang.uLongId,
        { t: Value, _: List<Value> ->
            try {
                (t as IntegerConversionsValue).evalToUnsigned64()
            } catch (_: Exception) {
                langThrow(NotInSource, RuntimeIntegerConversion)
            }
        })
        val outputType = integerTypes[res.identifier]!!
        res.costExpression = costExpression
        res.formalParams = listOf()
        res.returnType = outputType
        return res
    }
}