package org.shardscript.eval

import org.shardscript.semantics.core.*
import org.shardscript.semantics.prelude.Lang

class EvalAstVisitor(private val globalScope: ValueTable) : ParameterizedAstVisitor<ValueTable, Value> {
    override fun visit(ast: FileAst, param: ValueTable): Value {
        val blockScope = ValueTable(param)
        val resLines = ast.lines.map { it.accept(this, blockScope) }
        return resLines.last()
    }

    override fun visit(ast: BlockAst, param: ValueTable): Value {
        val blockScope = ValueTable(param)
        val resLines = ast.lines.map { it.accept(this, blockScope) }
        return resLines.last()
    }

    override fun visit(ast: LetAst, param: ValueTable): Value {
        val right = ast.rhs.accept(this, param)
        param.define(ast.identifier, right)
        return UnitValue
    }

    override fun visit(ast: RefAst, param: ValueTable): Value {
        return param.fetch(ast.identifier)
    }

    override fun visit(ast: IntLiteralAst, param: ValueTable): Value =
        IntValue(ast.canonicalForm)

    override fun visit(ast: BooleanLiteralAst, param: ValueTable): Value =
        BooleanValue(ast.canonicalForm)

    override fun visit(ast: DecimalLiteralAst, param: ValueTable): Value =
        DecimalValue(ast.canonicalForm)

    override fun visit(ast: CharLiteralAst, param: ValueTable): Value =
        CharValue(ast.canonicalForm)

    override fun visit(ast: StringLiteralAst, param: ValueTable): Value =
        StringValue(ast.canonicalForm)

    override fun visit(ast: StringInterpolationAst, param: ValueTable): Value {
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

    override fun visit(ast: FunctionAst, param: ValueTable): Value =
        UnitValue

    override fun visit(ast: LambdaAst, param: ValueTable): Value {
        val lambdaSymbol = ast.scope as LambdaSymbol
        return FunctionValue(lambdaSymbol.formalParams, ast.body)
    }

    override fun visit(ast: RecordDefinitionAst, param: ValueTable): Value =
        UnitValue

    override fun visit(ast: ObjectDefinitionAst, param: ValueTable): Value =
        UnitValue

    override fun visit(ast: DotAst, param: ValueTable): Value {
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

    override fun visit(ast: GroundApplyAst, param: ValueTable): Value {
        return when (val groundApplySlot = ast.groundApplySlot) {
            GroundApplySlotError -> langThrow(NotInSource, TypeSystemBug)
            is GroundApplySlotFormal -> {
                val args = ast.args.map { it.accept(this, param) }
                when (val toApply = param.fetch(groundApplySlot.payload.identifier)) {
                    is FunctionValue -> {
                        toApply.invoke(args, globalScope) { a, v ->
                            a.accept(this, v)
                        }
                    }
                    else -> langThrow(NotInSource, TypeSystemBug)
                }
            }
            is GroundApplySlotGF -> {
                val args = ast.args.map { it.accept(this, param) }
                val toApply = FunctionValue(groundApplySlot.payload.formalParams, groundApplySlot.payload.body)
                toApply.invoke(args, globalScope) { a, v ->
                    a.accept(this, v)
                }
            }
            is GroundApplySlotGRT -> {
                val args = ast.args.map { it.accept(this, param) }
                val fields = ValueTable(NullValueTable)
                groundApplySlot.payload.fields.zip(args).forEach {
                    fields.define(it.first.identifier, it.second)
                }
                val res = RecordValue(groundApplySlot.payload, fields)
                res.scope = groundApplySlot.payload
                res
            }
            is GroundApplySlotSI -> {
                val args = ast.args.map { it.accept(this, param) }
                when (val terminus = groundApplySlot.payload.substitutionChain.terminus) {
                    is ParameterizedFunctionSymbol -> {
                        val toApply = FunctionValue(terminus.formalParams, terminus.body)
                        toApply.invoke(args, globalScope) { a, v ->
                            a.accept(this, v)
                        }
                    }
                    is ParameterizedMemberPluginSymbol,
                    is ParameterizedStaticPluginSymbol -> langThrow(NotInSource, TypeSystemBug)
                }
            }
            is GroundApplySlotTI -> {
                val args = ast.args.map { it.accept(this, param) }
                when (val terminus = groundApplySlot.payload.substitutionChain.terminus) {
                    is ParameterizedBasicType -> {
                        when (terminus.identifier) {
                            Lang.listId, Lang.mutableListId -> {
                                ListValue(
                                    args.toMutableList(),
                                    args.size.toLong(),
                                    terminus.identifier == Lang.mutableListId
                                )
                            }

                            Lang.dictionaryId, Lang.mutableDictionaryId -> {
                                val pairs = args.map {
                                    it as RecordValue
                                }.map {
                                    Pair(
                                        it.fields.fetchHere(Lang.pairFirstId),
                                        it.fields.fetchHere(Lang.pairSecondId)
                                    )
                                }
                                DictionaryValue(
                                    pairs.toMap().toMutableMap(),
                                    args.size.toLong(),
                                    terminus.identifier == Lang.mutableDictionaryId
                                )
                            }

                            Lang.setId, Lang.mutableSetId -> {
                                SetValue(
                                    args.toMutableSet(),
                                    args.size.toLong(),
                                    terminus.identifier == Lang.mutableSetId
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
                        val res = RecordValue(groundApplySlot.payload, fields)
                        res.scope = terminus
                        res
                    }
                }
            }
        }
    }

    override fun visit(ast: DotApplyAst, param: ValueTable): Value {
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
                            return function.accept(this, functionScope)
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
                                    return function.accept(this, functionScope)
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

    override fun visit(ast: ForEachAst, param: ValueTable): Value {
        when (val source = ast.source.accept(this, param)) {
            is ListValue -> {
                source.elements.forEach {
                    val bodyScope = ValueTable(param)
                    bodyScope.define(ast.identifier, it)
                    ast.body.accept(this, bodyScope)
                }
                return UnitValue
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: AssignAst, param: ValueTable): Value {
        val rhs = ast.rhs.accept(this, param)
        param.assign(ast.identifier, rhs)
        return UnitValue
    }

    override fun visit(ast: DotAssignAst, param: ValueTable): Value {
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

    override fun visit(ast: IfAst, param: ValueTable): Value {
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