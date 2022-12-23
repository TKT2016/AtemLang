package atem.compiler.utils;

import atem.compiler.CompileContext;
import atem.compiler.utils.msgresources.CompileMessagesUtil;

public class SimpleLog {

    final CompileContext context;

    public SimpleLog(CompileContext context)
    {
        this.context =context;
    }
/*
    public void error(String msg)
    {
        context.errors++;
        response(msg);
    }*/

    public void error(String key, String msgContent)
    {
        context.errors++;
        String msgf = CompileMessagesUtil.getMsg(key);
        String msg= String.format(msgf,msgContent);
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
