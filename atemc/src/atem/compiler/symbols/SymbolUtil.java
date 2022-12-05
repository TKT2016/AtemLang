package atem.compiler.symbols;

import atem.compiler.tools.ClazzUtil;
import atem.compiler.utils.CompileError;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/** 符号辅助类 */
public class SymbolUtil {

    public static int getLambdaAdr(Symbol symbol)
    {
        if(symbol instanceof DVarSymbol)
        {
            DVarSymbol varSymbol =(DVarSymbol) symbol;
            return varSymbol.adr_lambda;
        }
        return -1;

    }

    public static boolean isEmitLocalVarRef(Symbol symbol)
    {
        if(symbol instanceof DVarSymbol)
        {
            DVarSymbol varSymbol =(DVarSymbol) symbol;
            return varSymbol.isEmitLocalVarRef();
        }
        return false;
    }

    /** 获取符号的类型 */
    public static BTypeSymbol getSymbolType(Symbol symbol)
    {
        if(symbol instanceof BTypeSymbol)
            return (BTypeSymbol)symbol;

        /* 如果是错误符号,返回Object类型 */
         if(symbol instanceof BErroneousSymbol)
            return RClassSymbolManager.ObjectSymbol ;

        /* 如果是多个难以有歧义的类型，返回Object类型*/
        if(symbol instanceof BMultiSymbol)
            return RClassSymbolManager.ObjectSymbol;

        /*如果是变量符号，返回变量符号的类型*/
        if(symbol instanceof BVarSymbol)
        {
            BVarSymbol varSymbol=(BVarSymbol)symbol;
            return  varSymbol.varType;
        }
        /*如果是方法符号，返回方法的返回类型 */
        if(symbol instanceof BMethodSymbol)
        {
            BMethodSymbol methodSymbol=(BMethodSymbol)symbol;
            return  methodSymbol.returnType;
        }

        /*其它情况，说明编译器有bug，抛出错误以便修正*/
        throw new CompileError();
    }

    public static boolean isStatic(Symbol symbol)
    {
        if(symbol instanceof RVarSymbol)
        {
            RVarSymbol varSymbol=(RVarSymbol)symbol;
            return  varSymbol.isStatic;
        }
        if(symbol instanceof RMethodSymbol)
        {
            RMethodSymbol tsymbol=(RMethodSymbol)symbol;
            return  tsymbol.isStatic;
        }
        if(symbol instanceof DMethodSymbol)
        {
            return true;
        }
        return false;
    }


    public static BTypeSymbol getElementType(Symbol symbol)
    {
        BTypeSymbol typeSymbol =symbol.getTypeSymbol();
        if(typeSymbol instanceof BArrayTypeSymbol)
        {
            return ((BArrayTypeSymbol)typeSymbol).elementType;
        }
        return null;
    }

    public static ArrayList<Symbol> filterVarSymbols(ArrayList<Symbol> list)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        for (Symbol t:list)
        {
            if(t instanceof BVarSymbol)
            {
                arrayList.add(t);
            }
        }
        return arrayList;
    }

    public static ArrayList<Symbol> filterMethodSymbols(ArrayList<Symbol> list)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        for (Symbol t:list)
        {
            if(t instanceof BMethodSymbol)
            {
                arrayList.add(t);
            }
        }
        return arrayList;
    }

    public static ArrayList<Symbol> filterTypeSymbols(ArrayList<Symbol> list)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        for (Symbol t:list)
        {
            if(t instanceof BTypeSymbol)
            {
                arrayList.add( t);
            }
        }
        return arrayList;
    }

    public static int getExtendsDeep(BTypeSymbol typeSymbol)
    {
        if(typeSymbol instanceof RClassSymbol)
        {
            return getExtendsDeep(((RClassSymbol)typeSymbol).clazz);
        }
        else
        {
            return 1;
        }
    }

    public static int getExtendsDeep(Class<?> clazz)
    {
        int i=0;
        Class<?> temp = clazz;
        while (temp!=null)
        {
            i++;
            temp=temp.getSuperclass();
        }
        return i;
    }

/*
    public static boolean isDeclFiledSymbol(BSymbol symbol)
    {
        if(symbol instanceof DVarSymbol)
        {
            DVarSymbol declVarSymbol =(DVarSymbol)symbol;
            if(declVarSymbol.varKind == VarSymbolKind.field)
                return true;
        }
        return false;
    }*/

    public static BTypeSymbol getOwnerType(Symbol symbol)
    {
        if(symbol instanceof BVarSymbol)
        {
            BVarSymbol varSymbol =(BVarSymbol)symbol;
            return varSymbol.ownerType;
        }
        else if(symbol instanceof BMethodSymbol)
        {
            BMethodSymbol methodSymbol =(BMethodSymbol)symbol;
            return methodSymbol.ownerType;
        }
        return null;
    }

    //public static boolean isArrayLengthSymbol(Symbol symbol)
 //   {
  //      return  symbol.equals(BArrayTypeSymbol.ArrayLengthFieldSymbol);
       /* if(!(symbol instanceof BVarSymbol)) return false;
        BVarSymbol varSymbol =(BVarSymbol) symbol;
        if(varSymbol.name.equals( NamesTexts.length) &&varSymbol.ownerType==null )
            return true;
        return false;*/
 //   }

    /** 根据参数类型列表查找构造函数 */
    /*public static ArrayList<BMethodSymbol> findConstructor(Symbol symbol, ArrayList<BTypeSymbol> argTypes) {
        if (symbol instanceof RClassSymbol) {
            RClassSymbol classSymbol = (RClassSymbol) symbol;
            Constructor[] constructors = classSymbol.clazz.getConstructors();
            ArrayList<BMethodSymbol> methodSyms = new ArrayList<>();
            for (Constructor constructor : constructors) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    if (isAssignFrom(parameterTypes, argTypes) !=-1) {
                        RMethodSymbol methodSymbol = RMethodSymbolManager.getSymbol(constructor, classSymbol);
                        methodSyms.add(methodSymbol);
                    }
                }
            }
            return methodSyms;
        }
        return null;
    }*/

    public static ArrayList<BMethodSymbol> findConstructor(Symbol symbol, BTypeSymbol[] argTypes) {
        if (symbol instanceof RClassSymbol) {
            RClassSymbol classSymbol = (RClassSymbol) symbol;
            Constructor[] constructors = classSymbol.clazz.getConstructors();
            ArrayList<BMethodSymbol> methodSyms = new ArrayList<>();
            for (Constructor constructor : constructors) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    if (isAssignFrom(parameterTypes, argTypes) !=-1) {
                        RMethodSymbol methodSymbol = RMethodSymbolManager.getSymbol(constructor, classSymbol);
                        methodSyms.add(methodSymbol);
                    }
                }
            }
            return methodSyms;
        }
        return null;
    }

    public static ArrayList<BMethodSymbol> findConstructor(Symbol symbol) {
        if (symbol instanceof RClassSymbol) {
            RClassSymbol classSymbol = (RClassSymbol) symbol;
            Constructor[] constructors = classSymbol.clazz.getConstructors();
            ArrayList<BMethodSymbol> methodSyms = new ArrayList<>();
            for (Constructor constructor : constructors) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();

                        RMethodSymbol methodSymbol = RMethodSymbolManager.getSymbol(constructor, classSymbol);
                        methodSyms.add(methodSymbol);

                }
            }
            return methodSyms;
        }
        return null;
    }

    public static int isAssignFrom(Class<?>[] parameterTypes, BTypeSymbol[] argTypes) {
        if (parameterTypes.length != argTypes.length)
            return -1;
        int sum=0;
        for (int i = 0; i < parameterTypes.length; i++) {
            BTypeSymbol typeSymbol = RClassSymbolManager.getSymbol(parameterTypes[i]);
            int v = matchAssignableSymbol(typeSymbol,argTypes[i]);
            if(v==-1)
                return -1;
            else
                sum+=v;
            /*if (!typeSymbol.isAssignableFromRight(argTypes.get(i))) {
                return 0;
            }*/
        }
        return sum;
    }


    public static int isAssignFrom(Class<?>[] parameterTypes, ArrayList<BTypeSymbol> argTypes) {
        if (parameterTypes.length != argTypes.size())
            return -1;
        int sum=0;
        for (int i = 0; i < parameterTypes.length; i++) {
            BTypeSymbol typeSymbol = RClassSymbolManager.getSymbol(parameterTypes[i]);
            int v = matchAssignableSymbol(typeSymbol,argTypes.get(i));
            if(v==-1)
                return -1;
            else
                sum+=v;
            /*if (!typeSymbol.isAssignableFromRight(argTypes.get(i))) {
                return 0;
            }*/
        }
        return sum;
    }

    /** 判断符号是否是基本类型的 */
  /*  public static boolean isPrimitive(Symbol symbol)
    {
        if(!(symbol instanceof RClassSymbol)) return false;
        RClassSymbol rClassSymbol = (RClassSymbol)symbol;
        return rClassSymbol.clazz.equals(boolean.class) || rClassSymbol.clazz.equals(int.class)|| rClassSymbol.clazz.equals(double.class);
    }*/

    public static boolean isBoolean(Symbol symbol)
    {
        return symbol.equals(RClassSymbolManager.booleanPrimitiveSymbol);
      /*  if(!(symbol instanceof RClassSymbol)) return false;
        RClassSymbol rClassSymbol = (RClassSymbol)symbol;
        return rClassSymbol.clazz.equals(boolean.class) || rClassSymbol.clazz.equals(Boolean.class);*/
    }

    public static boolean isString(Symbol symbol)
    {
        return symbol.equals(RClassSymbolManager.StringSymbol);
        /*
        if(!(symbol instanceof RClassSymbol)) return false;
        RClassSymbol rClassSymbol = (RClassSymbol)symbol;
        return rClassSymbol.clazz.equals(String.class);*/
    }

    public static boolean isInt(Symbol symbol)
    {
        return symbol.equals(RClassSymbolManager.intPrimitiveSymbol);
      /*  if(!(symbol instanceof RClassSymbol)) return false;
        RClassSymbol rClassSymbol = (RClassSymbol)symbol;
        boolean b1 = rClassSymbol.clazz.equals(int.class) || rClassSymbol.clazz.equals(Integer.class);
        boolean  b2 =    symbol.equals(RClassSymbolManager.intPrimitiveSymbol);
        if(b1!=b2)
            throw new CompileError();
        return  b2;*/
    }

    public static boolean isVoid(Symbol symbol)
    {
        return symbol.equals(RClassSymbolManager.voidPrimitiveSymbol);
        /*if(!(symbol instanceof RClassSymbol)) return false;
        RClassSymbol rClassSymbol = (RClassSymbol)symbol;
        return rClassSymbol.clazz.equals(void.class)|| rClassSymbol.clazz.equals(Void.class);*/
    }

    /** 计算符号的匹配度  */
    public static int matchAssignableSymbol(BTypeSymbol leftSymbol,BTypeSymbol rightSymbol)
    {
        if(leftSymbol.equals(rightSymbol)) return  0;
        if(leftSymbol .equals(RClassSymbolManager.ObjectSymbol))
        {
            return 0;
        }
        if(leftSymbol instanceof BArrayTypeSymbol)
        {
            return -1;
        }
        else if(leftSymbol instanceof  BErroneousSymbol)
        {
            return 1;
        }
        /*else if(leftSymbol instanceof  AnySymbol)
        {
            return 0;
        }*/
        else if(leftSymbol instanceof FileSymbol)
        {
            return -1;
        }
        else if(leftSymbol instanceof RClassSymbol)
        {
            RClassSymbol ca = (RClassSymbol) leftSymbol;
            if(rightSymbol instanceof BArrayTypeSymbol)
            {
                if(isObject(ca)) return 1;
                else
                    return -1;
            }
            else if(rightSymbol instanceof  BErroneousSymbol)
            {
                return 1;
            }
            else  if(rightSymbol instanceof FileSymbol)
            {
                if(isObject(ca)) return 1;
                else
                    return -1;
            }
            else if(rightSymbol instanceof RClassSymbol)
            {
                RClassSymbol cb = (RClassSymbol) rightSymbol;
                return ClazzUtil. matchAssignableClass(ca.clazz,cb.clazz);
            }
            throw new CompileError();
        }
        throw new CompileError();
    }


    /** 是否是Object类型 */
    public static boolean isObject(BTypeSymbol symbol)
    {
        if(symbol instanceof RClassSymbol)
        {
            RClassSymbol classSymbol = (RClassSymbol) symbol;
            return classSymbol.clazz.equals(Object.class);
        }
        return false;
    }
}
