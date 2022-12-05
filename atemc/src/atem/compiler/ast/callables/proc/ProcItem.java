package atem.compiler.ast.callables.proc;

import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCTree;
import atem.compiler.ast.TreeScanner;
import atem.compiler.ast.TreeTranslator;

public abstract class ProcItem extends JCExpression implements ProcTree {
    public int index;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) {   }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return this;
    }

}
