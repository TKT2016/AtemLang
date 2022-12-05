package atem.compiler.ast;


/** 如果否则语句 "if ( ) { } else { }"  */
public class JCIf extends JCStatement
{
    /** 条件表达式 */
    public JCExpression cond;

    /** 为true语句 */
    public JCStatement thenpart;

    /** 为false语句 */
    public JCStatement elsepart;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitIf(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateIf(this, arg);
    }
}
