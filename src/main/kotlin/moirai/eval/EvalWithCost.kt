package moirai.eval

import moirai.composition.CompilerFrontend
import moirai.composition.ExecutionArtifacts
import moirai.composition.ExecutionCache
import moirai.composition.pluginMap
import moirai.semantics.core.Architecture
import moirai.transport.TransportAst

data class EvalWithCostResult(val value: Value, val cost: Long)

fun evalWithCost(
    architecture: Architecture,
    executionArtifacts: ExecutionArtifacts,
    userPlugins: List<UserPlugin>
): EvalWithCostResult {
    val globalScope = ValueTable(NullValueTable)

    val userPluginMap = pluginMap(userPlugins)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, userPluginMap)

    val executionScope = ValueTable(globalScope)

    val value = executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
    val cost = executionArtifacts.semanticArtifacts.processedAst.cost

    return EvalWithCostResult(value, cost)
}

fun evalWithCost(
    fileName: String,
    transportAst: TransportAst,
    frontend: CompilerFrontend,
    executionCache: ExecutionCache,
    userPlugins: List<UserPlugin>
): EvalWithCostResult {
    val sa = frontend.compileTransportAst(fileName, transportAst, executionCache)

    val userPluginMap = pluginMap(userPlugins)
    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(frontend.architecture, globalScope, userPluginMap)

    val executionScope = ValueTable(globalScope)

    val value = sa.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
    val cost = sa.processedAst.cost

    return EvalWithCostResult(value, cost)
}