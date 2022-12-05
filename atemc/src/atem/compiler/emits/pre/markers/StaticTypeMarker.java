package atem.compiler.emits.pre.markers;

import atem.compiler.ast.*;
import atem.compiler.ast.makes.MKNewClass;
import atem.compiler.emits.pre.ExpandContext;
import atem.compiler.lex.Token;
import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.*;

public class StaticTypeMarker {
    public JCExprStatement exprStatement;
    public JCIdent identExpr;
    ExpandContext context;
    JCIdent source;
    Token posToken;

    BTypeSymbol typeSymbol;
    public StaticTypeMarker(JCIdent source, ExpandContext context)
    {
        this.source =source;
        this.context = context;
        posToken =source.posToken;
        typeSymbol = (BTypeSymbol)source.symbol;
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
        jcVariableDecl.expandKind= JCVariableDecl.ExpandKindEnum.StaticType;
        jcVariableDecl.expandInfo = typeSymbol;

        left= new JCIdent();
        initTree(left);
        left.isExpandCreated = true;
        left.nameToken = Token.createNamed(TokenKind.IDENTIFIER, posToken.line,posToken.pos,posToken.endPos, context.createVarId()) ;
        DVarSymbol varSymbol1 = new DVarSymbol(
                left.nameToken.identName,
                VarSymbolKind.localVar,
                RClassSymbolManager.TypeLiteralClassSymbol ,
                source.belongsInfo,
                false,-1
        );
        varSymbol1.isTempVar = true;
        left.symbol = varSymbol1;//   RClassSymbolManager.ObjectSymbol;
        //left.symbol =RClassSymbolManager.ObjectSymbol;//    RClassSymbolManager.TypeLiteralClassSymbol;

        MKNewClass right = new MKNewClass();
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
