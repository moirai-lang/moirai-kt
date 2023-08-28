package org.shardscript.semantics.core

import org.shardscript.semantics.prelude.StringMethods

fun isValidStringType(symbol: Symbol): Boolean =
    when (symbol) {
        is ObjectSymbol -> {
            symbol.existsHere(Identifier(StringMethods.ToString.idStr))
        }
        is BasicTypeSymbol -> {
            symbol.existsHere(Identifier(StringMethods.ToString.idStr))
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.originalSymbol) {
                is ParameterizedBasicTypeSymbol -> {
                    parameterizedType.existsHere(Identifier(StringMethods.ToString.idStr))
                }
                else -> false
            }
        }
        else -> false
    }

@UseExperimental(ExperimentalUnsignedTypes::class)
fun costExpressionFromValidStringType(symbol: Symbol): CostExpression {
    val member = when (symbol) {
        is ObjectSymbol -> {
            symbol.fetchHere(Identifier(StringMethods.ToString.idStr))
        }
        is BasicTypeSymbol -> {
            symbol.fetchHere(Identifier(StringMethods.ToString.idStr))
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.originalSymbol) {
                is ParameterizedBasicTypeSymbol -> {
                    parameterizedType.fetchHere(Identifier(StringMethods.ToString.idStr))
                }
                else -> langThrow(TypeSystemBug)
            }
        }
        else -> langThrow(TypeSystemBug)
    }
    return when (member) {
        is GroundMemberPluginSymbol -> {
            member.costExpression
        }
        is ParameterizedMemberPluginSymbol -> {
            member.costExpression
        }
        else -> langThrow(TypeSystemBug)
    }
}