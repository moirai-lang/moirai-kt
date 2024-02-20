package moirai.semantics.visitors

import moirai.semantics.core.*

internal class BanHigherOrderRefAstVisitor : UnitAstVisitor() {
    override fun visit(ast: RefAst) {
        if (ast.refSlot is RefSlotFormal) {
            if (ast.readType() is FunctionType) {
                errors.add(ast.ctx, CannotRefFunctionParam(toError(ast.identifier)))
            }
        }
    }
}