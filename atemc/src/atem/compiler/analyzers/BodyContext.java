package atem.compiler.analyzers;
import atem.compiler.symbols.DMethodSymbol;

/** 函数体语义分析上下文参数 */
 class BodyContext {
    public DMethodSymbol methodSymbol; //当前函数符号
    //public SymbolScope scope; //当前作用域
    public SearchKinds searchKinds; //当前搜索类型
   // public JCFileTree fileTree;
   // public ICallableAST callableAST;
  //  public boolean isClassContext=false;

    public BodyContext copy( )
    {
        BodyContext newContext = new BodyContext();
        newContext.methodSymbol = this.methodSymbol;
        //newContext.scope = this.scope;
        newContext.searchKinds = searchKinds;
        //newContext.fileTree = fileTree;
        //newContext.callableAST = callableAST;
       // newContext.isClassContext = isClassContext;
        return newContext;
    }

    /*  克隆一个BodyContext，并把searchKinds设为参数中的 */
    public BodyContext copy(SearchKinds searchKinds)
    {
        BodyContext context = copy();
        context.searchKinds = searchKinds;
        return context;
    }

     /*  克隆一个BodyContext，并创建新一层作用域 */
    public BodyContext newScope( )
    {
        BodyContext context = this.copy();
        //context.scope = this.scope.createChild();
        return context;
    }
}
