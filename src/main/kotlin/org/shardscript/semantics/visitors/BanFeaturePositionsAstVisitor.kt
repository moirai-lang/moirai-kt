package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class BanFeaturePositionsAstVisitor : UnitAstVisitor() {
    override fun visit(ast: FunctionAst) {
        super.visit(ast)
        when (val returnType = ast.scope.fetch(ast.returnType)) {
            is ParameterizedBasicTypeSymbol,
            is ParameterizedRecordTypeSymbol -> errors.add(ast.ctx, CannotUseRawType(returnType))
            is FunctionTypeSymbol -> errors.add(ast.ctx, FunctionReturnType(ast.identifier))
            is ObjectSymbol -> {
                if (!returnType.featureSupport.returnType) {
                    errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                }
            }
            is SymbolInstantiation -> {
                val parameterizedReturnType = returnType.substitutionChain.originalSymbol
                if (parameterizedReturnType is ParameterizedBasicTypeSymbol) {
                    if (!parameterizedReturnType.featureSupport.returnType) {
                        errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                    }
                }
                if (parameterizedReturnType is ParameterizedRecordTypeSymbol) {
                    if (!parameterizedReturnType.featureSupport.returnType) {
                        errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                    }
                }
            }
        }
        ast.formalParams.forEach {
            when (val formalParamType = ast.scope.fetch(it.ofType)) {
                is ParameterizedBasicTypeSymbol,
                is ParameterizedRecordTypeSymbol -> errors.add(ast.ctx, CannotUseRawType(formalParamType))
                is ObjectSymbol -> {
                    if (!formalParamType.featureSupport.paramType) {
                        errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                    }
                }
                is SymbolInstantiation -> {
                    val parameterizedFormalParamType = formalParamType.substitutionChain.originalSymbol
                    if (parameterizedFormalParamType is ParameterizedBasicTypeSymbol) {
                        if (!parameterizedFormalParamType.featureSupport.paramType) {
                            errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                        }
                    }
                    if (parameterizedFormalParamType is ParameterizedRecordTypeSymbol) {
                        if (!parameterizedFormalParamType.featureSupport.paramType) {
                            errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                        }
                    }
                }
            }
        }
    }

    override fun visit(ast: LetAst) {
        super.visit(ast)
        when (val ofTypeSymbol = ast.ofTypeSymbol) {
            is ParameterizedBasicTypeSymbol,
            is ParameterizedRecordTypeSymbol -> errors.add(ast.ctx, CannotUseRawType(ofTypeSymbol))
            is FunctionTypeSymbol -> errors.add(ast.ctx, FunctionAssign(ast.identifier))
            else -> Unit
        }
    }

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        ast.fields.forEach {
            when (val fieldType = ast.scope.fetch(it.ofType)) {
                is ParameterizedBasicTypeSymbol,
                is ParameterizedRecordTypeSymbol -> errors.add(ast.ctx, CannotUseRawType(fieldType))
                is FunctionTypeSymbol -> errors.add(ast.ctx, RecordFieldFunctionType(ast.identifier, it.identifier))
                is ObjectSymbol -> {
                    if (!fieldType.featureSupport.recordField) {
                        errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                    }
                }
                is SymbolInstantiation -> {
                    val parameterizedFieldType = fieldType.substitutionChain.originalSymbol
                    if (parameterizedFieldType is ParameterizedBasicTypeSymbol) {
                        if (!parameterizedFieldType.featureSupport.recordField) {
                            errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                        }
                    }
                    if (parameterizedFieldType is ParameterizedRecordTypeSymbol) {
                        if (!parameterizedFieldType.featureSupport.recordField) {
                            errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    override fun visit(ast: AsAst) {
        super.visit(ast)
        val asType = ast.scope.fetch(ast.signifier)
        if (asType is FunctionTypeSymbol) {
            errors.add(ast.ctx, InvalidAsCast(ast.signifier))
        }
    }

    override fun visit(ast: IsAst) {
        super.visit(ast)
        val isType = ast.scope.fetch(ast.signifier)
        if (isType is FunctionTypeSymbol) {
            errors.add(ast.ctx, InvalidIsCheck(ast.signifier))
        }
    }
}