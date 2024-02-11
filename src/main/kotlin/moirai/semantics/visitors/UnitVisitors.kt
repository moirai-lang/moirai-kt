package moirai.semantics.visitors

import moirai.semantics.core.*

open class UnitAstVisitor : AstVisitor<Unit> {
    val errors = LanguageErrors()
    override fun visit(ast: IntLiteralAst) = Unit

    override fun visit(ast: DecimalLiteralAst) = Unit

    override fun visit(ast: BooleanLiteralAst) = Unit

    override fun visit(ast: CharLiteralAst) = Unit

    override fun visit(ast: StringLiteralAst) = Unit

    override fun visit(ast: StringInterpolationAst) {
        ast.components.forEach {
            it.accept(this)
        }
    }

    override fun visit(ast: LetAst) {
        ast.rhs.accept(this)
    }

    override fun visit(ast: RefAst) = Unit

    override fun visit(ast: FileAst) {
        ast.lines.forEach {
            it.accept(this)
        }
    }

    override fun visit(ast: BlockAst) {
        ast.lines.forEach {
            it.accept(this)
        }
    }

    override fun visit(ast: FunctionAst) {
        ast.body.accept(this)
    }

    override fun visit(ast: LambdaAst) {
        ast.body.accept(this)
    }

    override fun visit(ast: RecordDefinitionAst) = Unit

    override fun visit(ast: ObjectDefinitionAst) = Unit

    override fun visit(ast: DotAst) {
        ast.lhs.accept(this)
    }

    override fun visit(ast: GroundApplyAst) {
        ast.args.forEach {
            it.accept(this)
        }
    }

    override fun visit(ast: DotApplyAst) {
        ast.lhs.accept(this)
        ast.args.forEach {
            it.accept(this)
        }
    }

    override fun visit(ast: ForEachAst) {
        ast.source.accept(this)
        ast.body.accept(this)
    }

    override fun visit(ast: AssignAst) {
        ast.rhs.accept(this)
    }

    override fun visit(ast: DotAssignAst) {
        ast.lhs.accept(this)
        ast.rhs.accept(this)
    }

    override fun visit(ast: IfAst) {
        ast.condition.accept(this)
        ast.trueBranch.accept(this)
        ast.falseBranch.accept(this)
    }

    override fun visit(ast: MatchAst) {
        ast.condition.accept(this)
        ast.cases.forEach {
            it.block.accept(this)
        }
    }
}

open class ParameterizedUnitAstVisitor<P> : ParameterizedAstVisitor<P, Unit> {
    val errors = LanguageErrors()
    override fun visit(ast: IntLiteralAst, param: P) = Unit

    override fun visit(ast: DecimalLiteralAst, param: P) = Unit

    override fun visit(ast: BooleanLiteralAst, param: P) = Unit

    override fun visit(ast: CharLiteralAst, param: P) = Unit

    override fun visit(ast: StringLiteralAst, param: P) = Unit

    override fun visit(ast: StringInterpolationAst, param: P) {
        ast.components.forEach {
            it.accept(this, param)
        }
    }

    override fun visit(ast: LetAst, param: P) {
        ast.rhs.accept(this, param)
    }

    override fun visit(ast: RefAst, param: P) = Unit

    override fun visit(ast: FileAst, param: P) {
        ast.lines.forEach {
            it.accept(this, param)
        }
    }

    override fun visit(ast: BlockAst, param: P) {
        ast.lines.forEach {
            it.accept(this, param)
        }
    }

    override fun visit(ast: FunctionAst, param: P) {
        ast.body.accept(this, param)
    }

    override fun visit(ast: LambdaAst, param: P) {
        ast.body.accept(this, param)
    }

    override fun visit(ast: RecordDefinitionAst, param: P) = Unit

    override fun visit(ast: ObjectDefinitionAst, param: P) = Unit

    override fun visit(ast: DotAst, param: P) {
        ast.lhs.accept(this, param)
    }

    override fun visit(ast: GroundApplyAst, param: P) {
        ast.args.forEach {
            it.accept(this, param)
        }
    }

    override fun visit(ast: DotApplyAst, param: P) {
        ast.lhs.accept(this, param)
        ast.args.forEach {
            it.accept(this, param)
        }
    }

    override fun visit(ast: ForEachAst, param: P) {
        ast.source.accept(this, param)
        ast.body.accept(this, param)
    }

    override fun visit(ast: AssignAst, param: P) {
        ast.rhs.accept(this, param)
    }

    override fun visit(ast: DotAssignAst, param: P) {
        ast.lhs.accept(this, param)
        ast.rhs.accept(this, param)
    }

    override fun visit(ast: IfAst, param: P) {
        ast.condition.accept(this, param)
        ast.trueBranch.accept(this, param)
        ast.falseBranch.accept(this, param)
    }

    override fun visit(ast: MatchAst, param: P) {
        ast.condition.accept(this, param)
        ast.cases.forEach {
            it.block.accept(this, param)
        }
    }
}