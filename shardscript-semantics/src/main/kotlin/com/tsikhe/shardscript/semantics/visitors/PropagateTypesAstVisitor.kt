package com.tsikhe.shardscript.semantics.visitors

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.infer.Substitution
import com.tsikhe.shardscript.semantics.infer.instantiateFunction
import com.tsikhe.shardscript.semantics.infer.instantiateRecord
import com.tsikhe.shardscript.semantics.prelude.Lang

internal class PropagateTypesAstVisitor(
    private val architecture: Architecture,
    private val preludeTable: PreludeTable
) : UnitAstVisitor() {
    private fun groundApply(ast: SymbolRefAst, identifier: Identifier, args: List<Ast>, symbol: Symbol) {
        when (symbol) {
            is ErrorSymbol -> ast.assignType(errors, ErrorSymbol)
            is GroundFunctionSymbol -> {
                if (identifier is ParameterizedIdentifier) {
                    errors.add(identifier.ctx, SymbolHasNoParameters(identifier))
                }
                ast.assignType(errors, symbol.returnType)
            }
            is ParameterizedFunctionSymbol -> {
                if (identifier is ParameterizedIdentifier) {
                    val idArgs = identifier.args
                    val idArgSymbols = idArgs.map { ast.scope.fetch(it) }
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
                if (identifier is ParameterizedIdentifier) {
                    errors.add(identifier.ctx, SymbolHasNoParameters(identifier))
                }
                when (val ofTypeSymbol = symbol.ofTypeSymbol) {
                    is GroundFunctionSymbol -> ast.assignType(errors, ofTypeSymbol.returnType)
                    is FunctionTypeSymbol -> ast.assignType(errors, ofTypeSymbol.returnType)
                    else -> {
                        errors.add(ast.ctx, SymbolCouldNotBeApplied(identifier))
                        ast.assignType(errors, ErrorSymbol)
                    }
                }
            }
            is GroundRecordTypeSymbol -> {
                if (identifier is ParameterizedIdentifier) {
                    errors.add(identifier.ctx, SymbolHasNoParameters(identifier))
                }
                ast.symbolRef = symbol
                ast.assignType(errors, symbol)
            }
            is ParameterizedRecordTypeSymbol -> {
                if (identifier is ParameterizedIdentifier) {
                    val idArgs = identifier.args
                    val idArgSymbols = idArgs.map { ast.scope.fetch(it) }
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
                if (identifier is ParameterizedIdentifier) {
                    val idArgs = identifier.args
                    val idArgSymbols = idArgs.map { ast.scope.fetch(it) }
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
                if (identifier is ParameterizedIdentifier) {
                    val idArgs = identifier.args
                    val idArgSymbols = idArgs.map { ast.scope.fetch(it) }
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
                errors.add(ast.ctx, SymbolCouldNotBeApplied(identifier))
                ast.assignType(errors, ErrorSymbol)
            }
        }
    }

    override fun visit(ast: SByteLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.sByteId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: ShortLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.shortId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: IntLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.intId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: LongLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.longId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: ByteLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.byteId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: UShortLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.uShortId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: UIntLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.uIntId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: ULongLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.uLongId))
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
            ast.assignType(errors, preludeTable.fetch(Lang.booleanId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: CharLiteralAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.charId))
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
            ast.assignType(errors, preludeTable.fetch(Lang.unitId))
            if (ast.ofType is ImplicitTypeLiteral) {
                ast.ofTypeSymbol = ast.rhs.readType()
            } else {
                validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                ast.ofTypeSymbol = ast.scope.fetch(ast.ofType)
            }
            val local = LocalVariableSymbol(ast.scope, ast.gid, ast.ofTypeSymbol, ast.mutable)
            ast.scope.define(ast.gid, local)
            ast.symbolRef = local
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: RefAst) {
        try {
            super.visit(ast)
            val symbol = ast.scope.fetch(ast.gid)
            ast.symbolRef = symbol
            when (symbol) {
                is ErrorSymbol -> ast.assignType(errors, ErrorSymbol)
                is Namespace -> ast.assignType(errors, symbol)
                is BasicTypeSymbol -> ast.assignType(errors, symbol)
                is LocalVariableSymbol -> ast.assignType(errors, symbol.ofTypeSymbol)
                is ObjectSymbol -> ast.assignType(errors, symbol)
                is GroundFunctionSymbol -> ast.assignType(errors, symbol.type())
                is FunctionFormalParameterSymbol -> ast.assignType(errors, symbol.ofTypeSymbol)
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
                ast.assignType(errors, preludeTable.fetch(Lang.unitId))
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
                ast.assignType(errors, preludeTable.fetch(Lang.unitId))
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
            ast.assignType(errors, preludeTable.fetch(Lang.unitId))
            if (ast.typeParams.isEmpty()) {
                val groundFunctionSymbol = ast.scope as GroundFunctionSymbol
                if (Lang.isUnitExactly(groundFunctionSymbol.returnType) && !Lang.isUnitExactly(ast.body.readType())) {
                    val refAst = RefAst(Lang.unitId)
                    refAst.scope = ast.body.scope
                    refAst.accept(this)
                    ast.body.lines.add(refAst)
                    ast.body.assignType(errors, preludeTable.fetch(Lang.unitId))
                }
            } else {
                val parameterizedFunctionSymbol = ast.scope as ParameterizedFunctionSymbol
                if (Lang.isUnitExactly(parameterizedFunctionSymbol.returnType) && !Lang.isUnitExactly(ast.body.readType())) {
                    val refAst = RefAst(Lang.unitId)
                    refAst.scope = ast.body.scope
                    refAst.accept(this)
                    ast.body.lines.add(refAst)
                    ast.body.assignType(errors, preludeTable.fetch(Lang.unitId))
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: RecordDefinitionAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.unitId))
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
            ast.assignType(errors, preludeTable.fetch(Lang.unitId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: EnumDefinitionAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, preludeTable.fetch(Lang.unitId))
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
                    val symbol = lhsType.fetchHere(ast.gid)
                    ast.symbolRef = symbol
                    when (symbol) {
                        is FieldSymbol -> ast.assignType(errors, symbol.ofTypeSymbol)
                        else -> {
                            errors.add(ast.ctx, SymbolIsNotAField(ast.gid))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.originalSymbol) {
                        is ParameterizedRecordTypeSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.gid)
                            ast.symbolRef = member
                            when (member) {
                                is FieldSymbol -> {
                                    val astType = lhsType.substitutionChain.replay(member.ofTypeSymbol)
                                    ast.assignType(errors, astType)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.gid))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        is ParameterizedBasicTypeSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.gid)
                            ast.symbolRef = member
                            when (member) {
                                is PlatformFieldSymbol -> {
                                    val astType = lhsType.substitutionChain.replay(member.ofTypeSymbol)
                                    ast.assignType(errors, astType)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.gid))
                                    ast.assignType(errors, ErrorSymbol)

                                }
                            }
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolHasNoFields(ast.gid, lhsType))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is Namespace -> {
                    val symbol = lhsType.fetchHere(ast.gid)
                    ast.symbolRef = symbol
                    when (symbol) {
                        is Namespace -> ast.assignType(errors, symbol)
                        is GroundFunctionSymbol -> ast.assignType(errors, symbol.type())
                        else -> {
                            errors.add(ast.ctx, InvalidNamespaceDot(ast.gid))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                else -> {
                    errors.add(ast.ctx, SymbolHasNoFields(ast.gid, ast.lhs.readType()))
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
            ast.tti = if (ast.identifier is ParameterizedIdentifier) {
                ast.identifier.tti
            } else {
                ast.identifier as GroundIdentifier
            }
            val symbol = ast.scope.fetch(ast.tti)
            filterValidGroundApply(ast.ctx, errors, symbol, ast.identifier)
            ast.symbolRef = symbol
            groundApply(ast, ast.identifier, ast.args, symbol)
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: DotApplyAst) {
        try {
            super.visit(ast)
            ast.tti = if (ast.identifier is ParameterizedIdentifier) {
                ast.identifier.tti
            } else {
                ast.identifier as GroundIdentifier
            }
            when (val lhsType = ast.lhs.readType()) {
                is ErrorSymbol -> ast.assignType(errors, ErrorSymbol)
                is Namespace -> {
                    val member = lhsType.fetchHere(ast.tti)
                    filterValidDotApply(ast.ctx, errors, member, ast.identifier)
                    ast.symbolRef = member
                    groundApply(ast, ast.identifier, ast.args, member)
                }
                is BasicTypeSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    filterValidDotApply(ast.ctx, errors, member, ast.identifier)
                    ast.symbolRef = member
                    when (member) {
                        is GroundFunctionSymbol -> {
                            if (ast.identifier is ParameterizedIdentifier) {
                                errors.add(ast.identifier.ctx, SymbolHasNoParameters(ast.identifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        is GroundMemberPluginSymbol -> {
                            if (ast.identifier is ParameterizedIdentifier) {
                                errors.add(ast.identifier.ctx, SymbolHasNoParameters(ast.identifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.identifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is GroundCoproductSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    filterValidDotApply(ast.ctx, errors, member, ast.identifier)
                    ast.symbolRef = member
                    when (member) {
                        is GroundMemberPluginSymbol -> {
                            if (ast.identifier is ParameterizedIdentifier) {
                                errors.add(ast.identifier.ctx, SymbolHasNoParameters(ast.identifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.identifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is GroundRecordTypeSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    filterValidDotApply(ast.ctx, errors, member, ast.identifier)
                    ast.symbolRef = member
                    when (member) {
                        is GroundMemberPluginSymbol -> {
                            if (ast.identifier is ParameterizedIdentifier) {
                                errors.add(ast.identifier.ctx, SymbolHasNoParameters(ast.identifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.identifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is ObjectSymbol -> {
                    val member = lhsType.fetchHere(ast.tti)
                    filterValidDotApply(ast.ctx, errors, member, ast.identifier)
                    ast.symbolRef = member
                    when (member) {
                        is GroundMemberPluginSymbol -> {
                            if (ast.identifier is ParameterizedIdentifier) {
                                errors.add(ast.identifier.ctx, SymbolHasNoParameters(ast.identifier))
                            }
                            ast.assignType(errors, member.returnType)
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.identifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.originalSymbol) {
                        is ParameterizedBasicTypeSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.tti)
                            if (ast.identifier is ParameterizedIdentifier) {
                                errors.add(ast.ctx, CannotExplicitlyInstantiate(member))
                            }
                            when (member) {
                                is GroundMemberPluginSymbol -> {
                                    if (ast.identifier is ParameterizedIdentifier) {
                                        errors.add(ast.identifier.ctx, SymbolHasNoParameters(ast.identifier))
                                    }
                                    ast.symbolRef = member
                                    filterValidDotApply(ast.ctx, errors, member, ast.identifier)
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
                                    filterValidDotApply(ast.ctx, errors, instantiation, ast.identifier)
                                    val returnType =
                                        instantiation.substitutionChain.replay(member.returnType)
                                    ast.assignType(errors, returnType)
                                }
                                is ParameterizedStaticPluginSymbol -> {
                                    errors.add(ast.ctx, TypeSystemBug)
                                    ast.assignType(errors, ErrorSymbol)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.identifier))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        is ParameterizedRecordTypeSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.tti)
                            filterValidDotApply(ast.ctx, errors, member, ast.identifier)
                            ast.symbolRef = member
                            when (member) {
                                is GroundMemberPluginSymbol -> {
                                    if (ast.identifier is ParameterizedIdentifier) {
                                        errors.add(ast.identifier.ctx, SymbolHasNoParameters(ast.identifier))
                                    }
                                    ast.assignType(errors, member.returnType)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.identifier))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        is ParameterizedCoproductSymbol -> {
                            val member = parameterizedSymbol.fetchHere(ast.tti)
                            filterValidDotApply(ast.ctx, errors, member, ast.identifier)
                            ast.symbolRef = member
                            when (member) {
                                is GroundMemberPluginSymbol -> {
                                    if (ast.identifier is ParameterizedIdentifier) {
                                        errors.add(ast.identifier.ctx, SymbolHasNoParameters(ast.identifier))
                                    }
                                    ast.assignType(errors, member.returnType)
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.identifier))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolCouldNotBeApplied(ast.identifier))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                else -> {
                    errors.add(ast.ctx, SymbolHasNoMembers(ast.identifier, ast.lhs.readType()))
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
            ast.assignType(errors, preludeTable.fetch(Lang.unitId))
            ast.source.accept(this)
            when (val sourceType = ast.source.readType()) {
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = sourceType.substitutionChain.originalSymbol) {
                        is ParameterizedBasicTypeSymbol -> {
                            if (parameterizedSymbol.featureSupport.forEachBlock) {
                                ast.sourceTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                ast.sourceOmicronSymbol = sourceType.substitutionChain.replayArgs()[1]
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                } else {
                                    validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                                    ast.ofTypeSymbol = ast.scope.fetch(ast.ofType)
                                }
                                ast.body.scope.define(ast.gid, ast.ofTypeSymbol)
                                ast.body.accept(this)
                            } else {
                                errors.add(ast.source.ctx, ForEachFeatureBan(ast.source.readType()))
                            }
                        }
                        is ParameterizedCoproductSymbol -> {
                            if (parameterizedSymbol.featureSupport.forEachBlock) {
                                ast.sourceTypeSymbol =
                                    sourceType.substitutionChain.replay(parameterizedSymbol.sourceType!!)
                                ast.sourceOmicronSymbol = OmicronTypeSymbol(architecture.defaultNodeCost)
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = ast.sourceTypeSymbol
                                } else {
                                    validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                                    ast.ofTypeSymbol = ast.scope.fetch(ast.ofType)
                                }
                                ast.body.scope.define(ast.gid, ast.ofTypeSymbol)
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
            ast.assignType(errors, ErrorSymbol)
        }
    }

    // Do not call super for collection iterators
    override fun visit(ast: MapAst) {
        try {
            ast.source.accept(this)
            when (val sourceType = ast.source.readType()) {
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = sourceType.substitutionChain.originalSymbol) {
                        is ParameterizedBasicTypeSymbol -> {
                            if (parameterizedSymbol.featureSupport.mapBlock) {
                                ast.sourceTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                ast.sourceOmicronSymbol = sourceType.substitutionChain.replayArgs()[1]
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                } else {
                                    validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                                    ast.ofTypeSymbol = ast.scope.fetch(ast.ofType)
                                }
                                ast.body.scope.define(ast.gid, ast.ofTypeSymbol)
                                ast.body.accept(this)
                                ast.bodyTypeSymbol = ast.body.readType()
                                val typeArgs = listOf(ast.bodyTypeSymbol, sourceType.substitutionChain.replayArgs()[1])
                                val bodySubstitution = Substitution(parameterizedSymbol.typeParams, typeArgs)
                                ast.assignType(errors, bodySubstitution.apply(parameterizedSymbol))
                            } else {
                                errors.add(ast.source.ctx, MapFeatureBan(ast.source.readType()))
                                ast.assignType(errors, ErrorSymbol)
                            }
                        }
                        is ParameterizedCoproductSymbol -> {
                            if (parameterizedSymbol.featureSupport.mapBlock) {
                                val sourceSubstitution = sourceType.substitutionChain
                                ast.sourceTypeSymbol =
                                    sourceSubstitution.replay(parameterizedSymbol.sourceType!!)
                                ast.sourceOmicronSymbol = OmicronTypeSymbol(architecture.defaultNodeCost)
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = ast.sourceTypeSymbol
                                } else {
                                    validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                                    ast.ofTypeSymbol = ast.scope.fetch(ast.ofType)
                                }
                                ast.body.scope.define(ast.gid, ast.ofTypeSymbol)
                                ast.body.accept(this)
                                ast.bodyTypeSymbol = ast.body.readType()
                                val typeArgs = parameterizedSymbol.replaceParameters(
                                    ast.bodyTypeSymbol,
                                    sourceSubstitution.replayArgs()
                                )
                                val bodySubstitution = Substitution(parameterizedSymbol.typeParams, typeArgs)
                                ast.assignType(errors, bodySubstitution.apply(parameterizedSymbol))
                            } else {
                                errors.add(ast.source.ctx, MapFeatureBan(ast.source.readType()))
                                ast.assignType(errors, ErrorSymbol)
                            }
                        }
                        else -> {
                            errors.add(ast.source.ctx, InvalidSource(ast.source.readType()))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                else -> {
                    errors.add(ast.source.ctx, InvalidSource(ast.source.readType()))
                    ast.assignType(errors, ErrorSymbol)
                }
            }
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    // Do not call super for collection iterators
    override fun visit(ast: FlatMapAst) {
        try {
            ast.source.accept(this)
            when (val sourceType = ast.source.readType()) {
                is SymbolInstantiation -> {
                    when (val parameterizedSourceSymbol = sourceType.substitutionChain.originalSymbol) {
                        is ParameterizedBasicTypeSymbol -> {
                            if (parameterizedSourceSymbol.featureSupport.flatMapBlock) {
                                ast.sourceTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                ast.sourceOmicronSymbol = sourceType.substitutionChain.replayArgs()[1]
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = sourceType.substitutionChain.replayArgs().first()
                                } else {
                                    validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                                    ast.ofTypeSymbol = ast.scope.fetch(ast.ofType)
                                }
                                ast.body.scope.define(ast.gid, ast.ofTypeSymbol)
                                ast.body.accept(this)
                                when (val bodyTypeSymbol = ast.body.readType()) {
                                    is SymbolInstantiation -> {
                                        when (val parameterizedBodySymbol =
                                            bodyTypeSymbol.substitutionChain.originalSymbol) {
                                            is ParameterizedBasicTypeSymbol -> {
                                                if (generatePath(parameterizedBodySymbol) == generatePath(
                                                        parameterizedSourceSymbol
                                                    )
                                                ) {
                                                    ast.bodyTypeSymbol = bodyTypeSymbol
                                                    val lhs: CostExpression = when (val sourceTypeArg =
                                                        sourceType.substitutionChain.replayArgs()[1]) {
                                                        is ImmutableOmicronTypeParameter -> {
                                                            sourceTypeArg
                                                        }
                                                        is OmicronTypeSymbol -> {
                                                            sourceTypeArg
                                                        }
                                                        else -> {
                                                            errors.add(ast.ctx, TypeSystemBug)
                                                            OmicronTypeSymbol(architecture.defaultNodeCost)
                                                        }
                                                    }
                                                    val rhs: CostExpression = when (val bodyTypeSymbolArg =
                                                        bodyTypeSymbol.substitutionChain.replayArgs()[1]) {
                                                        is ImmutableOmicronTypeParameter -> {
                                                            bodyTypeSymbolArg
                                                        }
                                                        is OmicronTypeSymbol -> {
                                                            bodyTypeSymbolArg
                                                        }
                                                        else -> {
                                                            errors.add(ast.ctx, TypeSystemBug)
                                                            OmicronTypeSymbol(architecture.defaultNodeCost)
                                                        }
                                                    }
                                                    val cost = ProductCostExpression(
                                                        listOf(
                                                            lhs,
                                                            rhs
                                                        )
                                                    )
                                                    val typeArgs =
                                                        listOf(
                                                            bodyTypeSymbol.substitutionChain.replayArgs().first(),
                                                            cost
                                                        )
                                                    val substitution = Substitution(
                                                        parameterizedBodySymbol.typeParams,
                                                        typeArgs
                                                    )
                                                    ast.assignType(errors, substitution.apply(parameterizedBodySymbol))
                                                } else {
                                                    errors.add(ast.source.ctx, InvalidBodyType(bodyTypeSymbol))
                                                    ast.assignType(errors, ErrorSymbol)
                                                }
                                            }
                                            else -> {
                                                errors.add(ast.source.ctx, InvalidBodyType(bodyTypeSymbol))
                                                ast.assignType(errors, ErrorSymbol)
                                            }
                                        }
                                    }
                                    else -> {
                                        errors.add(ast.source.ctx, InvalidBodyType(bodyTypeSymbol))
                                        ast.assignType(errors, ErrorSymbol)
                                    }
                                }
                            } else {
                                errors.add(ast.source.ctx, FlatMapFeatureBan(ast.source.readType()))
                                ast.assignType(errors, ErrorSymbol)
                            }
                        }
                        is ParameterizedCoproductSymbol -> {
                            if (parameterizedSourceSymbol.featureSupport.flatMapBlock) {
                                val sourceSubstitution = sourceType.substitutionChain
                                ast.sourceTypeSymbol = sourceSubstitution.replay(parameterizedSourceSymbol.sourceType!!)
                                ast.sourceOmicronSymbol = OmicronTypeSymbol(architecture.defaultNodeCost)
                                if (ast.ofType is ImplicitTypeLiteral) {
                                    ast.ofTypeSymbol = ast.sourceTypeSymbol
                                } else {
                                    validateExplicitSymbol(ast.ctx, errors, ast.ofType, ast.scope)
                                    ast.ofTypeSymbol = ast.scope.fetch(ast.ofType)
                                }
                                ast.body.scope.define(ast.gid, ast.ofTypeSymbol)
                                ast.body.accept(this)
                                when (val bodyTypeSymbol = ast.body.readType()) {
                                    is SymbolInstantiation -> {
                                        val bodySubstitution = bodyTypeSymbol.substitutionChain
                                        when (val parameterizedBodySymbol =
                                            bodyTypeSymbol.substitutionChain.originalSymbol) {
                                            is ParameterizedCoproductSymbol -> {
                                                if (generatePath(parameterizedBodySymbol) == generatePath(
                                                        parameterizedSourceSymbol
                                                    )
                                                ) {
                                                    ast.bodyTypeSymbol = bodyTypeSymbol
                                                    val bodySourceType =
                                                        bodySubstitution.replay(parameterizedBodySymbol.sourceType!!)
                                                    val typeArgs =
                                                        parameterizedBodySymbol.replaceParameters(
                                                            bodySourceType,
                                                            sourceSubstitution.replayArgs()
                                                        )
                                                    val substitution = Substitution(
                                                        parameterizedSourceSymbol.typeParams,
                                                        typeArgs
                                                    )
                                                    ast.assignType(
                                                        errors,
                                                        substitution.apply(parameterizedSourceSymbol)
                                                    )
                                                } else {
                                                    errors.add(ast.source.ctx, InvalidBodyType(bodyTypeSymbol))
                                                    ast.assignType(errors, ErrorSymbol)
                                                }
                                            }
                                            is ParameterizedRecordTypeSymbol -> {
                                                if (generatePath(parameterizedBodySymbol.parent as Symbol) == generatePath(
                                                        parameterizedSourceSymbol
                                                    )
                                                ) {
                                                    ast.bodyTypeSymbol = bodyTypeSymbol
                                                    val typeArgs = parameterizedSourceSymbol.replaceParameters(
                                                        bodySubstitution.replay(parameterizedBodySymbol.typeParams.first()),
                                                        sourceSubstitution.replayArgs()
                                                    )
                                                    val substitution = Substitution(
                                                        parameterizedSourceSymbol.typeParams,
                                                        typeArgs
                                                    )
                                                    ast.assignType(
                                                        errors,
                                                        substitution.apply(parameterizedSourceSymbol)
                                                    )
                                                } else {
                                                    errors.add(ast.source.ctx, InvalidBodyType(bodyTypeSymbol))
                                                    ast.assignType(errors, ErrorSymbol)
                                                }
                                            }
                                            else -> {
                                                errors.add(ast.source.ctx, InvalidBodyType(bodyTypeSymbol))
                                                ast.assignType(errors, ErrorSymbol)
                                            }
                                        }
                                    }
                                    is ObjectSymbol -> {
                                        ast.bodyTypeSymbol = bodyTypeSymbol
                                        ast.assignType(errors, sourceType)
                                    }
                                    else -> {
                                        errors.add(ast.source.ctx, InvalidBodyType(bodyTypeSymbol))
                                        ast.assignType(errors, ErrorSymbol)
                                    }
                                }
                            } else {
                                errors.add(ast.source.ctx, FlatMapFeatureBan(ast.source.readType()))
                                ast.assignType(errors, ErrorSymbol)
                            }
                        }
                        else -> {
                            errors.add(ast.source.ctx, InvalidSource(ast.source.readType()))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                else -> {
                    errors.add(ast.source.ctx, InvalidSource(ast.source.readType()))
                    ast.assignType(errors, ErrorSymbol)
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
            val symbol = ast.scope.fetch(ast.gid)
            ast.symbolRef = symbol
            when (symbol) {
                is LocalVariableSymbol -> ast.assignType(errors, preludeTable.fetch(Lang.unitId))
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
                    val member = lhsType.fetchHere(ast.gid)
                    ast.symbolRef = member
                    when (member) {
                        is FieldSymbol -> ast.assignType(errors, preludeTable.fetch(Lang.unitId))
                        else -> {
                            errors.add(ast.ctx, SymbolIsNotAField(ast.gid))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = lhsType.substitutionChain.originalSymbol) {
                        is ParameterizedRecordTypeSymbol -> {
                            val substitution = lhsType.substitutionChain
                            when (val member = parameterizedSymbol.fetchHere(ast.gid)) {
                                is FieldSymbol -> {
                                    ast.symbolRef = substitution.replay(member.ofTypeSymbol)
                                    ast.assignType(errors, preludeTable.fetch(Lang.unitId))
                                }
                                else -> {
                                    errors.add(ast.ctx, SymbolIsNotAField(ast.gid))
                                    ast.assignType(errors, ErrorSymbol)
                                }
                            }
                        }
                        else -> {
                            errors.add(ast.ctx, SymbolHasNoFields(ast.gid, ast.lhs.readType()))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                else -> {
                    errors.add(ast.ctx, SymbolHasNoFields(ast.gid, ast.lhs.readType()))
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

    // Do not call super for switch case
    override fun visit(ast: SwitchAst) {
        try {
            super.visit(ast)
            when (val source = ast.source.readType()) {
                is GroundCoproductSymbol -> {
                    if (!source.featureSupport.switchExpr) {
                        errors.add(ast.source.ctx, SwitchFeatureBan(source))
                        ast.assignType(errors, ErrorSymbol)
                    } else {
                        ast.sourceTypeSymbol = source
                    }
                }
                is GroundRecordTypeSymbol -> {
                    if (!source.featureSupport.switchExpr) {
                        errors.add(ast.source.ctx, SwitchFeatureBan(source))
                        ast.assignType(errors, ErrorSymbol)
                    } else {
                        ast.sourceTypeSymbol = source.parent as GroundCoproductSymbol
                    }
                }
                is SymbolInstantiation -> {
                    when (val parameterizedSymbol = source.substitutionChain.originalSymbol) {
                        is ParameterizedCoproductSymbol -> {
                            if (!parameterizedSymbol.featureSupport.switchExpr) {
                                errors.add(ast.source.ctx, SwitchFeatureBan(source))
                                ast.assignType(errors, ErrorSymbol)
                            } else {
                                ast.sourceTypeSymbol = source
                            }
                        }
                        else -> {
                            errors.add(ast.source.ctx, InvalidSwitchSource(source))
                            ast.assignType(errors, ErrorSymbol)
                        }
                    }
                }
                is ObjectSymbol -> {
                    if (!source.featureSupport.switchExpr) {
                        errors.add(ast.source.ctx, SwitchFeatureBan(source))
                        ast.assignType(errors, ErrorSymbol)
                    } else {
                        ast.sourceTypeSymbol = source.parent as GroundCoproductSymbol
                    }
                }
                else -> {
                    errors.add(ast.source.ctx, InvalidSwitchSource(source))
                    ast.assignType(errors, ErrorSymbol)
                }
            }
            ast.assignType(errors, findBestType(ast.ctx, errors, ast.cases.map { it.body.readType() }))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: AsAst) {
        try {
            super.visit(ast)
            ast.assignType(errors, ast.scope.fetch(ast.identifier))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }

    override fun visit(ast: IsAst) {
        try {
            super.visit(ast)
            ast.identifierSymbol = filterValidTypes(ast.ctx, errors, ast.scope.fetch(ast.identifier))
            ast.assignType(errors, preludeTable.fetch(Lang.booleanId))
        } catch (ex: LanguageException) {
            errors.addAll(ast.ctx, ex.errors)
            ast.assignType(errors, ErrorSymbol)
        }
    }
}
