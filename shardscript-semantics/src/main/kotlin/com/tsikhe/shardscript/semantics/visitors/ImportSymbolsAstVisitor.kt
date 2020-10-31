package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

internal class ImportSymbolsAstVisitor(val importTable: ImportTable) : UnitAstVisitor() {
    override fun visit(ast: FunctionAst) {
        importTable.register(ast.gid, ast.definitionSpace)
    }

    override fun visit(ast: RecordDefinitionAst) {
        importTable.register(ast.gid, ast.definitionSpace)
    }

    override fun visit(ast: ObjectDefinitionAst) {
        importTable.register(ast.gid, ast.definitionSpace)
    }

    override fun visit(ast: EnumDefinitionAst) {
        importTable.register(ast.gid, ast.definitionSpace)
        ast.records.forEach {
            importTable.register(ast.gid, ast.definitionSpace)
        }
        ast.objects.forEach {
            importTable.register(ast.gid, ast.definitionSpace)
        }
    }
}
