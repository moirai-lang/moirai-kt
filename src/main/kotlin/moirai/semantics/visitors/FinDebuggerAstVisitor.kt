package moirai.semantics.visitors

import moirai.semantics.core.*

class FinDebuggerAstVisitor(val architecture: Architecture) : UnitAstVisitor() {
    private fun testNode(ast: Ast) {
        val cost = ast.costExpression.accept(EvalCostExpressionVisitor(architecture))
        if (cost > architecture.costUpperLimit) {
            filterThrow(setOf(LanguageError(NotInSource, CostOverLimit)))
        }
    }

    override fun visit(ast: IntLiteralAst) {
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

    override fun visit(ast: MatchAst) {
        super.visit(ast)
        testNode(ast)
    }
}