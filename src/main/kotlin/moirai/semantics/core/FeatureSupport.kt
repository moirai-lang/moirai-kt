package moirai.semantics.core

internal data class FeatureSupport(
    val forEachBlock: Boolean,
    val recordField: Boolean,
    val paramType: Boolean,
    val returnType: Boolean,
    val typeArg: Boolean
)

internal val immutableOrderedFeatureSupport = FeatureSupport(
    forEachBlock = true,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

internal val immutableUnorderedFeatureSupport = FeatureSupport(
    forEachBlock = false,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

internal val noFeatureSupport = FeatureSupport(
    forEachBlock = false,
    recordField = false,
    paramType = false,
    returnType = false,
    typeArg = false
)

internal val userTypeFeatureSupport = FeatureSupport(
    forEachBlock = false,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

internal val unitFeatureSupport = FeatureSupport(
    forEachBlock = false,
    recordField = false,
    paramType = true,
    returnType = true,
    typeArg = true
)