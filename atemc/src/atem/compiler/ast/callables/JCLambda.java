package atem.compiler.ast.callables;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.ICallableAST;
import atem.compiler.lex.Token;
import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.*;
import atem.compiler.tools.IdGenerator;
import atem.compiler.utils.CompileError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JCLambda extends JCExpression implements ICallableAST
{
    public TokenKind spliteKind;
    public String methodName;

    public JCBlock body;

    public JCLambda(TokenKind spliteKind, JCTree[] items, Token posToken) {
        this.spliteKind =spliteKind;
        this.body = new JCBlock(posToken);
        this.body.statements = new ArrayList<>();
        for(var item :items)
        {
            if(item instanceof  JCStatement)
                this.body.statements.add((JCStatement)item);
            else if(item instanceof JCExpression)
            {
                JCExprStatement jcExprStatement = new JCExprStatement();
                jcExprStatement.expr = (JCExpression)item;
                jcExprStatement.posToken = jcExprStatement.expr.posToken;
                this.body.statements.add(jcExprStatement);
            }
            else
                throw new CompileError();
        }
        methodName = IdGenerator.getLambdaId();
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitLambda(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateLambda(this, arg);
    }

    public DMethodSymbol methodSymbol;

    public BMethodSymbol getMethodSymbol()
    {
        return methodSymbol;
    }

    private CallableCinfo callableCinfo = new CallableCinfo();

    public CallableCinfo getCallableCinfo()
    {
        return callableCinfo;
    }

    public SymbolScope getScope()
    {
        return belongsInfo.getScope();
    }

    public BTypeSymbol getRetType()
    {
        return methodSymbol.returnType;
    }
}
