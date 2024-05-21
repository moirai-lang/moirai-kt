package moirai.transport

import moirai.composition.ExecutionArtifacts
import moirai.semantics.core.*

sealed interface FetchTransportFunctionResult

data class TransportFunction(
    val name: String,
    val formalParams: List<TransportBinder>,
    val returnType: TransportType
): FetchTransportFunctionResult

data object TransportFunctionNotFound: FetchTransportFunctionResult

fun fetchTransportFunction(executionArtifacts: ExecutionArtifacts, functionName: String): FetchTransportFunctionResult {
    executionArtifacts.semanticArtifacts.sortedFunctions.nodes.forEach { symbol ->
        when (symbol) {
            is GroundFunctionSymbol -> {
                if (symbol.identifier.name == functionName) {
                    return TransportFunction(
                        symbol.identifier.name,
                        symbol.formalParams.map {
                            TransportBinder(
                                it.identifier.name,
                                convertToTransportType(it.ofTypeSymbol)
                            )
                        },
                        convertToTransportType(symbol.returnType)
                    )
                }
            }

            else -> Unit
        }

    }

    return TransportFunctionNotFound
}

internal fun convertToTransportType(type: Type): TransportType =
    when(type) {
        is BasicType -> TransportBasicType(type.identifier.name)
        is ConstantFin -> TransportConstantFin
        is Fin -> TransportFin(type.magnitude)
        is FinTypeParameter -> TransportFinTypeParameter(type.identifier.name)
        is InstantiationHashCodeCost -> NonPublicTransportType
        is MaxCostExpression -> TransportMaxCostExpression(
            type.children.map { convertToTransportType(it) as TransportCostExpression }
        )

        is ParameterHashCodeCost -> NonPublicTransportType
        is ProductCostExpression -> TransportProductCostExpression(
            type.children.map { convertToTransportType(it) as TransportCostExpression }
        )

        is SumCostExpression -> TransportSumCostExpression(
            type.children.map { convertToTransportType(it) as TransportCostExpression }
        )

        ErrorType -> NonPublicTransportType
        is FunctionType -> TransportFunctionType(
            type.formalParamTypes.map { convertToTransportType(it) },
            convertToTransportType(type.returnType)
        )

        is GroundRecordType -> TransportGroundRecordType(type.identifier.name, type.fields.map {
            TransportBinder(it.identifier.name, convertToTransportType(it.ofTypeSymbol))
        })

        is ObjectType -> TransportObjectType(type.identifier.name)
        is PlatformObjectType -> TransportPlatformObjectType(type.identifier.name)
        is PlatformSumObjectType -> TransportPlatformSumObjectType(type.sumType.identifier.name, type.identifier.name)
        is StandardTypeParameter -> TransportStandardTypeParameter(type.identifier.name)
        is ParameterizedBasicType -> NonPublicTransportType
        is ParameterizedRecordType -> NonPublicTransportType
        is PlatformSumRecordType -> NonPublicTransportType
        is PlatformSumType -> NonPublicTransportType

        is TypeInstantiation -> {
            val typeArgs = type.substitutionChain.replayArgs().map { convertToTransportType(it) }
            when (val terminus = type.substitutionChain.chain.terminus) {
                is ParameterHashCodeCost -> NonPublicTransportType
                is ParameterizedBasicType -> TransportParameterizedBasicType(
                    terminus.identifier.name,
                    typeArgs
                )

                is ParameterizedRecordType -> TransportParameterizedRecordType(
                    terminus.identifier.name,
                    typeArgs,
                    terminus.fields.map {
                        TransportBinder(
                            it.identifier.name,
                            convertToTransportType(type.substitutionChain.replay(it.ofTypeSymbol))
                        )
                    }
                )

                is PlatformSumRecordType -> TransportPlatformSumRecordType(
                    terminus.sumType.identifier.name,
                    terminus.identifier.name,
                    typeArgs,
                    terminus.fields.map {
                        TransportBinder(
                            it.identifier.name,
                            convertToTransportType(type.substitutionChain.replay(it.ofTypeSymbol))
                        )
                    }
                )

                is PlatformSumType -> TransportPlatformSumType(
                    terminus.identifier.name,
                    typeArgs,
                    terminus.memberTypes.map {
                        when (it) {
                            is PlatformSumObjectType -> convertToTransportType(it as Type) as TransportPlatformSumMember
                            is PlatformSumRecordType -> {
                                val instantiation = type.substitutionChain.replay(it)
                                convertToTransportType(instantiation) as TransportPlatformSumMember
                            }
                        }
                    }
                )
            }
        }
    }

internal fun convertToAst(transportAst: TransportAst): Ast =
    when(transportAst) {
        is ApplyTransportAst -> GroundApplyAst(
            NotInSource,
            Identifier(NotInSource, transportAst.name),
            transportAst.args.map { convertToAst(it) })

        is BooleanLiteralTransportAst -> BooleanLiteralAst(NotInSource, transportAst.canonicalForm)
        is DecimalLiteralTransportAst -> DecimalLiteralAst(NotInSource, transportAst.canonicalForm)
        is IntLiteralTransportAst -> IntLiteralAst(NotInSource, transportAst.canonicalForm)
        is RefTransportAst -> RefAst(NotInSource, Identifier(NotInSource, transportAst.name))
        is StringLiteralTransportAst -> StringLiteralAst(NotInSource, transportAst.canonicalForm)
        is CharLiteralTransportAst -> CharLiteralAst(NotInSource, transportAst.canonicalForm)
    }