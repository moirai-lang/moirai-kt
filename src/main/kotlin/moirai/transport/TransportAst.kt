package moirai.transport

import java.math.BigDecimal

sealed class TransportAst

data class IntLiteralTransportAst(val canonicalForm: Int): TransportAst()
data class DecimalLiteralTransportAst(val canonicalForm: BigDecimal): TransportAst()
data class BooleanLiteralTransportAst(val canonicalForm: Boolean): TransportAst()
data class StringLiteralTransportAst(val canonicalForm: String): TransportAst()
data class RefTransportAst(val name: String): TransportAst()
data class ApplyTransportAst(val name: String, val args: List<TransportAst>): TransportAst()