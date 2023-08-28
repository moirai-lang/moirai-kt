parser grammar ShardScriptParser;

options { tokenVocab=ShardScriptLexer; }

file
    :   importStat* stat+ EOF
    ;

importStat
    :   IMPORT importIdSeq
    ;

importIdSeq
    :   IDENTIFIER (DOT IDENTIFIER)*
    ;

stat
    :   letStat
    |   forStat
    |   funDefStat
    |   objectDefStat
    |   recordDefStat
    |   assignStat
    |   expr
    ;

assignStat
    :   left=expr ASSIGN right=expr
    ;

block
    :   LCURLY stats=stat* RCURLY
    ;

letStat
    :   VAL id=IDENTIFIER of=ofType? ASSIGN right=expr     # ImmutableLet
    |   MUTABLE id=IDENTIFIER of=ofType? ASSIGN right=expr # MutableLet
    ;

forStat
    :   FOR LPAREN id=IDENTIFIER of=ofType? IN source=expr RPAREN body=block
    ;

funDefStat
    :   DEF id=IDENTIFIER tp=typeParams? LPAREN params=paramSeq? RPAREN ret=ofType? body=block
    ;

typeParams
    :   LT typeParam (COMMA typeParam)* GT
    ;

typeParam
    :   IDENTIFIER  # IdentifierTypeParam
    |   FIN     # FinTypeParam
    ;

paramDef
    :   id=IDENTIFIER of=ofType
    ;

paramSeq
    :   paramDef (COMMA paramDef)*
    ;

objectDefStat
    :   OBJECT id=IDENTIFIER
    ;

recordDefStat
    :   RECORD id=IDENTIFIER tp=typeParams? LPAREN fields=fieldSeq RPAREN
    ;

fieldSeq
    :   fieldDef (COMMA fieldDef)*
    ;

fieldDef
    :   VAL id=IDENTIFIER of=ofType     # ImmutableField
    |   MUTABLE id=IDENTIFIER of=ofType # MutableField
    ;

expr
    :   LPAREN inner=expr RPAREN                                                        # ParenExpr
    |   left=expr DOT id=IDENTIFIER params=typeExprParams LPAREN args=exprSeq? RPAREN   # ParamDotApply
    |   left=expr DOT id=IDENTIFIER LPAREN args=exprSeq? RPAREN                         # DotApply
    |   left=expr DOT id=IDENTIFIER                                                     # DotExpr
    |   id=IDENTIFIER params=typeExprParams LPAREN args=exprSeq? RPAREN                 # ParamApplyExpr
    |   id=IDENTIFIER LPAREN args=exprSeq? RPAREN                                       # ApplyExpr
    |   left=expr LBRACK right=expr RBRACK                                              # IndexExpr
    |   anyif=ifExpr                                                                    # AnyIf
    |   op=NOT right=expr                                                               # UnaryNot
    |   op=SUB right=expr                                                               # UnaryNegate
    |   left=expr op=(MUL|DIV|MOD) right=expr                                           # InfixMulDivMod
    |   left=expr op=(ADD|SUB) right=expr                                               # InfixAddSub
    |   left=expr op=(GT|GTE|LT|LTE) right=expr                                         # InfixOrder
    |   left=expr op=(IS|AS) id=typeExpr                                                # TypeRelation
    |   left=expr op=(EQ|NEQ) right=expr                                                # InfixEquality
    |   left=expr op=AND right=expr                                                     # InfixAnd
    |   left=expr op=OR right=expr                                                      # InfixOr
    |   value=DECIMAL                                                                   # LiteralDecimal
    |   value=SBYTE                                                                     # LiteralSByte
    |   value=SHORT                                                                     # LiteralShort
    |   value=INT                                                                       # LiteralInt
    |   value=LONG                                                                      # LiteralLong
    |   value=BYTE                                                                      # LiteralByte
    |   value=USHORT                                                                    # LiteralUShort
    |   value=UINT                                                                      # LiteralUInt
    |   value=ULONG                                                                     # LiteralULong
    |   value=(BOOL_TRUE|BOOL_FALSE)                                                    # LiteralBool
    |   value=CHAR                                                                      # LiteralChar
    |   value=string                                                                    # StringExpr
    |   left=expr op=TO right=expr                                                      # ToExpr
    |   id=IDENTIFIER                                                                   # RefExpr
    ;

ifExpr
    :   op=IF LPAREN condition=expr RPAREN trueb=block ELSE elif=ifExpr     # IfElseIfExpr
    |   op=IF LPAREN condition=expr RPAREN trueb=block ELSE falseb=block    # IfElseExpr
    |   op=IF LPAREN condition=expr RPAREN trueb=block                      # StandaloneIfExpr
    ;

elseCase
    :   ELSE body=block
    ;

string
    :   STRING_START parts=stringParts STRING_END   # NonEmptyString
    |   STRING_START STRING_END                     # EmptyString
    ;

stringParts
    :   stringPart+
    ;

stringPart
    :   chars=STRING_CHARS                      # StringChars
    |   STRING_INTERP_OPEN interp=expr RCURLY   # StringInterp
    ;

exprSeq
    :   expr (COMMA expr)*
    ;

ofType
    :   COLON typeExpr
    ;

typeExpr
    :   LPAREN params=typeExprSeq RPAREN ARROW ret=typeExpr # MultiParamFunctionType
    |   LPAREN RPAREN ARROW ret=typeExpr                    # NoParamFunctionType
    |   input=typeExpr ARROW ret=typeExpr                   # OneParamFunctionType
    |   id=typePath params=typeExprParams                   # ParameterizedType
    |   id=typePath                                         # GroundType
    ;

typePath
    :   IDENTIFIER (DOT IDENTIFIER)+    # MultiTypePath
    |   IDENTIFIER                        # SingleTypePath
    ;

typeExprWithFin
    :   fin=FIN                                             # FinType
    |   magnitude=INT                                       # FinLiteral
    |   te=typeExpr                                         # NoFin
    ;

typeExprParams
    :   LT typeExprWithFin (COMMA typeExprWithFin)* GT
    ;

typeExprSeq
    :   typeExpr (COMMA typeExpr)*
    ;