package atem.compiler.ast;


/** 字面常量表达式 */
public class JCLiteral extends JCExpression
{
    /** 值表达式 */
    public Object value;

    public boolean isNullLiteral;

    public JCLiteral(Object value) {
        this.value = value;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitLiteral(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateLiteral(this, arg);
    }


}
