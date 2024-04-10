package moirai.semantics.core

import moirai.semantics.prelude.StringMethods
import kotlin.math.sqrt

internal sealed interface CostExpression : Type {
    fun <R> accept(visitor: CostExpressionVisitor<R>): R
}

internal interface CostExpressionVisitor<R> {
    fun visit(costExpression: FinTypeParameter): R
    fun visit(costExpression: ParameterHashCodeCost): R
    fun visit(costExpression: InstantiationHashCodeCost): R
    fun visit(costExpression: Fin): R
    fun visit(costExpression: ConstantFin): R
    fun visit(costExpression: SumCostExpression): R
    fun visit(costExpression: ProductCostExpression): R
    fun visit(costExpression: MaxCostExpression): R
}

internal enum class CostOperator(val idStr: String) {
    Sum("Sum"),
    Mul("Mul"),
    Max("Max")
}

internal fun sortCanonical(costExpressions: List<CostExpression>): List<CostExpression> {
    val res: MutableList<CostExpression> = mutableListOf()
    res.addAll(costExpressions.filterIsInstance<MaxCostExpression>())
    res.addAll(costExpressions.filterIsInstance<ProductCostExpression>())
    res.addAll(costExpressions.filterIsInstance<SumCostExpression>())
    res.addAll(costExpressions.filterIsInstance<ParameterHashCodeCost>().sortedBy { it.typeParameter.qualifiedName })
    res.addAll(costExpressions.filterIsInstance<InstantiationHashCodeCost>().sortedBy {
        (it.instantiation.substitutionChain.terminus as ParameterHashCodeCost).typeParameter.qualifiedName
    })
    res.addAll(costExpressions.filterIsInstance<FinTypeParameter>().sortedBy { it.qualifiedName })
    res.addAll(costExpressions.filterIsInstance<Fin>().sortedBy { it.magnitude })
    res.addAll(costExpressions.filterIsInstance<ConstantFin>())

    if (res.size != costExpressions.size) {
        langThrow(NotInSource, TypeSystemBug)
    }

    return res
}

internal fun costExpressionFromAnyType(type: Type): CostExpression {
    fun fromToString(member: Symbol): CostExpression {
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

    return when (type) {
        is ConstantFin -> type
        is Fin -> type
        is FinTypeParameter -> type
        is MaxCostExpression -> MaxCostExpression(type.children.map { costExpressionFromAnyType(it) })
        is ProductCostExpression -> ProductCostExpression(type.children.map { costExpressionFromAnyType(it) })
        is SumCostExpression -> SumCostExpression(type.children.map { costExpressionFromAnyType(it) })

        is PlatformObjectType -> {
            fromToString(type.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr)))
        }

        is BasicType -> {
            fromToString(type.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr)))
        }

        is TypeInstantiation -> {
            when (val parameterizedType = type.substitutionChain.terminus) {
                is ParameterizedBasicType -> {
                    fromToString(parameterizedType.fetchHere(Identifier(NotInSource, StringMethods.ToString.idStr)))
                }

                is ParameterHashCodeCost -> {
                    costExpressionFromAnyType(type.substitutionChain.replay(parameterizedType.typeParameter))
                }

                is ParameterizedRecordType -> {
                    SumCostExpression(parameterizedType.fields.map {
                        costExpressionFromAnyType(type.substitutionChain.replay(it.ofTypeSymbol))
                    })
                }

                is PlatformSumRecordType -> {
                    SumCostExpression(parameterizedType.fields.map {
                        costExpressionFromAnyType(type.substitutionChain.replay(it.ofTypeSymbol))
                    })
                }

                is PlatformSumType -> {
                    SumCostExpression(parameterizedType.memberTypes.map {
                        costExpressionFromAnyType(
                            type.substitutionChain.replay(
                                when (it) {
                                    is PlatformSumObjectType -> it
                                    is PlatformSumRecordType -> it
                                }
                            )
                        )
                    })
                }
            }
        }

        is GroundRecordType -> SumCostExpression(type.fields.map { costExpressionFromAnyType(it.ofTypeSymbol) })
        is ObjectType -> Fin(type.identifier.name.length.toLong())
        is PlatformSumObjectType -> Fin(type.identifier.name.length.toLong())

        else -> langThrow(TypeSystemBug)
    }
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

    override fun visit(costExpression: ParameterHashCodeCost): Long {
        langThrow(CalculateCostFailed)
    }

    override fun visit(costExpression: InstantiationHashCodeCost): Long {
        val tp = costExpression.instantiation.substitutionChain.terminus.typeParams.first()
        val ce = costExpression.instantiation.substitutionChain.replay(tp)
        return costExpressionFromAnyType(ce).accept(this)
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

    override fun visit(costExpression: ParameterHashCodeCost): Boolean {
        return false
    }

    override fun visit(costExpression: InstantiationHashCodeCost): Boolean {
        return true
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