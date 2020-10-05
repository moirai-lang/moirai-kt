package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*

internal fun createSuccessType(
    resultType: Scope<Symbol>,
    successTypeParam: StandardTypeParameter
): ParameterizedRecordTypeSymbol {
    val successType = ParameterizedRecordTypeSymbol(
        resultType,
        Lang.successId,
        noFeatureSupport
    )
    successType.typeParams = listOf(successTypeParam)
    val successField = FieldSymbol(successType, Lang.successFieldId, successTypeParam, mutable = false)
    successType.fields = listOf(successField)
    return successType
}

internal fun createFailureType(
    resultType: Scope<Symbol>,
    failureTypeParam: StandardTypeParameter
): ParameterizedRecordTypeSymbol {
    val failureType = ParameterizedRecordTypeSymbol(
        resultType,
        Lang.failureId,
        noFeatureSupport
    )
    failureType.typeParams = listOf(failureTypeParam)
    val failureField = FieldSymbol(failureType, Lang.failureFieldId, failureTypeParam, mutable = false)
    failureType.fields = listOf(failureField)
    return failureType
}

internal fun createResultType(
    langNS: Namespace
): ParameterizedCoproductSymbol {
    val resultType = ParameterizedCoproductSymbol(
        langNS,
        Lang.resultId,
        coproductFeatureSupport
    )
    val resultSuccessTypeParam = StandardTypeParameter(resultType, Lang.resultSuccessTypeParamId)
    resultType.define(Lang.resultSuccessTypeParamId, resultSuccessTypeParam)
    val resultFailureTypeParam = StandardTypeParameter(resultType, Lang.resultFailureTypeParamId)
    resultType.define(Lang.resultFailureTypeParamId, resultFailureTypeParam)
    resultType.typeParams = listOf(resultFailureTypeParam, resultSuccessTypeParam)

    val successType = createSuccessType(resultType, resultSuccessTypeParam)

    val failureType = createFailureType(resultType, resultFailureTypeParam)

    resultType.define(successType.gid, successType)
    resultType.define(failureType.gid, failureType)
    resultType.alternatives = listOf(failureType, successType)
    resultType.sourceType = resultSuccessTypeParam
    resultType.replaceParameters = { newSourceType, existingArgs ->
        listOf(newSourceType, existingArgs[1])
    }

    return resultType
}