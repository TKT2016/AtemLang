package atem.compiler.optimizers;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.symbols.*;
import atem.compiler.ast.*;
/** 优化辅助类 */
final class OptimizerUtil {
    /** 获取表达式的常量值,如果没有常量值,返回null */
     public static Object getLiteralValue(JCExpression expression)
    {
        if(expression instanceof JCLiteral)
            return ((JCLiteral)expression).value;
        return null;
    }
    /* 判断表达式是否的常量值是否等于false */
    public static boolean isTrue(JCExpression expression)
    {
        Object value = getLiteralValue(expression);
        return  isTrue(value);
    }
    /* 判断表达式是否的常量值是否等于true */
    public static boolean isFalse(JCExpression expression)
    {
        Object value = getLiteralValue(expression);
        return  isFalse(value);
    }

    /** 判断参数不为null并且等于true*/
    public static boolean isTrue(Object obj)
    {
        if(obj==null) return false;
        if(obj instanceof Boolean)
            return ((Boolean)obj).booleanValue();
        return false;
    }
    /** 判断参数不为null并且等于false*/
    public static boolean isFalse(Object obj)
    {
        if(obj==null) return false;
        if(obj instanceof Boolean)
            return !((Boolean)obj).booleanValue();
        return false;
    }

    /** 判断参数不为null并且是布尔类型 */
    public static boolean isBoolean(Object obj)
    {
        if(obj==null) return false;
        return (obj instanceof Boolean);
    }

    /** 判断参数不为null并且是整数类型 */
    public static boolean isInt(Object obj)
    {
        if(obj==null) return false;
        return  (obj instanceof Integer);
    }
    /** 判断参数不为null并且等于整数0 */
    public static boolean isIntZero(Object obj)
    {
        if(!isInt(obj)) return false;
        return  ((Integer)obj).intValue()==0;
    }
    /** 判断参数不为null并且等于整数1 */
    public static boolean isIntOne(Object obj)
    {
        if(!isInt(obj)) return false;
        return  ((Integer)obj).intValue()==1;
    }
    /** 根据标识符表达式和常量值创建一个JCLiteral */
    public static JCLiteral newLiteral(JCIdent tree, Object newValue)
    {
        JCLiteral newLiteral = new JCLiteral(newValue);
        BTypeSymbol typeSymbol = tree.symbol.getTypeSymbol();
        BTypeSymbol newSymbol;
        if(SymbolUtil.isBoolean(typeSymbol))
            newSymbol = RClassSymbolManager.booleanPrimitiveSymbol;
        else if(SymbolUtil.isInt(typeSymbol))
            newSymbol = RClassSymbolManager.intPrimitiveSymbol;
        else
            newSymbol = typeSymbol;
        copyTree(newLiteral,tree,newSymbol);
        return newLiteral;
    }
    /** 根据一个语法树和一个整数值创建一个JCLiteral */
    public static JCLiteral newIntLiteral(JCTree tree, int newValue)
    {
        JCLiteral newLiteral = new JCLiteral(newValue);
        copyTree(newLiteral,tree , RClassSymbolManager.intPrimitiveSymbol);
        return newLiteral;
    }
    /** 根据一个语法树和一个布尔值创建一个JCLiteral */
    public static JCLiteral newBooleanLiteral(JCTree tree, boolean newValue)
    {
        JCLiteral newLiteral = new JCLiteral(newValue);
        copyTree(newLiteral,tree, RClassSymbolManager.booleanPrimitiveSymbol);
        return newLiteral;
    }
    /** 根据一个语法树和一个字符串值创建一个JCLiteral */
    public static JCLiteral newStringLiteral(JCTree tree, String newValue)
    {
        JCLiteral newLiteral = new JCLiteral(newValue);
        copyTree(newLiteral,tree, getSymbol(tree));
        return newLiteral;
    }

    /** 复制一个语法树的属性到另一个语法树上 */
    private static void copyTree(JCTree newTree, JCTree oldTree, Symbol symbol)
    {
        newTree.log = oldTree.log;
       // newTree.pos = oldTree.pos;
        newTree.line = oldTree.line;
        newTree.posToken = oldTree.posToken;
        setSymbol(newTree,symbol);
    }

    /** 获取语法树的符号 */
    public static Symbol getSymbol(JCTree tree)
    {
        if(tree instanceof JCExpression)
            return ((JCExpression)tree).symbol;
        if(tree instanceof JCFunction)
            return ((JCFunction)tree).methodSymbol;
        if(tree instanceof JCFileTree)
            return ((JCFileTree)tree).fileSymbol;
        return null;
    }
    /** 设置语法树的符号 */
    public static void setSymbol(JCTree tree, Symbol symbol)
    {
        if(tree instanceof JCExpression)
            ((JCExpression)tree).symbol= symbol;
        if(tree instanceof JCFunction)
            ((JCFunction)tree).methodSymbol=(DMethodSymbol) symbol;
        if(tree instanceof JCFileTree)
            ((JCFileTree)tree).fileSymbol=(FileSymbol) symbol;
    }

    /** 获取一个语法树的变量符号,不符合条件返回null */
    public static BVarSymbol getVarSymbol(JCTree tree)
    {
        Symbol symbol= getSymbol(tree);
        if(symbol!=null && symbol instanceof BVarSymbol)
            return (BVarSymbol) symbol;
        return null;
    }

    /** 判断一个语法树的符号是否是变量符号 */
    public static boolean isVar(JCTree tree)
    {
        Symbol symbol= getSymbol(tree);
        if(symbol!=null && symbol instanceof BVarSymbol)
            return true;
        return false;
    }
}
