package atem.lang.rt;

public final class ReturnException extends Exception {
    public final Object result;

    public ReturnException(Object result) {
        this.result = result;
    }

    public ReturnException() {
        this.result = null;
    }
}
