package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class BanLambdaInvalidSubExpressions : UnitAstVisitor() {
    private var lambdaDepth = 0

    override fun visit(ast: ForEachAst) {
        super.visit(ast)

        if(lambdaDepth  != 0) {
            errors.add(ast.ctx, LambdaForLoop)
        }
    }

    override fun visit(ast: LambdaAst) {
        lambdaDepth += 1;

        super.visit(ast)

        lambdaDepth -= 1;
    }
}