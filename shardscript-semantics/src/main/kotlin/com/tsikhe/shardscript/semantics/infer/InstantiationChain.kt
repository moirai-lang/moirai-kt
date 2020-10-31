package com.tsikhe.shardscript.semantics.infer

import com.tsikhe.shardscript.semantics.core.ParameterizedSymbol
import com.tsikhe.shardscript.semantics.core.Symbol

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

    fun replayArgs(): List<Symbol> =
        originalSymbol.typeParams.map { replay(it) }

    fun replay(symbol: Symbol): Symbol {
        return when (chain) {
            is SubstitutionChain -> {
                substitution.applySymbol(chain.replay(symbol))
            }
            is TerminalChain -> {
                substitution.applySymbol(symbol)
            }
        }
    }
}
