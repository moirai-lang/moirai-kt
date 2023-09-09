package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class FunctionScanAstVisitor : UnitAstVisitor() {
    override fun visit(ast: FunctionAst) {
        try {
            if (ast.typeParams.isEmpty()) {
                val groundFunctionSymbol = ast.scope as GroundFunctionSymbol
                groundFunctionSymbol.formalParams = bindFormals(ast, groundFunctionSymbol)
                groundFunctionSymbol.returnType = groundFunctionSymbol.fetch(ast.returnType)
            } else {
                val parameterizedFunctionSymbol = ast.scope as ParameterizedFunctionSymbol
                parameterizedFunctionSymbol.formalParams = bindFormals(ast, parameterizedFunctionSymbol)
                parameterizedFunctionSymbol.returnType = parameterizedFunctionSymbol.fetch(ast.returnType)
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
                val ofTypeSymbol = lambdaSymbol.fetch(it.ofType)
                val paramSymbol = FunctionFormalParameterSymbol(lambdaSymbol, it.identifier, ofTypeSymbol)
                lambdaSymbol.define(it.identifier, paramSymbol)
                formalParams.add(paramSymbol)
            }

            lambdaSymbol.formalParams = formalParams

            super.visit(ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    private fun bindFormals(ast: FunctionAst, scopeHere: Scope<Symbol>): List<FunctionFormalParameterSymbol> {
        val formalParams: MutableList<FunctionFormalParameterSymbol> = ArrayList()
        ast.formalParams.forEach {
            val ofTypeSymbol = scopeHere.fetch(it.ofType)
            val paramSymbol = FunctionFormalParameterSymbol(scopeHere, it.identifier, ofTypeSymbol)
            scopeHere.define(it.identifier, paramSymbol)
            formalParams.add(paramSymbol)
        }
        return formalParams
    }
}