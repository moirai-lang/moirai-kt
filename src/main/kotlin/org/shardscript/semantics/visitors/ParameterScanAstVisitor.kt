package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

sealed class RecordMode

data class EnumRecord(val enumGid: Identifier) : RecordMode()
object RecordDef : RecordMode()

class ParameterScanAstVisitor : UnitAstVisitor() {
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
                        if (it.name.startsWith("#")) {
                            val typeParam = ImmutableFinTypeParameter(parameterizedRecordSymbol, it)
                            val postFix = it.name.substringAfter("#")
                            if (seenTypeParameters.contains(postFix)) {
                                errors.add(it.ctx, DuplicateTypeParameter(it))
                            } else {
                                seenTypeParameters.add(postFix)
                                parameterizedRecordSymbol.define(it, typeParam)
                            }
                            typeParam
                        } else {
                            val typeParam = StandardTypeParameter(parameterizedRecordSymbol, it)
                            if (seenTypeParameters.contains(it.name)) {
                                errors.add(it.ctx, DuplicateTypeParameter(it))
                            } else {
                                seenTypeParameters.add(it.name)
                                parameterizedRecordSymbol.define(it, typeParam)
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
                    if (it.name.startsWith("#")) {
                        val typeParam = ImmutableFinTypeParameter(parameterizedFunctionSymbol, it)
                        val postFix = it.name.substringAfter("#")
                        if (seenTypeParameters.contains(postFix)) {
                            errors.add(it.ctx, DuplicateTypeParameter(it))
                        } else {
                            seenTypeParameters.add(postFix)
                            parameterizedFunctionSymbol.define(it, typeParam)
                        }
                        typeParam
                    } else {
                        val typeParam = StandardTypeParameter(parameterizedFunctionSymbol, it)
                        if (seenTypeParameters.contains(it.name)) {
                            errors.add(it.ctx, DuplicateTypeParameter(it))
                        } else {
                            seenTypeParameters.add(it.name)
                            parameterizedFunctionSymbol.define(it, typeParam)
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