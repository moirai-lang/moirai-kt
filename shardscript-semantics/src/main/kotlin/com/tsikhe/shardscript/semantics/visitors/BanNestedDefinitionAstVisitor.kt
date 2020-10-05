package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

internal sealed class NestedDefinitionIndicator

internal object TopLevelIndicator : NestedDefinitionIndicator()
internal object WithinEnumIndicator : NestedDefinitionIndicator()
internal object WithinRecordIndicator : NestedDefinitionIndicator()
internal object OtherIndicator : NestedDefinitionIndicator()

internal class BanNestedDefinitionAstVisitor : ParameterizedUnitAstVisitor<NestedDefinitionIndicator>() {
    override fun visit(ast: StringInterpolationAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: LetAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: FileAst, param: NestedDefinitionIndicator) {
        super.visit(ast, TopLevelIndicator)
    }

    override fun visit(ast: BlockAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: FunctionAst, param: NestedDefinitionIndicator) {
        when (param) {
            is WithinRecordIndicator,
            is TopLevelIndicator -> Unit
            else -> errors.add(ast.ctx, InvalidDefinitionLocation(ast.gid))
        }
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: RecordDefinitionAst, param: NestedDefinitionIndicator) {
        when (param) {
            is WithinEnumIndicator,
            is TopLevelIndicator -> Unit
            else -> errors.add(ast.ctx, InvalidDefinitionLocation(ast.gid))
        }
        super.visit(ast, WithinRecordIndicator)
    }

    override fun visit(ast: ObjectDefinitionAst, param: NestedDefinitionIndicator) {
        when (param) {
            is WithinEnumIndicator,
            is TopLevelIndicator -> Unit
            else -> errors.add(ast.ctx, InvalidDefinitionLocation(ast.gid))
        }
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: EnumDefinitionAst, param: NestedDefinitionIndicator) {
        when (param) {
            is TopLevelIndicator -> Unit
            else -> errors.add(ast.ctx, InvalidDefinitionLocation(ast.gid))
        }
        super.visit(ast, WithinEnumIndicator)
    }

    override fun visit(ast: DotAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: GroundApplyAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: DotApplyAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: ForEachAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: MapAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: FlatMapAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: AssignAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: DotAssignAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: IfAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: SwitchAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: AsAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }

    override fun visit(ast: IsAst, param: NestedDefinitionIndicator) {
        super.visit(ast, OtherIndicator)
    }
}