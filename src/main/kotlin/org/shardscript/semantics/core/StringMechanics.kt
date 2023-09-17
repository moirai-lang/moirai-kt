package org.shardscript.semantics.core

import org.shardscript.semantics.prelude.StringMethods

fun isValidStringType(type: Type): Boolean =
    when (type) {
        is ObjectSymbol -> {
            type.existsHere(Identifier(StringMethods.ToString.idStr))
        }
        is BasicTypeSymbol -> {
            type.existsHere(Identifier(StringMethods.ToString.idStr))
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = type.substitutionChain.originalSymbol) {
                is ParameterizedBasicTypeSymbol -> {
                    parameterizedType.existsHere(Identifier(StringMethods.ToString.idStr))
                }
                else -> false
            }
        }
        else -> false
    }

fun costExpressionFromValidStringType(type: Type): CostExpression {
    val member = when (type) {
        is ObjectSymbol -> {
            type.fetchHere(Identifier(StringMethods.ToString.idStr))
        }
        is BasicTypeSymbol -> {
            type.fetchHere(Identifier(StringMethods.ToString.idStr))
        }
        is SymbolInstantiation -> {
            when (val parameterizedType = type.substitutionChain.originalSymbol) {
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