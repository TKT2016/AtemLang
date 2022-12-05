package atem.compiler.ast.makes;

import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCTree;
import atem.compiler.ast.TreeScanner;
import atem.compiler.ast.TreeTranslator;
import atem.compiler.symbols.BMethodSymbol;

/** new(...) 表达式 */
public class MKNewClass extends JCExpression
{
    /** 类型表达式 */
    public JCExpression clazzExpr;

    /** 构造函数实参列表 */
    public JCExpression[] args;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg){ v.visitNewClass(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateNewClass(this, arg);
    }

    /** 构造函数符号 */
    public BMethodSymbol constructorSymbol;


}

