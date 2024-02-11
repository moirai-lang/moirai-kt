package moirai.semantics.infer

import moirai.semantics.core.CostExpression
import moirai.semantics.core.FunctionType
import moirai.semantics.core.RawTerminus
import moirai.semantics.core.Type

sealed class InstantiationChain<T: RawTerminus> {
    abstract val terminus: T
    abstract fun toList(): List<Substitution>
}

data class TerminalChain<T: RawTerminus>(override val terminus: T) : InstantiationChain<T>() {
    override fun toList(): List<Substitution> = listOf()
}

data class SubstitutionChain<T: RawTerminus>(val substitution: Substitution, val chain: InstantiationChain<T>) : InstantiationChain<T>() {
    override val terminus: T = chain.terminus
    override fun toList(): List<Substitution> {
        val res = chain.toList().toMutableList()
        res.add(substitution)
        return res
    }

    fun replayArgs(): List<Type> =
        terminus.typeParams.map { replay(it) }

    fun replay(type: Type): Type {
        return when (chain) {
            is SubstitutionChain -> {
                substitution.applySymbol(chain.replay(type))
            }

            is TerminalChain -> {
                substitution.applySymbol(type)
            }
        }
    }

    fun replay(functionType: FunctionType): FunctionType {
        return when (chain) {
            is SubstitutionChain -> {
                substitution.applyFunctionType(chain.replay(functionType))
            }

            is TerminalChain -> {
                substitution.applyFunctionType(functionType)
            }
        }
    }

    fun replay(costExpression: CostExpression): CostExpression {
        return when (chain) {
            is SubstitutionChain -> {
                substitution.applyCost(chain.replay(costExpression))
            }

            is TerminalChain -> {
                substitution.applyCost(costExpression)
            }
        }
    }
}