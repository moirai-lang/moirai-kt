package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

internal sealed class RecordMode

internal data class EnumRecord(val enumGid: GroundIdentifier, val parameterized: Boolean) : RecordMode()
internal object RecordDef : RecordMode()

internal class ParameterScanAstVisitor : UnitAstVisitor() {
    private fun scanRecord(ast: RecordDefinitionAst, recordMode: RecordMode) {
        if (ast.typeParams.isNotEmpty()) {
            val parameterizedRecordSymbol = ast.scope as ParameterizedRecordTypeSymbol
            when (recordMode) {
                is EnumRecord -> {
                    if (!recordMode.parameterized) {
                        errors.add(
                            ast.ctx,
                            ParameterizedGroundMismatch(recordMode.enumGid, parameterizedRecordSymbol.gid)
                        )
                    } else {
                        val parent = parameterizedRecordSymbol.parent as ParameterizedCoproductSymbol
                        val typeParams: MutableList<TypeParameter> = ArrayList()
                        ast.typeParams.forEach {
                            if (parent.existsHere(it)) {
                                val param = parent.fetchHere(it)
                                if (param is TypeParameter) {
                                    typeParams.add(param)
                                } else {
                                    errors.add(ast.ctx, NotATypeParameter(param))
                                }
                            } else {
                                errors.add(ast.ctx, EnumRecordTypeParamMissing(it))
                            }
                        }
                        parameterizedRecordSymbol.typeParams = typeParams
                        typeParams.forEach {
                            parameterizedRecordSymbol.define(it.gid, it)
                        }
                    }
                }
                is RecordDef -> {
                    val seenTypeParameters: MutableSet<String> = HashSet()
                    parameterizedRecordSymbol.typeParams = ast.typeParams.map {
                        if (it.name.startsWith("#")) {
                            val typeParam = ImmutableOmicronTypeParameter(parameterizedRecordSymbol, it)
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
        } else {
            val groundRecordTypeSymbol = ast.scope as GroundRecordTypeSymbol
            when (recordMode) {
                is EnumRecord -> {
                    if (recordMode.parameterized) {
                        errors.add(ast.ctx, ParameterizedGroundMismatch(groundRecordTypeSymbol.gid, recordMode.enumGid))
                    }
                }
                is RecordDef -> Unit
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
                        val typeParam = ImmutableOmicronTypeParameter(parameterizedFunctionSymbol, it)
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

    override fun visit(ast: EnumDefinitionAst) {
        try {
            if (ast.typeParams.isNotEmpty()) {
                val parameterizedEnumSymbol = ast.scope as ParameterizedCoproductSymbol
                val seenTypeParameters: MutableSet<String> = HashSet()
                parameterizedEnumSymbol.typeParams = ast.typeParams.map {
                    if (it.name.startsWith("#")) {
                        val typeParam = ImmutableOmicronTypeParameter(parameterizedEnumSymbol, it)
                        val postFix = it.name.substringAfter("#")
                        if (seenTypeParameters.contains(postFix)) {
                            errors.add(it.ctx, DuplicateTypeParameter(it))
                        } else {
                            seenTypeParameters.add(postFix)
                            parameterizedEnumSymbol.define(it, typeParam)
                        }
                        typeParam
                    } else {
                        val typeParam = StandardTypeParameter(parameterizedEnumSymbol, it)
                        if (seenTypeParameters.contains(it.name)) {
                            errors.add(it.ctx, DuplicateTypeParameter(it))
                        } else {
                            seenTypeParameters.add(it.name)
                            parameterizedEnumSymbol.define(it, typeParam)
                        }
                        typeParam
                    }
                }
                val enumRecord = EnumRecord(parameterizedEnumSymbol.gid, true)
                ast.records.forEach {
                    scanRecord(it, enumRecord)
                    super.visit(it)
                }
            } else {
                val groundCoproductSymbol = ast.scope as GroundCoproductSymbol
                val enumRecord = EnumRecord(groundCoproductSymbol.gid, false)
                ast.records.forEach {
                    scanRecord(it, enumRecord)
                    super.visit(it)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
        }
    }
}
