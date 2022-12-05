package atem.compiler.ast.callables;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.proc.ProcItemParameter;
import atem.compiler.ast.callables.proc.ProcItemText;
import atem.compiler.symbols.*;
import atem.compiler.lex.Token;

import java.util.ArrayList;

/** 定义方法树 */
public class JCFunction extends JCTree implements SourceFileSection, ICallableAST
{
    /** 函数名称 */
    public Token nameToken;

    public String name()
    {
        return nameToken.identName;
    }

    /** 函数返回值表达式 */
    //public JCExpression retTypeExpr;

    /** 函数参数列表 */
    public ArrayList<JCVariableDecl> params;

    /** 函数体语句块 */
    public JCBlock body;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitMethodDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateMethod(this, arg);
    }

    /** 方法符号*/
    public DMethodSymbol methodSymbol;

    public BMethodSymbol getMethodSymbol()
    {
        return methodSymbol;
    }

    public BTypeSymbol getRetType()
    {
        return methodSymbol.returnType;
    }

    /** 第一个参数的名称是否是self */
    public boolean isSelfFirst;

    public boolean isClinitFunc;

    public CallableCinfo callableCinfo = new CallableCinfo();

    public CallableCinfo getCallableCinfo()
    {
        return callableCinfo;
    }

    public SymbolScope getScope()
    {
        return this.belongsInfo.getScope();
    }

    public String createMethodDefineValue()
    {
        var sb = new StringBuilder();
        sb.append(this.name());
        //sb.append("(");
        int size = params.size();
        if(size>0)
        {
            sb.append(" ");
            sb.append("$");
            for(int i=1;i<size;i++)
            {
                sb.append(" ");
                sb.append("$");
            }
        }
       // sb.append(")");
        return sb.toString().trim();
    }

}
