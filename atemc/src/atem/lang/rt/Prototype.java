package atem.lang.rt;

import java.util.HashMap;
import java.util.Map;

public class Prototype {
    Class<?> clazz;
    Map<String,Object> members = new HashMap<>();

    public Prototype(Class<?> clazz  )
    {
        this.clazz = clazz;
    }

    public boolean put(String name,Object value)
    {
        members.put(name,value);
        return true;
    }

    public Object get(String name)
    {
        if(! members.containsKey(name))
            return members.get(name);
        return Undefined.undefined;
    }

}
