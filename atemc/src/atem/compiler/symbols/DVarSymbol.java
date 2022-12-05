package atem.compiler.symbols;

import atem.compiler.ast.callables.ICallableAST;
import atem.compiler.utils.Debuger;

/** 源码变量符号(包括局部变量、方法参数) */
public class DVarSymbol extends BVarSymbol
{
    public IDimCallable dimTree;
    public final int dimPos;
    public DVarSymbol( String name,VarSymbolKind kind , BTypeSymbol varType, IDimCallable dimTree,boolean isStatic, int dimPos) {
        super(name,kind);
        this.varType = varType;
        this.isPublic = true; //默认public
        this.isStatic = isStatic; //默认static
        this.writable = true; //默认可写
        this.dimTree = dimTree;
        this.dimPos = dimPos;
    }

    public boolean isLambdaRefVar;
    public boolean isLambdaParameter;
    public boolean isMacroCallArg;

    public boolean isTempVar;
    public int adr_lambda=-1;

    public boolean isEmitLocalVarRef()
    {
        if(this.varKind!=VarSymbolKind.field)
        {
            if(isLambdaRefVar || isMacroCallArg)
                return true;
        }
        return false;
    }

    /* 用于字节码生成阶段 */
    public int adr = -1; //变量地址

    public interface IDimCallable
    {
        ICallableAST getDefedCallableAST();
        SymbolScope getScope();
    }
}
