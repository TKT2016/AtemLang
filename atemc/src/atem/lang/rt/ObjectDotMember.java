package atem.lang.rt;

import atem.compiler.CompilerConsts;
import atem.compiler.tools.ClazzUtil;
import atem.compiler.tools.ConstructorFinder;
import atem.compiler.tools.MethodFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectDotMember extends  RTObject implements DotMember
{
    public final Object master;
    public final String name;

    Class clazz;
    Object masterValue;
    Field field;
    ObjectFunction objectFunction;

    public ObjectDotMember(Object master, String name)
    {
        this.master = master;
        this.name = name;
    }

    public Object getValue( )
    {
        initMember();
            //throw new InterpreterError("不存在成员'"+name+"'");
        try {
            if(field!=null)
            {
                return  field.get(masterValue);
            }
            else if(objectFunction!=null)
            {
                return objectFunction;
            }
           /* else if(methods.size()>0)
            {
                return this.invoke(new Object[]{});
            }*/
        }catch (Exception ex)
        {
            throw new InterpreterError(ex);
        }
        throw new InterpreterError("调用错误");
    }

    public void setValue( Object value)
    {
        initMember();
        if(field==null)
            throw new InterpreterError("不存在成员'"+name+"'");
        try {
               field.set(masterValue,value);
        }catch (Exception ex)
        {
            throw new InterpreterError(ex);
        }
    }

    public Object invoke( Object[] args)   throws Exception
    {
        initMember();
        if(objectFunction!=null)
            return objectFunction.invoke(args);
        else
            throw new InterpreterError("不是函数，无法调用");
    }

    /* 根据参数个数判断能否运行,如果能，则运行 */
    public TryInvokeResult tryInvoke(Object[] args)  throws Exception
    {
        initMember();
        if(objectFunction!=null)
            return objectFunction.tryInvoke(args);
        return new TryInvokeResult();
    }

    boolean isReaded;
    void initMember()
    {
        if(!isReaded)
        {
            masterValue = RTUtil.getValue(this.master);
            clazz = RTUtil.getClass(masterValue);
            if(name.equals(CompilerConsts.NEW))
            {
                var constructors = ConstructorFinder.finds(clazz);
                if (constructors.size() == 0) {
                    ObjectOneConstructor objectOneConstructor = new ObjectOneConstructor(clazz, master,  constructors.get(0));
                    this.objectFunction = objectOneConstructor;
                } else if (constructors.size() > 0) {
                    Constructor[] constructors1 = new Constructor[constructors.size()];
                    for (int i = 0; i < constructors1.length; i++) {
                        constructors1[i] = constructors.get(i);
                    }
                    ObjectOverwritesConstructor objectOverwritesConstructor = new ObjectOverwritesConstructor(clazz, master,  constructors1);
                    this.objectFunction = objectOverwritesConstructor;
                }
            }
            else {
                field = ClazzUtil.findField(clazz, name);
                var methods = MethodFinder.findMethods(clazz, name);
                if (methods.size() == 1) {
                    ObjectOneMethod objectOneMethod = new ObjectOneMethod(clazz, master, name, methods.get(0));
                    this.objectFunction = objectOneMethod;
                } else if (methods.size() > 0) {
                    Method[] methods1 = new Method[methods.size()];
                    for (int i = 0; i < methods1.length; i++) {
                        methods1[i] = methods.get(i);
                    }
                    ObjectOverwritesMethod objectOverwritesMethod = new ObjectOverwritesMethod(clazz, master, name, methods1);
                    this.objectFunction = objectOverwritesMethod;
                }
            }
            isReaded = true;
            if(field==null && objectFunction==null)
            {
                throw new InterpreterError("没有成员'"+name+"'");
            }
        }
    }

    @Override
    public String toString()
    {
        return "<DotMember:"+master+"."+name+">";
    }
}
