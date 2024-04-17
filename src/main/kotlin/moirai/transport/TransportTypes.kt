package moirai.transport

sealed interface TransportType
sealed interface TransportCostExpression : TransportType
sealed interface TransportTypeParameter : TransportType
sealed interface TransportPlatformSumMember : TransportType

data class TransportRecordField(val name: String, val type: TransportType)

data class TransportFunctionType(
    val formalParamTypes: List<TransportType>,
    val returnType: TransportType
) : TransportType

data class TransportStandardTypeParameter(
    val qualifiedName: String,
    val name: String
) : TransportTypeParameter

data class TransportFinTypeParameter(
    val qualifiedName: String,
    val name: String
) : TransportTypeParameter, TransportCostExpression

data class TransportFin(val magnitude: Long) : TransportCostExpression
data object TransportConstantFin : TransportCostExpression
data class TransportSumCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression
data class TransportProductCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression
data class TransportMaxCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression

data class TransportPlatformObjectType(
    val name: String
) : TransportType

data class TransportObjectType(
    val qualifiedName: String,
    val name: String
) : TransportType

data class TransportGroundRecordType(
    val qualifiedName: String,
    val name: String,
    val fields: List<TransportRecordField>
) : TransportType

data class TransportParameterizedRecordType(
    val qualifiedName: String,
    val name: String,
    val typeParams: List<TransportTypeParameter>
) : TransportType

data class TransportBasicType(
    val name: String,
) : TransportType

data class TransportParameterizedBasicType(
    val name: String,
    val typeParams: List<TransportTypeParameter>
) : TransportType

data class TransportPlatformSumType(
    val name: String,
    val typeParams: List<TransportTypeParameter>,
    val memberTypes: List<TransportPlatformSumMember>
) : TransportType

data class TransportPlatformSumRecordType(
    val sumType: TransportPlatformSumType,
    val name: String,
    val typeParams: List<TransportTypeParameter>,
    val fields: List<TransportRecordField>
) : TransportPlatformSumMember

data class TransportPlatformSumObjectType(
    val sumType: TransportPlatformSumType,
    val name: String
) : TransportPlatformSumMember