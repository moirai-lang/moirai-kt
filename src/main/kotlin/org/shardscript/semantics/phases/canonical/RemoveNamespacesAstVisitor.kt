package org.shardscript.semantics.phases.canonical

import org.shardscript.semantics.core.*
import org.shardscript.semantics.phases.parse.*

class ResolveNamespaceAstVisitor(private val errors: LanguageErrors): ParameterizedScopedAstVisitor<LocalCanonicalScope, CanonicalAst> {
    override fun visit(ast: NumberLiteralScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return NumberLiteralCanonicalAst(ast.ctx, param, ast.canonicalForm)
    }

    override fun visit(ast: BooleanLiteralScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return BooleanLiteralCanonicalAst(ast.ctx, param, ast.canonicalForm)
    }

    override fun visit(ast: StringLiteralScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return StringLiteralCanonicalAst(ast.ctx, param, ast.canonicalForm)
    }

    override fun visit(ast: StringInterpolationScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return StringInterpolationCanonicalAst(ast.ctx, param, ast.components.map { it.accept(this, param) })
    }

    override fun visit(ast: LetScopedAst, param: LocalCanonicalScope): CanonicalAst {
        val res = LetCanonicalAst(ast.ctx, param, ast.identifier, ast.ofType, ast.rhs.accept(this, param), ast.mutable)
        param.define(errors, ast.identifier, NotANamespaceSymbol(ast.identifier.name))
        return res
    }

    override fun visit(ast: RefScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return RefCanonicalAst(ast.ctx, param, ast.identifier)
    }

    override fun visit(ast: FileScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return FileCanonicalAst(ast.ctx, param, ast.lines.map { it.accept(this, param) })
    }

    override fun visit(ast: BlockScopedAst, param: LocalCanonicalScope): CanonicalAst {
        val blockScope = LocalCanonicalScope(param)
        return BlockCanonicalAst(ast.ctx, param, blockScope, ast.lines.map { it.accept(this, blockScope) })
    }

    override fun visit(ast: FunctionScopedAst, param: LocalCanonicalScope): CanonicalAst {
        val bodyScope = LocalCanonicalScope(param)
        val body = BlockCanonicalAst(ast.body.ctx, param, bodyScope, ast.body.lines.map { it.accept(this, bodyScope) })
        val res = FunctionCanonicalAst(
            ast.ctx,
            param,
            bodyScope,
            ast.identifier,
            ast.typeParams,
            ast.formalParams,
            ast.returnType,
            body
        )
        param.define(errors, ast.identifier, NotANamespaceSymbol(ast.identifier.name))
        val seenTypeParameters: MutableMap<String, TypeParameterDefinition> = HashMap()
        val seenFormalParameters: MutableMap<String, Binder> = HashMap()
        ast.typeParams.forEach {
            if (seenTypeParameters.containsKey(it.identifier.name)) {
                errors.add(it.identifier.ctx, DuplicateTypeParameter(it.identifier.ctx, it.identifier.name))
            } else {
                seenTypeParameters[it.identifier.name] = it
                bodyScope.define(errors, it.identifier, NotANamespaceSymbol(it.identifier.name))
            }
        }
        ast.formalParams.forEach {
            if (seenTypeParameters.containsKey(it.identifier.name)) {
                errors.add(it.identifier.ctx, MaskingTypeParameter(it.identifier.ctx, it.identifier.name))
            } else if (seenFormalParameters.containsKey(it.identifier.name)) {
                errors.add(it.identifier.ctx, IdentifierAlreadyExists(it.identifier.ctx, it.identifier.name))
            } else {
                seenFormalParameters[it.identifier.name] = it
                bodyScope.define(errors, it.identifier, NotANamespaceSymbol(it.identifier.name))
            }
        }
        return res
    }

    override fun visit(ast: LambdaScopedAst, param: LocalCanonicalScope): CanonicalAst {
        val bodyScope = LocalCanonicalScope(param)
        return if (ast.body is BlockCanonicalAst) {
            val body = BlockCanonicalAst(ast.body.ctx, param, bodyScope, ast.body.lines.map { it.accept(this, bodyScope) })
            LambdaCanonicalAst(ast.ctx, param, bodyScope, ast.formalParams, body)
        } else {
            LambdaCanonicalAst(ast.ctx, param, bodyScope, ast.formalParams, ast.body.accept(this, bodyScope))
        }
    }

    override fun visit(ast: RecordDefinitionScopedAst, param: LocalCanonicalScope): CanonicalAst {
        val bodyScope = LocalCanonicalScope(param)
        val res = RecordDefinitionCanonicalAst(ast.ctx, param, bodyScope, ast.identifier, ast.typeParams, ast.fields)
        param.define(errors, ast.identifier, NotANamespaceSymbol(ast.identifier.name))
        return res
    }

    override fun visit(ast: ObjectDefinitionScopedAst, param: LocalCanonicalScope): CanonicalAst {
        val res = ObjectDefinitionCanonicalAst(ast.ctx, param, ast.identifier)
        param.define(errors, ast.identifier, NotANamespaceSymbol(ast.identifier.name))
        return res
    }

    override fun visit(ast: DotScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return DotCanonicalAst(ast.ctx, param, ast.lhs.accept(this, param), ast.identifier)
    }

    override fun visit(ast: GroundApplyScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return GroundApplyCanonicalAst(ast.ctx, param, ast.signifier, ast.args.map { it.accept(this, param) })
    }

    override fun visit(ast: DotApplyScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return DotApplyCanonicalAst(
            ast.ctx,
            param,
            ast.lhs.accept(this, param),
            ast.signifier,
            ast.args.map { it.accept(this, param) })
    }

    override fun visit(ast: ForEachScopedAst, param: LocalCanonicalScope): CanonicalAst {
        val bodyScope = LocalCanonicalScope(param)
        return if (ast.body is BlockCanonicalAst) {
            val body = BlockCanonicalAst(ast.body.ctx, param, bodyScope, ast.body.lines.map { it.accept(this, bodyScope) })
            ForEachCanonicalAst(
                ast.ctx,
                param,
                bodyScope,
                ast.identifier,
                ast.ofType,
                ast.source.accept(this, param),
                body
            )
        } else {
            ForEachCanonicalAst(
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

    override fun visit(ast: AssignScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return AssignCanonicalAst(ast.ctx, param, ast.identifier, ast.rhs.accept(this, param))
    }

    override fun visit(ast: DotAssignScopedAst, param: LocalCanonicalScope): CanonicalAst {
        return DotAssignCanonicalAst(
            ast.ctx,
            param,
            ast.lhs.accept(this, param),
            ast.identifier,
            ast.rhs.accept(this, param)
        )
    }

    override fun visit(ast: IfScopedAst, param: LocalCanonicalScope): CanonicalAst {
        val trueBranchScope = LocalCanonicalScope(param)
        val falseBranchScope = LocalCanonicalScope(param)
        val trueBranch = if (ast.trueBranch is BlockCanonicalAst) {
            BlockCanonicalAst(
                ast.trueBranch.ctx,
                param,
                trueBranchScope,
                ast.trueBranch.lines.map { it.accept(this, trueBranchScope) })
        } else {
            ast.trueBranch.accept(this, trueBranchScope)
        }
        val falseBranch = if (ast.falseBranch is BlockCanonicalAst) {
            BlockCanonicalAst(
                ast.falseBranch.ctx,
                param,
                falseBranchScope,
                ast.falseBranch.lines.map { it.accept(this, falseBranchScope) })
        } else {
            ast.falseBranch.accept(this, falseBranchScope)
        }
        return IfCanonicalAst(
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