package atem.compiler.ast;

import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.symbols.FileInnerClassSymbol;
import atem.compiler.symbols.FileSymbol;

import java.util.ArrayList;
/** 源文件语法树 */
public class JCFileTree extends JCTree
{
    /** 文件顶部定义的包 */
    public JCPackage packageDecl;

    /** 文件导入类型 */
    public ArrayList<JCImport> imports;

    public ArrayList<JCRequire> requires;

    /** 文件内定义的方法 */
    public ArrayList<JCFunction> functions;

    public ArrayList<JCMacroDecl> JCMacroDecls;

    public ArrayList<JCLambda> lambdas = new ArrayList<>();

    /** 文件内定义的方法 */
    public JCFunction clinitFunc;

    public ArrayList<JCVariableDecl> fieldDecls ;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitCompilationUnit(this,  arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateCompilationUnit(this, arg);
    }

    /** 符号 */
    public FileSymbol fileSymbol;

    public FileInnerClassSymbol fileInnerClassSymbol;

    public String sourceFile;

    public JCFileTree(String sourceFile)
    {
        this.sourceFile =sourceFile;
    }
}
