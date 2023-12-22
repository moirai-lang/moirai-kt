package org.shardscript.semantics.phases.parse

interface ScopedAstVisitor<R> {
    fun visit(ast: NumberLiteralScopedAst): R
    fun visit(ast: BooleanLiteralScopedAst): R
    fun visit(ast: StringLiteralScopedAst): R
    fun visit(ast: StringInterpolationScopedAst): R
    fun visit(ast: LetScopedAst): R
    fun visit(ast: RefScopedAst): R
    fun visit(ast: FileScopedAst): R
    fun visit(ast: BlockScopedAst): R
    fun visit(ast: FunctionScopedAst): R
    fun visit(ast: LambdaScopedAst): R
    fun visit(ast: RecordDefinitionScopedAst): R
    fun visit(ast: ObjectDefinitionScopedAst): R
    fun visit(ast: DotScopedAst): R
    fun visit(ast: GroundApplyScopedAst): R
    fun visit(ast: DotApplyScopedAst): R
    fun visit(ast: ForEachScopedAst): R
    fun visit(ast: AssignScopedAst): R
    fun visit(ast: DotAssignScopedAst): R
    fun visit(ast: IfScopedAst): R
}

interface ParameterizedScopedAstVisitor<P, R> {
    fun visit(ast: NumberLiteralScopedAst, param: P): R
    fun visit(ast: BooleanLiteralScopedAst, param: P): R
    fun visit(ast: StringLiteralScopedAst, param: P): R
    fun visit(ast: StringInterpolationScopedAst, param: P): R
    fun visit(ast: LetScopedAst, param: P): R
    fun visit(ast: RefScopedAst, param: P): R
    fun visit(ast: FileScopedAst, param: P): R
    fun visit(ast: BlockScopedAst, param: P): R
    fun visit(ast: FunctionScopedAst, param: P): R
    fun visit(ast: LambdaScopedAst, param: P): R
    fun visit(ast: RecordDefinitionScopedAst, param: P): R
    fun visit(ast: ObjectDefinitionScopedAst, param: P): R
    fun visit(ast: DotScopedAst, param: P): R
    fun visit(ast: GroundApplyScopedAst, param: P): R
    fun visit(ast: DotApplyScopedAst, param: P): R
    fun visit(ast: ForEachScopedAst, param: P): R
    fun visit(ast: AssignScopedAst, param: P): R
    fun visit(ast: DotAssignScopedAst, param: P): R
    fun visit(ast: IfScopedAst, param: P): R
}
