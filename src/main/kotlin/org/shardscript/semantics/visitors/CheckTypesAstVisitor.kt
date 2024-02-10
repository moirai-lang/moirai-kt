package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*
import org.shardscript.semantics.prelude.Lang

class CheckTypesAstVisitor(private val prelude: Scope) : UnitAstVisitor() {
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
            val returnType = ast.scope.fetchType(ast.returnType)
            checkTypes(ast.ctx, prelude, errors, returnType, ast.body.readType())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: GroundApplyAst) {
        try {
            super.visit(ast)
            checkApply(prelude, errors, ast, ast.args)
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
            when (val assignSlot = ast.assignSlot) {
                is AssignSlotLVS -> {
                    val symbolRef = assignSlot.payload
                    if (!symbolRef.mutable) {
                        errors.add(ast.ctx, ImmutableAssign(symbolRef))
                    }
                    checkTypes(ast.ctx, prelude, errors, symbolRef.ofTypeSymbol, ast.rhs.readType())
                }
                else -> {
                    errors.add(ast.ctx, InvalidAssign)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: DotAssignAst) {
        try {
            super.visit(ast)
            when (val dotAssignSlot = ast.dotAssignSlot) {
                is DotAssignSlotField -> {
                    val symbolRef = dotAssignSlot.payload
                    if (!symbolRef.mutable) {
                        errors.add(ast.ctx, ImmutableAssign(symbolRef))
                    }
                    checkTypes(ast.ctx, prelude, errors, symbolRef.ofTypeSymbol, ast.rhs.readType())
                }

                else -> {
                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: IfAst) {
        try {
            super.visit(ast)
            checkTypes(ast.condition.ctx, prelude, errors, prelude.fetchType(Lang.booleanId), ast.condition.readType())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: MatchAst) {
        try {
            super.visit(ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }
}