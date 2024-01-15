package org.shardscript.semantics.prelude

import org.shardscript.semantics.core.*
import org.shardscript.semantics.infer.DoubleParentSingleFinPluginInstantiation
import org.shardscript.semantics.infer.Substitution
import org.shardscript.semantics.infer.TripleParentSingleFinPluginInstantiation

/**
 * List Equality
 */
fun createListEqualsMember(
    listSymbol: ParameterizedBasicTypeSymbol,
    listElementType: StandardTypeParameter,
    listFin: ImmutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        listSymbol,
        Identifier(NotInSource, BinaryOperator.Equal.idStr),
        DoubleParentSingleFinPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    }
    val inputFinTypeArg = ImmutableFinTypeParameter(equalsMemberFunction, Lang.listInputFinTypeId)
    equalsMemberFunction.define(listElementType.identifier, listElementType)
    equalsMemberFunction.define(listFin.identifier, listFin)
    equalsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
    equalsMemberFunction.typeParams = listOf(listElementType, listFin, inputFinTypeArg)

    val inputSubstitution = Substitution(listSymbol.typeParams, listOf(listElementType, inputFinTypeArg))
    val inputType = inputSubstitution.apply(listSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            listFin,
            inputFinTypeArg
        )
    )

    listSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createListNotEqualsMember(
    listSymbol: ParameterizedBasicTypeSymbol,
    listElementType: StandardTypeParameter,
    listFin: ImmutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        listSymbol,
        Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
        DoubleParentSingleFinPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    }
    val inputFinTypeArg = ImmutableFinTypeParameter(notEqualsMemberFunction, Lang.listInputFinTypeId)
    notEqualsMemberFunction.define(listElementType.identifier, listElementType)
    notEqualsMemberFunction.define(listFin.identifier, listFin)
    notEqualsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
    notEqualsMemberFunction.typeParams = listOf(listElementType, listFin, inputFinTypeArg)

    val inputSubstitution = Substitution(listSymbol.typeParams, listOf(listElementType, inputFinTypeArg))
    val inputType = inputSubstitution.apply(listSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            listFin,
            inputFinTypeArg
        )
    )

    listSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}

fun createMutableListEqualsMember(
    mutableListSymbol: ParameterizedBasicTypeSymbol,
    mutableListElementType: StandardTypeParameter,
    mutableListFin: MutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableListSymbol,
        Identifier(NotInSource, BinaryOperator.Equal.idStr),
        DoubleParentSingleFinPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    }
    val inputFinTypeArg = ImmutableFinTypeParameter(equalsMemberFunction, Lang.mutableListInputFinTypeId)
    equalsMemberFunction.define(mutableListElementType.identifier, mutableListElementType)
    equalsMemberFunction.define(mutableListFin.identifier, mutableListFin)
    equalsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
    equalsMemberFunction.typeParams = listOf(mutableListElementType, mutableListFin, inputFinTypeArg)

    val inputSubstitution =
        Substitution(mutableListSymbol.typeParams, listOf(mutableListElementType, inputFinTypeArg))
    val inputType = inputSubstitution.apply(mutableListSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableListFin,
            inputFinTypeArg
        )
    )

    mutableListSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createMutableListNotEqualsMember(
    mutableListSymbol: ParameterizedBasicTypeSymbol,
    mutableListElementType: StandardTypeParameter,
    mutableListFin: MutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableListSymbol,
        Identifier(NotInSource, BinaryOperator.NotEqual.idStr),
        DoubleParentSingleFinPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    }
    val inputFinTypeArg = ImmutableFinTypeParameter(notEqualsMemberFunction, Lang.mutableListInputFinTypeId)
    notEqualsMemberFunction.define(mutableListElementType.identifier, mutableListElementType)
    notEqualsMemberFunction.define(mutableListFin.identifier, mutableListFin)
    notEqualsMemberFunction.define(inputFinTypeArg.identifier, inputFinTypeArg)
    notEqualsMemberFunction.typeParams = listOf(mutableListElementType, mutableListFin, inputFinTypeArg)

    val inputSubstitution =
        Substitution(mutableListSymbol.typeParams, listOf(mutableListElementType, inputFinTypeArg))
    val inputType = inputSubstitution.apply(mutableListSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableListFin,
            inputFinTypeArg
        )
    )

    mutableListSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}

/**
 * Dictionary Equality
 */
fun createDictionaryEqualsMember(
    dictionarySymbol: ParameterizedBasicTypeSymbol,
    dictionaryKeyType: StandardTypeParameter,
    dictionaryValueType: StandardTypeParameter,
    dictionaryFin: ImmutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
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

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            dictionaryFin,
            inputFinTypeArg
        )
    )

    dictionarySymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createDictionaryNotEqualsMember(
    dictionarySymbol: ParameterizedBasicTypeSymbol,
    dictionaryKeyType: StandardTypeParameter,
    dictionaryValueType: StandardTypeParameter,
    dictionaryFin: ImmutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
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

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            dictionaryFin,
            inputFinTypeArg
        )
    )

    dictionarySymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}

fun createMutableDictionaryEqualsMember(
    mutableDictionarySymbol: ParameterizedBasicTypeSymbol,
    mutableDictionaryKeyType: StandardTypeParameter,
    mutableDictionaryValueType: StandardTypeParameter,
    mutableDictionaryFin: MutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
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

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableDictionaryFin,
            inputFinTypeArg
        )
    )

    mutableDictionarySymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createMutableDictionaryNotEqualsMember(
    mutableDictionarySymbol: ParameterizedBasicTypeSymbol,
    mutableDictionaryKeyType: StandardTypeParameter,
    mutableDictionaryValueType: StandardTypeParameter,
    mutableDictionaryFin: MutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
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

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableDictionaryFin,
            inputFinTypeArg
        )
    )

    mutableDictionarySymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}

/**
 * Set Equality
 */
fun createSetEqualsMember(
    setSymbol: ParameterizedBasicTypeSymbol,
    setElementType: StandardTypeParameter,
    setFin: ImmutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
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

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            setFin,
            inputFinTypeArg
        )
    )

    setSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createSetNotEqualsMember(
    setSymbol: ParameterizedBasicTypeSymbol,
    setElementType: StandardTypeParameter,
    setFin: ImmutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
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

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            setFin,
            inputFinTypeArg
        )
    )

    setSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}

fun createMutableSetEqualsMember(
    mutableSetSymbol: ParameterizedBasicTypeSymbol,
    mutableSetElementType: StandardTypeParameter,
    mutableSetFin: MutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
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

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier(NotInSource, "other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableSetFin,
            inputFinTypeArg
        )
    )

    mutableSetSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createMutableSetNotEqualsMember(
    mutableSetSymbol: ParameterizedBasicTypeSymbol,
    mutableSetElementType: StandardTypeParameter,
    mutableSetFin: MutableFinTypeParameter,
    booleanType: BasicTypeSymbol
) {
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

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier(NotInSource, "other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableSetFin,
            inputFinTypeArg
        )
    )

    mutableSetSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}