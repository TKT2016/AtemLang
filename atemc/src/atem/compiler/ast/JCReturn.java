package atem.compiler.ast;

/**
 * 返回语句
 */
public class JCReturn extends JCStatement
{
    /** 返回的表达式，可以为null */
    public JCExpression expr;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitReturn(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateReturn(this, arg);
    }

}
