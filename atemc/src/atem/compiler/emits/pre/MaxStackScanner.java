package atem.compiler.emits.pre;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.makes.JCFieldAccess;

import atem.compiler.symbols.*;
import atem.compiler.utils.CompileError;
import atem.lang.rt.InterpreterError;


/** 局部变量地址计算扫描器 */
public class MaxStackScanner extends TreeScanner<MaxStackContext>
{
    public void visitCompilationUnit(JCFileTree fileTree ) {
        fileTree.scan(this,null);
    }

    /** 文件字节码生成 */
    @Override
    public void visitCompilationUnit(JCFileTree fileTree  , MaxStackContext arg) {
        for(JCFunction method:fileTree.functions)
            method.scan(this,arg);
        for(var method:fileTree.JCMacroDecls)
            method.scan(this,arg);
    }

    /** 扫描函数 */
    @Override
    public void visitMethodDef(JCFunction tree, MaxStackContext arg)
    {
        var callable = tree.belongsInfo.topFunc.getCallableCinfo();
        callable.setMaxLocals(1);
        arg= new MaxStackContext( tree.belongsInfo.topFunc);
        scanStack(tree.body,arg);
    }

    /** 扫描函数 */
    @Override
    public void visitMacroDef(JCMacroDecl tree, MaxStackContext arg)
    {
        var callable = tree.belongsInfo.topFunc.getCallableCinfo();
        callable.setMaxLocals(1);
        arg = new MaxStackContext( tree.belongsInfo.topFunc);
        scanStack(tree.body,arg);
    }

    /** 扫描函数 */
    @Override
    public void visitLambda(JCLambda tree, MaxStackContext arg)
    {
        var callable = tree.belongsInfo.topFunc.getCallableCinfo();
        callable.setMaxLocals(2);
        arg = new MaxStackContext( tree.belongsInfo.topFunc);
        scanStack(tree.body,arg);
    }

    /** 扫描代码块 */
    @Override
    public void visitBlock(JCBlock tree, MaxStackContext arg) {
        for(JCTree stmt:tree.statements) {
            scanStack(stmt, arg);
        }
    }

    /** 扫描表达式语句  */
    @Override
    public void visitExprStmt(JCExprStatement tree, MaxStackContext arg)
    {
        tree.expr.scan(this,arg);//只扫描变量声明表达式
    }

    /** 扫描变量声明表达式 */
    @Override
    public void visitVarDef(JCVariableDecl tree, MaxStackContext arg) {
        scanStack(tree.init,arg);
    }

    /** 生成函数调用表达式 */
    @Override
    public void visitMacroCall(JCMacroCall tree, MaxStackContext arg) {
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack( tree.getArgValues().length);
        for (JCExpression jcExpression : tree.getArgValues()) //生成参数
           jcExpression.scan(this,arg);
    }

    public void visitWhileLoop(JCWhile tree, MaxStackContext arg)
    {
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack( 1);
        scanStack(tree.cond,arg);
        scanStack(tree.body,arg);
    }

    public void visitIf(JCIf tree, MaxStackContext arg)
    {
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack( 1);
        scanStack(tree.cond,arg);
        scanStack(tree.thenpart,arg);
        scanStack(tree.elsepart,arg);
    }

    public void visitReturn(JCReturn tree, MaxStackContext arg)
    {
        scanStack(tree.expr,arg);
    }

    public void visitMethodInvocation(JCMethodInvocation tree, MaxStackContext arg)
    {
        var callable = tree.belongsInfo.topFunc.getCallableCinfo();
        int count=0;
        count+= scanStack( tree.methodExpr,arg);
        int i=1;
        int max =0;
        for (JCExpression jcExpression : tree.getArgs())
        {
            var s2  = scanStack(jcExpression,arg);
            max = Math.max(max , i+s2);
            i++;
        }
        count+=max;
        /*if(tree.methodExpr.symbol instanceof BMethodSymbol)
        {
            count+= tree.args.length;
         }
        else
        {
            count+=2;
        }*/
        callable.setMaxStack(count);
    }

    public void visitArrayLiteral(JCArrayLiteral tree, MaxStackContext arg)
    {
        int count=0;
        for(var element:tree.elements)
        {
            var sum=0;
            sum+=1;
            var s2  = scanStack(element,arg);
            sum+=s2;
            count= Math.max(count,sum);
        }
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(count);
    }
/*
    public void visitParenExpr(ParenExpr tree, MaxStackContext arg)
    {
        int count=0;
        for(JCExpression expression:tree.items)
        {
          var c1 = scanStack(expression,arg);
          count+=c1;
        }
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(count);
    }*/

    public void visitParens(JCParens tree, MaxStackContext arg)
    {
        visitTree(tree.expr,arg);
    }

    public void visitAssign(JCAssign tree, MaxStackContext arg)
    {
        int count=0;
        if(tree.left instanceof JCFieldAccess)
        {
            JCFieldAccess jcFieldAccess = (JCFieldAccess)tree.left;
            count += scanStack(jcFieldAccess.selected,arg);
            count += scanStack(tree.right,arg);
        }
        else if(tree.left instanceof JCIdent)
        {
            count += scanStack(tree.right,arg);
        }
        /*else if(tree.left.symbol .equals(RClassSymbolManager.VarDimInfoSymbol))
        {
            count += scanStack(tree.right,arg);
        }*/
        else
        {
            throw new CompileError();
        }
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(count);
    }

    public void visitUnary(JCUnary tree, MaxStackContext arg)
    {
        scanStack(tree.expr,arg);
    }

    public void visitBinary(JCBinary tree, MaxStackContext arg)
    {
        int count=0;
        count += scanStack(tree.left,arg);
        count += scanStack(tree.right,arg);
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(count);
    }

    public void visitPair(JCPair tree, MaxStackContext arg)
    {
        int count=0;
        count += scanStack(tree.left,arg);
        count += scanStack(tree.right,arg);
         tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(count);
    }
    
    public void visitFieldAccess(JCFieldAccess tree, MaxStackContext arg)
    {
        int left1 = scanStack(tree.selected,arg);
        int count =left1+1;
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(count);
    }

    public void visitIdent(JCIdent tree, MaxStackContext arg)
    {
        int count =0;
        Symbol symbol = tree.symbol;

        if (symbol instanceof BTypeSymbol) {
            count=2;
        }
        else if (symbol instanceof BMethodSymbol) {
            BMethodSymbol bMethodSymbol =(BMethodSymbol)symbol;
            if(bMethodSymbol.isStatic==false)
                count=1;
        }
        else  if (symbol instanceof DVarSymbol) {
            /* 情况2:是自定义变量符号, 取出这个变量的地址和类型对应lload指令生成 */
            DVarSymbol declVarSymbol = (DVarSymbol) symbol;
            if(declVarSymbol.varKind== VarSymbolKind.FuncParameter ||declVarSymbol.varKind== VarSymbolKind.localVar)
            {
                if(declVarSymbol.isLambdaRefVar) {
                    count=2;
                }
                else
                {
                    count=1;
                }
            }
            if(declVarSymbol.varKind== VarSymbolKind.MacroParameter )
            {
                    count=2;
            }
            else if(declVarSymbol.varKind== VarSymbolKind.field)
            {
                count=1;
            }
            else
            {
                throw new InterpreterError("暂不实现this");
            }
        }
        else
        {
            throw new CompileError();
        }
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(count);
    }

    public void visitLiteral(JCLiteral tree, MaxStackContext arg)
    {
        arg.seStack(1);
    }

    private int scanStack(JCTree tree,  MaxStackContext arg)
    {
        if(tree==null) return -1;
        MaxStackContext arg2 = arg.clone();
        tree.scan(this,arg2);
        tree.belongsInfo.topFunc.getCallableCinfo().setMaxStack(arg2.result);
        return arg2.result;
    }
}
