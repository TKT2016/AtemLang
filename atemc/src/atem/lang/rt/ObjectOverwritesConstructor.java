package atem.lang.rt;

import atem.compiler.CompilerConsts;

import atem.compiler.tools.ClazzUtil;
import atem.compiler.tools.ConstructorFinder;
import atem.compiler.utils.Debuger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class ObjectOverwritesConstructor extends ObjectFunction{
    public final Constructor[] constructors;

    public ObjectOverwritesConstructor(Class clazz, Object master, Constructor[] constructors)
    {
        super(clazz,master, CompilerConsts.NEW);
        this.constructors = constructors;
    }

    public int count()
    {
        return constructors.length;
    }

    public Object invoke( Object[] args)   throws Exception
    {
        Object[] argValues = getMethodArgValues(args);
        Class<?>[] argTypes = ClazzUtil. getArgTypes(argValues);
        ArrayList<Constructor> constructors = ConstructorFinder.finds(clazz,argTypes);
        if(constructors.size()==1)
        {
            try {
                Constructor constructor =constructors.get(0);
                Object obj =constructor.newInstance(argValues);
                return obj;
            }catch (Exception ex)
            {
                //Debuger.outln("57 invoke new : "+clazz.getName()+":"+name+" message:"+ex.getMessage());
                throw new InterpreterError(ex);
            }
        }
        else  if(constructors.size()==0)
        {
            throw new InterpreterError("没有找到构造函数");
        }
        else
        {
            throw  new InterpreterError("不明确的构造函数");
        }
    }

    /* 根据参数个数判断能否运行,如果能，则运行 */
    public TryInvokeResult tryInvoke(Object[] args)  throws Exception
    {
        Object[] argValues = getMethodArgValues(args);
        ArrayList<Constructor> constructors = ConstructorFinder.finds(clazz,args.length);
        if(constructors.size()==1)
        {
            try {
                Constructor constructor =constructors.get(0);
                Object obj =constructor.newInstance(argValues);
                return new TryInvokeResult(obj);
            }catch (Exception ex)
            {
                throw new InterpreterError(ex);
            }
        }
        return new TryInvokeResult();
    }
}
