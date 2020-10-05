package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

internal class ResurrectWhitelistAstVisitor : UnitAstVisitor() {
    override fun visit(ast: StringInterpolationAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: LetAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: RefAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: BlockAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: FunctionAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: RecordDefinitionAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: ObjectDefinitionAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: EnumDefinitionAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: DotAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: DotApplyAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: ForEachAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: MapAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: FlatMapAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: AssignAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: DotAssignAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: IfAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: SwitchAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }

    override fun visit(ast: IsAst) {
        errors.add(ast.ctx, ResurrectWhitelistError)
    }
}