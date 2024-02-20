package moirai.semantics.core

import moirai.semantics.prelude.StringMethods

internal fun isValidStringType(type: Type): Boolean =
    when (type) {
        is ObjectType -> {
            false
        }
        is PlatformObjectType -> {
            type.existsHere(Identifier(NotInSource, StringMethods.ToString.idStr))
        }
        is BasicType -> {
            type.existsHere(Identifier(NotInSource, StringMethods.ToString.idStr))
        }
        is TypeInstantiation -> {
            when (val parameterizedType = type.substitutionChain.terminus) {
                is ParameterizedBasicType -> {
                    parameterizedType.existsHere(Identifier(NotInSource, StringMethods.ToString.idStr))
                }
                else -> false
            }
        }
        else -> false
    }

internal fun costExpressionFromValidStringType(type: Type): CostExpression {
    val member = when (type) {
        is PlatformObjectType -> {
            type.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr))
        }
        is BasicType -> {
            type.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr))
        }
        is TypeInstantiation -> {
            when (val parameterizedType = type.substitutionChain.terminus) {
                is ParameterizedBasicType -> {
                    parameterizedType.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr))
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