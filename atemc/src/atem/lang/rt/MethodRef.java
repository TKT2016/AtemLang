package atem.lang.rt;


import atem.compiler.tools.MethodFinder;

import java.lang.reflect.Method;
import java.util.ArrayList;

/* 函数引用 */
public class MethodRef implements Invoker{
    public final Object master;
    public final String name;
    public final boolean isSelf;

    public Object selfObj;

    Class clazz;
    Object masterValue;
    ArrayList<Method> methods;
    boolean isReaded;

    public MethodRef(Object master, String name )
    {
        this.master = master;
        this.name = name;
        this.isSelf = false;
    }

    public MethodRef(Object master, String name,boolean isSelf)
    {
        this.master = master;
        this.name = name;
        this.isSelf = isSelf;
    }

    public Object getValue( )
    {
        throw new InterpreterError("MethodRef can't getValue");
        //  return this;
    }

    public void setValue( Object value)
    {
        throw new InterpreterError("MethodRef can't setValue");
    }

    public Object invoke( Object[] args)  throws Exception
    {
        Object[] argValues = new Object[args.length];
        for(int i=0;i<argValues.length;i++)
        {
            argValues[i] = RTUtil.getValue(args[i]);
        }
        return invokeMethod( argValues);
    }

    protected Object invokeMethod(Object[] args)  throws Exception
    {
        initMember();
        if(isSelf && selfObj!=null)
            return RTUtil.invokeMemberSelf(clazz,masterValue,name,selfObj,args);
        else
            return RTUtil.invokeMember(clazz,masterValue,name,args);
    }

    /* 根据参数个数判断能否运行,如果能，则运行 */
    public TryInvokeResult tryInvoke(Object[] args)  throws Exception
    {
        Object[] argValues = new Object[args.length];
        for(int i=0;i<argValues.length;i++)
        {
            argValues[i] = RTUtil.getValue(args[i]);
        }
        return tryInvokeMethod( argValues);
    }

    protected TryInvokeResult tryInvokeMethod(Object[] args)  throws Exception
    {
        initMember();
        if(isSelf && selfObj!=null)
            return RTUtil.tryInvokeMemberSelf(clazz,masterValue,name,selfObj,args);
        else
            return RTUtil.tryInvokeMember(clazz,masterValue,name,args);
    }

    void initMember()
    {
        if(!isReaded)
        {
            masterValue = RTUtil.getValue(this.master);
            clazz = RTUtil.getClass(masterValue);
            methods = MethodFinder.findMethods(clazz,name);
            isReaded = true;
        }
    }

    @Override
    public String toString()
    {
        return "<MethodRef:"+master+"."+name+">";
    }
}
