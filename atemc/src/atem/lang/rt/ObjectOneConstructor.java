package atem.lang.rt;

import atem.compiler.CompilerConsts;

import atem.compiler.utils.Debuger;

import java.lang.reflect.Constructor;

public class ObjectOneConstructor extends ObjectFunction{
    public final Constructor constructor;
    public ObjectOneConstructor(Class clazz, Object master,  Constructor constructor)
    {
        super(clazz,master, CompilerConsts.NEW);
        this.constructor = constructor;
    }

    public int count()
    {
        return 1;
    }

    public Object invoke( Object[] args)   throws Exception
    {
        Object[] argValues = getMethodArgValues(args);
        try {
            Object obj =constructor.newInstance(argValues);
            return obj;
        }catch (Exception ex)
        {
            Debuger.outln("57 invoke new : "+clazz.getName()+":"+name+" message:"+ex.getMessage());
            throw new InterpreterError(ex);
        }
    }

    /* 根据参数个数判断能否运行,如果能，则运行 */
    public TryInvokeResult tryInvoke(Object[] args)  throws Exception
    {
        if(constructor.getParameters().length==args.length )
        {
            Object obj =invoke(args);
            return new TryInvokeResult(obj);
        }
        return new TryInvokeResult();
    }
}
