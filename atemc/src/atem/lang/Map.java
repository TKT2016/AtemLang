package atem.lang;

import java.util.HashMap;

public class Map extends AtemObject {

    private HashMap<Object,Object> hashMap = new HashMap<>();

    private boolean _hasDefault;

    public Object _defaultValue;

    public Map(  )
    {
        _hasDefault =false;
    }

    public Map(Object defaultValue )
    {
        this._hasDefault = true;
        this._defaultValue =defaultValue;
    }

    /*** 是否有默认值 */
    public boolean hasDefault(  )
    {
        return  _hasDefault;
    }

    /*** 获取默认值 */
    public Object defaultValue(  )
    {
        return  _defaultValue;
    }

    /** 设置键值 */
    public void set(Object key,Object value)
    {
        hashMap.put(key,value);
    }

    /** 用键获取值 */
    public Object get(Object key)
    {
        if(hashMap.containsKey(key))
           return hashMap.get(key);
        if(_hasDefault)
            return _defaultValue;
        return null;
    }

    /** 是否含有键 */
    public boolean contains(Object key)
    {
        return hashMap.containsKey(key);
    }

    /** 个数  */
    public int count(   )
    {
        return hashMap.size( );
    }

    /** 所有键的列表 */
    public List keys()
    {
        var array =  hashMap.keySet().toArray();
        List list= new List(array);
        return  list;
    }

    /** 用键移除键值 */
    public Object remove(Object key)
    {
        return hashMap.remove(key);
    }

    /** 清空 */
    public void clear(  )
    {
        hashMap.clear( );
    }
}
