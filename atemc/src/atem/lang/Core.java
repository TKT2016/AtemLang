package atem.lang;


import atem.lang.rt.*;

public final class Core //extends AtemSourceGen
{
    public static Object println;
    public static Object print;

    static {
        println = new MethodRef(System.out, "println");
        print = new MethodRef(System.out, "print");
    }

    public static boolean isInstanceof(Object a,Object b)
    {
        a = RTUtil.getValue(a);
        b = RTUtil.getValue(b);
        if(!(b instanceof TypeLiteral))
            throw new InterpreterError("第二个参数必须是类型");
        TypeLiteral typeLiteral =(TypeLiteral) b;
        Class<?> aclass = a.getClass();
        if(typeLiteral.clazz.isInterface())
        {
            Class<?>[] interfacesArray = typeLiteral.clazz.getInterfaces();//获取这个类的所以接口类数组
            for (Class<?> item : interfacesArray) {
                if (item == aclass) { //判断是否有继承的接口
                    return true;
                }
            }
            return false;
        }
        else
        {
            var temp = aclass;
            while (true)
            {
                if(temp.equals(typeLiteral.clazz))
                    return true;
                temp = temp.getSuperclass();
                if(temp==null)
                    break;
            }
            return false;
        }
    }
/*
    public static Class<?> classof(Object a)
    {
        a = RTUtil.getValue(a);
        Class<?> result = a.getClass();
        return result;
    }
*/
    public static boolean isNull(Object a)
    {
        return a ==null;
    }

    public static boolean isNotNull(Object a)
    {
        return a !=null;
    }

    public static boolean isUndefined(Object a)
    {
        return a instanceof Undefined;
    }



    @Macro(value = "throw $" ,detail ="throw $ex")
    public static Object throwe(Object ex) throws ThrowException {
        ex =  RTUtil.getValue(ex);
        if(ex instanceof Exception) {
            Exception e = (Exception) ex;
            throw new ThrowException(e);
        }
        else
        {
            throw new InterpreterError("不是java.lang.Exception实例");
        }
    }

    @Macro(value = "try $ catch $" ,detail ="try $body catch $handler" )
    public static   Object try___catch______(Object body, Object handler)  throws Exception{
        body =  RTUtil.getValue(body);
        handler =  RTUtil.getValue(handler);
        try {
            RTUtil.invokeNonArgs(body);
        }
        catch (ThrowException e)
        {
            RTUtil.invokeNonArgs(handler);//  RTCore.invoke(handler, ConstVars.ObjectArrayEmpty );
        }
        return  VoidReturn.ret;
    }

    @Macro(value = "try $ catch $ $" ,detail ="try $body catch $ex $handler" )
    public static   Object try___catch______(Object body, Object ex, Object handler)  throws Exception{
        ex =  RTUtil.getValueRef(ex);
        body =  RTUtil.getValue(body);
        handler =  RTUtil.getValue(handler);
        LocalVarRef exRef =(LocalVarRef)ex;
        try {
            RTUtil.invokeNonArgs(body);
        }
        catch (ThrowException e)
        {
            exRef.setValue(e.targetException);
           // RTCore.tryInvoke(handler, ConstVars.ObjectArrayEmpty,  new Object[]{exRef});
            RTUtil.invokeNonArgs(handler);
        }
        return  VoidReturn.ret;
    }

    @Macro(value = "try $ catch $ $ finally $" ,detail ="try $body catch $ex $handler finally $finallyHandler" )
    public static final Object try___catch______finally___(Object body, Object ex, Object handler,Object finallyHandler)  throws Exception{
        ex =  RTUtil.getValueRef(ex);
        body =  RTUtil.getValue(body);
        handler =  RTUtil.getValue(handler);
        finallyHandler =  RTUtil.getValue(finallyHandler);

        LocalVarRef exRef =(LocalVarRef)ex;
        try {
            RTUtil.invokeNonArgs(body);
        }
        catch (ThrowException e)
        {
            exRef.setValue(e.targetException);
            //RTCore.tryInvoke(handler, ConstVars.ObjectArrayEmpty,  new Object[]{exRef});
            RTUtil.invokeNonArgs(handler);
        }
        finally {
            RTUtil.invokeNonArgs(finallyHandler );
        }
        return  VoidReturn.ret;
    }

    @Macro(value = "try $ catch $ finally $" ,detail ="try $body catch $handler finally $finallyHandler" )
    public static final Object try___catch______finally___(Object body, Object handler,Object finallyHandler)  throws Exception{
        body =  RTUtil.getValue(body);
        handler =  RTUtil.getValue(handler);
        finallyHandler =  RTUtil.getValue(finallyHandler);

        try {
            RTUtil.invokeNonArgs(body);
        }
        catch (ThrowException e)
        {
            //RTCore.tryInvoke(handler, ConstVars.ObjectArrayEmpty);
            RTUtil.invokeNonArgs(handler);
        }
        finally {
            RTUtil.invokeNonArgs(finallyHandler );
        }
        return  VoidReturn.ret;
    }

    @Macro(value = "forloop $ $ $ $", detail = "forloop $init $condi $step $body")
    public static final Object forloop____________(Object init, Object condi, Object step, Object body)  throws Exception {
        init =  RTUtil.getValueRef(init);
        condi =  RTUtil.getValue(condi);
        step =  RTUtil.getValue(step);
        body =  RTUtil.getValue(body);
        while(RTCore.toBoolean(RTUtil.invokeNonArgs(condi))) {
            try {
                RTUtil.invokeNonArgs(body );
                RTUtil.invokeNonArgs(step);
            } catch (BreakException var6) {
                break;
            }
        }
        return  VoidReturn.ret;
    }

    @Macro(value = "foreach $ in $ $", detail = "foreach $element in $list $body")
    public static final Object foreach____________(Object element, Object list , Object body)  throws Exception {
        element =  RTUtil.getValueRef(element);
        list = RTUtil.getValue(list);
        body =  RTUtil.getValue(body);
        List listr=(List) list;
        for(int i=0;i<listr.count();i++)
        {
            try {
                RTUtil.setValue(element,listr.get(i));
                RTUtil.invokeNonArgs(body );
            } catch (BreakException var6) {
                break;
            }
        }
        return  VoidReturn.ret;
    }

    @Macro(value = "foreach $ in $ index $ $", detail = "foreach $element in $list index $index $body")
    public static final Object foreach____________(Object element, Object list ,Object index, Object body)  throws Exception {
        element =  RTUtil.getValueRef(element);
        list = RTUtil.getValue(list);
        body =  RTUtil.getValue(body);

        List listr=(List) list;

        for(int i=0;i<listr.count();i++)
        {
            try {
                RTUtil.setValue(element,listr.get(i));
                RTUtil.setValue(index,i);
                RTUtil.invokeNonArgs(body );
            } catch (BreakException var6) {
                break;
            }
        }
        return  VoidReturn.ret;
    }

}
