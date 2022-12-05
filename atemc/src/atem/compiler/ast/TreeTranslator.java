package atem.compiler.ast;

import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.ast.makes.MKArrayAccess;
import atem.compiler.ast.makes.MKNewClass;

import java.util.ArrayList;

public abstract class TreeTranslator<A>
{
    public JCFileTree translate(JCFileTree compilationFile)
    {
        translate(compilationFile,null);
        return compilationFile;
    }

    public JCTree translateCompilationUnit(JCFileTree compilationFile, A arg)
    {
        for(int i = 0; i<compilationFile.functions.size(); i++)
            compilationFile.functions.get(i).translate(this,arg);
        return compilationFile;
    }
/*
    public JCTree translateArrayType(JCArrayTypeTree tree, A arg)
    {
        return tree;
    }*/
/*
    public JCTree translatePrimitiveType(JCPrimitiveTypeTree tree, A arg)
    {
        return tree;
    }*/

    public JCTree translateMethod(JCFunction tree, A arg)
    {
        tree.body = (JCBlock) tree.body.translate(this,arg);
        return tree;
    }

    public JCTree translateMacro(JCMacroDecl tree, A arg)
    {
        tree.body = (JCBlock) tree.body.translate(this,arg);
        return tree;
    }

    public JCTree translateBlock(JCBlock tree, A arg)
    {
        ArrayList<JCStatement> newStatements = new  ArrayList<JCStatement>();
        for (var statement : tree.statements)
        {
            JCTree jcTree =  statement.translate(this,arg);
            if(jcTree!=null)
            {
                newStatements.add((JCStatement)jcTree);
            }
        }
        tree.statements = newStatements;
        return tree;
    }

    public JCTree translateDynamicLiteral(JCDynamicLiteral tree, A arg)
    {
        ArrayList<JCPair> newPairs = new  ArrayList<JCPair>();
        for (var item : tree.pairs)
        {
            JCPair jcTree =  (JCPair)item.translate(this,arg);
            if(jcTree!=null)
            {
                newPairs.add(jcTree);
            }
        }
        tree.pairs = newPairs;
        return tree;
    }

    protected <T extends JCTree> T translate(T tree, A arg)
    {
        if(tree==null) return null;
        JCTree newTree = tree.translate(this, arg);
        return (T) newTree;
    }

    public JCTree translateImport(JCImport tree, A arg)
    {
        return tree;
    }

    public JCTree translateRequire(JCRequire tree, A arg)
    {
        return tree;
    }

    public JCTree translateAssign(JCAssign tree, A arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }
/*
    public JCTree translateNewArray(JCNewArray tree, A arg)
    {
        tree.elemtype = translate(tree.elemtype,arg);
        tree.lengthExpr =  translate(tree.lengthExpr,arg);
        return tree;
    }*/

    public JCTree translateNewClass(MKNewClass tree, A arg) {
        tree.clazzExpr = translate(tree.clazzExpr, arg);
        tree.args = translates(tree.args, arg);
        return tree;
    }

    public JCTree translatePackage(JCPackage tree, A arg)
    {
        return tree;
    }

    public JCTree visitArrayAccess(MKArrayAccess tree, A arg) {
        tree.indexed = (JCExpression) tree.indexed.translate(this, arg);
        tree.index = (JCExpression) tree.index.translate(this, arg);
        return tree;
    }

    public JCTree translateFieldAccess(JCFieldAccess tree, A arg)
    {
        tree.selected = translate(tree.selected,arg);
        return tree;
    }

    public JCTree translateBinary(JCBinary tree, A arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }

    public JCTree translatePair(JCPair tree, A arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }

    public JCTree translateIdent(JCIdent tree, A arg)
    {
        return tree;
    }

    public JCTree translateLiteral(JCLiteral tree, A arg)
    {
        return tree;
    }

    public JCTree translateParens(JCParens tree, A arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateUnary(JCUnary tree, A arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateReturn(JCReturn tree, A arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateIf(JCIf tree, A arg) {
        tree.cond = translate(tree.cond, arg);
        tree.thenpart = translate(tree.thenpart, arg);
        tree.elsepart = translate(tree.elsepart, arg);
        return tree;
    }
/*
    public JCTree translateForLoop(JCForLoop tree, A arg) {
        tree.init = translate(tree.init, arg);
        tree.cond = translate(tree.cond, arg);
        tree.step = translate( tree.step,arg);
        tree.body = translate( tree.body,arg);
        return tree;
    }
*/
    public JCTree translateWhile(JCWhile tree, A arg)
    {
        tree.cond = translate(tree.cond, arg);
        tree.body = translate( tree.body,arg);
        return tree;
    }

    public JCTree translateVariable(JCVariableDecl tree, A arg)
    {
        tree.init = translate( tree.init,arg);
        return tree;
    }

    public JCTree translateMethodInvocation(JCMethodInvocation tree, A arg)
    {
        tree.setArgs(translates(tree.getArgs(),arg));
        return tree;
    }

    public JCTree translateArrayLiteral(JCArrayLiteral tree, A arg)
    {
        tree.elements = translates(tree.elements,arg);
        return tree;
    }

    public JCTree translateMacroCall(JCMacroCall tree, A arg)
    {
        tree.setItems( translates(tree.getItems(),arg));
        return tree;
    }
/*
    public JCTree translateParenExpr(ParenExpr tree, A arg)
    {
        tree.items = translates(tree.items,arg);
        return tree;
    }*/

    public JCTree translateLambda(JCLambda tree, A arg)
    {
        tree.body = (JCBlock) tree.body.translate(this,arg);
        return tree;
    }

    public JCTree translateExprStmt(JCExprStatement tree, A arg)
    {
        tree.expr = translate(tree.expr,arg);
        return tree;
    }

    public JCTree translateBreak(BreakStatement tree, A arg)
    {
        return tree;
    }
/*
    public JCTree translateContinue(ContinueStatement tree, A arg)
    {
        return tree;
    }*/

    protected <R extends JCTree> ArrayList<R> translates(ArrayList<R> trees, A arg)
    {
        ArrayList<R> defs = new  ArrayList<R>();
        for (R deftree : trees) {
            JCTree newTree = deftree.translate(this, arg);
            if (newTree != null) {
                R nr = (R) newTree;
                defs.add(nr);
            }
        }
        return defs;
    }

    protected JCExpression[] translates(JCExpression[] trees, A arg)
    {
        JCExpression[] array = new JCExpression[trees.length];
        for(int i=0;i<trees.length;i++)
            array[i] = (JCExpression)trees[i].translate(this, arg);
        return array;
    }
}
