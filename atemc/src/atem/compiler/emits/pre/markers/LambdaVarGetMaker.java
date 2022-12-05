package atem.compiler.emits.pre.markers;

import atem.compiler.ast.*;
import atem.compiler.ast.makes.MKArrayAccess;
import atem.compiler.emits.pre.ExpandContext;
import atem.compiler.lex.Token;
import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.DVarSymbol;
import atem.compiler.symbols.VarSymbolKind;

public class LambdaVarGetMaker {
    DVarSymbol varSymbol;
    public JCExprStatement exprStatement;
    public JCIdent identExpr;
    ExpandContext context;
    JCIdent source;
    Token posToken;

    public LambdaVarGetMaker(JCIdent source,ExpandContext context)
    {
        this.source =source;
        this.context = context;
        posToken =source.posToken;
        varSymbol =(DVarSymbol) source.symbol;
    }

    public void make( )
    {
        exprStatement = makeStmt();
        identExpr =makeIdent();
    }

    JCExprStatement makeStmt()
    {
        JCVariableDecl variableDecl = makeVarDecl();
        JCExprStatement jcExprStatement = new JCExprStatement();
        initTree(jcExprStatement);
        jcExprStatement.expr = variableDecl;
        return jcExprStatement;
    }

    JCIdent left;
    JCVariableDecl makeVarDecl()
    {
        JCVariableDecl jcVariableDecl = new JCVariableDecl();
        initTree(jcVariableDecl);
        jcVariableDecl.expandKind= JCVariableDecl.ExpandKindEnum.LambdaVarGet;
        jcVariableDecl.expandInfo =varSymbol;
        left= new JCIdent();
        initTree(left);
        left.isExpandCreated = true;
        left.nameToken = Token.createNamed(TokenKind.IDENTIFIER, posToken.line,posToken.pos,posToken.endPos, context.createVarId()) ;

        DVarSymbol varSymbol1 = new DVarSymbol(left.nameToken.identName, VarSymbolKind.localVar, varSymbol.getTypeSymbol(),
                varSymbol.dimTree, false,-1);
        varSymbol1.isTempVar = true;
        left.symbol = varSymbol1;//   RClassSymbolManager.ObjectSymbol;
        MKArrayAccess right = new MKArrayAccess();
        initTree(right);
        jcVariableDecl.nameExpr = left;
        jcVariableDecl.init =right;
        return jcVariableDecl;
    }

    JCIdent makeIdent()
    {
        return  left;
    }

    void initTree(JCTree tree)
    {
        tree.posToken = source.posToken;
        tree.belongsInfo = source.belongsInfo;
    }
}
