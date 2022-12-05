package atem.compiler.ast;

import atem.compiler.tools.ListUtil;

import java.util.ArrayList;

/** 方法调用表达式 */
public class JCMethodInvocation extends JCExpression
{
    //public boolean isEmptyArgs;

    /** 函数名称表达式 */
    public JCExpression methodExpr;

    /** 函数实参列表 */
    private JCExpression[] args;

    public void setArgs(JCExpression[] exprs)
    {
        if(exprs.length==1 && TreeeUtil.isEmptyParens(exprs[0]))
        {
            this.args = new JCExpression[]{};
        }
        else {
            ArrayList<JCExpression> list = new ArrayList<>();
            for (var item : exprs) {
                if(TreeeUtil.isEmptyParens(item))
                {
                    item.error("函数调用的括号表达式内不能为空");
                }
                else
                    list.add(item);
                item.setIsMacroCallArg(false);
            }
            this.args = ListUtil.toExprArray(list);
        }
    }

    public  JCExpression[] getArgs()
    {
        return args;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitMethodInvocation(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateMethodInvocation(this, arg);
    }

    //public boolean isInstanceof;
}
