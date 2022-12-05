package atem.compiler.utils;

import atem.compiler.CompileContext;

public class SimpleLog {

    final CompileContext context;

    public SimpleLog(CompileContext context)
    {
        this.context =context;
    }

    public void error(String msg)
    {
        context.errors++;
        response(msg);
    }
/*
    public void warning(String msg)
    {
        context.warnings++;
        response(msg);
    }*/

    public void response(String msg)
    {
        System.err.println(msg);
    }

}
