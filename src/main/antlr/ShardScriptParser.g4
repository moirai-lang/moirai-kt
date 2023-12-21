parser grammar ShardScriptParser;

options { tokenVocab=ShardScriptLexer; }

file
    :   (transientImport? | (artifactStat importStat*)) stat+ EOF
    ;

transientImport
    :   TRANSIENT IMPORT importIdSeq
    ;

artifactStat
    :   ARTIFACT importIdSeq
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
    :   DEF id=IDENTIFIER tp=typeParams? LPAREN params=paramSeq? RPAREN ret=restrictedOfType? body=block
    ;

typeParams
    :   LT typeParam (COMMA typeParam)* GT
    ;

typeParam
    : id=IDENTIFIER COLON FIN   # FinTypeParam
    | id=IDENTIFIER             # IdentifierTypeParam
    ;

paramDef
    :   id=IDENTIFIER of=ofType
    ;

restrictedParamDef
    :   id=IDENTIFIER of=restrictedOfType
    ;

paramSeq
    :   paramDef (COMMA paramDef)*
    ;

restrictedParamSeq
    : restrictedParamDef (COMMA restrictedParamDef)*
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
    :   VAL id=IDENTIFIER of=restrictedOfType       # ImmutableField
    |   MUTABLE id=IDENTIFIER of=restrictedOfType   # MutableField
    ;

expr
    :   LPAREN inner=expr RPAREN                                                                    # ParenExpr
    |   LCURLY stats=stat* RCURLY                                                                   # BlockExpr
    |   left=expr DOT id=IDENTIFIER params=restrictedTypeExprParams LPAREN args=exprSeq? RPAREN     # ParamDotApply
    |   left=expr DOT id=IDENTIFIER LPAREN args=exprSeq? RPAREN                                     # DotApply
    |   left=expr DOT id=IDENTIFIER                                                                 # DotExpr
    |   id=IDENTIFIER params=restrictedTypeExprParams LPAREN args=exprSeq? RPAREN                   # ParamApplyExpr
    |   id=IDENTIFIER LPAREN args=exprSeq? RPAREN                                                   # ApplyExpr
    |   left=expr LBRACK right=expr RBRACK                                                          # IndexExpr
    |   anyif=ifExpr                                                                                # AnyIf
    |   anylambda=lambdaDef                                                                         # AnyLambda
    |   op=NOT right=expr                                                                           # UnaryNot
    |   op=SUB right=expr                                                                           # UnaryNegate
    |   left=expr op=(MUL|DIV|MOD) right=expr                                                       # InfixMulDivMod
    |   left=expr op=(ADD|SUB) right=expr                                                           # InfixAddSub
    |   left=expr op=(GT|GTE|LT|LTE) right=expr                                                     # InfixOrder
    |   left=expr op=(IS|AS) id=typeExpr                                                            # TypeRelation
    |   left=expr op=(EQ|NEQ) right=expr                                                            # InfixEquality
    |   left=expr op=AND right=expr                                                                 # InfixAnd
    |   left=expr op=OR right=expr                                                                  # InfixOr
    |   value=DECIMAL                                                                               # LiteralDecimal
    |   value=INT                                                                                   # LiteralInt
    |   value=(BOOL_TRUE|BOOL_FALSE)                                                                # LiteralBool
    |   value=string                                                                                # StringExpr
    |   left=expr op=TO right=expr                                                                  # ToExpr
    |   id=IDENTIFIER                                                                               # RefExpr
    ;

ifExpr
    :   op=IF LPAREN condition=expr RPAREN trueb=block ELSE elif=ifExpr     # IfElseIfExpr
    |   op=IF LPAREN condition=expr RPAREN trueb=block ELSE falseb=block    # IfElseExpr
    |   op=IF LPAREN condition=expr RPAREN trueb=block                      # StandaloneIfExpr
    ;

lambdaDef
    :   op=LAMBDA LPAREN params=restrictedParamSeq? RPAREN ARROW body=expr
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
    :   LPAREN params=restrictedTypeExprSeq RPAREN ARROW ret=restrictedTypeExpr # MultiParamFunctionType
    |   LPAREN RPAREN ARROW ret=restrictedTypeExpr                              # NoParamFunctionType
    |   input=restrictedTypeExpr ARROW ret=restrictedTypeExpr                   # OneParamFunctionType
    |   id=typePath params=restrictedTypeExprParams                             # ParameterizedType
    |   id=typePath                                                             # GroundType
    ;

typePath
    :   IDENTIFIER (DOT IDENTIFIER)+        # MultiTypePath
    |   IDENTIFIER                          # SingleTypePath
    ;

restrictedOfType
    :   COLON restrictedTypeExpr
    ;

restrictedTypeExpr
    :   id=typePath params=restrictedTypeExprParams         # RestrictedParameterizedType
    |   id=typePath                                         # RestrictedGroundType
    ;

restrictedTypeExprParams
    :   LT restrictedTypeExprOrLiteral (COMMA restrictedTypeExprOrLiteral)* GT
    ;

restrictedTypeExprOrLiteral
    :   magnitude=INT                                       # FinLiteral
    |   te=restrictedTypeExpr                               # NoFin
    ;

restrictedTypeExprSeq
    :   restrictedTypeExpr (COMMA restrictedTypeExpr)*
    ;