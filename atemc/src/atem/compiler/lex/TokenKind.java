package atem.compiler.lex;
/** 词标记类型*/
public enum TokenKind
{
    NONE,
    EOF(), //结束符号
    ERROR(), //错误
    IDENTIFIER(), //标识符
    INTLITERAL(), //整数常量类型
    FLOATLITERAL(),//float常量类型
    STRINGLITERAL(), //字符串常量类型
    //BOOLEAN("boolean"),
    //CLASS("class"),
    IMPORT("import"),
    REQUIRE("require"),
    IF("if"),
    ELSE("else"),
    //FOR("for"),
    //INT("int"),
    //NEW("new"),
    PACKAGE("package"),
    RETURN("return"),
    //VOID("void"),
    WHILE("while"),
    TRUE("true"),
    FALSE("false"),
    //THIS("this"),
    NULL("null"),
    FUNCTION("function"),
    MARCRO("macro"),
    VAR("var"),
    BREAK("break"),
    //CONTINUE("continue"),
    LPAREN("("),
    RPAREN(")"),
    LBRACKET("["),
    RBRACKET("]"),
    LBRACE("{"),
    RBRACE("}"),

    DOLLAR("$"),
    SEMI(";"),
    COMMA(","),

    DOT("."),
    EQ("="),
    GT(">"),
    LT("<"),

    NOT("!"),

    EQEQ("=="),
    LTEQ("<="),
    GTEQ(">="),
    NOTEQ("!="),
    AND("&&"),
    OR("||"),
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    COLON(":")
    ;
    /* 类型对应的名称 */
    public final String name;
    TokenKind() {
        this(null);
    }
    TokenKind(String name) {
        this.name = name;
    }

    public String toString() {
        switch (this) {
            case IDENTIFIER:
                return "token.identifier";
            case STRINGLITERAL:
                return "token.string";
            case INTLITERAL:
                return "token.integer";
            case FLOATLITERAL:
                return "token.float";
            case ERROR:
                return "token.error";
            case EOF:
                return "token.end-of-input";
            case DOT: case COMMA: case SEMI:
            case LPAREN: case RPAREN:
            case LBRACKET: case RBRACKET:
            case LBRACE: case RBRACE:
                return "'" + name + "'";
            default:
                return name;
        }
    }
}
