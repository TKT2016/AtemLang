package atem.compiler.parse;

import atem.compiler.ast.callables.JCLambda;
import atem.compiler.lex.Token;
import atem.compiler.lex.TokenKind;
import atem.compiler.ast.*;
import atem.compiler.tools.ListUtil;
import atem.compiler.utils.msgresources.CompileMessagesUtil;

import java.util.ArrayList;
import java.util.Stack;

import static atem.compiler.lex.TokenKind.*;
import static atem.compiler.lex.TokenKind.RBRACKET;

public class ExprParser {
    Parser parser;
    public ExprParser(Parser parser)
    {
        this.parser = parser;
    }

    Stack<JCExpression> stack = new Stack<>();

    JCExpression parse()
    {
        return parseVar();
    }

    JCExpression parseVar()
    {
        if(parser.kind==VAR)
            return variableDecl();
        else
            return parse2();
    }

    JCExpression parse2()
    {
        stack.clear();
        Token posToken= parser.token;

        while (true)
        {
            TokenKind kind= parser.kind;
            if(kind==EOF
                    || kind == RBRACKET
                    || kind == RBRACE
                    || kind == RPAREN
                    || kind == SEMI
                    || kind == COMMA
                //  || kind == COLON
            )
                break;
            JCExpression expression = parseAssign();
            if(expression==null)
                break;
            stack.push(expression);
        }

        if(stack.size()==1)
            return stack.pop();
        else   if(stack.size()==0)
            return null;
        else
            return parser.maker.at(posToken).ProcCallExpr(stack);
    }

    JCExpression parseAssign() {
        Token posToken = parser.token;
        var left = parseColon();
        if(parser.kind== TokenKind.EQ)
        {
            parser.nextToken();
            JCExpression right = parseColon() ;
            return parser.maker.at(posToken).Assign(left, right);
        }
        else
        {
            return left;
        }
    }

    JCExpression parseColon()
    {
        Token posToken = parser.token;
        var left = parseBinary();
        if(parser.kind== COLON)
        {
            parser.nextToken();
            JCExpression right = parseBinary() ;
            return parser.maker.at(posToken).Colon(left, right);
        }
        else
        {
            return left;
        }
    }

    JCExpression parseBinary()
    {
        return parseExprAndOr();
    }

    /** 分析与或逻辑表达式 */
    JCExpression parseExprAndOr()
    {
        /* 记录位置Token */
        Token posToken =  parser. token;
        /* 分析开头的表达式,按运算符优先级,调用分析比较表达式 */
        JCExpression temp = parseExprCompare();
        /* 循环当前符号是与或运算符时 */
        while(parser.kind == TokenKind.AND ||parser. kind == TokenKind.OR)
        {
            /* 记录当前运算符并移到下一词法标记 */
            TokenKind op= parser.kind;
            parser.nextToken();
            /* 分析运算符右边的表达式 */
            JCExpression rightExpr = parseExprCompare();
            /* 用左右两个表达式和运算符生成表达式语法树,并赋值到temp */
            temp = parser.maker.at(posToken).Binary(op, temp , rightExpr);
        }
        return temp;
    }

    /** 分析比较表达式 */
    JCExpression parseExprCompare()
    {
        Token posToken = parser.token;
        JCExpression temp = parseExprAddSub();

        while (true)
        {
            var kind =parser.kind;
            if(kind == EQEQ || kind == NOTEQ || kind == LT
                    || kind == GT|| kind == LTEQ|| kind == GTEQ)
            {
                TokenKind op= kind;
                parser.nextToken();
                JCExpression rightExpr = parseExprAddSub();
                temp =parser. maker.at(posToken).Binary(op, temp , rightExpr);
            }
            else
                break;
        }
        return temp;
    }

    /** 分析加减表达式*/
    protected JCExpression parseExprAddSub()
    {
        Token posToken =  parser.token;
        JCExpression temp = parseExprMulDiv();
        while(parser.kind == TokenKind.ADD || parser.kind == TokenKind.SUB)
        {
            TokenKind op=parser. kind;
            parser.nextToken();
            JCExpression rightExpr = parseExprMulDiv();
            temp = parser.maker.at(posToken).Binary(op, temp , rightExpr);
        }
        return temp;
    }

    /** 分析乘除表达式*/
    JCExpression parseExprMulDiv()
    {
        Token posToken =  parser.token;
        JCExpression expression = parseExprNot();
        while(parser.kind == TokenKind.MUL ||parser. kind == TokenKind.DIV)
        {
            TokenKind op=parser. kind;
            parser.nextToken();
            JCExpression rightExpr = parseExprNot();
            expression = parser.maker.at(posToken).Binary(op, expression , rightExpr);
        }
        return expression;
    }

    /** 分析否逻辑表达式*/
    JCExpression parseExprNot()
    {
        if(parser.kind == TokenKind.NOT)
        {
            Token posToken =  parser.token;
            TokenKind op = parser.kind;
            parser.nextToken();
            JCExpression rightExpr = primaryExpr();
            return parser.maker.at(posToken).Unary(op, rightExpr);
        }
        else
        {
            return primaryExpr();
        }
    }

    /** 分析组合表达式,包括访问成员表达式,函数调用表达式 */
    JCExpression primaryExpr()
    {
        //Token posToken = parser.token;
        JCExpression expression = parseHead();
        while (parser. kind !=EOF)
        {
            switch (parser.kind) {
                case DOT:
                    expression = fieldAccess(expression);
                    break;
                //case LPAREN:
              //      expression = methodInvocation(expression,posToken);
              //      break;
                default:
                    return expression;
            }
        }
        return expression;
    }

    JCExpression parseHead()
    {
        switch (parser.kind)
        {
           // case VAR:
           //     return variableDecl();
            case LBRACKET:
                return arrayLiteral();
            case LPAREN:
                return parenExpr();
            case LBRACE:
                return braceExpr();
            //case THIS:
            case IDENTIFIER:
                return parseIdent();
            case INTLITERAL: case FLOATLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
            case NULL:
                return literal();
            case ADD:case SUB: case NOT:
                return unary();
            case DOT:
            {
                parser.error(parser.token,CompileMessagesUtil.DotExperssionMissingLeft,"");// parser.error(parser.token,"'.'运算缺少左表达式");
                parser.nextToken();
                return parseHead();
            }
            case GT:case GTEQ:case LT:case LTEQ:case EQEQ:case NOTEQ:
            case MUL:case DIV:
            {
                parser.error(parser.token,CompileMessagesUtil.BinaryLeftMissingExperssion,"");//  parser.error(parser.token,"运算缺少左表达式");
                parser.nextToken();
                return parseHead();
            }
            case COLON:
            {
                parser.error(parser.token,CompileMessagesUtil.PairMissingKey,"");//  parser.error(parser.token,"pair 缺少 key");
                parser.nextToken();
                return parseHead();
            }
            case EQ:
            {
                parser.error(parser.token,CompileMessagesUtil.AssignRightMissingExperssion,"");//   parser.error(parser.token,"赋值缺少被赋值表达式");
                parser.nextToken();
                return parseHead();
            }
            default:
                return null;
        }
    }

    JCExpression unary()
    {
        Token postok = parser.token;
        TokenKind op = parser.kind;
        parser.nextToken();
        ExprParser exprParser = new ExprParser(this.parser);
        JCExpression expression = exprParser.parse();
        JCUnary unary = parser.maker.at(postok).Unary(op,expression);
        return unary;
    }

    /** 分析常量表达式
     * Literal =
     *     INTLITERAL
     *   | STRINGLITERAL
     *   | TRUE
     *   | FALSE
     */
    JCExpression literal( ) {
        if(parser.kind== NULL)
        {
            JCLiteral literal = parser.maker.at(parser.token).Literal(null);
            literal.isNullLiteral =true;
            parser.nextToken();
            return literal;
        }
        else
        {
            Object value = ParseUtil. parseTokenKindValue(parser);
            JCLiteral literal = parser.maker.at(parser.token).Literal(value);
            parser.nextToken();
            return literal;
        }
    }

    /* 分析标识符 */
    JCIdent parseIdent()
    {
        /* 记录开始位置词法标记 */
        Token posToken = parser. token;
        if ( parser. kind == IDENTIFIER) {
            parser. nextToken();
            /* 用posToken创建标识符表达式 */
            JCIdent jcIdent = parser. maker.at(posToken).Ident(posToken);
            return jcIdent;
        }
        else
        {
            /* 错误处理 */
            parser.  log.error(posToken, CompileMessagesUtil.ExpectIdent,"");//  parser.  log.error(posToken,"期望标识符");
            return null;
        }
    }

    JCExpression fieldAccess(JCExpression expr ) {
        Token posToken = parser.token;
       // JCExpression expr = pop();
        if(parser.kind ==DOT) {
            parser.nextToken();
            if (parser.kind == LPAREN) {
                ArrayList<JCExpression> items = parenItems();
                    expr = parser.maker.at(expr.posToken).Apply(expr, ListUtil.toExprArray(items) );
            } else {
                Token nameToken = parser.parseNameToken();// String name = parseName();
                if (expr == null)
                    expr = parser.maker.at(posToken).FieldAccess(expr, nameToken);
                else
                    expr = parser.maker.at(expr.posToken).FieldAccess(expr, nameToken);
            }
        }
        return expr;
    }

    ArrayList<JCExpression> parenItems() {
        //Token posToken = parser.token;
        parser.accept(LPAREN);
        ArrayList<JCExpression> args = parseExprs(RPAREN);
        parser.accept(RPAREN);
        return args;
    }

    /** 分析变量声明语句 */
    JCVariableDecl variableDecl() {
        Token posToken = parser.token;
        parser.nextToken();
        JCIdent name = parseIdent();
        JCVariableDecl statement = variableDecl(name,posToken);
        return statement;
    }

    JCVariableDecl variableDecl(  JCIdent name,Token posToken)
    {
        JCExpression init = null;
        if (parser.kind == TokenKind.EQ) //如果是'=',变量有初始值
        {
            parser. nextToken();
            init = parseSubExpression();
        }
        JCVariableDecl result = parser.maker.at(posToken).VarDef(name, init,true);
        return result;
    }

    JCExpression parseSubExpression()
    {
        ExprParser parser1 = new ExprParser(this.parser);
        return parser1.parse();
    }

    JCArrayLiteral arrayLiteral()
    {
        Token posToken =  parser.token;
        parser.accept(LBRACKET);
        ArrayList<JCExpression> args =  parseExprs(RBRACKET);
        parser.accept(RBRACKET);
        JCArrayLiteral JCArrayLiteral =  parser.maker.at(posToken).arrayLiteral(   args);
        return JCArrayLiteral;
    }

    JCParens parenExpr() {
         return parser.parens(true);
    }

    JCExpression braceExpr() {
        Token posToken = parser.token;
        parser.accept(LBRACE);
        ArrayList<JCTree> args =new ArrayList<>();
        TokenKind kind =  parseBraceExprs(args);
        parser.accept(RBRACE);
        if(args.size()>0 && args.get(0) instanceof JCPair)
        {
            var dynamicLiteral = parser.maker.at(posToken).dynamicLiteral(kind, args);
            return dynamicLiteral;
        }
        else {
            JCLambda JCLambda = parser.maker.at(posToken).lambda(kind, args);
            return JCLambda;
        }
    }

    ArrayList<JCExpression> parseExprs(TokenKind endKind)
    {
        ArrayList<JCExpression> args = new ArrayList<>();

        if ( parser.kind != endKind) {
            JCExpression argExpr =   parseSubExpression();
            if (argExpr != null)
                args.add(argExpr);
            while ( parser.kind == COMMA) {
                Token commaToken =  parser.token;
                parser.nextToken();
                argExpr =  parseSubExpression();
                if (argExpr != null)
                    args.add(argExpr);
                else
                    parser.error(parser.token, CompileMessagesUtil.RedundantCommaSymbols,"");// parser.error(commaToken,"多余的逗号");
            }
        }
        return args;
    }

    TokenKind parseBraceExprs( ArrayList<JCTree> args)
    {
        TokenKind kind = NONE;
        boolean first =true;
        while (true)
        {
            BraceItem braceItem = parseBraceExprItem();
            if(braceItem.argExpr!=null)
                args.add(braceItem.argExpr);
            else {
                TokenKind curkind = parser.kind;
                if(curkind==RBRACE
                        || curkind==EOF
                )
                    break;
                parser.error(parser.token, CompileMessagesUtil.RedundantDivisionSymbols,"");//  parser.error(parser.token, "多余的分割符号");
                parser.nextToken();
            }
            if(first)
            {
                if(braceItem.argExpr instanceof JCStatement)
                {
                    kind = SEMI;
                    first =false;
                }
                else {
                    kind = braceItem.endKind;
                    first = false;
                }
            }
            TokenKind curkind = parser.kind;
            if(curkind==RBRACE
                    || curkind==EOF
            )
                break;
        }

        return kind;
    }

    BraceItem parseBraceExprItem()
    {
        Token posToken = parser.token;
        BraceItem braceItem = new BraceItem();
        JCExpression expression = parseSubExpression();
        if(expression!=null) {
            if (parser.kind == COMMA) {
                braceItem.argExpr = expression;
                braceItem.endKind = parser.kind;
                parser.nextToken();
            } else if (parser.kind == SEMI) {
                braceItem.argExpr = parser.maker.at(posToken).Exec(expression);
                braceItem.endKind = parser.kind;
                parser.nextToken();
            } else {
                braceItem.argExpr = expression;
            }
        }
        else
        {
            if(parser.kind==RBRACE)
            {
                return braceItem;
            }
            else
            {
                JCStatement statement = parser.parseStatement();
                braceItem.argExpr = statement;
            }
        }
        return braceItem;
    }

    class BraceItem
    {
        JCTree argExpr;
        TokenKind endKind = NONE;
    }
}
