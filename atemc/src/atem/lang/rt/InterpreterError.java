package atem.lang.rt;

public class InterpreterError extends Error{
    public   Exception exception;
    public InterpreterError(Exception ex)
    {
        this.exception=ex;
    }

    public String message;
    public InterpreterError(String msg)
    {
        System.err.println(msg);
        this.message =msg;
    }
}
