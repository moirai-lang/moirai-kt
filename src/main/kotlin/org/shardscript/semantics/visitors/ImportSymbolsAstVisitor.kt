package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class ImportSymbolsAstVisitor(private val importTable: ImportTable) : UnitAstVisitor() {
    override fun visit(ast: FunctionAst) {
        importTable.register(ast.identifier, ast.definitionSpace)
    }

    override fun visit(ast: RecordDefinitionAst) {
        importTable.register(ast.identifier, ast.definitionSpace)
    }

    override fun visit(ast: ObjectDefinitionAst) {
        importTable.register(ast.identifier, ast.definitionSpace)
    }
}