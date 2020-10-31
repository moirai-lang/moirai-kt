package com.tsikhe.shardscript.semantics.core

import java.math.BigInteger

interface Architecture {
    val distributedPluginCost: BigInteger
    val defaultNodeCost: BigInteger
    val costUpperLimit: BigInteger
}
