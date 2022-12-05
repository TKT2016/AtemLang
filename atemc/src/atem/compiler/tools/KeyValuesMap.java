package atem.compiler.tools;

import java.util.ArrayList;
import java.util.HashMap;

public class KeyValuesMap <K,V>
{
    HashMap<K, ArrayList<V>> map ;

    public KeyValuesMap( )
    {
        map= new HashMap<>();
    }

    public int size()
    {
        return map.size();
    }

    public HashMap<K, ArrayList<V>> getMap()
    {
        return map;
    }

    public void put(K key,V v)
    {
        if(map.containsKey(key))
        {
            ArrayList<V> list = get(key);
            list.add(v);
        }
        else
        {
            ArrayList<V> list = new ArrayList<>();
            list.add(v);
            map.put(key,list);
        }
    }

    public ArrayList<V> get(K key)
    {
        if(map.containsKey(key))
            return map.get(key);
        return new ArrayList<>();
    }

    public boolean contains(K key)
    {
        return map.containsKey(key);
    }

}
