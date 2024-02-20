package moirai.semantics.prelude

internal enum class BinaryOperator(val opStr: String, val idStr: String) {
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

internal enum class UnaryOperator(val idStr: String) {
    Not("not"),
    Negate("negate")
}

internal enum class CollectionFields(val idStr: String) {
    Size("size")
}

internal enum class StringMethods(val idStr: String) {
    ToCharArray("toCharList"),
    ToString("toString")
}

internal enum class DecimalMethods(val idStr: String) {
    AscribeFin("ascribeFin")
}

internal enum class CollectionMethods(val idStr: String) {
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