package atem.compiler.ast.callables;

import atem.compiler.symbols.*;
import org.objectweb.asm.Label;

public interface ICallableAST {
  /*  public void setRetVarSymbol(DVarSymbol retVarSymbol);

    public DVarSymbol getRetVarSymbol();

    public void setLambdaVarArraySymbol(DVarSymbol lambdaVarArraySymbol);

    public DVarSymbol getLambdaVarArraySymbol();

    public SymbolScope getScope();

    public int getLambdaVarCount();

    public void setLambdaVarCount(int count);

  */

/*
    public BMethodSymbol getMethodSymbol();
    */
    public CallableCinfo getCallableCinfo();

    public SymbolScope getScope();

    public BTypeSymbol getRetType();
}
