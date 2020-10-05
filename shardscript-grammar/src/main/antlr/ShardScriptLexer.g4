lexer grammar ShardScriptLexer;

ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
MOD: '%';
NOT: '!';
GT:  '>';
GTE: '>=';
LT:  '<';
LTE: '<=';
EQ:  '==';
NEQ: '!=';
AND: '&&';
OR:  '||';

LPAREN: '(';
RPAREN: ')';
LCURLY: '{' -> pushMode(DEFAULT_MODE);
RCURLY: '}' -> popMode;
LBRACK: '[';
RBRACK: ']';

BOOL_TRUE: 'true';
BOOL_FALSE: 'false';

VAL: 'val';
MUTABLE: 'mutable';
DEF: 'def';
RECORD: 'record';
ENUM: 'enum';
OBJECT: 'object';
FOR: 'for';
MAP: 'map';
FLATMAP: 'flatmap';
IN: 'in';
TO: 'to';
IF: 'if';
ELSE: 'else';
SWITCH: 'switch';
CASE: 'case';
IS: 'is';
AS: 'as';
IMPORT: 'import';
LAMBDA: 'lambda';
BIND: 'bind';
YIELD: 'yield';
VARARGS: 'varargs';
FROM: 'from';
WHERE: 'where';
SELECT: 'select';
GROUP: 'group';
INTO: 'into';
ORDERBY: 'orderby';
JOIN: 'join';
LET: 'let';
ASCENDING: 'ascending';
DESCENDING: 'descending';
ON: 'on';
EQUALS: 'equals';
BY: 'by';

ASSIGN: '=';
DOT: '.';
UNDERSCORE: '_';
COLON:  ':';
COMMA: ',';
ARROW: '->';
POUND: '#';

SBYTE
    :   ('0' | NON_LEADING_ZERO_INT) 's8'
    ;

SHORT
    :   ('0' | NON_LEADING_ZERO_INT) 's16'
    ;

INT
    :   '0' | NON_LEADING_ZERO_INT
    ;

LONG
    :   ('0' | NON_LEADING_ZERO_INT) 's64'
    ;

BYTE
    :   ('0' | NON_LEADING_ZERO_INT) 'u8'
    ;

USHORT
    :   ('0' | NON_LEADING_ZERO_INT) 'u16'
    ;

UINT
    :   ('0' | NON_LEADING_ZERO_INT) 'u32'
    ;

ULONG
    :   ('0' | NON_LEADING_ZERO_INT) 'u64'
    ;

DECIMAL
    :   SIGNIFICAND EXPONENT?
    ;

IDENTIFIER
    :   LETTER_OR_UNDERSCORE (LETTER_OR_UNDERSCORE_OR_DIGIT)*
    ;

OMICRON
    :   POUND LETTER+
    ;

CHAR
    : '\'' (~["'\\\r\n$] | ESCAPE_SEQUENCE) '\''
    ;

fragment EXPONENT
    :   EXPONENT_INDICATOR SIGNED_INTEGER?
    ;

fragment EXPONENT_INDICATOR
    :   [eE]
    ;

fragment SIGNED_INTEGER
    :   [+\-]? DIGIT*
    ;

fragment SIGNIFICAND
    :   INTEGER_PART DOT FRACTION_PART?
    ;

fragment FRACTION_PART
    :   DIGIT*
    ;

fragment INTEGER_PART
    :   DIGIT*
    ;

fragment NON_LEADING_ZERO_INT
    :   NON_ZERO_DIGIT DIGIT*
    ;

fragment DIGIT
    :   '0' | NON_ZERO_DIGIT
    ;

fragment NON_ZERO_DIGIT
    :   [1-9]
    ;

fragment LETTER
    :   [a-zA-Z]
    ;

fragment LETTER_OR_UNDERSCORE
    :   LETTER | UNDERSCORE
    ;

fragment LETTER_OR_UNDERSCORE_OR_DIGIT
    :   LETTER_OR_UNDERSCORE | DIGIT
    ;

fragment HEX_DIGIT
    : [0-9a-fA-F]
    ;

fragment ESCAPE_SEQUENCE
    : '\\' [btnfr"'\\$]
    | '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

STRING_START:   '"' -> pushMode(STRING_COMPONENT);

WS
    :   [ \t\n\r]+ -> skip
    ;

mode STRING_COMPONENT;

STRING_END:         '"' -> popMode;
STRING_CHARS:       (~["\\\r\n$] | ESCAPE_SEQUENCE)+;
STRING_INTERP_OPEN: '${' -> pushMode(DEFAULT_MODE);
