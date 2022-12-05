package atem.compiler.analyzers;
import atem.compiler.analyzers.pre.TreeBelongsScanner;
import atem.compiler.ast.JCFileTree;
import atem.compiler.CompileContext;

/** 语义分析器 */
public class Analyzer {
    CompileContext compileContext;

    public Analyzer(CompileContext compileContext)
    {
        this.compileContext = compileContext;
    }

    /** 文件结构分析 */
    public void enterFileTree(JCFileTree tree) {
        (new TreeBelongsScanner()).setBelongs(tree);
        (new FileMemberAnalyzer(compileContext)).visitFile(tree);
    }

    /* 函数体语义分析 */
    public void attrTree(JCFileTree tree) {
      //  ( new BodyAnalyzer()).visitMethods(tree);
        ( new BodyAnalyzeTranslator(compileContext)).visitMethods(tree);
    }
}
