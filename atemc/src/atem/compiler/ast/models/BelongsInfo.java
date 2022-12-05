package atem.compiler.ast.models;

import atem.compiler.analyzers.pre.TreeBelongsContext;
import atem.compiler.ast.*;
import atem.compiler.ast.callables.ICallableAST;
import atem.compiler.symbols.DVarSymbol;
import atem.compiler.symbols.SymbolScope;

public class BelongsInfo implements DVarSymbol.IDimCallable{
    public JCFileTree fileTree;
    public ICallableAST defedCallableAST;
    public ICallableAST callableASTParent;
    public ICallableAST topFunc;
    public JCStatement statement;
    public SymbolScope scope;

    public BelongsInfo clone()
    {
        BelongsInfo belongsInfo = new BelongsInfo();
        belongsInfo.fileTree = this.fileTree;
        belongsInfo.defedCallableAST = this.defedCallableAST;
        belongsInfo.statement = this.statement;
        belongsInfo.callableASTParent = this.callableASTParent;
        belongsInfo.topFunc = this.topFunc;
        belongsInfo.scope = this.scope;
        return belongsInfo;
    }

    public  BelongsInfo ( )
    {

    }

    public  BelongsInfo (TreeBelongsContext arg)
    {
        this.fileTree = arg.fileTree;
        this.defedCallableAST = arg.callableAST;
        this.statement = arg.statement;
        this.scope = arg.scope;
        this.topFunc = arg.topFunc;
    }

    public ICallableAST getDefedCallableAST()
    {
        return defedCallableAST;
    }

    public SymbolScope getScope()
    {
        return scope;
    }
}
