package org.shardscript.semantics.visitors

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.infer.instantiateFunction
import org.shardscript.semantics.infer.instantiatePlatformSumRecord
import org.shardscript.semantics.infer.instantiateRecord
import org.shardscript.semantics.prelude.Lang

class PropagateTypesAstVisitor(
    private val preludeTable: Scope
) : UnitAstVisitor() {
    private fun groundApply(ast: GroundApplyAst, signifier: Signifier, args: List<Ast>, scope: Scope) {
        when (val symbol = scope.fetch(signifier)) {
            is ErrorSymbol -> {
                ast.groundApplySlot = GroundApplySlotError
                ast.assignType(errors, ErrorType)
            }

            is GroundFunctionSymbol -> {
                if (signifier is ParameterizedSignifier) {
                    errors.add(signifier.ctx, SymbolHasNoParameters(signifier))
                }
                ast.groundApplySlot = GroundApplySlotGF(symbol)
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
                    is FunctionType -> {
                        ast.groundApplySlot = GroundApplySlotFormal(symbol)
                        ast.assignType(errors, ofTypeSymbol.returnType)
                    }

                    else -> {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                        ast.groundApplySlot = GroundApplySlotError
                        ast.assignType(errors, ErrorType)
                    }
                }
            }

            is TypePlaceholder -> {
                when (val type = scope.fetchType(signifier)) {
                    is TypeInstantiation -> {
                        when (val terminus = type.substitutionChain.terminus) {
                            is ParameterizedBasicType -> handleParamBasicType(signifier, ast, terminus, args)
                            is ParameterizedRecordType -> handleParamRecord(signifier, ast, terminus, args)
                            is PlatformSumRecordType -> handlePlatformSumRecord(signifier, ast, terminus, args)
                            is PlatformSumType -> {
                                errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                                ast.groundApplySlot = GroundApplySlotError
                                ast.assignType(errors, ErrorType)
                            }
                        }
                    }

                    is GroundRecordType -> {
                        if (signifier is ParameterizedSignifier) {
                            errors.add(signifier.ctx, SymbolHasNoParameters(signifier))
                        }
                        ast.groundApplySlot = GroundApplySlotGRT(type)
                        ast.assignType(errors, type)
                    }

                    is ParameterizedRecordType -> {
                        handleParamRecord(signifier, ast, type, args)
                    }

                    is PlatformSumRecordType -> {
                        handlePlatformSumRecord(signifier, ast, type, args)
                    }

                    is ParameterizedBasicType -> {
                        handleParamBasicType(signifier, ast, type, args)
                    }

                    else -> {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                        ast.groundApplySlot = GroundApplySlotError
                        ast.assignType(errors, ErrorType)
                    }
                }
            }

            is ParameterizedStaticPluginSymbol -> {
                handleParamStatic(signifier, ast, symbol, args)
            }

            is SymbolInstantiation -> {
                when (val terminus = symbol.substitutionChain.terminus) {
                    is ParameterizedFunctionSymbol -> handleParamFunc(signifier, ast, terminus, args)
                    is ParameterizedStaticPluginSymbol -> handleParamStatic(signifier, ast, terminus, args)
                    is ParameterizedMemberPluginSymbol -> {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                        ast.groundApplySlot = GroundApplySlotError
                        ast.assignType(errors, ErrorType)
                    }
                }
            }

            else -> {
                errors.add(ast.ctx, SymbolCouldNotBeApplied(signifier))
                ast.groundApplySlot = GroundApplySlotError
                ast.assignType(errors, ErrorType)
            }
        }
    }

    private fun handleParamStatic(
        signifier: Signifier,
        ast: GroundApplyAst,
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
                ast.groundApplySlot = GroundApplySlotSI(instantiation)
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
            ast.groundApplySlot = GroundApplySlotSI(instantiation)
            val returnType = instantiation.substitutionChain.replay(symbol.returnType)
            ast.assignType(errors, returnType)
        }
    }

    private fun handleParamFunc(
        signifier: Signifier,
        ast: GroundApplyAst,
        symbol: ParameterizedFunctionSymbol,
        args: List<Ast>
    ) {
        if (signifier is ParameterizedSignifier) {
            val idArgs = signifier.args
            val idArgSymbols = idArgs.map { ast.scope.fetchType(it) }
            if (idArgs.size == symbol.typeParams.size) {
                val substitution = Substitution(symbol.typeParams, idArgSymbols)
                val instantiation = substitution.apply(symbol)
                ast.groundApplySlot = GroundApplySlotSI(instantiation)
                val returnType = instantiation.substitutionChain.replay(symbol.returnType)
                ast.assignType(errors, returnType)
            } else {
                errors.add(ast.ctx, IncorrectNumberOfTypeArgs(symbol.typeParams.size, idArgs.size))
                ast.assignType(errors, ErrorType)
            }
        } else {
            val instantiation = instantiateFunction(ast.ctx, args, symbol, errors)
            ast.groundApplySlot = GroundApplySlotSI(instantiation)
            val returnType = instantiation.substitutionChain.replay(symbol.returnType)
            ast.assignType(errors, returnType)
        }
    }

    private fun handleParamRecord(
        signifier: Signifier,
        ast: GroundApplyAst,
        type: ParameterizedRecordType,
        args: List<Ast>
    ) {
        if (signifier is ParameterizedSignifier) {
            val idArgs = signifier.args
            val idArgSymbols = idArgs.map { ast.scope.fetchType(it) }
            if (idArgs.size == type.typeParams.size) {
                val substitution = Substitution(type.typeParams, idArgSymbols)
                val instantiation = substitution.apply(type)
                ast.groundApplySlot = GroundApplySlotTI(instantiation)
                ast.assignType(errors, instantiation)
            } else {
                errors.add(ast.ctx, IncorrectNumberOfTypeArgs(type.typeParams.size, idArgs.size))
                ast.assignType(errors, ErrorType)
            }
        } else {
            val instantiation = instantiateRecord(ast.ctx, args, type, errors)
            ast.groundApplySlot = GroundApplySlotTI(instantiation)
            ast.assignType(errors, instantiation)
        }
    }

    private fun handlePlatformSumRecord(
        signifier: Signifier,
        ast: GroundApplyAst,
        type: PlatformSumRecordType,
        args: List<Ast>
    ) {
        if (signifier is ParameterizedSignifier) {
            val idArgs = signifier.args
            val idArgSymbols = idArgs.map { ast.scope.fetchType(it) }
            if (idArgs.size == type.typeParams.size) {
                val substitution = Substitution(type.typeParams, idArgSymbols)
                val instantiation = substitution.apply(type)
                ast.groundApplySlot = GroundApplySlotTI(instantiation)
                ast.assignType(errors, instantiation)
            } else {
                errors.add(ast.ctx, IncorrectNumberOfTypeArgs(type.typeParams.size, idArgs.size))
                ast.assignType(errors, ErrorType)
            }
        } else {
            val instantiation = instantiatePlatformSumRecord(ast.ctx, args, type, errors)
            ast.groundApplySlot = GroundApplySlotTI(instantiation)
            ast.assignType(errors, instantiation)
        }
    }

    private fun handleParamBasicType(
        signifier: Signifier,
        ast: GroundApplyAst,
        type: ParameterizedBasicType,
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
                ast.groundApplySlot = GroundApplySlotTI(instantiation)
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
            ast.groundApplySlot = GroundApplySlotTI(instantiation)
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
            val parameterizedType = preludeTable.fetchType(Lang.decimalId) as ParameterizedBasicType
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
            val parameterizedType = preludeTable.fetchType(Lang.stringId) as ParameterizedBasicType
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
            val parameterizedType = preludeTable.fetchType(Lang.stringId) as ParameterizedBasicType
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
                    is ObjectType -> {
                        ast.refSlot = RefSlotObject(type)
                        ast.assignType(errors, type)
                    }

                    is PlatformObjectType -> {
                        ast.refSlot = RefSlotPlatformObject(type)
                        ast.assignType(errors, type)
                    }

                    is PlatformSumObjectType -> {
                        ast.refSlot = RefSlotSumObject(type)
                        ast.assignType(errors, type)
                    }

                    is StandardTypeParameter -> {
                        ast.refSlot = RefSlotSTP(type)
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
                        if (ast.readType() is FunctionType) {
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
                is ErrorType -> {
                    ast.dotSlot = DotSlotError
                    ast.assignType(errors, ErrorType)
                }
                is GroundRecordType -> {
                    when (val symbol = lhsType.fetchHere(ast.identifier)) {
                        is FieldSymbol -> {
                            ast.dotSlot = DotSlotField(symbol)
                            ast.assignType(errors, symbol.ofTypeSymbol)
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                            ast.dotSlot = DotSlotError
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is TypeInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.terminus) {
                        is ParameterizedRecordType -> {
                            when (val member = parameterizedSymbol.fetchHere(ast.identifier)) {
                                is FieldSymbol -> {
                                    ast.dotSlot = DotSlotField(member)
                                    val astType = lhsType.substitutionChain.replay(member.ofTypeSymbol)
                                    ast.assignType(errors, astType)
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                                    ast.dotSlot = DotSlotError
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }

                        is ParameterizedBasicType -> {
                            when (val member = parameterizedSymbol.fetchHere(ast.identifier)) {
                                is PlatformFieldSymbol -> {
                                    ast.dotSlot = DotSlotPlatformField(member)
                                    val astType = lhsType.substitutionChain.replay(member.ofTypeSymbol)
                                    ast.assignType(errors, astType)
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                                    ast.dotSlot = DotSlotError
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }

                        is PlatformSumRecordType -> {
                            when (val member = parameterizedSymbol.fetchHere(ast.identifier)) {
                                is FieldSymbol -> {
                                    ast.dotSlot = DotSlotField(member)
                                    val astType = lhsType.substitutionChain.replay(member.ofTypeSymbol)
                                    ast.assignType(errors, astType)
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                                    ast.dotSlot = DotSlotError
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }
                        is PlatformSumType -> {
                            errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                            ast.dotSlot = DotSlotError
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                else -> {
                    errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                    ast.dotSlot = DotSlotError
                    ast.assignType(errors, ErrorType)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.dotSlot = DotSlotError
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
            } else {
                filterValidGroundApply(ast.ctx, errors, symbol, ast.signifier)
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
                is ErrorType -> {
                    ast.dotApplySlot = DotApplySlotError
                    ast.assignType(errors, ErrorType)
                }

                is BasicType -> {
                    val member = lhsType.fetchHere(ast.tti)
                    if (member is TypePlaceholder) {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                        ast.dotApplySlot = DotApplySlotError
                        ast.assignType(errors, ErrorType)
                    } else {
                        filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    }
                    when (member) {
                        is GroundFunctionSymbol -> {
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                            }
                            ast.dotApplySlot = DotApplySlotGF(member)
                            ast.assignType(errors, member.returnType)
                        }

                        is GroundMemberPluginSymbol -> {
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                            }
                            ast.dotApplySlot = DotApplySlotGMP(member)
                            ast.assignType(errors, member.returnType)
                        }

                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                            ast.dotApplySlot = DotApplySlotError
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is GroundRecordType -> {
                    val member = lhsType.fetchHere(ast.tti)
                    if (member is TypePlaceholder) {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                        ast.dotApplySlot = DotApplySlotError
                        ast.assignType(errors, ErrorType)
                    } else {
                        filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    }
                    when (member) {
                        is GroundMemberPluginSymbol -> {
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                            }
                            ast.dotApplySlot = DotApplySlotGMP(member)
                            ast.assignType(errors, member.returnType)
                        }

                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                            ast.dotApplySlot = DotApplySlotError
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is PlatformObjectType -> {
                    val member = lhsType.fetchHere(ast.tti)
                    if (member is TypePlaceholder) {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                        ast.dotApplySlot = DotApplySlotError
                        ast.assignType(errors, ErrorType)
                    } else {
                        filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                    }
                    when (member) {
                        is GroundMemberPluginSymbol -> {
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                            }
                            ast.dotApplySlot = DotApplySlotGMP(member)
                            ast.assignType(errors, member.returnType)
                        }

                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                            ast.dotApplySlot = DotApplySlotError
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is TypeInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.terminus) {
                        is ParameterizedBasicType -> {
                            val member = parameterizedSymbol.fetchHere(ast.tti)
                            if (ast.signifier is ParameterizedSignifier) {
                                errors.add(ast.ctx, CannotExplicitlyInstantiate(member))
                            }
                            when (member) {
                                is GroundMemberPluginSymbol -> {
                                    if (ast.signifier is ParameterizedSignifier) {
                                        errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                                    }
                                    filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                                    ast.dotApplySlot = DotApplySlotGMP(member)
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
                                    ast.dotApplySlot = DotApplySlotSI(instantiation)
                                    filterValidDotApply(ast.ctx, errors, instantiation, ast.signifier)
                                    val returnType =
                                        instantiation.substitutionChain.replay(member.returnType)
                                    ast.assignType(errors, returnType)
                                }

                                is ParameterizedStaticPluginSymbol -> {
                                    errors.add(ast.ctx, TypeSystemBug)
                                    ast.dotApplySlot = DotApplySlotError
                                    ast.assignType(errors, ErrorType)
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                                    ast.dotApplySlot = DotApplySlotError
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }

                        is ParameterizedRecordType -> {
                            val member = parameterizedSymbol.fetchHere(ast.tti)
                            filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                            when (member) {
                                is GroundMemberPluginSymbol -> {
                                    if (ast.signifier is ParameterizedSignifier) {
                                        errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                                    }
                                    ast.dotApplySlot = DotApplySlotGMP(member)
                                    ast.assignType(errors, member.returnType)
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                                    ast.dotApplySlot = DotApplySlotError
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }

                        is PlatformSumRecordType -> {
                            val member = parameterizedSymbol.fetchHere(ast.tti)
                            filterValidDotApply(ast.ctx, errors, member, ast.signifier)
                            when (member) {
                                is GroundMemberPluginSymbol -> {
                                    if (ast.signifier is ParameterizedSignifier) {
                                        errors.add(ast.signifier.ctx, SymbolHasNoParameters(ast.signifier))
                                    }
                                    ast.dotApplySlot = DotApplySlotGMP(member)
                                    ast.assignType(errors, member.returnType)
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.signifier))
                                    ast.dotApplySlot = DotApplySlotError
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }
                        is PlatformSumType -> {
                            errors.add(ast.ctx, SymbolHasNoMembers(ast.signifier, ast.lhs.readType() as Symbol))
                            ast.dotApplySlot = DotApplySlotError
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                else -> {
                    errors.add(ast.ctx, SymbolHasNoMembers(ast.signifier, ast.lhs.readType() as Symbol))
                    ast.dotApplySlot = DotApplySlotError
                    ast.assignType(errors, ErrorType)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.dotApplySlot = DotApplySlotError
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
                        is ParameterizedBasicType -> {
                            if (parameterizedSymbol.featureSupport.forEachBlock) {
                                ast.sourceTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                ast.sourceFinSymbol = sourceType.substitutionChain.replayArgs()[1]
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                } else {
                                    val ofType = ast.scope.fetchType(ast.ofType)
                                    ast.ofTypeSymbol = ofType
                                }
                                val lvs = LocalVariableSymbol(ast.body.scope, ast.identifier, ast.ofTypeSymbol, false)
                                ast.body.scope.define(ast.identifier, lvs)
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
            when (val symbol = ast.scope.fetch(ast.identifier)) {
                is LocalVariableSymbol -> {
                    ast.assignSlot = AssignSlotLVS(symbol)
                    ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
                }
                else -> {
                    errors.add(ast.ctx, InvalidRef(symbol))
                    ast.assignSlot = AssignSlotError
                    ast.assignType(errors, ErrorType)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignSlot = AssignSlotError
            ast.assignType(errors, ErrorType)
        }
    }

    override fun visit(ast: DotAssignAst) {
        try {
            super.visit(ast)
            when (val lhsType = ast.lhs.readType()) {
                is ErrorType -> {
                    ast.dotAssignSlot = DotAssignSlotError
                    ast.assignType(errors, ErrorType)
                }
                is GroundRecordType -> {
                    when (val member = lhsType.fetchHere(ast.identifier)) {
                        is FieldSymbol -> {
                            ast.dotAssignSlot = DotAssignSlotField(member)
                            ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
                        }

                        else -> {
                            errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                            ast.dotAssignSlot = DotAssignSlotError
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                is TypeInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.terminus) {
                        is ParameterizedRecordType -> {
                            when (val member = parameterizedSymbol.fetchHere(ast.identifier)) {
                                is FieldSymbol -> {
                                    ast.dotAssignSlot = DotAssignSlotField(member)
                                    ast.assignType(errors, preludeTable.fetchType(Lang.unitId))
                                }

                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.identifier))
                                    ast.dotAssignSlot = DotAssignSlotError
                                    ast.assignType(errors, ErrorType)
                                }
                            }
                        }

                        else -> {
                            errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                            ast.dotAssignSlot = DotAssignSlotError
                            ast.assignType(errors, ErrorType)
                        }
                    }
                }

                else -> {
                    errors.add(ast.ctx, SymbolHasNoFields(ast.identifier, ast.lhs.readType() as Symbol))
                    ast.dotAssignSlot = DotAssignSlotError
                    ast.assignType(errors, ErrorType)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.dotAssignSlot = DotAssignSlotError
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

    override fun visit(ast: MatchAst) {
        try {
            ast.condition.accept(this)
            when (val conditionType = ast.condition.readType()) {
                is TypeInstantiation -> {
                    when (val terminus = conditionType.substitutionChain.terminus) {
                        is PlatformSumType -> {
                            val nameSet: MutableMap<String, CaseBlock> = mutableMapOf()
                            ast.cases.forEach {
                                if (nameSet.containsKey(it.identifier.name)) {
                                    errors.add(ast.condition.ctx, DuplicateCaseDetected(it.identifier.name))
                                }
                                nameSet[it.identifier.name] = it
                            }
                            terminus.memberTypes.forEach {
                                when (it) {
                                    is PlatformSumObjectType -> {
                                        if (!nameSet.containsKey(it.identifier.name)) {
                                            errors.add(ast.condition.ctx, MissingMatchCase(it.identifier.name))
                                        } else {
                                            nameSet[it.identifier.name]!!.member = it
                                            nameSet[it.identifier.name]!!.itType = it
                                        }
                                    }

                                    is PlatformSumRecordType -> {
                                        if (!nameSet.containsKey(it.identifier.name)) {
                                            errors.add(ast.condition.ctx, MissingMatchCase(it.identifier.name))
                                        } else {
                                            nameSet[it.identifier.name]!!.member = it
                                            nameSet[it.identifier.name]!!.itType =
                                                conditionType.substitutionChain.replay(it)
                                        }
                                    }
                                }
                            }
                        }

                        else -> errors.add(ast.condition.ctx, SumTypeRequired(conditionType))
                    }
                }

                else -> errors.add(ast.condition.ctx, SumTypeRequired(conditionType))
            }

            ast.cases.forEach {
                val local = LocalVariableSymbol(it.block.scope, Lang.itId, it.itType, false)
                it.block.scope.define(Lang.itId, local)
                it.block.accept(this)
            }

            ast.assignType(
                errors,
                findBestType(
                    ast.ctx,
                    errors,
                    ast.cases.map { it.block.readType() }
                )
            )
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorType)
        }
    }
}