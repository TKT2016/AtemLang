package atem.compiler.emits.jasm;

import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.*;
import atem.compiler.utils.CompileError;
import org.objectweb.asm.Opcodes;
import java.util.*;
import static org.objectweb.asm.Opcodes.*;

/** 字节码指令选择 */
public abstract class OpCodeSelecter {
    static {
        initLoadOpMap();
        initArrayLoadOpMap();
        initStoreOpMap();
        initArrayStormOpMap();
        initPushIntConstOpMap();
        initAddOpMap();
        initSubOpMap();
        initMulOpMap();
        initDivOpMap();
        initIntCompareOpMap();
        initNegOpMap();
        initReturnOpMap();
        initNewArrayOpMap();
    }

    private static Map<Class<?>,Integer> loadOpMap;
    private static void initLoadOpMap()
    {
        loadOpMap = new HashMap<>();
        loadOpMap.put(boolean.class, Opcodes.ILOAD);
        loadOpMap.put(byte.class, Opcodes.ILOAD);
        loadOpMap.put(char.class, Opcodes.ILOAD);
        loadOpMap.put(short.class, Opcodes.ILOAD);
        loadOpMap.put(int.class,Opcodes.ILOAD);
        loadOpMap.put(long.class , Opcodes.LLOAD);
        loadOpMap.put(float.class,Opcodes.FLOAD);
        loadOpMap.put(double.class,Opcodes.DLOAD);
    }

    public static int load(Class<?> clazz)
    {
        if(loadOpMap.containsKey(clazz))
            return loadOpMap.get(clazz).intValue();
        else
            return ALOAD;
    }

    public static int load(BTypeSymbol typeSymbol)
    {
        if (typeSymbol instanceof RClassSymbol) {
            RClassSymbol rClassSymbol = (RClassSymbol) typeSymbol;
            int op = OpCodeSelecter.load(rClassSymbol.clazz);
            return op;
        } else {
            return ALOAD;
        }
    }

    private static Map<Class<?>,Integer> arrayLoadOpMap;

    private static void initArrayLoadOpMap()
    {
        arrayLoadOpMap = new HashMap<>();
        arrayLoadOpMap.put(boolean.class, Opcodes.BALOAD);
        arrayLoadOpMap.put(byte.class, Opcodes.BALOAD);
        arrayLoadOpMap.put(char.class, Opcodes.CALOAD);
        arrayLoadOpMap.put(short.class, Opcodes.SALOAD);
        arrayLoadOpMap.put(int.class,Opcodes.IALOAD);
        arrayLoadOpMap.put(long.class ,Opcodes.LALOAD);
        arrayLoadOpMap.put(float.class,Opcodes.FALOAD);
        arrayLoadOpMap.put(double.class,Opcodes.DALOAD);
    }

    public static int arrayLoad(Class<?> clazz)
    {
        if(arrayLoadOpMap.containsKey(clazz))
            return arrayLoadOpMap.get(clazz).intValue();
        else
            return AALOAD;
    }

    public static int arrayLoad(BTypeSymbol elementSymbol )
    {
        if(elementSymbol instanceof RClassSymbol) {
           return arrayLoad(((RClassSymbol)elementSymbol).clazz);
        }
        else
            return AALOAD;
    }

    private static Map<Class<?>,Integer> arrayStormOpMap;

    private static void initArrayStormOpMap()
    {
        arrayStormOpMap = new HashMap<>();
        arrayStormOpMap.put(boolean.class, Opcodes.BASTORE); //Store into byte or boolean array
        arrayStormOpMap.put(byte.class, Opcodes.BASTORE); // Store into byte or boolean array
        arrayStormOpMap.put(char.class, Opcodes.CASTORE);
        arrayStormOpMap.put(short.class, Opcodes.SASTORE);
        arrayStormOpMap.put(int.class,Opcodes.IASTORE);
        arrayStormOpMap.put(long.class ,Opcodes.LASTORE);
        arrayStormOpMap.put(float.class,Opcodes.FASTORE);
        arrayStormOpMap.put(double.class,Opcodes.DASTORE);
    }
/*
    public static int arrayStorm(Class<?> clazz)
    {
        if(arrayStormOpMap.containsKey(clazz))
            return arrayStormOpMap.get(clazz).intValue();
        else
            return AASTORE;
    }*/
/*
    public static int arrayStorm(BTypeSymbol elementType)
    {
        if (elementType instanceof RClassSymbol) {
            RClassSymbol rClassSymbol = (RClassSymbol) elementType;
            int op = arrayStorm(rClassSymbol.clazz);
            return op;
        } else {
            return AASTORE;
        }
    }
*/
    private static Map<Class<?>,Integer> storeOpMap;

    private static void initStoreOpMap()
    {
        storeOpMap = new HashMap<>();
        storeOpMap.put(boolean.class, Opcodes.ISTORE);
        storeOpMap.put(byte.class,Opcodes.ISTORE);
        storeOpMap.put(char.class,Opcodes.ISTORE);
        storeOpMap.put(short.class,Opcodes.ISTORE);
        storeOpMap.put(int.class,Opcodes.ISTORE);
        storeOpMap.put(float.class,Opcodes.FSTORE);
        storeOpMap.put(double.class,Opcodes.DSTORE);
    }

    public static int getStoreOpCode(Class<?> clazz)
    {
        if(storeOpMap.containsKey(clazz))
            return storeOpMap.get(clazz).intValue();
        else
            return ASTORE;
    }

    public static int getStoreOpCode(BTypeSymbol typeSymbol)
    {
        if (typeSymbol instanceof RClassSymbol) {
            RClassSymbol rClassSymbol = (RClassSymbol) typeSymbol;
           return OpCodeSelecter.getStoreOpCode(rClassSymbol.clazz);
        }
        return ASTORE;
    }

    private static Map<Integer,Integer> pushIntConstOpMap;

    private static void initPushIntConstOpMap()
    {
        pushIntConstOpMap = new HashMap<>();
        pushIntConstOpMap.put( 0 , Opcodes.ICONST_0);
        pushIntConstOpMap.put(1 ,Opcodes.ICONST_1);
        pushIntConstOpMap.put(2,Opcodes.ICONST_2);
        pushIntConstOpMap.put(3 ,Opcodes.ICONST_3);
        pushIntConstOpMap.put(4 ,Opcodes.ICONST_4);
        pushIntConstOpMap.put(5 ,Opcodes.ICONST_5);
    }

    public static int pushIntConst(int ivalue)
    {
        if(pushIntConstOpMap.containsKey(ivalue))
            return pushIntConstOpMap.get(ivalue).intValue();
        return -1;
    }

    private static Map<TokenKind,Integer> intCompareOpMap;

    private static void initIntCompareOpMap()
    {
        intCompareOpMap = new HashMap<>();
        intCompareOpMap.put(TokenKind.GT, Opcodes.IF_ICMPGT);
        intCompareOpMap.put(TokenKind.LT, Opcodes.IF_ICMPLT);
        intCompareOpMap.put(TokenKind.EQEQ, Opcodes.IF_ICMPEQ);
        intCompareOpMap.put(TokenKind.GTEQ,Opcodes.IF_ICMPGE);
        intCompareOpMap.put(TokenKind.LTEQ ,Opcodes.IF_ICMPLE);
        intCompareOpMap.put(TokenKind.NOTEQ,Opcodes.IF_ICMPNE);
    }

    /** 根据比较运算符取整数比较指令 */
    public  static int getIntCompareOpCode(TokenKind opcode)
    {
        if(intCompareOpMap.containsKey(opcode))
            return intCompareOpMap.get(opcode).intValue();
        else
            throw new CompileError();
    }


    private static Map<Class<?>,Integer> newArrayOpMap;

    private static void initNewArrayOpMap()
    {
        newArrayOpMap = new HashMap<>();
        newArrayOpMap.put(boolean.class, Opcodes.T_BOOLEAN);
        newArrayOpMap.put(byte.class, Opcodes.T_BYTE);
        newArrayOpMap.put(char.class, Opcodes.T_CHAR);
        newArrayOpMap.put(short.class, Opcodes.T_SHORT);
        newArrayOpMap.put(int.class,Opcodes.T_INT);
        newArrayOpMap.put(long.class ,Opcodes.T_LONG);
        newArrayOpMap.put(float.class,Opcodes.T_FLOAT);
        newArrayOpMap.put(double.class,Opcodes.T_DOUBLE);
    }

    public static int newArray(Class<?> clazz)
    {
        if(newArrayOpMap.containsKey(clazz))
            return newArrayOpMap.get(clazz).intValue();
        else
            return -1;
    }

    private static Map<Class<?>,Integer> divOpMap;

    private static void initDivOpMap()
    {
        divOpMap = new HashMap<>();
        divOpMap.put(byte.class, Opcodes.IDIV);
        divOpMap.put(char.class, Opcodes.IDIV);
        divOpMap.put(short.class, Opcodes.IDIV);
        divOpMap.put(int.class,Opcodes.IDIV);
        divOpMap.put(long.class ,Opcodes.LDIV);
        divOpMap.put(float.class,Opcodes.FDIV);
        divOpMap.put(double.class,Opcodes.DDIV);
    }

    public static int div(Class<?> clazz)
    {
        if(divOpMap.containsKey(clazz))
            return divOpMap.get(clazz).intValue();
        else
            return -1;
    }

    private static Map<Class<?>,Integer> mulOpMap;

    private static void initMulOpMap()
    {
        mulOpMap = new HashMap<>();
        mulOpMap.put(byte.class, Opcodes.IMUL);
        mulOpMap.put(char.class, Opcodes.IMUL);
        mulOpMap.put(short.class, Opcodes.IMUL);
        mulOpMap.put(int.class,Opcodes.IMUL);
        mulOpMap.put(long.class ,Opcodes.LMUL);
        mulOpMap.put(float.class,Opcodes.FMUL);
        mulOpMap.put(double.class,Opcodes.DMUL);
    }

    public static int mul(Class<?> clazz)
    {
        if(mulOpMap.containsKey(clazz))
            return mulOpMap.get(clazz).intValue();
        else
            return -1;
    }

    private static Map<Class<?>,Integer> subOpMap;

    private static void initSubOpMap()
    {
        subOpMap = new HashMap<>();
        subOpMap.put(byte.class, Opcodes.ISUB);
        subOpMap.put(char.class, Opcodes.ISUB);
        subOpMap.put(short.class, Opcodes.ISUB);
        subOpMap.put(int.class,Opcodes.ISUB);
        subOpMap.put(long.class ,Opcodes.LSUB);
        subOpMap.put(float.class,Opcodes.FSUB);
        subOpMap.put(double.class,Opcodes.DSUB);
    }

    public static int sub(Class<?> clazz)
    {
        if(subOpMap.containsKey(clazz))
            return subOpMap.get(clazz).intValue();
        else
            return -1;
    }

    private static Map<Class<?>,Integer> addOpMap;

    private static void initAddOpMap()
    {
        addOpMap = new HashMap<>();
        addOpMap.put(byte.class, Opcodes.IADD);
        addOpMap.put(char.class, Opcodes.IADD);
        addOpMap.put(short.class, Opcodes.IADD);
        addOpMap.put(int.class,Opcodes.IADD);
        addOpMap.put(long.class ,Opcodes.LADD);
        addOpMap.put(float.class,Opcodes.FADD);
        addOpMap.put(double.class,Opcodes.DADD);
    }

    public static int add(Class<?> clazz)
    {
        if(addOpMap.containsKey(clazz))
            return addOpMap.get(clazz).intValue();
        else
            return -1;
    }

    private static Map<Class<?>,Integer> negOpMap;

    private static void initNegOpMap()
    {
        negOpMap = new HashMap<>();
        negOpMap.put(boolean.class, Opcodes.INEG);
        negOpMap.put(byte.class, Opcodes.INEG);
        negOpMap.put(char.class, Opcodes.INEG);
        negOpMap.put(short.class, Opcodes.INEG);
        negOpMap.put(int.class,Opcodes.INEG);
        negOpMap.put(long.class ,Opcodes.LNEG);
        negOpMap.put(float.class,Opcodes.FNEG);
        negOpMap.put(double.class,Opcodes.DNEG);
    }

    public static int neg(Class<?> clazz)
    {
        if(negOpMap.containsKey(clazz))
            return negOpMap.get(clazz).intValue();
        else
            return -1;
    }

    private static Map<Class<?>,Integer> returnOpMap;

    private static void initReturnOpMap()
    {
        returnOpMap = new HashMap<>();
        returnOpMap.put(void.class, Opcodes.RETURN);
        returnOpMap.put(boolean.class, Opcodes.IRETURN);
        returnOpMap.put(byte.class, Opcodes.IRETURN);
        returnOpMap.put(char.class, Opcodes.IRETURN);
        returnOpMap.put(short.class, Opcodes.IRETURN);
        returnOpMap.put(int.class,Opcodes.IRETURN);
        returnOpMap.put(long.class ,Opcodes.LRETURN);
        returnOpMap.put(float.class,Opcodes.FRETURN);
        returnOpMap.put(double.class,Opcodes.DRETURN);
    }

    public static int ret(Class<?> clazz)
    {
        if(returnOpMap.containsKey(clazz))
            return returnOpMap.get(clazz).intValue();
        else
            return ARETURN;
    }

}
