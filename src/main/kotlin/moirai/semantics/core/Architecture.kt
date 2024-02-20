package moirai.semantics.core

interface Architecture {
    val defaultNodeCost: Long
    val costUpperLimit: Long
}