package atem.compiler.parse;

import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.tools.ListUtil;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.JCMacroCall;
import atem.compiler.ast.callables.proc.ProcItem;
import atem.compiler.utils.SourceLog;
import atem.compiler.lex.Token;
import atem.compiler.ast.*;
import atem.compiler.lex.TokenKind;

import java.util.ArrayList;
import java.util.Stack;

import atem.compiler.ast.JCTree;
import atem.compiler.tools.StringHelper;
import atem.compiler.utils.msgresources.CompileMessagesUtil;

/** 抽象语法树工厂(包含检查树的完整性) */
public class TreeMaker
{
    public SourceLog log;

    /** 当前位置 */
    public int pos= -1;//SourceLog.NOPOS;

    /* 当前行号*/
    public int line = 1 ;

    Token posToken;

    public TreeMaker(SourceLog log ) {
        this.log = log;
    }

    /** 给pos和line赋值并返回自己以便连续调用 */
   /* public TreeMaker at(int pos,int line) {
        this.pos = pos;
        this.line = line;
        return this;
    }*/

    public TreeMaker at(Token token) {
        this.posToken = token;
        this.pos = token.pos;
        this.line = token.line;
        return this;
        //return at(token.pos,token.line);
    }

    void initTree(JCTree tree)
    {
        tree.posToken = posToken;
        tree.log = log;
        //tree.pos = pos;
        tree.line = line;
    }
/*
    void error(String key, String msg)
    {
       // this.log.error(pos,line,msg,1);
        this.log.error(posToken,key,msg);
    }*/

    void error(String key )
    {
        // this.log.error(pos,line,msg,1);
        this.log.error(posToken,key,"");
    }

    /** 创建JCPackage */
    public JCPackage PackageDecl(JCExpression pid) {
        JCPackage tree = new JCPackage(  );
        tree.packageName = pid;
        initTree(tree);
        check(tree);
        return tree;
    }

    boolean check( JCPackage tree)
    {
        boolean right= true;
        if(tree.packageName==null)
        {
            error(CompileMessagesUtil.PackageMissingName);// error("定义 package缺少名称");
            right = false;
        }
        return right;
    }

    /** 创建JCImport,需要判断是否有'*' */
  /*  public JCImport Import(JCExpression qualid)
    {
        boolean isPackageStar =false;
        JCExpression expression2 = qualid;

        if(qualid instanceof JCFieldAccess)
        {
            JCFieldAccess jcFieldAccess=(JCFieldAccess)qualid;
            if(jcFieldAccess.name=="*")
            {
                isPackageStar = true;
                expression2 = jcFieldAccess.selected;
            }
            else
            {
                error("导入package必须以'.*'结尾");
            }
        }
        return Import(expression2,isPackageStar);
    }*/

    /** 创建JCImport */
   /* public JCImport Import(JCTree qualid,boolean isPackageStar) {
        JCImport tree = new JCImport(qualid,isPackageStar);
        initTree(tree);
        check(tree);
        return tree;
    }*/

    /** 创建JCImport */
    public JCImport Import(JCFieldAccess typeTree) {
        JCImport tree = new JCImport();
        tree.typeTree = typeTree;
        initTree(tree);
        check(tree);
        return tree;
    }

    public JCRequire Require(JCFieldAccess typeTree) {
        JCRequire tree = new JCRequire();
        tree.typeTree = typeTree;
        initTree(tree);
        check(tree);
        return tree;
    }

    boolean check( JCRequire tree)
    {
        boolean right= true;
        if(tree.typeTree ==null)
        {
            error(CompileMessagesUtil.RequireMissingName);//error("require缺少类名称或包名称");
            right = false;
        }
        return right;
    }

    boolean check( JCImport tree)
    {
        boolean right= true;
        if(tree.typeTree ==null)
        {
            error(CompileMessagesUtil.ImportMissingName);//   error("import缺少类名称或包名称");
            right = false;
        }
        return right;
    }

    /* 创建JCClassDecl */
    /*public JCClassDecl ClassDef(String name, ArrayList<JCTree> defs)
    {
        JCClassDecl tree = new JCClassDecl(name, defs);
        initTree(tree);
        check(tree);
        return tree;
    }*/

   /* boolean check( JCClassDecl tree)
    {
        boolean error=false;
        if(StringHelper.isNullOrEmpty(tree.name))
        {
            error("import缺少类名称或包名称");
            error = true;
        }
        return !error;
    }*/

    /* 创建JCMethodDecl */
    public JCFunction MethodDef(Token nameToken, ArrayList<JCVariableDecl> params, JCBlock body)
    {
      /*  if(retTypeExpr== null ) {
            error("函数缺少返回值");
            return null;
        }
        if(nameToken== null ||  StringHelper.isNullOrEmpty(nameToken.identName)) {
            error("函数名称不能为空");
            return null;
        }
        if(params== null ) {
            error("函数缺少形参");
            return null;
        }
        if(body== null ) {
            error("缺少函数体");
            return null;
        }*/

        JCFunction tree = new JCFunction( );
        tree.nameToken = nameToken;
        //tree.retTypeExpr = retTypeExpr;
        tree.params = params;
        tree.body = body;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check( JCFunction tree)
    {
        boolean right= true;
       /* if(tree.retTypeExpr== null ) {
            error("函数缺少返回值");
           right =false;
        }*/
        if( tree.nameToken== null ||  StringHelper.isNullOrEmpty( tree.nameToken.identName)) {
            error(CompileMessagesUtil.FunctionMissingName);//  error("函数名称不能为空");
            right =false;
        }
        if( tree.params== null ) {
            error(CompileMessagesUtil.FunctionMissingParameters);//  error("函数缺少形参");
            right =false;
        }
        if( tree.body== null ) {
            error(CompileMessagesUtil.FunctionMissingBody);//  e   error("缺少函数体");
            right =false;
        }
        return right;
    }


    public JCMacroDecl macroDecl(ArrayList<ProcItem> items , JCBlock body)
    {
        JCMacroDecl tree = new JCMacroDecl( );
        tree.items = new ProcItem[items.size()];
        for(int i=0;i<items.size();i++)
        {
            tree.items[i] = items.get(i);
            tree.items[i].index = i;
        }
        tree.body = body;
        initTree(tree);
       // check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check( JCMacroDecl tree)
    {
        return true;
    }

    /** 创建类型声明 JCVariableDecl */
   /* public JCVariableDecl VarDef( JCExpression varType,JCIdent nameExpr,  JCExpression init ,boolean isLocalVar) {
        JCVariableDecl tree = new JCVariableDecl( );
        tree.varType = varType;
        tree.nameExpr = nameExpr;
        tree.init = init;
        initTree(tree);
        check(tree,isLocalVar);
        return tree;
    }*/
    public JCVariableDecl VarDef( JCIdent nameExpr,  JCExpression init ,boolean isLocalVar) {
        JCVariableDecl tree = new JCVariableDecl( );
        tree.nameExpr = nameExpr;
        tree.init = init;
        initTree(tree);
        check(tree,isLocalVar);
        return tree;
    }
    boolean check( JCVariableDecl tree,boolean isLocalVar)
    {
        boolean right= true;
      /*  if( tree.varType ==null)
        {
            error("变量缺少声明类型");
            right =false;
        }*/
        if( tree.nameExpr ==null)
        {
            error(CompileMessagesUtil.VariableMissingName);//   error("变量缺少名称");
            right =false;
        }
        //if(isLocalVar && tree.init==null)
        //{
        //    error("声明局部变量时必须赋值");
       //     right =false;
       // }
        return right;
    }

    /** 创建语句块JCBlock */
    public JCBlock Block( ArrayList<JCStatement> stats) {
       /*  for(JCTree stmt:tree.stats)
        {
            if(stmt instanceof JCBlock)
            {
                stmt.error("代码块内禁止嵌套代码块");
            }
            stmt.scan(this,context);
        }*/
        JCBlock tree = new JCBlock( this.posToken);
        tree.statements = stats;
        initTree(tree);
        return tree;
    }

    /** 创建while循环 */
    public JCWhile WhileLoop(JCExpression cond, JCStatement body) {
        /*if(cond==null) {
            error("while循环语句缺少条件表达式");
           return  null;
        }
        if(body==null) {
            error("while循环语句缺少循环体");
            return  null;
        }*/
        if(cond instanceof JCParens)
        {
            JCParens jcParens = (JCParens) cond;
            cond = jcParens.expr;
        }
        JCWhile tree = new JCWhile( );
        tree.cond = cond;
        tree.body = body;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCWhile tree)
    {
        boolean right =true;
        if(tree.cond==null) {
            error(CompileMessagesUtil.WhileLoopMissingCondition);//  error("while循环语句缺少条件表达式");
            right =false;
        }
        if(tree.body==null) {
            error(CompileMessagesUtil.WhileLoopMissingBody);//   error("while循环语句缺少循环体");
            right =false;
        }
        return right;
    }

    /** 创建JCForLoop循环 */
    /*public JCForLoop ForLoop(JCStatement init,
                             JCExpression cond,
                             JCExpression step,
                             JCStatement body)
    {
        JCForLoop tree = new JCForLoop(init, cond, step, body);
        initTree(tree);
        return tree;
    }*/

    /** 创建JCIf */
    public JCIf If(JCExpression cond, JCStatement thenpart, JCStatement elsepart) {
        if(cond==null) {
            error(CompileMessagesUtil.IfMissingCondition);//  error("if语句缺少条件表达式");
            return null;
        }
        if(thenpart==null)
        {
            error(CompileMessagesUtil.IfMissingBody);//  error("if语句缺少执行语句");
            return null;
        }
        JCIf tree = new JCIf();
        tree.cond = cond;
        tree.thenpart = thenpart;
        tree.elsepart = elsepart;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCIf tree)
    {
        boolean right =true;
        if(tree.cond==null) {
            error(CompileMessagesUtil.IfMissingCondition);//     error("if语句缺少条件表达式");
            right =false;
        }
        if(tree.thenpart==null)
        {
            error(CompileMessagesUtil.IfMissingBody);// error("if语句缺少执行语句");
            right =false;
        }
        return right;
    }

    /* 创建表达式语句 JCExpressionStatement */
    public JCExprStatement Exec(JCExpression expr) {
        JCExprStatement tree = new JCExprStatement();
        tree.expr = expr;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCExprStatement tree)
    {
        boolean right =true;
        if(tree.expr==null)
        {
            error(CompileMessagesUtil.StatementMissingExpression);//   error("语句缺少表达式");
            right =false;
        }
        return right;
    }

    /** 创建JCBreak语法树 */
   /* public JCBreak Break( ) {
        JCBreak tree = new JCBreak();
        initTree(tree);
        return tree;
    }*/

    /** 创建JCContinue语法树 */
  /*  public JCContinue Continue() {
        JCContinue tree = new JCContinue();
        initTree(tree);
        return tree;
    }*/

    public BreakStatement Break(   ) {
        BreakStatement tree = new BreakStatement();
        initTree(tree);
        return tree;
    }
/*
    public ContinueStatement Continue(   ) {
        var tree = new ContinueStatement();
        initTree(tree);
        return tree;
    }*/

    /** 创建JCReturn语法树 */
    public JCReturn Return(JCExpression expr) {
        JCReturn tree = new JCReturn();
        tree.expr = expr;
        initTree(tree);
        return tree;
    }

    public JCMethodInvocation Apply(JCExpression meth, JCExpression[] args)
    {
        JCMethodInvocation tree = new JCMethodInvocation( );
        tree.methodExpr = meth;
        tree.setArgs( args);
        initTree(tree);
        return tree;
    }

    public JCArrayLiteral arrayLiteral(ArrayList<JCExpression> elements)
    {
        JCArrayLiteral tree = new JCArrayLiteral(  ListUtil.toExprArray( elements));
        initTree(tree);
        return tree;
    }
/*
    public ParenExpr parenExpr(  ArrayList<JCExpression> elements)
    {
        ParenExpr tree = new ParenExpr( ListUtil.toExprArray( elements));
        initTree(tree);
        return tree;
    }*/

    public JCDynamicLiteral dynamicLiteral(TokenKind spliteKind, ArrayList<JCTree> elements)
    {
        JCDynamicLiteral JCDynamicLiteral = new JCDynamicLiteral( );
        JCDynamicLiteral.pairs = new ArrayList<>();
        initTree(JCDynamicLiteral);
        for(var element :elements)
        {
            if(element instanceof JCPair)
            {
                JCDynamicLiteral.pairs.add((JCPair) element);
            }
            else
            {
                element.error(CompileMessagesUtil.DynamicMemberShouldBePair);//  element.error("动态变量的成员应该是名称键值对");
            }
        }
        return JCDynamicLiteral;
    }


    public JCLambda lambda(TokenKind spliteKind, ArrayList<JCTree> elements)
    {
        JCLambda tree = new JCLambda(spliteKind, ListUtil.toAstArray( elements),this.posToken);
        initTree(tree);
        return tree;
    }

    public JCMacroCall ProcCallExpr(Stack<JCExpression> stack)
    {
        int size = stack.size();
       JCExpression[] array = new JCExpression[size];
        for (int i=size-1;i>=0 ;i--)
        {
            array[i] =stack.pop();
        }
       /*
        if(array.length>0)
        {
            var first = array[0];
            if(!(first instanceof JCIdent))
            {
                error("宏调用必须第一个元素为标识符");
            }
        }
        */

        JCMacroCall tree = new JCMacroCall( array);
        initTree(tree);
        return tree;
    }
/*
    public JCNewClass NewClass(JCExpression clazzExpr, ArrayList<JCExpression> args)
    {
        JCNewClass tree =new JCNewClass(  ) ;
        tree.clazzExpr = clazzExpr;
        tree.args = ListUtil.toExprArray(args) ;
        //this.args.addAll(args);
        initTree(tree);
        return tree;
    }*/
/*
    public JCNewArray NewArray(JCExpression elemtype)
    {
        JCNewArray tree = new JCNewArray();
        tree.elemtype = elemtype;
        //this.lengthExpr = dims;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }*/
/*
    public boolean check(JCNewArray tree)
    {
        boolean right =true;
        if(tree.elemtype==null)
        {
            error("创建数组表达式缺少类型");
            right =false;
        }
        return right;
    }*/

    public JCParens Parens(JCExpression expr,boolean allowEmpty) {
       // if(expr==null)
       //     error("括号内缺少表达式");

        JCParens tree = new JCParens();
        tree.expr = expr;
        initTree(tree);
        if(!allowEmpty)
            check(tree); //if(!check(tree)) return null;
        return tree;
    }

    public boolean check(JCParens tree)
    {
        boolean right =true;
        if(tree.expr==null)
        {
            error(CompileMessagesUtil.BRACEMissingExperssion);//     error("括号内缺少表达式");
            right =false;
        }
        return right;
    }

    /** 创建赋值表达式 */
    public JCAssign Assign(JCExpression lhs, JCExpression rhs) {
        JCAssign tree = new JCAssign( );
        tree.left = lhs;
        tree.right = rhs;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCAssign tree)
    {
        boolean right =true;
        if(tree.left==null)
        {
            error(CompileMessagesUtil.AssignLeftMissingExperssion);//    error("赋值语句左边缺少表达式");
            right =false;
        }

        if(tree.right ==null) {
            error(CompileMessagesUtil.AssignRightMissingExperssion);//   error("赋值语句右边缺少表达式");
            right =false;
        }
        return right;
    }

    public JCUnary Unary(TokenKind opcode, JCExpression arg) {
        JCUnary tree = new JCUnary( );
        tree.opcode = opcode;
        tree.expr = arg;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCUnary tree)
    {
        boolean right =true;
        TokenKind opcode = tree.opcode;
        if(!(opcode== TokenKind.ADD ||opcode== TokenKind.SUB ||opcode== TokenKind.NOT))
        {
          error(CompileMessagesUtil.UnaryExpressionOpShouldBe);//  error("单目表达式前缀只能为'+','-','!'");
            right =false;
        }

        if( tree.expr==null)
        {
            error(CompileMessagesUtil.UnaryExpressionRightMissingExperssion);//  error("单目后边缺少表达式");
            right =false;
        }
        return right;
    }

    public JCExpression Colon(  JCExpression lhs, JCExpression rhs) {
        JCPair tree = new JCPair();
            tree.left = lhs;
            tree.right = rhs;
            initTree(tree);
          //  check(tree); //if(!check(tree)) return null;
            return tree;

    }
    public JCExpression Binary(TokenKind opcode, JCExpression lhs, JCExpression rhs) {
        if(lhs!=null) {
            JCBinary tree = new JCBinary();
            tree.opcode = opcode;
            tree.left = lhs;
            tree.right = rhs;
            initTree(tree);
            check(tree); //if(!check(tree)) return null;
            return tree;
        }
        else
        {
            return Unary(opcode,rhs);
        }
    }

    boolean check(JCBinary tree)
    {
        boolean right =true;
        if( tree.left ==null)
        {
            error(CompileMessagesUtil.BinaryLeftMissingExperssion);//error("二元运算表达式左边缺少表达式");
            right =false;
        }
        if( tree.right ==null)
        {
            error(CompileMessagesUtil.BinaryRightMissingExperssion);//  error("二元运算表达式右边缺少表达式");
            right =false;
        }
        return right;
    }
/*
    public JCArrayAccess ArrayAccess(JCExpression indexed, JCExpression index) {
        JCArrayAccess tree = new JCArrayAccess( );
        tree.indexed = indexed;
        tree.index = index;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }*/
/*
    boolean check(JCArrayAccess tree)
    {
        boolean right =true;
        if(tree.indexed==null)
        {
            error("数组访问表达式左边缺少数组");
            right =false;
        }

        if(tree.index==null) {
            error("数组访问表达式左边缺少索引");
            right =false;
        }
        return right;
    }
*/
    public JCFieldAccess FieldAccess(JCExpression selected, Token selector) {
        JCFieldAccess tree = new JCFieldAccess(  );
        tree.selected = selected;
        tree.nameToken = selector;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCFieldAccess tree)
    {
        boolean right =true;
        if(tree.selected ==null) {
            error(CompileMessagesUtil.DotExperssionMissingName);//     error("缺少被限定名称");
            right =false;
        }
        if(tree.nameToken==null || StringHelper.isNullOrEmpty(tree.nameToken.identName))
        {
            error(CompileMessagesUtil.DotExperssionMissingName);//    error("缺少限定名称");
            right =false;
        }
        return right;
    }

    public JCIdent Ident(Token nameToken) {
        if(nameToken==null ||   StringHelper.isNullOrEmpty(nameToken.identName))
        {
            error(CompileMessagesUtil.IdentMissingName);//   error("标识符不能为空");
            return  null;
        }
        JCIdent tree = new JCIdent(nameToken);
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCIdent tree)
    {
        boolean right =true;
        if(tree.nameToken ==null ||   StringHelper.isNullOrEmpty(tree.nameToken.identName)) {
            error(CompileMessagesUtil.IdentMissingName);//   error("标识符不能为空");
            right =false;
        }
        return right;
    }

    public JCLiteral Literal(Object value) {
        JCLiteral tree = new JCLiteral( value);
        initTree(tree);
        return tree;
    }

    /** 创建基本类型语法树实例 */
   /* public JCPrimitiveTypeTree PrimitiveType(TokenKind kind) {
        JCPrimitiveTypeTree tree = new JCPrimitiveTypeTree( );
        tree.kind = kind;
        initTree(tree);
        return tree;
    }
    */
/*
    public JCArrayTypeTree TypeArray(JCExpression elemtype) {
        JCArrayTypeTree tree = new JCArrayTypeTree();
        tree.elemType = elemtype;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }*/
/*
    boolean check(JCArrayTypeTree tree)
    {
        boolean right =true;
        if(tree.elemType ==null)
        {
            error("数组声明缺少类型");
            right =false;
        }
        return right;
    }*/
/*
    public JCErroneous Erroneous() {
        return Erroneous(new ArrayList<>());
    }*/
/*
    public JCErroneous Erroneous(ArrayList<? extends JCTree> errs) {
        JCErroneous tree = new JCErroneous();
        tree.errs  = errs;
        tree.log= log;
        tree.pos = pos;
        tree.line = line;
        return tree;
    }*/
}
