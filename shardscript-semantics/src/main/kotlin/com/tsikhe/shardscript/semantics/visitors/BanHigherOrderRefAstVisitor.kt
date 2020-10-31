package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.CannotRefFunctionParam
import com.tsikhe.shardscript.semantics.core.FunctionFormalParameterSymbol
import com.tsikhe.shardscript.semantics.core.FunctionTypeSymbol
import com.tsikhe.shardscript.semantics.core.RefAst

internal class BanHigherOrderRefAstVisitor : UnitAstVisitor() {
    override fun visit(ast: RefAst) {
        if (ast.symbolRef is FunctionFormalParameterSymbol) {
            if (ast.readType() is FunctionTypeSymbol) {
                errors.add(ast.ctx, CannotRefFunctionParam(ast.gid))
            }
        }
    }
}
