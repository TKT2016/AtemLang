package atem.compiler.symbols;

import atem.compiler.analyzers.MacroDescData;

public abstract class MacroSymbol extends  Symbol {
    public BTypeSymbol ownerType; //函数所属类型

    protected MacroSymbol(BTypeSymbol owner,String name)
    {
        super(name);
        ownerType = owner;
    }

    /** 获取参数个数 */
    public abstract int getParameterCount();

    /** 获取第i个参数符号 */
    public abstract BVarSymbol getParameterSymbol(int i);

    public final  BTypeSymbol returnType =RClassSymbolManager.ObjectSymbol;

    public abstract MacroDescData getMacroData();
}
