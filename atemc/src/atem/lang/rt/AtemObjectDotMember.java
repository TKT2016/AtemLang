package atem.lang.rt;

import atem.compiler.CompilerConsts;

import atem.compiler.tools.ClazzUtil;
import atem.compiler.tools.ConstructorFinder;
import atem.compiler.tools.MethodFinder;
import atem.lang.AtemObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AtemObjectDotMember extends  RTObject implements DotMember
{
    public final AtemObject masterValue;
    public final String name;
    Class clazz;
    Field field;
    ObjectFunction objectFunction;
    
    public AtemObjectDotMember(Object masterValue, String name)
    {
        this.masterValue =(AtemObject) masterValue;
        this.name = name;
    }

    public Object getValue( )
    {
        try {
            if(field!=null)
            {
                return  field.get(masterValue);
            }
            else if(objectFunction!=null)
            {
                return objectFunction;
            }
           else
            {
                return masterValue.prototype.__get(name);
            }
        }catch (Exception ex)
        {
            throw new InterpreterError(ex);
        }
        //throw new InterpreterError("调用错误");
    }

    public void setValue( Object value)
    {
        initMember();
        try {
            if(field!=null)
            {
                field.set(masterValue,value);
            }
            else
            {
                masterValue.prototype.__set(name,value);
            }
        }catch (Exception ex)
        {
            throw new InterpreterError(ex);
        }
      //  throw new InterpreterError("调用错误");
    }

    public Object invoke( Object[] args)  throws Exception
    {
        initMember();
        if(objectFunction!=null)
            return objectFunction.invoke(args);
        else
        {
            Object extMember = getValue();
            if(extMember.equals(Undefined.undefined))
            {
                throw new InterpreterError("没有成员"+name);
            }
            return  RTCore.invoke(extMember,args);
        }
    }

    /* 根据参数个数判断能否运行,如果能，则运行 */
    public TryInvokeResult tryInvoke(Object[] args)  throws Exception
    {
        initMember();
        if(objectFunction!=null)
            return objectFunction.tryInvoke(args);
        else
        {
            Object extMember = getValue();
            if(extMember.equals(Undefined.undefined))
            {
                throw new InterpreterError("没有成员"+name);
            }
            return  RTCore.tryInvoke(extMember,args);
        }
    }

    boolean isReaded;
    void initMember()
    {
        if(!isReaded)
        {
            clazz = RTUtil.getClass(masterValue);
            if(name.equals(CompilerConsts.NEW))
            {
                var constructors = ConstructorFinder.finds(clazz);
                if (constructors.size() ==1) {
                    ObjectOneConstructor objectOneConstructor = new ObjectOneConstructor(clazz, masterValue,  constructors.get(0));
                    this.objectFunction = objectOneConstructor;
                } else if (constructors.size() > 0) {
                    Constructor[] constructors1 = new Constructor[constructors.size()];
                    for (int i = 0; i < constructors1.length; i++) {
                        constructors1[i] = constructors.get(i);
                    }
                    ObjectOverwritesConstructor objectOverwritesConstructor = new ObjectOverwritesConstructor(clazz, masterValue,  constructors1);
                    this.objectFunction = objectOverwritesConstructor;
                }
            }
            else {
                field = ClazzUtil.findField(clazz, name);
                var methods = MethodFinder.findMethods(clazz, name);
                if (methods.size() == 1) {
                    ObjectOneMethod objectOneMethod = new ObjectOneMethod(clazz, masterValue, name, methods.get(0));
                    this.objectFunction = objectOneMethod;
                } else if (methods.size() > 0) {
                    Method[] methods1 = new Method[methods.size()];
                    for (int i = 0; i < methods1.length; i++) {
                        methods1[i] = methods.get(i);
                    }
                    ObjectOverwritesMethod objectOverwritesMethod = new ObjectOverwritesMethod(clazz, masterValue, name, methods1);
                    this.objectFunction = objectOverwritesMethod;
                }
            }
            isReaded = true;
            /*if(field==null && objectFunction==null)
            {
                throw new InterpreterError("没有成员'"+name+"'");
            }*/
        }
    }

    @Override
    public String toString()
    {
        return "<DynamicMember:"+ masterValue +"."+name+">";
    }
}
