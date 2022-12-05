package atem.compiler.ast;

import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.ast.makes.MKArrayAccess;
import atem.compiler.ast.makes.MKNewClass;

public abstract class TreeScanner<T>
{
    public void visitCompilationUnit(JCFileTree compilationUnit  , T arg) {
        if(compilationUnit.packageDecl !=null)
            compilationUnit.packageDecl.scan(this,arg);

        for(int i=0;i<compilationUnit.imports.size();i++)
        {
            compilationUnit.imports.get(i).scan(this,arg);
        }

        for(int i = 0; i<compilationUnit.functions.size(); i++)
        {
            compilationUnit.functions.get(i).scan(this,arg);
        }
    }

    public void visitPackageDef(JCPackage jcPackageDecl, T arg)
    {
        return;
    }

    public void visitImport(JCImport tree, T arg)
    {
        return;
    }

    public void visitRequire(JCRequire tree, T arg)
    {
        return;
    }

    public void visitMethodDef(JCFunction tree, T arg)
    {
        //visitTree(tree.retTypeExpr,arg);
        for(JCVariableDecl param:tree.params)
        {
            visitTree(param,arg);
        }
        tree.body.scan(this,arg );
    }

    public void visitMacroDef(JCMacroDecl tree, T arg)
    {
        for(var param:tree.items)
        {
            visitTree(param,arg);
        }
        tree.body.scan(this,arg );
    }

    public void visitVarDef(JCVariableDecl tree, T arg) {
       // visitTree(tree.varType,arg);
        visitTree(tree.init,arg);
    }

    public void visitBlock(JCBlock tree,  T arg) {
        for(JCTree stmt:tree.statements)
        {
            visitTree(stmt,arg);
        }
    }

    public void visitDynamicLiteral(JCDynamicLiteral tree, T arg) {
        for(JCPair pair:tree.pairs)
        {
            visitTree(pair,arg);
        }
    }

    public void visitWhileLoop(JCWhile tree, T arg)
    {
        visitTree(tree.cond,arg);
        visitTree(tree.body,arg);
    }

    public void visitIf(JCIf tree, T arg)
    {
        visitTree(tree.cond,arg);
        visitTree(tree.thenpart,arg);
        visitTree(tree.elsepart,arg);
    }

    public void visitExprStmt(JCExprStatement tree, T arg)
    {
        visitTree(tree.expr,arg);
    }

    public void visitBreak(BreakStatement tree, T arg)
    {

    }
/*
    public void visitContinue(ContinueStatement tree, T arg)
    {

    }*/

    public void visitReturn(JCReturn tree, T arg)
    {
        visitTree(tree.expr,arg);
    }

    public void visitMethodInvocation(JCMethodInvocation tree, T arg)
    {
        visitTree(tree.methodExpr,arg);
        for(JCExpression expression:tree.getArgs())
        {
            visitTree(expression,arg);
        }
    }

    public void visitArrayLiteral(JCArrayLiteral tree, T arg)
    {
        for(JCExpression expression:tree.elements)
        {
            visitTree(expression,arg);
        }
    }

    public void visitMacroCall(JCMacroCall tree, T arg)
    {
        for(JCExpression expression:tree.getItems())
        {
            visitTree(expression,arg);
        }
    }
/*
    public void visitParenExpr(ParenExpr tree, T arg)
    {
        for(JCExpression expression:tree.items)
        {
            visitTree(expression,arg);
        }
    }*/

    public void visitLambda(JCLambda tree, T arg)
    {
        tree.body.scan(this,arg );
    }

    public void visitNewClass(MKNewClass tree, T arg)
    {
        if(tree.clazzExpr!=null)
        visitTree(tree.clazzExpr,arg);
        if(tree.args!=null)
        for(JCExpression expression:tree.args)
        {
            visitTree(expression,arg);
        }
    }
/*
    public void visitNewArray(JCNewArray tree, T arg)
    {
        visitTree(tree.elemtype,arg);
        visitTree(tree.lengthExpr,arg);
    }*/
   
    public void visitParens(JCParens tree, T arg)
    {
        visitTree(tree.expr,arg);
    }
   
    public void visitAssign(JCAssign tree, T arg)
    {
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }

    public void visitUnary(JCUnary tree, T arg)
    {
        visitTree(tree.expr,arg);
    }

    public void visitBinary(JCBinary tree, T arg)
    {
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }

    public void visitPair(JCPair tree, T arg)
    {
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }

    public void visitArrayAccess(MKArrayAccess tree, T arg)
    {
        visitTree(tree.indexed,arg);
        visitTree(tree.index,arg);
    }

    public void visitFieldAccess(JCFieldAccess tree, T arg)
    {
        visitTree( tree.selected,arg);
    }

    public void visitIdent(JCIdent tree, T arg)
    {
        return;
    }

    public void visitLiteral(JCLiteral tree, T arg)
    {
       return;
    }

/*
    public void visitPrimitiveType(JCPrimitiveTypeTree tree, T arg) {
        return;
    }*/
/*
    public void visitArrayType(JCArrayTypeTree tree, T arg)
    {
        visitTree(tree.elemType,arg);
    }*/
/*
    public void visitErroneous(JCErroneous tree, T arg)
    {
        return;
    }*/

    protected void visitTree(JCTree tree,T arg)
    {
        if(tree==null) return;
        tree.scan(this,arg);
    }
}
