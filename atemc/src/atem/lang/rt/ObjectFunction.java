package atem.lang.rt;

public abstract class ObjectFunction implements Invoker{
    public final Class clazz;
    public final Object master;
    public final String name ;

    protected ObjectFunction(Class clazz, Object master, String name )
    {
        this.clazz =clazz;
        this.master = master;
        this.name = name;
    }

    public abstract int count();

    protected Object[] getMethodArgValues( Object[] args)
    {
        Object[] argValues = new Object[args.length];
        for(int i=0;i<argValues.length;i++)
        {
            argValues[i] = RTCore.getValue(args[i]);
        }
        return argValues;
    }
}
