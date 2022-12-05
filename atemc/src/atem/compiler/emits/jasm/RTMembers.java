package atem.compiler.emits.jasm;

import atem.compiler.tools.ConstructorFinder;
import atem.compiler.tools.MethodFinder;
import atem.compiler.utils.CompileError;
import atem.lang.rt.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RTMembers {

    public static final Method NG;
    public static final Method ADD;
    public static final Method SUB;
    public static final Method MUL;
    public static final Method DIV;
    public static final Method AND;
    public static final Method OR;
    public static final Method GT;
    public static final Method GTEQ;
    public static final Method LT;
    public static final Method LTEQ;
    public static final Method EQEQ;
    public static final Method NOTEQ;
    public static final Method NOT;
    public static final Method toBoolean;
    public static final Method invoke;
    public static final Method setValue;

    public static final Method newDotMember;
    public static final Method TypeLiteralGet;

    public static final Constructor newMethodRef3;
    public static final Constructor newFieldRef;
    public static final Constructor newLocalVarRef;
    public static final Constructor newObjectDotMember;
    public static final Constructor newBreakException;
    public static final Constructor  newReturnException1;
    public static final Constructor  newReturnException0;
    public static final Field fieldVoidReturnRet;
    public static final Field fieldDynamicPrototype;

    public static final Method IntegerValueOf;
    public static final Method FloatValueOf;
    public static final Method BooleanValueOf;

    public static final Constructor newList;
    public static final Method listAdd;

    public static final Constructor newMap0;
    public static final Constructor newMap1;
    public static final Method mapSetKV;

    public static final Constructor newDynamic;
    public static final Method PrototypeAddMember;

    static
    {
        NG =  findMethod(RTCore.class,"NG");
        ADD =  findMethod(RTCore.class,"ADD");
        SUB =  findMethod(RTCore.class,"SUB");
        MUL =  findMethod(RTCore.class,"MUL");
        DIV =  findMethod(RTCore.class,"DIV");
        AND =  findMethod(RTCore.class,"AND");
        OR =  findMethod(RTCore.class,"OR");
        GT =  findMethod(RTCore.class,"GT");
        GTEQ =  findMethod(RTCore.class,"GTEQ");
        LT =  findMethod(RTCore.class,"LT");
        LTEQ =  findMethod(RTCore.class,"LTEQ");
        EQEQ =  findMethod(RTCore.class,"EQEQ");
        NOTEQ =  findMethod(RTCore.class,"NOTEQ");
        NOT =  findMethod(RTCore.class,"NOT");
        toBoolean =  findMethod(RTCore.class,"toBoolean");
        newDotMember =  findMethod(RTCore.class,"newDotMember");
        invoke =  findMethod(RTCore.class,"invoke");
        setValue=  findMethod(RTCore.class,"setValue");

        TypeLiteralGet =  findMethod(TypeLiteral.class,"get");

        newMethodRef3 =  findConstructor(MethodRef.class,new Class[]{Object.class,String.class,boolean.class});
        newFieldRef=  findConstructor(FieldRef.class,new Class[]{TypeLiteral.class,String.class});
        newLocalVarRef=  findConstructor(LocalVarRef.class,new Class[]{( new Object[]{}).getClass(),int.class });
        newObjectDotMember = findConstructor(ObjectDotMember.class,new Class[]{Object.class,String.class});
        newBreakException = findConstructor(BreakException.class,new Class[]{ });
        newReturnException1 = findConstructor(ReturnException.class,new Class[]{Object.class});
        newReturnException0= findConstructor(ReturnException.class,new Class[]{});
        fieldVoidReturnRet  =findField(VoidReturn.class,"ret");
        fieldDynamicPrototype  =findField(atem.lang.Dynamic.class,"prototype");
        IntegerValueOf =  findMethod(Integer.class,"valueOf" ,new Class[]{int.class} );
        FloatValueOf =  findMethod(Float.class,"valueOf",new Class[]{float.class});
        BooleanValueOf =  findMethod(Boolean.class,"valueOf",new Class[]{boolean.class});

        newList=  findConstructor(atem.lang.List.class,new Class[]{ });
        listAdd =  findMethod(atem.lang.List.class,"add" ,new Class[]{Object.class} );

        newMap0=  findConstructor(atem.lang.Map.class,new Class[]{ });
        newMap1=  findConstructor(atem.lang.Map.class,new Class[]{Object.class });
        mapSetKV =  findMethod(atem.lang.Map.class,"set" ,new Class[]{Object.class,Object.class} );

        newDynamic=  findConstructor(atem.lang.Dynamic.class,new Class[]{ });
        PrototypeAddMember =  findMethod(atem.lang.Prototype.class,"__addMember" ,new Class[]{ String.class, Object.class} );
    }

    private static Field findField(Class<?> clazz,String name )
    {
        try {
            Field field = clazz.getField(name);
            return field;
        }catch (Exception e)
        {
            throw new CompileError();
        }
    }

    private static Constructor findConstructor(Class<?> clazz,  Class<?>[] argTypes )
    {
       var  arrayList =ConstructorFinder.finds(clazz, argTypes);
        if(arrayList.size()==0)
            throw new CompileError();
        if(arrayList.size()>1)
            throw new CompileError();
        return arrayList.get(0);
    }

    private static Method findMethod(Class<?> clazz, String name )
    {
        ArrayList<Method> arrayList = MethodFinder. findMethods(clazz,name);
        if(arrayList.size()==0)
           throw new CompileError();
        if(arrayList.size()>1)
            throw new CompileError();
        return arrayList.get(0);
    }

    private static Method findMethod(Class<?> clazz, String name, Class<?>[] argTypes )
    {
        try {
            Method method = clazz.getMethod( name,argTypes)  ;
            return method;
        }catch (Exception e)
        {
            throw new CompileError();
        }
    }
}
