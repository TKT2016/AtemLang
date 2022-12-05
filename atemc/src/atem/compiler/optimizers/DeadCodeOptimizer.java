package atem.compiler.optimizers;

import atem.compiler.ast.TreeTranslator;
import atem.compiler.ast.*;
import java.util.ArrayList;

/** 其它死代码消除 */
public class DeadCodeOptimizer extends TreeTranslator<Object> {
    /** 优化代码块 */
    @Override
    public JCTree translateBlock(JCBlock tree, Object arg) {
        ArrayList<JCStatement> newStatements = new ArrayList<JCStatement>();
        for (JCStatement statement : tree.statements)
        {
            if (statement instanceof JCIf || statement instanceof JCWhile) {
                JCStatement newStatement = (JCStatement) statement.translate(this, arg);
                if(newStatement!=null)
                    newStatements.add(newStatement);
            }
            else {
                newStatements.add(statement);
            }
        }
        tree.statements = newStatements;
        return tree;
    }

    /** 优化IF语句 */
    @Override
    public JCTree translateIf(JCIf tree, Object arg) {
        if (OptimizerUtil.isTrue(tree.cond))
            return tree.elsepart.translate(this, arg);
        if (OptimizerUtil.isFalse(tree.cond))
            return tree.thenpart.translate(this, arg);

        tree.thenpart = translate(tree.thenpart, arg);
        tree.elsepart = translate(tree.elsepart, arg);
        return tree;
    }

    /** 优化WHILE循环语句 */
    @Override
    public JCTree translateWhile(JCWhile tree, Object arg)
    {
        if(OptimizerUtil.isFalse(tree.cond))
            return null;
        return tree;
    }
}
