package atem.compiler.tools;

import java.lang.reflect.Field;

public abstract class ClazzUtil {
    public static Class<?>[] getArgTypes(Object[] args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for(int i=0;i<args.length;i++)
        {
            if( args[i] ==null)
                argTypes[i] = Object.class;
            else
                argTypes[i] = args[i].getClass();
        }
        return argTypes;
    }
    public static Field findField(Class<?> clazz,String name)
    {
        for(Field field : clazz.getFields())
        {
            if(field.getName().equals(name))
                return field;
        }
        return null;
    }

    /** 计算a赋值为b的匹配度(-1:不匹配,0:相同,其它数值:继承多少层) */
    public static int matchAssignableClass(Class<?> a,Class<?> b)
    {
        if(a.equals(b)) return 0;
        if(isBooleanClass(a)&&isBooleanClass(b)) return 0;
        if(isInt(a)&&isInt(b)) return 0;
        if(isFloat(a)&&isFloat(b)) return 0;

        if(! a.isAssignableFrom((b))) return -1;
        if(b.isInterface()) return 1;

        int i= 0 ;
        Class<?> temp =b;
        while (temp!=null)
        {
            if(a .equals(temp))
                break;
            temp = temp.getSuperclass();
            i++;
        }
        return i;
    }

    private static boolean isFloat (Class<?> clazz)
    {
        if(clazz.equals(float.class))
            return true;
        if(clazz.equals(Float.class))
            return true;
        return false;
    }

    private static boolean isInt (Class<?> clazz)
    {
        if(clazz.equals(int.class))
            return true;
        if(clazz.equals(Integer.class))
            return true;
        return false;
    }

    private static boolean isBooleanClass(Class<?> clazz)
    {
        if(clazz.equals(boolean.class))
            return true;
        if(clazz.equals(Boolean.class))
            return true;
        return false;
    }

    /* 验证函数符号参数与指定的参数类型匹配度 */
    public static int matchTypes(Class<?>[] leftArgTypes, Class<?>[] argTypes)
    {
        int paramCount =leftArgTypes.length;
        // 1: 比较参数个数是否相同
        if(argTypes.length!=paramCount)
            return -1;
        //2 : 如果参数个数都为0，则都是匹配的
        if(paramCount==0)
            return 0;
        else {
            //Class<?>[] methodParameterTypes  = methodSymbol.getParameterTypes();
            // 比较每个参数的匹配度，并累加；但是只要有一个参数不匹配，则这些函数都是不匹配的，直接返回-1
            int sum = 0;
            for (int i = 0; i < paramCount; i++) {
                Class<?> argtypeSymbol = argTypes[i];
                Class<?> methodParameterType = leftArgTypes[i];
                int k = ClazzUtil.matchAssignableClass(methodParameterType, argtypeSymbol);
                if (k < 0)
                    return -1;
                else
                    sum += k;
            }
            return sum;
        }
    }
}
