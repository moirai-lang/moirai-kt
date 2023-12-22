package org.shardscript.semantics.phases.parse

class BindScopesAstVisitor: ParameterizedPostParseAstVisitor<LocalPostParseScope, ScopedAst> {
    override fun visit(ast: NumberLiteralPostParseAst, param: LocalPostParseScope): ScopedAst {
        return NumberLiteralScopedAst(ast.ctx, param, ast.canonicalForm)
    }

    override fun visit(ast: BooleanLiteralPostParseAst, param: LocalPostParseScope): ScopedAst {
        return BooleanLiteralScopedAst(ast.ctx, param, ast.canonicalForm)
    }

    override fun visit(ast: StringLiteralPostParseAst, param: LocalPostParseScope): ScopedAst {
        return StringLiteralScopedAst(ast.ctx, param, ast.canonicalForm)
    }

    override fun visit(ast: StringInterpolationPostParseAst, param: LocalPostParseScope): ScopedAst {
        return StringInterpolationScopedAst(ast.ctx, param, ast.components.map { it.accept(this, param) })
    }

    override fun visit(ast: LetPostParseAst, param: LocalPostParseScope): ScopedAst {
        return LetScopedAst(ast.ctx, param, ast.identifier, ast.ofType, ast.rhs.accept(this, param), ast.mutable)
    }

    override fun visit(ast: RefPostParseAst, param: LocalPostParseScope): ScopedAst {
        return RefScopedAst(ast.ctx, param, ast.identifier)
    }

    override fun visit(ast: FilePostParseAst, param: LocalPostParseScope): ScopedAst {
        return FileScopedAst(ast.ctx, param, ast.lines.map { it.accept(this, param) })
    }

    override fun visit(ast: BlockPostParseAst, param: LocalPostParseScope): ScopedAst {
        val blockScope = LocalPostParseScope(param)
        return BlockScopedAst(ast.ctx, param, blockScope, ast.lines.map { it.accept(this, blockScope) })
    }

    override fun visit(ast: FunctionPostParseAst, param: LocalPostParseScope): ScopedAst {
        val bodyScope = LocalPostParseScope(param)
        val body = BlockScopedAst(ast.body.ctx, param, bodyScope, ast.body.lines.map { it.accept(this, bodyScope) })
        return FunctionScopedAst(ast.ctx, param, bodyScope, ast.identifier, ast.typeParams, ast.formalParams, ast.returnType, body)
    }

    override fun visit(ast: LambdaPostParseAst, param: LocalPostParseScope): ScopedAst {
        val bodyScope = LocalPostParseScope(param)
        return if (ast.body is BlockPostParseAst) {
            val body = BlockScopedAst(ast.body.ctx, param, bodyScope, ast.body.lines.map { it.accept(this, bodyScope) })
            LambdaScopedAst(ast.ctx, param, bodyScope, ast.formalParams, body)
        } else {
            LambdaScopedAst(ast.ctx, param, bodyScope, ast.formalParams, ast.body.accept(this, bodyScope))
        }
    }

    override fun visit(ast: RecordDefinitionPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }

    override fun visit(ast: ObjectDefinitionPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }

    override fun visit(ast: DotPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }

    override fun visit(ast: GroundApplyPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }

    override fun visit(ast: DotApplyPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }

    override fun visit(ast: ForEachPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }

    override fun visit(ast: AssignPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }

    override fun visit(ast: DotAssignPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }

    override fun visit(ast: IfPostParseAst, param: LocalPostParseScope): ScopedAst {
        TODO("Not yet implemented")
    }
}