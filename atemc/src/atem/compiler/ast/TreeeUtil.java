package atem.compiler.ast;

import atem.compiler.ast.callables.ICallableAST;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.makes.JCFieldAccess;

public class TreeeUtil {

    public static boolean isMacroCallLamba(ICallableAST callableAST )
    {
        if(callableAST instanceof JCLambda)
        {
            JCLambda jcLambda = (JCLambda) callableAST;
            if(jcLambda.propertys.isMacroCallArg)//  if(jcLambda.isMacroCallPart)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmptyParens(JCTree tree )
    {
        if(tree instanceof JCParens)
        {
            JCParens jcParens = (JCParens) tree;
            return jcParens.isEmpty();
        }

       /* if(tree instanceof ParenExpr)
        {
            ParenExpr jcParens = (ParenExpr) tree;
            return jcParens.isEmpty();
        }*/
        return false;
    }
/*
    public static boolean isInstanceofAccess(JCTree tree )
    {
        if(tree instanceof JCFieldAccess)
        {
            JCFieldAccess jcParens = (JCFieldAccess) tree;
            return jcParens.isInstanceof;
        }
       else if(tree instanceof JCMethodInvocation)
        {
            JCMethodInvocation methodInvocation = (JCMethodInvocation) tree;
            return methodInvocation.isInstanceof;
        }
        return false;
    }*/

    public static JCExpression[] getParenInnerExpr( JCExpression[] args)
    {
        var temp = args;
        while (true)
        {
            if(temp.length==1)
            {
                /*if(temp[0] instanceof ParenExpr)
                {
                    temp =((ParenExpr) temp[0]).items;
                }
                else*/
                    break;
            }
            else
                break;
        }
        return temp;
    }
}
