package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*

internal class CostExpressionAstVisitor(private val architecture: Architecture) : UnitAstVisitor() {
    private fun addDefault(costExpression: CostExpression): CostExpression =
        SumCostExpression(listOf(OmicronTypeSymbol(architecture.defaultNodeCost), costExpression))

    private fun convertCostExpression(symbolRef: Symbol): CostExpression =
        when (symbolRef) {
            is GroundFunctionSymbol -> {
                symbolRef.costExpression
            }
            is GroundMemberPluginSymbol -> {
                symbolRef.costExpression
            }
            is SymbolInstantiation -> replaySubstitutions(symbolRef)
            else -> OmicronTypeSymbol(architecture.defaultNodeCost)
        }

    private fun replaySubstitutions(instantiation: SymbolInstantiation): CostExpression =
        when (val parameterizedSymbol = instantiation.substitutionChain.originalSymbol) {
            is ParameterizedFunctionSymbol -> {
                val original = parameterizedSymbol.costExpression
                instantiation.substitutionChain.replay(original as Symbol) as CostExpression
            }
            is ParameterizedMemberPluginSymbol -> {
                val original = parameterizedSymbol.costExpression
                instantiation.substitutionChain.replay(original as Symbol) as CostExpression
            }
            is ParameterizedStaticPluginSymbol -> {
                val original = parameterizedSymbol.costExpression
                instantiation.substitutionChain.replay(original as Symbol) as CostExpression
            }
            else -> OmicronTypeSymbol(architecture.defaultNodeCost)
        }

    override fun visit(ast: SByteLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: ShortLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: IntLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: LongLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: ByteLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: UShortLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: UIntLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: ULongLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: DecimalLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: BooleanLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: CharLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: StringLiteralAst) {
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: StringInterpolationAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                ast.components.map {
                    it.costExpression
                }
            )
        )
    }

    override fun visit(ast: LetAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.rhs.costExpression)
    }

    override fun visit(ast: RefAst) {
        ast.costExpression = convertCostExpression(ast.symbolRef)
    }

    override fun visit(ast: FileAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                ast.lines.map {
                    it.costExpression
                }
            )
        )
    }

    override fun visit(ast: BlockAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                ast.lines.map {
                    it.costExpression
                }
            )
        )
    }

    override fun visit(ast: FunctionAst) {
        // The body cost will be calculated after topological sort of all functions
        // so we do not call super.visit(ast)
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: ObjectDefinitionAst) {
        super.visit(ast)
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: EnumDefinitionAst) {
        super.visit(ast)
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: DotAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.lhs.costExpression)
    }

    override fun visit(ast: GroundApplyAst) {
        super.visit(ast)
        val argCosts: MutableList<CostExpression> = ArrayList()
        val multipliers = when (val symbolRef = ast.symbolRef) {
            is GroundFunctionSymbol -> symbolRef.formalParams.map { it.costMultiplier }
            is SymbolInstantiation -> {
                when (val parameterizedSymbol = symbolRef.substitutionChain.originalSymbol) {
                    is ParameterizedFunctionSymbol -> {
                        parameterizedSymbol.formalParams.map { it.costMultiplier }
                    }
                    else -> {
                        listOf()
                    }
                }
            }
            else -> {
                listOf()
            }
        }
        multipliers.zip(ast.args).forEach {
            argCosts.add(ProductCostExpression(listOf(it.second.costExpression, it.first)))
        }
        val bodyCost = convertCostExpression(ast.symbolRef)
        if (argCosts.isEmpty()) {
            ast.costExpression = addDefault(bodyCost)
        } else {
            ast.costExpression = addDefault(SumCostExpression(listOf(bodyCost, SumCostExpression(argCosts))))
        }
    }

    override fun visit(ast: DotApplyAst) {
        super.visit(ast)
        val argCosts: MutableList<CostExpression> = ArrayList()
        val multipliers = when (val symbolRef = ast.symbolRef) {
            is GroundFunctionSymbol -> symbolRef.formalParams.map { it.costMultiplier }
            is SymbolInstantiation -> {
                when (val parameterizedSymbol = symbolRef.substitutionChain.originalSymbol) {
                    is ParameterizedFunctionSymbol -> {
                        parameterizedSymbol.formalParams.map {
                            symbolRef.substitutionChain.replay(it.costMultiplier as Symbol) as CostExpression
                        }
                    }
                    else -> {
                        listOf()
                    }
                }
            }
            else -> {
                listOf()
            }
        }
        multipliers.zip(ast.args).forEach {
            argCosts.add(ProductCostExpression(listOf(it.second.costExpression, it.first)))
        }
        val bodyCost = convertCostExpression(ast.symbolRef)
        if (argCosts.isEmpty()) {
            ast.costExpression = addDefault(SumCostExpression(listOf(bodyCost, ast.lhs.costExpression)))
        } else {
            ast.costExpression =
                addDefault(SumCostExpression(listOf(bodyCost, ast.lhs.costExpression, SumCostExpression(argCosts))))
        }
    }

    override fun visit(ast: ForEachAst) {
        super.visit(ast)
        val multiplier = when (val omicron = ast.sourceOmicronSymbol) {
            is CostExpression -> omicron
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
        ast.costExpression = addDefault(ProductCostExpression(listOf(multiplier, ast.body.costExpression)))
    }

    override fun visit(ast: MapAst) {
        super.visit(ast)
        val multiplier = when (val omicron = ast.sourceOmicronSymbol) {
            is CostExpression -> omicron
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
        ast.costExpression = addDefault(ProductCostExpression(listOf(multiplier, ast.body.costExpression)))
    }

    override fun visit(ast: FlatMapAst) {
        super.visit(ast)
        val multiplier = when (val omicron = ast.sourceOmicronSymbol) {
            is CostExpression -> omicron
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
        ast.costExpression = addDefault(ProductCostExpression(listOf(multiplier, ast.body.costExpression)))
    }

    override fun visit(ast: AssignAst) {
        super.visit(ast)
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: DotAssignAst) {
        super.visit(ast)
        ast.costExpression = OmicronTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: IfAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                listOf(
                    ast.condition.costExpression,
                    MaxCostExpression(
                        listOf(
                            ast.trueBranch.costExpression,
                            ast.falseBranch.costExpression
                        )
                    )
                )
            )
        )
    }

    override fun visit(ast: SwitchAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                listOf(
                    ast.source.costExpression,
                    MaxCostExpression(
                        ast.cases.map { it.body.costExpression }
                    )
                )
            )
        )
    }

    override fun visit(ast: AsAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.lhs.costExpression)
    }

    override fun visit(ast: IsAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.lhs.costExpression)
    }
}