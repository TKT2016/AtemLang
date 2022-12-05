package atem.compiler.analyzers.pre;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.ICallableAST;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.symbols.SymbolScope;

public class TreeBelongsContext {
    public JCFileTree fileTree;
   // public ICallableAST parentCallableAST;
    public ICallableAST callableAST;
    public JCStatement statement;
   // public JCTree parentTree;
    public SymbolScope scope;
    public ICallableAST topFunc;
    /* 所处的最顶层Lambda,最顶层Lambda等于它自己 */
    public JCLambda topLambda;

    public TreeBelongsContext clone()
    {
        var belongsInfo = new TreeBelongsContext();
        belongsInfo.fileTree = this.fileTree;
        belongsInfo.callableAST = this.callableAST;
        belongsInfo.statement = this.statement;
       belongsInfo.topFunc = this.topFunc;
        belongsInfo.scope = this.scope;
        belongsInfo.topLambda = this.topLambda;
        return belongsInfo;
    }
}
