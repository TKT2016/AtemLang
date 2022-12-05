package atem.compiler.ast.makes;

import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCTree;
import atem.compiler.ast.TreeScanner;
import atem.compiler.ast.TreeTranslator;

/** 数组访问表达式 */
public class MKArrayAccess extends JCExpression
{
    /** 数组变量 */
    public JCExpression indexed;
    /** 数组索引表达式 */
    public JCExpression index;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg){ v.visitArrayAccess(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.visitArrayAccess(this, arg);
    }
}
