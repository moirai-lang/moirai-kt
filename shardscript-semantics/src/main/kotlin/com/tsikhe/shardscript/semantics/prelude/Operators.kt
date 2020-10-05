package com.tsikhe.shardscript.semantics.prelude

enum class BinaryOperator(val opStr: String, val idStr: String) {
    Add("+", "add"),
    Sub("-", "sub"),
    Mul("*", "mul"),
    Div("/", "div"),
    Mod("%", "mod"),
    GreaterThan(">", "greaterThan"),
    GreaterThanEqual(">=", "greaterThanOrEquals"),
    LessThan("<", "lessThan"),
    LessThanEqual("<=", "lessThanOrEquals"),
    Equal("==", "equals"),
    NotEqual("!=", "notEquals"),
    And("&&", "and"),
    Or("||", "or")
}

enum class UnaryOperator(val opStr: String, val idStr: String) {
    Not("!", "not"),
    Negate("-", "negate")
}

enum class CollectionFields(val idStr: String) {
    Size("size")
}

enum class StringMethods(val idStr: String) {
    ToCharArray("toCharList"),
    ToString("toString")
}

enum class CollectionMethods(val idStr: String) {
    IndexLookup("get"),
    KeyLookup("get"),
    Contains("contains"),
    IndexAssign("set"),
    KeyAssign("set"),
    Remove("remove"),
    RemoveAtIndex("removeAt"),
    InsertElement("add"),
    ToImmutableList("toList"),
    ToImmutableSet("toSet"),
    ToImmutableDictionary("toDictionary")
}

enum class IntegerConversions(val idStr: String) {
    ToSigned8("toSigned8"),
    ToSigned16("toSigned16"),
    ToSigned32("toSigned32"),
    ToSigned64("toSigned64"),
    ToUnsigned8("toUnsigned8"),
    ToUnsigned16("toUnsigned16"),
    ToUnsigned32("toUnsigned32"),
    ToUnsigned64("toUnsigned64"),
}

enum class TypeRelations(val idStr: String) {
    Is("is"),
    As("as")
}