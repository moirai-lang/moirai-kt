package moirai.transport

sealed interface TransportType
sealed interface TransportCostExpression : TransportType
sealed interface TransportTypeParameter : TransportType
sealed interface TransportPlatformSumMember : TransportType

class TransportFunctionType(
    val formalParamTypes: List<TransportType>,
    val returnType: TransportType
) : TransportType

class TransportStandardTypeParameter(
    val qualifiedName: String,
    val name: String
) : TransportTypeParameter

class TransportFinTypeParameter(
    val qualifiedName: String,
    val name: String
) : TransportTypeParameter, TransportCostExpression

class TransportFin(val magnitude: Long) : TransportCostExpression
data object TransportConstantFin : TransportCostExpression
class TransportSumCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression
class TransportProductCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression
class TransportMaxCostExpression(val args: List<TransportCostExpression>) : TransportCostExpression

class TransportPlatformObjectType(
    val name: String
) : TransportType

class TransportObjectType(
    val qualifiedName: String,
    val name: String
) : TransportType

class TransportGroundRecordType(
    val qualifiedName: String,
    val name: String,
    val fields: List<String>
) : TransportType

class TransportParameterizedRecordType(
    val qualifiedName: String,
    val name: String,
    val typeParams: List<TransportTypeParameter>
) : TransportType

class TransportBasicType(
    val name: String,
) : TransportType

class TransportParameterizedBasicType(
    val name: String,
    val typeParams: List<TransportTypeParameter>
) : TransportType

class TransportPlatformSumType(
    val name: String,
    val typeParams: List<TransportTypeParameter>,
    val memberTypes: List<TransportPlatformSumMember>
) : TransportType

class TransportPlatformSumRecordType(
    val sumType: TransportPlatformSumType,
    val name: String,
    val typeParams: List<TransportTypeParameter>,
    val fields: List<String>
) : TransportPlatformSumMember

class TransportPlatformSumObjectType(
    val sumType: TransportPlatformSumType,
    val name: String
) : TransportPlatformSumMember