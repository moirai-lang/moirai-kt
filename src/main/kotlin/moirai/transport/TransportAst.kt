package moirai.transport

import java.math.BigDecimal

/**
 * This type provides a stable public surface for simple Abstract Syntax Trees. It can be used
 * in contexts where generating raw Moirai code may not be safe. For example, when converting
 * a transport format to Moirai, the transport format may contain a string interpolation that
 * creates a security risk if directly converted to Moirai. Instead of converting to raw code,
 * this AST allows a conversion to a safe in-memory object.
 */
sealed class TransportAst

data class IntLiteralTransportAst(val canonicalForm: Int): TransportAst()
data class DecimalLiteralTransportAst(val canonicalForm: BigDecimal): TransportAst()
data class BooleanLiteralTransportAst(val canonicalForm: Boolean): TransportAst()
data class StringLiteralTransportAst(val canonicalForm: String): TransportAst()
data class RefTransportAst(val name: String): TransportAst()
data class ApplyTransportAst(val name: String, val args: List<TransportAst>): TransportAst()