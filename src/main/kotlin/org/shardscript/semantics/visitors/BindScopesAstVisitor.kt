package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

// Omit FileAst, root scope created in earlier phases
class BindScopesAstVisitor(
    val architecture: Architecture,
    val fileName: String
) :
    ParameterizedUnitAstVisitor<Scope>() {
    override fun visit(ast: IntLiteralAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DecimalLiteralAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: BooleanLiteralAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: CharLiteralAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: StringLiteralAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: StringInterpolationAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: LetAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: RefAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: BlockAst, param: Scope) {
        try {
            val blockScope = Block(param)
            ast.scope = blockScope
            super.visit(ast, blockScope)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: FunctionAst, param: Scope) {
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

    override fun visit(ast: LambdaAst, param: Scope) {
        try {
            val symbol: Scope = LambdaSymbol(param)
            ast.scope = symbol
            super.visit(ast, symbol)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: RecordDefinitionAst, param: Scope) {
        try {
            val type = if (ast.typeParams.isEmpty()) {
                val res = GroundRecordTypeSymbol(
                    param,
                    "${fileName}.${ast.identifier.name}",
                    ast.identifier
                )
                res
            } else {
                val res = ParameterizedRecordTypeSymbol(
                    param,
                    "${fileName}.${ast.identifier.name}",
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
                ast.definitionSpace.defineType(
                    ast.identifier,
                    type
                )
            }
            ast.scope = type
            super.visit(ast, type)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: ObjectDefinitionAst, param: Scope) {
        try {
            val type = ObjectSymbol(
                "${fileName}.${ast.identifier.name}",
                ast.identifier,
                userTypeFeatureSupport
            )
            ast.definitionSpace = param
            if (ast.definitionSpace.existsHere(ast.identifier)) {
                errors.add(ast.ctx, IdentifierAlreadyExists(ast.identifier))
            } else {
                ast.definitionSpace.defineType(
                    ast.identifier,
                    type
                )
            }
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: GroundApplyAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotApplyAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    // Do not call super for collection iterators
    override fun visit(ast: ForEachAst, param: Scope) {
        try {
            ast.scope = param
            ast.source.accept(this, param)
            val bodyScope = Block(param)
            ast.body.accept(this, bodyScope)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: AssignAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotAssignAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: IfAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: AsAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: IsAst, param: Scope) {
        try {
            ast.scope = param
            super.visit(ast, param)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }
}