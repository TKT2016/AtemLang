package atem.compiler.emits.pre;

import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.ast.makes.MKArrayAccess;
import atem.compiler.ast.makes.MKNewClass;
import atem.compiler.symbols.DVarSymbol;
import atem.compiler.symbols.DMethodSymbol;
import atem.compiler.ast.*;
import atem.compiler.ast.TreeScanner;
import atem.compiler.symbols.VarSymbolKind;
import atem.compiler.utils.CompileError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 局部变量地址计算扫描器 */
public class LocalVarAdrScanner extends TreeScanner<LocalVarAdrContext>
{
    public void visitCompilationUnit(JCFileTree fileTree ) {
        fileTree.scan(this,null);
    }

    /** 文件字节码生成 */
    @Override
    public void visitCompilationUnit(JCFileTree fileTree  , LocalVarAdrContext arg) {
        for(JCFunction method:fileTree.functions)
            method.scan(this,arg);
        for(var method:fileTree.JCMacroDecls)
            method.scan(this,arg);
        for(var lbd:fileTree.lambdas)
            lbd.scan(this,arg);
    }

    /** 扫描函数 */
    @Override
    public void visitMethodDef(JCFunction tree, LocalVarAdrContext arg)
    {
        DMethodSymbol declMethodSymbol = tree.methodSymbol;
        /* 计算方法参数的地址 */
        /** 非静态函数要传递this,所以方法的参数要从1开始 */
        int startAdr = declMethodSymbol.isStatic?0:1;
        //if(arg==null)
            arg = new LocalVarAdrContext(startAdr,0);
        /* 按参数顺序依次给参数地址赋值 */
        for(int i=0;i<declMethodSymbol.getParameterCount();i++)
        {
            DVarSymbol symbol = declMethodSymbol.getParameterSymbol(i);
            setAdr(symbol,arg,tree);
        }
        setAdr(tree.getCallableCinfo().retVarSymbol,arg,tree);
        setAdr(tree.getCallableCinfo().lambdaVarArraySymbol ,arg,tree);
       // tree.getCallableCinfo().setMaxStack(1);
        /* 扫描函数体变量地址 */
        tree.body.scan(this,arg );
    }

    /** 扫描函数 */
    @Override
    public void visitMacroDef(JCMacroDecl tree, LocalVarAdrContext arg)
    {
        var declMethodSymbol = tree.macroSymbol;
        /* 计算方法参数的地址 */
        /** 非静态函数要传递this,所以方法的参数要从1开始 */
        int startAdr =0;// declMethodSymbol.isStatic?0:1;
       // if(arg==null)
        arg = new LocalVarAdrContext(startAdr,0);
        /* 按参数顺序依次给参数地址赋值 */
        for(int i=0;i<declMethodSymbol.getParameterCount();i++)
        {
            DVarSymbol symbol = declMethodSymbol.getParameterSymbol(i);
            setAdr(symbol,arg,tree);
        }
        //setAdr(tree.getRetVarSymbol(),arg,tree);
      //  setAdr(tree.getLambdaVarArraySymbol(),arg,tree);
        setAdr(tree.getCallableCinfo().retVarSymbol,arg,tree);
        setAdr(tree.getCallableCinfo().lambdaVarArraySymbol ,arg,tree);
       // tree.getCallableCinfo().setMaxStack(1);
        /* 扫描函数体变量地址 */
        tree.body.scan(this,arg );
    }

    /** 扫描函数 */
    @Override
    public void visitLambda(JCLambda tree, LocalVarAdrContext arg)
    {
        DMethodSymbol declMethodSymbol = tree.methodSymbol;
        /* 计算方法参数的地址 */
        /** 非静态函数要传递this,所以方法的参数要从1开始 */
        int startAdr = declMethodSymbol.isStatic?0:1;
        arg = new LocalVarAdrContext(startAdr,0);
        /* 对参数名称进行排序 */
        List<String> names = new ArrayList<String>();
        for(int i=0;i<declMethodSymbol.getParameterCount();i++)
        {
            DVarSymbol symbol = declMethodSymbol.getParameterSymbol(i);
            names.add(symbol.name);
        }
        Collections.sort(names);
        /* 按参数名称顺序给参数地址赋值 */
        for(int i=0;i<names.size();i++)
        {
            String name = names.get(i);
            DVarSymbol symbol = declMethodSymbol.parametersMap.get(name);
            setAdr(symbol,arg,tree);
        }

        setAdr(tree.getCallableCinfo().retVarSymbol,arg,tree);
        setAdr(tree.getCallableCinfo().lambdaVarArraySymbol ,arg,tree);
        /* 扫描函数体变量地址 */
        visitLambdaBody( tree.body,arg);
    }

    private void visitLambdaBody(JCBlock tree, LocalVarAdrContext arg)
    {
        LocalVarAdrContext newContext = new LocalVarAdrContext(arg.adr,arg.adr_lambda);
        if(tree.statements.size()==1)
        {
            tree.statements.get(0).setIsLambdaBodyOne(true);
        }
        for(int i=0;i< tree.statements.size();i++)
        {
            var stmt = tree.statements.get(i);
            //if(i==tree.statements.size()-1)
            //    stmt.setIsLambdaLast( true);
            stmt.scan(this,newContext);
        }
    }

    /** 扫描代码块 */
    @Override
    public void visitBlock(JCBlock tree, LocalVarAdrContext arg) {
        /* 一个代码块就对应一个作用域，需要创建一个新的 LocalVarAdrContext */
        LocalVarAdrContext newContext = new LocalVarAdrContext(arg.adr,arg.adr_lambda);
        for(JCTree stmt:tree.statements)
        {
           /* if(stmt instanceof JCExprStatement
                    || stmt instanceof JCIf
                    ||stmt instanceof JCWhile
                    ||stmt instanceof JCBlock
            )*/
                stmt.scan(this,newContext);
            //用新的LocalVarAdrContext进行计算
        }
    }

    /** 扫描表达式语句  */
    @Override
    public void visitExprStmt(JCExprStatement tree, LocalVarAdrContext arg)
    {
       // if(tree.expr instanceof JCVariableDecl)
            tree.expr.scan(this,arg);//只扫描变量声明表达式
    }

    /** 扫描变量声明表达式 */
    @Override
    public void visitVarDef(JCVariableDecl tree, LocalVarAdrContext arg) {
       // if(tree.getDimName().equals("value"))
       //     Debuger.outln("122 visitVarDef:"+ tree.getDimName() );
        setAdr((DVarSymbol)tree.nameExpr.symbol,arg,tree);
        if(tree.init!=null)
        {
            tree.init.scan(this,arg);
        }
    }

    /** 设置变量符号的地址 */
    private void setAdr(DVarSymbol symbol, LocalVarAdrContext arg,JCTree tree)
    {
        if(symbol==null)
            throw new CompileError();
        if(symbol.varKind == VarSymbolKind.FuncParameter
        ||symbol.varKind == VarSymbolKind.MacroParameter
        )
        {
            setAdrReal(symbol,arg,tree);
            if(symbol.isEmitLocalVarRef()) {
                setAdrLambdaReal(symbol,arg,tree);
            }
        }
        else if(symbol.varKind == VarSymbolKind.localVar )
        {
            if(symbol.isEmitLocalVarRef()) {
                setAdrLambdaReal(symbol,arg,tree);
            }
            else
            {
                setAdrReal(symbol,arg,tree);
            }
        }
       /* if(symbol.varKind == VarSymbolKind.FuncParameter ) {
            setAdrReal(symbol,arg,tree);
            if(symbol.isLambdaRefVar) {
                setAdrLambdaReal(symbol,arg,tree);
            }
        }
        else if(symbol.varKind == VarSymbolKind.MacroParameter ) {
            setAdrReal(symbol,arg,tree);
            if(symbol.isLambdaRefVar) {
                setAdrLambdaReal(symbol,arg,tree);
            }
        }
        else if(  symbol.varKind==VarSymbolKind.localVar) {
            if(symbol.isLambdaRefVar) {
                setAdrLambdaReal(symbol,arg,tree);
            }
            else
            {
                setAdrReal(symbol,arg,tree);
            }
        }*/
    }

    void setAdrLambdaReal(DVarSymbol symbol, LocalVarAdrContext arg,JCTree tree)
    {
        if(symbol.adr_lambda!=-1) return;
        symbol.adr_lambda = arg.adr_lambda; //设置参数地址
        arg.adr_lambda++; //地址自增1
        //var maxAdr = Math.max(tree.belongsInfo.topFunc.getCallableCinfo().lambdaVarCount, arg.adr_lambda);
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxLambdaVarCount(arg.adr_lambda);
    }

    void setAdrReal(DVarSymbol symbol, LocalVarAdrContext arg,JCTree tree)
    {
        if(symbol.adr!=-1) return;
        symbol.adr = arg.adr; //设置参数地址
        arg.adr++; //地址自增1
        if(tree.belongsInfo==null || tree.belongsInfo.topFunc==null)
            throw new CompileError();
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxLocals(arg.adr);
    }

    /** 生成函数调用表达式 */
    @Override
    public void visitMacroCall(JCMacroCall tree, LocalVarAdrContext arg) {
        //tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack( tree.getArgValues().length);
        for (JCExpression jcExpression : tree.getArgValues()) //生成参数
           jcExpression.scan(this,arg);
    }

    public void visitWhileLoop(JCWhile tree, LocalVarAdrContext arg)
    {
      //  tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack( 1);
        visitTree(tree.cond,arg);
        visitTree(tree.body,arg);
    }

    public void visitIf(JCIf tree, LocalVarAdrContext arg)
    {
     //   tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack( 1);
        visitTree(tree.cond,arg);
        visitTree(tree.thenpart,arg);
        visitTree(tree.elsepart,arg);
    }

    public void visitReturn(JCReturn tree, LocalVarAdrContext arg)
    {
        visitTree(tree.expr,arg);
    }

    public void visitMethodInvocation(JCMethodInvocation tree, LocalVarAdrContext arg)
    {
       // tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(tree.args.length+1);
        visitTree(tree.methodExpr,arg);
        for(JCExpression expression:tree.getArgs())
        {
            visitTree(expression,arg);
        }
    }

    public void visitArrayLiteral(JCArrayLiteral tree, LocalVarAdrContext arg)
    {
        for(JCExpression expression:tree.elements)
        {
            visitTree(expression,arg);
        }
    }
/*
    public void visitParenExpr(ParenExpr tree, LocalVarAdrContext arg)
    {
        for(JCExpression expression:tree.items)
        {
            visitTree(expression,arg);
        }
    }*/

    public void visitParens(JCParens tree, LocalVarAdrContext arg)
    {

        visitTree(tree.expr,arg);
    }

    public void visitAssign(JCAssign tree, LocalVarAdrContext arg)
    {
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }

    public void visitUnary(JCUnary tree, LocalVarAdrContext arg)
    {
        //tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(1);
        visitTree(tree.expr,arg);
    }

    public void visitBinary(JCBinary tree, LocalVarAdrContext arg)
    {
      //  tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(2);
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }

    public void visitPair(JCPair tree, LocalVarAdrContext arg)
    {
        //tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(2);
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }
    
    public void visitFieldAccess(JCFieldAccess tree, LocalVarAdrContext arg)
    {
      //  tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(2);
        visitTree( tree.selected,arg);
    }

    public void visitIdent(JCIdent tree, LocalVarAdrContext arg)
    {
        if(tree.symbol instanceof  DVarSymbol)
            setAdr((DVarSymbol)tree.symbol,arg,tree);
       // tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(1);
        return;
    }

    public void visitLiteral(JCLiteral tree, LocalVarAdrContext arg)
    {
        //tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(1);
    }

    @Override
    public void visitNewClass(MKNewClass tree, LocalVarAdrContext arg)
    {

    }

    public void visitArrayAccess(MKArrayAccess tree, LocalVarAdrContext arg)
    {

    }
}
