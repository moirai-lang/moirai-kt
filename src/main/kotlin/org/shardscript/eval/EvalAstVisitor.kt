package org.shardscript.eval

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.infer.SubstitutionChain
import org.shardscript.semantics.prelude.Lang

data class EvalContext(val values: ValueTable, val substitutions: Map<TypeParameter, Type>)

class EvalAstVisitor(architecture: Architecture, private val globalScope: ValueTable) : ParameterizedAstVisitor<EvalContext, Value> {
    private val evalCostVisitor = EvalCostExpressionVisitor(architecture)

    fun invoke(functionValue: FunctionValue, args: List<Value>, substitutions: Map<TypeParameter, Type>): Value {
        val functionScope = ValueTable(globalScope)
        functionValue.formalParams.zip(args).forEach {
            functionScope.define(it.first.identifier, it.second)
        }
        return functionValue.body.accept(this, EvalContext(functionScope, substitutions))
    }

    private fun computeFin(typeParameter: FinTypeParameter, substitutions: Map<TypeParameter, Type>): Long {
        if (substitutions.containsKey(typeParameter)) {
            val mapped = substitutions[typeParameter]!!
            if (mapped is CostExpression && mapped.accept(CanEvalCostExpressionVisitor)) {
                return mapped.accept(evalCostVisitor)
            } else {
                langThrow(NotInSource, RuntimeCostExpressionEvalFailed)
            }
        } else {
            langThrow(NotInSource, RuntimeCostExpressionEvalFailed)
        }
    }

    private fun <T: RawTerminus> createSubstitutions(
        substitutions: Map<TypeParameter, Type>,
        substitutionChain: SubstitutionChain<T>
    ): Map<TypeParameter, Type> {
        if (substitutions.isEmpty()) {
            return substitutionChain.terminus.typeParams.zip(substitutionChain.replayArgs()).toMap()
        }

        val entries = substitutions.entries.toList()
        val params = entries.map { it.key }
        val args = entries.map { it.value }
        val extendedSub = Substitution(params, args)
        val extendedChain = SubstitutionChain(extendedSub, substitutionChain)
        return substitutionChain.terminus.typeParams.zip(extendedChain.replayArgs()).toMap()
    }

    override fun visit(ast: FileAst, param: EvalContext): Value {
        val blockScope = ValueTable(param.values)
        val resLines = ast.lines.map { it.accept(this, EvalContext(blockScope, param.substitutions)) }
        return resLines.last()
    }

    override fun visit(ast: BlockAst, param: EvalContext): Value {
        val blockScope = ValueTable(param.values)
        val resLines = ast.lines.map { it.accept(this, EvalContext(blockScope, param.substitutions)) }
        return resLines.last()
    }

    override fun visit(ast: LetAst, param: EvalContext): Value {
        val right = ast.rhs.accept(this, param)
        param.values.define(ast.identifier, right)
        return UnitValue
    }

    override fun visit(ast: RefAst, param: EvalContext): Value {
        return when (val refSlot = ast.refSlot) {
            is RefSlotObject -> ObjectValue(refSlot.payload)
            is RefSlotPlatformObject -> if (refSlot.payload.identifier == Lang.unitId) {
                UnitValue
            } else {
                langThrow(NotInSource, TypeSystemBug)
            }

            else -> param.values.fetch(ast.identifier)
        }
    }

    override fun visit(ast: IntLiteralAst, param: EvalContext): Value =
        IntValue(ast.canonicalForm)

    override fun visit(ast: BooleanLiteralAst, param: EvalContext): Value =
        BooleanValue(ast.canonicalForm)

    override fun visit(ast: DecimalLiteralAst, param: EvalContext): Value =
        DecimalValue(ast.canonicalForm)

    override fun visit(ast: CharLiteralAst, param: EvalContext): Value =
        CharValue(ast.canonicalForm)

    override fun visit(ast: StringLiteralAst, param: EvalContext): Value =
        StringValue(ast.canonicalForm)

    override fun visit(ast: StringInterpolationAst, param: EvalContext): Value {
        val sb = StringBuilder()
        ast.components.forEach {
            when (val component = it.accept(this, param)) {
                is StringValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is IntValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is DecimalValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is BooleanValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is CharValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is UnitValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                else -> langThrow(it.ctx, TypeSystemBug)
            }
        }
        return StringValue(sb.toString())
    }

    override fun visit(ast: FunctionAst, param: EvalContext): Value =
        UnitValue

    override fun visit(ast: LambdaAst, param: EvalContext): Value {
        val lambdaSymbol = ast.scope as LambdaSymbol
        return FunctionValue(lambdaSymbol.formalParams, ast.body)
    }

    override fun visit(ast: RecordDefinitionAst, param: EvalContext): Value =
        UnitValue

    override fun visit(ast: ObjectDefinitionAst, param: EvalContext): Value =
        UnitValue

    override fun visit(ast: DotAst, param: EvalContext): Value {
        return when (val lhs = ast.lhs.accept(this, param)) {
            is RecordValue -> {
                lhs.fields.fetch(ast.identifier)
            }
            is ListValue -> {
                when (val dotSlot = ast.dotSlot) {
                    is DotSlotPlatformField -> Plugins.fields[dotSlot.payload]!!.invoke(lhs)
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            is DictionaryValue -> {
                when (val dotSlot = ast.dotSlot) {
                    is DotSlotPlatformField -> Plugins.fields[dotSlot.payload]!!.invoke(lhs)
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            is SetValue -> {
                when (val dotSlot = ast.dotSlot) {
                    is DotSlotPlatformField -> Plugins.fields[dotSlot.payload]!!.invoke(lhs)
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            is StringValue -> {
                when (val dotSlot = ast.dotSlot) {
                    is DotSlotPlatformField -> Plugins.fields[dotSlot.payload]!!.invoke(lhs)
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: GroundApplyAst, param: EvalContext): Value {
        return when (val groundApplySlot = ast.groundApplySlot) {
            GroundApplySlotError -> langThrow(NotInSource, TypeSystemBug)
            is GroundApplySlotFormal -> {
                val args = ast.args.map { it.accept(this, param) }
                when (val toApply = param.values.fetch(groundApplySlot.payload.identifier)) {
                    is FunctionValue -> {
                        invoke(toApply, args, param.substitutions)
                    }
                    else -> langThrow(NotInSource, TypeSystemBug)
                }
            }
            is GroundApplySlotGF -> {
                val args = ast.args.map { it.accept(this, param) }
                val toApply = FunctionValue(groundApplySlot.payload.formalParams, groundApplySlot.payload.body)
                invoke(toApply, args, param.substitutions)
            }
            is GroundApplySlotGRT -> {
                val args = ast.args.map { it.accept(this, param) }
                val fields = ValueTable(NullValueTable)
                groundApplySlot.payload.fields.zip(args).forEach {
                    fields.define(it.first.identifier, it.second)
                }
                val res = RecordValue(groundApplySlot.payload, fields, mapOf())
                res.scope = groundApplySlot.payload
                res
            }
            is GroundApplySlotSI -> {
                val args = ast.args.map { it.accept(this, param) }
                when (val terminus = groundApplySlot.payload.substitutionChain.terminus) {
                    is ParameterizedFunctionSymbol -> {
                        val substitutions = createSubstitutions(param.substitutions, groundApplySlot.payload.substitutionChain)
                        val toApply = FunctionValue(terminus.formalParams, terminus.body)
                        invoke(toApply, args, substitutions)
                    }
                    is ParameterizedMemberPluginSymbol -> langThrow(NotInSource, TypeSystemBug)
                    is ParameterizedStaticPluginSymbol -> Plugins.staticPlugins[terminus]!!.invoke(args)
                }
            }
            is GroundApplySlotTI -> {
                val args = ast.args.map { it.accept(this, param) }
                when (val terminus = groundApplySlot.payload.substitutionChain.terminus) {
                    is ParameterizedBasicType -> {
                        when (terminus.identifier) {
                            Lang.listId -> {
                                val substitutions = createSubstitutions(param.substitutions, groundApplySlot.payload.substitutionChain)
                                ListValue(
                                    args.toMutableList(),
                                    substitutions,
                                    computeFin(Lang.listFinTypeParam, substitutions),
                                    false
                                )
                            }

                            Lang.mutableListId -> {
                                val substitutions = createSubstitutions(param.substitutions, groundApplySlot.payload.substitutionChain)
                                ListValue(
                                    args.toMutableList(),
                                    substitutions,
                                    computeFin(Lang.mutableListFinTypeParam, substitutions),
                                    true
                                )
                            }

                            Lang.dictionaryId -> {
                                val pairs = args.map {
                                    it as RecordValue
                                }.map {
                                    Pair(
                                        it.fields.fetchHere(Lang.pairFirstId),
                                        it.fields.fetchHere(Lang.pairSecondId)
                                    )
                                }
                                val substitutions = createSubstitutions(param.substitutions, groundApplySlot.payload.substitutionChain)
                                DictionaryValue(
                                    pairs.toMap().toMutableMap(),
                                    substitutions,
                                    computeFin(Lang.dictionaryFinTypeParam, substitutions),
                                    false
                                )
                            }

                            Lang.mutableDictionaryId -> {
                                val pairs = args.map {
                                    it as RecordValue
                                }.map {
                                    Pair(
                                        it.fields.fetchHere(Lang.pairFirstId),
                                        it.fields.fetchHere(Lang.pairSecondId)
                                    )
                                }
                                val substitutions = createSubstitutions(param.substitutions, groundApplySlot.payload.substitutionChain)
                                DictionaryValue(
                                    pairs.toMap().toMutableMap(),
                                    substitutions,
                                    computeFin(Lang.mutableDictionaryFinTypeParam, substitutions),
                                    true
                                )
                            }

                            Lang.setId -> {
                                val substitutions = createSubstitutions(param.substitutions, groundApplySlot.payload.substitutionChain)
                                SetValue(
                                    args.toMutableSet(),
                                    substitutions,
                                    computeFin(Lang.setFinTypeParam, substitutions),
                                    false
                                )
                            }

                            Lang.mutableSetId -> {
                                val substitutions = createSubstitutions(param.substitutions, groundApplySlot.payload.substitutionChain)
                                SetValue(
                                    args.toMutableSet(),
                                    substitutions,
                                    computeFin(Lang.mutableSetFinTypeParam, substitutions),
                                    true
                                )
                            }

                            else -> langThrow(NotInSource, TypeSystemBug)
                        }
                    }
                    is ParameterizedRecordType -> {
                        val fields = ValueTable(NullValueTable)
                        terminus.fields.zip(args).forEach {
                            fields.define(it.first.identifier, it.second)
                        }
                        val substitutions = createSubstitutions(param.substitutions, groundApplySlot.payload.substitutionChain)
                        val res = RecordValue(groundApplySlot.payload, fields, substitutions)
                        res.scope = terminus
                        res
                    }
                }
            }
        }
    }

    override fun visit(ast: DotApplyAst, param: EvalContext): Value {
        val args = ast.args.map { it.accept(this, param) }
        try {
            val lhs = ast.lhs.accept(this, param)
            when (val dotApplySlot = ast.dotApplySlot) {
                DotApplySlotError -> langThrow(ast.ctx, TypeSystemBug)
                is DotApplySlotGF -> {
                    val toApply = dotApplySlot.payload
                    when (lhs) {
                        is RecordValue -> {
                            val functionScope = ValueTable(lhs.fields)
                            val function = toApply.body
                            toApply.formalParams.zip(args).forEach {
                                functionScope.define(it.first.identifier, it.second)
                            }
                            return function.accept(this, EvalContext(functionScope, param.substitutions))
                        }
                        else -> {
                            langThrow(ast.ctx, TypeSystemBug)
                        }
                    }
                }
                is DotApplySlotGMP -> {
                    val toApply = dotApplySlot.payload
                    return Plugins.groundMemberPlugins[toApply]!!.invoke(lhs, args)
                }
                is DotApplySlotSI -> {
                    val toApply = dotApplySlot.payload
                    when (val parameterizedType = toApply.substitutionChain.terminus) {
                        is ParameterizedFunctionSymbol -> {
                            when (lhs) {
                                is RecordValue -> {
                                    val functionScope = ValueTable(lhs.fields)
                                    val function = parameterizedType.body
                                    parameterizedType.formalParams.zip(args).forEach {
                                        functionScope.define(it.first.identifier, it.second)
                                    }
                                    return function.accept(this, EvalContext(functionScope, param.substitutions))
                                }
                                else -> {
                                    langThrow(ast.ctx, TypeSystemBug)
                                }
                            }
                        }
                        is ParameterizedMemberPluginSymbol -> {
                            return Plugins.parameterizedMemberPlugins[parameterizedType]!!.invoke(lhs, args)
                        }
                        else -> langThrow(ast.ctx, TypeSystemBug)
                    }
                }
            }
        } catch (ex: ArithmeticException) {
            langThrow(ast.ctx, DecimalInfiniteDivide)
        }
    }

    override fun visit(ast: ForEachAst, param: EvalContext): Value {
        when (val source = ast.source.accept(this, param)) {
            is ListValue -> {
                source.elements.forEach {
                    val bodyScope = ValueTable(param.values)
                    bodyScope.define(ast.identifier, it)
                    ast.body.accept(this, EvalContext(bodyScope, param.substitutions))
                }
                return UnitValue
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: AssignAst, param: EvalContext): Value {
        val rhs = ast.rhs.accept(this, param)
        param.values.assign(ast.identifier, rhs)
        return UnitValue
    }

    override fun visit(ast: DotAssignAst, param: EvalContext): Value {
        return when (val lhs = ast.lhs.accept(this, param)) {
            is RecordValue -> {
                val rhs = ast.rhs.accept(this, param)
                lhs.fields.assign(ast.identifier, rhs)
                UnitValue
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: IfAst, param: EvalContext): Value {
        val condition = ast.condition.accept(this, param)
        if (condition is BooleanValue) {
            return if (condition.canonicalForm) {
                ast.trueBranch.accept(this, param)
            } else {
                ast.falseBranch.accept(this, param)
            }
        } else {
            langThrow(ast.ctx, TypeSystemBug)
        }
    }
}