package com.tsikhe.shardscript.semantics.core

data class FeatureSupport(
    val switchExpr: Boolean,
    val forEachBlock: Boolean,
    val mapBlock: Boolean,
    val flatMapBlock: Boolean,
    val recordField: Boolean,
    val paramType: Boolean,
    val returnType: Boolean,
    val typeArg: Boolean
)

val immutableOrderedFeatureSupport = FeatureSupport(
    switchExpr = false,
    forEachBlock = true,
    mapBlock = true,
    flatMapBlock = true,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

val immutableUnorderedFeatureSupport = FeatureSupport(
    switchExpr = false,
    forEachBlock = false,
    mapBlock = false,
    flatMapBlock = false,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

val noFeatureSupport = FeatureSupport(
    switchExpr = false,
    forEachBlock = false,
    mapBlock = false,
    flatMapBlock = false,
    recordField = false,
    paramType = false,
    returnType = false,
    typeArg = false
)

val coproductFeatureSupport = FeatureSupport(
    switchExpr = true,
    forEachBlock = true,
    mapBlock = true,
    flatMapBlock = true,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

val userGroundCoproductFeatureSupport = FeatureSupport(
    switchExpr = true,
    forEachBlock = false,
    mapBlock = false,
    flatMapBlock = false,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)

val userTypeFeatureSupport = FeatureSupport(
    switchExpr = false,
    forEachBlock = false,
    mapBlock = false,
    flatMapBlock = false,
    recordField = true,
    paramType = true,
    returnType = true,
    typeArg = true
)
