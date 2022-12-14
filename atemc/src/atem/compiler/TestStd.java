package atem.compiler;

import atem.compiler.tools.FileUtil;

import java.util.Calendar;
import java.util.TimeZone;

public class TestStd {

    public static void main(String[] args)
    {
        testone("\\others\\Test");

        //testone("HelloWorld");
      //  testone("Literals");
        //testone("VarDim");
       // testone("Datatypes");
      //  testone("Functions");

       // testone("Macros1");
       /* testone("Lambda");
        testone("OpSymbol");
        testone("Str");
        testone("IfElse");*/
       // testone("Loop");
       // testone("Core1");
       // testone("List1");
        //testone("Map1");
        //testone("Dynamic1");
        //testone("Self1");
       // testone("ExceptionHandler");
         //testone("Member1");
       // testone("CoreFns");
       // testone("\\others\\SimpleDialogs1");
        // testone("\\others\\TestJSoup");
        //testone("\\others\\Swing1");
    }

    static void testone(String srcName)
    {
        var curPath =FileUtil.getCurrentPath();
        String baseDir = curPath+"\\atemc\\examples\\stdsamples\\";
        String feoSrc=srcName;
        feoSrc+= CompilerConsts.ext;
        CompileContext context = new CompileContext();
        context.setOutPath("out");
        context.sourcesFiles = new String[]{ baseDir+feoSrc};
        CompilerMain.compileRun( context ,false);
    }
}
