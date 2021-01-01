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
        GroundIdentifier(BinaryOperator.Equal.idStr),
        DoubleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.listInputOmicronTypeId)
    equalsMemberFunction.define(listElementType.gid, listElementType)
    equalsMemberFunction.define(listOmicron.gid, listOmicron)
    equalsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    equalsMemberFunction.typeParams = listOf(listElementType, listOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(listSymbol.typeParams, listOf(listElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(listSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, GroundIdentifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            listOmicron,
            inputOmicronTypeArg
        )
    )

    listSymbol.define(equalsMemberFunction.gid, equalsMemberFunction)
}

fun createListNotEqualsMember(
    listSymbol: ParameterizedBasicTypeSymbol,
    listElementType: StandardTypeParameter,
    listOmicron: ImmutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        listSymbol,
        GroundIdentifier(BinaryOperator.NotEqual.idStr),
        DoubleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.listInputOmicronTypeId)
    notEqualsMemberFunction.define(listElementType.gid, listElementType)
    notEqualsMemberFunction.define(listOmicron.gid, listOmicron)
    notEqualsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams = listOf(listElementType, listOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(listSymbol.typeParams, listOf(listElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(listSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, GroundIdentifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            listOmicron,
            inputOmicronTypeArg
        )
    )

    listSymbol.define(notEqualsMemberFunction.gid, notEqualsMemberFunction)
}

fun createMutableListEqualsMember(
    mutableListSymbol: ParameterizedBasicTypeSymbol,
    mutableListElementType: StandardTypeParameter,
    mutableListOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableListSymbol,
        GroundIdentifier(BinaryOperator.Equal.idStr),
        DoubleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.mutableListInputOmicronTypeId)
    equalsMemberFunction.define(mutableListElementType.gid, mutableListElementType)
    equalsMemberFunction.define(mutableListOmicron.gid, mutableListOmicron)
    equalsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    equalsMemberFunction.typeParams = listOf(mutableListElementType, mutableListOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(mutableListSymbol.typeParams, listOf(mutableListElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(mutableListSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, GroundIdentifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableListOmicron,
            inputOmicronTypeArg
        )
    )

    mutableListSymbol.define(equalsMemberFunction.gid, equalsMemberFunction)
}

fun createMutableListNotEqualsMember(
    mutableListSymbol: ParameterizedBasicTypeSymbol,
    mutableListElementType: StandardTypeParameter,
    mutableListOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableListSymbol,
        GroundIdentifier(BinaryOperator.NotEqual.idStr),
        DoubleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.mutableListInputOmicronTypeId)
    notEqualsMemberFunction.define(mutableListElementType.gid, mutableListElementType)
    notEqualsMemberFunction.define(mutableListOmicron.gid, mutableListOmicron)
    notEqualsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams = listOf(mutableListElementType, mutableListOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(mutableListSymbol.typeParams, listOf(mutableListElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(mutableListSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, GroundIdentifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableListOmicron,
            inputOmicronTypeArg
        )
    )

    mutableListSymbol.define(notEqualsMemberFunction.gid, notEqualsMemberFunction)
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
        GroundIdentifier(BinaryOperator.Equal.idStr),
        TripleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.dictionaryInputOmicronTypeId)
    equalsMemberFunction.define(dictionaryKeyType.gid, dictionaryKeyType)
    equalsMemberFunction.define(dictionaryValueType.gid, dictionaryValueType)
    equalsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    equalsMemberFunction.typeParams =
        listOf(dictionaryKeyType, dictionaryValueType, dictionaryOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(dictionarySymbol.typeParams, listOf(dictionaryKeyType, dictionaryValueType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(dictionarySymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, GroundIdentifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            dictionaryOmicron,
            inputOmicronTypeArg
        )
    )

    dictionarySymbol.define(equalsMemberFunction.gid, equalsMemberFunction)
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
        GroundIdentifier(BinaryOperator.NotEqual.idStr),
        TripleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.dictionaryInputOmicronTypeId)
    notEqualsMemberFunction.define(dictionaryKeyType.gid, dictionaryKeyType)
    notEqualsMemberFunction.define(dictionaryValueType.gid, dictionaryValueType)
    notEqualsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams =
        listOf(dictionaryKeyType, dictionaryValueType, dictionaryOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(dictionarySymbol.typeParams, listOf(dictionaryKeyType, dictionaryValueType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(dictionarySymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, GroundIdentifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            dictionaryOmicron,
            inputOmicronTypeArg
        )
    )

    dictionarySymbol.define(notEqualsMemberFunction.gid, notEqualsMemberFunction)
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
        GroundIdentifier(BinaryOperator.Equal.idStr),
        TripleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    }
    val inputOmicronTypeArg =
        ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.mutableDictionaryInputOmicronTypeId)
    equalsMemberFunction.define(mutableDictionaryKeyType.gid, mutableDictionaryKeyType)
    equalsMemberFunction.define(mutableDictionaryValueType.gid, mutableDictionaryValueType)
    equalsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    equalsMemberFunction.typeParams =
        listOf(mutableDictionaryKeyType, mutableDictionaryValueType, mutableDictionaryOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(
        mutableDictionarySymbol.typeParams,
        listOf(mutableDictionaryKeyType, mutableDictionaryValueType, inputOmicronTypeArg)
    )
    val inputType = inputSubstitution.apply(mutableDictionarySymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, GroundIdentifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableDictionaryOmicron,
            inputOmicronTypeArg
        )
    )

    mutableDictionarySymbol.define(equalsMemberFunction.gid, equalsMemberFunction)
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
        GroundIdentifier(BinaryOperator.NotEqual.idStr),
        TripleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    }
    val inputOmicronTypeArg =
        ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.mutableDictionaryInputOmicronTypeId)
    notEqualsMemberFunction.define(mutableDictionaryKeyType.gid, mutableDictionaryKeyType)
    notEqualsMemberFunction.define(mutableDictionaryValueType.gid, mutableDictionaryValueType)
    notEqualsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams =
        listOf(mutableDictionaryKeyType, mutableDictionaryValueType, mutableDictionaryOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(
        mutableDictionarySymbol.typeParams,
        listOf(mutableDictionaryKeyType, mutableDictionaryValueType, inputOmicronTypeArg)
    )
    val inputType = inputSubstitution.apply(mutableDictionarySymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, GroundIdentifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableDictionaryOmicron,
            inputOmicronTypeArg
        )
    )

    mutableDictionarySymbol.define(notEqualsMemberFunction.gid, notEqualsMemberFunction)
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
        GroundIdentifier(BinaryOperator.Equal.idStr),
        DoubleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.setInputOmicronTypeId)
    equalsMemberFunction.define(setElementType.gid, setElementType)
    equalsMemberFunction.define(setOmicron.gid, setOmicron)
    equalsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    equalsMemberFunction.typeParams = listOf(setElementType, setOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(setSymbol.typeParams, listOf(setElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(setSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, GroundIdentifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            setOmicron,
            inputOmicronTypeArg
        )
    )

    setSymbol.define(equalsMemberFunction.gid, equalsMemberFunction)
}

fun createSetNotEqualsMember(
    setSymbol: ParameterizedBasicTypeSymbol,
    setElementType: StandardTypeParameter,
    setOmicron: ImmutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        setSymbol,
        GroundIdentifier(BinaryOperator.NotEqual.idStr),
        DoubleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.setInputOmicronTypeId)
    notEqualsMemberFunction.define(setElementType.gid, setElementType)
    notEqualsMemberFunction.define(setOmicron.gid, setOmicron)
    notEqualsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams = listOf(setElementType, setOmicron, inputOmicronTypeArg)

    val inputSubstitution = Substitution(setSymbol.typeParams, listOf(setElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(setSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, GroundIdentifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            setOmicron,
            inputOmicronTypeArg
        )
    )

    setSymbol.define(notEqualsMemberFunction.gid, notEqualsMemberFunction)
}

fun createMutableSetEqualsMember(
    mutableSetSymbol: ParameterizedBasicTypeSymbol,
    mutableSetElementType: StandardTypeParameter,
    mutableSetOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val equalsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableSetSymbol,
        GroundIdentifier(BinaryOperator.Equal.idStr),
        DoubleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(equalsMemberFunction, Lang.mutableSetInputOmicronTypeId)
    equalsMemberFunction.define(mutableSetElementType.gid, mutableSetElementType)
    equalsMemberFunction.define(mutableSetOmicron.gid, mutableSetOmicron)
    equalsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    equalsMemberFunction.typeParams = listOf(mutableSetElementType, mutableSetOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(mutableSetSymbol.typeParams, listOf(mutableSetElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(mutableSetSymbol)

    val otherParam = FunctionFormalParameterSymbol(equalsMemberFunction, GroundIdentifier("other"), inputType)
    equalsMemberFunction.formalParams = listOf(otherParam)
    equalsMemberFunction.returnType = booleanType
    equalsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableSetOmicron,
            inputOmicronTypeArg
        )
    )

    mutableSetSymbol.define(equalsMemberFunction.gid, equalsMemberFunction)
}

fun createMutableSetNotEqualsMember(
    mutableSetSymbol: ParameterizedBasicTypeSymbol,
    mutableSetElementType: StandardTypeParameter,
    mutableSetOmicron: MutableOmicronTypeParameter,
    booleanType: BasicTypeSymbol
) {
    val notEqualsMemberFunction = ParameterizedMemberPluginSymbol(
        mutableSetSymbol,
        GroundIdentifier(BinaryOperator.NotEqual.idStr),
        DoubleParentSingleOmicronPluginInstantiation
    ) { t: Value, args: List<Value> ->
        (t as EqualityValue).evalNotEquals(args.first())
    }
    val inputOmicronTypeArg = ImmutableOmicronTypeParameter(notEqualsMemberFunction, Lang.mutableSetInputOmicronTypeId)
    notEqualsMemberFunction.define(mutableSetElementType.gid, mutableSetElementType)
    notEqualsMemberFunction.define(mutableSetOmicron.gid, mutableSetOmicron)
    notEqualsMemberFunction.define(inputOmicronTypeArg.gid, inputOmicronTypeArg)
    notEqualsMemberFunction.typeParams = listOf(mutableSetElementType, mutableSetOmicron, inputOmicronTypeArg)

    val inputSubstitution =
        Substitution(mutableSetSymbol.typeParams, listOf(mutableSetElementType, inputOmicronTypeArg))
    val inputType = inputSubstitution.apply(mutableSetSymbol)

    val otherParam = FunctionFormalParameterSymbol(notEqualsMemberFunction, GroundIdentifier("other"), inputType)
    notEqualsMemberFunction.formalParams = listOf(otherParam)
    notEqualsMemberFunction.returnType = booleanType
    notEqualsMemberFunction.costExpression = SumCostExpression(
        listOf(
            mutableSetOmicron,
            inputOmicronTypeArg
        )
    )

    mutableSetSymbol.define(notEqualsMemberFunction.gid, notEqualsMemberFunction)
}