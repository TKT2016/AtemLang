package atem.compiler.optimizers;
import atem.compiler.lex.TokenKind;
import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCUnary;
import atem.compiler.utils.CompileError;
/** 一元运算表达式常量运算器 */
class JCUnaryConstCalculator {
    /** 优化JCUnary  */
    public JCExpression translate(JCUnary tree )
    {
        TokenKind opcode = tree.opcode;
        /* 取出运算符右表达式常量值 */
        Object value = OptimizerUtil.getLiteralValue(tree.expr);
        /* 1:右表达式是常量值 */
        if(value!=null)
        {
            if (opcode.equals(TokenKind.ADD)) {
                int resultValue = ((Integer) value).intValue();
                return OptimizerUtil.newIntLiteral(tree, resultValue);
            }
            else if (opcode.equals(TokenKind.SUB)) {
                int resultValue = -((Integer) value).intValue();
                return OptimizerUtil.newIntLiteral(tree, resultValue);
            }
            else if (opcode.equals(TokenKind.NOT)) {
                boolean resultValue = !( (Boolean) value).booleanValue();
                return OptimizerUtil.newBooleanLiteral(tree, resultValue);
            }
            else
                throw new CompileError();
        }
        else if(opcode.equals(TokenKind.ADD))
                return tree.expr;
        else
            return tree;
    }
}
