package atem.compiler.symbols;
import atem.compiler.tools.ListMap;

/** 源码函数符号 */
public class DMethodSymbol extends BMethodSymbol
{
    public DMethodSymbol(BTypeSymbol owner, String name /*BTypeSymbol returnType,*/,boolean isStatic,boolean isPublic)
    {
        super(name,owner,isPublic,isStatic,false,false);
        this.returnType =RClassSymbolManager.ObjectSymbol;// new AnySymbol();// RClassSymbolManager.ADynamicSymbol;
        //this.methodDecl=methodDecl;
    }

    /** 方法参数表 */
    public final ListMap<DVarSymbol> parametersMap = new ListMap<>();

    /** 添加参数 */
    public void addParameter(DVarSymbol varSymbol)
    {
        parametersMap.put(varSymbol.name, varSymbol);
    }

    public int getParameterCount()
    {
        return parametersMap.size();
    }

    @Override
    public DVarSymbol getParameterSymbol(int i)
    {
        return parametersMap.get(i);
    }

    public boolean contains(String name)
    {
        return parametersMap.contains(name);
    }
}
