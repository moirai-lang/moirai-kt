package moirai.transport

sealed interface TransportType
sealed interface TransportCostExpression : TransportType
sealed interface TransportTypeParameter : TransportType
sealed interface TransportPlatformSumMember : TransportType

internal class TransportFunctionType(
    val formalParamTypes: List<TransportType>,
    val returnType: TransportType
) : TransportType

internal class TransportStandardTypeParameter(
    val qualifiedName: String,
    val name: String
) : TransportTypeParameter

internal class TransportFinTypeParameter(
    val qualifiedName: String,
    val name: String
) : TransportTypeParameter, TransportCostExpression

internal class TransportFin(val magnitude: Long) : TransportCostExpression
internal data object TransportConstantFin : TransportCostExpression
internal class TransportSumCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression
internal class TransportProductCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression
internal class TransportMaxCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression

internal class TransportPlatformObjectType(
    val name: String
) : TransportType

internal class TransportObjectType(
    val qualifiedName: String,
    val name: String
) : TransportType

internal class TransportGroundRecordType(
    val qualifiedName: String,
    val name: String,
    val fields: List<String>
) : TransportType

internal class TransportParameterizedRecordType(
    val qualifiedName: String,
    val name: String,
    val typeParams: List<TransportTypeParameter>
) : TransportType

internal class TransportBasicType(
    val name: String,
) : TransportType

internal class TransportParameterizedBasicType(
    val name: String,
    val typeParams: List<TransportTypeParameter>
) : TransportType

internal class TransportPlatformSumType(
    val name: String,
    val typeParams: List<TransportTypeParameter>,
    val memberTypes: List<TransportPlatformSumMember>
) : TransportType

internal class TransportPlatformSumRecordType(
    val sumType: TransportPlatformSumType,
    val name: String,
    val typeParams: List<TransportTypeParameter>,
    val fields: List<String>
) : TransportPlatformSumMember

internal class TransportPlatformSumObjectType(
    val sumType: TransportPlatformSumType,
    val name: String
) : TransportPlatformSumMember