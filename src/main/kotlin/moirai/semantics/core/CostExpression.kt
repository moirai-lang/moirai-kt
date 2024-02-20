package moirai.semantics.core

import kotlin.math.sqrt

internal sealed interface CostExpression : Type {
    fun <R> accept(visitor: CostExpressionVisitor<R>): R
}

internal interface CostExpressionVisitor<R> {
    fun visit(costExpression: FinTypeParameter): R
    fun visit(costExpression: Fin): R
    fun visit(costExpression: ConstantFin): R
    fun visit(costExpression: SumCostExpression): R
    fun visit(costExpression: ProductCostExpression): R
    fun visit(costExpression: MaxCostExpression): R
}

internal class EvalCostExpressionVisitor(val architecture: Architecture): CostExpressionVisitor<Long> {
    init {
        if(architecture.costUpperLimit > sqrt(Long.MAX_VALUE.toDouble()).toLong() - 2) {
            langThrow(InvalidCostUpperLimit)
        }
    }

    override fun visit(costExpression: FinTypeParameter): Long {
        langThrow(CalculateCostFailed)
    }

    override fun visit(costExpression: Fin): Long {
        if (costExpression.magnitude <= 0L) {
            langThrow(NegativeFin)
        }
        return costExpression.magnitude
    }

    override fun visit(costExpression: ConstantFin): Long {
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

internal object CommonCostExpressions {
    val defaultMultiplier = Fin(1L)
    val twoPass = Fin(2L)
}

internal object CanEvalCostExpressionVisitor: CostExpressionVisitor<Boolean> {
    override fun visit(costExpression: FinTypeParameter): Boolean {
        return false
    }

    override fun visit(costExpression: Fin): Boolean {
        return true
    }

    override fun visit(costExpression: ConstantFin): Boolean {
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