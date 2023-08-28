package org.shardscript.semantics.core

interface Architecture {
    val distributedPluginCost: Long
    val defaultNodeCost: Long
    val costUpperLimit: Long
}