package com.tsikhe.shardscript.semantics.prelude

import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.infer.DoubleParentSingleOmicronPluginInstantiation
import com.tsikhe.shardscript.semantics.infer.Substitution
import com.tsikhe.shardscript.semantics.infer.TripleParentSingleOmicronPluginInstantiation

/**
 * List Equality
 */
fun createListEqualsMember(
    listSymbol: ParameterizedBasicTypeSymbol,
    listElementType: StandardTypeParameter,
    listOmicron: ImmutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        listSymbol,
        Identifier(BinaryOperator.Equal.idStr),
        DoubleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.listInputOmicronTypeId)
    equalsMemberFunction.define(listElementType.identifier, listElementType)
    equalsMemberFunction.define(listOmicron.identifier, listOmicron)
    equalsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    equalsMemberFunction.typeParams = listOf(listElementType, listOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(listSymbol.typeParams, listOf(listElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(listSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            listOmicron,
            inputOmicronTypeArg
        )
    )

    listSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createListNotEqualsMember(
    listSymbol: ParameterizedBasicTypeSymbol,
    listElementType: StandardTypeParameter,
    listOmicron: ImmutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        listSymbol,
        Identifier(BinaryOperator.NotEqual.idStr),
        DoubleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.listInputOmicronTypeId)
    notEqualsMemberFunction.define(listElementType.identifier, listElementType)
    notEqualsMemberFunction.define(listOmicron.identifier, listOmicron)
    notEqualsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams = listOf(listElementType, listOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(listSymbol.typeParams, listOf(listElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(listSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            listOmicron,
            inputOmicronTypeArg
        )
    )

    listSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}

fun createMutableListEqualsMember(
    mutableListSymbol: ParameterizedBasicTypeSymbol,
    mutableListElementType: StandardTypeParameter,
    mutableListOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableListSymbol,
        Identifier(BinaryOperator.Equal.idStr),
        DoubleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.mutableListInputOmicronTypeId)
    equalsMemberFunction.define(mutableListElementType.identifier, mutableListElementType)
    equalsMemberFunction.define(mutableListOmicron.identifier, mutableListOmicron)
    equalsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    equalsMemberFunction.typeParams = listOf(mutableListElementType, mutableListOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(mutableListSymbol.typeParams, listOf(mutableListElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(mutableListSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableListOmicron,
            inputOmicronTypeArg
        )
    )

    mutableListSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createMutableListNotEqualsMember(
    mutableListSymbol: ParameterizedBasicTypeSymbol,
    mutableListElementType: StandardTypeParameter,
    mutableListOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableListSymbol,
        Identifier(BinaryOperator.NotEqual.idStr),
        DoubleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.mutableListInputOmicronTypeId)
    notEqualsMemberFunction.define(mutableListElementType.identifier, mutableListElementType)
    notEqualsMemberFunction.define(mutableListOmicron.identifier, mutableListOmicron)
    notEqualsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams = listOf(mutableListElementType, mutableListOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(mutableListSymbol.typeParams, listOf(mutableListElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(mutableListSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableListOmicron,
            inputOmicronTypeArg
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
    dictionaryOmicron: ImmutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        dictionarySymbol,
        Identifier(BinaryOperator.Equal.idStr),
        TripleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.dictionaryInputOmicronTypeId)
    equalsMemberFunction.define(dictionaryKeyType.identifier, dictionaryKeyType)
    equalsMemberFunction.define(dictionaryValueType.identifier, dictionaryValueType)
    equalsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    equalsMemberFunction.typeParams =
        listOf(dictionaryKeyType, dictionaryValueType, dictionaryOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(dictionarySymbol.typeParams, listOf(dictionaryKeyType, dictionaryValueType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(dictionarySymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            dictionaryOmicron,
            inputOmicronTypeArg
        )
    )

    dictionarySymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createDictionaryNotEqualsMember(
    dictionarySymbol: ParameterizedBasicTypeSymbol,
    dictionaryKeyType: StandardTypeParameter,
    dictionaryValueType: StandardTypeParameter,
    dictionaryOmicron: ImmutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        dictionarySymbol,
        Identifier(BinaryOperator.NotEqual.idStr),
        TripleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.dictionaryInputOmicronTypeId)
    notEqualsMemberFunction.define(dictionaryKeyType.identifier, dictionaryKeyType)
    notEqualsMemberFunction.define(dictionaryValueType.identifier, dictionaryValueType)
    notEqualsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams =
        listOf(dictionaryKeyType, dictionaryValueType, dictionaryOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(dictionarySymbol.typeParams, listOf(dictionaryKeyType, dictionaryValueType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(dictionarySymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            dictionaryOmicron,
            inputOmicronTypeArg
        )
    )

    dictionarySymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}

fun createMutableDictionaryEqualsMember(
    mutableDictionarySymbol: ParameterizedBasicTypeSymbol,
    mutableDictionaryKeyType: StandardTypeParameter,
    mutableDictionaryValueType: StandardTypeParameter,
    mutableDictionaryOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableDictionarySymbol,
        Identifier(BinaryOperator.Equal.idStr),
        TripleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    })
    val inputOmicronTypeArg =
        ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.mutableDictionaryInputOmicronTypeId)
    equalsMemberFunction.define(mutableDictionaryKeyType.identifier, mutableDictionaryKeyType)
    equalsMemberFunction.define(mutableDictionaryValueType.identifier, mutableDictionaryValueType)
    equalsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    equalsMemberFunction.typeParams =
        listOf(mutableDictionaryKeyType, mutableDictionaryValueType, mutableDictionaryOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(
        mutableDictionarySymbol.typeParams,
        listOf(mutableDictionaryKeyType, mutableDictionaryValueType, inputOmicronTypeArg)
    )
    val inputType = inputSubstitution.apply(mutableDictionarySymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableDictionaryOmicron,
            inputOmicronTypeArg
        )
    )

    mutableDictionarySymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createMutableDictionaryNotEqualsMember(
    mutableDictionarySymbol: ParameterizedBasicTypeSymbol,
    mutableDictionaryKeyType: StandardTypeParameter,
    mutableDictionaryValueType: StandardTypeParameter,
    mutableDictionaryOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableDictionarySymbol,
        Identifier(BinaryOperator.NotEqual.idStr),
        TripleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    })
    val inputOmicronTypeArg =
        ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.mutableDictionaryInputOmicronTypeId)
    notEqualsMemberFunction.define(mutableDictionaryKeyType.identifier, mutableDictionaryKeyType)
    notEqualsMemberFunction.define(mutableDictionaryValueType.identifier, mutableDictionaryValueType)
    notEqualsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams =
        listOf(mutableDictionaryKeyType, mutableDictionaryValueType, mutableDictionaryOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(
        mutableDictionarySymbol.typeParams,
        listOf(mutableDictionaryKeyType, mutableDictionaryValueType, inputOmicronTypeArg)
    )
    val inputType = inputSubstitution.apply(mutableDictionarySymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableDictionaryOmicron,
            inputOmicronTypeArg
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
    setOmicron: ImmutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        setSymbol,
        Identifier(BinaryOperator.Equal.idStr),
        DoubleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.setInputOmicronTypeId)
    equalsMemberFunction.define(setElementType.identifier, setElementType)
    equalsMemberFunction.define(setOmicron.identifier, setOmicron)
    equalsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    equalsMemberFunction.typeParams = listOf(setElementType, setOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(setSymbol.typeParams, listOf(setElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(setSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            setOmicron,
            inputOmicronTypeArg
        )
    )

    setSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createSetNotEqualsMember(
    setSymbol: ParameterizedBasicTypeSymbol,
    setElementType: StandardTypeParameter,
    setOmicron: ImmutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        setSymbol,
        Identifier(BinaryOperator.NotEqual.idStr),
        DoubleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.setInputOmicronTypeId)
    notEqualsMemberFunction.define(setElementType.identifier, setElementType)
    notEqualsMemberFunction.define(setOmicron.identifier, setOmicron)
    notEqualsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams = listOf(setElementType, setOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(setSymbol.typeParams, listOf(setElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(setSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            setOmicron,
            inputOmicronTypeArg
        )
    )

    setSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}

fun createMutableSetEqualsMember(
    mutableSetSymbol: ParameterizedBasicTypeSymbol,
    mutableSetElementType: StandardTypeParameter,
    mutableSetOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableSetSymbol,
        Identifier(BinaryOperator.Equal.idStr),
        DoubleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.mutableSetInputOmicronTypeId)
    equalsMemberFunction.define(mutableSetElementType.identifier, mutableSetElementType)
    equalsMemberFunction.define(mutableSetOmicron.identifier, mutableSetOmicron)
    equalsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    equalsMemberFunction.typeParams = listOf(mutableSetElementType, mutableSetOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(mutableSetSymbol.typeParams, listOf(mutableSetElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(mutableSetSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, Identifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableSetOmicron,
            inputOmicronTypeArg
        )
    )

    mutableSetSymbol.define(equalsMemberFunction.identifier, equalsMemberFunction)
}

fun createMutableSetNotEqualsMember(
    mutableSetSymbol: ParameterizedBasicTypeSymbol,
    mutableSetElementType: StandardTypeParameter,
    mutableSetOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableSetSymbol,
        Identifier(BinaryOperator.NotEqual.idStr),
        DoubleParentSingleOmicronPluginInstantiation,
    { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    })
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.mutableSetInputOmicronTypeId)
    notEqualsMemberFunction.define(mutableSetElementType.identifier, mutableSetElementType)
    notEqualsMemberFunction.define(mutableSetOmicron.identifier, mutableSetOmicron)
    notEqualsMemberFunction.define(inputOmicronTypeArg.identifier, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams = listOf(mutableSetElementType, mutableSetOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(mutableSetSymbol.typeParams, listOf(mutableSetElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(mutableSetSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, Identifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableSetOmicron,
            inputOmicronTypeArg
        )
    )

    mutableSetSymbol.define(notEqualsMemberFunction.identifier, notEqualsMemberFunction)
}