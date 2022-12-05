package atem.compiler.optimizers;

import atem.compiler.ast.*;
import atem.compiler.ast.TreeTranslator;

/** 常量折叠优化 */
public class ConstFoldOptimizer extends TreeTranslator<Object> {

    /** 优化一元运算表达式 */
    @Override
    public JCTree translateUnary(JCUnary tree, Object arg)
    {
        /* 优化运算符后的表达式 */
        tree.expr = translate( tree.expr,arg);
        return ( new JCUnaryConstCalculator()).translate(tree);
    }

    /** 优化二元运算表达式 */
    @Override
    public JCTree translateBinary(JCBinary tree, Object arg)
    {
        /* 优化左表达式 */
        tree.left = translate(tree.left, arg);
        /* 优化右表达式 */
        tree.right = translate(tree.right, arg);

        return ( new JCBinaryConstCalculator()).translate(tree);
    }
}
