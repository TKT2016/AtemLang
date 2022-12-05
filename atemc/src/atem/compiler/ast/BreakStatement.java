package atem.compiler.ast;

public class BreakStatement extends JCStatement
{
    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitBreak(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateBreak(this, arg);
    }
}
