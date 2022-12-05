package atem.compiler.ast.makes;

import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCTree;
import atem.compiler.ast.TreeScanner;
import atem.compiler.ast.TreeTranslator;
import atem.compiler.lex.Token;

/** 访问字段表达式 */
public class JCFieldAccess extends JCExpression //implements IValueSetGet
{
    /** 点运算被限定的部分 */
    public JCExpression selected;
    /** 点运算名称 */
    public Token nameToken;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg){ v.visitFieldAccess(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateFieldAccess(this, arg);
    }
/*
    public Object getValue(TreeInterpreter treeInterpreter, TreeInterpreteContext context)
    {
        return  treeInterpreter.evalTree(this,context);
    }

    public void setValue(TreeInterpreter treeInterpreter, TreeInterpreteContext context, Object value)
    {
        if(selected.symbol instanceof RClassSymbol)
        {
            RClassSymbol classSymbol =(RClassSymbol) selected.symbol;
            TypeLiteral staticType = TypeLiteral.get(classSymbol.clazz);
        }
    }*/

    public boolean isAssignLeft;

    //public boolean isInstanceof;

/*
    @Override
    public void  isAnalyzered(boolean v)
    {
        propertys._isAnalyzered =v;
    }
    */
    /*
    @Override
    public boolean isAnalyzered( )
    {
        return  _isAnalyzered;
    }*/
}
