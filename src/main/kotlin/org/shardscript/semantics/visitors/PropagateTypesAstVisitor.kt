package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.infer.instantiateFunction
import org.shardscript.semantics.infer.instantiateRecord
import org.shardscript.semantics.prelude.Lang

class PropagateTypesAstVisitor(
    private val preludeTable: Scope<Symbol>
) : UnitAstVisitor() {
    private fun groundApply(ast: SymbolRefAst, signifier: Signifier, args: List<Ast>, symbol: Symbol) {
        when (symbol) {
            is ErrorSymbol -> ast.assignType(errors, ErrorSymbol)
            is GroundFunctionSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    errors.add(signifier.ctx, SymbolHasNoParameters(signifier))
                }
                ast.assignType(errors, symbol.returnType)
            }
            is ParameterizedFunctionSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    val idArgs = signifier.args
                    val idArgSymbols = idArgs.map { symbolToType(errors, it.ctx, ast.scope.fetch(it), it) }
                    if (idArgs.size == symbol.typeParams.size) {
                        val substitution = Substitution(symbol.typeParams, idArgSymbols)
                        val instantiation = substitution.apply(symbol)
                        ast.symbolRef = instantiation
                        val returnType = instantiation.substitutionChain.replay(symbol.returnType)
                        ast.assignType(errors, returnType)
                    } else {
                        errors.add(ast.ctx, IncorrectNumberOfTypeArgs(symbol.typeParams.size, idArgs.size))
                        ast.assignType(errors, ErrorSymbol)
                    }
                } else {
                    val instantiation = instantiateFunction(ast.ctx, args, symbol, errors)
                    ast.symbolRef = instantiation
                    val returnType = instantiation.substitutionChain.replay(symbol.returnType)
                    ast.assignType(errors, returnType)
                }
            }
            is FunctionFormalParameterSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    errors.add(signifier.ctx, SymbolHasNoParameters(signifier))
                }
                when (val ofTypeSymbol = symbol.ofTypeSymbol) {
                    is FunctionTypeSymbol -> ast.assignType(errors, ofTypeSymbol.returnType)
                    else -> {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                        ast.assignType(errors, ErrorSymbol)
                    }
                }
            }
            is GroundRecordTypeSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    errors.add(signifier.ctx, SymbolHasNoParameters(signifier))
                }
                ast.symbolRef = symbol
                ast.assignType(errors, symbol)
            }
            is ParameterizedRecordTypeSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    val idArgs = signifier.args
                    val idArgSymbols = idArgs.map { symbolToType(errors, it.ctx, ast.scope.fetch(it), it) }
                    if (idArgs.size == symbol.typeParams.size) {
                        val substitution = Substitution(symbol.typeParams, idArgSymbols)
                        val instantiation = substitution.apply(symbol)
                        ast.symbolRef = instantiation
                        ast.assignType(errors, instantiation)
                    } else {
                        errors.add(ast.ctx, IncorrectNumberOfTypeArgs(symbol.typeParams.size, idArgs.size))
                        ast.assignType(errors, ErrorSymbol)
                    }
                } else {
                    val instantiation = instantiateRecord(ast.ctx, args, symbol, errors)
                    ast.symbolRef = instantiation
                    ast.assignType(errors, instantiation)
                }
            }
            is ParameterizedBasicTypeSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    val idArgs = signifier.args
                    val idArgSymbols = idArgs.map { symbolToType(errors, it.ctx, ast.scope.fetch(it), it) }
                    if (idArgs.size == symbol.typeParams.size) {
                        val instantiation = symbol.instantiation.apply(
                            ast.ctx,
                            errors,
                            args,
                            symbol,
                            idArgSymbols
                        )
                        ast.symbolRef = instantiation
                        ast.assignType(errors, instantiation)
                    } else {
                        errors.add(ast.ctx, IncorrectNumberOfTypeArgs(symbol.typeParams.size, idArgs.size))
                        ast.assignType(errors, ErrorSymbol)
                    }
                } else {
                    val instantiation = symbol.instantiation.apply(
                        ast.ctx,
                        errors,
                        args,
                        symbol,
                        listOf()
                    )
                    ast.symbolRef = instantiation
                    ast.assignType(errors, instantiation)
                }
            }
            is ParameterizedStaticPluginSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    val idArgs = signifier.args
                    val idArgSymbols = idArgs.map { symbolToType(errors, it.ctx, ast.scope.fetch(it), it) }
                    if (idArgs.size == symbol.typeParams.size) {
                        val instantiation = symbol.instantiation.apply(
                            ast.ctx,
                            errors,
                            args,
                            symbol,
                            idArgSymbols
                        )
                        ast.symbolRef = instantiation
                        val returnType = instantiation.substitutionChain.replay(symbol.returnType)
                        ast.assignType(errors, returnType)
                    } else {
                        errors.add(ast.ctx, IncorrectNumberOfTypeArgs(symbol.typeParams.size, idArgs.size))
                        ast.assignType(errors, ErrorSymbol)
                    }
                } else {
                    val instantiation = symbol.instantiation.apply(
                        ast.ctx,
                        errors,
                        args,
                        symbol,
                        listOf()
                    )
                    ast.symbolRef = instantiation
                    val returnType = instantiation.substitutionChain.replay(symbol.returnType)
                    ast.assignType(errors, returnType)
                }
            }
            else -> {
                errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                ast.assignType(errors, ErrorSymbol)
            }
        }
    }

    override fun visit(ast: IntLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.intId) as Type)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: DecimalLiteralAst) {
        try {
            super.visit(ast)
            val parameterizedType = preludeTable.fetch(Lang.decimalId) as ParameterizedBasicTypeSymbol
            ast.assignType(
                errors, parameterizedType.instantiation.apply(
                    ast.ctx,
                    errors,
                    listOf(ast),
                    parameterizedType,
                    listOf()
                )
            )
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: BooleanLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.booleanId) as Type)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: CharLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.charId) as Type)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: StringLiteralAst) {
        try {
            super.visit(ast)
            val parameterizedType = preludeTable.fetch(Lang.stringId) as ParameterizedBasicTypeSymbol
            ast.assignType(
                errors, parameterizedType.instantiation.apply(
                    ast.ctx,
                    errors,
                    listOf(ast),
                    parameterizedType,
                    listOf()
                )
            )
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: StringInterpolationAst) {
        try {
            super.visit(ast)
            val parameterizedType = preludeTable.fetch(Lang.stringId) as ParameterizedBasicTypeSymbol
            ast.assignType(
                errors, parameterizedType.instantiation.apply(
                    ast.ctx,
                    errors,
                    ast.components,
                    parameterizedType,
                    listOf()
                )
            )
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: LetAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
            if (ast.ofType is ImplicitTypeLiteral) {
                ast.ofTypeSymbol = ast.rhs.readType()
            } else {
                validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                val ofType = symbolToType(errors, ast.ofType.ctx, ast.scope.fetch(ast.ofType), ast.ofType)
                ast.ofTypeSymbol = ofType
            }
            val local = LocalVariableSymbol(ast.scope, ast.identifier, ast.ofTypeSymbol, ast.mutable)
            ast.scope.define(ast.identifier, local)
            ast.symbolRef = local
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: RefAst) {
        try {
            super.visit(ast)
            val symbol = ast.scope.fetch(ast.identifier)
            ast.symbolRef = symbol
            when (symbol) {
                is ErrorSymbol -> ast.assignType(errors, ErrorSymbol)
                is BasicTypeSymbol -> ast.assignType(errors, symbol)
                is LocalVariableSymbol -> ast.assignType(errors, symbol.ofTypeSymbol)
                is ObjectSymbol -> ast.assignType(errors, symbol)
                is PlatformObjectSymbol -> ast.assignType(errors, symbol)
                is FunctionFormalParameterSymbol -> {
                    ast.assignType(errors, symbol.ofTypeSymbol)
                    if (ast.readType() is FunctionTypeSymbol) {
                        errors.add(ast.ctx, CannotRefFunctionParam(ast.identifier))
                    }
                }
                is FieldSymbol -> ast.assignType(errors, symbol.ofTypeSymbol)
                is GroundRecordTypeSymbol -> ast.assignType(errors, symbol)
                is StandardTypeParameter -> ast.assignType(errors, symbol)
                is SymbolInstantiation -> ast.assignType(errors, symbol)
                else -> {
                    errors.add(ast.ctx, InvalidRef(symbol))
                    ast.assignType(errors, ErrorSymbol)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: FileAst) {
        try {
            super.visit(ast)
            if (ast.lines.isEmpty()) {
                ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
            } else {
                ast.assignType(errors, ast.lines.last().readType())
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: BlockAst) {
        try {
            super.visit(ast)
            if (ast.lines.isEmpty()) {
                ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
            } else {
                ast.assignType(errors, ast.lines.last().readType())
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: FunctionAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
            if (ast.typeParams.isEmpty()) {
                val groundFunctionSymbol = ast.scope as GroundFunctionSymbol
                if (groundFunctionSymbol.returnType == Lang.unitObject && ast.body.readType() != Lang.unitObject) {
                    val refAst = RefAst(NotInSource, Lang.unitId)
                    refAst.scope = ast.body.scope
                    refAst.accept(this)
                    ast.body.lines.add(refAst)
                    ast.body.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
                }
            } else {
                val parameterizedFunctionSymbol = ast.scope as ParameterizedFunctionSymbol
                if (parameterizedFunctionSymbol.returnType == Lang.unitObject && ast.body.readType() != Lang.unitObject) {
                    val refAst = RefAst(NotInSource, Lang.unitId)
                    refAst.scope = ast.body.scope
                    refAst.accept(this)
                    ast.body.lines.add(refAst)
                    ast.body.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: LambdaAst) {
        try {
            super.visit(ast)

            val lambdaSymbol = ast.scope as LambdaSymbol
            lambdaSymbol.returnType = ast.body.readType()

            ast.assignType(errors, lambdaSymbol.type())
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: RecordDefinitionAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
            ast.fields.forEach {
                validateExplicitSymbol(ast.ctx, errors, it.ofType, ast.scope)
                it.symbol = ast.scope.fetch(it.ofType)
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: ObjectDefinitionAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: DotAst) {
        try {
            super.visit(ast)
            when (val lhsType = ast.lhs.readType()) {
                is ErrorSymbol -> ast.assignType(errors, ErrorSymbol)
                is GroundRecordTypeSymbol -> {
                    val symbol = lhsType.fetchHere(ast.identifier)
                    ast.symbolRef = symbol
                    when (symbol) {
                        is FieldSymbol -> ast.assignType(errors, symbol.ofTypeSymbol)
                        else -> {
                            errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.originalSymbol) {
                        is ParameterizedRecordTypeSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.identifier)
                            ast.symbolRef = member
                            when (member) {
                                is FieldSymbol -> {
                                    val astType = lhsType.substitutionChain.replay(member.ofTypeSymbol)
                                    ast.assignType(errors, astType)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        is ParameterizedBasicTypeSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.identifier)
                            ast.symbolRef = member
                            when (member) {
                                is PlatformFieldSymbol -> {
                                    val astType = lhsType.substitutionChain.replay(member.ofTypeSymbol)
                                    ast.assignType(errors, astType)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                                    ast.assignType(errors, ErrorSymbol)

                                }
                            }
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, lhsType))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                else -> {
                    errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                    ast.assignType(errors, ErrorSymbol)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: GroundApplyAst) {
        try {
            super.visit(ast)
            ast.tti = when(ast.signifier) {
                is ParameterizedSignifier -> {
                    ast.signifier.tti
                }
                is Identifier -> {
                    ast.signifier
                }
                else -> {
                    langThrow(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                }
            }
            val symbol = ast.scope.fetch(ast.tti)
            filterValidGroundApply(ast.ctx, errors, symbol, ast.signifier)
            ast.symbolRef = symbol
            groundApply(ast, ast.signifier, ast.args, symbol)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: DotApplyAst) {
        try {
            super.visit(ast)
            ast.tti = when(ast.signifier) {
                is ParameterizedSignifier -> {
                    ast.signifier.tti
                }
                is Identifier -> {
                    ast.signifier
                }
                else -> {
                    langThrow(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                }
            }
            when (val lhsType = ast.lhs.readType()) {
                is ErrorSymbol -> ast.assignType(errors, ErrorSymbol)
                is BasicTypeSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    ast.symbolRef = member
                    when (member) {
                        is GroundFunctionSymbol -> {
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        is GroundMemberPluginSymbol -> {
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is GroundRecordTypeSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    ast.symbolRef = member
                    when (member) {
                        is GroundMemberPluginSymbol -> {
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is PlatformObjectSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    ast.symbolRef = member
                    when (member) {
                        is GroundMemberPluginSymbol -> {
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.originalSymbol) {
                        is ParameterizedBasicTypeSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.tti)
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.ctx, CannotExplicitlyInstantiate(member))
                            }
                            when (member) {
                                is GroundMemberPluginSymbol -> {
                                    if (ast.signifier is ParameterizedSignifier) {
                                        errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                                    }
                                    ast.symbolRef = member
                                    filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                                    ast.assignType(errors, member.returnType)
                                }
                                is ParameterizedMemberPluginSymbol -> {
                                    val instantiation = member.instantiation.apply(
                                        ast.ctx,
                                        errors,
                                        ast.args,
                                        member,
                                        lhsType,
                                        listOf()
                                    )
                                    ast.symbolRef = instantiation
                                    filterValidDotApply(ast.ctx, errors, instantiation, ast.signifier)
                                    val returnType =
                                        instantiation.substitutionChain.replay(member.returnType)
                                    ast.assignType(errors, returnType)
                                }
                                is ParameterizedStaticPluginSymbol -> {
                                    errors.add(ast.ctx, TypeSystemBug)
                                    ast.assignType(errors, ErrorSymbol)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        is ParameterizedRecordTypeSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.tti)
                            filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                            ast.symbolRef = member
                            when (member) {
                                is GroundMemberPluginSymbol -> {
                                    if (ast.signifier is ParameterizedSignifier) {
                                        errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                                    }
                                    ast.assignType(errors, member.returnType)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                else -> {
                    errors.add(ast.ctx, SymbolHasNoMembers(ast.signifier, ast.lhs.readType() as Symbol))
                    ast.assignType(errors, ErrorSymbol)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    // Do not call super for collection iterators
    override fun visit(ast: ForEachAst) {
        try {
            ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
            ast.source.accept(this)
            when (val sourceType = ast.source.readType()) {
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = sourceType.substitutionChain.originalSymbol) {
                        is ParameterizedBasicTypeSymbol -> {
                            if (parameterizedSymbol.featureSupport.forEachBlock) {
                                ast.sourceTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                ast.sourceFinSymbol = sourceType.substitutionChain.replayArgs()[1]
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                } else {
                                    validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                                    val ofType = symbolToType(errors, ast.ofType.ctx, ast.scope.fetch(ast.ofType), ast.ofType)
                                    ast.ofTypeSymbol = ofType
                                }
                                ast.body.scope.define(ast.identifier, ast.ofTypeSymbol as Symbol)
                                ast.body.accept(this)
                            } else {
                                errors.add(ast.source.ctx, ForEachFeatureBan(ast.source.readType() as Symbol))
                            }
                        }
                        else -> {
                            errors.add(ast.source.ctx, InvalidSource(ast.source.readType()))
                        }
                    }
                }
                else -> {
                    errors.add(ast.source.ctx, InvalidSource(ast.source.readType()))
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: AssignAst) {
        try {
            super.visit(ast)
            val symbol = ast.scope.fetch(ast.identifier)
            ast.symbolRef = symbol
            when (symbol) {
                is LocalVariableSymbol -> ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
                else -> {
                    errors.add(ast.ctx, InvalidRef(symbol))
                    ast.assignType(errors, ErrorSymbol)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: DotAssignAst) {
        try {
            super.visit(ast)
            when (val lhsType = ast.lhs.readType()) {
                is ErrorSymbol -> ast.assignType(errors, ErrorSymbol)
                is GroundRecordTypeSymbol -> {
                    val member = lhsType.fetchHere(ast.identifier)
                    ast.symbolRef = member
                    when (member) {
                        is FieldSymbol -> ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
                        else -> {
                            errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.originalSymbol) {
                        is ParameterizedRecordTypeSymbol -> {
                            val substitution = lhsType.substitutionChain
                            when (val member = parameterizedSymbol.fetchHere(ast.identifier)) {
                                is FieldSymbol -> {
                                    ast.symbolRef = substitution.replay(member.ofTypeSymbol) as Symbol
                                    ast.assignType(errors, preludeTable.fetch(Lang.unitId) as Type)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                else -> {
                    errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                    ast.assignType(errors, ErrorSymbol)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: IfAst) {
        try {
            super.visit(ast)
            ast.assignType(
                errors,
                findBestType(
                    ast.ctx,
                    errors,
                    listOf(
                        ast.trueBranch.readType(),
                        ast.falseBranch.readType()
                    )
                )
            )
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: AsAst) {
        try {
            super.visit(ast)
            val ofType = symbolToType(errors, ast.signifier.ctx, ast.scope.fetch(ast.signifier), ast.signifier)
            ast.assignType(errors, ofType)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: IsAst) {
        try {
            super.visit(ast)
            val ofType = symbolToType(errors, ast.signifier.ctx, ast.scope.fetch(ast.signifier), ast.signifier)
            ast.identifierSymbol = filterValidTypes(ast.ctx, errors, ofType)
            ast.assignType(errors, preludeTable.fetch(Lang.booleanId) as Type)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }
}