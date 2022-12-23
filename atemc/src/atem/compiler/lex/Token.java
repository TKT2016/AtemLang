package atem.compiler.lex;

import atem.compiler.utils.msgresources.CompileMessagesUtil;

public class Token {
    /** 类型 */
    public final TokenKind kind;
    /** 开始位置 */
    public final int pos;
    /** 行号 */
    public final int line;
    /** 结束位置 */
    public final int endPos;

    public int getSize()
    {
        int size = endPos-pos;
        if(size<=0)
            size=1;
        return size;
    }

    /* 标识符名称(如果kind是IDENTIFIER类型) */
    public String identName;

    /* 字面常量值(如果kind是STRINGLITERAL或INTLITERAL) */
    public String valueString;

    private Token(TokenKind kind, int line,int pos, int endPos) {
        this.kind = kind;
        this.pos = pos;
        this.endPos = endPos;
        this.line=line;
    }

    public static Token createLiteral(TokenKind kind,int line, int pos, int endPos, String stringVal) {
        Token token = new Token(kind, line,pos, endPos);
        token.valueString = stringVal;
        return token;
    }

    public static Token createNamed(TokenKind kind,int line, int pos, int endPos, String name) {
        Token token = new Token(kind, line,pos, endPos);
        token.identName = name;
        return token;
    }

    public static Token createNormal(TokenKind kind,int line, int pos, int endPos) {
        Token token = new Token(kind,  line,pos, endPos);
        token.identName = TokenKindsMap.lookupName(kind);
        return token;
    }

    public static Token createEOF(int line, int pos) {
        Token token = new Token(TokenKind.EOF,  line,pos, pos);
        token.identName = "";
        return token;
    }

    @Override
    public String toString()
    {
        String str = getTextShow();
        return str+"  "+line+ CompileMessagesUtil.getLineWord() +" "+ pos+":"+identName;
    }

    private String getTextShow()
    {
        if(kind== TokenKind.STRINGLITERAL)
            return '"'+ valueString +'"';
        if(kind== TokenKind.INTLITERAL)
            return valueString ;
        if(kind== TokenKind.FLOATLITERAL)
            return valueString ;
        if(kind== TokenKind.IDENTIFIER)
            return identName;
        if(kind== TokenKind.ERROR)
            return "(token.error)";
        if(kind== TokenKind.EOF)
            return "(token.eof)";
        if(identName !=null)
            return kind.name;
        return  " ";
    }
}
