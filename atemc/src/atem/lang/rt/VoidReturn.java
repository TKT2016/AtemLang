package atem.lang.rt;

public class VoidReturn {

    private VoidReturn()
    {

    }

    public static final VoidReturn ret = new VoidReturn();

    public String toString()
    {
        return "<VoidReturn>";
    }
}
