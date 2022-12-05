package atem.compiler.ast;

/** 括号表达式 */
public class JCParens extends JCExpression
{
    /** 括号内表达式 */
    public JCExpression expr;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitParens(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateParens(this, arg);
    }

    public boolean isEmpty()
    {
        return expr==null;
    }

    @Override
    public void setIsMacroCallArg(boolean isMacroCallArg)
    {
        propertys.isMacroCallArg =isMacroCallArg;
        if(expr!=null)
            expr.setIsMacroCallArg(isMacroCallArg);//setIsMacroCallArg(isMacroCallArg);
    }
}