package atem.lang;

public class AtemObject {
    public final Prototype prototype;
    public AtemObject()
    {
        prototype = new Prototype(this);
    }
}

/*
public interface AtemObject {
    public final Prototype prototype  = new Prototype(this);

}
*/