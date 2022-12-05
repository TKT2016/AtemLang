package atem.compiler.symbols;

/** 符号基类 */
public abstract class Symbol {
    /** 符号名称 */
    public String name;

    public Symbol(String name)
    {
        this.name=name;
    }
    
    /** 获取符号的类型 */
    public final BTypeSymbol getTypeSymbol()
    {
        return SymbolUtil.getSymbolType(this);
    }

    public String toString() {
        return name;
    }
}
