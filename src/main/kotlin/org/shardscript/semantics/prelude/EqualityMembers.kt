package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.DoubleParentSingleFinPluginInstantiation
import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.infer.TripleParentSingleFinPluginInstantiation

object EqualityMembers {
    val listEquals = createListEqualsMember()
    val listNotEquals = createListNotEqualsMember()
    val mutableListEquals = createMutableListEqualsMember()
    val mutableListNotEquals = createMutableListNotEqualsMember()

    /**
     * List Equality
     */
    fun createListEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.listType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(equalsMemberFunction, Lang.listInputFinTypeId)
        equalsMemberFunction.define(Lang.listElementTypeParam.identifier, Lang.listElementTypeParam)
        equalsMemberFunction.define(Lang.listFinTypeParam.identifier, Lang.listFinTypeParam)
        equalsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams = listOf(Lang.listElementTypeParam, Lang.listFinTypeParam, inputFinTypeArg)

        val inputSubstitution =
            Substitution(Lang.listType.typeParams, listOf(Lang.listElementTypeParam, inputFinTypeArg))
        val inputType = inputSubstitution.apply(Lang.listType)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.listFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.listType.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    fun createListNotEqualsMember(): ParameterizedMemberPluginSymbol
    {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.listType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(notEqualsMemberFunction, Lang.listInputFinTypeId)
        notEqualsMemberFunction.define(Lang.listElementTypeParam.identifier, Lang.listElementTypeParam)
        notEqualsMemberFunction.define(Lang.listFinTypeParam.identifier, Lang.listFinTypeParam)
        notEqualsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams = listOf(Lang.listElementTypeParam, Lang.listFinTypeParam, inputFinTypeArg)

        val inputSubstitution =
            Substitution(Lang.listType.typeParams, listOf(Lang.listElementTypeParam, inputFinTypeArg))
        val inputType = inputSubstitution.apply(Lang.listType)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.listFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.listType.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }

    fun createMutableListEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableListType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(equalsMemberFunction, Lang.mutableListInputFinTypeId)
        equalsMemberFunction.define(Lang.mutableListElementTypeParam.identifier, Lang.mutableListElementTypeParam)
        equalsMemberFunction.define(Lang.mutableListFinTypeParam.identifier, Lang.mutableListFinTypeParam)
        equalsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams = listOf(Lang.mutableListElementTypeParam, Lang.mutableListFinTypeParam, inputFinTypeArg)

        val inputSubstitution =
            Substitution(Lang.mutableListType.typeParams, listOf(Lang.mutableListElementTypeParam, inputFinTypeArg))
        val inputType = inputSubstitution.apply(Lang.mutableListType)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.mutableListFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.mutableListType.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    fun createMutableListNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableListType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(notEqualsMemberFunction, Lang.mutableListInputFinTypeId)
        notEqualsMemberFunction.define(Lang.mutableListElementTypeParam.identifier, Lang.mutableListElementTypeParam)
        notEqualsMemberFunction.define(Lang.mutableListFinTypeParam.identifier, Lang.mutableListFinTypeParam)
        notEqualsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams = listOf(Lang.mutableListElementTypeParam, Lang.mutableListFinTypeParam, inputFinTypeArg)

        val inputSubstitution =
            Substitution(Lang.mutableListType.typeParams, listOf(Lang.mutableListElementTypeParam, inputFinTypeArg))
        val inputType = inputSubstitution.apply(Lang.mutableListType)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.mutableListFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.mutableListType.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }

    /**
     * Dictionary Equality
     */
    fun createDictionaryEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            dictionarySymbol,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            TripleParentSingleFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        }
        val inputFinTypeArg = ImmutableFinTypeParameter(equalsMemberFunction, Lang.dictionaryInputFinTypeId)
        equalsMemberFunction.define(dictionaryKeyType.identifier, dictionaryKeyType)
        equalsMemberFunction.define(dictionaryValueType.identifier, dictionaryValueType)
        equalsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams =
            listOf(dictionaryKeyType, dictionaryValueType, dictionaryFin, inputFinTypeArg)

        val inputSubstitution =
            Substitution(dictionarySymbol.typeParams, listOf(dictionaryKeyType, dictionaryValueType, inputFinTypeArg))
        val inputType = inputSubstitution.apply(dictionarySymbol)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                dictionaryFin,
                inputFinTypeArg
            )
        )

        dictionarySymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    fun createDictionaryNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            dictionarySymbol,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            TripleParentSingleFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        }
        val inputFinTypeArg = ImmutableFinTypeParameter(notEqualsMemberFunction, Lang.dictionaryInputFinTypeId)
        notEqualsMemberFunction.define(dictionaryKeyType.identifier, dictionaryKeyType)
        notEqualsMemberFunction.define(dictionaryValueType.identifier, dictionaryValueType)
        notEqualsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams =
            listOf(dictionaryKeyType, dictionaryValueType, dictionaryFin, inputFinTypeArg)

        val inputSubstitution =
            Substitution(dictionarySymbol.typeParams, listOf(dictionaryKeyType, dictionaryValueType, inputFinTypeArg))
        val inputType = inputSubstitution.apply(dictionarySymbol)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                dictionaryFin,
                inputFinTypeArg
            )
        )

        dictionarySymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }

    fun createMutableDictionaryEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            mutableDictionarySymbol,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            TripleParentSingleFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        }
        val inputFinTypeArg =
            ImmutableFinTypeParameter(equalsMemberFunction, Lang.mutableDictionaryInputFinTypeId)
        equalsMemberFunction.define(mutableDictionaryKeyType.identifier, mutableDictionaryKeyType)
        equalsMemberFunction.define(mutableDictionaryValueType.identifier, mutableDictionaryValueType)
        equalsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams =
            listOf(mutableDictionaryKeyType, mutableDictionaryValueType, mutableDictionaryFin, inputFinTypeArg)

        val inputSubstitution = Substitution(
            mutableDictionarySymbol.typeParams,
            listOf(mutableDictionaryKeyType, mutableDictionaryValueType, inputFinTypeArg)
        )
        val inputType = inputSubstitution.apply(mutableDictionarySymbol)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                mutableDictionaryFin,
                inputFinTypeArg
            )
        )

        mutableDictionarySymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    fun createMutableDictionaryNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            mutableDictionarySymbol,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            TripleParentSingleFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        }
        val inputFinTypeArg =
            ImmutableFinTypeParameter(notEqualsMemberFunction, Lang.mutableDictionaryInputFinTypeId)
        notEqualsMemberFunction.define(mutableDictionaryKeyType.identifier, mutableDictionaryKeyType)
        notEqualsMemberFunction.define(mutableDictionaryValueType.identifier, mutableDictionaryValueType)
        notEqualsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams =
            listOf(mutableDictionaryKeyType, mutableDictionaryValueType, mutableDictionaryFin, inputFinTypeArg)

        val inputSubstitution = Substitution(
            mutableDictionarySymbol.typeParams,
            listOf(mutableDictionaryKeyType, mutableDictionaryValueType, inputFinTypeArg)
        )
        val inputType = inputSubstitution.apply(mutableDictionarySymbol)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                mutableDictionaryFin,
                inputFinTypeArg
            )
        )

        mutableDictionarySymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }

    /**
     * Set Equality
     */
    fun createSetEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            setSymbol,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DoubleParentSingleFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        }
        val inputFinTypeArg = ImmutableFinTypeParameter(equalsMemberFunction, Lang.setInputFinTypeId)
        equalsMemberFunction.define(setElementType.identifier, setElementType)
        equalsMemberFunction.define(setFin.identifier, setFin)
        equalsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams = listOf(setElementType, setFin, inputFinTypeArg)

        val inputSubstitution = Substitution(setSymbol.typeParams, listOf(setElementType, inputFinTypeArg))
        val inputType = inputSubstitution.apply(setSymbol)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                setFin,
                inputFinTypeArg
            )
        )

        setSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    fun createSetNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            setSymbol,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DoubleParentSingleFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        }
        val inputFinTypeArg = ImmutableFinTypeParameter(notEqualsMemberFunction, Lang.setInputFinTypeId)
        notEqualsMemberFunction.define(setElementType.identifier, setElementType)
        notEqualsMemberFunction.define(setFin.identifier, setFin)
        notEqualsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams = listOf(setElementType, setFin, inputFinTypeArg)

        val inputSubstitution = Substitution(setSymbol.typeParams, listOf(setElementType, inputFinTypeArg))
        val inputType = inputSubstitution.apply(setSymbol)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                setFin,
                inputFinTypeArg
            )
        )

        setSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }

    fun createMutableSetEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            mutableSetSymbol,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DoubleParentSingleFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalEquals(args.first())
        }
        val inputFinTypeArg = ImmutableFinTypeParameter(equalsMemberFunction, Lang.mutableSetInputFinTypeId)
        equalsMemberFunction.define(mutableSetElementType.identifier, mutableSetElementType)
        equalsMemberFunction.define(mutableSetFin.identifier, mutableSetFin)
        equalsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams = listOf(mutableSetElementType, mutableSetFin, inputFinTypeArg)

        val inputSubstitution =
            Substitution(mutableSetSymbol.typeParams, listOf(mutableSetElementType, inputFinTypeArg))
        val inputType = inputSubstitution.apply(mutableSetSymbol)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                mutableSetFin,
                inputFinTypeArg
            )
        )

        mutableSetSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    fun createMutableSetNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            mutableSetSymbol,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DoubleParentSingleFinPluginInstantiation
        ) { t: Value, args: List<Value> ->
            (t as EqualityValue).evalNotEquals(args.first())
        }
        val inputFinTypeArg = ImmutableFinTypeParameter(notEqualsMemberFunction, Lang.mutableSetInputFinTypeId)
        notEqualsMemberFunction.define(mutableSetElementType.identifier, mutableSetElementType)
        notEqualsMemberFunction.define(mutableSetFin.identifier, mutableSetFin)
        notEqualsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams = listOf(mutableSetElementType, mutableSetFin, inputFinTypeArg)

        val inputSubstitution =
            Substitution(mutableSetSymbol.typeParams, listOf(mutableSetElementType, inputFinTypeArg))
        val inputType = inputSubstitution.apply(mutableSetSymbol)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                mutableSetFin,
                inputFinTypeArg
            )
        )

        mutableSetSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }
}