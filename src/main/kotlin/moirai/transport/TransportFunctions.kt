package moirai.transport

import moirai.composition.ExecutionArtifacts
import moirai.semantics.core.*

sealed interface FetchTransportFunctionResult

data class TransportFunction(
    val name: String,
    val typeParams: List<TransportTypeParameter>,
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
                        listOf(),
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

            is ParameterizedFunctionSymbol -> {
                if (symbol.identifier.name == functionName) {
                    return TransportFunction(
                        symbol.identifier.name,
                        symbol.typeParams.map { convertToTransportType(it) as TransportTypeParameter },
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
        ConstantFin -> TransportConstantFin
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
        is ParameterizedBasicType -> TransportParameterizedBasicType(
            type.identifier.name,
            type.typeParams.map { convertToTransportType(it) as TransportTypeParameter }
        )

        is ParameterizedRecordType -> TransportParameterizedRecordType(
            type.identifier.name,
            type.typeParams.map { convertToTransportType(it) as TransportTypeParameter },
            type.fields.map { TransportBinder(it.identifier.name, convertToTransportType(it.ofTypeSymbol)) }
        )

        is PlatformSumRecordType -> TransportPlatformSumRecordType(
            type.sumType.identifier.name,
            type.identifier.name,
            type.typeParams.map { convertToTransportType(it) as TransportTypeParameter },
            type.fields.map { TransportBinder(it.identifier.name, convertToTransportType(it.ofTypeSymbol)) }
        )

        is PlatformSumType -> TransportPlatformSumType(
            type.identifier.name,
            type.typeParams.map { convertToTransportType(it) as TransportTypeParameter },
            type.memberTypes.map { convertToTransportType(it as Type) as TransportPlatformSumMember }
        )

        is TypeInstantiation -> NonPublicTransportType
    }