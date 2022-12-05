package atem.compiler.ast;

import atem.compiler.symbols.Symbol;

/** 表达式抽象语法树父类 */
public abstract class JCExpression extends JCTree
{
    public Symbol symbol;

    public boolean isInvocationMethod;
}
