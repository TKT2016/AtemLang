package atem.compiler.ast.callables;

import atem.compiler.symbols.DVarSymbol;
import atem.compiler.symbols.SymbolScope;

public class CallableCinfo {

    public DVarSymbol retVarSymbol;

    //public DVarSymbol varSymbol;

    public DVarSymbol lambdaVarArraySymbol;

    //public SymbolScope scope;

    public int maxLambdaVarCount;

    public void setMaxLambdaVarCount(int count)
    {
        var maxAdr = Math.max(maxLambdaVarCount, count);
        maxLambdaVarCount = maxAdr;
    }

    public int maxStack;

    public void setMaxStack(int count)
    {
        var maxAdr = Math.max(maxStack, count);
        maxStack = maxAdr;
    }

    public int  maxLocals;

    public void setMaxLocals(int count)
    {
        var maxAdr = Math.max(maxLocals, count);
        maxLocals = maxAdr;
    }
}
