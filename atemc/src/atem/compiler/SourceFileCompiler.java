package atem.compiler;

import atem.compiler.analyzers.*;
import atem.compiler.emits.FileEmit;
import atem.compiler.lex.Tokenizer;
import atem.compiler.optimizers.Optimizer;
import atem.compiler.parse.Parser;
import atem.compiler.utils.SourceLog;
import atem.compiler.tools.FileUtil;
import atem.compiler.ast.JCFileTree;

import java.io.File;
import java.io.IOException;

public class SourceFileCompiler {
    CompileContext context;

    public SourceFileCompiler( CompileContext context)
    {
        this.context =context;
    }

    public CompileContext compile(   ) {
        String inputFile = context.sourcesFiles[0];
        File file = new File(inputFile);
         if(! file.exists())
         {
             context.log.error(String.format("源文件不存在:%s",inputFile));
             return context;
         }

        JCFileTree compilationFile = parseFile(inputFile);
        if (context.errors > 0) return context;
        analyze(compilationFile);
        if (context.errors > 0) return context;

        if (context.errors == 0) {
            new FileEmit( ).emit(compilationFile,context);
        }
        context.compiledFileSymbols.add(compilationFile.fileSymbol);
        return context;
    }

    public JCFileTree parseFile(String fileObject)
    {
        JCFileTree compilationFile = parse(fileObject);
        return compilationFile;
    }

    void analyze(JCFileTree tree)
    {
        Analyzer analyzer= new Analyzer(context);
        analyzer. enterFileTree(tree);
        if (context.errors > 0) return;
        analyzer. attrTree(tree);
    }

    JCFileTree optimize(JCFileTree tree)
    {
        Optimizer optimizer = new Optimizer();
        JCFileTree tree1 = optimizer.translate(tree);
        return tree1;
    }

    JCFileTree parse(String file)
    {
        try {
            String code = FileUtil.readText(file);
            SourceLog log = new SourceLog( context, file,code);
            Tokenizer tokenizer = new Tokenizer(log, code);
            Parser parser = new Parser(tokenizer,log);
            JCFileTree tree = parser.parseCompilationUnit();
            return tree;
        }
        catch (IOException e) {
            context.log.error("文件读取发生异常:" + file+":"+e.getMessage());
        }
        return null;
    }

   public void showResult()
    {
        StringBuilder builder = new StringBuilder();
        if (context.errors==0)
            builder.append("0 error");
        else
            builder.append("有"+context.errors +"个 errors");
        context.log.response(builder.toString());
    }
}
