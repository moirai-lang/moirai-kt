package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

// Omit FileAst, root scope created in earlier phases
class BindScopesAstVisitor(
    val architecture: Architecture,
    val fileName: String
) :
    ParameterizedUnitAstVisitor<Scope<Symbol>>() {
    override fun visit(ast: IntLiteralAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DecimalLiteralAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: BooleanLiteralAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: CharLiteralAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: StringLiteralAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: StringInterpolationAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: LetAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: RefAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: BlockAst, param: Scope<Symbol>) {
        try {
            val blockScope = Block(param)
            ast.scope = blockScope
            super.visit(ast, blockScope)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: FunctionAst, param: Scope<Symbol>) {
        try {
            val symbol: NamedSymbolWithMembers = if (ast.typeParams.isEmpty()) {
                GroundFunctionSymbol(param, ast.identifier, ast.ctx, ast.body)
            } else {
                ast.typeParams.forEach {
                    if (it.identifier == ast.identifier) {
                        errors.add(it.identifier.ctx, MaskingTypeParameter(it.identifier))
                    }
                }
                ParameterizedFunctionSymbol(param, ast.identifier, ast.ctx, ast.body)
            }
            ast.definitionSpace = param
            if (ast.definitionSpace.existsHere(ast.identifier)) {
                errors.add(ast.ctx, IdentifierAlreadyExists(ast.identifier))
            } else {
                ast.definitionSpace.define(
                    ast.identifier,
                    symbol
                )
            }
            ast.scope = symbol
            super.visit(ast, symbol)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: LambdaAst, param: Scope<Symbol>) {
        try {
            val symbol: Scope<Symbol> = LambdaSymbol(param)
            ast.scope = symbol
            super.visit(ast, symbol)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: RecordDefinitionAst, param: Scope<Symbol>) {
        try {
            val symbol = if (ast.typeParams.isEmpty()) {
                val res = GroundRecordTypeSymbol(
                    param,
                    "${fileName}.${ast.identifier.name}",
                    ast.identifier,
                    userTypeFeatureSupport
                )
                res
            } else {
                val res = ParameterizedRecordTypeSymbol(
                    param,
                    ast.identifier,
                    userTypeFeatureSupport
                )
                ast.typeParams.forEach {
                    if (it.identifier == ast.identifier) {
                        errors.add(it.identifier.ctx, MaskingTypeParameter(it.identifier))
                    }
                }
                res
            }
            ast.definitionSpace = param
            if (ast.definitionSpace.existsHere(ast.identifier)) {
                errors.add(ast.ctx, IdentifierAlreadyExists(ast.identifier))
            } else {
                ast.definitionSpace.define(
                    ast.identifier,
                    symbol
                )
            }
            ast.scope = symbol
            super.visit(ast, symbol)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: ObjectDefinitionAst, param: Scope<Symbol>) {
        try {
            val symbol = ObjectSymbol(
                param,
                ast.identifier,
                userTypeFeatureSupport
            )
            ast.definitionSpace = param
            if (ast.definitionSpace.existsHere(ast.identifier)) {
                errors.add(ast.ctx, IdentifierAlreadyExists(ast.identifier))
            } else {
                ast.definitionSpace.define(
                    ast.identifier,
                    symbol
                )
            }
            ast.scope = symbol
            super.visit(ast, symbol)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: GroundApplyAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotApplyAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    // Do not call super for collection iterators
    override fun visit(ast: ForEachAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            ast.source.accept(this, param)
            val bodyScope = Block(param)
            ast.body.accept(this, bodyScope)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: AssignAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotAssignAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: IfAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: AsAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: IsAst, param: Scope<Symbol>) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }
}