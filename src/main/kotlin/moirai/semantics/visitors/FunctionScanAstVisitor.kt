package moirai.semantics.visitors

import moirai.semantics.core.*

internal class FunctionScanAstVisitor : UnitAstVisitor() {
    override fun visit(ast: FunctionAst) {
        try {
            if (ast.typeParams.isEmpty()) {
                val groundFunctionSymbol = ast.scope as GroundFunctionSymbol
                groundFunctionSymbol.formalParams = bindFormals(ast, groundFunctionSymbol)
                groundFunctionSymbol.returnType = groundFunctionSymbol.fetchType(ast.returnType)
            } else {
                val parameterizedFunctionSymbol = ast.scope as ParameterizedFunctionSymbol
                parameterizedFunctionSymbol.formalParams = bindFormals(ast, parameterizedFunctionSymbol)
                parameterizedFunctionSymbol.returnType = parameterizedFunctionSymbol.fetchType(ast.returnType)
            }
            super.visit(ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: LambdaAst) {
        try {
            val lambdaSymbol = ast.scope as LambdaSymbol

            val formalParams: MutableList<FunctionFormalParameterSymbol> = ArrayList()
            ast.formalParams.forEach {
                val ofType = lambdaSymbol.fetchType(it.ofType)
                val paramSymbol = FunctionFormalParameterSymbol(lambdaSymbol, it.identifier, ofType)
                lambdaSymbol.define(it.identifier, paramSymbol)
                formalParams.add(paramSymbol)
            }

            lambdaSymbol.formalParams = formalParams

            super.visit(ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    private fun bindFormals(ast: FunctionAst, scopeHere: Scope): List<FunctionFormalParameterSymbol> {
        val formalParams: MutableList<FunctionFormalParameterSymbol> = ArrayList()
        ast.formalParams.forEach {
            val ofType = scopeHere.fetchType(it.ofType)
            val paramSymbol = FunctionFormalParameterSymbol(scopeHere, it.identifier, ofType)
            scopeHere.define(it.identifier, paramSymbol)
            formalParams.add(paramSymbol)
        }
        return formalParams
    }
}