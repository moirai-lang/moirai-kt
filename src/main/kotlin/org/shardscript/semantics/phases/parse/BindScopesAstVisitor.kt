package org.shardscript.semantics.phases.parse

import org.shardscript.semantics.core.*

class BindScopesAstVisitor(private val errors: LanguageErrors): ParameterizedPostParseAstVisitor<LocalPostParseScope, ScopedAst> {
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
        val res = LetScopedAst(ast.ctx, param, ast.identifier, ast.ofType, ast.rhs.accept(this, param), ast.mutable)
        param.define(errors, ast.identifier, LocalVariableSymbol(ast.identifier.name, ast))
        return res
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
        val res = FunctionScopedAst(
            ast.ctx,
            param,
            bodyScope,
            ast.identifier,
            ast.typeParams,
            ast.formalParams,
            ast.returnType,
            body
        )
        param.define(errors, ast.identifier, FunctionDefinitionSymbol(ast.identifier.name, ast))
        val seenTypeParameters: MutableMap<String, TypeParameterDefinition> = HashMap()
        val seenFormalParameters: MutableMap<String, Binder> = HashMap()
        ast.typeParams.forEach {
            if (seenTypeParameters.containsKey(it.identifier.name)) {
                errors.add(it.identifier.ctx, DuplicateTypeParameter(it.identifier))
            } else {
                seenTypeParameters[it.identifier.name] = it
                bodyScope.define(errors, it.identifier, TypeParameterSymbol(it.identifier.name, it))
            }
        }
        ast.formalParams.forEach {
            if (seenTypeParameters.containsKey(it.identifier.name)) {
                errors.add(it.identifier.ctx, MaskingTypeParameter(it.identifier))
            } else if (seenFormalParameters.containsKey(it.identifier.name)) {
                errors.add(it.identifier.ctx, IdentifierAlreadyExists(it.identifier))
            } else {
                seenFormalParameters[it.identifier.name] = it
                bodyScope.define(errors, it.identifier, FormalParameterSymbol(it.identifier.name, it))
            }
        }
        return res
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
        val bodyScope = LocalPostParseScope(param)
        val res = RecordDefinitionScopedAst(ast.ctx, param, bodyScope, ast.identifier, ast.typeParams, ast.fields)
        param.define(errors, ast.identifier, RecordDefinitionSymbol(ast.identifier.name, ast))
        return res
    }

    override fun visit(ast: ObjectDefinitionPostParseAst, param: LocalPostParseScope): ScopedAst {
        val res = ObjectDefinitionScopedAst(ast.ctx, param, ast.identifier)
        param.define(errors, ast.identifier, ObjectDefinitionSymbol(ast.identifier.name, ast))
        return res
    }

    override fun visit(ast: DotPostParseAst, param: LocalPostParseScope): ScopedAst {
        return DotScopedAst(ast.ctx, param, ast.lhs.accept(this, param), ast.identifier)
    }

    override fun visit(ast: GroundApplyPostParseAst, param: LocalPostParseScope): ScopedAst {
        return GroundApplyScopedAst(ast.ctx, param, ast.signifier, ast.args.map { it.accept(this, param) })
    }

    override fun visit(ast: DotApplyPostParseAst, param: LocalPostParseScope): ScopedAst {
        return DotApplyScopedAst(
            ast.ctx,
            param,
            ast.lhs.accept(this, param),
            ast.signifier,
            ast.args.map { it.accept(this, param) })
    }

    override fun visit(ast: ForEachPostParseAst, param: LocalPostParseScope): ScopedAst {
        val bodyScope = LocalPostParseScope(param)
        return if (ast.body is BlockPostParseAst) {
            val body = BlockScopedAst(ast.body.ctx, param, bodyScope, ast.body.lines.map { it.accept(this, bodyScope) })
            ForEachScopedAst(
                ast.ctx,
                param,
                bodyScope,
                ast.identifier,
                ast.ofType,
                ast.source.accept(this, param),
                body
            )
        } else {
            ForEachScopedAst(
                ast.ctx,
                param,
                bodyScope,
                ast.identifier,
                ast.ofType,
                ast.source.accept(this, param),
                ast.body.accept(this, bodyScope)
            )
        }
    }

    override fun visit(ast: AssignPostParseAst, param: LocalPostParseScope): ScopedAst {
        return AssignScopedAst(ast.ctx, param, ast.identifier, ast.rhs.accept(this, param))
    }

    override fun visit(ast: DotAssignPostParseAst, param: LocalPostParseScope): ScopedAst {
        return DotAssignScopedAst(
            ast.ctx,
            param,
            ast.lhs.accept(this, param),
            ast.identifier,
            ast.rhs.accept(this, param)
        )
    }

    override fun visit(ast: IfPostParseAst, param: LocalPostParseScope): ScopedAst {
        val trueBranchScope = LocalPostParseScope(param)
        val falseBranchScope = LocalPostParseScope(param)
        val trueBranch = if (ast.trueBranch is BlockPostParseAst) {
            BlockScopedAst(
                ast.trueBranch.ctx,
                param,
                trueBranchScope,
                ast.trueBranch.lines.map { it.accept(this, trueBranchScope) })
        } else {
            ast.trueBranch.accept(this, trueBranchScope)
        }
        val falseBranch = if (ast.falseBranch is BlockPostParseAst) {
            BlockScopedAst(
                ast.falseBranch.ctx,
                param,
                falseBranchScope,
                ast.falseBranch.lines.map { it.accept(this, falseBranchScope) })
        } else {
            ast.falseBranch.accept(this, falseBranchScope)
        }
        return IfScopedAst(
            ast.ctx,
            param,
            trueBranchScope,
            falseBranchScope,
            ast.condition.accept(this, param),
            trueBranch,
            falseBranch
        )
    }
}