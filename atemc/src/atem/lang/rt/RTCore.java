package atem.lang.rt;


import atem.compiler.tools.ClazzUtil;
import atem.compiler.tools.MethodFinder;
import atem.lang.AtemObject;
import atem.lang.Dynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public final class RTCore {
    public static void setValue(Object masterObj,Object value)
    {
        RTUtil.setValue(masterObj,value);
    }

    public static Object getValue(Object masterObj)
    {
        return RTUtil.getValue(masterObj);
    }

    public static Object newDotMember(Object master, String name)
    {
       // if(master instanceof Dynamic)
       //     return new DynamicDotMember((Dynamic) master,name);
       if(master instanceof AtemObject)
            return new AtemObjectDotMember(master,name);

       return new ObjectDotMember(master,name);
    }

    public static Object invoke(Object masterObj, Object[] args)  throws Exception
    {
        if(masterObj instanceof VoidReturn)
            throw new InterpreterError(masterObj+" 没有成员,无法调用");
        //Debuger.outln("26 invoke:"+masterObj);
        if(masterObj instanceof Undefined)
            throw new InterpreterError(masterObj+" 没有成员,无法调用");
        if(masterObj instanceof Invoker)
        {
            var invoker = (Invoker) masterObj;
            Object value = invoker.invoke(args);
            return value;
        }
       /*else  if(masterObj instanceof DotMember)
        {
            DotMember objectDotMember = (DotMember) masterObj;
            Object value = objectDotMember.invoke(args);
            return value;
        }*/
       else if(masterObj instanceof VarRef)
        {
            if(args.length==0) {
                VarRef dotMember = (VarRef) masterObj;
                Object value = dotMember.getValue();
                return value;
            }
            else
            {
                throw new InterpreterError("取值不需要参数:"+masterObj.getClass().getName());
            }
        }
       else if(masterObj instanceof VoidReturn && args.length==0)
            return  VoidReturn.ret;
       else  if(args.length==0)
            return masterObj;
        //if( args.length==0)
        //    return masterObj;
        throw new InterpreterError("类型无法调用:"+masterObj.getClass().getName());
    }

    public static Object tryInvoke(Object masterObj, Object[] args1,Object[] args2)  throws Exception {
        //Debuger.outln("26 invoke:"+masterObj);
        if (masterObj instanceof Undefined)
            throw new InterpreterError(masterObj + " 没有成员,无法调用");
        if (masterObj instanceof Invoker) {
            var invoker = (Invoker) masterObj;
            var tryInvokeResult1 = invoker.tryInvoke(args1);
            if (tryInvokeResult1.success)
                return tryInvokeResult1.result;

            var tryInvokeResult2 = invoker.tryInvoke(args2);
            if (tryInvokeResult2.success)
                return tryInvokeResult2.result;
            throw new InterpreterError("参数错误:" + masterObj.getClass().getName());
        }
        /*else if(masterObj instanceof DotMember)
        {
            DotMember objectDotMember = (DotMember) masterObj;
            for(var args:argsList)
            {
                var tryInvokeResult = objectDotMember.invokeTry(args);
                if(tryInvokeResult.success)
                {
                    return tryInvokeResult.result;
                }
            }
            throw new InterpreterError("参数错误:"+masterObj.getClass().getName());
        }*/
        else if(masterObj instanceof VarRef)
        {
                if (args1.length == 0||args2.length == 0) {
                    VarRef dotMember = (VarRef) masterObj;
                    Object value = dotMember.getValue();
                    return new TryInvokeResult(value);
                }
            throw new InterpreterError("取值不需要参数:"+masterObj.getClass().getName());
        }
        else if(masterObj instanceof VoidReturn  )
        {
                if (args1.length == 0||args2.length == 0)
                    return  VoidReturn.ret;
            throw new InterpreterError("VoidReturn类型无法调用:"+masterObj.getClass().getName());
        }
        throw new InterpreterError("类型无法调用:"+masterObj.getClass().getName());
    }
    @Deprecated
    public static TryInvokeResult tryInvoke(Object masterObj, Object[] args)  throws Exception {
        //Debuger.outln("26 invoke:"+masterObj);
        if (masterObj instanceof Undefined)
            throw new InterpreterError(masterObj + " 没有成员,无法调用");
        if (masterObj instanceof Invoker) {
            var invoker = (Invoker) masterObj;
            var tryInvokeResult1 = invoker.tryInvoke(args);
            return tryInvokeResult1;
        }
        else if(masterObj instanceof VarRef)
        {
            if (args.length == 0) {
                VarRef dotMember = (VarRef) masterObj;
                Object value = dotMember.getValue();
                return new TryInvokeResult(value);
            }
            return new TryInvokeResult();
        }
        else if(masterObj instanceof VoidReturn  )
        {
            if (args.length == 0)
                return new TryInvokeResult(VoidReturn.ret);
        }
        return new TryInvokeResult();
    }

    public static Object ADD(Object a,Object b)
    {
        var leftValue = RTCore. getValue(a);
        var rightValue =RTCore.getValue(b);

        if(a instanceof String || b instanceof String)
        {
            return  leftValue.toString()+rightValue.toString();// a.toString()+b.toString();
        }

        if (leftValue instanceof Integer && rightValue instanceof Integer)
            return  (Integer) leftValue + (Integer) rightValue;

        if (leftValue instanceof Float && rightValue instanceof Float)
          return  (Float) leftValue + (Float) rightValue;

        if (leftValue instanceof Double && rightValue instanceof Double)
            return  (Double) leftValue + (Double) rightValue;

        if (leftValue instanceof Double || rightValue instanceof Double)
            return   RTUtil.toDouble(leftValue).doubleValue() +  RTUtil.toDouble(rightValue) .doubleValue();

        if (leftValue instanceof Float || rightValue instanceof Float)
            return   RTUtil.toFloat(leftValue).floatValue() +  RTUtil.toFloat(rightValue).floatValue() ;
        throw new InterpreterError("类型不能相加");
    }

    public static Object MUL(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);
        if (leftValue instanceof Integer && rightValue instanceof Integer)
            return  (Integer) leftValue * (Integer) rightValue;

        if (leftValue instanceof Float && rightValue instanceof Float)
            return  (Float) leftValue * (Float) rightValue;

        if (leftValue instanceof Double && rightValue instanceof Double)
            return  (Double) leftValue * (Double) rightValue;

        if (leftValue instanceof Double || rightValue instanceof Double)
            return   RTUtil.toDouble(leftValue).doubleValue()*  RTUtil.toDouble(rightValue).doubleValue() ;

        if (leftValue instanceof Float || rightValue instanceof Float)
            return   RTUtil.toFloat(leftValue).floatValue() *  RTUtil.toFloat(rightValue).floatValue() ;
        throw new InterpreterError("类型不能相乘");
    }

    public static Object DIV(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);
        if (leftValue instanceof Integer && rightValue instanceof Integer)
            return  (Integer) leftValue / (Integer) rightValue;

        if (leftValue instanceof Float && rightValue instanceof Float)
            return  (Float) leftValue / (Float) rightValue;

        if (leftValue instanceof Double && rightValue instanceof Double)
            return  (Double) leftValue /(Double) rightValue;

        if (leftValue instanceof Double || rightValue instanceof Double)
            return   RTUtil.toDouble(leftValue).doubleValue() /  RTUtil.toDouble(rightValue).doubleValue() ;

        if (leftValue instanceof Float || rightValue instanceof Float)
            return   RTUtil.toFloat(leftValue) .floatValue() /  RTUtil.toFloat(rightValue) .floatValue();
        throw new InterpreterError("类型不能相减");
    }

    /** 对数字取负值 */
    public static Object NG(Object a)
    {
        var leftValue =RTCore.getValue(a);
        if (leftValue instanceof Integer )
            return  -(Integer) leftValue;

        if (leftValue instanceof Float )
            return -(Float) leftValue ;

        if (leftValue instanceof Double )
            return  - (Double) leftValue ;

        throw new InterpreterError("类型不能取负");
    }

    public static Object SUB(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);
        if (leftValue instanceof Integer && rightValue instanceof Integer)
            return  (Integer) leftValue - (Integer) rightValue;

        if (leftValue instanceof Float && rightValue instanceof Float)
            return  (Float) leftValue - (Float) rightValue;

        if (leftValue instanceof Double && rightValue instanceof Double)
            return  (Double) leftValue - (Double) rightValue;

        if (leftValue instanceof Double || rightValue instanceof Double)
            return  RTUtil. toDouble(leftValue).doubleValue() -  RTUtil.toDouble(rightValue).doubleValue() ;

        if (leftValue instanceof Float || rightValue instanceof Float)
            return  RTUtil.toFloat(leftValue).floatValue() -  RTUtil.toFloat(rightValue).floatValue() ;
        throw new InterpreterError("类型不能相减");
    }

    public static boolean toBoolean(Object value)
    {
        return ((Boolean)value).booleanValue();
    }

    public static Boolean AND(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);
        return  (Boolean) leftValue && (Boolean)rightValue;
    }

    public static Boolean OR(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);
        return  (Boolean) leftValue|| (Boolean)rightValue;
    }

    public static Boolean NOT(Object a )
    {
        var leftValue =RTCore.getValue(a);
        return ! (Boolean) leftValue;
    }

    public static Boolean GT(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);

        if(leftValue==null || leftValue==null) throw new InterpreterError("GT 不能为null");

        if (leftValue instanceof Integer && rightValue instanceof Integer)
            return  (Integer) leftValue > (Integer) rightValue;

        if (leftValue instanceof Float && rightValue instanceof Float)
            return  (Float) leftValue >  (Float) rightValue;

        if (leftValue instanceof Double && rightValue instanceof Double)
            return  (Double) leftValue >  (Double) rightValue;

        if (leftValue instanceof Double || rightValue instanceof Double)
            return   RTUtil.toDouble(leftValue).doubleValue() > RTUtil.  toDouble(rightValue).doubleValue() ;

        if (leftValue instanceof Float || rightValue instanceof Float)
            return  RTUtil. toFloat(leftValue).floatValue()>  RTUtil. toFloat(rightValue) .floatValue();
        throw new InterpreterError("类型不能 GT");
    }

    public static Boolean GTEQ(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);

        if(leftValue==null || leftValue==null) throw new InterpreterError("GT 不能为null");

        if (leftValue instanceof Integer && rightValue instanceof Integer)
            return  (Integer) leftValue >= (Integer) rightValue;

        if (leftValue instanceof Float && rightValue instanceof Float)
            return  (Float) leftValue >=  (Float) rightValue;

        if (leftValue instanceof Double && rightValue instanceof Double)
            return  (Double) leftValue >=  (Double) rightValue;

        if (leftValue instanceof Double || rightValue instanceof Double)
            return   RTUtil.toDouble(leftValue).doubleValue() >=  RTUtil. toDouble(rightValue).doubleValue() ;

        if (leftValue instanceof Float || rightValue instanceof Float)
            return  RTUtil. toFloat(leftValue) .floatValue() >=   RTUtil.toFloat(rightValue) .floatValue();
        throw new InterpreterError("类型不能 GE");
    }

    public static Boolean LT(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);

        if(leftValue==null || leftValue==null) throw new InterpreterError("LT不能为null");

        if (leftValue instanceof Integer && rightValue instanceof Integer)
            return  (Integer) leftValue < (Integer) rightValue;

        if (leftValue instanceof Float && rightValue instanceof Float)
            return  (Float) leftValue <  (Float) rightValue;

        if (leftValue instanceof Double && rightValue instanceof Double)
            return  (Double) leftValue <  (Double) rightValue;

        if (leftValue instanceof Double || rightValue instanceof Double)
            return   RTUtil.toDouble(leftValue).doubleValue() <  RTUtil. toDouble(rightValue).doubleValue() ;

        if (leftValue instanceof Float || rightValue instanceof Float)
            return  RTUtil. toFloat(leftValue) .floatValue()<   RTUtil.toFloat(rightValue).floatValue() ;
        throw new InterpreterError("类型不能 LT");
    }


    public static Boolean LTEQ(Object a,Object b)
    {
        var leftValue =RTCore.getValue(a);
        var rightValue =RTCore.getValue(b);

        if(leftValue==null || leftValue==null) throw new InterpreterError("LT不能为null");

        if (leftValue instanceof Integer && rightValue instanceof Integer)
            return  (Integer) leftValue <= (Integer) rightValue;

        if (leftValue instanceof Float && rightValue instanceof Float)
            return  (Float) leftValue <=  (Float) rightValue;

        if (leftValue instanceof Double && rightValue instanceof Double)
            return  (Double) leftValue <=  (Double) rightValue;

        if (leftValue instanceof Double || rightValue instanceof Double)
            return   RTUtil.toDouble(leftValue) .doubleValue()<=   RTUtil.toDouble(rightValue).doubleValue() ;

        if (leftValue instanceof Float || rightValue instanceof Float)
            return  RTUtil. toFloat(leftValue) .floatValue()<=   RTUtil.toFloat(rightValue).floatValue() ;
        throw new InterpreterError("类型不能 LT");
    }

    public static Boolean EQEQ(Object a,Object b)
    {
        if(a==null && b==null)
        {
            return true;
        }
        else if(a==null)
        {
            var rightValue =RTCore.getValue(b);
            return rightValue==null;
        }
        else if(b==null)
        {
            var leftValue =RTCore.getValue(b);
            return leftValue==null;
        }
        else {
            var leftValue = RTCore.getValue(a);
            var rightValue = RTCore.getValue(b);
            if (leftValue instanceof Integer && rightValue instanceof Integer)
                return (Integer) leftValue == (Integer) rightValue;

            if (leftValue instanceof Float && rightValue instanceof Float)
                return (Float) leftValue == (Float) rightValue;

            if (leftValue instanceof Double && rightValue instanceof Double)
                return ((Double) leftValue).doubleValue() == ((Double) rightValue).doubleValue();

            if (leftValue instanceof Double || rightValue instanceof Double)
                return RTUtil.toDouble(leftValue).doubleValue() == RTUtil.toDouble(rightValue).doubleValue();

            if (leftValue instanceof Float || rightValue instanceof Float)
                return RTUtil.toFloat(leftValue).floatValue() == RTUtil.toFloat(rightValue).floatValue();
            return a.equals(b);
        }
    }

    public static Boolean NOTEQ(Object a,Object b)
    {
       return !EQEQ(a,b);
    }

}
