package atem.compiler.ast;

import atem.compiler.analyzers.MacroDescData;

import atem.compiler.symbols.MacroSymbol;
import atem.compiler.tools.ListUtil;
import atem.lang.rt.InterpreterError;

import java.util.ArrayList;

public class JCMacroCall extends JCExpression
{
    /** 值表达式 */
    public JCExpression[] items;
    public JCMacroCall(JCExpression[] items) {
        setItems(items);
    }

    public void setItems(JCExpression[] exprs)
    {
        this.items = exprs;

        /*if(exprs.length==2 && TreeeUtil.isEmptyParens(exprs[1]))
        {
            this.items = new JCExpression[]{};
        }
        else {
            ArrayList<JCExpression> list = new ArrayList<>();
            for (var item : exprs) {
                if(TreeeUtil.isEmptyParens(item))
                {
                    item.error("宏调用的括号表达式内不能为空");
                }
                else
                    list.add(item);
               // item.setIsMacroCallArg(false);
            }
            this.items = ListUtil.toExprArray(list);
        }*/
    }

    public  JCExpression[] getItems()
    {
        return items;
    }

    public MacroSymbol targetMacro;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitMacroCall(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateMacroCall(this, arg);
    }

    public boolean firstIsIdent()
    {
        Object obj = items[0];
        return   (obj instanceof JCIdent);
    }

    public MacroDescData getTargetMacroData()
    {
        return this.targetMacro.getMacroData();
    }

    public JCExpression[] argValues;
    public JCExpression[] getArgValues()
    {
        if(targetMacro ==null)
        {
            throw new InterpreterError("内部错误,没有targetProcAST");
        }
        if(argValues==null)
        {
            ArrayList<JCExpression> list = new ArrayList<>();
            var mdata = getTargetMacroData();
            int size = mdata.size();
            for( int i=0;i<size;i++ )
            {
                if(mdata.macroValueArray[i].equals("$"))
                {
                    list.add(this.items[i]);
                }
            }
            argValues = ListUtil.toExprArray(list);
        }
        return argValues;
    }
}
