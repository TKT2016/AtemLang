package atem.compiler.parse;

import atem.compiler.CompilerConsts;
import atem.compiler.emits.jasm.RTSigns;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.lex.Tokenizer;
import atem.compiler.lex.Token;
import atem.compiler.lex.TokenKind;
import atem.compiler.tools.ListUtil;
import atem.compiler.ast.*;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.JCMacroCall;
import atem.compiler.utils.CompileError;
import atem.compiler.utils.Debuger;
import atem.compiler.utils.SourceLog;
import atem.compiler.utils.msgresources.CompileMessagesUtil;

import java.util.ArrayList;
import static atem.compiler.lex.TokenKind.*;

/** 语法分析器 */
public class Parser {
   /* 词法分析器 */
    Tokenizer tokenizer;
    /* 当前词法标记 */
    Token token;
    /* 当前词法标记类型 */
    TokenKind kind;
    /* 错误提示 */
    SourceLog log;
    /* 语法树建造器 */
    TreeMaker maker;

    ProcParser procParser ;
    //ExprParser exprParser;

    public Parser( Tokenizer tokenizer, SourceLog log) {
        this.tokenizer = tokenizer;
        this.log = log;
        this.maker = new TreeMaker(log);
        procParser= new ProcParser(this);
      //  exprParser = new ExprParser(this);
        /* 读取第一个Token */
        nextToken();
    }

    void nextToken() {
        token = tokenizer.readToken();
        kind = token.kind;
    }

    /** 分析源文件语法树
     * CompilationUnit =  PackageDecl  {ImportDecl} {MethodDecl}
     */
    public JCFileTree parseCompilationUnit() {
        JCFileTree fileTree = new JCFileTree(log.sourceFile);
        fileTree.log = log;

        try {
            if (kind == TokenKind.PACKAGE) {
                JCPackage packageDecl = parsePackageDecl();
                fileTree.packageDecl = packageDecl;
            }
            /* 分析 import */
            fileTree.imports = new ArrayList<>();
            fileTree.requires = new ArrayList<>();
            while (true) {
                if(kind == TokenKind.IMPORT ) {
                    JCImport importTree = importDeclaration();
                    if (importTree != null && importTree.typeTree != null)
                        fileTree.imports.add(importTree);
                }
                else if(kind == REQUIRE ) {
                    var importTree = requireDeclaration();
                    if (importTree != null && importTree.typeTree != null)
                        fileTree.requires.add(importTree);
                }
                else
                    break;
            }

            /* 分析函数 */
            fileTree.functions = new ArrayList<>();
            fileTree.JCMacroDecls = new ArrayList<>();

            fileTree.clinitFunc = new JCFunction();
            fileTree.clinitFunc.posToken = Token.createNormal(NONE,0,0,0);
            fileTree.functions.add(fileTree.clinitFunc);
            fileTree.clinitFunc.isClinitFunc =true;
            var tempToken =Token.createNamed(IDENTIFIER,0,0,0, CompilerConsts.clinitMethodName);;
            fileTree.clinitFunc.nameToken =   tempToken;
            fileTree.clinitFunc.params = new ArrayList<>();
            fileTree.clinitFunc.body = new JCBlock(tempToken);
            fileTree.clinitFunc.body.posToken = Token.createNormal(NONE,0,0,0);
            fileTree.clinitFunc.body.statements = new  ArrayList<>();

            while (kind != TokenKind.EOF) {
                if(kind== FUNCTION)
                {
                    JCFunction def = methodDecl();
                    if (def != null) {
                        fileTree.functions.add(def);
                    }
                }
                else if(kind== MARCRO)
                {
                    JCMacroDecl def = procParser.parse();
                    if (def != null) {
                        fileTree.JCMacroDecls.add(def);
                    }
                }
               else
                {
                    JCStatement statement = parseStatement();
                    if(statement!=null) {
                        statement.isClinitStmt = true;
                        fileTree.clinitFunc.body.statements.add(statement);
                    }
                }
            }
        }
        catch (ParseEOFError ex){
        }
        //if(fileTree.packageDecl==null)
         //   log.error("缺少包定义");
        return fileTree;
    }

    /** 分析PACKAGE语句
     * JCPackage = PACKAGE Ident { "." Ident } ";"
     */
    JCPackage parsePackageDecl()
    {
        /* 记录开始词法标记 */
        Token posToken = token;
        /* 验证关键字PACKAGE */
        accept(PACKAGE);
        /* 分析包名 */
        JCExpression packageName = qualifiedName();
        /* 验证末尾是否是分号*/
        accept(TokenKind.SEMI);
        if(packageName==null)
        {
            log.error(posToken, CompileMessagesUtil.PackageMissingName,"");//   log.error(posToken,"package缺少名称");
            return null ;
        }
        else {
            /* 创建PACKAGE语法树实例 */
            JCPackage pd = maker.at(posToken).PackageDecl(packageName);
            return pd;
        }
    }

    ExprParser getExprParser ()
    {
        ExprParser exprParser = new ExprParser(this);
        return exprParser;
    }

    /* 分析限定名称表达式,比如 package1.pakcage2 */
    JCExpression qualifiedName( ) {
        /* 读取开头的标识符 */
        JCExpression temp =getExprParser (). parseIdent();
        /* 判断后面是否一直是点号 */
        while (kind == DOT) {
            nextToken();
            Token nameToken =parseNameToken();
            temp = maker.at(token).FieldAccess(temp, nameToken);
        }
        return temp;
    }

    /** 分析导入语句 import ...;
     * ImportDeclaration = IMPORT Ident { "." Ident } ";"
     * */
    JCImport importDeclaration() {
        /* 记录开始词法标记 */
        Token posToken = token;
        accept(IMPORT);
        JCFieldAccess pid = null;
        /* 分析类型名称 */
        JCExpression iexp = qualifiedName( );
        /* 这里限制只能导入外部包中类型,不是则报错 */
        if(iexp instanceof JCFieldAccess)
            pid = (JCFieldAccess)iexp;
        else
            error(posToken,CompileMessagesUtil.ImportIllegalType,"");//  error(posToken,"导入的不是正确的类型");
        /* /验证末尾是否是分号 */
        accept(TokenKind.SEMI);
        return maker.at(posToken).Import(pid);
    }

    JCRequire requireDeclaration() {
        /* 记录开始词法标记 */
        Token posToken = token;
        /* 验证关键字PACKAGE */
        accept(REQUIRE);
        JCFieldAccess pid = null;
        /* 分析类型名称 */
        JCExpression iexp = qualifiedName( );
        if(iexp instanceof JCFieldAccess)
            pid = (JCFieldAccess)iexp;
        else
            error(posToken,CompileMessagesUtil.ImportIllegalType,"");//error(posToken,"导入的不是正确的类型");
        /* /验证末尾是否是分号 */
        accept(TokenKind.SEMI);
        return maker.at(posToken).Require(pid);
    }

    /** 分析定义函数语法 */
    JCFunction methodDecl( ) {
        nextToken();
        /** 忽略多余的分号 ';' */
        while (token.kind == SEMI)
                nextToken();
        /* 记录开始token */
        Token posToken = token;
        /* 分析返回类型 */
        //JCExpression retTypeExpr = parseType();
        /* 分析函数名称 */
        Token nameToken =  parseNameToken();
        /* 参数列表 */
        ArrayList<JCVariableDecl> params = formalParameterList();
        /* 函数体 */
        JCBlock body = block();
        /* 生成定义函数语法树 */
        return maker.at(posToken).MethodDef( nameToken,  params, body);
    }

    /** 分析定义的参数列表 */
    ArrayList<JCVariableDecl> formalParameterList( ) {
        ArrayList<JCVariableDecl> params = new ArrayList<>();
        accept(LPAREN);
        if ( kind != RPAREN) {
            /* 1:分析第一个参数 */
            addParsedParameter(params);
            /*  2：如果当前是逗号,则跳过这个逗号,继续分析参数知道为否*/
            while ( kind == COMMA) {
                nextToken();
                addParsedParameter(params);
            }
        }
        accept(RPAREN);
        return params;
    }

    /** 分析函数声明的单个参数,并检查添加到列表  */
    private void addParsedParameter(ArrayList<JCVariableDecl> params)
    {
        JCVariableDecl param = formalParameter();
        /* 检查参数是否正确,正确的才添加 */
        if (param!=null &&  param.nameExpr != null)//  if (param!=null &&param.varType!=null &&  param.nameExpr != null)
            params.add(param);
    }

    /** 分析函数声明的单个参数
     *  FormalParameter = Type name
     */
    JCVariableDecl formalParameter() {
        Token posToken = token;
        /* 分析类型 */
       // JCExpression type = parseType();
       // if( type==null )
       // return null;
        /* 分析参数名称 */
        JCIdent name = getExprParser ().parseIdent();
        if( name==null )
            return null;
      //  return maker.at(posToken).VarDef(null, name, null,false);
        return maker.at(posToken).VarDef( name, null,false);
    }

    /** 分析语句 ,从简单到复杂排列*/
    protected JCStatement parseStatement()
    {
        switch (kind) {
            case WHILE:
                return parseWhile();
            case IF:
                return parseIf();
            case LBRACE:
                return block();//分析语句块
            //case INT:
            //case BOOLEAN:
            case IDENTIFIER:
            case VAR: // // JCVariableDecl variableDecl = variableDecl( );
                return expressionStatement();//分析其它语句
            case SEMI:
                nextToken(); //跳过无意义的分号
                return parseStatement();
            case ELSE:
                log.error( token,CompileMessagesUtil.ElseMissingIf,"" );//  log.error( token,"ELSE缺少IF" );
                nextToken();
                return parseStatement();
            case RBRACE:
                log.error( token,CompileMessagesUtil.RBRACEMissingLBRACE,"" );//   log.error( token,"右大括号没有匹配的左大括号" );
                nextToken();
                return parseStatement();
            case RETURN:
                return parseReturn();
            case BREAK:
            {
                var posToken = token;
                accept(BREAK);
                accept(SEMI);
                return maker.at(posToken).Break();
            }
          /*  case CONTINUE:
            {
                var posToken = token;
                accept(CONTINUE);
                accept(SEMI);
                return maker.at(posToken).Continue();
            }*/
            case EOF:
                return null;
            default:
                /* 错误处理 */
                log.error( token,CompileMessagesUtil.IllegalExpressionStatementElement,"" );//  log.error( token,"非法的表达式语句成分" );
                nextToken();
                return parseStatement();
        }
    }


    /**
     * 分析语句块
     * Block = "{" Statements "}"
     */
    protected JCBlock block( ) {
        /* 记录开始token */
        Token posToken = token;
        /* 验证左大括号*/
        accept(LBRACE);
        /* 用于保存分析出的语句 */
        ArrayList<JCStatement> stats= new ArrayList<>();
        while (true)
        {
            /*  当前符号是终止符'}',跳出循环 */
            if(kind== RBRACE) break;
            /* 文当前符号是件末尾,跳出循环 */
            if(kind== EOF) break;
            /* 分析语句,不为null保存 */
            JCStatement statement = parseStatement();
            if(statement!=null)
                stats.add(statement);
        }
        /* 验证右大括号*/
        accept(RBRACE);
        /* 生语句块语法树 */
        return maker.at(posToken).Block( stats);
    }

    /** 分析if语句
     * IF Expression Statement [ELSE Statement]
     * */
    JCIf parseIf()
    {
        /* 记录开始token */
        Token posToken = token;
        /* 验证关键字IF */
        accept(IF);
        /* 分析括号表达式 */
        JCExpression cond =  parens(false);
        /* 分析THEN部分 */
        JCStatement thenPart = parseStatement();
        /* 分析ELSE部分 */
        JCStatement elsePart = null;
        /* ELSE部分是可选的,所以要用IF判断一下 */
        if (kind == ELSE) {
            accept(ELSE);
            elsePart = parseStatement();
        }
        /* 生成IF语法树 */
        return maker.at(posToken).If(cond, thenPart, elsePart);
    }

    /** 分析while循环
     * WHILE ParExpression Statement
     * */
    JCWhile parseWhile()
    {
        /* 记录开始token */
        Token posToken = token;
        /* 验证关键字WHILE */
        accept(WHILE);
        /* 分析括号表达式 */
        JCExpression cond =  parens(false);
        /* 分析循环体 */
        JCStatement body = parseStatement();
        /* 生成WHILE语法树 */
        return maker.at(posToken).WhileLoop(cond, body);
    }

    /** 分析return语句
     * RETURN [Expression] ";"
     * */
    JCReturn parseReturn()
    {
        /* 记录开始token */
        Token posToken = token;
        /* 验证关键字 RETURN */
        accept(RETURN);
        /* RETURN 后的表达式是可选的 */
        JCExpression expr = null;
        /* 判断当前是否是分号,如果不是，读取表达式 */
        if(kind != SEMI)
            expr = parseExpression();
        /* 验证语句末尾分号 */
        accept(SEMI);
        /* 生成语法树实例 */
        return maker.at(posToken).Return(expr);
    }

    /** 分析表达式语句
     * JCExpressionStatement ->
     *  expression ';'
     *  expression = expression ';'
     *  expression NAME ';'
     * */
    JCStatement expressionStatement(  )
    {
        return expressionStatement(true);
    }

    JCStatement expressionStatement( boolean acceptSMIE )
    {
        Token posToken = token;
        /** 分析开头表达式 */
        JCExpression expression = parseExpression();

        if(kind == EQ)
        {
            /* 当前token是'='分析赋值表达式  */
            expression = assign(expression,posToken);
        }
        //else if (kind == VAR)
       // {
       //* 当前token是标识符分析变量声明表达式  */
         //   expression = variableDecl(expression,posToken);
     //   }
        else if (kind == IDENTIFIER)
        {
            //expression = variableDecl(expression,posToken);
            throw new CompileError();
        }

        if(acceptSMIE)
            accept(SEMI); //验证分号

        /* 检测表达式是否属于语句中正确类型 */
        if (expression instanceof JCAssign
                || expression instanceof JCMethodInvocation
               // || expression instanceof JCNewClass
                || expression instanceof JCVariableDecl
                || expression instanceof JCMacroCall
        )
        {
            JCExprStatement statement = maker.at(posToken).Exec(expression);
            return statement;
        }
        else {
            error(posToken, CompileMessagesUtil.IllegalExpressionStatementElement,"");  //error(posToken, "不是正确的表达式语句");
            return null;
        }
    }

    /**
     *  {@literal
     *  Expression = Expression1 [ExpressionRest]
     *  ExpressionRest = [AssignmentOperator Expression1]
     *  Type = Type1
     *  TypeNoParams = TypeNoParams1
     *  StatementExpression = Expression
     *  ConstantExpression = Expression
     *  }
     */
    protected JCExpression parseExpression() {
        return getExprParser ().parse();
    }

    /** 分析与或逻辑表达式 */
    JCExpression parseExprAndOr()
    {
        /* 记录位置Token */
        Token posToken =  token;
        /* 分析开头的表达式,按运算符优先级,调用分析比较表达式 */
        JCExpression temp = parseExprCompare();
        /* 循环当前符号是与或运算符时 */
        while(kind == TokenKind.AND || kind == TokenKind.OR)
        {
            /* 记录当前运算符并移到下一词法标记 */
            TokenKind op= kind;
            nextToken();
            /* 分析运算符右边的表达式 */
            JCExpression rightExpr = parseExprCompare();
            /* 用左右两个表达式和运算符生成表达式语法树,并赋值到temp */
            temp = maker.at(posToken).Binary(op, temp , rightExpr);
        }
        return temp;
    }

    /** 分析比较表达式 */
    JCExpression parseExprCompare()
    {
        Token posToken = token;
        JCExpression temp = parseExprAddSub();
        while(kind == EQEQ || kind == NOTEQ || kind == LT
                || kind == GT|| kind == LTEQ|| kind == GTEQ)
        {
            TokenKind op= kind;
            nextToken();
            JCExpression rightExpr = parseExprAddSub();
            temp = maker.at(posToken).Binary(op, temp , rightExpr);
        }
        return temp;
    }

    /** 分析加减表达式*/
    protected JCExpression parseExprAddSub()
    {
        Token posToken =  token;
        JCExpression temp = parseExprMulDiv();
        while(kind == TokenKind.ADD || kind == TokenKind.SUB)
        {
            TokenKind op= kind;
            nextToken();
            JCExpression rightExpr = parseExprMulDiv();
            temp = maker.at(posToken).Binary(op, temp , rightExpr);
        }
        return temp;
    }

    /** 分析乘除表达式*/
    JCExpression parseExprMulDiv()
    {
        Token posToken =  token;
        JCExpression expression = parseExprNot();
        while(kind == TokenKind.MUL || kind == TokenKind.DIV)
        {
            TokenKind op= kind;
            nextToken();
            JCExpression rightExpr = parseExprNot();
            expression = maker.at(posToken).Binary(op, expression , rightExpr);
        }
        return expression;
    }

    /** 分析否逻辑表达式*/
    JCExpression parseExprNot()
    {
        if(kind == TokenKind.NOT)
        {
            Token posToken =  token;
            TokenKind op = kind;
            nextToken();
            JCExpression rightExpr = parseComplex();
            return maker.at(posToken).Unary(op, rightExpr);
        }
        else
        {
            return parseComplex();
        }
    }

    /** 分析组合表达式,包括访问成员表达式,函数调用表达式,访问数组表达式 */
    JCExpression parseComplex()
    {
        Token posToken = token;
        JCExpression expression = factor();
        while (kind !=EOF)
        {
            switch (kind) {
                case DOT:
                    expression = fieldAccess(expression,posToken);
                    break;
                case LPAREN:
                    expression = methodInvocation(expression,posToken);
                    break;
              //  case LBRACKET:
                //    expression = arrayAccess(expression,posToken);
                default:
                    return expression;
            }
        }
        return expression;
    }

    JCExpression factor() {
        switch (kind) {
            case INTLITERAL:case FLOATLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
                return literal();
            case LPAREN:
                return parens(true);
            default:
                return null;
        }
    }

    /** 分析括号表达式
     * ParExpression = "(" Expression ")"
     */
    JCParens parens(boolean allowEmpty) {
        Token posToken = token;
        /* 验证左括号 */
        accept(LPAREN);
        /* 分析括号中的表达式 */
        JCExpression t = parseExpression();
        /* 验证右括号 */
        accept(RPAREN);
        /* 生成语法树并返回 */
        return maker.at(posToken).Parens(t,allowEmpty);
    }

    JCExpression parseTypeNotArray()
    {
        Token posToken = token;
        JCExpression expr;
        switch (kind) {
            //case VOID:
             //   return primitiveType();
            //case INT:
          //  case BOOLEAN:
            //    expr = primitiveType();
           //     break;
            case IDENTIFIER:
                expr = getExprParser ().parseIdent();
                break;
            case COMMA:
                error(token, CompileMessagesUtil.RedundantCommaSymbols,"");// error(posToken,"多余的逗号");
                return null;
            case SEMI:
                error(token, CompileMessagesUtil.RedundantSemiSymbols,"");// error(posToken,"多余的分号");
                return null;
            default:
                error(posToken,CompileMessagesUtil.IllegalType,"");//    error(posToken,"非法的类型");
                nextToken();
                return null;
        }
        while (kind ==DOT)
        {
            expr = fieldAccess(expr,posToken);
        }
        return expr;
    }

    /** 分析赋值表达式 */
    JCAssign assign(JCExpression leftExpr,Token startToken)
    {
        nextToken();
        JCExpression right = parseExpression() ;
        return maker.at(startToken).Assign(leftExpr, right);
    }

    Token parseNameToken() {
        if (kind == IDENTIFIER) {
            Token tmpToken = token;
            nextToken();
            return tmpToken;
        }
        else {
            error(token, CompileMessagesUtil.ExpectIdent,"");//error(token,"不是标识符" );
            return null;
        }
    }

    JCExpression fieldAccess(JCExpression expr,Token posToken) {
        if(kind ==DOT)
        {
            nextToken();
            Token nameToken =parseNameToken();// String name = parseName();
            expr = maker.at(posToken).FieldAccess(expr,nameToken);
        }
        return expr;
    }

    /** 分析常量表达式
     * Literal =
     *     INTLITERAL
     *   | STRINGLITERAL
     *   | TRUE
     *   | FALSE
     */
    JCExpression literal( ) {
        Object value = ParseUtil. parseTokenKindValue(this);
        JCLiteral literal = maker.at(token).Literal(value);
        nextToken();
        return literal;
    }

    /** 分析函数调用表达式 */
    JCMethodInvocation methodInvocation(JCExpression t, Token posToken) {
        ArrayList<JCExpression> args = arguments();
        JCMethodInvocation methodInvocation = maker.at(posToken).Apply(t,  ListUtil.toExprArray(args) );
        return methodInvocation;
    }

    /** 分析函数参数
     *  Arguments = "(" [Expression { COMMA Expression }] ")"
     */
    ArrayList<JCExpression> arguments() {
        ArrayList<JCExpression> args = new ArrayList<>();
        accept(LPAREN);
        if (kind != RPAREN) {
            JCExpression argExpr = parseExpression();
            if (argExpr != null)
                args.add(argExpr);
            while (kind == COMMA) {
                Token commaToken = token;
                nextToken();
                argExpr = parseExpression();
                if (argExpr != null)
                    args.add(argExpr);
                else
                    error(commaToken, CompileMessagesUtil.RedundantCommaSymbols,"");//error(commaToken,"多余的逗号");
            }
        }
        accept(RPAREN);
        return args;
    }

    void accept(TokenKind tk) {
        if(token.kind == tk)
            nextToken();
        else
        {
            //String msg;
            if (tk.name != null)
            {
                //msg = "期望是 '" + tk.name + "' ";
                log.error(token,CompileMessagesUtil.ExpectFor, tk.name);
            }
            else if (tk == TokenKind.IDENTIFIER)
            {
               // msg ="期望是<标识符>";
                log.error(token,CompileMessagesUtil.ExpectIdent,"");
            }
            else
            {
                log.error(token,CompileMessagesUtil.ExpectFor, "<" + tk + ">");
            }
                //msg ="期望是<" + tk + ">";
           // log.error(token,msg);
        }
    }

    void error(Token posToken,String key, String msg)
    {
        this.log.error(posToken,key,msg);
    }
}
