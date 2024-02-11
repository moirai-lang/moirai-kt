package moirai.semantics.core

data class FeatureSupport(
    val forEachBlock: Boolean,
    val recordField: Boolean,
    val paramType: Boolean,
    val returnType: Boolean,
    val typeArg: Boolean
)

val immutableOrderedFeatureSupport = FeatureSupport(
    forEachBlock = true,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

val immutableUnorderedFeatureSupport = FeatureSupport(
    forEachBlock = false,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

val noFeatureSupport = FeatureSupport(
    forEachBlock = false,
    recordField = false,
    paramType = false,
    returnType = false,
    typeArg = false
)

val userTypeFeatureSupport = FeatureSupport(
    forEachBlock = false,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

val unitFeatureSupport = FeatureSupport(
    forEachBlock = false,
    recordField = false,
    paramType = true,
    returnType = true,
    typeArg = true
)