package moirai.eval

import moirai.composition.CompilerFrontend
import moirai.composition.SourceStore
import moirai.semantics.core.Architecture

fun eval(
    source: String,
    architecture: Architecture,
    sourceStore: SourceStore
): Value {
    val frontend = CompilerFrontend(architecture, sourceStore)

    val executionArtifacts = frontend.compile(source)

    val globalScope = ValueTable(NullValueTable)
    val evalVisitor = EvalAstVisitor(architecture, globalScope)

    val executionScope = ValueTable(globalScope)
    return executionArtifacts.processedAst.accept(evalVisitor, EvalContext(executionScope, mapOf()))
}