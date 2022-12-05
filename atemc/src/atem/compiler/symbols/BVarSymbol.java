package atem.compiler.symbols;

/** 变量符号基类(包括局部变量、字段、方法参数、数组长度字段) */
public abstract class BVarSymbol extends Symbol
{
    public VarSymbolKind varKind; //变量符号类型
    public BTypeSymbol varType; //变量类型
    public boolean isPublic; //是否是public
    public boolean isStatic; // 是否是静态的
    public boolean writable; //是否可写
    public BTypeSymbol ownerType; //所属类型(当它是字段符号时有意义)

    protected BVarSymbol(String name,VarSymbolKind kind)
    {
        super(name);
        this.varKind = kind;
    }
}