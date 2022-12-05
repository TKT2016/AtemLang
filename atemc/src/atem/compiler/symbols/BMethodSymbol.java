package atem.compiler.symbols;

/** 函数符号基类 */
public abstract class BMethodSymbol extends Symbol
{
    public BTypeSymbol ownerType; //函数所属类型
    public final boolean isPublic;//是否是public
    public final boolean isStatic;// 是否是静态的
    public final boolean isConstructor; // 是否是构造函数
    public BTypeSymbol returnType; // 返回结果类型
    public boolean isSelfFirst;

    protected BMethodSymbol(String name, BTypeSymbol owner, boolean isPublic, boolean isStatic, boolean isConstructor,boolean isSelfFirst)
    {
        super(name);
        ownerType = owner;
        this.isPublic = isPublic;
        this.isStatic = isStatic;
        this.isConstructor = isConstructor;
        this.isSelfFirst = isSelfFirst;
    }

    /** 获取参数个数 */
    public abstract int getParameterCount();

    /** 获取第i个参数符号 */
    public abstract BVarSymbol getParameterSymbol(int i);

}
