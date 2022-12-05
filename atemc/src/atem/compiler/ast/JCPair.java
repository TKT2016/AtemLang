package atem.compiler.ast;


/** 键值对表达式 */
public class JCPair extends JCExpression
{
    /** 左表达式 */
    public JCExpression left;

    /** 右表达式 */
    public JCExpression right;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitPair(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translatePair(this, arg);
    }

    public boolean isDynamicMember = false;

    public JCArrayLiteral arrayLiteral;
    public boolean isMapDefaultItem;
}
