package moirai.transport

/**
 * This type provides a stable public surface for the output of the type system. Whereas
 * the internal type system may change at any time, this public surface should never change
 * for library consumers. The types in this file can be used as part of a system that translates
 * transport formats to Moirai code, such as JSON to Moirai conversion.
 */
sealed interface TransportType
sealed interface TransportCostExpression : TransportType
sealed interface TransportPlatformSumMember : TransportType

data class TransportBinder(val name: String, val type: TransportType)

data object NonPublicTransportType: TransportType

data class TransportFunctionType(
    val formalParamTypes: List<TransportType>,
    val returnType: TransportType
) : TransportType

data class TransportStandardTypeParameter(
    val name: String
) : TransportType

data class TransportFinTypeParameter(
    val name: String
) : TransportCostExpression

data class TransportFin(val magnitude: Long) : TransportCostExpression
data object TransportConstantFin : TransportCostExpression
data class TransportSumCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression
data class TransportProductCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression
data class TransportMaxCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression

data class TransportPlatformObjectType(
    val name: String
) : TransportType

data class TransportObjectType(
    val name: String
) : TransportType

data class TransportGroundRecordType(
    val name: String,
    val fields: List<TransportBinder>
) : TransportType

data class TransportParameterizedRecordType(
    val name: String,
    val typeArgs: List<TransportType>,
    val fields: List<TransportBinder>
) : TransportType

data class TransportBasicType(
    val name: String,
) : TransportType

data class TransportParameterizedBasicType(
    val name: String,
    val typeArgs: List<TransportType>
) : TransportType

data class TransportPlatformSumType(
    val name: String,
    val typeArgs: List<TransportType>,
    val memberTypes: List<TransportPlatformSumMember>
) : TransportType

data class TransportPlatformSumRecordType(
    val sumTypeName: String,
    val name: String,
    val typeArgs: List<TransportType>,
    val fields: List<TransportBinder>
) : TransportPlatformSumMember

data class TransportPlatformSumObjectType(
    val sumTypeName: String,
    val name: String
) : TransportPlatformSumMember