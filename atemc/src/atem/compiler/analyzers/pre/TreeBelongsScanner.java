package atem.compiler.analyzers.pre;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.ast.models.BelongsInfo;
import atem.compiler.symbols.*;

import java.util.ArrayList;

/** 源文件生成器 */
public class TreeBelongsScanner extends TreeScanner<TreeBelongsContext>
{
    public void setBelongs(JCFileTree fileTree )
    {
        visitCompilationUnit(fileTree,null);
    }

    @Override
    public void visitCompilationUnit(JCFileTree tree  , TreeBelongsContext arg) {
        arg = new TreeBelongsContext();
        arg.scope = new SymbolScope(null,tree.sourceFile);
        arg.fileTree = tree;
        tree.belongsInfo = new BelongsInfo(arg);
        /* 生成所有函数 */
        for(JCFunction method:tree.functions)
            method.scan(this,arg);
        for(var method:tree.JCMacroDecls)
            method.scan(this,arg);
    }

    /** 方法字节码生成 */
    @Override
    public void visitMethodDef(JCFunction tree, TreeBelongsContext arg)
    {
        var arg2 =arg.clone();
        arg2.topFunc = tree;
        if(!tree.propertys.isMacroCallArg)
            arg2.scope = arg2.scope.createChild(tree.name()+"_"+tree.posToken.pos);
        arg2.callableAST = tree;
        tree.belongsInfo = new BelongsInfo(arg2);
        for (JCVariableDecl variableDecl : tree.params)
            variableDecl.scan(this,arg2);
        tree.body.bodyKind = JCBlock.BodyKind.FunctionBody;
        tree.body.scan(this,arg2);
    }

    public void visitLambda(JCLambda tree, TreeBelongsContext arg)
    {
        var arg2 =arg.clone();
        if(!tree.propertys.isMacroCallArg)
            arg2.scope = arg2.scope.createChild("Lambda_"+tree.posToken.pos);
        arg2.callableAST = tree;
        if(arg2.topLambda==null)
            arg2.topLambda = tree;

        tree.belongsInfo = new BelongsInfo(arg2);
        tree.belongsInfo.callableASTParent = arg.callableAST;
        tree.body.bodyKind = JCBlock.BodyKind.FunctionBody;
        tree.body.scan(this,arg2);
        if(tree.belongsInfo.fileTree.lambdas==null)
            tree.belongsInfo.fileTree.lambdas = new ArrayList<>();
       tree.belongsInfo.fileTree.lambdas.add(tree);
    }

    /** 方法字节码生成 */
    @Override
    public void visitMacroDef(JCMacroDecl tree, TreeBelongsContext arg)
    {
        var arg2 =arg.clone();
        if(!tree.propertys.isMacroCallArg)
            arg2.scope = arg.scope.createChild("MacroAST_"+tree.posToken.pos);
        arg2.callableAST = tree;
        arg2.topFunc  =tree;
        tree.belongsInfo = new BelongsInfo(arg2);
        for (var item : tree.items)
        {
            item.belongsInfo = new BelongsInfo(arg2);
        }

        tree.body.bodyKind = JCBlock.BodyKind.FunctionBody;
        tree.body.scan(this,arg2);
    }

    /** 代码块生成 */
    @Override
    public void visitBlock(JCBlock tree, TreeBelongsContext arg)
    {
        var arg2 =arg.clone();
        if(!tree.propertys.isMacroCallArg)
        arg2.scope = arg2.scope.createChild("Block_"+tree.posToken.pos);
        tree.belongsInfo = new BelongsInfo(arg2);
        for(JCStatement statement:tree.statements)
        {
           var arg3 =arg.clone();
            arg3.statement =statement;
            statement.scan(this,arg3);
        }
    }

    /** 生成While循环语句 */
    @Override
    public void visitWhileLoop(JCWhile tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.cond,arg2);
        scanBelongs(tree.body,arg2);
    }

    /** 生成if语句 */
    @Override
    public void visitIf(JCIf tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.cond,arg2);
        scanBelongs(tree.thenpart,arg2);
        scanBelongs(tree.elsepart,arg2);
    }

    /** 生成return语句 */
    @Override
    public void visitReturn(JCReturn tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.expr, arg2);
    }

    /** 生成表达式语句 */
    @Override
    public void visitExprStmt(JCExprStatement tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.expr, arg2);
    }

    /** 生成常量表达式 */
    @Override
    public void visitLiteral(JCLiteral tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
    }

    /** 生成标识符表达式 */
    public void visitIdent(JCIdent tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
    }

    /** 生成点运算表达式访问 */
    @Override
    public void visitFieldAccess(JCFieldAccess tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.selected, arg2);
    }

    /** 生成一元运算表达式 */
    @Override
    public void visitUnary(JCUnary tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.expr, arg2);
    }

    /** 生成二元运算表达式 */
    @Override
    public void visitBinary(JCBinary tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.left, arg2);
        scanBelongs(tree.right, arg2);
    }

    /** 生成变量声明表达式 */
    @Override
    public void visitVarDef(JCVariableDecl tree, TreeBelongsContext arg)
    {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.init, arg2);
    }

    /** 生成赋值表达式 */
    @Override
    public void visitAssign(JCAssign tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.left, arg2);
        scanBelongs(tree.right, arg2);
    }

    /** 生成函数调用表达式 */
    @Override
    public void visitMethodInvocation(JCMethodInvocation tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.methodExpr,arg2);
        for (JCExpression jcExpression : tree.getArgs())
            scanBelongs(jcExpression,arg2);
    }

    @Override
    public void visitMacroCall(JCMacroCall tree, TreeBelongsContext arg)
    {
        var arg2 =arg.clone();
        if(!tree.propertys.isMacroCallArg)
            arg2.scope =arg2.scope.createChild("MacroCall_"+tree.posToken.pos);
        tree.belongsInfo = new BelongsInfo(arg2);

        for (JCExpression jcExpression : tree.getItems()) {
            jcExpression.belongsInfo = tree.belongsInfo;
            scanBelongs(jcExpression, arg2);
        }
    }

    /** 生成括号表达式 */
    @Override
    public void visitParens(JCParens tree, TreeBelongsContext arg) {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        scanBelongs(tree.expr, arg2);
    }

    public void scanBelongs(JCTree tree, TreeBelongsContext arg) {
        if (tree == null) return;
        tree.scan(this, arg);
    }

    public void visitArrayLiteral(JCArrayLiteral tree, TreeBelongsContext arg)
    {
        var arg2 =arg.clone();
        tree.belongsInfo = new BelongsInfo(arg2);
        int count=0;
        for(var element:tree.elements)
        {
            scanBelongs(element, arg2);
        }
    }
}
