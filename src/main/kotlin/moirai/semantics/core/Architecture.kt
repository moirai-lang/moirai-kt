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
     * magnitude more expensive than other nodes. A string is used as a key because a
     * public list of Ast node types may become stale.
     */
    fun getNodeCostOverlay(key: String): NodeCostOverlay
}

/**
 * A public list of Ast node types, with some reserved enum values for backwards-compatibility.
 */
enum class AstNodeNames(val key: String) {
    IntLiteralAst("Int"),
    DecimalLiteralAst("Decimal"),
    BooleanLiteralAst("Boolean"),
    CharLiteralAst("Char"),
    StringLiteralAst("String"),
    StringInterpolationAst("StringInterpolation"),
    LetAst("Let"),
    RefAst("Ref"),
    FileAst("File"),
    BlockAst("Block"),
    FunctionAst("Function"),
    LambdaAst("Lambda"),
    RecordDefinitionAst("Record"),
    ObjectDefinitionAst("Object"),
    DotAst("Dot"),
    GroundApplyAst("Apply"),
    DotApplyAst("DotApply"),
    ForEachAst("ForEach"),
    AssignAst("Assign"),
    DotAssignAst("DotAssign"),
    IfAst("If"),
    MatchAst("Match"),
    Reserved1("Reserved1"),
    Reserved2("Reserved2"),
    Reserved3("Reserved3"),
    Reserved4("Reserved4"),
    Reserved5("Reserved5")
}