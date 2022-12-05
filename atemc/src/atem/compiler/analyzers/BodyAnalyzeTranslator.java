package atem.compiler.analyzers;

import atem.compiler.CompilerConsts;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.ast.models.BelongsInfo;
import atem.compiler.lex.Token;
import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.*;
import atem.compiler.ast.*;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.CompileContext;
import atem.compiler.utils.CompileError;

import java.util.ArrayList;

public class BodyAnalyzeTranslator extends TreeTranslator<BodyContext>
{
    CompileContext compileContext;

    public BodyAnalyzeTranslator(CompileContext compileContext)
    {
        this.compileContext = compileContext;
    }

    public JCFileTree translate(JCFileTree compilationFile)
    {
        translate(compilationFile,null);
        return compilationFile;
    }

    public void visitMethods(JCFileTree tree) {
        BodyContext  globalContext = new BodyContext();
        globalContext.methodSymbol = null;
        tree.fieldDecls = new ArrayList<>();

        globalContext.searchKinds = new SearchKinds();

        for(JCFunction methodDecl:tree.functions)
            methodDecl.translate(this,globalContext);
        for(JCMacroDecl JCMacroDecl :tree.JCMacroDecls)
            JCMacroDecl.translate(this,globalContext);
    }

    /*
    public JCTree translateCompilationUnit(JCFileTree compilationFile, BodyContext arg)
    {
        for(int i=0;i<compilationFile.methods.size();i++)
            compilationFile.methods.get(i).translate(this,arg);
        return compilationFile;
    }
 */

    public JCTree translateMethod(JCFunction tree , BodyContext arg)
    {
      //  tree.retVarSymbol = createRetVarSymbol(arg,tree.scope);
      //  arg = new BodyContext();
        arg = arg.copy( new SearchKinds());
       // arg.callableAST = tree;
        /* 放入当前函数符号 */
        arg.methodSymbol =  tree.methodSymbol;
        /* 放入当前函数作用域 */
     //   arg.scope = tree.scope;
       // tree.body.bodyKind = JCBlock.BodyKind.FunctionBody;
        /* 设置默认搜索标识符 */
       // arg.searchKinds = new SearchKinds();
        /* 分析函数体 */
        tree.body.translate  (this,arg);
        return tree;
    }

    DVarSymbol createRetVarSymbol(BodyContext arg, SymbolScope methodScope , BelongsInfo belongsInfo,int dimPos)
    {
        BTypeSymbol typeSymbol = RClassSymbolManager.ObjectSymbol;
        String varName = CompilerConsts.defaultRetVarName;
        var kind =  VarSymbolKind.localVar;
        var varSymbol = new DVarSymbol(varName, kind, typeSymbol, belongsInfo,false,dimPos);
        varSymbol.ownerType = belongsInfo.fileTree.fileSymbol;
        varSymbol.isStatic = false;
        belongsInfo.scope.addSymbol(varSymbol);
        return varSymbol;
    }

    DVarSymbol createLambdaVarArraySymbol(BodyContext arg,SymbolScope methodScope, BelongsInfo belongsInfo)
    {
        BTypeSymbol typeSymbol = BArrayTypeSymbol.ObjectArraySymbol;
        String varName = CompilerConsts.lambdaInnerClassLocal;
        var kind =  VarSymbolKind.localVar;
        var varSymbol = new DVarSymbol(varName, kind, typeSymbol, belongsInfo,false,-1);
        varSymbol.ownerType = belongsInfo.fileTree.fileSymbol;
        varSymbol.isStatic = false;
        belongsInfo.scope.addSymbol(varSymbol);
        return varSymbol;
    }

/*
    public JCTree translateArrayType(JCArrayTypeTree tree, BodyContext arg)
    {
        return tree;
    }*/

  /*  public JCTree translatePrimitiveType(JCPrimitiveTypeTree tree, BodyContext arg)
    {
        tree.symbol= AnalyzerUtil.getPrimitiveSymbol(tree.kind); //保存符号
        return tree;
    }*/

    public JCTree translateMacro(JCMacroDecl tree, BodyContext arg)
    {
      //  tree.retSymbol = createRetVarSymbol(arg);
        arg = arg.copy( new SearchKinds());
        /* 放入当前函数符号 */
       // arg.methodSymbol =  tree.methodSymbol;
        /* 放入当前函数作用域 */
       // arg.scope = tree.scope;
        /* 设置默认搜索标识符 */
       // arg.searchKinds = new SearchKinds();
        /* 分析函数体 */
        tree.body.translate  (this,arg);
        return tree;
    }

    public JCTree translateBlock(JCBlock tree, BodyContext arg)
    {
        tree.statements = translates(tree.statements,arg);
        if(tree.bodyKind== JCBlock.BodyKind.FunctionBody) {
            tree.belongsInfo.defedCallableAST.getCallableCinfo().retVarSymbol=(createRetVarSymbol(arg, tree.belongsInfo.scope, tree.belongsInfo,-1));
            tree.belongsInfo.defedCallableAST.getCallableCinfo().lambdaVarArraySymbol =( createLambdaVarArraySymbol(arg,tree.belongsInfo.scope, tree.belongsInfo));
        }
        return tree;
    }

    protected <T extends JCTree> T translate(T tree, BodyContext arg)
    {
        if(tree==null) return null;
        if(tree.propertys._isAnalyzered) return tree;
        JCTree newTree = tree.translate(this, arg);
        newTree.propertys = tree.propertys;
        tree.isAnalyzered(true);
        return (T) newTree;
    }

    public JCTree translateAssign(JCAssign tree, BodyContext arg)
    {
        if(tree.left instanceof JCFieldAccess)
        {
            ( (JCFieldAccess)tree.left).isAssignLeft=true;
        }
        BodyContext newContext = arg.copy(new SearchKinds());
        tree.left =translate(tree.left,newContext);
        tree.right =translate(tree.right,newContext);
       // AnalyzerUtil.checkAssignable(tree.left,tree.right);
       // if(!AnalyzerUtil.writable(tree.left))
       //     tree.left.error("表达式'%s'是只读的,不能被修改",tree.left.toString());
        return tree;
    }

    /**分析参数并且返回这些参数的类型符号列表  */
    /*private BTypeSymbol[] attrArgs(JCExpression[] args, BodyContext context)
    {
        BTypeSymbol[] argTypes =new BTypeSymbol[args.length];
        int i=0;
        for (JCExpression item : args)
        {
            if(TreeeUtil.isEmptyParens(item))
            {
                if(args.length!=1)
                {
                    item.error("空参数'%s'只能有一个", item.toString());
                    item.symbol = new BErroneousSymbol();
                }
                continue;
            }
            BodyContext argContext = context.copy( new SearchKinds());
            JCExpression newExpr = translate(item, argContext);
            if( newExpr.symbol instanceof BErroneousSymbol)
            {
                return  null;
            }
            else if(newExpr.symbol instanceof BMultiSymbol)
            {
                BMultiSymbol multiSymbol=(BMultiSymbol)newExpr.symbol;
                var methodSymbol =multiSymbol.filterByArgCount(args.length);
                if(methodSymbol!=null)
                {
                    newExpr.symbol = methodSymbol;
                }
                else {
                    item.error("有歧义的多个符号'%s'", item.toString());
                    return null;
                }
            }
            if(item.symbol==null)
            {
                item.symbol = new BErroneousSymbol();
                //throw new CompileError();
            }
            BTypeSymbol argTypeSymbol = item.symbol.getTypeSymbol();
            if(SymbolUtil.isVoid(argTypeSymbol))
            {
                item.error("没有返回值'%s'", item.toString());
                return  null;
            }
            argTypes[i]=argTypeSymbol;
            i++;
        }
        return argTypes;
    }*/

    public JCTree translatePackage(JCPackage tree, BodyContext arg)
    {
        return tree;
    }

    public JCTree translateFieldAccess(JCFieldAccess tree, BodyContext arg)
    {
        Token nameToken = tree.nameToken;
        var memberName = nameToken.identName;
       // if(memeber.equals(AtemConsts.propertyMember))
        //    Debuger.outln("translateFieldAccess 220 : "+tree);
        tree.selected = translate(tree.selected,arg.copy(new SearchKinds(true,true)));//,false)));
        Symbol selectedSymbol = tree.selected.symbol;
        if(selectedSymbol==null)
            throw new CompileError();
        if(memberName.equals(CompilerConsts.NEW))
        {
            tree.symbol =selectedSymbol;
            return tree;
        }
        /*
        if(memberName.equals(AtemLanguageConst._instanceof))
        {
            tree.isInstanceof =true;
            tree.symbol =RClassSymbolManager.booleanPrimitiveSymbol;
            return tree;
        }*/

        if(selectedSymbol.equals(RClassSymbolManager.ObjectSymbol) )
        {
            attrMemberSymbol(tree ,false);
        }
        else  if(selectedSymbol instanceof BTypeSymbol)
        {
            /* 1: 类型名称访问成员,表面它是静态成员 */
            attrMemberSymbol(tree ,true);
        }
        else if(selectedSymbol instanceof BVarSymbol)
        {
            /* 2: 被选择表达式是变量,查找它的实例成员 */
            attrMemberSymbol(tree,false);
        }
        else if(selectedSymbol instanceof BMultiSymbol)
        {
            /* 3: 类型是BManySymbol,说明有多个类型，无法唯一确定，导致程序有歧义 */
            tree.error( nameToken , "有歧义的多个符号'%s'", nameToken.identName);
            tree.symbol= new BErroneousSymbol();
        }
        else {
            throw new CompileError();
        }

        return tree;
    }

    /* 分析类型成员 */
    private static void attrMemberSymbol(JCFieldAccess tree,  boolean isStatic )
    {
        BTypeSymbol typeSymbol = tree.selected.symbol.getTypeSymbol();
        Token nameToken = tree.nameToken;
        String memberName = nameToken.identName;
        if(memberName.equals(CompilerConsts.NEW)
            //|| memberName.equals(AtemLanguageConsts.PROPERTY)
        )
        {
            return;
        }
        ArrayList<Symbol> symbols = typeSymbol.findMembers(memberName);
        if(symbols==null || symbols.size()==0)
        {
            if(typeSymbol.equals(RClassSymbolManager.ObjectSymbol))
            {
                tree.symbol = RClassSymbolManager.ObjectSymbol;
            }
            else {
                /* 没有找到成员报错 */
               // tree.error(nameToken, "找不到符号'%s'", memberName);
               // tree.symbol = new BErroneousSymbol();
                tree.symbol = RClassSymbolManager.ObjectSymbol;
            }
        }
        else if(symbols.size()==1)
        {
            Symbol symbol =symbols.get(0);
            tree.symbol = symbol;
            /* 只找到一个成员时,检查static修饰符是否相符 */
           // if(SymbolUtil.isStatic(symbol)!=isStatic)
          //      tree.error(nameToken, "'%s'静态修饰符不同", memberName);
        }
        else
        {
            /* 找到多个成员，一般都是重载的函数，按isStatic进行筛选 */
            ArrayList<Symbol> symbols2 = new ArrayList<>();
            for(Symbol symbol :symbols)
            {
                if(SymbolUtil.isStatic(symbol)==isStatic)
                    symbols2.add(symbol);
            }

            if(symbols2.size()==0)
            {
                tree.error(nameToken, "'%s'静态修饰符不同",memberName);
                tree.symbol = new BErroneousSymbol();
            }
            else if(symbols2.size()==1)
            {
                tree.symbol = symbols2.get(0);
            }
            else
            {
                /* 筛选后还有多个,把这些符号合并到BManySymbol中,让下一步分析进行筛选 */
                BMultiSymbol moreSym = new BMultiSymbol(memberName,symbols2);
                tree.symbol  = moreSym;
            }
        }
    }

    public JCTree translateBinary(JCBinary tree, BodyContext arg)
    {
        BodyContext context = arg.copy(new SearchKinds());
        tree.left =translate(tree.left,context);
        tree.right =translate(tree.right,context);

        /** 检查是否是字符串联结表达式 */
        if(tree.opcode.equals(TokenKind.ADD))
        {
            if(AnalyzerUtil.isString(tree.left)||AnalyzerUtil.isString(tree.right))
            {
                tree.isStringContact = true;
            }
        }
        /* 分析表达式返回值 */
        switch (tree.opcode)
        {
            case AND: case OR: case GT:case GTEQ: case LTEQ: case  LT: case EQEQ: case NOTEQ:
            tree.symbol=RClassSymbolManager.booleanPrimitiveSymbol;
            break;
            case SUB: case MUL: case DIV:
            tree.symbol= RClassSymbolManager.intPrimitiveSymbol;
            break;
            case ADD:
                tree.symbol=tree.isStringContact ? RClassSymbolManager.StringSymbol :RClassSymbolManager.intPrimitiveSymbol;
                break;
        }
        /* 检查左右表达式类型是否正确 */
        switch (tree.opcode)
        {
            case AND: case OR:
            AnalyzerUtil.checkBoolean(tree.left);
            AnalyzerUtil.checkBoolean(tree.right);
            break;
            case GT:case GTEQ: case LTEQ: case  LT: case EQEQ: case NOTEQ:
            case ADD:case SUB: case MUL: case DIV:
          /*  if(!tree.isStringContact)
            {
                AnalyzerUtil. checkNumber(tree.left);
                AnalyzerUtil.checkNumber(tree.right);
            }*/
            break;
        }
        return tree;
    }

    public JCTree translateDynamicLiteral(JCDynamicLiteral tree, BodyContext arg)
    {
        ArrayList<JCPair> newPairs = new  ArrayList<JCPair>();
        for (JCPair item : tree.pairs)
        {
            item.isDynamicMember = true;
            JCPair jcTree =  (JCPair)item.translate(this,arg);
            if(jcTree!=null)
            {
                newPairs.add(jcTree);
            }
        }
        tree.pairs = newPairs;
        tree.symbol = RClassSymbolManager.DynamicClassSymbol;
        return tree;
    }

    public JCTree translatePair(JCPair tree, BodyContext arg)
    {
        if(tree.isDynamicMember)
        {
            if(!(tree.left instanceof JCIdent))
            {
                tree.error("动态类成员必须是ident标识");
            }
        }
        else if(tree.arrayLiteral!=null)
        {
            if(tree.left instanceof JCIdent)
            {
                JCIdent jcIdent =(JCIdent) tree.left;
                if(jcIdent.getName().equals(CompilerConsts.DEFAULT))
                {
                    tree.arrayLiteral.defaultPair = tree;
                    tree.isMapDefaultItem =true;
                }
            }
        }
        else
        {
             tree.left =translate(tree.left,arg);
        }
        tree.right =translate(tree.right,arg);
        tree.symbol = RClassSymbolManager.PairClassSymbol;
        return tree;
    }

    public JCTree translateIdent(JCIdent tree, BodyContext arg)
    {
        String varName = tree.getName();
       // if(varName.equals("Core"))
      //      Debuger.outln("408 translateIdent:"+varName+" "+tree.posToken.pos+" "+tree.posToken.line);
        if(tree.isDollarIdent) {
            var  defedCallableAST = tree.belongsInfo.defedCallableAST;
            if(defedCallableAST instanceof JCLambda)
            {
                JCLambda jcLambda = (JCLambda) defedCallableAST;
                DVarSymbol psymbol = null;
                if( jcLambda.methodSymbol.contains(varName))
                {
                    psymbol = jcLambda.methodSymbol.parametersMap.get(varName);
                }
                else
                {
                    psymbol =new DVarSymbol(varName, VarSymbolKind.FuncParameter  , RClassSymbolManager.ObjectSymbol, tree.belongsInfo, false,tree.nameToken.pos);
                    psymbol.isLambdaParameter =true;
                    jcLambda.methodSymbol.addParameter(psymbol);
                }
                tree.symbol= psymbol;
            }
            else
            {
                tree.error(tree.nameToken, "'%s'只能在Lambda内声明使用", "$变量");
            }
        }
        else
        {
            SearchKinds searchKinds = new SearchKinds();
            searchKinds.isSearchType = true;
            searchKinds.isSearchVar = true;
           // searchKinds.isSearchMethod = tree.isInvocationMethod;
            ArrayList<Symbol> symbols = SearchSymbol.findIdents(tree.belongsInfo.scope, varName, searchKinds, this.compileContext, tree.belongsInfo,tree.nameToken.pos);
            if (symbols.size() == 1) {
                Symbol symbol = symbols.get(0);
                if(tree.propertys.isMacroCallArg && symbol instanceof DVarSymbol)
                {
                    DVarSymbol dVarSymbol = (DVarSymbol) symbol;
                    dVarSymbol.isMacroCallArg = tree.propertys.isMacroCallArg;
                }
                tree.symbol = symbol;
                if (symbol instanceof RClassSymbol)
                    tree.isTypeName = true;
            }
            else if (symbols.size() == 0) {
                tree.error(tree.nameToken, "找不到变量'%s'", varName);
                tree.symbol = new BErroneousSymbol();
            }
            else {
                if(tree.isInvocationMethod)
                {
                    ArrayList<BMethodSymbol> methodSymbols = new ArrayList<>();
                     for(var symbol : symbols)
                     {
                         if(symbol instanceof BMethodSymbol)
                         {
                             methodSymbols.add((BMethodSymbol)symbol);
                         }
                     }
                     if(methodSymbols.size()==1)
                     {
                         tree.symbol = methodSymbols.get(0);
                         return tree;
                     }
                     else if( methodSymbols.size()>1)
                     {
                         BMultiSymbol manySymbol = BMultiSymbol.create(varName,methodSymbols);
                         tree.symbol = manySymbol;
                         return tree;
                     }
                }
                tree.error(tree.nameToken, "变量'%s'不明确", varName);
                tree.symbol = new BErroneousSymbol();
                //BManySymbol moreSym = new BManySymbol(varName, symbols);
                // tree.symbol = moreSym;
            }
        }
        return tree;
    }

    public JCTree translateLiteral(JCLiteral tree, BodyContext arg)
    {
        /* 获取常量值对应的 Class */
        TokenKind tokenKind = AnalyzerUtil.getLiteralKind(tree);
        // Class<?> clazz = AnalyzerUtil.getLiteralClazz(tokenKind);
        //tree.symbol= RClassSymbolManager.getSymbol(clazz); //保存符号
        tree.symbol= AnalyzerUtil.getPrimitiveSymbol(tokenKind);
        return tree;
    }

    public JCTree translateParens(JCParens tree, BodyContext arg) {
        //if(tree.expr==null)
       //     Debuger.outln("518 translateParens:");
        tree.expr = translate(tree.expr, arg);
        if( tree.expr!=null)
            tree.symbol = tree.expr.symbol;
        return tree;
    }

    public JCTree translateUnary(JCUnary tree, BodyContext arg) {
        BodyContext context = arg.copy( new SearchKinds());//只搜索变量
        tree.expr = translate(tree.expr, context);
        TokenKind opcode= tree.opcode;
        /* 检查条件表达式的结果是不是boolean类型 */
        if(opcode== TokenKind.NOT && !AnalyzerUtil.isBoolean(tree.expr)) {
            tree.expr.error("不兼容的类型,无法转换为boolean");
            tree.symbol = RClassSymbolManager.booleanPrimitiveSymbol;
        }
        else if((opcode== TokenKind.SUB ||opcode== TokenKind.ADD) && !AnalyzerUtil.isInt(tree.expr)) {
            tree.expr.error("不兼容的类型,无法转换为int");
            tree.symbol = RClassSymbolManager.intPrimitiveSymbol;
        }
        else
            tree.symbol =  tree.expr.symbol.getTypeSymbol();
        tree.symbol =tree.expr.symbol;
        return tree;
    }

    public JCTree translateReturn(JCReturn tree, BodyContext arg) {
        if(tree.belongsInfo.defedCallableAST instanceof JCMacroDecl)
        {
            if(tree.expr!=null)
            {
                tree.expr.error("Macro方法体中的return语句不能有返回值");
            }
        }
        tree.expr = translate(tree.expr, arg);

        /* 取出函数的返回类型 */
      //  BTypeSymbol returnTypeSymbol =  arg.methodSymbol.returnType;
    //    boolean isVoid =  SymbolUtil.isVoid(returnTypeSymbol);
   //     if(isVoid && tree.expr!=null)
   //         tree.expr.error("不需要返回值");
    //    if(!isVoid && tree.expr==null)
      //      tree.expr.error("缺少返回值" );
        /* 分析返回值类型是否与函数定义的返回类型兼容 */
     //   if (tree.expr != null && !isVoid)
    //    {
      //      BTypeSymbol rightType = tree.expr.symbol.getTypeSymbol();
      //      if(!AnalyzerUtil.checkAssignable(returnTypeSymbol,rightType))
      //          tree.expr.error("不兼容的返回类型");
    //    }
        return tree;
    }

    public JCTree translateIf(JCIf tree, BodyContext arg) {
        tree.cond = translate(tree.cond, arg);
        tree.thenpart = translate(tree.thenpart, arg);
        tree.elsepart = translate(tree.elsepart, arg);
        return tree;
    }

    public JCTree translateWhile(JCWhile tree, BodyContext arg)
    {
        tree.cond = translate(tree.cond, arg);
        tree.body = translate( tree.body,arg);
        return tree;
    }

    public JCTree translateVariable(JCVariableDecl tree, BodyContext arg) {
        //Debuger.outln("515 translateVariable:"+tree);
        tree.init = translate(tree.init, arg);
        if(checkVarName(tree.nameExpr)) {
            BTypeSymbol typeSymbol = RClassSymbolManager.ObjectSymbol;
            String varName = tree.nameExpr.getName();
            ArrayList<Symbol> symbols = SearchSymbol.findIdents(tree.belongsInfo.scope, varName,
                    new SearchKinds(), this.compileContext, tree.belongsInfo,tree.nameExpr.nameToken.pos);
            var isClinitStmt = tree.belongsInfo.statement.isClinitStmt;
            var kind = VarSymbolKind.localVar;
            var isStatic =  false;
            if( isClinitStmt && tree.propertys.isMacroCallArg==false)
            {
                kind = VarSymbolKind.field;
                isStatic =true;
            }

            /* 查找这个变量名称是否已经使用过 */
            if (symbols.size() == 0) {
                /* 创建新变量符号，并加入的当前作用域中 */
               var  varSymbol = new DVarSymbol(varName, kind, typeSymbol, tree.belongsInfo, isStatic,tree.nameExpr.posToken.pos);
                varSymbol.ownerType = tree.belongsInfo.fileTree.fileSymbol;
                varSymbol.isMacroCallArg = tree.propertys.isMacroCallArg;
                if (isClinitStmt && tree.propertys.isMacroCallArg==false) {
                    tree.belongsInfo.fileTree.belongsInfo.scope.addSymbol(varSymbol);
                    tree.belongsInfo.fileTree.fieldDecls.add(tree);
                    tree.isFileStaticField = true;
                }
                else {
                    tree.belongsInfo.scope.addSymbol(varSymbol);
                }
                tree.nameExpr.symbol = varSymbol;
            }
            else {
                tree.error(tree.nameExpr.nameToken, "已经定义了变量 '%s'", varName);
               var  varSymbol = (BVarSymbol) symbols.get(0); //错误处理，取前面已经存在的变量符号
                tree.nameExpr.symbol = varSymbol;
            }
            //tree.nameExpr.symbol = varSymbol;
        }
       // tree.symbol = RClassSymbolManager.VarDimInfoSymbol;
        /* 检查赋值类型 */
      /*  if (tree.init != null)
        {
            boolean assignable = AnalyzerUtil.checkAssignable(   tree.nameExpr.symbol.getTypeSymbol() ,   tree.init.symbol.getTypeSymbol());
            if(!assignable)
                tree.nameExpr.error( tree.nameExpr.nameToken, "变量'%s'无法赋值",tree.nameExpr.nameToken.identName );
        }*/
        return tree;
    }

    protected boolean checkVarName(JCIdent nameExpr)
    {
        if(nameExpr.isThis)
        {
            nameExpr.error("'%s'不能作为声明变量名称", CompilerConsts.Self);
            return false ;
        }
        else if(nameExpr.isTypeName)
        {
            nameExpr.error("'%s'与静态类型名称相同,不能作为声明变量名称",nameExpr.getName());
        }
        else if(nameExpr.isDollarIdent)
        {
            nameExpr.error("Lambda 内部参数'%s'不能作为声明变量名称",nameExpr.getName());
        }
        return true;
    }
    MethodInvocationAnalyzer methodInvocationAnalyzer = new MethodInvocationAnalyzer(this);
    public JCTree translateMethodInvocation(JCMethodInvocation tree, BodyContext arg)
    {
        return methodInvocationAnalyzer.translateMethodInvocation(tree,arg);
    }

   /* public JCMethodInvocation translateInstanceof(JCMethodInvocation tree, BodyContext arg)
    {
        tree.isInstanceof = true;
        tree.symbol  =RClassSymbolManager.booleanPrimitiveSymbol;
        BTypeSymbol[] argTypes = attrArgs(tree.getArgs(),arg);
        if(argTypes==null ){
            tree.symbol = new BErroneousSymbol();
            return tree;
        }
        if(tree.getArgs().length!=1)
        {
            tree.error("调用'%s'参数之只是1个", AtemLanguageConst._instanceof);
            return tree;
        }
        JCExpression invoArg = tree.getArgs()[0];
        if(!(invoArg.symbol instanceof BTypeSymbol))
        {
            invoArg.error("'%s'调用的参数必须是类型", AtemLanguageConst._instanceof);
            return tree;
        }
        return tree;
    }*/

    public JCTree translateArrayLiteral(JCArrayLiteral tree, BodyContext arg)
    {
        int count =tree.elements.length;
        tree.symbol = RClassSymbolManager.AListSymbol;
        boolean isMap =false;
        if(count>0 && tree.elements[0] instanceof JCPair)
        {
            tree.symbol = RClassSymbolManager.AMapSymbol;
            isMap =true;
        }
        if(count>0)
        {
            if(isMap)
            {
                for (var ele :tree.elements)
                {
                    if(!(ele instanceof JCPair))
                    {
                        ele.error("不是map项");
                    }
                    else
                    {
                        JCPair jcPair =(JCPair)ele;
                        jcPair.arrayLiteral = tree;
                    }
                }
            }
            tree.elements = translates(tree.elements, arg);
        }
        return tree;
    }

    MacroCallAnalyzer macroCallAnalyzer = new MacroCallAnalyzer(this);
    public JCTree translateMacroCall(JCMacroCall tree, BodyContext arg)
    {
        JCExpression exp2 =  macroCallAnalyzer.visitProcCall(tree,arg);
       /* if(exp2 instanceof JCMacroCall)
        {
            JCMacroCall JCMacroCall2 = (JCMacroCall) exp2;
            for(var p : JCMacroCall2.getArgValues())
            {
                if(p instanceof JCLambda )
                {
                    JCLambda jcLambda = (JCLambda) p;
                    jcLambda.isMacroCallPart= true;
                }
            }
        }*/
        return exp2;
    }
/*
    public JCTree translateParenExpr(ParenExpr tree, BodyContext arg)
    {
       var temp =tree;
       while (true)
       {
           if(temp.items.length==1)
           {
               if(temp.items[0] instanceof ParenExpr)
               {
                   temp =(ParenExpr) temp.items[0];
               }
               else
                   break;
           }
           else
               break;
       }
        temp.items = translates(temp.items,arg);
        temp.belongsInfo =tree.belongsInfo;
        if(temp.items.length==1)
        {
            temp.symbol = temp.items[0].symbol;
        }
        return temp;
    }*/

    public JCTree translateLambda(JCLambda tree, BodyContext arg)
    {
        tree.methodSymbol =  new DMethodSymbol(tree.belongsInfo.fileTree.fileInnerClassSymbol, tree.methodName,false,true);
        arg= arg.copy( );
        int count = tree.body.statements.size();

        if(count==0)
        {
            tree.symbol =RClassSymbolManager.ObjectSymbol;
        }
        else
        {
           /* if(items.get(0) instanceof PairAST) {
                tree.symbol = RClassSymbolManager.ObjectSymbol;
            }
            else
                tree.symbol = RClassSymbolManager.voidPrimitiveSymbol;*/
        }
        tree.body.translate  (this,arg);
        tree.symbol = RClassSymbolManager.DotMeberClassSymbol;
        return tree;
    }

    public JCTree translateExprStmt(JCExprStatement tree, BodyContext arg)
    {
        tree.expr = translate(tree.expr,arg);
        return tree;
    }

    protected <R extends JCTree> ArrayList<R> translates(ArrayList<R> trees, BodyContext arg)
    {
        ArrayList<R> defs = new  ArrayList<R>();
        for (R deftree : trees) {
            JCTree newTree = deftree.translate(this, arg);
            if (newTree != null) {
                R nr = (R) newTree;
                defs.add(nr);
            }
        }
        return defs;
    }

    protected JCExpression[] translates(JCExpression[] trees, BodyContext arg)
    {
        JCExpression[] array = new JCExpression[trees.length];
        for(int i=0;i<trees.length;i++)
            array[i] = (JCExpression)translate(trees[i], arg);
        return array;
    }
}
