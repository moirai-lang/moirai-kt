package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class RecordScanAstVisitor : UnitAstVisitor() {
    private fun scanRecord(ast: RecordDefinitionAst) {
        if (ast.typeParams.isEmpty()) {
            val groundRecordType = ast.scope as GroundRecordType
            groundRecordType.fields = ast.fields.map {
                val ofTypeSymbol = groundRecordType.fetchType(it.ofType)
                val fieldSymbol = FieldSymbol(groundRecordType, it.identifier, ofTypeSymbol, it.mutable)
                groundRecordType.define(it.identifier, fieldSymbol)
                fieldSymbol
            }
        } else {
            val parameterizedRecordSymbol = ast.scope as ParameterizedRecordType
            parameterizedRecordSymbol.fields = ast.fields.map {
                val ofTypeSymbol = parameterizedRecordSymbol.fetchType(it.ofType)
                val fieldSymbol = FieldSymbol(parameterizedRecordSymbol, it.identifier, ofTypeSymbol, it.mutable)
                parameterizedRecordSymbol.define(it.identifier, fieldSymbol)
                fieldSymbol
            }
        }
    }

    override fun visit(ast: RecordDefinitionAst) {
        try {
            scanRecord(ast)
            super.visit(ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }
}