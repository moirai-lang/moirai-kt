/**
 * Copyright 2020 Bryan Croteau
 */
package com.tsikhe.shardscript.eval

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.prelude.Lang

class EvalAstVisitor(prelude: PreludeTable) : ParameterizedAstVisitor<ValueTable, Value> {
    private val someConstructor = RecordConstructorValue(prelude, prelude.fetch(Lang.someId))
    private val rightConstructor = RecordConstructorValue(prelude, prelude.fetch(Lang.rightId))
    private val successConstructor = RecordConstructorValue(prelude, prelude.fetch(Lang.successId))

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
        param.define(ast.gid, right)
        return UnitValue
    }

    override fun visit(ast: RefAst, param: ValueTable): Value {
        return param.fetch(ast.gid)
    }

    override fun visit(ast: SByteLiteralAst, param: ValueTable): Value =
        SByteValue(ast.canonicalForm)

    override fun visit(ast: ShortLiteralAst, param: ValueTable): Value =
        ShortValue(ast.canonicalForm)

    override fun visit(ast: IntLiteralAst, param: ValueTable): Value =
        IntValue(ast.canonicalForm)

    override fun visit(ast: LongLiteralAst, param: ValueTable): Value =
        LongValue(ast.canonicalForm)

    override fun visit(ast: ByteLiteralAst, param: ValueTable): Value =
        ByteValue(ast.canonicalForm)

    override fun visit(ast: UShortLiteralAst, param: ValueTable): Value =
        UShortValue(ast.canonicalForm)

    override fun visit(ast: UIntLiteralAst, param: ValueTable): Value =
        UIntValue(ast.canonicalForm)

    override fun visit(ast: ULongLiteralAst, param: ValueTable): Value =
        ULongValue(ast.canonicalForm)

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
                is SByteValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is ShortValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is IntValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is LongValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is ByteValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is UShortValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is UIntValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
                is ULongValue -> sb.append((component.evalToString() as StringValue).canonicalForm)
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

    override fun visit(ast: RecordDefinitionAst, param: ValueTable): Value =
        UnitValue

    override fun visit(ast: ObjectDefinitionAst, param: ValueTable): Value =
        UnitValue

    override fun visit(ast: EnumDefinitionAst, param: ValueTable): Value =
        UnitValue

    override fun visit(ast: DotAst, param: ValueTable): Value {
        return when (val lhs = ast.lhs.accept(this, param)) {
            is RecordValue -> {
                lhs.fields.fetch(ast.gid)
            }
            is ListValue -> {
                (ast.symbolRef as PlatformFieldSymbol).accessor(lhs)
            }
            is DictionaryValue -> {
                (ast.symbolRef as PlatformFieldSymbol).accessor(lhs)
            }
            is SetValue -> {
                (ast.symbolRef as PlatformFieldSymbol).accessor(lhs)
            }
            is StringValue -> {
                (ast.symbolRef as PlatformFieldSymbol).accessor(lhs)
            }
            is NamespaceValue -> {
                lhs.router.fetchHere(ast.gid)
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: GroundApplyAst, param: ValueTable): Value {
        val args = ast.args.map { it.accept(this, param) }
        return when (val toApply = param.fetch(ast.tti)) {
            is FunctionValue -> {
                toApply.invoke(args)
            }
            is RecordConstructorValue -> {
                toApply.apply(args)
            }
            is ListConstructorValue -> {
                val instantiation = ast.symbolRef as SymbolInstantiation
                toApply.apply(instantiation.substitutionChain.replayArgs(), args)
            }
            is SetConstructorValue -> {
                val instantiation = ast.symbolRef as SymbolInstantiation
                toApply.apply(instantiation.substitutionChain.replayArgs(), args)
            }
            is DictionaryConstructorValue -> {
                val instantiation = ast.symbolRef as SymbolInstantiation
                toApply.apply(instantiation.substitutionChain.replayArgs(), args)
            }
            is PluginValue -> {
                toApply.invoke(args)
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: DotApplyAst, param: ValueTable): Value {
        val args = ast.args.map { it.accept(this, param) }
        try {
            when (val lhs = ast.lhs.accept(this, param)) {
                is NamespaceValue -> {
                    return when (val toApply = lhs.router.fetchHere(ast.tti)) {
                        is FunctionValue -> {
                            toApply.invoke(args)
                        }
                        is RecordConstructorValue -> {
                            toApply.apply(args)
                        }
                        is PluginValue -> {
                            toApply.invoke(args)
                        }
                        else -> {
                            langThrow(ast.ctx, TypeSystemBug)
                        }
                    }
                }
                else -> {
                    when (val toApply = ast.symbolRef) {
                        is GroundMemberPluginSymbol -> {
                            return toApply.invoke(lhs, args)
                        }
                        is SymbolInstantiation -> {
                            when (val parameterizedType = toApply.substitutionChain.originalSymbol) {
                                is ParameterizedFunctionSymbol -> {
                                    when (lhs) {
                                        is RecordValue -> {
                                            val functionScope = ValueTable(lhs.fields)
                                            val function = parameterizedType.body
                                            parameterizedType.formalParams.zip(args).forEach {
                                                functionScope.define(it.first.gid, it.second)
                                            }
                                            return function.accept(this, functionScope)
                                        }
                                        else -> {
                                            langThrow(ast.ctx, TypeSystemBug)
                                        }
                                    }
                                }
                                is ParameterizedMemberPluginSymbol -> {
                                    return parameterizedType.invoke(lhs, args)
                                }
                                else -> langThrow(ast.ctx, TypeSystemBug)
                            }
                        }
                        is ParameterizedStaticPluginSymbol -> {
                            langThrow(ast.ctx, TypeSystemBug)
                        }
                        is GroundFunctionSymbol -> {
                            when (lhs) {
                                is RecordValue -> {
                                    val functionScope = ValueTable(lhs.fields)
                                    val function = toApply.body
                                    toApply.formalParams.zip(args).forEach {
                                        functionScope.define(it.first.gid, it.second)
                                    }
                                    return function.accept(this, functionScope)
                                }
                                else -> {
                                    langThrow(ast.ctx, TypeSystemBug)
                                }
                            }
                        }
                        else -> {
                            langThrow(ast.ctx, TypeSystemBug)
                        }
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
                    bodyScope.define(ast.gid, it)
                    ast.body.accept(this, bodyScope)
                }
                return UnitValue
            }
            is ObjectValue -> {
                when (generatePath(source.symbol)) {
                    listOf(
                        Lang.shardId.name,
                        Lang.langId.name,
                        Lang.optionId.name,
                        Lang.noneId.name
                    ) -> return UnitValue
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            is RecordValue -> {
                return when (generatePath(source.symbol)) {
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.optionId.name, Lang.someId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.someFieldId))
                        ast.body.accept(this, bodyScope)
                        UnitValue
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.eitherId.name, Lang.rightId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.rightFieldId))
                        ast.body.accept(this, bodyScope)
                        UnitValue
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.resultId.name, Lang.successId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.successFieldId))
                        ast.body.accept(this, bodyScope)
                        UnitValue
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.eitherId.name, Lang.leftId.name),
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.resultId.name, Lang.failureId.name) -> UnitValue
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: MapAst, param: ValueTable): Value {
        when (val source = ast.source.accept(this, param)) {
            is ListValue -> {
                return ListValue(ImmutableBasicTypeMode, source.elements.map {
                    val bodyScope = ValueTable(param)
                    bodyScope.define(ast.gid, it)
                    ast.body.accept(this, bodyScope)
                }.toMutableList())
            }
            is ObjectValue -> {
                when (generatePath(source.symbol)) {
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.optionId.name, Lang.noneId.name) -> return source
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            is RecordValue -> {
                return when (generatePath(source.symbol)) {
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.optionId.name, Lang.someId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.someFieldId))
                        val bodyRes = ast.body.accept(this, bodyScope)
                        someConstructor.apply(listOf(bodyRes))
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.eitherId.name, Lang.rightId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.rightFieldId))
                        val bodyRes = ast.body.accept(this, bodyScope)
                        rightConstructor.apply(listOf(bodyRes))
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.resultId.name, Lang.successId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.successFieldId))
                        val bodyRes = ast.body.accept(this, bodyScope)
                        successConstructor.apply(listOf(bodyRes))
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.eitherId.name, Lang.leftId.name),
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.resultId.name, Lang.failureId.name) -> UnitValue
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: FlatMapAst, param: ValueTable): Value {
        when (val source = ast.source.accept(this, param)) {
            is ListValue -> {
                return ListValue(ImmutableBasicTypeMode, source.elements.flatMap {
                    val bodyScope = ValueTable(param)
                    bodyScope.define(ast.gid, it)
                    (ast.body.accept(this, bodyScope) as ListValue).elements
                }.toMutableList())
            }
            is ObjectValue -> {
                when (generatePath(source.symbol)) {
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.optionId.name, Lang.noneId.name) -> return source
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            is RecordValue -> {
                return when (generatePath(source.symbol)) {
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.optionId.name, Lang.someId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.someFieldId))
                        ast.body.accept(this, bodyScope)
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.eitherId.name, Lang.rightId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.rightFieldId))
                        ast.body.accept(this, bodyScope)
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.resultId.name, Lang.successId.name) -> {
                        val bodyScope = ValueTable(param)
                        bodyScope.define(ast.gid, source.fields.fetchHere(Lang.successFieldId))
                        ast.body.accept(this, bodyScope)
                    }
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.eitherId.name, Lang.leftId.name),
                    listOf(Lang.shardId.name, Lang.langId.name, Lang.resultId.name, Lang.failureId.name) -> UnitValue
                    else -> langThrow(ast.ctx, TypeSystemBug)
                }
            }
            else -> {
                langThrow(ast.ctx, TypeSystemBug)
            }
        }
    }

    override fun visit(ast: AssignAst, param: ValueTable): Value {
        val rhs = ast.rhs.accept(this, param)
        param.assign(ast.gid, rhs)
        return UnitValue
    }

    override fun visit(ast: DotAssignAst, param: ValueTable): Value {
        return when (val lhs = ast.lhs.accept(this, param)) {
            is RecordValue -> {
                val rhs = ast.rhs.accept(this, param)
                lhs.fields.assign(ast.gid, rhs)
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

    override fun visit(ast: SwitchAst, param: ValueTable): Value {
        val path = when (val source = ast.source.accept(this, param)) {
            is ObjectValue -> source.path
            is RecordValue -> source.path
            else -> langThrow(ast.ctx, TypeSystemBug)
        }
        ast.cases.forEach {
            when (it) {
                is CoproductBranch -> {
                    if (it.path == path) {
                        return it.body.accept(this, param)
                    }
                }
                is ElseBranch -> {
                    return it.body.accept(this, param)
                }
            }
        }
        langThrow(ast.ctx, TypeSystemBug)
    }

    override fun visit(ast: AsAst, param: ValueTable): Value {
        return ast.lhs.accept(this, param)
    }

    override fun visit(ast: IsAst, param: ValueTable): Value {
        ast.lhs.accept(this, param)
        return ast.result
    }
}