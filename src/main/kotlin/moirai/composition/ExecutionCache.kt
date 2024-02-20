package moirai.composition

import moirai.grammar.MoiraiParser
import moirai.semantics.core.Ast
import moirai.semantics.workflow.SemanticArtifacts

data class ImportScan(
    val sourceText: String,
    val scriptType: ScriptType
) {
    internal lateinit var parseTree: MoiraiParser.FileContext
    internal lateinit var imports: Set<ImportStat>
}

data class ExecutionArtifacts(
    val importScan: ImportScan,
) {
    internal lateinit var processedAst: Ast
    internal lateinit var semanticArtifacts: SemanticArtifacts
}

sealed class ExecutionCacheRequestResult

data object NotInCache: ExecutionCacheRequestResult()
data class InCache(val executionArtifacts: ExecutionArtifacts): ExecutionCacheRequestResult()


interface ExecutionCache {
    fun fetchExecutionArtifacts(namespace: List<String>): ExecutionCacheRequestResult
    fun storeExecutionArtifacts(namespace: List<String>, executionArtifacts: ExecutionArtifacts)
    fun invalidateCache(namespace: List<String>)
}