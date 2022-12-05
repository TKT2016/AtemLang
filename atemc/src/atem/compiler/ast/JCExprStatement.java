package atem.compiler.ast;

import atem.compiler.utils.Debuger;

/** 表达式语句 */
public class JCExprStatement extends JCStatement
{
    /** 语句的表达式*/
    public JCExpression expr;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitExprStmt(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateExprStmt(this, arg);
    }
/*
    @Override
    public void setIsLambdaLast(boolean isLambdaLast)
    {
        this.isLambdaLast =isLambdaLast;
        expr.isLambdaLast  = isLambdaLast;
        //Debuger.outln("24 setIsLambdaLast:"+expr+" "+expr.posToken.line);
    }*/

    @Override
    public void setIsLambdaBodyOne(boolean isLambdaBodyOne)
    {
        propertys.isLambdaBodyOne =isLambdaBodyOne;
        expr.setIsLambdaBodyOne(isLambdaBodyOne) ;
    }
}
