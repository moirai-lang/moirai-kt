package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

sealed class RecordMode

data class EnumRecord(val enumGid: Identifier) : RecordMode()
data object RecordDef : RecordMode()

class ParameterScanAstVisitor(private val fileName: String) : UnitAstVisitor() {
    private fun qualifiedName(parentId: Identifier, id: Identifier): String =
        "${fileName}.${parentId.name}.${id.name}"

    private fun scanRecord(ast: RecordDefinitionAst, recordMode: RecordMode) {
        if (ast.typeParams.isNotEmpty()) {
            val parameterizedRecordSymbol = ast.scope as ParameterizedRecordTypeSymbol
            when (recordMode) {
                is EnumRecord -> {
                    errors.add(
                        ast.ctx,
                        ParameterizedGroundMismatch(recordMode.enumGid, parameterizedRecordSymbol.identifier)
                    )
                }
                is RecordDef -> {
                    val seenTypeParameters: MutableSet<String> = HashSet()
                    parameterizedRecordSymbol.typeParams = ast.typeParams.map {
                        if (it.type == TypeParameterKind.Fin) {
                            val typeParam = ImmutableFinTypeParameter(qualifiedName(ast.identifier, it.identifier), it.identifier)
                            val postFix = it.identifier.name
                            if (seenTypeParameters.contains(postFix)) {
                                errors.add(it.identifier.ctx, DuplicateTypeParameter(it.identifier))
                            } else {
                                seenTypeParameters.add(postFix)
                                parameterizedRecordSymbol.define(it.identifier, typeParam)
                            }
                            typeParam
                        } else {
                            val typeParam = StandardTypeParameter(qualifiedName(ast.identifier, it.identifier), it.identifier)
                            if (seenTypeParameters.contains(it.identifier.name)) {
                                errors.add(it.identifier.ctx, DuplicateTypeParameter(it.identifier))
                            } else {
                                seenTypeParameters.add(it.identifier.name)
                                parameterizedRecordSymbol.define(it.identifier, typeParam)
                            }
                            typeParam
                        }
                    }
                }
            }
        }
    }

    override fun visit(ast: FunctionAst) {
        try {
            if (ast.typeParams.isNotEmpty()) {
                val parameterizedFunctionSymbol = ast.scope as ParameterizedFunctionSymbol
                val seenTypeParameters: MutableSet<String> = HashSet()
                parameterizedFunctionSymbol.typeParams = ast.typeParams.map {
                    if (it.type == TypeParameterKind.Fin) {
                        val typeParam = ImmutableFinTypeParameter(qualifiedName(ast.identifier, it.identifier), it.identifier)
                        val postFix = it.identifier.name
                        if (seenTypeParameters.contains(postFix)) {
                            errors.add(it.identifier.ctx, DuplicateTypeParameter(it.identifier))
                        } else {
                            seenTypeParameters.add(postFix)
                            parameterizedFunctionSymbol.define(it.identifier, typeParam)
                        }
                        typeParam
                    } else {
                        val typeParam = StandardTypeParameter(qualifiedName(ast.identifier, it.identifier), it.identifier)
                        if (seenTypeParameters.contains(it.identifier.name)) {
                            errors.add(it.identifier.ctx, DuplicateTypeParameter(it.identifier))
                        } else {
                            seenTypeParameters.add(it.identifier.name)
                            parameterizedFunctionSymbol.define(it.identifier, typeParam)
                        }
                        typeParam
                    }
                }
            }
            super.visit(ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }

    override fun visit(ast: RecordDefinitionAst) {
        try {
            scanRecord(ast, RecordDef)
            super.visit(ast)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }
}