package atem.compiler.ast.callables;

import atem.compiler.analyzers.MacroDescData;
import atem.compiler.ast.*;
import atem.compiler.ast.callables.proc.ProcItem;
import atem.compiler.ast.callables.proc.ProcItemParameter;
import atem.compiler.ast.callables.proc.ProcItemText;
import atem.compiler.symbols.*;
import atem.compiler.tools.ListUtil;

import java.util.ArrayList;

public class JCMacroDecl extends JCTree implements SourceFileSection, ICallableAST {

    public ProcItem[] items;

    /** 函数体语句块 */
    public JCBlock body;

    public DMacroSymbol macroSymbol;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitMacroDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateMacro(this, arg);
    }
/*

    public String getFirstItemText()
    {
        ProcItem firstItem =(ProcItem)items[0];
        if(firstItem instanceof ProcItemText) {
            ProcItemText ident = (ProcItemText) items[0];
            return ident.getText();
        }
        return null;
    }
*/
    public int size()
    {
        return items.length;
    }

    public int getParameterCount()
    {
        int count =0;
        for(ProcItem item: items)
        {
            if(item instanceof ProcItemParameter)
                count++;
        }
        return  count;
    }

    ProcItemParameter[] parameters;
    public  ProcItemParameter[] getParameters()
    {
        if(parameters==null)
        {
            ArrayList<ProcItemParameter> list = new ArrayList<>();
            int size = this.items.length;
            for( int i=0;i<size;i++ )
            {
                if(items[i] instanceof ProcItemParameter)
                {
                    list.add((ProcItemParameter)this.items[i]);
                }
            }
            parameters = ListUtil.toProcItemParameter(list);
        }
        return parameters;
    }

    public String createMethodName()
    {
        var sb = new StringBuilder();
        int size = items.length;
        for( int i=0;i<size;i++ )
        {
            var item = items[i];
            if(item instanceof ProcItemParameter)
            {
               sb.append("___");
            }
            else
            {
                ProcItemText procItemText =(ProcItemText) item;
                sb.append(procItemText.getText());
            }
        }
        return sb.toString();
    }

    public String createMacroAnnotationValue()
    {
        var sb = new StringBuilder();
        int size = items.length;
        for( int i=0;i<size;i++ )
        {
            sb.append(" ");
            var item = items[i];
            if(item instanceof ProcItemParameter)
            {
                sb.append("$");
            }
            else
            {
                ProcItemText procItemText =(ProcItemText) item;
                sb.append(procItemText.getText());
            }
        }
        return sb.toString().trim();
    }

    public String createMacroAnnotationDetail()
    {
        var sb = new StringBuilder();
        int size = items.length;
        for( int i=0;i<size;i++ )
        {
            sb.append(" ");
            var item = items[i];
            if(item instanceof ProcItemParameter)
            {
                ProcItemParameter procItemParameter=(ProcItemParameter)item;
                sb.append("$"+procItemParameter.name);
            }
            else
            {
                ProcItemText procItemText =(ProcItemText) item;
                sb.append(procItemText.getText());
            }
        }
        return sb.toString().trim();
    }

    private MacroDescData macroDescData;
    public MacroDescData getMacroData()
    {
        if(macroDescData!=null) return macroDescData;
        macroDescData = new MacroDescData(this.createMacroAnnotationValue());
        return macroDescData;
    }

    public ProcItemParameter getParameterItem(int i)
    {
        return  getParameters()[i];
    }

    public CallableCinfo callableCinfo = new CallableCinfo();

    public CallableCinfo getCallableCinfo()
    {
        return callableCinfo;
    }

    public SymbolScope getScope()
    {
        return belongsInfo.getScope();
    }

    public BTypeSymbol getRetType()
    {
        return macroSymbol.returnType;
    }
}
