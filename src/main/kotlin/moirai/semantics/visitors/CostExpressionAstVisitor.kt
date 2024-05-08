package moirai.semantics.visitors

import moirai.semantics.core.*
import moirai.semantics.infer.SubstitutionChain
import moirai.semantics.infer.TerminalChain

internal class CostExpressionAstVisitor(private val architecture: Architecture) : UnitAstVisitor() {
    private fun addDefault(costExpression: CostExpression, nodeKind: AstNodeKind): CostExpression =
        SumCostExpression(listOf(Fin(getDefaultNodeCost(nodeKind)), costExpression))

    private fun getDefaultNodeCost(nodeKind: AstNodeKind): Long =
        when (val overlay = architecture.getNodeCostOverlay(nodeKind)) {
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
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.IntLiteralAst))
    }

    override fun visit(ast: DecimalLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.DecimalLiteralAst))
    }

    override fun visit(ast: BooleanLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.BooleanLiteralAst))
    }

    override fun visit(ast: CharLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.CharLiteralAst))
    }

    override fun visit(ast: StringLiteralAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.StringLiteralAst))
    }

    override fun visit(ast: StringInterpolationAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                ast.components.map {
                    it.costExpression
                }
            ),
            AstNodeKind.StringInterpolationAst
        )
    }

    override fun visit(ast: LetAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.rhs.costExpression, AstNodeKind.LetAst)
    }

    override fun visit(ast: RefAst) {
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.RefAst))
    }

    override fun visit(ast: FileAst) {
        super.visit(ast)
        ast.costExpression = addDefault(
            SumCostExpression(
                ast.lines.map {
                    it.costExpression
                }
            ),
            AstNodeKind.FileAst
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
            AstNodeKind.BlockAst
        )
    }

    override fun visit(ast: FunctionAst) {
        // The body cost will be calculated after topological sort of all functions,
        // therefore we do not call super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.FunctionAst))
    }

    override fun visit(ast: LambdaAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.body.costExpression, AstNodeKind.LambdaAst)
    }

    override fun visit(ast: RecordDefinitionAst) {
        super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.RecordDefinitionAst))
    }

    override fun visit(ast: ObjectDefinitionAst) {
        super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.ObjectDefinitionAst))
    }

    override fun visit(ast: DotAst) {
        super.visit(ast)
        ast.costExpression = addDefault(ast.lhs.costExpression, AstNodeKind.DotAst)
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
            ast.costExpression = addDefault(bodyCost, AstNodeKind.GroundApplyAst)
        } else {
            ast.costExpression = addDefault(
                SumCostExpression(listOf(bodyCost, SumCostExpression(argCosts))),
                AstNodeKind.GroundApplyAst
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
                addDefault(SumCostExpression(listOf(bodyCost, ast.lhs.costExpression)), AstNodeKind.DotApplyAst)
        } else {
            ast.costExpression =
                addDefault(
                    SumCostExpression(listOf(bodyCost, ast.lhs.costExpression, SumCostExpression(argCosts))),
                    AstNodeKind.DotApplyAst
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
            addDefault(ProductCostExpression(listOf(multiplier, ast.body.costExpression)), AstNodeKind.ForEachAst)
    }

    override fun visit(ast: AssignAst) {
        super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.AssignAst))
    }

    override fun visit(ast: DotAssignAst) {
        super.visit(ast)
        ast.costExpression = Fin(getDefaultNodeCost(AstNodeKind.DotAssignAst))
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
            AstNodeKind.IfAst
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
            AstNodeKind.MatchAst
        )
    }
}