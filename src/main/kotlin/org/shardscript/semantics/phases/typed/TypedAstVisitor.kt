package org.shardscript.semantics.phases.typed

interface TypedAstVisitor<R> {
    fun visit(ast: NumberLiteralTypedAst): R
    fun visit(ast: BooleanLiteralTypedAst): R
    fun visit(ast: StringLiteralTypedAst): R
    fun visit(ast: StringInterpolationTypedAst): R
    fun visit(ast: LetTypedAst): R
    fun visit(ast: RefTypedAst): R
    fun visit(ast: FileTypedAst): R
    fun visit(ast: BlockTypedAst): R
    fun visit(ast: FunctionTypedAst): R
    fun visit(ast: LambdaTypedAst): R
    fun visit(ast: RecordDefinitionTypedAst): R
    fun visit(ast: ObjectDefinitionTypedAst): R
    fun visit(ast: DotTypedAst): R
    fun visit(ast: GroundApplyTypedAst): R
    fun visit(ast: DotApplyTypedAst): R
    fun visit(ast: ForEachTypedAst): R
    fun visit(ast: AssignTypedAst): R
    fun visit(ast: DotAssignTypedAst): R
    fun visit(ast: IfTypedAst): R
}

interface ParameterizedTypedAstVisitor<P, R> {
    fun visit(ast: NumberLiteralTypedAst, param: P): R
    fun visit(ast: BooleanLiteralTypedAst, param: P): R
    fun visit(ast: StringLiteralTypedAst, param: P): R
    fun visit(ast: StringInterpolationTypedAst, param: P): R
    fun visit(ast: LetTypedAst, param: P): R
    fun visit(ast: RefTypedAst, param: P): R
    fun visit(ast: FileTypedAst, param: P): R
    fun visit(ast: BlockTypedAst, param: P): R
    fun visit(ast: FunctionTypedAst, param: P): R
    fun visit(ast: LambdaTypedAst, param: P): R
    fun visit(ast: RecordDefinitionTypedAst, param: P): R
    fun visit(ast: ObjectDefinitionTypedAst, param: P): R
    fun visit(ast: DotTypedAst, param: P): R
    fun visit(ast: GroundApplyTypedAst, param: P): R
    fun visit(ast: DotApplyTypedAst, param: P): R
    fun visit(ast: ForEachTypedAst, param: P): R
    fun visit(ast: AssignTypedAst, param: P): R
    fun visit(ast: DotAssignTypedAst, param: P): R
    fun visit(ast: IfTypedAst, param: P): R
}
