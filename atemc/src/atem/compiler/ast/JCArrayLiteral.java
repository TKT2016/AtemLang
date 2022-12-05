package atem.compiler.ast;

public class JCArrayLiteral extends JCExpression
{
    /** 值表达式 */
    public JCExpression[] elements;

    public JCArrayLiteral(JCExpression[] elements) {
        this.elements = elements;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitArrayLiteral(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateArrayLiteral(this, arg);
    }

    public JCPair defaultPair;
}
