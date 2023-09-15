package org.shardscript.semantics.infer

import org.shardscript.semantics.core.CostExpression
import org.shardscript.semantics.core.FunctionTypeSymbol
import org.shardscript.semantics.core.ParameterizedSymbol
import org.shardscript.semantics.core.Type

sealed class InstantiationChain {
    abstract val originalSymbol: ParameterizedSymbol
    abstract fun toList(): List<Substitution>
}

data class TerminalChain(override val originalSymbol: ParameterizedSymbol) : InstantiationChain() {
    override fun toList(): List<Substitution> = listOf()
}

data class SubstitutionChain(val substitution: Substitution, val chain: InstantiationChain) : InstantiationChain() {
    override val originalSymbol: ParameterizedSymbol = chain.originalSymbol
    override fun toList(): List<Substitution> {
        val res = chain.toList().toMutableList()
        res.add(substitution)
        return res
    }

    fun replayArgs(): List<Type> =
        originalSymbol.typeParams.map { replay(it) }

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

    fun replay(functionTypeSymbol: FunctionTypeSymbol): FunctionTypeSymbol {
        return when (chain) {
            is SubstitutionChain -> {
                substitution.applyFunctionType(chain.replay(functionTypeSymbol))
            }
            is TerminalChain -> {
                substitution.applyFunctionType(functionTypeSymbol)
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