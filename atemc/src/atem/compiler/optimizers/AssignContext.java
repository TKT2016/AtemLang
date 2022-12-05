package atem.compiler.optimizers;
import atem.compiler.symbols.BVarSymbol;
import atem.compiler.ast.JCStatement;
import java.util.HashMap;
/** 赋值优化参数 */
public class AssignContext {
    /** 变量符号与最后赋值表达式表 */
    private HashMap<BVarSymbol, JCStatement> assignMap = new HashMap<>();
    /** 无效赋值语句表 */
    private HashMap<JCStatement, BVarSymbol> noneffectives = new HashMap<>();
    /** 当前语句 */
    public JCStatement statement;
    /** 清空表 */
    public void clear()
    {
        assignMap.clear();
        noneffectives.clear();
    }

    /** 设置符号赋值 */
    public void setWrite(BVarSymbol varSymbol , JCStatement tree)
    {
        if(assignMap.containsKey(varSymbol))
        {
            /* 1 取出此变量符号上一次的赋值表达式preAssign */
            JCStatement preAssign = assignMap.get(varSymbol);
            if(preAssign!=null)
                /* 把上一次赋值表达式preAssignTree作为无效赋值放到noneffectives */
                noneffectives.put(preAssign,varSymbol);
        }
        /* 保存此变量符号最后赋值表达式 */
        assignMap.put(varSymbol,tree);
    }

    /** 设置读取某个变量符号 */
    public void setRead(BVarSymbol varSymbol)
    {
        assignMap.remove(varSymbol);
    }

    /** 查询语法树是否是有效赋语句 */
    public boolean isEffective(JCStatement tree)
    {
        return !noneffectives.containsKey(tree);
    }
}
