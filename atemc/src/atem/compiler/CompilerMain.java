package atem.compiler;

import atem.compiler.symbols.FileSymbol;
import atem.compiler.tools.FileUtil;
import atem.compiler.tools.runs.ClassRunArg;
import atem.compiler.tools.runs.ClassRuner;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CompilerMain {

    public static void main(String[] args) {
        if(args.length==0)
        {
            System.out.println("Please input a source file...");
            return;
        }

       /* for(int i=0;i<args.length;i++)
        {
            System.out.println("CompilerMain args "+i+" : "+args[i] );
        }*/

        CompileContext  context = parseCmd(args);

       // System.out.println("compile configs:"+context.toConfigsString());
        compileRun(context,false);
    }

    private static CompileContext parseCmd(String[] args)
    {
        CompileContext context = new CompileContext();

        Options options = new Options();
        Option opt = new Option("lp", "libpath", true, "Lib Path");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("o", "out", true, "Output Path");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("h", "help", false, "Help");
        opt.setRequired(false);
        options.addOption(opt);

        CommandLine commandLine = null;
        CommandLineParser parser = new PosixParser();
        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption("h")) {
                showHelp(options);
            }

            if (commandLine.hasOption("lp")) {
                var value =commandLine.getOptionValue("lp");
                context.setLibPaths( value.split(";"));
            }

            if (commandLine.hasOption("o")) {
                var value =commandLine.getOptionValue("o");
                context.setOutPath( value);
            }

            String[] argArray = commandLine.getArgs();
            context.sourcesFiles =argArray;
        }
        catch (ParseException e) {
            showHelp(options);
        }

        return context;
    }

    static void showHelp( Options options )
    {
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        hf.printHelp("AtemCompiler", options, true);
    }

    public static int compileRun(CompileContext  context,boolean checkBytes)
    {
        SourceFileCompiler fileCompiler = new SourceFileCompiler(context);
         fileCompiler.compile( );
        fileCompiler.showResult();
        if(context==null ||fileCompiler.context.errors>0)
            return 1;
        FileSymbol sourceFileSymbol = context.compiledFileSymbols.get(0);
        ClassRuner classRuner = new ClassRuner();
        String[] classPaths = new String[]{context.getOutPath()};
        try {
            byte[] bytes =  FileUtil.readAllBytes(sourceFileSymbol.compiledClassFile);
            ClassRunArg runArg = new ClassRunArg();
            runArg.classPaths = classPaths;
            runArg.checkBytes = checkBytes;
            runArg.bytes = bytes;
            runArg.className =  sourceFileSymbol.getFullname();
            runArg.method =  "main";
            runArg.argTypes = new Class<?>[]{(new String[]{}).getClass()};
            runArg.args = new Object[]{new String[]{} };
            runArg.compileContext = context;
            Object result=  classRuner.runBytes(runArg);
        }
        catch (IOException e)
        {
            System.err.println("文件编译失败:"+e.getMessage());
            return 1;
        }
        catch (Exception e)
        {
            System.err.println("调用异常:"+e.getMessage());
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}
