package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*

internal fun createRightType(
    eitherType: Scope<Symbol>,
    rightTypeParam: StandardTypeParameter
): ParameterizedRecordTypeSymbol {
    val rightType = ParameterizedRecordTypeSymbol(
        eitherType,
        Lang.rightId,
        noFeatureSupport
    )
    rightType.typeParams = listOf(rightTypeParam)
    val rightField = FieldSymbol(rightType, Lang.rightFieldId, rightTypeParam, mutable = false)
    rightType.fields = listOf(rightField)
    return rightType
}

internal fun createLeftType(
    eitherType: Scope<Symbol>,
    leftTypeParam: StandardTypeParameter
): ParameterizedRecordTypeSymbol {
    val leftType = ParameterizedRecordTypeSymbol(
        eitherType,
        Lang.leftId,
        noFeatureSupport
    )
    leftType.typeParams = listOf(leftTypeParam)
    val leftField = FieldSymbol(leftType, Lang.leftFieldId, leftTypeParam, mutable = false)
    leftType.fields = listOf(leftField)
    return leftType
}

internal fun createEitherType(
    langNS: Namespace
): ParameterizedCoproductSymbol {
    val eitherType = ParameterizedCoproductSymbol(
        langNS,
        Lang.eitherId,
        coproductFeatureSupport
    )
    val eitherRightTypeParam = StandardTypeParameter(eitherType, Lang.eitherRightTypeParamId)
    eitherType.define(Lang.eitherRightTypeParamId, eitherRightTypeParam)
    val eitherLeftTypeParam = StandardTypeParameter(eitherType, Lang.eitherLeftTypeParamId)
    eitherType.define(Lang.eitherLeftTypeParamId, eitherLeftTypeParam)
    eitherType.typeParams = listOf(eitherLeftTypeParam, eitherRightTypeParam)

    val rightType = createRightType(eitherType, eitherRightTypeParam)
    val leftType = createLeftType(eitherType, eitherLeftTypeParam)

    eitherType.define(rightType.gid, rightType)
    eitherType.define(leftType.gid, leftType)
    eitherType.alternatives = listOf(leftType, rightType)
    eitherType.sourceType = eitherRightTypeParam
    eitherType.replaceParameters = { newSourceType, existingArgs ->
        listOf(existingArgs[0], newSourceType)
    }

    return eitherType
}
