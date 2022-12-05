package atem.compiler.ast;

/** while循环语句  */
public class JCWhile extends JCStatement
{
    /** 循环条件表达式 */
    public JCExpression cond;

    /** 循环体 */
    public JCStatement body;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitWhileLoop(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateWhile(this, arg);
    }
    
}