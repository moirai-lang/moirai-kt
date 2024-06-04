package moirai.eval

import moirai.composition.*
import moirai.semantics.core.Architecture
import moirai.transport.TransportAst

data class EvalWithCostResult(val value: Value, val cost: Long, val alternativeCosts: Map<Architecture, Long>)

fun evalWithCost(
    source: String,
    architecture: Architecture,
    sourceStore: SourceStore
): EvalWithCostResult {
    val frontend = CompilerFrontend(architecture, sourceStore)

    val executionArtifacts = frontend.fullCompileWithTopologicalSort(source)

    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, mapOf())

    val executionScope = ValueTable(globalScope)
    val value = executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
    val cost = executionArtifacts.semanticArtifacts.processedAst.cost
    val alternativeCosts = executionArtifacts.semanticArtifacts.processedAst.alternativeCosts

    return EvalWithCostResult(value, cost, alternativeCosts)
}

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
    val alternativeCosts = executionArtifacts.semanticArtifacts.processedAst.alternativeCosts

    return EvalWithCostResult(value, cost, alternativeCosts)
}

fun evalWithCost(
    source: String,
    architecture: Architecture,
    sourceStore: SourceStore,
    pluginSource: String,
    userPlugins: List<UserPlugin>
): EvalWithCostResult {
    val frontend = CompilerFrontend(architecture, sourceStore, UserPluginSource(pluginSource))

    val executionArtifacts = frontend.fullCompileWithTopologicalSort(source)

    val userPluginMap = pluginMap(userPlugins)
    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, userPluginMap)

    val executionScope = ValueTable(globalScope)
    val value = executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
    val cost = executionArtifacts.semanticArtifacts.processedAst.cost
    val alternativeCosts = executionArtifacts.semanticArtifacts.processedAst.alternativeCosts

    return EvalWithCostResult(value, cost, alternativeCosts)
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
    val alternativeCosts = sa.processedAst.alternativeCosts

    return EvalWithCostResult(value, cost, alternativeCosts)
}