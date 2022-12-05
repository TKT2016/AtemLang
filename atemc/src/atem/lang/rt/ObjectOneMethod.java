package atem.lang.rt;

import atem.compiler.tools.ConstructorFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class ObjectOneMethod extends ObjectFunction{
    public final Method method;
    //public final boolean isStatic;
    public ObjectOneMethod(Class clazz, Object master, String name, Method method)
    {
        super(clazz,master,name);
        this.method = method;
        var isStatic = Modifier.isStatic(method.getModifiers());
        if(!isStatic)
        {
            if(master instanceof TypeLiteral)
            {
                throw new InterpreterError("object is not an instance of declaring class");
            }
        }
    }

    public int count()
    {
        return 1;
    }

    public Object invoke( Object[] args)   throws Exception
    {
        Object[] argValues = getMethodArgValues(args);
        var masterValue =RTUtil.getValue(master);
        return RTUtil.invokeMethod(method,masterValue,argValues);
    }

    /* 根据参数个数判断能否运行,如果能，则运行 */
    public TryInvokeResult tryInvoke(Object[] args)  throws Exception
    {
        if(method.getParameters().length==args.length )
        {
            Object obj =invoke(args);
            return new TryInvokeResult(obj);
        }
        return new TryInvokeResult();
    }
}
