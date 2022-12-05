package AtemIDE.executor;

import AtemIDE.IDEConsts;

public class CompileCmdBuilder {

    public static String getCompileArgsCmd(String src,String outPath)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("-jar ");
        appendStringLiteral(builder,IDEConsts.getCompilerJarPath());
        builder.append(" ");
        appendStringLiteral(builder,src);

        builder.append(" ");
        appendStringLiteral(builder,"-lp");
        builder.append(" ");
        appendStringLiteral(builder,IDEConsts.getDLibPath());

        builder.append(" ");
        appendStringLiteral(builder,"-o");
        builder.append(" ");
        appendStringLiteral(builder,outPath);

        return builder.toString();
    }

    private static void appendStringLiteral(StringBuilder builder,String str)
    {
        builder.append("\"");  //builder.append("\"");
        builder.append(str);
        builder.append("\"");  //builder.append("\"");
    }

}
