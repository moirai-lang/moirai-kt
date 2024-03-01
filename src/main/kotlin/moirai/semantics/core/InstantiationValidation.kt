package moirai.semantics.core

// This interface abstracts the validation logic for creating an instantiation for a symbol. For example,
// a Dictionary type will fill in the type arguments using the type arguments of the pairs, whereas a List
// type will fill in the type arguments from the elements directly. Note: this can also be used in theory
// to instantiate a dot apply node in the case where the LHS is a ground type.
internal interface GroundInstantiationValidation<T: RawTerminus, S> {
    fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: T,
        identifier: Identifier,
        explicitTypeArgs: List<Type>
    ): S
}

// This interface abstracts the validation logic for creating an instantiation using the RHS of a dot
// apply node. The instantiation on the LHS needs to be included because it can also contribute to the
// final instantiation type arguments. Note the LHS needs to be an instantiation.
internal interface DotInstantiationValidation<T: RawTerminus, S> {
    fun apply(
        ctx: SourceContext,
        errors: LanguageErrors,
        args: List<Ast>,
        terminus: T,
        identifier: Identifier,
        lhsInstantiation: TypeInstantiation,
        explicitTypeArgs: List<Type>
    ): S
}