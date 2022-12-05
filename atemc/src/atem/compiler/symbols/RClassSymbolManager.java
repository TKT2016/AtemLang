package atem.compiler.symbols;
import atem.compiler.CompileContext;
import atem.lang.rt.VarRef;
import atem.lang.rt.ObjectDotMember;
import atem.lang.rt.TypeLiteral;
import atem.lang.Map;
import atem.lang.Prototype;
import atem.lang.List;
import atem.lang.rt.Pair;

import java.util.HashMap;
/** 反射类型符号管理器*/
public abstract class RClassSymbolManager {
    /** void类型符号 */
    public static final RClassSymbol voidPrimitiveSymbol;
    /** boolean基本类型符号 */
    public static final RClassSymbol booleanPrimitiveSymbol;
    /** int 基本类型符号 */
    public static final RClassSymbol intPrimitiveSymbol;

    public static final RClassSymbol floatSymbol;

    /** String 基本类型符号 */
    public static final RClassSymbol StringSymbol;

    /** Object 基本类型符号 */
    public static final RClassSymbol ObjectSymbol;

    public static final RClassSymbol AListSymbol;

    public static final RClassSymbol DotMeberClassSymbol;

    public static final RClassSymbol IAddrRefSymbol;

    public static final RClassSymbol TypeLiteralClassSymbol;

    public static final RClassSymbol DynamicClassSymbol;

    public static final RClassSymbol PairClassSymbol;

    public static final RClassSymbol AMapSymbol;

    /** 初始化时创建常用类型并保存 */
    static {
        cache = new HashMap<>();

        voidPrimitiveSymbol = new RClassSymbol(void.class);
        booleanPrimitiveSymbol = new RClassSymbol(boolean.class);
        intPrimitiveSymbol = new RClassSymbol(int.class);
        StringSymbol  = new RClassSymbol(String.class);
        ObjectSymbol  = new RClassSymbol(Object.class);

        putSymbol(voidPrimitiveSymbol);
        putSymbol(booleanPrimitiveSymbol);
        putSymbol(intPrimitiveSymbol);
        putSymbol(StringSymbol);
        putSymbol(ObjectSymbol);

        floatSymbol  = new RClassSymbol(float.class);
        putSymbol(floatSymbol);

        DotMeberClassSymbol = new RClassSymbol(ObjectDotMember.class);
        putSymbol(DotMeberClassSymbol);

        AListSymbol  = new RClassSymbol(List.class);
        putSymbol(AListSymbol);

        IAddrRefSymbol = new RClassSymbol(VarRef.class);
        putSymbol(IAddrRefSymbol);

        TypeLiteralClassSymbol = new RClassSymbol(TypeLiteral.class);
        putSymbol(TypeLiteralClassSymbol);

        DynamicClassSymbol = new RClassSymbol(Prototype.class);
        putSymbol(DynamicClassSymbol);

        PairClassSymbol = new RClassSymbol(Pair.class);
        putSymbol(PairClassSymbol);

        AMapSymbol = new RClassSymbol(Map.class);
        putSymbol(AMapSymbol);

      /*  Object[] obArr = new Object[0];
       // ObjectArraySymbol = new RClassSymbol( obArr.getClass());
        ObjectArraySymbol =  BArrayTypeSymbol.getSymbol(ObjectSymbol);
        putSymbol(ObjectArraySymbol);*/
    }
    /** 缓存 */
    private static HashMap<Class<?>, RClassSymbol> cache;
    /** 使用反射从类名称获取Class符号 */
    public static RClassSymbol forName(String classFullName , CompileContext compileContext)
    {
        Class<?> clazz =null;
        try {
             clazz = Class.forName(classFullName);
        }
        catch (ClassNotFoundException e)
        {
            //Debuger.outln("ClassNotFound forName 59:"+e.getMessage());
        }
        catch (NoClassDefFoundError e)
        {
           // Debuger.outln("NoClassDefFoundError forName 63:"+e.getMessage());
            //throw e;
        }

        for (ClassLoader classLoader:compileContext.urlClassLoaders)
        {
            try {
                clazz  = classLoader.loadClass(classFullName);
            }
            catch (ClassNotFoundException e)
            {
               // Debuger.outln("forName 69:"+e.getMessage());
            }
            catch (NoClassDefFoundError e)
            {
                // Debuger.outln("NoClassDefFoundError forName 63:"+e.getMessage());
                //throw e;
            }
        }

        if(clazz==null)
            return null;
        else {
            RClassSymbol rClassSymbol = getSymbol(clazz);
            return rClassSymbol;
        }
    }

    /** 从Class获取Class符号 */
    public static RClassSymbol getSymbol(Class<?> clazz)
    {
        if(cache.containsKey(clazz))
            return cache.get(clazz);
        RClassSymbol rClassSymbol = new RClassSymbol(clazz);
        cache.put(clazz, rClassSymbol);
        return rClassSymbol;
    }

    private static void putSymbol(RClassSymbol sym)
    {
        cache.put(sym.clazz,sym);
    }
}
