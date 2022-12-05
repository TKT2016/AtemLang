package atem.compiler.ast;

/**
 * java 包
 */
public class JCPackage extends JCTree implements SourceFileSection
{
    /* 包名称表达式 */
    public JCExpression packageName;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitPackageDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translatePackage(this, arg);
    }

}
