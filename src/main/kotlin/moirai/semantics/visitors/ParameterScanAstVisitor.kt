package moirai.semantics.visitors

import moirai.semantics.core.*

internal sealed class RecordMode

internal data class EnumRecord(val enumGid: Identifier) : RecordMode()
internal data object RecordDef : RecordMode()

internal fun qualifiedName(fileName: String, parentId: Identifier, id: Identifier): String =
    "${fileName}.${parentId.name}.${id.name}"

internal class ParameterScanAstVisitor(private val fileName: String) : UnitAstVisitor() {
    private fun scanRecord(ast: RecordDefinitionAst, recordMode: RecordMode) {
        if (ast.typeParams.isNotEmpty()) {
            val parameterizedRecordSymbol = ast.scope as ParameterizedRecordType
            when (recordMode) {
                is EnumRecord -> {
                    errors.add(
                        ast.ctx,
                        ParameterizedGroundMismatch(toError(recordMode.enumGid), toError(parameterizedRecordSymbol.identifier))
                    )
                }
                is RecordDef -> {
                    val seenTypeParameters: MutableSet<String> = HashSet()
                    parameterizedRecordSymbol.typeParams = ast.typeParams.map {
                        if (it.type == TypeParameterKind.Fin) {
                            val typeParam = FinTypeParameter(qualifiedName(fileName, ast.identifier, it.identifier), it.identifier)
                            val postFix = it.identifier.name
                            if (seenTypeParameters.contains(postFix)) {
                                errors.add(it.identifier.ctx, DuplicateTypeParameter(toError(it.identifier)))
                            } else {
                                seenTypeParameters.add(postFix)
                                parameterizedRecordSymbol.defineType(it.identifier, typeParam)
                            }
                            typeParam
                        } else {
                            val typeParam = StandardTypeParameter(qualifiedName(fileName, ast.identifier, it.identifier), it.identifier)
                            if (seenTypeParameters.contains(it.identifier.name)) {
                                errors.add(it.identifier.ctx, DuplicateTypeParameter(toError(it.identifier)))
                            } else {
                                seenTypeParameters.add(it.identifier.name)
                                parameterizedRecordSymbol.defineType(it.identifier, typeParam)
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
                        val typeParam = FinTypeParameter(qualifiedName(fileName, ast.identifier, it.identifier), it.identifier)
                        val postFix = it.identifier.name
                        if (seenTypeParameters.contains(postFix)) {
                            errors.add(it.identifier.ctx, DuplicateTypeParameter(toError(it.identifier)))
                        } else {
                            seenTypeParameters.add(postFix)
                            parameterizedFunctionSymbol.defineType(it.identifier, typeParam)
                        }
                        typeParam
                    } else {
                        val typeParam = StandardTypeParameter(qualifiedName(fileName, ast.identifier, it.identifier), it.identifier)
                        if (seenTypeParameters.contains(it.identifier.name)) {
                            errors.add(it.identifier.ctx, DuplicateTypeParameter(toError(it.identifier)))
                        } else {
                            seenTypeParameters.add(it.identifier.name)
                            parameterizedFunctionSymbol.defineType(it.identifier, typeParam)
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