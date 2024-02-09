package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

class CostExpressionAstVisitor(private val architecture: Architecture) : UnitAstVisitor() {
    private fun addDefault(costExpression: CostExpression): CostExpression =
        SumCostExpression(listOf(FinTypeSymbol(architecture.defaultNodeCost), costExpression))

    private fun convertCostExpression(symbolRef: Symbol): CostExpression =
        when (symbolRef) {
            is GroundFunctionSymbol -> {
                symbolRef.costExpression
            }
            is GroundMemberPluginSymbol -> {
                symbolRef.costExpression
            }
            is SymbolInstantiation -> replaySubstitutions(symbolRef)
            else -> FinTypeSymbol(architecture.defaultNodeCost)
        }

    private fun convertCostExpression(typeRef: Type): CostExpression =
        when (typeRef) {
            is TypeInstantiation -> FinTypeSymbol(architecture.defaultNodeCost)
            else -> FinTypeSymbol(architecture.defaultNodeCost)
        }

    private fun replaySubstitutions(instantiation: SymbolInstantiation): CostExpression =
        when (val parameterizedSymbol = instantiation.substitutionChain.terminus) {
            is ParameterizedFunctionSymbol -> {
                val original = parameterizedSymbol.costExpression
                instantiation.substitutionChain.replay(original)
            }
            is ParameterizedMemberPluginSymbol -> {
                val original = parameterizedSymbol.costExpression
                instantiation.substitutionChain.replay(original)
            }
            is ParameterizedStaticPluginSymbol -> {
                val original = parameterizedSymbol.costExpression
                instantiation.substitutionChain.replay(original)
            }
        }

    override fun visit(ast: IntLiteralAst) {
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: DecimalLiteralAst) {
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: BooleanLiteralAst) {
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: CharLiteralAst) {
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: StringLiteralAst) {
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
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
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
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
        // The body cost will be calculated after topological sort of all functions,
        // therefore we do not call super.visit(ast)
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: LambdaAst) {
        super.visit(ast)
        ast.costExpression = ast.body.costExpression
    }

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: ObjectDefinitionAst) {
        super.visit(ast)
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
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
                when (val parameterizedSymbol = symbolRef.substitutionChain.terminus) {
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
        val bodyCost = when (ast.symbolRef) {
            is TypePlaceholder -> convertCostExpression(ast.typeRef)
            else -> convertCostExpression(ast.symbolRef)
        }
        if (argCosts.isEmpty()) {
            ast.costExpression = addDefault(bodyCost)
        } else {
            ast.costExpression = addDefault(SumCostExpression(listOf(bodyCost, SumCostExpression(argCosts))))
        }
    }

    override fun visit(ast: DotApplyAst) {
        super.visit(ast)
        val argCosts: MutableList<CostExpression> = ArrayList()
        val multipliers = when (val dotApplySlot = ast.dotApplySlot) {
            DotApplySlotError -> langThrow(NotInSource, TypeSystemBug)
            is DotApplySlotGF -> {
                val symbolRef = dotApplySlot.payload
                symbolRef.formalParams.map { it.costMultiplier }
            }
            is DotApplySlotGMP -> {
                val symbolRef = dotApplySlot.payload
                symbolRef.formalParams.map { it.costMultiplier }
            }
            is DotApplySlotSI -> {
                val symbolRef = dotApplySlot.payload
                when (val parameterizedSymbol = symbolRef.substitutionChain.terminus) {
                    is ParameterizedFunctionSymbol -> {
                        parameterizedSymbol.formalParams.map {
                            symbolRef.substitutionChain.replay(it.costMultiplier)
                        }
                    }
                    else -> {
                        listOf()
                    }
                }
            }
        }
        multipliers.zip(ast.args).forEach {
            argCosts.add(ProductCostExpression(listOf(it.second.costExpression, it.first)))
        }
        val bodyCost = when (val dotApplySlot = ast.dotApplySlot) {
            DotApplySlotError -> langThrow(NotInSource, TypeSystemBug)
            is DotApplySlotGF -> {
                convertCostExpression(dotApplySlot.payload)
            }
            is DotApplySlotGMP -> {
                convertCostExpression(dotApplySlot.payload)
            }
            is DotApplySlotSI -> {
                convertCostExpression(dotApplySlot.payload)
            }
        }
        if (argCosts.isEmpty()) {
            ast.costExpression = addDefault(SumCostExpression(listOf(bodyCost, ast.lhs.costExpression)))
        } else {
            ast.costExpression =
                addDefault(SumCostExpression(listOf(bodyCost, ast.lhs.costExpression, SumCostExpression(argCosts))))
        }
    }

    override fun visit(ast: ForEachAst) {
        super.visit(ast)
        val multiplier = when (val fin = ast.sourceFinSymbol) {
            is CostExpression -> fin
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
        ast.costExpression = addDefault(ProductCostExpression(listOf(multiplier, ast.body.costExpression)))
    }

    override fun visit(ast: AssignAst) {
        super.visit(ast)
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
    }

    override fun visit(ast: DotAssignAst) {
        super.visit(ast)
        ast.costExpression = FinTypeSymbol(architecture.defaultNodeCost)
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
}