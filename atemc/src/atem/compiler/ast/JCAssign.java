package atem.compiler.ast;

/** 赋值表达式 "=" */
public class JCAssign extends JCExpression
{
    /** 左边被赋值表达式 */
    public JCExpression left;
    /** 右边值表达式 */
    public JCExpression right;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitAssign(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateAssign(this, arg);
    }

}
