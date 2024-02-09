package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class BanFeaturePositionsAstVisitor : UnitAstVisitor() {
    override fun visit(ast: FunctionAst) {
        super.visit(ast)
        when (val returnType = ast.scope.fetchType(ast.returnType)) {
            is ParameterizedBasicType,
            is ParameterizedRecordType -> errors.add(ast.ctx, CannotUseRawType(returnType))
            is FunctionType -> errors.add(ast.ctx, FunctionReturnType(ast.identifier))
            is ObjectType -> {
                if (!returnType.featureSupport.returnType) {
                    errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                }
            }
            is TypeInstantiation -> {
                val parameterizedReturnType = returnType.substitutionChain.terminus
                if (parameterizedReturnType is ParameterizedBasicType) {
                    if (!parameterizedReturnType.featureSupport.returnType) {
                        errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                    }
                }
                if (parameterizedReturnType is ParameterizedRecordType) {
                    if (!parameterizedReturnType.featureSupport.returnType) {
                        errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                    }
                }
            }
            else -> Unit
        }
        ast.formalParams.forEach {
            when (val formalParamType = ast.scope.fetchType(it.ofType)) {
                is ParameterizedBasicType,
                is ParameterizedRecordType -> errors.add(ast.ctx, CannotUseRawType(formalParamType))
                is ObjectType -> {
                    if (!formalParamType.featureSupport.paramType) {
                        errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                    }
                }
                is TypeInstantiation -> {
                    val parameterizedFormalParamType = formalParamType.substitutionChain.terminus
                    if (parameterizedFormalParamType is ParameterizedBasicType) {
                        if (!parameterizedFormalParamType.featureSupport.paramType) {
                            errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                        }
                    }
                    if (parameterizedFormalParamType is ParameterizedRecordType) {
                        if (!parameterizedFormalParamType.featureSupport.paramType) {
                            errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                        }
                    }
                }

                else -> Unit
            }
        }
    }

    override fun visit(ast: LetAst) {
        super.visit(ast)
        when (val ofTypeSymbol = ast.ofTypeSymbol) {
            is ParameterizedBasicType,
            is ParameterizedRecordType -> errors.add(ast.ctx, CannotUseRawType(ofTypeSymbol))
            is FunctionType -> errors.add(ast.ctx, FunctionAssign(ast.identifier))
            else -> Unit
        }
    }

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        ast.fields.forEach {
            when (val fieldType = ast.scope.fetchType(it.ofType)) {
                is ParameterizedBasicType,
                is ParameterizedRecordType -> errors.add(ast.ctx, CannotUseRawType(fieldType))
                is FunctionType -> errors.add(ast.ctx, RecordFieldFunctionType(ast.identifier, it.identifier))
                is ObjectType -> {
                    if (!fieldType.featureSupport.recordField) {
                        errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                    }
                }
                is TypeInstantiation -> {
                    val parameterizedFieldType = fieldType.substitutionChain.terminus
                    if (parameterizedFieldType is ParameterizedBasicType) {
                        if (!parameterizedFieldType.featureSupport.recordField) {
                            errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                        }
                    }
                    if (parameterizedFieldType is ParameterizedRecordType) {
                        if (!parameterizedFieldType.featureSupport.recordField) {
                            errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                        }
                    }
                }
                else -> Unit
            }
        }
    }
}