package com.tsikhe.shardscript.semantics.core

import com.tsikhe.shardscript.semantics.prelude.StringMethods

internal fun isValidStringType(symbol: Symbol): Boolean =
    when (symbol) {
        is ObjectSymbol,
        is BasicTypeSymbol -> {
            (symbol as SymbolTable).existsHere(GroundIdentifier(StringMethods.ToString.idStr))
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.originalSymbol) {
                is ParameterizedBasicTypeSymbol -> {
                    (parameterizedType as SymbolTable).existsHere(GroundIdentifier(StringMethods.ToString.idStr))
                }
                else -> false
            }
        }
        else -> false
    }

@UseExperimental(ExperimentalUnsignedTypes::class)
internal fun costExpressionFromValidStringType(symbol: Symbol): CostExpression {
    val member = when (symbol) {
        is ObjectSymbol,
        is BasicTypeSymbol -> (symbol as SymbolTable).fetchHere(GroundIdentifier(StringMethods.ToString.idStr))
        is SymbolInstantiation -> {
            when (val parameterizedType = symbol.substitutionChain.originalSymbol) {
                is ParameterizedBasicTypeSymbol -> {
                    (parameterizedType as SymbolTable).fetchHere(GroundIdentifier(StringMethods.ToString.idStr))
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