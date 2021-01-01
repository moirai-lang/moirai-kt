package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.prelude.Lang

class CheckTypesAstVisitor(private val prelude: PreludeTable) : UnitAstVisitor() {
    override fun visit(ast: StringInterpolationAst) {
        try {
            super.visit(ast)
            ast.components.forEach {
                if (!isValidStringType(it.readType())) {
                    errors.add(it.ctx, IncompatibleString(it.readType()))
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: LetAst) {
        try {
            super.visit(ast)
            checkTypes(ast.ctx, prelude, errors, ast.ofTypeSymbol, ast.rhs.readType())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: FileAst) {
        try {
            super.visit(ast)
            if (ast.lines.isNotEmpty()) {
                checkTypes(ast.ctx, prelude, errors, ast.readType(), ast.lines.last().readType())
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: BlockAst) {
        try {
            super.visit(ast)
            if (ast.lines.isNotEmpty()) {
                checkTypes(ast.ctx, prelude, errors, ast.readType(), ast.lines.last().readType())
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: FunctionAst) {
        try {
            super.visit(ast)
            val returnType = ast.scope.fetch(ast.returnType)
            checkTypes(ast.ctx, prelude, errors, returnType, ast.body.readType())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: GroundApplyAst) {
        try {
            super.visit(ast)
            checkApply(prelude, errors, ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotApplyAst) {
        try {
            super.visit(ast)
            checkApply(prelude, errors, ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: ForEachAst) {
        try {
            super.visit(ast)
            checkTypes(ast.ctx, prelude, errors, ast.ofTypeSymbol, ast.sourceTypeSymbol)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: AssignAst) {
        try {
            super.visit(ast)
            val symbolRef = ast.symbolRef as LocalVariableSymbol
            if (!symbolRef.mutable) {
                errors.add(ast.ctx, ImmutableAssign(symbolRef))
            }
            checkTypes(ast.ctx, prelude, errors, symbolRef.ofTypeSymbol, ast.rhs.readType())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotAssignAst) {
        try {
            super.visit(ast)
            val symbolRef = ast.symbolRef as FieldSymbol
            if (!symbolRef.mutable) {
                errors.add(ast.ctx, ImmutableAssign(symbolRef))
            }
            checkTypes(ast.ctx, prelude, errors, symbolRef.ofTypeSymbol, ast.rhs.readType())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: IfAst) {
        try {
            super.visit(ast)
            checkTypes(ast.condition.ctx, prelude, errors, prelude.fetch(Lang.booleanId), ast.condition.readType())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: AsAst) {
        try {
            super.visit(ast)
            checkTypes(ast.ctx, prelude, errors, ast.readType(), ast.lhs.readType())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: IsAst) {
        try {
            super.visit(ast)
            val testErrors = LanguageErrors()
            checkTypes(ast.ctx, prelude, testErrors, ast.identifierSymbol, ast.lhs.readType())
            if (testErrors.toSet().isEmpty()) {
                ast.result = BooleanValue(true)
            } else {
                ast.result = BooleanValue(false)
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }
}