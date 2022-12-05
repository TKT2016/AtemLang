package atem.compiler.symbols;

import atem.lang.rt.SelfFunction;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RMethodSymbolManager {

    /** 缓存 */
    private static HashMap<Object, RMethodSymbol> cacheMap = new HashMap<>();

    /** 根据方法Method获取方法符号 */
    public static RMethodSymbol getSymbol(Method method, BTypeSymbol owner)
    {
        if(cacheMap.containsKey(method))
            return cacheMap.get(method);
        int modifiers = method.getModifiers();
        boolean isPublic = Modifier.isPublic(modifiers);
        boolean isStatic = Modifier.isStatic(modifiers);
        String methodName = method.getName();
        ArrayList<RVarSymbol> parameters = createParameters( method.getParameters());
        boolean isSelfFirst = (method.isAnnotationPresent(SelfFunction.class ));
        RMethodSymbol symbol = new RMethodSymbol(methodName , owner, parameters,isPublic,isStatic,false,isSelfFirst);
        /* 函数符号类型是函数返回值对应符号 */
        symbol.returnType = RClassSymbolManager.getSymbol(method.getReturnType());
        symbol.member = method;
        cacheMap.put(method,symbol);
        return symbol;
    }

    /** 缓存 */
    private static HashMap<Object, RMacroSymbol> macroMacheMap = new HashMap<>();

    /** 根据方法Method获取方法符号 */
    public static RMacroSymbol getMacroSymbol(Method method, BTypeSymbol owner)
    {
        if(macroMacheMap.containsKey(method))
            return macroMacheMap.get(method);
       // int modifiers = method.getModifiers();
        //boolean isPublic = Modifier.isPublic(modifiers);
       // boolean isStatic = Modifier.isStatic(modifiers);
        //String methodName = method.getName();
        //ArrayList<RVarSymbol> parameters = createParameters( method.getParameters());
        RMacroSymbol symbol = new RMacroSymbol( owner, method);
       // symbol.member = method;
        /* 函数符号类型是函数返回值对应符号 */
        //symbol.returnType = RClassSymbolManager.getSymbol(method.getReturnType());
        //symbol.member = method;
        macroMacheMap.put(method,symbol);
        return symbol;
    }

    /** 根据构造函数获取方法符号 */
    public static RMethodSymbol getSymbol(Constructor constructor, BTypeSymbol owner)
    {
        if(cacheMap.containsKey(constructor))
            return cacheMap.get(constructor);
        int modifiers = constructor.getModifiers();
        boolean isPublic = Modifier.isPublic(modifiers);
        boolean isStatic = Modifier.isStatic(modifiers);
        ArrayList<RVarSymbol> parameters = createParameters( constructor.getParameters());
        /* <init> 是jvm中构造函数的默认名称 */
        RMethodSymbol symbol = new RMethodSymbol( "<init>",owner,parameters,isPublic,isStatic,true,false);
        symbol.member = constructor;
        /* 函数符号类型是构造函数所属的对应符号 */
        symbol.returnType = RClassSymbolManager.getSymbol(constructor.getDeclaringClass());
        symbol.member = constructor;
        cacheMap.put(constructor,symbol);
        return symbol;
    }

    public static ArrayList<RVarSymbol> createParameters(Parameter[] parameters )
    {
        ArrayList<RVarSymbol> params = new ArrayList<>();
        for(int i=0;i< parameters.length;i++)
        {
            Parameter parameter = parameters[i];
            RVarSymbol varSymbol = RVarSymbolManager.getSymbol(parameter);
            params.add(varSymbol);
        }
        return params;
    }
}
