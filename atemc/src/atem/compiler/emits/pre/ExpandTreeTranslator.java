package atem.compiler.emits.pre;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.emits.pre.markers.DotMemberMarker;
import atem.compiler.emits.pre.markers.LambdaRefMarker;
import atem.compiler.emits.pre.markers.LambdaVarGetMaker;
import atem.compiler.emits.pre.markers.StaticTypeMarker;
import atem.compiler.symbols.*;
import atem.compiler.utils.CompileError;

/** 语法树生成前进行展开,生成临时变量 */
public class ExpandTreeTranslator extends TreeTranslator<ExpandContext>
{
    public JCFileTree translate(JCFileTree compilationFile)
    {
        translate(compilationFile,null);
        return compilationFile;
    }

    @Override
    public JCTree translateMethod(JCFunction tree , ExpandContext arg)
    {
        arg = new ExpandContext();
        tree.body.translate  (this,arg);
        return tree;
    }

    public JCTree translateMacro(JCMacroDecl tree, ExpandContext arg)
    {
        arg = new ExpandContext();
        tree.body.translate  (this,arg);
        return tree;
    }

    public JCTree translateBlock(JCBlock tree, ExpandContext arg)
    {
        arg = arg.clone();
        for(var stmt:tree.statements)
        {
            var stmt2 = translate(stmt,arg);
            arg.statements.add(stmt2);
        }
        tree.statements =  arg.statements;
        return tree;
    }

    public JCTree translateAssign(JCAssign tree, ExpandContext arg)
    {
        //tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }

    public JCTree translateFieldAccess(JCFieldAccess tree, ExpandContext arg)
    {
       /* if(tree.isInstanceof)
            return tree;*/
        DotMemberMarker marker = new DotMemberMarker(tree,arg);
        marker.make();
        arg.statements.add(marker.exprStatement);
        return marker.identExpr;
    }

    public JCTree translateBinary(JCBinary tree, ExpandContext arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }

    @Override
    public JCTree translateDynamicLiteral(JCDynamicLiteral tree, ExpandContext arg)
    {
        return tree;
    }

    public JCTree translatePair(JCPair tree, ExpandContext arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }

    public JCTree translateIdent(JCIdent tree, ExpandContext arg)
    {
        Symbol symbol = tree.symbol;

        if (symbol instanceof BTypeSymbol) {
            StaticTypeMarker marker = new StaticTypeMarker(tree,arg);
            marker.make();
            arg.statements.add(marker.exprStatement);
            return marker.identExpr;
        }
        else if (symbol instanceof BMethodSymbol) {
                return  tree;
        }
        else  if (symbol instanceof DVarSymbol) {
            /* 情况2:是自定义变量符号, 取出这个变量的地址和类型对应lload指令生成 */
            DVarSymbol declVarSymbol = (DVarSymbol) symbol;
            if(declVarSymbol.isLambdaRefVar)
            {
                LambdaVarGetMaker marker = new LambdaVarGetMaker(tree,arg);
                marker.make();
                arg.statements.add(marker.exprStatement);
                return marker.identExpr;
            }
            else
            {
                return tree;
            }
        }
        else
        {
            throw new CompileError();
        }
    }

    public JCTree translateLiteral(JCLiteral tree, ExpandContext arg)
    {
        return tree;
    }

    public JCTree translateParens(JCParens tree, ExpandContext arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateUnary(JCUnary tree, ExpandContext arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateReturn(JCReturn tree, ExpandContext arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateIf(JCIf tree, ExpandContext arg) {
        tree.cond = translate(tree.cond, arg);
        tree.thenpart = translate(tree.thenpart, arg);
        tree.elsepart = translate(tree.elsepart, arg);
        return tree;
    }

    public JCTree translateWhile(JCWhile tree, ExpandContext arg)
    {
        tree.cond = translate(tree.cond, arg);
        tree.body = translate( tree.body,arg);
        return tree;
    }

    public JCTree translateVariable(JCVariableDecl tree, ExpandContext arg) {
        tree.init = translate(tree.init,arg);
        return tree;
    }

    public JCTree translateMethodInvocation(JCMethodInvocation tree, ExpandContext arg)
    {
        tree.methodExpr = translate(tree.methodExpr,arg);
        tree.setArgs( translates(tree.getArgs(),arg));
        return tree;
    }

    public JCTree translateArrayLiteral(JCArrayLiteral tree, ExpandContext arg)
    {
        tree.elements = translates(tree.elements,arg);
        return tree;
    }

    public JCTree translateMacroCall(JCMacroCall tree, ExpandContext arg)
    {
        tree.argValues = translates(tree.getArgValues(),arg);
        return tree;
    }
/*
    public JCTree translateParenExpr(ParenExpr tree, ExpandContext arg)
    {
        tree.items = translates(tree.items,arg);
        return tree;
    }*/

    public JCTree translateLambda(JCLambda tree, ExpandContext arg)
    {
        LambdaRefMarker marker = new LambdaRefMarker(tree,arg);
        marker.make();
        arg.statements.add(marker.exprStatement);

        tree.body.translate  (this,arg);
        
        return marker.identExpr;
    }

    public JCTree translateExprStmt(JCExprStatement tree, ExpandContext arg)
    {
        tree.expr = translate(tree.expr,arg);
        return tree;
    }

    protected JCExpression[] translates(JCExpression[] trees, ExpandContext arg)
    {
        JCExpression[] array = new JCExpression[trees.length];
        for(int i=0;i<trees.length;i++)
            array[i] = (JCExpression)translate(trees[i], arg);
        return array;
    }

    protected <T extends JCTree> T translate(T tree, ExpandContext arg)
    {
        if(tree==null) return null;
        //if(tree.isAnalyzered) return tree;
        JCTree newTree = tree.translate(this, arg);
        // tree.isAnalyzered = true;
        return (T) newTree;
    }
}
