package atem.compiler.ast;

import java.util.ArrayList;

public class JCDynamicLiteral extends JCExpression
{
    public ArrayList<JCPair> pairs;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitDynamicLiteral(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateDynamicLiteral(this, arg);
    }
}