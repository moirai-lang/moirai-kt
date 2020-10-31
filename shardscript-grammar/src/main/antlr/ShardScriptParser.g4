parser grammar ShardScriptParser;

options { tokenVocab=ShardScriptLexer; }

file
    :   importStat* stat+ EOF
    ;

importStat
    :   IMPORT importIdSeq
    ;

importIdSeq
    :   contextualId (DOT contextualId)*
    ;

stat
    :   letStat
    |   forStat
    |   funDefStat
    |   objectDefStat
    |   recordDefStat
    |   enumDefStat
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
    :   VAL id=contextualId of=ofType? ASSIGN right=expr     # ImmutableLet
    |   MUTABLE id=contextualId of=ofType? ASSIGN right=expr # MutableLet
    ;

forStat
    :   FOR LPAREN id=contextualId of=ofType? IN source=expr RPAREN body=block
    ;

funDefStat
    :   DEF id=contextualId tp=typeParams? LPAREN params=paramSeq? RPAREN ret=ofType? body=block
    ;

typeParams
    :   LT typeParam (COMMA typeParam)* GT
    ;

typeParam
    :   contextualId  # IdentifierTypeParam
    |   OMICRON     # OmicronTypeParam
    ;

paramDef
    :   id=contextualId of=ofType
    ;

paramSeq
    :   paramDef (COMMA paramDef)*
    ;

objectDefStat
    :   OBJECT id=contextualId
    ;

recordDefStat
    :   RECORD id=contextualId tp=typeParams? LPAREN fields=fieldSeq RPAREN
    ;

fieldSeq
    :   fieldDef (COMMA fieldDef)*
    ;

fieldDef
    :   VAL id=contextualId of=ofType     # ImmutableField
    |   MUTABLE id=contextualId of=ofType # MutableField
    ;

enumDefStat
    :   ENUM id=contextualId tp=typeParams? body=enumDefBody
    ;

enumDefBody
    :   LCURLY stats=enumDefBodyStat* RCURLY
    ;

enumDefBodyStat
    :   recordDefStat
    |   objectDefStat
    ;

expr
    :   LPAREN inner=expr RPAREN                                                        # ParenExpr
    |   left=expr DOT id=contextualId params=typeExprParams LPAREN args=exprSeq? RPAREN   # ParamDotApply
    |   left=expr DOT id=contextualId LPAREN args=exprSeq? RPAREN                         # DotApply
    |   left=expr DOT id=contextualId                                                     # DotExpr
    |   id=contextualId params=typeExprParams LPAREN args=exprSeq? RPAREN                 # ParamApplyExpr
    |   id=contextualId LPAREN args=exprSeq? RPAREN                                       # ApplyExpr
    |   left=expr LBRACK right=expr RBRACK                                              # IndexExpr
    |   anyif=ifExpr                                                                    # AnyIf
    |   op=NOT right=expr                                                               # UnaryNot
    |   op=SUB right=expr                                                               # UnaryNegate
    |   MAP LPAREN id=contextualId of=ofType? IN source=expr RPAREN body=block            # MapExpr
    |   FLATMAP LPAREN id=contextualId of=ofType? IN source=expr RPAREN body=block        # FlatMapExpr
    |   SWITCH LPAREN source=expr RPAREN alternatives=switchBody                        # SwitchExpr
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
    |   id=contextualId                                                                   # RefExpr
    ;

ifExpr
    :   op=IF LPAREN condition=expr RPAREN trueb=block ELSE elif=ifExpr     # IfElseIfExpr
    |   op=IF LPAREN condition=expr RPAREN trueb=block ELSE falseb=block    # IfElseExpr
    |   op=IF LPAREN condition=expr RPAREN trueb=block                      # StandaloneIfExpr
    ;

switchBody
    :   LCURLY enumCase+ elseCase? RCURLY
    ;

enumCase
    :   CASE id=contextualId body=block
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
    :   contextualId (DOT contextualId)+    # MultiTypePath
    |   contextualId                      # SingleTypePath
    ;

typeExprWithOmicron
    :   omicron=OMICRON                                     # OmicronType
    |   magnitude=INT                                       # OmicronLiteral
    |   te=typeExpr                                         # NoOmicron
    ;

typeExprParams
    :   LT typeExprWithOmicron (COMMA typeExprWithOmicron)* GT
    ;

typeExprSeq
    :   typeExpr (COMMA typeExpr)*
    ;

contextualId
    :   WHERE
    |   SELECT
    |   GROUP
    |   INTO
    |   ORDERBY
    |   JOIN
    |   ASCENDING
    |   DESCENDING
    |   ON
    |   IDENTIFIER
    ;
