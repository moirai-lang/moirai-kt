package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

class RecordScanAstVisitor : UnitAstVisitor() {
    private fun scanRecord(ast: RecordDefinitionAst) {
        val recordSymbol = if (ast.typeParams.isEmpty()) {
            val groundRecordTypeSymbol = ast.scope as GroundRecordTypeSymbol
            groundRecordTypeSymbol.fields = ast.fields.map {
                val ofTypeSymbol = groundRecordTypeSymbol.fetch(it.ofType)
                val fieldSymbol = FieldSymbol(groundRecordTypeSymbol, it.gid, ofTypeSymbol, it.mutable)
                groundRecordTypeSymbol.define(it.gid, fieldSymbol)
                fieldSymbol
            }
            groundRecordTypeSymbol
        } else {
            val parameterizedRecordSymbol = ast.scope as ParameterizedRecordTypeSymbol
            parameterizedRecordSymbol.fields = ast.fields.map {
                val ofTypeSymbol = parameterizedRecordSymbol.fetch(it.ofType)
                val fieldSymbol = FieldSymbol(parameterizedRecordSymbol, it.gid, ofTypeSymbol, it.mutable)
                parameterizedRecordSymbol.define(it.gid, fieldSymbol)
                fieldSymbol
            }
            parameterizedRecordSymbol
        }
        recordSymbol.define(Identifier.thisId(), recordSymbol)
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