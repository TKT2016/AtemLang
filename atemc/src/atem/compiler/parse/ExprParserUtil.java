package atem.compiler.parse;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.utils.CompileError;

public class ExprParserUtil {
/*
    JCExpression setRight(JCExpression expression,JCExpression right)
    {
        if(expression==null)
            return right;
        if(expression instanceof JCAssign)
        {
            JCAssign assign = (JCAssign) expression;
            return getRight(assign.right);
        }
        if(expression instanceof JCIdent)
        {
            return expression;
        }
        if(expression instanceof JCVariableDecl)
        {
            JCVariableDecl variableDecl = (JCVariableDecl) expression;
            if(variableDecl.init!=null)
                return getRight(variableDecl.init);
            else
                throw new CompileError();
        }
        if(expression instanceof JCLambda)
        {
            return expression;
        }
        if(expression instanceof JCBinary)
        {
            JCBinary jcBinary = (JCBinary) expression;
            return getRight( jcBinary.right);
        }
        if(expression instanceof JCParens)
        {
            return expression;
        }
        if(expression instanceof JCLiteral)
        {
            return expression;
        }
        if(expression instanceof JCPair)
        {
            JCPair jcPair = (JCPair) expression;
            return getRight(jcPair.right);
        }
        if(expression instanceof JCMacroCall)
        {
            JCMacroCall macroCall = (JCMacroCall) expression;
            int count = macroCall.getItems().length;
            if(macroCall.getItems().length>0)
                return macroCall.getItems()[count-1];
            else
                throw new CompileError();
        }
        if(expression instanceof JCUnary)
        {
            JCUnary exp = (JCUnary) expression;
            return getRight(exp.expr);
        }
        if(expression instanceof JCFieldAccess)
        {
            return expression;
        }
        throw new CompileError();
    }
*/
    JCExpression getRight(JCExpression expression)
    {
        if(expression instanceof JCAssign)
        {
            JCAssign assign = (JCAssign) expression;
            return getRight(assign.right);
        }
        if(expression instanceof JCIdent)
        {
            return expression;
        }
        if(expression instanceof JCVariableDecl)
        {
            JCVariableDecl variableDecl = (JCVariableDecl) expression;
            if(variableDecl.init!=null)
              return getRight(variableDecl.init);
            else
                throw new CompileError();
        }
        if(expression instanceof JCLambda)
        {
            return expression;
        }
        if(expression instanceof JCBinary)
        {
            JCBinary jcBinary = (JCBinary) expression;
            return getRight( jcBinary.right);
        }
        if(expression instanceof JCParens)
        {
            return expression;
        }
        if(expression instanceof JCLiteral)
        {
            return expression;
        }
        if(expression instanceof JCPair)
        {
            JCPair jcPair = (JCPair) expression;
            return getRight(jcPair.right);
        }
        if(expression instanceof JCMacroCall)
        {
            JCMacroCall macroCall = (JCMacroCall) expression;
            int count = macroCall.getItems().length;
            if(macroCall.getItems().length>0)
                return macroCall.getItems()[count-1];
            else
                throw new CompileError();
        }
        if(expression instanceof JCUnary)
        {
            JCUnary exp = (JCUnary) expression;
            return getRight(exp.expr);
        }
        if(expression instanceof JCFieldAccess)
        {
            return expression;
        }
        throw new CompileError();
    }
}
