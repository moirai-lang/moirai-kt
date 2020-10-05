package com.tsikhe.shardscript.semantics.core

import java.math.BigInteger

interface CostExpression

internal fun evalCostExpression(costExpression: CostExpression): BigInteger =
    when (costExpression) {
        is OmicronTypeSymbol -> {
            if (costExpression.magnitude <= BigInteger.ZERO) {
                langThrow(NegativeOmicron)
            }
            costExpression.magnitude
        }
        is ImmutableOmicronTypeParameter -> langThrow(CalculateCostFailed)
        is MutableOmicronTypeParameter -> langThrow(CalculateCostFailed)
        is SumCostExpression -> {
            val children = costExpression.children.map { evalCostExpression(it) }
            if (children.isEmpty()) {
                langThrow(TypeSystemBug)
            }
            val res = children.reduce { acc, next -> acc + next }
            if (res <= BigInteger.ZERO) {
                langThrow(NegativeOmicron)
            }
            res
        }
        is ProductCostExpression -> {
            val children = costExpression.children.map { evalCostExpression(it) }
            if (children.isEmpty()) {
                langThrow(TypeSystemBug)
            }
            val res = children.reduce { acc, next -> acc * next }
            if (res <= BigInteger.ZERO) {
                langThrow(NegativeOmicron)
            }
            res
        }
        is MaxCostExpression -> {
            val children = costExpression.children.map { evalCostExpression(it) }
            if (children.isEmpty()) {
                langThrow(TypeSystemBug)
            }
            val res = children.reduce { acc, next -> acc.coerceAtLeast(next) }
            if (res <= BigInteger.ZERO) {
                langThrow(NegativeOmicron)
            }
            res
        }
        else -> langThrow(TypeSystemBug)
    }

object CommonCostExpressions {
    val defaultMultiplier = OmicronTypeSymbol(BigInteger.ONE)
    val twoPass = OmicronTypeSymbol(BigInteger.TWO)
}

fun canEvalImmediately(costExpression: CostExpression): Boolean =
    when (costExpression) {
        is ImmutableOmicronTypeParameter -> false
        is MutableOmicronTypeParameter -> false
        is OmicronTypeSymbol -> true
        is SumCostExpression -> costExpression.children.all { canEvalImmediately(it) }
        is ProductCostExpression -> costExpression.children.all { canEvalImmediately(it) }
        is MaxCostExpression -> costExpression.children.all { canEvalImmediately(it) }
        else -> langThrow(TypeSystemBug)
    }

internal fun validateCostExpression(costExpression: CostExpression) {
    when (costExpression) {
        is ImmutableOmicronTypeParameter -> {
            langThrow(TypeSystemBug)
        }
        is MutableOmicronTypeParameter -> {
            langThrow(TypeSystemBug)
        }
        is OmicronTypeSymbol -> Unit
        is SumCostExpression -> costExpression.children.forEach { validateCostExpression(it) }
        is ProductCostExpression -> costExpression.children.forEach { validateCostExpression(it) }
        is MaxCostExpression -> costExpression.children.forEach { validateCostExpression(it) }
        else -> Unit
    }
}