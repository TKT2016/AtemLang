package atem.compiler.symbols;
import atem.compiler.utils.CompileError;

import java.lang.reflect.*;
import java.util.*;

/** 反射得到的函数符号 */
public class RMethodSymbol extends BMethodSymbol {
    Object member; //可能为方法Method或者构造函数Constructor
    private ArrayList<RVarSymbol> params ; //参数列表
    public Executable fn;

    RMethodSymbol(String name, BTypeSymbol owner, ArrayList<RVarSymbol> params,boolean isPublic, boolean isStatic, boolean isConstructor,boolean isSelfFirst)
    {
        super( name, owner, isPublic, isStatic, isConstructor,isSelfFirst);
        this.params = params;
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
}
