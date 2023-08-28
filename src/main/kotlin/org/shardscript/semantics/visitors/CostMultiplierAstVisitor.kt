package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*

data class ParamCostExMultiplier(
    val formalParameterSymbol: FunctionFormalParameterSymbol,
    val costExpression: CostExpression
)

class CostMultiplierAstVisitor(val architecture: Architecture) : AstVisitor<List<ParamCostExMultiplier>> {
    override fun visit(ast: SByteLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: ShortLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: IntLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: LongLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: ByteLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: UShortLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: UIntLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: ULongLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: DecimalLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: BooleanLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: CharLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: StringLiteralAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: StringInterpolationAst): List<ParamCostExMultiplier> {
        return ast.components.flatMap { it.accept(this) }
    }

    override fun visit(ast: LetAst): List<ParamCostExMultiplier> {
        return ast.rhs.accept(this)
    }

    override fun visit(ast: RefAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: FileAst): List<ParamCostExMultiplier> {
        return ast.lines.flatMap { it.accept(this) }
    }

    override fun visit(ast: BlockAst): List<ParamCostExMultiplier> {
        return ast.lines.flatMap { it.accept(this) }
    }

    override fun visit(ast: FunctionAst): List<ParamCostExMultiplier> {
        val paramMultipliers = ast.body.accept(this)
        val paramMultiplierMap: MutableMap<FunctionFormalParameterSymbol, MutableList<CostExpression>> = HashMap()
        paramMultipliers.forEach {
            if (!paramMultiplierMap.containsKey(it.formalParameterSymbol)) {
                paramMultiplierMap[it.formalParameterSymbol] = ArrayList()
            }
            paramMultiplierMap[it.formalParameterSymbol]!!.add(it.costExpression)
        }
        paramMultiplierMap.forEach {
            if (it.value.size == 1) {
                it.key.costMultiplier = it.value.first()
            } else if (it.value.size > 1) {
                it.key.costMultiplier = SumCostExpression(it.value.toList())
            }
        }
        return listOf()
    }

    override fun visit(ast: RecordDefinitionAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: ObjectDefinitionAst): List<ParamCostExMultiplier> {
        return listOf()
    }

    override fun visit(ast: DotAst): List<ParamCostExMultiplier> {
        return ast.lhs.accept(this)
    }

    override fun visit(ast: GroundApplyAst): List<ParamCostExMultiplier> {
        val res = ast.args.flatMap { it.accept(this) }.toMutableList()
        when (val symbolRef = ast.symbolRef) {
            is FunctionFormalParameterSymbol -> {
                when (symbolRef.ofTypeSymbol) {
                    is FunctionTypeSymbol -> res.add(
                        ParamCostExMultiplier(
                            symbolRef,
                            OmicronTypeSymbol(architecture.defaultNodeCost)
                        )
                    )
                    else -> Unit
                }
            }
            else -> Unit
        }
        return res
    }

    override fun visit(ast: DotApplyAst): List<ParamCostExMultiplier> {
        val res = ast.lhs.accept(this).toMutableList()
        ast.args.forEach { res.addAll(it.accept(this)) }
        return res
    }

    override fun visit(ast: ForEachAst): List<ParamCostExMultiplier> {
        val res = ast.source.accept(this).toMutableList()
        val multiplier: CostExpression = when (val omicron = ast.sourceOmicronSymbol) {
            is ImmutableOmicronTypeParameter -> omicron
            is OmicronTypeSymbol -> omicron
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
        val body = ast.body.accept(this)
        body.forEach {
            res.add(
                ParamCostExMultiplier(
                    it.formalParameterSymbol,
                    ProductCostExpression(listOf(it.costExpression, multiplier))
                )
            )
        }
        return res
    }

    override fun visit(ast: AssignAst): List<ParamCostExMultiplier> {
        return ast.rhs.accept(this).toMutableList()
    }

    override fun visit(ast: DotAssignAst): List<ParamCostExMultiplier> {
        val res = ast.lhs.accept(this).toMutableList()
        res.addAll(ast.rhs.accept(this))
        return res
    }

    override fun visit(ast: IfAst): List<ParamCostExMultiplier> {
        val res = ast.condition.accept(this).toMutableList()
        res.addAll(ast.trueBranch.accept(this))
        res.addAll(ast.falseBranch.accept(this))
        return res
    }

    override fun visit(ast: AsAst): List<ParamCostExMultiplier> {
        return ast.lhs.accept(this)
    }

    override fun visit(ast: IsAst): List<ParamCostExMultiplier> {
        return ast.lhs.accept(this)
    }
}