package atem.compiler.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class SignatureUtil {

    /**  生成函数参数列表及返回值的签名 */
    public  static  String getSignature(Method method )
    {
        boolean insertL = true;
        final StringBuffer buf = new StringBuffer();
        buf.append("(");

        var argTypes = method.getParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            Class<?> argType =argTypes[i];
                String singature = getSignature(argType,(insertL));
                buf.append(singature);
        }
        buf.append(")");
        buf.append( getSignature( method.getReturnType(),(insertL)));
        return buf.toString();
    }

    public  static  String getSignature(Constructor constructor )
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        var argTypes = constructor.getParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            Class<?> argType =argTypes[i];
            String singature = getSignature(argType,(true));
            buf.append(singature);
        }
        buf.append(")");
        buf.append( "V");
        return buf.toString();
    }

    public static String getSignature(Class<?> clazz )
    {
        return getSignature(clazz,false);
    }

    public static String getSignature(Class<?> clazz, boolean insertL)
    {
        if (int.class.equals(clazz)) {
            return "I";
        }
        else if (void.class.equals(clazz)) {
            return "V";
        }
        else if (boolean.class.equals(clazz)) {
            return "Z";
        }
        else if (char.class.equals(clazz)) {
            return "C";
        }
        else  if (byte.class.equals(clazz)) {
            return "B";
        }
        else if (short.class.equals(clazz)) {
            return "S";
        }
        else if (float.class.equals(clazz)) {
            return "F";
        }
        else  if (long.class.equals(clazz)) {
            return "J";
        }
        else  if (double.class.equals(clazz)) {
            return "D";
        }
        else
        {
            String nameFull = clazz.getName();
            String name2 = nameFull.replaceAll("\\.", "/");
            if(insertL &&! name2.startsWith("["))
                return warpL(name2);
            else
                return name2;
            /*String lstr = insertL?"L":"";
            String elstr = insertL?";":"";
            return lstr + name2+elstr;*/
        }
    }

    public static String warpL(String sign  )
    {
        if(sign.startsWith("["))
            return sign;
        else
            return "L" + sign+";";
    }

    public static String nameToSign(String classFullName)
    {
        String  str = classFullName.replace(".", "/");
        return str;
    }
}
