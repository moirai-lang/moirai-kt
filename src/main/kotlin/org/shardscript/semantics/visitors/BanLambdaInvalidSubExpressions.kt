package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class BanLambdaInvalidSubExpressions : UnitAstVisitor() {
    var lambdaDepth = 0

    override fun visit(ast: GroundApplyAst) {
        super.visit(ast)

        if(lambdaDepth  != 0) {
            errors.add(ast.ctx, LambdaApply(ast.signifier))
        }
    }

    override fun visit(ast: DotApplyAst) {
        super.visit(ast)

        if(lambdaDepth  != 0) {
            errors.add(ast.ctx, LambdaApply(ast.signifier))
        }
    }

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