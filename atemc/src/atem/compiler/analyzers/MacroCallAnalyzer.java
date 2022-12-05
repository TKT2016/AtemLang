package atem.compiler.analyzers;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.symbols.MacroSymbol;
import atem.compiler.symbols.RClassSymbolManager;
import atem.compiler.symbols.RMethodSymbol;
import atem.compiler.symbols.Symbol;
import atem.compiler.utils.Debuger;

import java.util.ArrayList;

public class MacroCallAnalyzer {
    BodyAnalyzeTranslator translator;

    public MacroCallAnalyzer(BodyAnalyzeTranslator translator)
    {
        this.translator =translator;
    }

    public JCExpression visitProcCall(JCMacroCall macroCall, BodyContext arg) {
        macroCall.symbol = RClassSymbolManager.voidPrimitiveSymbol;

        if (macroCall.firstIsIdent()) {
            ArrayList<MacroSymbol> macroDecls = findProcAST(macroCall, arg);
            if (macroDecls.size()==1) {
                var macroDecl = macroDecls.get(0);
                macroCall.targetMacro = macroDecl;
                MacroDescData macroDescData = macroCall.getTargetMacroData();
                parseNormalProCall(macroDescData, macroCall,arg);
                return macroCall;
            }
            else if (macroDecls.size()>0)
            {
                //var macroDecl = macroDecls.get(0);
                //macroCall.targetMacro = macroDecl;
                macroCall.error("不确定的macro调用");
                return macroCall;
            }
        }

        JCExpression newExp = anlyzeAsMethodInvocation(macroCall,arg);
        return newExp;
    }

    private  JCExpression anlyzeAsMethodInvocation(JCMacroCall macroCall, BodyContext arg)
    {
        var first =  macroCall.getItems()[0];
        if(macroCall.getItems().length==1)
            return first;

        first .isInvocationMethod=true;

        JCMethodInvocation methodInvocation = new JCMethodInvocation();
        methodInvocation.posToken =first.posToken;
        methodInvocation.log = first.log;
        methodInvocation.line = first.line;
        methodInvocation.belongsInfo = macroCall.belongsInfo;
        methodInvocation.methodExpr =  first;
        /*if(!(methodInvocation.methodExpr instanceof JCIdent
             || methodInvocation.methodExpr instanceof JCFieldAccess
        )
        )*/
        if(methodInvocation.methodExpr instanceof JCLiteral
        )
        {
            first.error("函数调用的方法错误");
        }
        JCExpression[] args = new JCExpression[macroCall.getItems().length-1];
        for(int i = 1; i< macroCall.getItems().length; i++)
        {
            args[i-1]= translator.translate( macroCall.getItems()[i],arg);
            args[i-1].belongsInfo =  macroCall.getItems()[i].belongsInfo;
        }
        methodInvocation.setArgs( args);
        methodInvocation.symbol = RClassSymbolManager.ObjectSymbol;

      //  if(methodInvocation.methodExpr.toString().endsWith(".setBounds"))
      //      Debuger.outln("67 anlyzeAsMethodInvocation:"+methodInvocation);
        return (JCExpression) translator.translateMethodInvocation(methodInvocation,arg);
    }

   private void parseNormalProCall(MacroDescData macroDescData, JCMacroCall jcMacroCall, BodyContext arg)
    {
        int length = macroDescData.size() ;
        var callItems = jcMacroCall.getItems();
        for (int i = 0; i < length; i++) {
            var macroValueItem =macroDescData.macroValueArray[i];
            if (macroValueItem.equals("$") ) {
                var jcExpression = callItems[i];
               // JCExpression jcExpression = jcMacroCall.getItems()[i];
                jcExpression.setIsMacroCallArg(true);
                var np =   translator.translate(jcExpression,arg);
                if(TreeeUtil.isEmptyParens(np))
                {
                    np.error("宏调用的括号表达式内不能为空");
                }
                np.setIsMacroCallArg(true);
                jcMacroCall.getItems()[i] = np;
            }
        }
    }

    private ArrayList<MacroSymbol> findProcAST(JCMacroCall macroCall, BodyContext arg)
    {
        ArrayList<MacroSymbol> list = new ArrayList<>();
        for(JCMacroDecl macroDecl :macroCall.belongsInfo.fileTree.JCMacroDecls)
        {
            if(isEq(macroDecl,macroCall))
            {
                list.add(macroDecl.macroSymbol);
            }
        }
        String macroAnnotationValue = createMacroAnnotationValue(macroCall);
        ArrayList<MacroSymbol> methodSymbols = macroCall.belongsInfo.fileTree.belongsInfo.scope.searchRequireMacro(macroAnnotationValue);
        list.addAll(methodSymbols);
        return list;
    }

    private boolean isEq(JCMacroDecl macroDecl, JCMacroCall macroCall)
    {
        String m1 =macroDecl.createMacroAnnotationValue();
        String m2 = createMacroAnnotationValue(macroCall);
        return m1.equals(m2);
        /*var macroDefItems = macroDecl.items;
        if(macroCall.getItems().length!=macroDefItems.length)
            return false;
        for(int i=0;i<macroDefItems.length;i++)
        {
            var defitem = macroDefItems[i];
            var callitem = macroCall.getItems()[i];
            if(!isEq(defitem,callitem))
                return false;
        }
        return  true;*/
    }

    public String createMacroAnnotationValue( JCMacroCall macroCall)
    {
        var sb = new StringBuilder();
        int size = macroCall.items.length;
        for( int i=0;i<size;i++ )
        {
            sb.append(" ");
            var item =macroCall. items[i];
            if(item instanceof JCIdent)
            {
                JCIdent jcIdent =(JCIdent) item;
                if(jcIdent.isDollarIdent)
                {
                    sb.append("$");
                }
                else
                {
                    String name = jcIdent.getName();
                    SearchKinds searchKinds = new SearchKinds();
                    searchKinds.isSearchType = true;
                    searchKinds.isSearchVar = true;
                    ArrayList<Symbol> symbols = SearchSymbol.findIdents(macroCall.belongsInfo.scope, name, searchKinds, translator.compileContext, macroCall.belongsInfo,jcIdent.nameToken.pos);
                    if(symbols.size()>0)
                        sb.append("$");
                    else
                        sb.append(jcIdent.getName());
                }
            }
            else
            {
                sb.append("$");
            }
        }
        return sb.toString().trim();
    }
}
