package atem.lang.rt;

import java.lang.reflect.Method;

public class ObjectOverwritesMethod extends ObjectFunction{
    public final Method[] methods;

    public ObjectOverwritesMethod(Class clazz, Object master, String name, Method[] methods)
    {
        super(clazz,master,name);
        this.methods = methods;
    }

    public int count()
    {
        return methods.length;
    }

    public Object invoke( Object[] args)   throws Exception
    {
        Object[] argValues = getMethodArgValues(args);
        var masterValue =RTUtil.getValue(master);
        return RTUtil.invokeMember(clazz,masterValue,name,argValues);
    }

    /* 根据参数个数判断能否运行,如果能，则运行 */
    public TryInvokeResult tryInvoke(Object[] args)  throws Exception
    {
        Object[] argValues = getMethodArgValues(args);
        var masterValue =RTUtil.getValue(master);
        return RTUtil.tryInvokeMember(clazz,masterValue,name,argValues);
    }
}
