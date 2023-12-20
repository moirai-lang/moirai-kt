package org.shardscript.semantics.phases.canonical

interface CanonicalAstVisitor<R> {
    fun visit(ast: NumberLiteralCanonicalAst): R
    fun visit(ast: BooleanLiteralCanonicalAst): R
    fun visit(ast: StringLiteralCanonicalAst): R
    fun visit(ast: StringInterpolationCanonicalAst): R
    fun visit(ast: LetCanonicalAst): R
    fun visit(ast: RefCanonicalAst): R
    fun visit(ast: FileCanonicalAst): R
    fun visit(ast: BlockCanonicalAst): R
    fun visit(ast: LambdaCanonicalAst): R
    fun visit(ast: DotCanonicalAst): R
    fun visit(ast: GroundApplyCanonicalAst): R
    fun visit(ast: DotApplyCanonicalAst): R
    fun visit(ast: ForEachCanonicalAst): R
    fun visit(ast: AssignCanonicalAst): R
    fun visit(ast: DotAssignCanonicalAst): R
    fun visit(ast: IfCanonicalAst): R
}

interface ParameterizedCanonicalAstVisitor<P, R> {
    fun visit(ast: NumberLiteralCanonicalAst, param: P): R
    fun visit(ast: BooleanLiteralCanonicalAst, param: P): R
    fun visit(ast: StringLiteralCanonicalAst, param: P): R
    fun visit(ast: StringInterpolationCanonicalAst, param: P): R
    fun visit(ast: LetCanonicalAst, param: P): R
    fun visit(ast: RefCanonicalAst, param: P): R
    fun visit(ast: FileCanonicalAst, param: P): R
    fun visit(ast: BlockCanonicalAst, param: P): R
    fun visit(ast: LambdaCanonicalAst, param: P): R
    fun visit(ast: DotCanonicalAst, param: P): R
    fun visit(ast: GroundApplyCanonicalAst, param: P): R
    fun visit(ast: DotApplyCanonicalAst, param: P): R
    fun visit(ast: ForEachCanonicalAst, param: P): R
    fun visit(ast: AssignCanonicalAst, param: P): R
    fun visit(ast: DotAssignCanonicalAst, param: P): R
    fun visit(ast: IfCanonicalAst, param: P): R
}
