package atem.compiler.symbols;

import atem.compiler.analyzers.MacroDescData;
import atem.compiler.utils.CompileError;
import atem.lang.rt.Macro;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class RMacroSymbol extends MacroSymbol
{
    Method method;
    private ArrayList<RVarSymbol> params ;
    public RMacroSymbol(BTypeSymbol owner,  Method method)
    {
        super(owner, method.getName());
        this.params = RMethodSymbolManager.createParameters(method.getParameters());
        this. method = method;
    }
    @Override
    public int getParameterCount()
    {
        return params.size();
    }

    @Override
    public BVarSymbol getParameterSymbol(int i)
    {
        if(i>params.size()-1)
            throw new CompileError();
        return params.get(i);
    }

    private MacroDescData macroDescData;
    public MacroDescData getMacroData()
    {
       // if(macroDescData!=null) return macroDescData;
       // if(! isMacro()) return null;
       // Method method= (Method) member;
        Macro macro = method.getAnnotation(Macro.class);
        macroDescData = new MacroDescData(macro.value());
        return macroDescData;
    }
}
