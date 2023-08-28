package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.CannotRefFunctionParam
import org.shardscript.semantics.core.FunctionFormalParameterSymbol
import org.shardscript.semantics.core.FunctionTypeSymbol
import org.shardscript.semantics.core.RefAst

class BanHigherOrderRefAstVisitor : UnitAstVisitor() {
    override fun visit(ast: RefAst) {
        if (ast.symbolRef is FunctionFormalParameterSymbol) {
            if (ast.readType() is FunctionTypeSymbol) {
                errors.add(ast.ctx, CannotRefFunctionParam(ast.identifier))
            }
        }
    }
}