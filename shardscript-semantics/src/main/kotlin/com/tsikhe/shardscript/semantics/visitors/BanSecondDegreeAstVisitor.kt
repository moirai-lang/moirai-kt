package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

class BanSecondDegreeAstVisitor : UnitAstVisitor() {
    private fun checkForSecondDegree(ctx: SourceContext, gid: GroundIdentifier, ofTypeSymbol: FunctionTypeSymbol) {
        if (ofTypeSymbol.returnType is FunctionTypeSymbol) {
            errors.add(ctx, FunctionReturnType(gid))
            errors.add(ctx, SecondDegreeHigherOrderFunction(gid))
        }
        ofTypeSymbol.formalParamTypes.forEach {
            if (it is FunctionTypeSymbol) {
                errors.add(ctx, SecondDegreeHigherOrderFunction(gid))
            }
        }
    }

    override fun visit(ast: FunctionAst) {
        super.visit(ast)
        ast.formalParams.forEach {
            val ofTypeSymbol = ast.scope.fetch(it.ofType)
            if (ofTypeSymbol is FunctionTypeSymbol) {
                checkForSecondDegree(ast.ctx, it.gid, ofTypeSymbol)
            }
        }
    }
}