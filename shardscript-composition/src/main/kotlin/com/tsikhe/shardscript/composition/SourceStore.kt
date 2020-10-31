package com.tsikhe.shardscript.composition

interface SourceStore {
    fun fetchSourceText(namespace: List<String>): String
}
