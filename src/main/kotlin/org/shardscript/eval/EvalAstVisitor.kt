package org.shardscript.eval

import org.shardscript.semantics.core.*

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
                Plugins.fields[ast.symbolRef as PlatformFieldSymbol]!!.invoke(lhs)
            }
            is DictionaryValue -> {
                Plugins.fields[ast.symbolRef as PlatformFieldSymbol]!!.invoke(lhs)
            }
            is SetValue -> {
                Plugins.fields[ast.symbolRef as PlatformFieldSymbol]!!.invoke(lhs)
            }
            is StringValue -> {
                Plugins.fields[ast.symbolRef as PlatformFieldSymbol]!!.invoke(lhs)
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
                toApply.invoke(args, globalScope) { a, v ->
                    a.accept(this, v)
                }
            }
            is RecordConstructorValue -> {
                toApply.apply(args)
            }
            is ListConstructorValue -> {
                val instantiation = ast.typeRef as TypeInstantiation
                toApply.apply(instantiation.substitutionChain.replayArgs(), args)
            }
            is SetConstructorValue -> {
                val instantiation = ast.typeRef as TypeInstantiation
                toApply.apply(instantiation.substitutionChain.replayArgs(), args)
            }
            is DictionaryConstructorValue -> {
                val instantiation = ast.typeRef as TypeInstantiation
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
            val lhs = ast.lhs.accept(this, param)
            when (val toApply = ast.symbolRef) {
                is GroundMemberPluginSymbol -> {
                    return Plugins.groundMemberPlugins[toApply]!!.invoke(lhs, args)
                }
                is SymbolInstantiation -> {
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
                is ParameterizedStaticPluginSymbol -> {
                    langThrow(ast.ctx, TypeSystemBug)
                }
                is GroundFunctionSymbol -> {
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
                else -> {
                    langThrow(ast.ctx, TypeSystemBug)
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

    override fun visit(ast: AsAst, param: ValueTable): Value {
        return ast.lhs.accept(this, param)
    }

    override fun visit(ast: IsAst, param: ValueTable): Value {
        ast.lhs.accept(this, param)
        return if (ast.testErrors.toSet().isEmpty()) {
            BooleanValue(true)
        } else {
            BooleanValue(false)
        }
    }
}