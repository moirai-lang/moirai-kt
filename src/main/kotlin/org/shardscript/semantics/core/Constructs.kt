package org.shardscript.semantics.core

interface LanguageElement {
    var ctx: SourceContext
}

data class Binder(val identifier: Identifier, val ofType: Signifier) {
    lateinit var symbol: Symbol
}

data class FieldDef(val identifier: Identifier, val ofType: Signifier, val mutable: Boolean) {
    lateinit var symbol: Symbol
}

enum class TypeParameterKind {
    Type,
    Fin
}

data class TypeParameterDefinition(val identifier: Identifier, val type: TypeParameterKind)

sealed class CaseBranch : LanguageElement {
    abstract val body: BlockAst
}

data class CoproductBranch(
    override var ctx: SourceContext,
    val identifier: Identifier,
    override val body: BlockAst
) : CaseBranch() {
    lateinit var path: List<String>
}

data class ElseBranch(override var ctx: SourceContext, override val body: BlockAst) : CaseBranch()

interface SingleTypeInstantiation {
    fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation
}

interface TwoTypeInstantiation {
    fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        parameterized: ParameterizedSymbol,
        existingInstantiation: SymbolInstantiation,
        explicitTypeArgs: List<Type>
    ): SymbolInstantiation
}

sealed class BasicTypeMode

data class MutableBasicTypeMode(val fin: Long) : BasicTypeMode()
data object ImmutableBasicTypeMode : BasicTypeMode()
