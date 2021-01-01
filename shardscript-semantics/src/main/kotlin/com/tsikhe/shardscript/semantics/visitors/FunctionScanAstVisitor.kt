package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

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

    private fun bindFormals(ast: FunctionAst, parent: Scope<Symbol>): List<FunctionFormalParameterSymbol> {
        val formalParams: MutableList<FunctionFormalParameterSymbol> = ArrayList()
        ast.formalParams.forEach {
            val ofTypeSymbol = parent.fetch(it.ofType)
            val paramSymbol = FunctionFormalParameterSymbol(parent, it.gid, ofTypeSymbol)
            parent.define(it.gid, paramSymbol)
            formalParams.add(paramSymbol)
        }
        return formalParams
    }
}