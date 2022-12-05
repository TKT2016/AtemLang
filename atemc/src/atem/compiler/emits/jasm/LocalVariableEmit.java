package atem.compiler.emits.jasm;

import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.emits.SymbolSignatureUtil;
import atem.compiler.symbols.*;
import atem.compiler.ast.*;
import atem.compiler.ast.TreeScanner;
import atem.compiler.utils.CompileError;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/** 函数内变量信息生成 */
public class LocalVariableEmit extends TreeScanner<MethodVisitor> {
    /* 变量信息生成 */
    public void emitVars(JCFunction tree, MethodVisitor mv) {
        visitMethodDef(tree, mv);
    }

    public void emitVars(JCLambda tree, MethodVisitor mv) {
        visitLambda(tree, mv);
    }

    public void emitVars(JCMacroDecl tree, MethodVisitor mv) {
        visitMacroDef(tree, mv);
    }

    /** 设置变量符号的地址 */
    private void emitVarLabel(DVarSymbol varSymbol, MethodVisitor mv) {
        if(varSymbol.isTempVar )
            return;

        if(varSymbol.isLambdaRefVar)
            return;
        if(varSymbol.varKind== VarSymbolKind.field)
            return;
        if(varSymbol.isEmitLocalVarRef())
            return;
        /* 变量名称 */
        String name = varSymbol.name;
        /* 变量类型签名 */
        String sign = SymbolSignatureUtil.getParamsSignature(varSymbol.varType, true);
        /* 变量作用域开始标签 */
        SymbolScope scope = varSymbol.dimTree.getScope();
        Label startLabel=scope.getStartLabel(); ;
        /* 变量作用域结束标签 */
        Label endLabel= scope.getEndLabel();
        /* 变量地址 */
        int adr = varSymbol.adr;
        if(adr<0)
            throw new CompileError();

        mv.visitLocalVariable(name, sign, null, startLabel, endLabel, adr);
    }

    /** 扫描函数*/
    @Override
    public void visitLambda(JCLambda tree, MethodVisitor mv) {
        DMethodSymbol declMethodSymbol = tree.methodSymbol;
        /* 按参数顺序依次给参数地址赋值 */
        for (int i = 0; i < declMethodSymbol.getParameterCount(); i++) {
            DVarSymbol varSymbol = declMethodSymbol.getParameterSymbol(i);
            emitVarLabel(varSymbol, mv);
        }
        //emitThis(mv,tree);
        emitVarLabel(tree.getCallableCinfo().retVarSymbol , mv);
        emitVarLabel(tree.getCallableCinfo().lambdaVarArraySymbol  , mv);
        /* 扫描函数体变量地址 */
        tree.body.scan(this, mv);
    }
/*
    void emitThis(MethodVisitor mv,JCLambda lambda)
    {

        String name = "this";

        String sign = SymbolSignatureUtil.getParamsSignature(lambda.belongsInfo.fileTree.fileInnerClassSymbol, true);

        SymbolScope scope = lambda.belongsInfo.getScope();
        Label startLabel=scope.getStartLabel(); ;

        Label endLabel= scope.getEndLabel();

        int adr = 0;
        mv.visitLocalVariable(name, sign, null, startLabel, endLabel, adr);
    }
*/
    /** 扫描函数*/
    @Override
    public void visitMethodDef(JCFunction tree, MethodVisitor mv) {
        DMethodSymbol declMethodSymbol = tree.methodSymbol;
        /* 按参数顺序依次给参数地址赋值 */
        for (int i = 0; i < declMethodSymbol.getParameterCount(); i++) {
            DVarSymbol varSymbol = declMethodSymbol.getParameterSymbol(i);
            emitVarLabel(varSymbol, mv);
        }
        emitVarLabel(tree.getCallableCinfo().retVarSymbol , mv);
        emitVarLabel(tree.getCallableCinfo().lambdaVarArraySymbol  , mv);
        /* 扫描函数体变量地址 */
        tree.body.scan(this, mv);
    }

    /** 扫描函数*/
    @Override
    public void visitMacroDef(JCMacroDecl tree, MethodVisitor mv) {
        var declMethodSymbol = tree.macroSymbol;
        /* 按参数顺序依次给参数地址赋值 */
        for (int i = 0; i < declMethodSymbol.getParameterCount(); i++) {
            DVarSymbol varSymbol = declMethodSymbol.getParameterSymbol(i);
            emitVarLabel(varSymbol, mv);
        }
     //   emitVarLabel(tree.retSymbol, mv);
        /* 扫描函数体变量地址 */
        tree.body.scan(this, mv);
    }

    /** 扫描变量声明表达式 */
    @Override
    public void visitVarDef(JCVariableDecl tree, MethodVisitor mv) {
        emitVarLabel((DVarSymbol) tree.nameExpr.symbol, mv);
    }

    /**
     * 扫描代码块
     */
    @Override
    public void visitBlock(JCBlock tree, MethodVisitor mv) {
        for (JCTree stmt : tree.statements) {
            /*if (stmt instanceof JCExprStatement
                    || stmt instanceof JCIf
                    || stmt instanceof JCWhile
                    || stmt instanceof JCBlock)*/
                stmt.scan(this, mv);
        }
    }

    /**
     * 扫描表达式语句
     */
    @Override
    public void visitExprStmt(JCExprStatement tree, MethodVisitor mv) {
        if (tree.expr instanceof JCVariableDecl)
            tree.expr.scan(this, mv);//只扫描变量声明表达式
    }
}
