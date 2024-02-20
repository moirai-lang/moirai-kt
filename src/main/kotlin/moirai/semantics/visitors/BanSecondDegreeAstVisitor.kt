package moirai.semantics.visitors

import moirai.semantics.core.*

internal class BanSecondDegreeAstVisitor : UnitAstVisitor() {
    private fun checkForSecondDegree(ctx: SourceContext, identifier: Identifier, ofTypeSymbol: FunctionType) {
        if (ofTypeSymbol.returnType is FunctionType) {
            errors.add(ctx, FunctionReturnType(toError(identifier)))
            errors.add(ctx, SecondDegreeHigherOrderFunction(toError(identifier)))
        }
        ofTypeSymbol.formalParamTypes.forEach {
            if (it is FunctionType) {
                errors.add(ctx, SecondDegreeHigherOrderFunction(toError(identifier)))
            }
        }
    }

    override fun visit(ast: FunctionAst) {
        super.visit(ast)
        ast.formalParams.forEach {
            val ofTypeSymbol = ast.scope.fetchType(it.ofType)
            if (ofTypeSymbol is FunctionType) {
                checkForSecondDegree(ast.ctx, it.identifier, ofTypeSymbol)
            }
        }
    }
}