package org.shardscript.semantics.phases.parse

interface PostParseAstVisitor<R> {
    fun visit(ast: NumberLiteralPostParseAst): R
    fun visit(ast: BooleanLiteralPostParseAst): R
    fun visit(ast: StringLiteralPostParseAst): R
    fun visit(ast: StringInterpolationPostParseAst): R
    fun visit(ast: LetPostParseAst): R
    fun visit(ast: RefPostParseAst): R
    fun visit(ast: FilePostParseAst): R
    fun visit(ast: BlockPostParseAst): R
    fun visit(ast: FunctionPostParseAst): R
    fun visit(ast: LambdaPostParseAst): R
    fun visit(ast: RecordDefinitionPostParseAst): R
    fun visit(ast: ObjectDefinitionPostParseAst): R
    fun visit(ast: DotPostParseAst): R
    fun visit(ast: GroundApplyPostParseAst): R
    fun visit(ast: DotApplyPostParseAst): R
    fun visit(ast: ForEachPostParseAst): R
    fun visit(ast: AssignPostParseAst): R
    fun visit(ast: DotAssignPostParseAst): R
    fun visit(ast: IfPostParseAst): R
}

interface ParameterizedPostParseAstVisitor<P, R> {
    fun visit(ast: NumberLiteralPostParseAst, param: P): R
    fun visit(ast: BooleanLiteralPostParseAst, param: P): R
    fun visit(ast: StringLiteralPostParseAst, param: P): R
    fun visit(ast: StringInterpolationPostParseAst, param: P): R
    fun visit(ast: LetPostParseAst, param: P): R
    fun visit(ast: RefPostParseAst, param: P): R
    fun visit(ast: FilePostParseAst, param: P): R
    fun visit(ast: BlockPostParseAst, param: P): R
    fun visit(ast: FunctionPostParseAst, param: P): R
    fun visit(ast: LambdaPostParseAst, param: P): R
    fun visit(ast: RecordDefinitionPostParseAst, param: P): R
    fun visit(ast: ObjectDefinitionPostParseAst, param: P): R
    fun visit(ast: DotPostParseAst, param: P): R
    fun visit(ast: GroundApplyPostParseAst, param: P): R
    fun visit(ast: DotApplyPostParseAst, param: P): R
    fun visit(ast: ForEachPostParseAst, param: P): R
    fun visit(ast: AssignPostParseAst, param: P): R
    fun visit(ast: DotAssignPostParseAst, param: P): R
    fun visit(ast: IfPostParseAst, param: P): R
}
