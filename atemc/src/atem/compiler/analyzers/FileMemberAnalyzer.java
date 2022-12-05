package atem.compiler.analyzers;
import atem.compiler.CompilerConsts;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.symbols.*;
import atem.compiler.ast.*;
import atem.compiler.tools.FileUtil;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.callables.proc.ProcItemParameter;
import atem.compiler.tools.runs.JarClassLoadUtil;
import atem.compiler.CompileContext;
import atem.compiler.utils.CompileError;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
/** 文件结构语义分析器 */
 class FileMemberAnalyzer {

    CompileContext compileContext;

    public FileMemberAnalyzer(CompileContext compileContext)
    {
        this.compileContext = compileContext;
    }

    private void loadClassPaths()
    {
        compileContext.urlClassLoaders = new ArrayList<>();
        var libPaths = compileContext.getLibPaths();
        if(libPaths !=null && libPaths.length>0)
        {
            for(String cp:libPaths)
            {
                File folder = new File(cp);
                if(folder.exists()==false)
                {
                    throw new CompileError();
                }
                compileContext.urlClassLoaders.addAll(  JarClassLoadUtil.loadFolder(folder));
            }
        }
    }

    /* 分析文件内包定义导入方法等 */
    public void visitFile(JCFileTree tree)
    {
        loadClassPaths();
        tree.fileSymbol =  new FileSymbol(FileUtil.getNameNoExt( tree.log.sourceFile));
        tree.fileInnerClassSymbol = new FileInnerClassSymbol(tree.fileSymbol);

        // 分析package
        visitPackageDef(tree.packageDecl, tree.fileSymbol );

        // 分析import
        /* importTypes 用于检测是否重复导入类型*/
        HashMap<String, String> importTypes = new HashMap<>();
        for(JCImport jcImport : tree.imports)
            visitImport(jcImport,tree,importTypes);
        HashMap<String, String> requireTypes = new HashMap<>();
        for(var jcRequire : tree.requires)
            visitRequire(jcRequire,tree,requireTypes,compileContext);

        HashMap<String,JCTree> defedProcs = new HashMap<>();
        /* 分析函数 */
        for(JCFunction methodDecl :tree.functions)
            visitMethodDef(methodDecl,tree.fileSymbol,tree.belongsInfo.scope,defedProcs);

        for(JCMacroDecl JCMacroDecl :tree.JCMacroDecls)
            visitMacroAST(JCMacroDecl,tree.fileSymbol,tree.belongsInfo.scope,   defedProcs);
    }

     /* 分析package定义 */
    private void visitPackageDef(JCPackage jcPackage, FileSymbol fileSymbol)
    {
        if(jcPackage==null) return;
        if(fileSymbol.packageName==null)
        {
            String packageName = AnalyzerUtil.fullName(jcPackage.packageName);
            fileSymbol.packageName = packageName;
        }
    }

     /* 分析import */
    private void visitImport(JCImport jcImport, JCFileTree fileTree,HashMap<String, String> importTypes)
    {
        /* 生成类型的全名称 */
        String fullName =AnalyzerUtil.fullName(jcImport.typeTree);
        /* 检查是否已经导入 */
        if(importTypes.containsKey(fullName))
        {
            jcImport.log.error(jcImport.typeTree.nameToken  ,String.format("'%s'重复导入", fullName));
            return;
        }
        else
        {
            fileTree.belongsInfo.scope.addPackage(fullName);
            importTypes.put(fullName,fullName);
        }
    }

    private void visitRequire(JCRequire jcRequire, JCFileTree fileTree,HashMap<String, String> requireTypes, CompileContext compileContext)
    {
        /* 生成类型的全名称 */
        String fullName =AnalyzerUtil.fullName(jcRequire.typeTree);
        /* 检查是否已经导入 */
        if(requireTypes.containsKey(fullName))
        {
            jcRequire.log.error(jcRequire.typeTree.nameToken  ,String.format("'%s'重复导入", fullName));
        }
        else
        {
            requireTypes.put(fullName,fullName);
             RClassSymbol classSymbol = RClassSymbolManager.forName(fullName,compileContext);
             if(classSymbol ==null) {
                 jcRequire.log.error(jcRequire.typeTree.nameToken  , String.format("类型'%s'不存在", fullName));
             }
             else
             {
                 fileTree.belongsInfo.scope.addRequire(classSymbol);
             }
        }
    }

     /* 分析函数定义 */
    void visitMethodDef(JCFunction tree, FileSymbol fileSymbol, SymbolScope fileScope,  HashMap<String,JCTree> defedProcs) {
        String defineSign = tree.createMethodDefineValue();
        if(defedProcs.containsKey(defineSign))
        {
            tree.error(tree.nameToken,"已经经定义了方法或函数 '%s'",tree.nameToken.identName);
            return;
        }
        defedProcs.put(defineSign,tree);
        tree.methodSymbol =  new DMethodSymbol(fileSymbol,tree.nameToken.identName,true,!tree.isClinitFunc );

        for (var i=0;i<tree.params.size();i++)// : tree.params)
        {
            JCVariableDecl variableDecl = tree.params.get(i);
            visitParameter(variableDecl,  tree. methodSymbol,tree.getScope() ,i,tree);
        }

        if (!fileSymbol.addMethod(tree.methodSymbol))
        {
            tree.error(tree.nameToken,"已经定义了方法 '%s'",tree.nameToken.identName);
        }
        fileScope.addSymbol(tree.methodSymbol);
    }

     /* 分析函数参数*/
    void visitParameter(JCVariableDecl tree, DMethodSymbol methodSymbol, SymbolScope methodScope,int index,JCFunction jcFunction ) {
        BTypeSymbol typeSymbol =RClassSymbolManager.ObjectSymbol;
        String parameterName = tree.nameExpr.getName();
        if (methodSymbol.parametersMap.contains(parameterName))
        {
            tree.error(tree.nameExpr.nameToken,"方法已经定义了参数 '%s'", parameterName);
            DVarSymbol paramSymbol = methodSymbol.parametersMap.get(parameterName);
            tree.symbol=paramSymbol;
        }
        else {
            DVarSymbol paramSymbol = new DVarSymbol( parameterName,  VarSymbolKind.FuncParameter, typeSymbol,tree.belongsInfo,false,tree.nameExpr.nameToken.pos);
            methodSymbol.addParameter(paramSymbol);
            methodScope.addSymbol(paramSymbol);
        }

        if(tree.nameExpr.isThis)
        {
            jcFunction.isSelfFirst = true;
            if(index!=0)
                tree.error(tree.nameExpr.nameToken," '%s' 参数应该放在第一个位置", CompilerConsts.Self);
            else
                methodSymbol.isSelfFirst =true;
        }
    }

    void visitMacroAST(JCMacroDecl tree, FileSymbol fileSymbol, SymbolScope fileScope  ,HashMap<String,JCTree> defedProcs) {
        String defineSign = tree.createMacroAnnotationValue();
        if(defedProcs.containsKey(defineSign))
        {
            tree.error(tree.posToken,"已经经定义了方法或函数 '%s'",defineSign);
            return;
        }
        defedProcs.put(defineSign,tree);
        String procName = tree.createMethodName();// .getFirstItemText();
        tree.macroSymbol =  new DMacroSymbol(fileSymbol,procName ,tree);
        for (ProcItemParameter variableDecl : tree.getParameters())
            visitProcASTParameter(variableDecl,  tree.macroSymbol,tree.getScope() );
    }

    void visitProcASTParameter(ProcItemParameter tree, DMacroSymbol methodSymbol, SymbolScope methodScope)
    {
        BTypeSymbol typeSymbol =RClassSymbolManager.ObjectSymbol;
        String parameterName = tree.name;
        if(tree.posToken==null)
            throw new CompileError();
        if (methodSymbol.parametersMap.contains(parameterName))
        {
            tree.error(tree.posToken ,"方法已经定义了参数 '%s'", parameterName);
            DVarSymbol paramSymbol = methodSymbol.parametersMap.get(parameterName);
            tree.symbol=paramSymbol;
        }
        else {
            DVarSymbol paramSymbol = new DVarSymbol( parameterName,  VarSymbolKind.MacroParameter, typeSymbol,tree.belongsInfo,false,tree.posToken.pos);
            methodSymbol.addParameter(paramSymbol);
            methodScope.addSymbol(paramSymbol);
        }

        if(parameterName.equals(CompilerConsts.Self))
        {
                tree.error("定义 macro的参数不能有'%s' ", CompilerConsts.Self);
        }
    }
}
