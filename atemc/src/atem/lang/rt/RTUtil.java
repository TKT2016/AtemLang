package atem.lang.rt;

import atem.compiler.tools.ClazzUtil;
import atem.compiler.tools.MethodFinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class RTUtil {

    static Float toFloat(Object value)
    {
        return Float.parseFloat(value.toString());
    }

    static Double toDouble(Object value)
    {
        return Double.parseDouble(value.toString());
    }

    public static Class<?> getClass(Object masterObj)
    {
        if(masterObj instanceof TypeLiteral)
        {
            var typeLiteral = (TypeLiteral) masterObj;
            return typeLiteral.clazz;
        }
        else  if(masterObj instanceof Class<?>)
        {
            return ( Class<?> )masterObj;
        }
        return masterObj.getClass();
    }

    public static TryInvokeResult tryInvokeMemberSelf(Class<?> clazz, Object masterValue, String name, Object self, Object[] args) throws Exception
    {
        ArrayList<Method> methods = MethodFinder.findMethods(clazz,name,args.length+1);
        if(methods.size()==1)
        {
            Method method =methods.get(0);
            Object[] args2 = new Object[args.length+1];
            args2[0]=self;
            for(int i=0;i<args.length;i++)
            {
                args2[i+1]=args[i];
            }
            Object obj = RTUtil. invokeMethod(method,masterValue,args2);
            return new TryInvokeResult(obj);
        }
        return  tryInvokeMember( clazz,  masterValue, name,  args);
    }

    public static TryInvokeResult tryInvokeMember(Class<?> clazz, Object masterValue, String name, Object[] args) throws Exception
    {
        ArrayList<Method> methods = MethodFinder.findMethods(clazz,name,args.length);
        if(methods.size()==1)
        {
            Method method =methods.get(0);
            Object result= RTUtil.invokeMethod(method,masterValue, args);
            return new TryInvokeResult(result);
        }
        return new TryInvokeResult( );
    }

    public static Object invokeMemberSelf(Class<?> clazz, Object masterValue, String name, Object self, Object[] args) throws Exception
    {
        Object[] args2 = new Object[args.length+1];
        args2[0]=self;
        for(int i=0;i<args.length;i++)
        {
            args2[i+1]=args[i];
        }
        Class<?>[] argTypes = ClazzUtil.getArgTypes(args2);
        ArrayList<Method> methods = MethodFinder.findMethods(clazz,name,argTypes);
        if(methods.size()==1)
        {
            Method method =methods.get(0);
            return RTUtil. invokeMethod(method,masterValue,args2);
        }
        return  RTUtil. invokeMember( clazz,  masterValue, name,  args);
    }

/*
    public static Object tryInvoke(Object masterObj, ArrayList<Object[]> argsList)  throws Exception
    {
        //Debuger.outln("26 invoke:"+masterObj);
        if(masterObj instanceof Undefined)
            throw new InterpreterError(masterObj+" 没有成员,无法调用");
        if(masterObj instanceof Invoker)
        {
            var invoker = (Invoker) masterObj;
            for(var args:argsList)
            {
                var tryInvokeResult = invoker.invokeTry(args);
                if(tryInvokeResult.success)
                {
                    return tryInvokeResult.result;
                }
            }
            throw new InterpreterError("参数错误:"+masterObj.getClass().getName());
        }
        else if(masterObj instanceof VarRef)
        {
            for(var args:argsList) {
                if (args.length == 0) {
                    VarRef dotMember = (VarRef) masterObj;
                    Object value = dotMember.getValue();
                    return value;
                }
            }
            throw new InterpreterError("取值不需要参数:"+masterObj.getClass().getName());
        }
        else if(masterObj instanceof VoidReturn  )
        {
            for(var args:argsList) {
                if (args.length == 0) {
                    return  VoidReturn.ret;
                }
            }
            throw new InterpreterError("VoidReturn类型无法调用:"+masterObj.getClass().getName());
        }
        throw new InterpreterError("类型无法调用:"+masterObj.getClass().getName());
    }*/

    public static void setValue(Object masterObj,Object value)
    {
        if(masterObj instanceof ValueSetGet)
        {
            var dotMember = (ValueSetGet) masterObj;
            dotMember.setValue( value);
            return;
        }
        throw  new InterpreterError("不能赋值:"+masterObj.getClass());
    }

    public static Object getValue(Object masterObj)
    {
        if(masterObj instanceof ValueSetGet)
        {
            var dotMember = (ValueSetGet) masterObj;
            var value = dotMember.getValue( );
            return getValue(value);
        }
        return masterObj;
    }

    public static Object getValueRef(Object masterObj)
    {
        if(masterObj instanceof VarRef)
        {
            return masterObj;
        }
        return RTCore.getValue(masterObj);
    }

    public static Object invokeMethod(Method method , Object masterValue, Object[] args) throws Exception
    {
        try {
            //Debuger.outln("invokeMethod 113 invoke : "+clazz.getName()+":"+name+" method:"+method+" "+args);
            Object result= method.invoke(masterValue,args);
            if(method.getReturnType().equals(void.class))
                return VoidReturn.ret;
            else
                return result;
        }
        catch (Exception ex)
        {
            if(ex instanceof  java.lang.IllegalArgumentException)
            {
                throw new InterpreterError(ex.getMessage()); //wrong number of arguments
            }
           else if(ex instanceof java.lang.reflect.InvocationTargetException)
            {
                InvocationTargetException invocationTargetException = (InvocationTargetException) ex;
                var throwable= invocationTargetException.getTargetException();
                if(throwable instanceof BreakException)
                {
                    throw (BreakException)invocationTargetException.getTargetException();
                }
                /* else if(throwable instanceof ContinueException)
                    {
                        throw (ContinueException)invocationTargetException.getTargetException();
                }*/
                else if(throwable instanceof ReturnException)
                {
                    throw (ReturnException)invocationTargetException.getTargetException();
                }
                else if(throwable instanceof ThrowException)
                {
                    throw (ThrowException) throwable;
                }
            }
            throw ex;
        }
    }

    public static Object invokeNonArgs(Object masterObj )  throws Exception
    {
        return RTCore. invoke(masterObj, ConstVars.ObjectArrayEmpty);
    }

    public static Object invokeMember(Class<?> clazz, Object masterValue, String name, Object[] args) throws Exception
    {
        Class<?>[] argTypes = ClazzUtil. getArgTypes(args);
        ArrayList<Method> methods = MethodFinder.findMethods(clazz,name,argTypes);
        if(methods.size()==1)
        {
            Method method =methods.get(0);
            return invokeMethod(method,masterValue, args);
        }
        else  if(methods.size()==0)
        {
            System.err.println("131 invoke : "+clazz.getName()+":"+name);
            throw new InterpreterError("没有找到函数");
        }
        else
        {
            throw  new InterpreterError("不明确的函数");
        }
    }

    public static Object invokeMember( Object masterValue, String name, Object[] args) throws Exception
    {
        Class<?> clazz = masterValue.getClass();
        return invokeMember(clazz,masterValue,name,args);
    }

}
