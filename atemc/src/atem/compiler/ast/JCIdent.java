package atem.compiler.ast;

import atem.compiler.CompilerConsts;
import atem.compiler.emits.jasm.RTSigns;
import atem.compiler.lex.Token;

/** 标识符表达式 */
public class JCIdent extends JCExpression //implements IValueSetGet
{
    /** 标识符名称标记 */
    public Token nameToken;

    public final boolean isThis;

    public final boolean isDollarIdent;

    public JCIdent(Token token)
    {
        nameToken = token;
        isThis = getName().equals(CompilerConsts.Self);
        isDollarIdent = getName().startsWith("$");
    }

    public JCIdent( )
    {
        isThis = false;
        isDollarIdent=false;
    }

    public String getName()
    {
        return  nameToken.identName;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitIdent(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateIdent(this, arg);
    }

    public boolean isTypeName;

    public boolean isExpandCreated;
}
