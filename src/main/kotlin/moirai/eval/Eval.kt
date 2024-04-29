package moirai.eval

import moirai.composition.*
import moirai.semantics.core.Architecture
import moirai.transport.TransportAst

fun eval(
    source: String,
    architecture: Architecture,
    sourceStore: SourceStore
): Value {
    val frontend = CompilerFrontend(architecture, sourceStore)

    val executionArtifacts = frontend.fullCompileWithTopologicalSort(source)

    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, mapOf())

    val executionScope = ValueTable(globalScope)
    return executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}

fun eval(
    architecture: Architecture,
    executionArtifacts: ExecutionArtifacts
): Value {
    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, mapOf())

    val executionScope = ValueTable(globalScope)
    return executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}

fun eval(
    architecture: Architecture,
    executionArtifacts: ExecutionArtifacts,
    userPlugins: List<UserPlugin>
): Value {
    val globalScope = ValueTable(NullValueTable)

    val userPluginMap = pluginMap(userPlugins)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, userPluginMap)

    val executionScope = ValueTable(globalScope)
    return executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}

fun eval(
    source: String,
    architecture: Architecture,
    sourceStore: SourceStore,
    pluginSource: String,
    userPlugins: List<UserPlugin>
): Value {
    val frontend = CompilerFrontend(architecture, sourceStore, UserPluginSource(pluginSource))

    val executionArtifacts = frontend.fullCompileWithTopologicalSort(source)

    val userPluginMap = pluginMap(userPlugins)
    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, userPluginMap)

    val executionScope = ValueTable(globalScope)
    return executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}

fun eval(
    fileName: String,
    transportAst: TransportAst,
    architecture: Architecture,
    sourceStore: SourceStore,
    executionCache: ExecutionCache,
    pluginSource: String,
    userPlugins: List<UserPlugin>
): Value {
    val frontend = CompilerFrontend(architecture, sourceStore, UserPluginSource(pluginSource))

    val sa = frontend.compileTransportAst(fileName, transportAst, executionCache)

    val userPluginMap = pluginMap(userPlugins)
    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, userPluginMap)

    val executionScope = ValueTable(globalScope)
    return sa.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}

fun eval(
    fileName: String,
    transportAst: TransportAst,
    frontend: CompilerFrontend,
    executionCache: ExecutionCache,
    userPlugins: List<UserPlugin>
): Value {
    val sa = frontend.compileTransportAst(fileName, transportAst, executionCache)

    val userPluginMap = pluginMap(userPlugins)
    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(frontend.architecture, globalScope, userPluginMap)

    val executionScope = ValueTable(globalScope)
    return sa.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}