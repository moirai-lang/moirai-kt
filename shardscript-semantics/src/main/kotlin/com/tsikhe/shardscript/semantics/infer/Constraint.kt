package com.tsikhe.shardscript.semantics.infer

import com.tsikhe.shardscript.semantics.core.Left
import com.tsikhe.shardscript.semantics.core.Right

internal data class Constraint<T, S>(val someItem: Left<T>, val mustBe: Right<S>)

internal data class EquivalenceRelation<T, S>(val someItem: Left<T>, val equivalences: Set<Right<S>>)

internal fun <T, S> equivalenceRelations(constraints: Set<Constraint<T, S>>): List<EquivalenceRelation<T, S>> {
    val result: MutableMap<Left<T>, MutableSet<Right<S>>> = HashMap()

    constraints.forEach {
        if (!result.containsKey(it.someItem)) {
            result[it.someItem] = HashSet()
        }
        result[it.someItem]!!.add(it.mustBe)
    }

    return result.map { EquivalenceRelation(it.key, it.value) }
}