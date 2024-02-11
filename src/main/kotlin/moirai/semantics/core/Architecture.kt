package moirai.semantics.core

interface Architecture {
    val distributedPluginCost: Long
    val defaultNodeCost: Long
    val costUpperLimit: Long
}