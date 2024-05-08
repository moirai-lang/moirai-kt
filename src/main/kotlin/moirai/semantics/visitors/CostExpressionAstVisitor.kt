package moirai.semantics.visitors

import moirai.semantics.core.*
import moirai.semantics.infer.SubstitutionChain
import moirai.semantics.infer.TerminalChain

internal class CostExpressionAstVisitor(private val architecture: Architecture) : UnitAstVisitor() {
    private fun addDefault(costExpression: CostExpression, key: String): CostExpression =
        SumCostExpression(listOf(Fin(getDefaultNodeCost(key)), costExpression))

    private fun getDefaultNodeCost(key: String): Long =
        when (val overlay = architecture.getNodeCostOverlay(key)) {
            is DefinedOverlay -> overlay.nodeCost
            UndefinedOverlay -> architecture.defaultNodeCost
        }

    private fun convertCostExpression(symbolRef: Symbol): CostExpression =
        when (symbolRef) {
            is GroundFunctionSymbol -> {
                symbolRef.costExpression
            }

            is GroundMemberPluginSymbol -> {
                symbolRef.costExpression
            }

            is SymbolInstantiation -> replaySubstitutions(symbolRef)
            else -> Fin(architecture.defaultNodeCost)
        }

    private fun convertCostExpression(typeRef: Type): CostExpression =
        when (typeRef) {
            is TypeInstantiation -> Fin(architecture.defaultNodeCost)
            else -> Fin(architecture.defaultNodeCost)
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

    private fun handleHash(
        ce: CostExpression,
        lhsType: Type
    ) = if (ce is ParameterHashCodeCost) {
        // As a special case, if the function involves generating a hash code,
        // we need to create an instantiation using the LHS chain
        when (lhsType) {
            is TypeInstantiation -> {
                val chain = SubstitutionChain(
                    lhsType.substitutionChain.substitution,
                    TerminalChain<TerminusType>(ce)
                )
                InstantiationHashCodeCost(TypeInstantiation(chain))
            }

            else -> langThrow(TypeSystemBug)
        }
    } else {
        ce
    }

    override fun visit(ast: IntLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.IntLiteralAst.key))
    }

    override fun visit(ast: DecimalLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.DecimalLiteralAst.key))
    }

    override fun visit(ast: BooleanLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.BooleanLiteralAst.key))
    }

    override fun visit(ast: CharLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.CharLiteralAst.key))
    }

    override fun visit(ast: StringLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.StringLiteralAst.key))
    }

    override fun visit(ast: StringInterpolationAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                ast.components.map {
                    it.costExpression
                }
            ),
            AstNodeNames.StringInterpolationAst.key
        )
    }

    override fun visit(ast: LetAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.rhs.costExpression, AstNodeNames.LetAst.key)
    }

    override fun visit(ast: RefAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.RefAst.key))
    }

    override fun visit(ast: FileAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                ast.lines.map {
                    it.costExpression
                }
            ),
            AstNodeNames.FileAst.key
        )
    }

    override fun visit(ast: BlockAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                ast.lines.map {
                    it.costExpression
                }
            ),
            AstNodeNames.BlockAst.key
        )
    }

    override fun visit(ast: FunctionAst) {
        // The body cost will be calculated after topological sort of all functions,
        // therefore we do not call super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.FunctionAst.key))
    }

    override fun visit(ast: LambdaAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.body.costExpression, AstNodeNames.LambdaAst.key)
    }

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.RecordDefinitionAst.key))
    }

    override fun visit(ast: ObjectDefinitionAst) {
        super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.ObjectDefinitionAst.key))
    }

    override fun visit(ast: DotAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.lhs.costExpression, AstNodeNames.DotAst.key)
    }

    override fun visit(ast: GroundApplyAst) {
        super.visit(ast)
        val argCosts: MutableList<CostExpression> = ArrayList()
        val multipliers = when (val groundApplySlot = ast.groundApplySlot) {
            GroundApplySlotError -> langThrow(NotInSource, TypeSystemBug)
            is GroundApplySlotFormal -> listOf()
            is GroundApplySlotGF -> {
                val symbolRef = groundApplySlot.payload
                symbolRef.formalParams.map { it.costMultiplier }
            }

            is GroundApplySlotGSPS -> {
                val symbolRef = groundApplySlot.payload
                symbolRef.formalParams.map { it.costMultiplier }
            }

            is GroundApplySlotGRT -> listOf()
            is GroundApplySlotSI -> {
                val symbolRef = groundApplySlot.payload
                when (val parameterizedSymbol = symbolRef.substitutionChain.terminus) {
                    is ParameterizedFunctionSymbol -> {
                        parameterizedSymbol.formalParams.map { it.costMultiplier }
                    }

                    else -> {
                        listOf()
                    }
                }
            }

            is GroundApplySlotTI -> listOf()
        }
        multipliers.zip(ast.args).forEach {
            argCosts.add(ProductCostExpression(listOf(it.second.costExpression, it.first)))
        }
        val bodyCost = when (val groundApplySlot = ast.groundApplySlot) {
            GroundApplySlotError -> langThrow(NotInSource, TypeSystemBug)
            is GroundApplySlotFormal -> {
                convertCostExpression(groundApplySlot.payload)
            }

            is GroundApplySlotGF -> {
                convertCostExpression(groundApplySlot.payload)
            }

            is GroundApplySlotGSPS -> {
                convertCostExpression(groundApplySlot.payload)
            }

            is GroundApplySlotGRT -> {
                convertCostExpression(groundApplySlot.payload)
            }

            is GroundApplySlotSI -> {
                convertCostExpression(groundApplySlot.payload)
            }

            is GroundApplySlotTI -> {
                convertCostExpression(groundApplySlot.payload)
            }
        }
        if (argCosts.isEmpty()) {
            ast.costExpression = addDefault(bodyCost, AstNodeNames.GroundApplyAst.key)
        } else {
            ast.costExpression = addDefault(
                SumCostExpression(listOf(bodyCost, SumCostExpression(argCosts))),
                AstNodeNames.GroundApplyAst.key
            )
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
        val lhsType = ast.lhs.readType()
        val bodyCost = when (val dotApplySlot = ast.dotApplySlot) {
            DotApplySlotError -> langThrow(NotInSource, TypeSystemBug)
            is DotApplySlotGF -> {
                handleHash(convertCostExpression(dotApplySlot.payload), lhsType)
            }

            is DotApplySlotGMP -> {
                handleHash(convertCostExpression(dotApplySlot.payload), lhsType)
            }

            is DotApplySlotSI -> {
                handleHash(convertCostExpression(dotApplySlot.payload), lhsType)
            }
        }

        if (argCosts.isEmpty()) {
            ast.costExpression =
                addDefault(SumCostExpression(listOf(bodyCost, ast.lhs.costExpression)), AstNodeNames.DotApplyAst.key)
        } else {
            ast.costExpression =
                addDefault(
                    SumCostExpression(listOf(bodyCost, ast.lhs.costExpression, SumCostExpression(argCosts))),
                    AstNodeNames.DotApplyAst.key
                )
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
        ast.costExpression =
            addDefault(ProductCostExpression(listOf(multiplier, ast.body.costExpression)), AstNodeNames.ForEachAst.key)
    }

    override fun visit(ast: AssignAst) {
        super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.AssignAst.key))
    }

    override fun visit(ast: DotAssignAst) {
        super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeNames.DotAssignAst.key))
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
            ),
            AstNodeNames.IfAst.key
        )
    }

    override fun visit(ast: MatchAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                listOf(
                    ast.condition.costExpression,
                    MaxCostExpression(
                        ast.cases.map {
                            it.block.costExpression
                        }
                    )
                )
            ),
            AstNodeNames.MatchAst.key
        )
    }
}