package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class BanHigherOrderRefAstVisitor : UnitAstVisitor() {
    override fun visit(ast: RefAst) {
        if (ast.refSlot is RefSlotFormal) {
            if (ast.readType() is FunctionType) {
                errors.add(ast.ctx, CannotRefFunctionParam(ast.identifier))
            }
        }
    }
}