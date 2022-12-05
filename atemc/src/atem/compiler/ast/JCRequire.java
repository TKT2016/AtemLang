package atem.compiler.ast;

import atem.compiler.ast.makes.JCFieldAccess;

/** 导入包或类型语句 */
public class JCRequire extends JCTree  implements SourceFileSection
{
    /** 导入的类型表达式 */
    public JCFieldAccess typeTree;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitRequire(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateRequire(this, arg);
    }
}
