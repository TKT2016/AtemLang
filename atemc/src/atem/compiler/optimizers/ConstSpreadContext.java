package atem.compiler.optimizers;
import atem.compiler.symbols.BVarSymbol;
import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCLiteral;

import java.util.HashMap;
/** 常量传播上下文参数 */
public class ConstSpreadContext {
    /** 变量符号的常量值表 */
    private HashMap<BVarSymbol, Object> valueMap = new HashMap<>();

    /** 清空常量值表 */
    public void clear()
    {
        valueMap.clear();
    }

    /* 设置变量符号的常量值 */
    public void setConstValue(BVarSymbol varSymbol, Object constValue)
    {
        valueMap.put(varSymbol,constValue);
    }

    /* 获取表达式的常量值 */
    public Object getConstValue(JCExpression tree)
    {
        if(tree instanceof JCLiteral)
            return ((JCLiteral) tree).value;
        else if(OptimizerUtil.isVar(tree))
        {
            BVarSymbol varSymbol = OptimizerUtil.getVarSymbol(tree);
            /* 根据变量符号从常量值表取值 */
            return valueMap.get(varSymbol);
        }
        else
            return null;
    }
}
