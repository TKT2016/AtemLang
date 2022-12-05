package atem.compiler.utils;

public class CompileError extends Error{

    public CompileError()
    {

    }

    public CompileError(String msg)
    {
        super(msg);
    }
}
