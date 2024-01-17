package org.shardscript.semantics.core

import kotlin.math.sqrt

sealed interface CostExpression : Type {
    fun <R> accept(visitor: CostExpressionVisitor<R>): R
}

interface CostExpressionVisitor<R> {
    fun visit(costExpression: ImmutableFinTypeParameter): R
    fun visit(costExpression: MutableFinTypeParameter): R
    fun visit(costExpression: FinTypeSymbol): R
    fun visit(costExpression: ConstantFinTypeSymbol): R
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

    override fun visit(costExpression: ImmutableFinTypeParameter): Long {
        langThrow(CalculateCostFailed)
    }

    override fun visit(costExpression: MutableFinTypeParameter): Long {
        langThrow(CalculateCostFailed)
    }

    override fun visit(costExpression: FinTypeSymbol): Long {
        if (costExpression.magnitude <= 0L) {
            langThrow(NegativeFin)
        }
        return costExpression.magnitude
    }

    override fun visit(costExpression: ConstantFinTypeSymbol): Long {
        return architecture.defaultNodeCost
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
            langThrow(NegativeFin)
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
            langThrow(NegativeFin)
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
            langThrow(NegativeFin)
        }
        return res
    }

}

object CommonCostExpressions {
    val defaultMultiplier = FinTypeSymbol(1L)
    val twoPass = FinTypeSymbol(2L)
}

object CanEvalCostExpressionVisitor: CostExpressionVisitor<Boolean> {
    override fun visit(costExpression: ImmutableFinTypeParameter): Boolean {
        return false
    }

    override fun visit(costExpression: MutableFinTypeParameter): Boolean {
        return false
    }

    override fun visit(costExpression: FinTypeSymbol): Boolean {
        return true
    }

    override fun visit(costExpression: ConstantFinTypeSymbol): Boolean {
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
    override fun visit(costExpression: ImmutableFinTypeParameter) {
        langThrow(TypeSystemBug)
    }

    override fun visit(costExpression: MutableFinTypeParameter) {
        langThrow(TypeSystemBug)
    }

    override fun visit(costExpression: FinTypeSymbol) = Unit

    override fun visit(costExpression: ConstantFinTypeSymbol) = Unit

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