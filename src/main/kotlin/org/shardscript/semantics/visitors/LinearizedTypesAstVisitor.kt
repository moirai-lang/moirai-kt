package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

data class TypeParamChildren(val typeParam: TypeParameter, val children: Set<LinearizedSymbol>)

sealed class LinearizedSymbol {
    abstract val ctx: SourceContext
    abstract val symbol: Symbol
}

data class SymbolOnly(override val ctx: SourceContext, override val symbol: Symbol) : LinearizedSymbol()
data class TypeArgChildren(
    override val ctx: SourceContext,
    override val symbol: Symbol,
    val typeParamChildren: List<TypeParamChildren>
) : LinearizedSymbol()

fun linearize(ctx: SourceContext, symbol: Symbol): Set<LinearizedSymbol> =
    when (symbol) {
        ErrorSymbol -> setOf()
        is GroundRecordTypeSymbol -> {
            val res: MutableSet<LinearizedSymbol> = HashSet()
            res.add(SymbolOnly(ctx, symbol))
            res
        }
        is ParameterizedRecordTypeSymbol -> {
            val res: MutableSet<LinearizedSymbol> = HashSet()
            res.add(SymbolOnly(ctx, symbol))
            res
        }
        is SymbolInstantiation -> {
            when (val parameterizedSymbol = symbol.substitutionChain.originalSymbol) {
                is ParameterizedRecordTypeSymbol -> {
                    val res: MutableSet<LinearizedSymbol> = HashSet()
                    val paramChildren: MutableList<TypeParamChildren> = ArrayList()
                    parameterizedSymbol.typeParams.zip(symbol.substitutionChain.replayArgs()).forEach {
                        val children = linearize(ctx, it.second as Symbol)
                        paramChildren.add(TypeParamChildren(it.first, children))
                    }
                    res.add(TypeArgChildren(ctx, symbol, paramChildren))
                    res
                }
                is ParameterizedBasicTypeSymbol -> {
                    val res: MutableSet<LinearizedSymbol> = HashSet()
                    val paramChildren: MutableList<TypeParamChildren> = ArrayList()
                    parameterizedSymbol.typeParams.zip(symbol.substitutionChain.replayArgs()).forEach {
                        val children = linearize(ctx, it.second as Symbol)
                        paramChildren.add(TypeParamChildren(it.first, children))
                    }
                    res.add(TypeArgChildren(ctx, symbol, paramChildren))
                    res
                }
                is ParameterizedFunctionSymbol -> {
                    val res: MutableSet<LinearizedSymbol> = HashSet()
                    parameterizedSymbol.formalParams.forEach {
                        val ofTypeSymbol = symbol.substitutionChain.replay(it.ofTypeSymbol)
                        res.addAll(linearize(ctx, ofTypeSymbol as Symbol))
                    }
                    val returnType = symbol.substitutionChain.replay(parameterizedSymbol.returnType)
                    res.addAll(linearize(ctx, returnType as Symbol))
                    val paramChildren: MutableList<TypeParamChildren> = ArrayList()
                    parameterizedSymbol.typeParams.zip(symbol.substitutionChain.replayArgs()).forEach {
                        val children = linearize(ctx, it.second as Symbol)
                        paramChildren.add(TypeParamChildren(it.first, children))
                    }
                    res.add(TypeArgChildren(ctx, symbol, paramChildren))
                    res
                }
                is ParameterizedMemberPluginSymbol -> {
                    val res: MutableSet<LinearizedSymbol> = HashSet()
                    parameterizedSymbol.formalParams.forEach {
                        val ofTypeSymbol = symbol.substitutionChain.replay(it.ofTypeSymbol)
                        res.addAll(linearize(ctx, ofTypeSymbol as Symbol))
                    }
                    val returnType = symbol.substitutionChain.replay(parameterizedSymbol.returnType)
                    res.addAll(linearize(ctx, returnType as Symbol))
                    val paramChildren: MutableList<TypeParamChildren> = ArrayList()
                    parameterizedSymbol.typeParams.zip(symbol.substitutionChain.replayArgs()).forEach {
                        val children = linearize(ctx, it.second as Symbol)
                        paramChildren.add(TypeParamChildren(it.first, children))
                    }
                    res.add(TypeArgChildren(ctx, symbol, paramChildren))
                    res
                }
                is ParameterizedStaticPluginSymbol -> {
                    val res: MutableSet<LinearizedSymbol> = HashSet()
                    parameterizedSymbol.formalParams.forEach {
                        val ofTypeSymbol = symbol.substitutionChain.replay(it.ofTypeSymbol)
                        res.addAll(linearize(ctx, ofTypeSymbol as Symbol))
                    }
                    val returnType = symbol.substitutionChain.replay(parameterizedSymbol.returnType)
                    res.addAll(linearize(ctx, returnType as Symbol))
                    val paramChildren: MutableList<TypeParamChildren> = ArrayList()
                    parameterizedSymbol.typeParams.zip(symbol.substitutionChain.replayArgs()).forEach {
                        val children = linearize(ctx, it.second as Symbol)
                        paramChildren.add(TypeParamChildren(it.first, children))
                    }
                    res.add(TypeArgChildren(ctx, symbol, paramChildren))
                    res
                }
            }
        }
        is SystemRootNamespace -> setOf()
        is UserRootNamespace -> setOf()
        is Namespace -> setOf()
        is Block -> setOf()
        is PreludeTable -> setOf()
        is ImportTable -> setOf()
        is BasicTypeSymbol -> setOf(SymbolOnly(ctx, symbol))
        is ObjectSymbol -> setOf(SymbolOnly(ctx, symbol))
        is GroundFunctionSymbol -> {
            val res: MutableSet<LinearizedSymbol> = HashSet()
            res.add(SymbolOnly(ctx, symbol))
            symbol.formalParams.forEach {
                res.addAll(linearize(ctx, it.ofTypeSymbol as Symbol))
            }
            res.addAll(linearize(ctx, symbol.returnType as Symbol))
            res
        }
        is LambdaSymbol -> {
            val res: MutableSet<LinearizedSymbol> = HashSet()
            res.add(SymbolOnly(ctx, symbol))
            symbol.formalParams.forEach {
                res.addAll(linearize(ctx, it.ofTypeSymbol as Symbol))
            }
            res.addAll(linearize(ctx, symbol.returnType as Symbol))
            res
        }
        is GroundMemberPluginSymbol -> setOf(SymbolOnly(ctx, symbol))
        is FunctionTypeSymbol -> {
            val res: MutableSet<LinearizedSymbol> = HashSet()
            res.add(SymbolOnly(ctx, symbol))
            symbol.formalParamTypes.forEach {
                res.addAll(linearize(ctx, it as Symbol))
            }
            res.addAll(linearize(ctx, symbol.returnType as Symbol))
            res
        }
        is FinTypeSymbol -> setOf(SymbolOnly(ctx, symbol))
        is StandardTypeParameter -> setOf(SymbolOnly(ctx, symbol))
        is ImmutableFinTypeParameter -> setOf(SymbolOnly(ctx, symbol))
        is MutableFinTypeParameter -> setOf(SymbolOnly(ctx, symbol))
        is SumCostExpression -> setOf(SymbolOnly(ctx, symbol))
        is ProductCostExpression -> setOf(SymbolOnly(ctx, symbol))
        is MaxCostExpression -> setOf(SymbolOnly(ctx, symbol))
        is FunctionFormalParameterSymbol -> linearize(ctx, symbol.ofTypeSymbol as Symbol)
        is FieldSymbol -> linearize(ctx, symbol.ofTypeSymbol as Symbol)
        is PlatformFieldSymbol -> setOf(SymbolOnly(ctx, symbol))
        is LocalVariableSymbol -> setOf(SymbolOnly(ctx, symbol))
        is ParameterizedFunctionSymbol -> setOf(SymbolOnly(ctx, symbol))
        is ParameterizedBasicTypeSymbol -> setOf(SymbolOnly(ctx, symbol))
        is ParameterizedMemberPluginSymbol -> setOf(SymbolOnly(ctx, symbol))
        is ParameterizedStaticPluginSymbol -> setOf(SymbolOnly(ctx, symbol))
    }

class LinearizedTypesAstVisitor : UnitAstVisitor() {
    val linearized: MutableSet<LinearizedSymbol> = HashSet()

    override fun visit(ast: IntLiteralAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: DecimalLiteralAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: BooleanLiteralAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: CharLiteralAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: StringLiteralAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: StringInterpolationAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: LetAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.ofTypeSymbol as Symbol))
    }

    override fun visit(ast: RefAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: FileAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: BlockAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: FunctionAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
        ast.formalParams.forEach {
            linearized.addAll(linearize(ast.ctx, ast.scope.fetch(it.ofType)))
        }
        linearized.addAll(linearize(ast.ctx, ast.scope.fetch(ast.returnType)))
    }

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
        ast.fields.forEach {
            linearized.addAll(linearize(ast.ctx, ast.scope.fetch(it.ofType)))
        }
    }

    override fun visit(ast: ObjectDefinitionAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: DotAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: GroundApplyAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: DotApplyAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: ForEachAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: AssignAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: DotAssignAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: IfAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: AsAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.readType() as Symbol))
    }

    override fun visit(ast: IsAst) {
        super.visit(ast)
        linearized.addAll(linearize(ast.ctx, ast.identifierSymbol as Symbol))
    }
}