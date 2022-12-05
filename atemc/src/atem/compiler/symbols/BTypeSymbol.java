package atem.compiler.symbols;
import java.util.ArrayList;

/** 类型符号基类 */
public abstract class BTypeSymbol extends Symbol {
    /** 是否是接口类型 */
    public final boolean isInterface;
    /** 访问修饰符是否是public的 */
    public final boolean isPublic;

    public BTypeSymbol(String name,  boolean isInterface, boolean isPublic)
    {
        super(name);
        this.isInterface = isInterface;
        this.isPublic = isPublic;
    }
    /** 根据名称搜索字段 */
    public abstract BVarSymbol findField(String name);

    /** 根据名称查找成员(即字段和方法) */
    public abstract ArrayList<Symbol> findMembers(String name);
}
