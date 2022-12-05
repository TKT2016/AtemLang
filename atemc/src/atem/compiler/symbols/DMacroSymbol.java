package atem.compiler.symbols;
import atem.compiler.analyzers.MacroDescData;
import atem.compiler.tools.ListMap;
import atem.compiler.ast.callables.JCMacroDecl;

/** 源码函数符号 */
public class DMacroSymbol extends MacroSymbol
{
    public JCMacroDecl macroDecl;

    public DMacroSymbol(BTypeSymbol owner, String name, JCMacroDecl macroDecl)
    {
        super(owner,name);
       // this.returnType =RClassSymbolManager.ObjectSymbol;
        this.macroDecl = macroDecl;
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

    public MacroDescData getMacroData()
    {
       return macroDecl.getMacroData();
    }
}
