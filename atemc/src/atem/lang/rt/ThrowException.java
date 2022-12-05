package atem.lang.rt;

public class ThrowException  extends Exception {
    public final Exception targetException;

    public ThrowException(Exception targetException) {
        this.targetException = targetException;
    }

}
