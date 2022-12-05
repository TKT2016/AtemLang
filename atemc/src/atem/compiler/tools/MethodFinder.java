package atem.compiler.tools;

import atem.compiler.symbols.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class MethodFinder {

    public static ArrayList<Method> findMethods(Class<?> clazz, String name , Class<?>[] argTypes )
    {
        ArrayList<Method> arrayList =findMethods(clazz,name);
        ArrayList<Method> arrayList1 = filterMethods(arrayList,argTypes);
        return  arrayList1;
    }

    public static Method findMethod(Class<?> clazz, String name )
    {
        ArrayList<Method> arrayList =findMethods(clazz,name);
        if(arrayList.size()==0)
            return null;
        return arrayList.get(0);
    }

    public static ArrayList<Method> findMethods(Class<?> clazz, String name )
    {
        ArrayList<Method> arrayList = new ArrayList<>();
        /* 第1步:搜索方法 */
        Method[] methods = clazz.getMethods();
        for(Method method :methods)
        {
            if(method.getName().equals(name) && Modifier.isPublic(method.getModifiers()))
            {
                arrayList.add(method);
            }
        }
        return arrayList;
    }

    public static ArrayList<Method> findMethods(Class<?> clazz, String name,int argsCount )
    {
        ArrayList<Method> arrayList = new ArrayList<>();
        /* 第1步:搜索方法 */
        Method[] methods = clazz.getMethods();
        for(Method method :methods)
        {
            if(method.getName().equals(name) && Modifier.isPublic(method.getModifiers()))
            {
                if(method.getParameters().length==argsCount)
                    arrayList.add(method);
            }
        }
        return arrayList;
    }

    public static ArrayList<Method> filterMethods(ArrayList<Method> methods , Class<?>[] argTypes)
    {
        /* 根据参数类型匹配度筛选 */
        ArrayList<Method> methods2 = new MethodsFilterByArgsTypes(methods,argTypes).filter();
        if(methods2.size()<=1)
            return methods2;
        /* 根据函数返回类型匹配度筛选 */
        ArrayList<Method> methods3 = new MethodsFilterByReturnType(methods2).filter();
        return methods3;
    }

    /* 筛选父类 */
    static  abstract class MethodsFilter
    {
        abstract  ArrayList<Method> getMethods();
        abstract int getRate(Method methodSymbol);

        public  ArrayList<Method> filter()
        {
            Integer minRate =null;
            Map<Method, Integer> map = new HashMap<>();
            for (Method symbol : getMethods()) {
                Method methodSymbol = (Method) symbol;
                int rate =getRate(methodSymbol);
                if(rate>=0)
                {
                    map.put(methodSymbol,rate);
                    if(minRate==null)
                        minRate = rate;
                    else
                        minRate = Math.min(minRate,rate);
                }
            }
            ArrayList<Method> methodSymbolsNew = new ArrayList<>();
            for (Method methodSymbol : map.keySet()) {
                Integer rate = map.get(methodSymbol);
                if(rate==minRate)
                    methodSymbolsNew.add(methodSymbol);
            }
            return methodSymbolsNew;
        }
    }

    /** 根据函数参数类型筛选 */
    static  class MethodsFilterByArgsTypes extends MethodsFilter
    {
        ArrayList<Method> methods;
        Class<?>[] argTypes;
        //ArrayList<BTypeSymbol> argTypes;
        public MethodsFilterByArgsTypes(ArrayList<Method> methods, Class<?>[] argTypes)
        {
            this. methods = methods;
            this.argTypes=argTypes;
        }

        ArrayList<Method> getMethods()
        {
            return methods;
        }

        int getRate(Method method)
        {
            return  ClazzUtil.matchTypes(method.getParameterTypes(),argTypes);
        }
    }

    /** 根据函数返回类型筛选 */
    static class MethodsFilterByReturnType extends MethodsFilter
    {
        ArrayList<Method> methods;
        public MethodsFilterByReturnType(ArrayList<Method> methods)
        {
            this.methods=methods;
        }

        ArrayList<Method> getMethods()
        {
            return methods;
        }

        int getRate(Method methodSymbol)
        {
            return SymbolUtil.getExtendsDeep(methodSymbol.getReturnType());
        }
    }
}
