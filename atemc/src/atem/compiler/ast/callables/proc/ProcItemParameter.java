package atem.compiler.ast.callables.proc;

import atem.compiler.lex.Token;

public class ProcItemParameter extends ProcItem {
   // public MetaItemParameterKind kind;
    //public String suffixIdent;
   private final Token ident;
    public final String name;

    public ProcItemParameter(Token ident)
    {
        this.ident = ident;
        this.name = ident.identName;
        posToken = ident;
    }

    public String toString()
    {
        return "("+ name+")";
    }
/*
    public enum MetaItemParameterKind
    {
        Ident,
        Expr,
        Stmt
    }*/
}
