package atem.lang.rt;

public final class Undefined //extends AtemObject
{
    private Undefined()
    {

    }

    public static final Undefined undefined = new Undefined();

    public String toString()
    {
        return "<undefined>";
    }
}
