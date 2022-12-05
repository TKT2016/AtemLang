package atem.compiler.ast;

import atem.compiler.lex.TokenKind;

/** 一元运算表达式 */
public class JCUnary extends JCExpression
{
    /** 运算符 */
    public TokenKind opcode;

    /** 右表达式 */
    public JCExpression expr;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitUnary(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateUnary(this, arg);
    }
}
