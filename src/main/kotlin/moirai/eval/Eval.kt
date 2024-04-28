package moirai.eval

import moirai.composition.*
import moirai.semantics.core.Architecture
import moirai.semantics.core.NotInSource
import moirai.semantics.core.PluginAlreadyExists
import moirai.semantics.core.langThrow
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

    val userPluginMap: MutableMap<String, UserPlugin> = mutableMapOf()
    userPlugins.forEach {
        if (!userPluginMap.containsKey(it.key)) {
            userPluginMap[it.key] = it
        } else {
            langThrow(NotInSource, PluginAlreadyExists(it.key))
        }
    }

    val evalVisitor = EvalAstVisitor(architecture, globalScope, userPluginMap.toMap())

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

    val userPluginMap: MutableMap<String, UserPlugin> = mutableMapOf()
    userPlugins.forEach {
        if(!userPluginMap.containsKey(it.key)) {
            userPluginMap[it.key] = it
        } else {
            langThrow(NotInSource, PluginAlreadyExists(it.key))
        }
    }

    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, userPluginMap.toMap())

    val executionScope = ValueTable(globalScope)
    return executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}

fun eval(
    fileName: String,
    transportAst: TransportAst,
    architecture: Architecture,
    sourceStore: SourceStore,
    executionCache: ExecutionCache
): Value {
    val frontend = CompilerFrontend(architecture, sourceStore)

    val sa = frontend.compileTransportAst(fileName, transportAst, executionCache)

    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope, mapOf())

    val executionScope = ValueTable(globalScope)
    return sa.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}