package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

open class UnitAstVisitor : AstVisitor<Unit> {
    val errors = LanguageErrors()

    override fun visit(ast: SByteLiteralAst) = Unit

    override fun visit(ast: ShortLiteralAst) = Unit

    override fun visit(ast: IntLiteralAst) = Unit

    override fun visit(ast: LongLiteralAst) = Unit

    override fun visit(ast: ByteLiteralAst) = Unit

    override fun visit(ast: UShortLiteralAst) = Unit

    override fun visit(ast: UIntLiteralAst) = Unit

    override fun visit(ast: ULongLiteralAst) = Unit

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

    override fun visit(ast: AsAst) {
        ast.lhs.accept(this)
    }

    override fun visit(ast: IsAst) {
        ast.lhs.accept(this)
    }
}

open class ParameterizedUnitAstVisitor<P> : ParameterizedAstVisitor<P, Unit> {
    val errors = LanguageErrors()

    override fun visit(ast: SByteLiteralAst, param: P) = Unit

    override fun visit(ast: ShortLiteralAst, param: P) = Unit

    override fun visit(ast: IntLiteralAst, param: P) = Unit

    override fun visit(ast: LongLiteralAst, param: P) = Unit

    override fun visit(ast: ByteLiteralAst, param: P) = Unit

    override fun visit(ast: UShortLiteralAst, param: P) = Unit

    override fun visit(ast: UIntLiteralAst, param: P) = Unit

    override fun visit(ast: ULongLiteralAst, param: P) = Unit

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

    override fun visit(ast: AsAst, param: P) {
        ast.lhs.accept(this, param)
    }

    override fun visit(ast: IsAst, param: P) {
        ast.lhs.accept(this, param)
    }
}