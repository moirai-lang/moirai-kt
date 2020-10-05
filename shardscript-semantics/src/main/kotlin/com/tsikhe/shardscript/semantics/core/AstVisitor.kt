package com.tsikhe.shardscript.semantics.core

interface AstVisitor<R> {
    fun visit(ast: SByteLiteralAst): R
    fun visit(ast: ShortLiteralAst): R
    fun visit(ast: IntLiteralAst): R
    fun visit(ast: LongLiteralAst): R
    fun visit(ast: ByteLiteralAst): R
    fun visit(ast: UShortLiteralAst): R
    fun visit(ast: UIntLiteralAst): R
    fun visit(ast: ULongLiteralAst): R
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
    fun visit(ast: RecordDefinitionAst): R
    fun visit(ast: ObjectDefinitionAst): R
    fun visit(ast: EnumDefinitionAst): R
    fun visit(ast: DotAst): R
    fun visit(ast: GroundApplyAst): R
    fun visit(ast: DotApplyAst): R
    fun visit(ast: ForEachAst): R
    fun visit(ast: MapAst): R
    fun visit(ast: FlatMapAst): R
    fun visit(ast: AssignAst): R
    fun visit(ast: DotAssignAst): R
    fun visit(ast: IfAst): R
    fun visit(ast: SwitchAst): R
    fun visit(ast: AsAst): R
    fun visit(ast: IsAst): R
}

interface ParameterizedAstVisitor<P, R> {
    fun visit(ast: SByteLiteralAst, param: P): R
    fun visit(ast: ShortLiteralAst, param: P): R
    fun visit(ast: IntLiteralAst, param: P): R
    fun visit(ast: LongLiteralAst, param: P): R
    fun visit(ast: ByteLiteralAst, param: P): R
    fun visit(ast: UShortLiteralAst, param: P): R
    fun visit(ast: UIntLiteralAst, param: P): R
    fun visit(ast: ULongLiteralAst, param: P): R
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
    fun visit(ast: RecordDefinitionAst, param: P): R
    fun visit(ast: ObjectDefinitionAst, param: P): R
    fun visit(ast: EnumDefinitionAst, param: P): R
    fun visit(ast: DotAst, param: P): R
    fun visit(ast: GroundApplyAst, param: P): R
    fun visit(ast: DotApplyAst, param: P): R
    fun visit(ast: ForEachAst, param: P): R
    fun visit(ast: MapAst, param: P): R
    fun visit(ast: FlatMapAst, param: P): R
    fun visit(ast: AssignAst, param: P): R
    fun visit(ast: DotAssignAst, param: P): R
    fun visit(ast: IfAst, param: P): R
    fun visit(ast: SwitchAst, param: P): R
    fun visit(ast: AsAst, param: P): R
    fun visit(ast: IsAst, param: P): R
}
