package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

internal class OmicronDebuggerAstVisitor(val architecture: Architecture) : UnitAstVisitor() {
    fun testNode(ast: Ast) {
        val cost = evalCostExpression(ast.costExpression)
        if (cost > architecture.costUpperLimit) {
            filterThrow(setOf(LanguageError(NotInSource, CostOverLimit)))
        }
    }

    override fun visit(ast: SByteLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: ShortLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: IntLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: LongLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: ByteLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: UShortLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: UIntLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: ULongLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: DecimalLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: BooleanLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: CharLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: StringLiteralAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: StringInterpolationAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: LetAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: RefAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: FileAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: BlockAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: FunctionAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: ObjectDefinitionAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: EnumDefinitionAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: DotAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: GroundApplyAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: DotApplyAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: ForEachAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: MapAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: FlatMapAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: AssignAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: DotAssignAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: IfAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: SwitchAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: AsAst) {
        super.visit(ast)
        testNode(ast)
    }

    override fun visit(ast: IsAst) {
        super.visit(ast)
        testNode(ast)
    }
}
