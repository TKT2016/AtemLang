package atem.compiler.optimizers;

import atem.compiler.symbols.BVarSymbol;
import atem.compiler.ast.TreeTranslator;
import atem.compiler.ast.*;
import java.util.ArrayList;

/** 变量赋值优化 */
public class AssignOptimizer extends TreeTranslator<AssignContext> {

    /** 优化语句块 */
    @Override
    public JCTree translateBlock(JCBlock tree, AssignContext arg)
    {
        AssignContext context = new AssignContext();
        for (JCStatement statement : tree.statements)
        {
            /* 把当前语句代入到AssignContext中 */
            context.statement = statement;
            statement.translate(this, context);
            if(statement instanceof JCIf
                    || statement instanceof JCWhile
                    || statement instanceof JCBlock
            )
            {
                context.clear(); //清空常量表
            }
        }
        /** 过滤去除无效语句 */
        /* 放置有效语句 */
        ArrayList<JCStatement> newStatements2 = new  ArrayList<JCStatement>();
        for (JCStatement statement :  tree.statements)
        {
           if(context.isEffective(statement))
               newStatements2.add(statement);
        }
        /* 语句设为新的有效语句 */
        tree.statements = newStatements2;
        return tree;
    }

    /** 分析赋值表达式 */
    @Override
    public JCTree translateAssign(JCAssign tree, AssignContext arg)
    {
        tree.right = translate(tree.right,arg);
        if(OptimizerUtil.isVar(tree.left))
        {
            BVarSymbol varSymbol = OptimizerUtil.getVarSymbol(tree.left);
            arg.setWrite(varSymbol, arg.statement);
        }
        return tree;
    }

    /** 分析标识符表达式 */
    @Override
    public JCTree translateIdent(JCIdent tree, AssignContext arg)
    {
        if(OptimizerUtil.isVar(tree))
        {
            /* 如果标识符是变量符号,则设它读取过 */
            BVarSymbol varSymbol = OptimizerUtil.getVarSymbol(tree);
            arg.setRead(varSymbol);
        }
        return tree;
    }
}
