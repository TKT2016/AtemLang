package atem.compiler.optimizers;

import atem.compiler.ast.TreeTranslator;
import atem.compiler.ast.*;
import java.util.ArrayList;

/** return 语句后死代码消除 */
class ReturnDeadCodeOptimizer extends TreeTranslator<Object> {

    @Override
    public JCTree translateBlock(JCBlock tree, Object arg)
    {
        tree =(JCBlock) optimize(tree , arg);
        return tree;
    }

    @Override
    public JCTree translateIf(JCIf tree, Object arg)
    {
        tree.thenpart= optimize(tree.thenpart , arg);
        tree.elsepart= optimize(tree.elsepart , arg);
        return tree;
    }

    @Override
    public JCTree translateWhile(JCWhile tree, Object arg)
    {
        tree.body= optimize(tree.body , arg);
        return tree;
    }

    JCStatement optimize(JCStatement tree , Object arg)
    {
        if(tree!=null && tree instanceof JCBlock)
        {
            JCBlock jcBlock = (JCBlock) tree;
            ArrayList<JCStatement> newStatements = new ArrayList<JCStatement>();
            for (JCStatement statement : jcBlock.statements) {
                if(statement instanceof JCReturn)
                {
                    newStatements.add(statement);
                    break; //如果当前语句是return语句,跳出循环
                }
                else {
                    JCStatement newStatement = (JCStatement) statement.translate(this, arg);
                    newStatements.add(newStatement);
                }
            }
            jcBlock.statements = newStatements;
            tree = jcBlock;
        }
        return tree;
    }
}
