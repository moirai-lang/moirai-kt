package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class BanSecondDegreeAstVisitor : UnitAstVisitor() {
    private fun checkForSecondDegree(ctx: SourceContext, identifier: Identifier, ofTypeSymbol: FunctionTypeSymbol) {
        if (ofTypeSymbol.returnType is FunctionTypeSymbol) {
            errors.add(ctx, FunctionReturnType(identifier))
            errors.add(ctx, SecondDegreeHigherOrderFunction(identifier))
        }
        ofTypeSymbol.formalParamTypes.forEach {
            if (it is FunctionTypeSymbol) {
                errors.add(ctx, SecondDegreeHigherOrderFunction(identifier))
            }
        }
    }

    override fun visit(ast: FunctionAst) {
        super.visit(ast)
        ast.formalParams.forEach {
            val ofTypeSymbol = ast.scope.fetchType(it.ofType)
            if (ofTypeSymbol is FunctionTypeSymbol) {
                checkForSecondDegree(ast.ctx, it.identifier, ofTypeSymbol)
            }
        }
    }
}