package atem.compiler.tools;

import atem.compiler.symbols.SymbolUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ConstructorFinder {

    public static ArrayList<Constructor> finds(Class<?> clazz , Class<?>[] argTypes )
    {
        ArrayList<Constructor> arrayList = finds(clazz);
        ArrayList<Constructor> arrayList1 = filterMethods(arrayList,argTypes);
        return  arrayList1;
    }

    public static ArrayList<Constructor> finds(Class<?> clazz , int argsCount )
    {
        ArrayList<Constructor> arrayList = finds(clazz);
        ArrayList<Constructor> arrayList1 = filterMethods(arrayList,argsCount);
        return  arrayList1;
    }

    public static ArrayList<Constructor> finds(Class<?> clazz )
    {
        ArrayList<Constructor> arrayList = new ArrayList<>();
        /* 第1步:搜索方法 */
        Constructor[] methods = clazz.getConstructors();
        for(Constructor method :methods)
        {
            if( Modifier.isPublic(method.getModifiers()))
            {
                arrayList.add(method);
            }
        }
        return arrayList;
    }

    /* 验证函数符号参数与指定的参数类型匹配度 */
   /* public static int matchMethod(Constructor methodSymbol, Class<?>[] argTypes)
    {
        int paramCount = methodSymbol.getParameterCount();
        // 1: 比较参数个数是否相同
        if(argTypes.length!=paramCount)
            return -1;
        //2 : 如果参数个数都为0，则都是匹配的
        if(paramCount==0)
            return 0;
        else {
            Class<?>[] methodParameterTypes  = methodSymbol.getParameterTypes();
            // 比较每个参数的匹配度，并累加；但是只要有一个参数不匹配，则这些函数都是不匹配的，直接返回-1
            int sum = 0;
            for (int i = 0; i < paramCount; i++) {
                Class<?> argtypeSymbol = argTypes[i];
                Class<?> methodParameterType = methodParameterTypes[i];
                int k = ClazzUtil.matchAssignableClass(methodParameterType, argtypeSymbol);
                if (k < 0)
                    return -1;
                else
                    sum += k;
            }
            return sum;
        }
    }*/

    public static ArrayList<Constructor> filterMethods(ArrayList<Constructor> constructors , Class<?>[] argTypes)
    {
        /* 根据参数类型匹配度筛选 */
        ArrayList<Constructor> methods2 = new MethodsFilterByArgsTypes(constructors,argTypes).filter();
        if(methods2.size()<=1)
            return methods2;
        return methods2;
        /* 根据函数返回类型匹配度筛选 */
        //ArrayList<Constructor> methods3 = new MethodsFilterByReturnType(methods2).filter();
     //   return methods3;
    }


    public static ArrayList<Constructor> filterMethods(ArrayList<Constructor> constructors ,int argsCount)
    {
        ArrayList<Constructor> list = new ArrayList<>();
        for (var item :constructors)
        {
            if(item.getParameters().length==argsCount)
                list.add(item);
        }
        return list;
    }

    /* 筛选父类 */
    static  abstract class MethodsFilter
    {
        abstract  ArrayList<Constructor> getMethods();
        abstract int getRate(Constructor methodSymbol);

        public  ArrayList<Constructor> filter()
        {
            Integer minRate =null;
            Map<Constructor, Integer> map = new HashMap<>();
            for (Constructor symbol : getMethods()) {
                Constructor methodSymbol = (Constructor) symbol;
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
            ArrayList<Constructor> methodSymbolsNew = new ArrayList<>();
            for (Constructor methodSymbol : map.keySet()) {
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
        ArrayList<Constructor> methods;
        Class<?>[] argTypes;
        //ArrayList<BTypeSymbol> argTypes;
        public MethodsFilterByArgsTypes(ArrayList<Constructor> methods, Class<?>[] argTypes)
        {
            this. methods = methods;
            this.argTypes=argTypes;
        }

        ArrayList<Constructor> getMethods()
        {
            return methods;
        }

        int getRate(Constructor constructor)
        {
          return  ClazzUtil.matchTypes(constructor.getParameterTypes(),argTypes);
        }
    }

    /** 根据函数返回类型筛选 */
  /*  static class MethodsFilterByReturnType extends MethodsFilter
    {
        ArrayList<Constructor> methods;
        public MethodsFilterByReturnType(ArrayList<Constructor> methods)
        {
            this.methods=methods;
        }

        ArrayList<Constructor> getMethods()
        {
            return methods;
        }

        int getRate(Constructor constructor)
        {
            return SymbolUtil.getExtendsDeep(constructor.getDeclaringClass());
        }
    }*/
}
