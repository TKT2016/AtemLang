package atem.compiler.ast;

/** 声明变量表达式 */
public class JCVariableDecl extends JCExpression
{
    /** 变量名称 */
    public  JCIdent nameExpr;

    /** 变量初始值 */
    public JCExpression init;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitVarDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateVariable(this, arg);
    }

    @Override
    public void setIsMacroCallArg(boolean isMacroCallArg)
    {
        propertys.isMacroCallArg =isMacroCallArg;
        nameExpr.setIsMacroCallArg(isMacroCallArg);
    }

    public boolean isFileStaticField=true;

    public ExpandKindEnum expandKind  =ExpandKindEnum.NONE ;
    public Object expandInfo;

    public enum ExpandKindEnum
    {
        NONE,
        StaticType,
        LambdaVarGet,
        DotMember,
        LambdaRef
    }
}
