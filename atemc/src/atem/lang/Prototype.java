package atem.lang;

import atem.lang.rt.MethodRef;
import atem.lang.rt.Undefined;

import java.util.HashMap;

public final class Prototype {
    public Object master;
    public Prototype(Object master)
    {
        this.master = master;
    }
    private HashMap<String,Object> exmembers = new HashMap<>();

    public void __addMember(String name, Object value)
    {
        put(name,value);
    }

    public Object __get(String name )
    {
        if(exmembers.containsKey(name))
            return  exmembers.get(name);
        else
            return Undefined.undefined;
    }

    public boolean __set(String name,Object value )
    {
        if(exmembers.containsKey(name))
        {
            put(name,value);
            return true;
        }
        else
        {
            put(name,value);
            return false;
        }
    }

    private void put(String name,Object value )
    {
        if(value instanceof MethodRef)
        {
            MethodRef methodRef=(MethodRef) value;
            if(methodRef.isSelf)
                methodRef.selfObj = master;
        }
        exmembers.put(name,value);
    }

    public boolean __contains(String name)
    {
        return (exmembers.containsKey(name));
    }

    /** 所有成员名称 */
    public List keys()
    {
        var array =  exmembers.keySet().toArray();
        List list= new List(array);
        return  list;
    }
}
