package moirai.semantics.core

sealed interface NodeCostOverlay

data class DefinedOverlay(val nodeCost: Long): NodeCostOverlay
data object UndefinedOverlay: NodeCostOverlay

interface Architecture {
    val defaultNodeCost: Long
    val costUpperLimit: Long

    /**
     * This method allows architectures to make specific nodes more expensive. The exact
     * weights for each node will need to be determined through load testing on a given
     * architecture. For example, the ForEachAst may need to be one or two orders of
     * magnitude more expensive than other nodes.
     */
    fun getNodeCostOverlay(nodeKind: AstNodeKind): NodeCostOverlay
}

/**
 * A public list of Ast node types. Note that if the list of AST kinds changes,
 * it can and should be a breaking change so that users can define the new node
 * cost overlays.
 */
enum class AstNodeKind {
    IntLiteralAst,
    DecimalLiteralAst,
    BooleanLiteralAst,
    CharLiteralAst,
    StringLiteralAst,
    StringInterpolationAst,
    LetAst,
    RefAst,
    FileAst,
    BlockAst,
    FunctionAst,
    LambdaAst,
    RecordDefinitionAst,
    ObjectDefinitionAst,
    DotAst,
    GroundApplyAst,
    DotApplyAst,
    ForEachAst,
    AssignAst,
    DotAssignAst,
    IfAst,
    MatchAst
}