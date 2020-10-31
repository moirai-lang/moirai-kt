package com.tsikhe.shardscript.semantics.core

interface Scope<T> {
    fun define(gid: GroundIdentifier, definition: T)
    fun exists(identifier: Identifier): Boolean
    fun existsHere(identifier: Identifier): Boolean
    fun fetch(identifier: Identifier): T
    fun fetchHere(identifier: Identifier): T
}
