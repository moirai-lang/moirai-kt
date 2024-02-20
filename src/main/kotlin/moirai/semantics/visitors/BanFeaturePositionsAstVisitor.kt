package moirai.semantics.visitors

import moirai.semantics.core.*

internal class BanFeaturePositionsAstVisitor : UnitAstVisitor() {
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

            is PlatformSumObjectType -> {
                if (!returnType.featureSupport.returnType) {
                    errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                }
            }

            is TypeInstantiation -> {
                when (val parameterizedReturnType = returnType.substitutionChain.terminus) {
                    is ParameterizedBasicType -> {
                        if (!parameterizedReturnType.featureSupport.returnType) {
                            errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                        }
                    }

                    is ParameterizedRecordType -> {
                        if (!parameterizedReturnType.featureSupport.returnType) {
                            errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                        }
                    }

                    is PlatformSumRecordType -> {
                        if (!parameterizedReturnType.featureSupport.returnType) {
                            errors.add(ast.ctx, ReturnTypeFeatureBan(returnType))
                        }
                    }

                    else -> Unit
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
                    when (val parameterizedFormalParamType = formalParamType.substitutionChain.terminus) {
                        is ParameterizedBasicType -> {
                            if (!parameterizedFormalParamType.featureSupport.paramType) {
                                errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                            }
                        }

                        is ParameterizedRecordType -> {
                            if (!parameterizedFormalParamType.featureSupport.paramType) {
                                errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                            }
                        }

                        is PlatformSumRecordType -> {
                            if (!parameterizedFormalParamType.featureSupport.paramType) {
                                errors.add(ast.ctx, FormalParamFeatureBan(formalParamType))
                            }
                        }

                        else -> Unit
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
            is ParameterizedRecordType,
            is PlatformSumRecordType -> errors.add(ast.ctx, CannotUseRawType(ofTypeSymbol))

            is FunctionType -> errors.add(ast.ctx, FunctionAssign(ast.identifier))

            is TypeInstantiation -> {
                when (val parameterizedFormalParamType = ofTypeSymbol.substitutionChain.terminus) {
                    is PlatformSumRecordType -> {
                        if (!parameterizedFormalParamType.featureSupport.paramType) {
                            errors.add(ast.ctx, CannotUsePlatformSumTypeMember(parameterizedFormalParamType))
                        }
                    }

                    else -> Unit
                }
            }

            is PlatformSumObjectType -> {
                errors.add(ast.ctx, CannotUsePlatformSumTypeMember(ofTypeSymbol))
            }

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

                is PlatformObjectType -> {
                    if (!fieldType.featureSupport.recordField) {
                        errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                    }
                }

                is PlatformSumObjectType -> {
                    if (!fieldType.featureSupport.recordField) {
                        errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                    }
                }

                is TypeInstantiation -> {
                    when (val parameterizedFieldType = fieldType.substitutionChain.terminus) {
                        is ParameterizedBasicType -> {
                            if (!parameterizedFieldType.featureSupport.recordField) {
                                errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                            }
                        }

                        is ParameterizedRecordType -> {
                            if (!parameterizedFieldType.featureSupport.recordField) {
                                errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                            }
                        }

                        is PlatformSumRecordType -> {
                            if (!parameterizedFieldType.featureSupport.recordField) {
                                errors.add(ast.ctx, RecordFieldFeatureBan(fieldType))
                            }
                        }

                        else -> Unit
                    }
                }

                else -> Unit
            }
        }
    }
}