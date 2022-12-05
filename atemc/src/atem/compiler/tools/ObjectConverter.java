package atem.compiler.tools;

public abstract class ObjectConverter {
    public static Object getValue(Object value)
    {
        Object temp= value;
        /*while (temp instanceof VarRef)
        {
            temp= ( (VarRef)temp).getValue();
        }*/
        return temp;
    }

    public static double toDouble(Object value)
    {
        value = getValue(value);
        if(value instanceof Integer)
        {
            Integer integer =(Integer) value;
            return integer.intValue();
        }

        if(value instanceof Float)
        {
            Float aFloat =(Float) value;
            return aFloat.floatValue();
        }

        if(value instanceof Double)
        {
            Double aDouble =(Double) value;
            return aDouble.doubleValue();
        }
        return (double) value;
    }
}
