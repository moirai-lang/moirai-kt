package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.infer.instantiateFunction
import org.shardscript.semantics.infer.instantiateRecord
import org.shardscript.semantics.prelude.Lang

class PropagateTypesAstVisitor(
    private val preludeTable: Scope
) : UnitAstVisitor() {
    private fun groundApply(ast: SymbolRefAst, signifier: Signifier, args: List<Ast>, scope: Scope) {
        when (val symbol = scope.fetch(signifier)) {
            is ErrorSymbol -> ast.assignType(errors, ErrorType)
            is GroundFunctionSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    errors.add(signifier.ctx, SymbolHasNoParameters(signifier))
                }
                ast.assignType(errors, symbol.returnType)
            }

            is ParameterizedFunctionSymbol -> {
                handleParamFunc(signifier, ast, symbol, args)
            }

            is FunctionFormalParameterSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    errors.add(signifier.ctx, SymbolHasNoParameters(signifier))
                }
                when (val ofTypeSymbol = symbol.ofTypeSymbol) {
                    is FunctionTypeSymbol -> ast.assignType(errors, ofTypeSymbol.returnType)
                    else -> {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                        ast.assignType(errors, ErrorType)
                    }
                }
            }

            is TypePlaceholder -> {
                when (val type = scope.fetchType(signifier)) {
                    is TypeInstantiation -> {
                        when (val terminus = type.substitutionChain.terminus) {
                            is ParameterizedBasicTypeSymbol -> handleParamBasicType(signifier, ast, terminus, args)
                            is ParameterizedRecordTypeSymbol -> handleParamRecord(signifier, ast, terminus, args)
                        }
                    }

                    is GroundRecordTypeSymbol -> {
                        if (signifier is ParameterizedSignifier) {
                            errors.add(signifier.ctx, SymbolHasNoParameters(signifier))
                        }
                        ast.symbolRef = TypePlaceholder
                        ast.typeRef = type
                        ast.assignType(errors, type)
                    }

                    is ParameterizedRecordTypeSymbol -> {
                        handleParamRecord(signifier, ast, type, args)
                    }

                    is ParameterizedBasicTypeSymbol -> {
                        handleParamBasicType(signifier, ast, type, args)
                    }

                    else -> {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                        ast.assignType(errors, ErrorType)
                    }
                }
            }

            is ParameterizedStaticPluginSymbol -> {
                handleParamStatic(signifier, ast, symbol, args)
            }

            is SymbolInstantiation -> {
                when(val terminus = symbol.substitutionChain.terminus) {
                    is ParameterizedFunctionSymbol -> handleParamFunc(signifier, ast, terminus, args)
                    is ParameterizedStaticPluginSymbol -> handleParamStatic(signifier, ast, terminus, args)
                    is ParameterizedMemberPluginSymbol -> {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                        ast.assignType(errors, ErrorType)
                    }
                }
            }

            else -> {
                errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                ast.assignType(errors, ErrorType)
            }
        }
    }

    private fun handleParamStatic(
        signifier: Signifier,
        ast: SymbolRefAst,
        symbol: ParameterizedStaticPluginSymbol,
        args: List<Ast>
    ) {
        if (signifier is ParameterizedSignifier) {
            val idArgs = signifier.args
            val idArgSymbols = idArgs.map { ast.scope.fetchType(it) }
            if (idArgs.size == symbol.typeParams.size) {
                val instantiation = symbol.instantiation.apply(
                    ast.ctx,
                    errors,
                    args,
                    symbol,
                    symbol.identifier,
                    idArgSymbols
                )
                ast.symbolRef = instantiation
                val returnType = instantiation.substitutionChain.replay(symbol.returnType)
                ast.assignType(errors, returnType)
            } else {
                errors.add(ast.ctx, IncorrectNumberOfTypeArgs(symbol.typeParams.size, idArgs.size))
                ast.assignType(errors, ErrorType)
            }
        } else {
            val instantiation = symbol.instantiation.apply(
                ast.ctx,
                errors,
                args,
                symbol,
                symbol.identifier,
                listOf()
            )
            ast.symbolRef = instantiation
            val returnType = instantiation.substitutionChain.replay(symbol.returnType)
            ast.assignType(errors, returnType)
        }
    }

    private fun handleParamFunc(
        signifier: Signifier,
        ast: SymbolRefAst,
        symbol: ParameterizedFunctionSymbol,
        args: List<Ast>
    ) {
        if (signifier is ParameterizedSignifier) {
            val idArgs = signifier.args
            val idArgSymbols = idArgs.map { ast.scope.fetchType(it) }
            if (idArgs.size == symbol.typeParams.size) {
                val substitution = Substitution(symbol.typeParams, idArgSymbols)
                val instantiation = substitution.apply(symbol)
                ast.symbolRef = instantiation
                val returnType = instantiation.substitutionChain.replay(symbol.returnType)
                ast.assignType(errors, returnType)
            } else {
                errors.add(ast.ctx, IncorrectNumberOfTypeArgs(symbol.typeParams.size, idArgs.size))
                ast.assignType(errors, ErrorType)
            }
        } else {
            val instantiation = instantiateFunction(ast.ctx, args, symbol, errors)
            ast.symbolRef = instantiation
            val returnType = instantiation.substitutionChain.replay(symbol.returnType)
            ast.assignType(errors, returnType)
        }
    }

    private fun handleParamRecord(
        signifier: Signifier,
        ast: SymbolRefAst,
        type: ParameterizedRecordTypeSymbol,
        args: List<Ast>
    ) {
        if (signifier is ParameterizedSignifier) {
            val idArgs = signifier.args
            val idArgSymbols = idArgs.map { ast.scope.fetchType(it) }
            if (idArgs.size == type.typeParams.size) {
                val substitution = Substitution(type.typeParams, idArgSymbols)
                val instantiation = substitution.apply(type)
                ast.symbolRef = TypePlaceholder
                ast.typeRef = instantiation
                ast.assignType(errors, instantiation)
            } else {
                errors.add(ast.ctx, IncorrectNumberOfTypeArgs(type.typeParams.size, idArgs.size))
                ast.assignType(errors, ErrorType)
            }
        } else {
            val instantiation = instantiateRecord(ast.ctx, args, type, errors)
            ast.symbolRef = TypePlaceholder
            ast.typeRef = instantiation
            ast.assignType(errors, instantiation)
        }
    }

    private fun handleParamBasicType(
        signifier: Signifier,
        ast: SymbolRefAst,
        type: ParameterizedBasicTypeSymbol,
        args: List<Ast>
    ) {
        if (signifier is ParameterizedSignifier) {
            val idArgs = signifier.args
            val idArgSymbols = idArgs.map { ast.scope.fetchType(it) }
            if (idArgs.size == type.typeParams.size) {
                val instantiation = type.instantiation.apply(
                    ast.ctx,
                    errors,
                    args,
                    type,
                    type.identifier,
                    idArgSymbols
                )
                ast.symbolRef = TypePlaceholder
                ast.typeRef = instantiation
                ast.assignType(errors, instantiation)
            } else {
                errors.add(ast.ctx, IncorrectNumberOfTypeArgs(type.typeParams.size, idArgs.size))
                ast.assignType(errors, ErrorType)
            }
        } else {
            val instantiation = type.instantiation.apply(
                ast.ctx,
                errors,
                args,
                type,
                type.identifier,
                listOf()
            )
            ast.symbolRef = TypePlaceholder
            ast.typeRef = instantiation
            ast.assignType(errors, instantiation)
        }
    }

    override fun visit(ast: IntLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetchType(Lang.intId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: DecimalLiteralAst) {
        try {
            super.visit(ast)
            val parameterizedType = preludeTable.fetchType(Lang.decimalId) as ParameterizedBasicTypeSymbol
            ast.assignType(
                errors, parameterizedType.instantiation.apply(
                    ast.ctx,
                    errors,
                    listOf(ast),
                    parameterizedType,
                    parameterizedType.identifier,
                    listOf()
                )
            )
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: BooleanLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetchType(Lang.booleanId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: CharLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetchType(Lang.charId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: StringLiteralAst) {
        try {
            super.visit(ast)
            val parameterizedType = preludeTable.fetchType(Lang.stringId) as ParameterizedBasicTypeSymbol
            ast.assignType(
                errors, parameterizedType.instantiation.apply(
                    ast.ctx,
                    errors,
                    listOf(ast),
                    parameterizedType,
                    parameterizedType.identifier,
                    listOf()
                )
            )
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: StringInterpolationAst) {
        try {
            super.visit(ast)
            val parameterizedType = preludeTable.fetchType(Lang.stringId) as ParameterizedBasicTypeSymbol
            ast.assignType(
                errors, parameterizedType.instantiation.apply(
                    ast.ctx,
                    errors,
                    ast.components,
                    parameterizedType,
                    parameterizedType.identifier,
                    listOf()
                )
            )
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: LetAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
            if (ast.ofType is ImplicitTypeLiteral) {
                ast.ofTypeSymbol = ast.rhs.readType()
            } else {
                val ofType = ast.scope.fetchType(ast.ofType)
                ast.ofTypeSymbol = ofType
            }
            val local = LocalVariableSymbol(ast.scope, ast.identifier, ast.ofTypeSymbol, ast.mutable)
            ast.scope.define(ast.identifier, local)
            ast.symbolRef = local
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: RefAst) {
        try {
            super.visit(ast)
            val symbol = ast.scope.fetch(ast.identifier)
            if (symbol is TypePlaceholder) {
                when (val type = ast.scope.fetchType(ast.identifier)) {
                    is BasicTypeSymbol -> {
                        ast.refSlot = RefSlotBasic(type)
                        ast.assignType(errors, type)
                    }

                    is ObjectSymbol -> {
                        ast.refSlot = RefSlotObject(type)
                        ast.assignType(errors, type)
                    }

                    is PlatformObjectSymbol -> {
                        ast.refSlot = RefSlotPlatformObject(type)
                        ast.assignType(errors, type)
                    }

                    is StandardTypeParameter -> {
                        ast.refSlot = RefSlotSTP(type)
                        ast.assignType(errors, type)
                    }

                    is TypeInstantiation -> {
                        ast.refSlot = RefSlotTI(type)
                        ast.assignType(errors, type)
                    }

                    else -> {
                        errors.add(ast.ctx, InvalidRef(symbol))
                        ast.refSlot = RefSlotError
                        ast.assignType(errors, ErrorType)
                    }
                }
            } else {
                when (symbol) {
                    is ErrorSymbol -> {
                        ast.refSlot = RefSlotError
                        ast.assignType(errors, ErrorType)
                    }

                    is LocalVariableSymbol -> {
                        ast.refSlot = RefSlotLVS(symbol)
                        ast.assignType(errors, symbol.ofTypeSymbol)
                    }

                    is FunctionFormalParameterSymbol -> {
                        ast.refSlot = RefSlotFormal(symbol)
                        ast.assignType(errors, symbol.ofTypeSymbol)
                        if (ast.readType() is FunctionTypeSymbol) {
                            errors.add(ast.ctx, CannotRefFunctionParam(ast.identifier))
                        }
                    }

                    is FieldSymbol -> {
                        ast.refSlot = RefSlotField(symbol)
                        ast.assignType(errors, symbol.ofTypeSymbol)
                    }

                    else -> {
                        errors.add(ast.ctx, InvalidRef(symbol))
                        ast.refSlot = RefSlotError
                        ast.assignType(errors, ErrorType)
                    }
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.refSlot = RefSlotError
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: FileAst) {
        try {
            super.visit(ast)
            if (ast.lines.isEmpty()) {
                ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
            } else {
                ast.assignType(errors, ast.lines.last().readType())
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: BlockAst) {
        try {
            super.visit(ast)
            if (ast.lines.isEmpty()) {
                ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
            } else {
                ast.assignType(errors, ast.lines.last().readType())
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: FunctionAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
            if (ast.typeParams.isEmpty()) {
                val groundFunctionSymbol = ast.scope as GroundFunctionSymbol
                if (groundFunctionSymbol.returnType == Lang.unitObject && ast.body.readType() != Lang.unitObject) {
                    val refAst = RefAst(NotInSource, Lang.unitId)
                    refAst.scope = ast.body.scope
                    refAst.accept(this)
                    ast.body.lines.add(refAst)
                    ast.body.assignType(errors, preludeTable.fetchType(Lang.unitId))
                }
            } else {
                val parameterizedFunctionSymbol = ast.scope as ParameterizedFunctionSymbol
                if (parameterizedFunctionSymbol.returnType == Lang.unitObject && ast.body.readType() != Lang.unitObject) {
                    val refAst = RefAst(NotInSource, Lang.unitId)
                    refAst.scope = ast.body.scope
                    refAst.accept(this)
                    ast.body.lines.add(refAst)
                    ast.body.assignType(errors, preludeTable.fetchType(Lang.unitId))
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
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
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: RecordDefinitionAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
            ast.fields.forEach {
                it.symbol = ast.scope.fetch(it.ofType)
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: ObjectDefinitionAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: DotAst) {
        try {
            super.visit(ast)
            when (val lhsType = ast.lhs.readType()) {
                is ErrorType -> ast.assignType(errors, ErrorType)
                is GroundRecordTypeSymbol -> {
                    val symbol = lhsType.fetchHere(ast.identifier)
                    ast.symbolRef = symbol
                    when (symbol) {
                        is FieldSymbol -> ast.assignType(errors, symbol.ofTypeSymbol)
                        else -> {
                            errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is TypeInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.terminus) {
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
                                    ast.assignType(errors, ErrorType)
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
                                    ast.assignType(errors, ErrorType)

                                }
                            }
                        }
                    }
                }

                else -> {
                    errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                    ast.assignType(errors, ErrorType)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: GroundApplyAst) {
        try {
            super.visit(ast)
            ast.tti = when (ast.signifier) {
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
            if (symbol is TypePlaceholder) {
                val type = ast.scope.fetchType(ast.tti)
                filterValidGroundApply(ast.ctx, errors, type, ast.signifier)
                ast.symbolRef = TypePlaceholder
                ast.typeRef = type
            } else {
                filterValidGroundApply(ast.ctx, errors, symbol, ast.signifier)
                ast.symbolRef = symbol
            }
            groundApply(ast, ast.signifier, ast.args, ast.scope)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: DotApplyAst) {
        try {
            super.visit(ast)
            ast.tti = when (ast.signifier) {
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
                is ErrorType -> ast.assignType(errors, ErrorType)
                is BasicTypeSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    if (member is TypePlaceholder) {
                        val type = lhsType.fetchTypeHere(ast.tti)
                        filterValidGroundApply(ast.ctx, errors, type, ast.signifier)
                        ast.symbolRef = TypePlaceholder
                        ast.typeRef = type
                    } else {
                        filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    }
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
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is GroundRecordTypeSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    if (member is TypePlaceholder) {
                        val type = lhsType.fetchTypeHere(ast.tti)
                        filterValidGroundApply(ast.ctx, errors, type, ast.signifier)
                        ast.symbolRef = TypePlaceholder
                        ast.typeRef = type
                    } else {
                        filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    }
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
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is PlatformObjectSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    if (member is TypePlaceholder) {
                        val type = lhsType.fetchTypeHere(ast.tti)
                        filterValidGroundApply(ast.ctx, errors, type, ast.signifier)
                        ast.symbolRef = TypePlaceholder
                        ast.typeRef = type
                    } else {
                        filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    }
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
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is TypeInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.terminus) {
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
                                        member.identifier,
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
                                    ast.assignType(errors, ErrorType)
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                                    ast.assignType(errors, ErrorType)
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
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }
                    }
                }

                else -> {
                    errors.add(ast.ctx, SymbolHasNoMembers(ast.signifier, ast.lhs.readType() as Symbol))
                    ast.assignType(errors, ErrorType)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    // Do not call super for collection iterators
    override fun visit(ast: ForEachAst) {
        try {
            ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
            ast.source.accept(this)
            when (val sourceType = ast.source.readType()) {
                is TypeInstantiation -> {
                    when (val parameterizedSymbol = sourceType.substitutionChain.terminus) {
                        is ParameterizedBasicTypeSymbol -> {
                            if (parameterizedSymbol.featureSupport.forEachBlock) {
                                ast.sourceTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                ast.sourceFinSymbol = sourceType.substitutionChain.replayArgs()[1]
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                } else {
                                    val ofType = ast.scope.fetchType(ast.ofType)
                                    ast.ofTypeSymbol = ofType
                                }
                                ast.body.scope.defineType(ast.identifier, ast.ofTypeSymbol)
                                ast.body.accept(this)
                            } else {
                                errors.add(ast.source.ctx, ForEachFeatureBan(ast.source.readType()))
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
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: AssignAst) {
        try {
            super.visit(ast)
            val symbol = ast.scope.fetch(ast.identifier)
            ast.symbolRef = symbol
            when (symbol) {
                is LocalVariableSymbol -> ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
                else -> {
                    errors.add(ast.ctx, InvalidRef(symbol))
                    ast.assignType(errors, ErrorType)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: DotAssignAst) {
        try {
            super.visit(ast)
            when (val lhsType = ast.lhs.readType()) {
                is ErrorType -> ast.assignType(errors, ErrorType)
                is GroundRecordTypeSymbol -> {
                    val member = lhsType.fetchHere(ast.identifier)
                    ast.symbolRef = member
                    when (member) {
                        is FieldSymbol -> ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
                        else -> {
                            errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is TypeInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.terminus) {
                        is ParameterizedRecordTypeSymbol -> {
                            val substitution = lhsType.substitutionChain
                            when (val member = parameterizedSymbol.fetchHere(ast.identifier)) {
                                is FieldSymbol -> {
                                    ast.symbolRef = substitution.replay(member.ofTypeSymbol) as Symbol
                                    ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }

                        else -> {
                            errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                else -> {
                    errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                    ast.assignType(errors, ErrorType)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
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
            ast.assignType(errors, ErrorType)
        }
    }
}