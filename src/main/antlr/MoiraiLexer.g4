lexer grammar MoiraiLexer;

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

FIN: 'Fin';

CASE: 'case';
DEF: 'def';
ELSE: 'else';
FOR: 'for';
IF: 'if';
IMPORT: 'import';
IN: 'in';
LAMBDA: 'lambda';
MATCH: 'match';
MUTABLE: 'mutable';
OBJECT: 'object';
RECORD: 'record';
SCRIPT: 'script';
TO: 'to';
TYPE: 'type';
TRANSIENT: 'transient';
VAL: 'val';
VARIANT: 'variant';

COST: 'cost';
PLUGIN: 'plugin';
SIGNATURE: 'signature';

ASSIGN: '=';
DOT: '.';
UNDERSCORE: '_';
COLON:  ':';
COMMA: ',';
ARROW: '->';
POUND: '#';

INT
    :   '0' | NON_LEADING_ZERO_INT
    ;

DECIMAL
    :   SIGNIFICAND EXPONENT?
    ;

IDENTIFIER
    :   LETTER_OR_UNDERSCORE (LETTER_OR_UNDERSCORE_OR_DIGIT)*
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
    :   DIGIT+
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
