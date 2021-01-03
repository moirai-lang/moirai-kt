package com.tsikhe.shardscript.semantics.core

import kotlin.math.sqrt

interface CostExpression {
    fun <R> accept(visitor: CostExpressionVisitor<R>): R
}

interface CostExpressionVisitor<R> {
    fun visit(costExpression: ImmutableOmicronTypeParameter): R
    fun visit(costExpression: MutableOmicronTypeParameter): R
    fun visit(costExpression: OmicronTypeSymbol): R
    fun visit(costExpression: SumCostExpression): R
    fun visit(costExpression: ProductCostExpression): R
    fun visit(costExpression: MaxCostExpression): R
}

class EvalCostExpressionVisitor(val architecture: Architecture): CostExpressionVisitor<Long> {
    init {
        if(architecture.costUpperLimit > sqrt(Long.MAX_VALUE.toDouble()).toLong() - 2) {
            langThrow(InvalidCostUpperLimit)
        }
    }

    override fun visit(costExpression: ImmutableOmicronTypeParameter): Long {
        langThrow(CalculateCostFailed)
    }

    override fun visit(costExpression: MutableOmicronTypeParameter): Long {
        langThrow(CalculateCostFailed)
    }

    override fun visit(costExpression: OmicronTypeSymbol): Long {
        if (costExpression.magnitude <= 0L) {
            langThrow(NegativeOmicron)
        }
        return costExpression.magnitude
    }

    override fun visit(costExpression: SumCostExpression): Long {
        val children = costExpression.children.map { it.accept(this) }
        if (children.isEmpty()) {
            langThrow(TypeSystemBug)
        }
        val res = children.reduce { acc, next ->
            if(acc > architecture.costUpperLimit || next > architecture.costUpperLimit) {
                // Prevent Overflow
                architecture.costUpperLimit + 1
            } else {
                acc + next
            }
        }
        if (res <= 0L) {
            langThrow(NegativeOmicron)
        }
        return res
    }

    override fun visit(costExpression: ProductCostExpression): Long {
        val children = costExpression.children.map { it.accept(this) }
        if (children.isEmpty()) {
            langThrow(TypeSystemBug)
        }
        val res = children.reduce { acc, next ->
            if(acc > architecture.costUpperLimit || next > architecture.costUpperLimit) {
                // Prevent Overflow
                architecture.costUpperLimit + 1
            } else {
                acc * next
            }
        }
        if (res <= 0L) {
            langThrow(NegativeOmicron)
        }
        return res
    }

    override fun visit(costExpression: MaxCostExpression): Long {
        val children = costExpression.children.map { it.accept(this) }
        if (children.isEmpty()) {
            langThrow(TypeSystemBug)
        }
        val res = children.reduce { acc, next ->
            if(acc > architecture.costUpperLimit || next > architecture.costUpperLimit) {
                // Prevent Overflow
                architecture.costUpperLimit + 1
            } else {
                acc.coerceAtLeast(next)
            }
        }
        if (res <= 0L) {
            langThrow(NegativeOmicron)
        }
        return res
    }

}

object CommonCostExpressions {
    val defaultMultiplier = OmicronTypeSymbol(1L)
    val twoPass = OmicronTypeSymbol(2L)
}

object CanEvalCostExpressionVisitor: CostExpressionVisitor<Boolean> {
    override fun visit(costExpression: ImmutableOmicronTypeParameter): Boolean {
        return false
    }

    override fun visit(costExpression: MutableOmicronTypeParameter): Boolean {
        return false
    }

    override fun visit(costExpression: OmicronTypeSymbol): Boolean {
        return true
    }

    override fun visit(costExpression: SumCostExpression): Boolean {
        return costExpression.children.all { it.accept(this) }
    }

    override fun visit(costExpression: ProductCostExpression): Boolean {
        return costExpression.children.all { it.accept(this) }
    }

    override fun visit(costExpression: MaxCostExpression): Boolean {
        return costExpression.children.all { it.accept(this) }
    }
}

object ValidateCostExpressionVisitor: CostExpressionVisitor<Unit> {
    override fun visit(costExpression: ImmutableOmicronTypeParameter) {
        langThrow(TypeSystemBug)
    }

    override fun visit(costExpression: MutableOmicronTypeParameter) {
        langThrow(TypeSystemBug)
    }

    override fun visit(costExpression: OmicronTypeSymbol) = Unit

    override fun visit(costExpression: SumCostExpression) {
        costExpression.children.forEach { it.accept(this) }
    }

    override fun visit(costExpression: ProductCostExpression) {
        costExpression.children.forEach { it.accept(this) }
    }

    override fun visit(costExpression: MaxCostExpression) {
        costExpression.children.forEach { it.accept(this) }
    }
}