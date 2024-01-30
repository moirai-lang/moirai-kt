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
    val dictionaryEquals = createDictionaryEqualsMember()
    val dictionaryNotEquals = createDictionaryNotEqualsMember()
    val mutableDictionaryEquals = createMutableDictionaryEqualsMember()
    val mutableDictionaryNotEquals = createMutableDictionaryNotEqualsMember()
    val setEquals = createSetEqualsMember()
    val setNotEquals = createSetNotEqualsMember()
    val mutableSetEquals = createMutableSetEqualsMember()
    val mutableSetNotEquals = createMutableSetNotEqualsMember()

    /**
     * List Equality
     */
    private fun createListEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.listType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.listId.name}.${BinaryOperator.Equal.idStr}.${Lang.listInputFinTypeId.name}",
            Lang.listInputFinTypeId
        )
        equalsMemberFunction.defineType(Lang.listElementTypeParam.identifier, Lang.listElementTypeParam)
        equalsMemberFunction.defineType(Lang.listFinTypeParam.identifier, Lang.listFinTypeParam)
        equalsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
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

    private fun createListNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.listType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.listId.name}.${BinaryOperator.NotEqual.idStr}.${Lang.listInputFinTypeId.name}",
            Lang.listInputFinTypeId
        )
        notEqualsMemberFunction.defineType(Lang.listElementTypeParam.identifier, Lang.listElementTypeParam)
        notEqualsMemberFunction.defineType(Lang.listFinTypeParam.identifier, Lang.listFinTypeParam)
        notEqualsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
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

    private fun createMutableListEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableListType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.mutableListId.name}.${BinaryOperator.Equal.idStr}.${Lang.mutableListInputFinTypeId.name}",
            Lang.mutableListInputFinTypeId
        )
        equalsMemberFunction.defineType(Lang.mutableListElementTypeParam.identifier, Lang.mutableListElementTypeParam)
        equalsMemberFunction.defineType(Lang.mutableListFinTypeParam.identifier, Lang.mutableListFinTypeParam)
        equalsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams =
            listOf(Lang.mutableListElementTypeParam, Lang.mutableListFinTypeParam, inputFinTypeArg)

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

    private fun createMutableListNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableListType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.mutableListId.name}.${BinaryOperator.NotEqual.idStr}.${Lang.mutableListInputFinTypeId.name}",
            Lang.mutableListInputFinTypeId
        )
        notEqualsMemberFunction.defineType(Lang.mutableListElementTypeParam.identifier, Lang.mutableListElementTypeParam)
        notEqualsMemberFunction.defineType(Lang.mutableListFinTypeParam.identifier, Lang.mutableListFinTypeParam)
        notEqualsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams =
            listOf(Lang.mutableListElementTypeParam, Lang.mutableListFinTypeParam, inputFinTypeArg)

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
    private fun createDictionaryEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.dictionaryType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            TripleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.dictionaryId.name}.${BinaryOperator.Equal.idStr}.${Lang.dictionaryInputFinTypeId.name}",
            Lang.dictionaryInputFinTypeId
        )
        equalsMemberFunction.defineType(Lang.dictionaryKeyTypeParam.identifier, Lang.dictionaryKeyTypeParam)
        equalsMemberFunction.defineType(Lang.dictionaryValueTypeParam.identifier, Lang.dictionaryValueTypeParam)
        equalsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams =
            listOf(
                Lang.dictionaryKeyTypeParam,
                Lang.dictionaryValueTypeParam,
                Lang.dictionaryFinTypeParam,
                inputFinTypeArg
            )

        val inputSubstitution =
            Substitution(
                Lang.dictionaryType.typeParams,
                listOf(Lang.dictionaryKeyTypeParam, Lang.dictionaryValueTypeParam, inputFinTypeArg)
            )
        val inputType = inputSubstitution.apply(Lang.dictionaryType)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.dictionaryFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.dictionaryType.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    private fun createDictionaryNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.dictionaryType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            TripleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.dictionaryId.name}.${BinaryOperator.NotEqual.idStr}.${Lang.dictionaryInputFinTypeId.name}",
            Lang.dictionaryInputFinTypeId
        )
        notEqualsMemberFunction.defineType(Lang.dictionaryKeyTypeParam.identifier, Lang.dictionaryKeyTypeParam)
        notEqualsMemberFunction.defineType(Lang.dictionaryValueTypeParam.identifier, Lang.dictionaryValueTypeParam)
        notEqualsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams =
            listOf(
                Lang.dictionaryKeyTypeParam,
                Lang.dictionaryValueTypeParam,
                Lang.dictionaryFinTypeParam,
                inputFinTypeArg
            )

        val inputSubstitution =
            Substitution(
                Lang.dictionaryType.typeParams,
                listOf(Lang.dictionaryKeyTypeParam, Lang.dictionaryValueTypeParam, inputFinTypeArg)
            )
        val inputType = inputSubstitution.apply(Lang.dictionaryType)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.dictionaryFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.dictionaryType.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }

    private fun createMutableDictionaryEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableDictionaryType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            TripleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg =
            ImmutableFinTypeParameter(
                "${Lang.mutableDictionaryId.name}.${BinaryOperator.Equal.idStr}.${Lang.mutableDictionaryInputFinTypeId.name}",
                Lang.mutableDictionaryInputFinTypeId
            )
        equalsMemberFunction.defineType(Lang.mutableDictionaryKeyTypeParam.identifier, Lang.mutableDictionaryKeyTypeParam)
        equalsMemberFunction.defineType(
            Lang.mutableDictionaryValueTypeParam.identifier,
            Lang.mutableDictionaryValueTypeParam
        )
        equalsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams =
            listOf(
                Lang.mutableDictionaryKeyTypeParam,
                Lang.mutableDictionaryValueTypeParam,
                Lang.mutableDictionaryFinTypeParam,
                inputFinTypeArg
            )

        val inputSubstitution = Substitution(
            Lang.mutableDictionaryType.typeParams,
            listOf(Lang.mutableDictionaryKeyTypeParam, Lang.mutableDictionaryValueTypeParam, inputFinTypeArg)
        )
        val inputType = inputSubstitution.apply(Lang.mutableDictionaryType)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.mutableDictionaryFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.mutableDictionaryType.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    private fun createMutableDictionaryNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableDictionaryType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            TripleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg =
            ImmutableFinTypeParameter(
                "${Lang.mutableDictionaryId.name}.${BinaryOperator.NotEqual.idStr}.${Lang.mutableDictionaryInputFinTypeId.name}",
                Lang.mutableDictionaryInputFinTypeId
            )
        notEqualsMemberFunction.defineType(
            Lang.mutableDictionaryKeyTypeParam.identifier,
            Lang.mutableDictionaryKeyTypeParam
        )
        notEqualsMemberFunction.defineType(
            Lang.mutableDictionaryValueTypeParam.identifier,
            Lang.mutableDictionaryValueTypeParam
        )
        notEqualsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams =
            listOf(
                Lang.mutableDictionaryKeyTypeParam,
                Lang.mutableDictionaryValueTypeParam,
                Lang.mutableDictionaryFinTypeParam,
                inputFinTypeArg
            )

        val inputSubstitution = Substitution(
            Lang.mutableDictionaryType.typeParams,
            listOf(Lang.mutableDictionaryKeyTypeParam, Lang.mutableDictionaryValueTypeParam, inputFinTypeArg)
        )
        val inputType = inputSubstitution.apply(Lang.mutableDictionaryType)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.mutableDictionaryFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.mutableDictionaryType.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }

    /**
     * Set Equality
     */
    private fun createSetEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.setType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.setId.name}.${BinaryOperator.Equal.idStr}.${Lang.setInputFinTypeId.name}",
            Lang.setInputFinTypeId
        )
        equalsMemberFunction.defineType(Lang.setElementTypeParam.identifier, Lang.setElementTypeParam)
        equalsMemberFunction.defineType(Lang.setFinTypeParam.identifier, Lang.setFinTypeParam)
        equalsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams = listOf(Lang.setElementTypeParam, Lang.setFinTypeParam, inputFinTypeArg)

        val inputSubstitution = Substitution(Lang.setType.typeParams, listOf(Lang.setElementTypeParam, inputFinTypeArg))
        val inputType = inputSubstitution.apply(Lang.setType)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.setFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.setType.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    private fun createSetNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.setType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.setId.name}.${BinaryOperator.NotEqual.idStr}.${Lang.setInputFinTypeId.name}",
            Lang.setInputFinTypeId
        )
        notEqualsMemberFunction.defineType(Lang.setElementTypeParam.identifier, Lang.setElementTypeParam)
        notEqualsMemberFunction.defineType(Lang.setFinTypeParam.identifier, Lang.setFinTypeParam)
        notEqualsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams = listOf(Lang.setElementTypeParam, Lang.setFinTypeParam, inputFinTypeArg)

        val inputSubstitution = Substitution(Lang.setType.typeParams, listOf(Lang.setElementTypeParam, inputFinTypeArg))
        val inputType = inputSubstitution.apply(Lang.setType)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.setFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.setType.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }

    private fun createMutableSetEqualsMember(): ParameterizedMemberPluginSymbol {
        val equalsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableSetType,
            Identifier(NotInSource, BinaryOperator.Equal.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.mutableSetId.name}.${BinaryOperator.Equal.idStr}.${Lang.mutableSetInputFinTypeId.name}",
            Lang.mutableSetInputFinTypeId
        )
        equalsMemberFunction.defineType(Lang.mutableSetElementTypeParam.identifier, Lang.mutableSetElementTypeParam)
        equalsMemberFunction.defineType(Lang.mutableSetFinTypeParam.identifier, Lang.mutableSetFinTypeParam)
        equalsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        equalsMemberFunction.typeParams =
            listOf(Lang.mutableSetElementTypeParam, Lang.mutableSetFinTypeParam, inputFinTypeArg)

        val inputSubstitution =
            Substitution(Lang.mutableSetType.typeParams, listOf(Lang.mutableSetElementTypeParam, inputFinTypeArg))
        val inputType = inputSubstitution.apply(Lang.mutableSetType)

        val otherParam =
            FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
        equalsMemberFunction.formalParams = listOf(otherParam)
        equalsMemberFunction.returnType = Lang.booleanType
        equalsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.mutableSetFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.mutableSetType.define(equalsMemberFunction.identifier, equalsMemberFunction)
        return equalsMemberFunction
    }

    private fun createMutableSetNotEqualsMember(): ParameterizedMemberPluginSymbol {
        val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
            Lang.mutableSetType,
            Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
            DoubleParentSingleFinPluginInstantiation
        )
        val inputFinTypeArg = ImmutableFinTypeParameter(
            "${Lang.mutableSetId.name}.${BinaryOperator.NotEqual.idStr}.${Lang.mutableSetInputFinTypeId.name}",
            Lang.mutableSetInputFinTypeId
        )
        notEqualsMemberFunction.defineType(Lang.mutableSetElementTypeParam.identifier, Lang.mutableSetElementTypeParam)
        notEqualsMemberFunction.defineType(Lang.mutableSetFinTypeParam.identifier, Lang.mutableSetFinTypeParam)
        notEqualsMemberFunction.defineType(inputFinTypeArg.identifier, inputFinTypeArg)
        notEqualsMemberFunction.typeParams =
            listOf(Lang.mutableSetElementTypeParam, Lang.mutableSetFinTypeParam, inputFinTypeArg)

        val inputSubstitution =
            Substitution(Lang.mutableSetType.typeParams, listOf(Lang.mutableSetElementTypeParam, inputFinTypeArg))
        val inputType = inputSubstitution.apply(Lang.mutableSetType)

        val otherParam =
            FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
        notEqualsMemberFunction.formalParams = listOf(otherParam)
        notEqualsMemberFunction.returnType = Lang.booleanType
        notEqualsMemberFunction.costExpression = SumCostExpression(
            listOf(
                Lang.mutableSetFinTypeParam,
                inputFinTypeArg
            )
        )

        Lang.mutableSetType.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
        return notEqualsMemberFunction
    }
}