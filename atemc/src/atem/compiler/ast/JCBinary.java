package atem.compiler.ast;

import atem.compiler.lex.TokenKind;

/** 二元运算表达式 */
public class JCBinary extends JCExpression
{
    /** 运算符 */
    public TokenKind opcode;

    /** 左表达式 */
    public JCExpression left;

    /** 右表达式 */
    public JCExpression right;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitBinary(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateBinary(this, arg);
    }

    public boolean isStringContact = false;

}
