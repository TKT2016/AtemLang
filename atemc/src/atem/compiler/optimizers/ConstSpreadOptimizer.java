package atem.compiler.optimizers;

import atem.compiler.symbols.BVarSymbol;
import atem.compiler.symbols.DVarSymbol;
import atem.compiler.ast.*;
import atem.compiler.ast.TreeTranslator;
import java.util.ArrayList;

/** 常量传播优化(只优化变量) */
public class ConstSpreadOptimizer extends TreeTranslator<ConstSpreadContext> {

    /** 优化代码块 */
    @Override
    public JCTree translateBlock(JCBlock tree, ConstSpreadContext arg)
    {
        ConstSpreadContext context = new ConstSpreadContext();
        /* 保存优化后的语句 */
        ArrayList<JCStatement> newStatements = new  ArrayList<JCStatement>();
        /* 优化代码块中每一个语句 */
        for (JCStatement statement : tree.statements)
        {
            JCStatement newStatement = (JCStatement) statement.translate(this, context);
            newStatements.add(newStatement);
            /* 遇到可能还有代码块的语句，清空常量值表(简单优化) */
            if(newStatement instanceof JCIf
                    || newStatement instanceof JCWhile
                    || newStatement instanceof JCBlock
            )
            {
                context.clear(); //清空常量值表
            }
        }
        /* 代码块内语句设置为优化后的 */
        tree.statements = newStatements;
        return tree;
    }
    /** 优化if语句 */
    @Override
    public JCTree translateIf(JCIf tree, ConstSpreadContext arg)
    {
        tree.cond = translate( tree.cond,arg);
        tree.thenpart = translate( tree.thenpart,arg);
        tree.elsepart = translate( tree.elsepart,arg);
        return tree;
    }

    /** 优化while循环语句 */
    @Override
    public JCTree translateWhile(JCWhile tree, ConstSpreadContext arg)
    {
        /* 循环的条件表达式不能优化，循环体执行一次后条件表达式值就可能改变 */
        tree.body = translate( tree.body,arg);
        return tree;
    }

    /** 优化变量声明 */
    @Override
    public JCTree translateVariable(JCVariableDecl tree, ConstSpreadContext arg) {
        tree.init = translate(tree.init,arg);
        /* 变量声明后赋值情况下，要记录这个变量的常量值 */
        if (tree.init != null) {
            Object constValue = arg.getConstValue(tree.init);
            arg.setConstValue((DVarSymbol)tree.nameExpr.symbol ,constValue);
        }
        return tree;
    }

    /* 优化变量赋值 */
    @Override
    public JCTree translateAssign(JCAssign tree, ConstSpreadContext arg)
    {
        tree.right = translate(tree.right,arg);
        /* 赋值语句只优化左边是变量的情况 */
        if(OptimizerUtil.isVar(tree.left))
        {
            BVarSymbol varSymbol = OptimizerUtil.getVarSymbol(tree.left);
            Object constValue = arg.getConstValue(tree.right);
            /* 保存这一步的变量的值 */
            arg.setConstValue(varSymbol,constValue);
        }
        return tree;
    }

    /** 优化标识符 */
    @Override
    public JCTree translateIdent(JCIdent tree, ConstSpreadContext arg)
    {
        /* 如果确定这个标识符是变量 */
        if(OptimizerUtil.isVar(tree))
        {
            /*如果这个变量在这一步有常量值*/
            Object constValue = arg.getConstValue(tree);
            if(constValue!=null)
                /* 以常量值生成一个新的JCLiteral返回,替代原先的标识符 */
                return OptimizerUtil.newLiteral(tree,constValue);
        }
        return tree;
    }

    /** 优化一元运算符表达式 */
    @Override
    public JCTree translateUnary(JCUnary tree, ConstSpreadContext arg)
    {
        tree.expr = translate( tree.expr,arg);
        /* 如果 tree.expr有常量值,会计算出一个确定的值返回 */
        return (new JCUnaryConstCalculator()).translate(tree);
    }

    /** 优化二元运算符表达式 */
    @Override
    public JCTree translateBinary(JCBinary tree, ConstSpreadContext arg)
    {
        /* 优化左表达式 */
        tree.left = translate(tree.left, arg);
        /* 优化右表达式 */
        tree.right = translate(tree.right, arg);
        /* 如果 左右都有常量值,会计算出一个确定的值返回 */
        return ( new JCBinaryConstCalculator()).translate(tree);
    }
}
