package atem.lang.rt;

import java.util.HashMap;
import java.util.Map;

public final class TypeLiteral extends RTObject{
    static Map<Class<?>, TypeLiteral> map = new HashMap<>();
    public final Class<?> clazz;
    Prototype prototype;

    private TypeLiteral(Class<?> clazz  )
    {
        this.clazz = clazz;
        prototype = new Prototype(clazz);
    }

    public static synchronized TypeLiteral get(Class<?> clazz)
    {
        if(map.containsKey(clazz)==false)
        {
            TypeLiteral staticType = new TypeLiteral(clazz);
            map.put(clazz, staticType);
        }
        return  map.get(clazz);
    }

    public String toString()
    {
        //return "TypeLiteral::"+clazz.getName();
        return "<"+clazz.getName()+">";
    }
}
