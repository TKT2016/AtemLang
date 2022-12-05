package atem.compiler.emits.pre.markers;

import atem.compiler.ast.*;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.ast.makes.MKNewClass;
import atem.compiler.emits.pre.ExpandContext;
import atem.compiler.lex.Token;
import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.DVarSymbol;
import atem.compiler.symbols.RClassSymbolManager;
import atem.compiler.symbols.VarSymbolKind;

public class DotMemberMarker {
    public JCExprStatement exprStatement;
    public JCIdent identExpr;
    ExpandContext context;
    JCFieldAccess source;
    Token posToken;

    public DotMemberMarker(JCFieldAccess source, ExpandContext context)
    {
        this.source =source;
        this.context = context;
        posToken =source.posToken;
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
        jcVariableDecl.expandKind= JCVariableDecl.ExpandKindEnum.DotMember;
        jcVariableDecl.expandInfo = source;
        left= new JCIdent();
        initTree(left);
        left.isExpandCreated = true;
        left.nameToken = Token.createNamed(TokenKind.IDENTIFIER, posToken.line,posToken.pos,posToken.endPos, context.createVarId()) ;
        DVarSymbol varSymbol1 = new DVarSymbol(
                left.nameToken.identName,
                VarSymbolKind.localVar,
                RClassSymbolManager.ObjectSymbol ,
                source.belongsInfo,
                false,-1
        );
        varSymbol1.isTempVar = true;
        left.symbol = varSymbol1;//   RClassSymbolManager.ObjectSymbol;
       // left.symbol =  RClassSymbolManager.ObjectSymbol;//  RClassSymbolManager.DotMeberClassSymbol;

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
