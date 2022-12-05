package atem.lang;

import java.util.ArrayList;

public class List extends AtemObject {
     ArrayList<Object> arrayList;

    public List()
    {
        arrayList = new ArrayList<>();
    }

    public List(int count)
    {
        arrayList = new ArrayList<>(count);
    }

    List(Object[] eles)
    {
        arrayList = new ArrayList<>(eles.length);
        for (Object item:eles)
            arrayList.add(item);
    }

    public int count()
    {
        return arrayList.size();
    }

    public void add(Object ele)
    {
        arrayList.add(ele);
    }

    public Object get(Integer i)
    {
        return arrayList.get(i.intValue());
    }

    public void set(Integer i,Object value)
    {
        arrayList.set(i.intValue(),value);
    }

    public void clear()
    {
        arrayList.clear();
    }
}
