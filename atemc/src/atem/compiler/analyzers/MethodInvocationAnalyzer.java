package atem.compiler.analyzers;

import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCMethodInvocation;
import atem.compiler.ast.JCTree;
import atem.compiler.ast.TreeeUtil;
import atem.compiler.symbols.*;
import atem.compiler.utils.CompileError;

import java.util.ArrayList;

public class MethodInvocationAnalyzer {

    BodyAnalyzeTranslator translator;

    public MethodInvocationAnalyzer( BodyAnalyzeTranslator translator)
    {
        this.translator = translator;
    }

    public JCTree translateMethodInvocation(JCMethodInvocation tree, BodyContext arg)
    {
        var args2 = TreeeUtil.getParenInnerExpr(tree.getArgs());
        tree.setArgs( translator.translates(args2,arg));
        // if(tree.toString().startsWith("fun1"))
        //     Debuger.outln("584 translateMethodInvocation:"+tree);
        /* 分析方法名称,只搜索方法 */
        SearchKinds searchKinds = new SearchKinds(false,false);//,true);
        tree.methodExpr = translator. translate( tree.methodExpr, arg.copy(searchKinds));
       /* if(TreeeUtil.isInstanceofAccess(tree.methodExpr))
        {
            return  translateInstanceof( tree,  arg);
        }*/

        /* 分析参数,获取参数类型 */
        BTypeSymbol[] argTypes = attrArgs(tree.getArgs(),arg);
        if(argTypes==null ){
            tree.symbol = new BErroneousSymbol();
            return tree;
        }

        Symbol msymbol = tree.methodExpr.symbol;
        if(msymbol==null)
            throw new CompileError();
        if (msymbol instanceof BMethodSymbol)
        {
            /* 找到唯一的函数 */
            BMethodSymbol methodSymbol = (BMethodSymbol) msymbol;
            /* 函数调用的返回值就是函数符号的返回值 */
            tree.symbol = methodSymbol.returnType;
            /* 检查形参和实参类型是否匹配 */
            if (SearchSymbol.matchMethod(methodSymbol,argTypes) >= 0) {
                /* 设置查找到的方法符号 */
                tree.methodExpr.symbol =methodSymbol;
            }
            else
            {
                /* 参数不匹配错误处理 */
                tree.error( "方法'%s'的参数不匹配", msymbol.name);
                tree.methodExpr.symbol =new BErroneousSymbol();
            }
        }
        else if (msymbol instanceof BMultiSymbol) {
            BMultiSymbol multiSymbol=(BMultiSymbol)msymbol;
            var methodSymbol =multiSymbol.filterByArgCount(argTypes.length);
            if(methodSymbol!=null)
            {
                tree.methodExpr.symbol = methodSymbol;
            }
            tree.symbol = RClassSymbolManager.ObjectSymbol;
        }
        else if(msymbol instanceof BErroneousSymbol)
            /* 如果是错误符号，不做处理 */
            tree.symbol= msymbol;
        else if(msymbol.equals(RClassSymbolManager.ObjectSymbol))
            tree.symbol= msymbol;
        else
            tree.symbol= msymbol;
        return tree;
    }

    /**分析参数并且返回这些参数的类型符号列表  */
    private BTypeSymbol[] attrArgs(JCExpression[] args, BodyContext context)
    {
        BTypeSymbol[] argTypes =new BTypeSymbol[args.length];
        int i=0;
        for (JCExpression item : args)
        {
            if(TreeeUtil.isEmptyParens(item))
            {
                if(args.length!=1)
                {
                    item.error("空参数'%s'只能有一个", item.toString());
                    item.symbol = new BErroneousSymbol();
                }
                continue;
            }
            BodyContext argContext = context.copy( new SearchKinds());
            JCExpression newExpr = translator. translate(item, argContext);
            if( newExpr.symbol instanceof BErroneousSymbol)
            {
                //return  null;
            }
            else if(newExpr.symbol instanceof BMultiSymbol)
            {
                item.error("有歧义的多个符号'%s'", item.toString());
                item.symbol = new BErroneousSymbol();
              /*  BMultiSymbol multiSymbol=(BMultiSymbol)newExpr.symbol;
                var methodSymbol =multiSymbol.filterByArgCount(args.length);
                if(methodSymbol!=null)
                {
                    newExpr.symbol = methodSymbol;
                }
                else {
                    item.error("有歧义的多个符号'%s'", item.toString());
                    return null;
                }*/
            }
            if(item.symbol==null)
            {
                item.symbol = new BErroneousSymbol();
                //throw new CompileError();
            }
            BTypeSymbol argTypeSymbol = item.symbol.getTypeSymbol();
            if(SymbolUtil.isVoid(argTypeSymbol))
            {
                item.error("没有返回值'%s'", item.toString());
                return  null;
            }
            argTypes[i]=argTypeSymbol;
            i++;
        }
        return argTypes;
    }
}
