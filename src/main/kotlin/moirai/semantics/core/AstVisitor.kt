package moirai.semantics.core

internal interface AstVisitor<R> {
    fun visit(ast: IntLiteralAst): R
    fun visit(ast: DecimalLiteralAst): R
    fun visit(ast: BooleanLiteralAst): R
    fun visit(ast: CharLiteralAst): R
    fun visit(ast: StringLiteralAst): R
    fun visit(ast: StringInterpolationAst): R
    fun visit(ast: LetAst): R
    fun visit(ast: RefAst): R
    fun visit(ast: FileAst): R
    fun visit(ast: BlockAst): R
    fun visit(ast: FunctionAst): R
    fun visit(ast: LambdaAst): R
    fun visit(ast: RecordDefinitionAst): R
    fun visit(ast: ObjectDefinitionAst): R
    fun visit(ast: DotAst): R
    fun visit(ast: GroundApplyAst): R
    fun visit(ast: DotApplyAst): R
    fun visit(ast: ForEachAst): R
    fun visit(ast: AssignAst): R
    fun visit(ast: DotAssignAst): R
    fun visit(ast: IfAst): R
    fun visit(ast: MatchAst): R
}

internal interface ParameterizedAstVisitor<P, R> {
    fun visit(ast: IntLiteralAst, param: P): R
    fun visit(ast: DecimalLiteralAst, param: P): R
    fun visit(ast: BooleanLiteralAst, param: P): R
    fun visit(ast: CharLiteralAst, param: P): R
    fun visit(ast: StringLiteralAst, param: P): R
    fun visit(ast: StringInterpolationAst, param: P): R
    fun visit(ast: LetAst, param: P): R
    fun visit(ast: RefAst, param: P): R
    fun visit(ast: FileAst, param: P): R
    fun visit(ast: BlockAst, param: P): R
    fun visit(ast: FunctionAst, param: P): R
    fun visit(ast: LambdaAst, param: P): R
    fun visit(ast: RecordDefinitionAst, param: P): R
    fun visit(ast: ObjectDefinitionAst, param: P): R
    fun visit(ast: DotAst, param: P): R
    fun visit(ast: GroundApplyAst, param: P): R
    fun visit(ast: DotApplyAst, param: P): R
    fun visit(ast: ForEachAst, param: P): R
    fun visit(ast: AssignAst, param: P): R
    fun visit(ast: DotAssignAst, param: P): R
    fun visit(ast: IfAst, param: P): R
    fun visit(ast: MatchAst, param: P): R
}
