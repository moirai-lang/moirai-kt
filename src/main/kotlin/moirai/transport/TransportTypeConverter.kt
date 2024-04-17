package moirai.transport

import moirai.semantics.core.*

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
            TransportRecordField(it.identifier.name, convertToTransportType(it.ofTypeSymbol))
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
            type.fields.map { TransportRecordField(it.identifier.name, convertToTransportType(it.ofTypeSymbol)) }
        )

        is PlatformSumRecordType -> TransportPlatformSumRecordType(
            type.sumType.identifier.name,
            type.identifier.name,
            type.typeParams.map { convertToTransportType(it) as TransportTypeParameter },
            type.fields.map { TransportRecordField(it.identifier.name, convertToTransportType(it.ofTypeSymbol)) }
        )

        is PlatformSumType -> TransportPlatformSumType(
            type.identifier.name,
            type.typeParams.map { convertToTransportType(it) as TransportTypeParameter },
            type.memberTypes.map { convertToTransportType(it as Type) as TransportPlatformSumMember }
        )

        is TypeInstantiation -> NonPublicTransportType
    }