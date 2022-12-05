package atem.compiler.ast.callables.proc;


import atem.compiler.lex.Token;

public class ProcItemText extends ProcItem {
    private Token ident;

    public ProcItemText(Token ident)
    {
        this.ident = ident;
        posToken = ident;
    }

    public String toString()
    {
        return ident.identName;
    }

    public String getText()
    {
        return ident.identName;
    }

}
