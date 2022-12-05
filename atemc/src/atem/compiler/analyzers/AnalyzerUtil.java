package atem.compiler.analyzers;

import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.*;
import atem.compiler.symbols.RClassSymbol;

import atem.compiler.symbols.SymbolUtil;
import atem.compiler.ast.*;
import atem.compiler.utils.CompileError;

public abstract class AnalyzerUtil {

    static
    {
       // initTokenKindClazzMap();
    }

    /** 获取点运算符树的全名称或者标识符表达式的名称 */
    public static String fullName(JCTree tree) {
        if (tree instanceof JCIdent)
            return ((JCIdent) tree).getName();
        else if (tree instanceof JCFieldAccess) {
            String sname = fullName(((JCFieldAccess) tree).selected);
            return sname == null ? null :  sname + "."+name(tree) ;
        }
        return null;
    }

    public static String name(JCTree tree) {
        if(tree instanceof JCIdent)
            return ((JCIdent) tree).getName();
        else if(tree instanceof JCFieldAccess)
            return ((JCFieldAccess) tree).nameToken.identName;
        return null;
    }

    public static  boolean isInt( JCExpression expression)
    {
        BTypeSymbol typeSymbol = expression.symbol.getTypeSymbol();
        return  SymbolUtil.isInt(typeSymbol);
    }

    public static TokenKind getLiteralKind(JCLiteral tree)
    {
        Object value= tree.value;
        if(tree.isNullLiteral)
            return TokenKind.NULL;
        else if(value instanceof String)
            return TokenKind.STRINGLITERAL;
        else if(value instanceof Integer)
            return  TokenKind.INTLITERAL;
        else if(value instanceof Float)
            return  TokenKind.FLOATLITERAL;
        else if(value instanceof Boolean) {
            Boolean b = (Boolean) value;
            if (b)
                return TokenKind.TRUE;
            else
                return TokenKind.FALSE;
        }
        else
            throw new CompileError();
    }
    /*
    private static Map<TokenKind,Class<?>> literalTokenClazzMap;
    private static void initTokenKindClazzMap()
    {
        literalTokenClazzMap = new HashMap<>();
        literalTokenClazzMap.put(TokenKind.TRUE, boolean.class);
        literalTokenClazzMap.put(TokenKind.FALSE, boolean.class);
        literalTokenClazzMap.put(TokenKind.BOOLEAN, boolean.class);
        literalTokenClazzMap.put(TokenKind.INTLITERAL, int.class);
        literalTokenClazzMap.put(TokenKind.STRINGLITERAL, String.class);
    }
*/
   /* public static Class<?> getLiteralClazz(TokenKind literalKind)
    {
        if(literalTokenClazzMap.containsKey(literalKind))
            return literalTokenClazzMap.get(literalKind);
        else
            throw new CompileError();
    }*/

  /*  public static Class<?> getLiteralClazz(TokenKind literalKind)
    {
        switch (literalKind) {
            case TRUE:
                return boolean.class;
            case FALSE:
                return boolean.class;
            case BOOLEAN:
                return boolean.class;
            case INTLITERAL:
                return int.class;
            case STRINGLITERAL:
                return String.class;
            default:
                throw new CompileError();
        }
    }*/

    /** 获取对应的Class(语义分析阶段使用) */
  /*  public static Class<?> getPrimitiveClazz(TokenKind tokenKind)
    {
        switch (tokenKind) {
            case VOID:
                return void.class;
            case BOOLEAN:
                return boolean.class;
            case INT:
                return int.class;
            default:
                throw new CompileError();
        }
    }*/

    public static RClassSymbol getPrimitiveSymbol(TokenKind tokenKind)
    {
        switch (tokenKind) {
           // case VOID:
           //     return RClassSymbolManager.voidPrimitiveSymbol;
            //case BOOLEAN:
            //    return RClassSymbolManager.booleanPrimitiveSymbol;
            //case INT:
           //     return RClassSymbolManager.intPrimitiveSymbol;
            case NULL:
                return  RClassSymbolManager.ObjectSymbol;
            case TRUE:
                return  RClassSymbolManager.booleanPrimitiveSymbol;
            case FALSE:
                return  RClassSymbolManager.booleanPrimitiveSymbol;
            case INTLITERAL:
                return RClassSymbolManager.intPrimitiveSymbol;
            case FLOATLITERAL:
                return RClassSymbolManager.floatSymbol;
            case STRINGLITERAL:
                return RClassSymbolManager.StringSymbol;
            default:
                throw new CompileError();
        }
    }

    public static void checkBoolean(JCTree tree)
    {
        if(tree== null) return;
        if(! isBoolean( tree))
            tree.error("不兼容的类型,无法转换为boolean");
    }

    public static  boolean isBoolean( JCTree tree)
    {
        if(tree instanceof JCExpression)
        {
            JCExpression jcExpression= (JCExpression)tree;
            BTypeSymbol typeSymbol =jcExpression.symbol.getTypeSymbol();
            return  SymbolUtil.isBoolean(typeSymbol);
        }
        return false;
    }

    public static  boolean isString( JCTree tree)
    {
        if(tree instanceof JCExpression)
        {
            JCExpression jcExpression= (JCExpression)tree;
            BTypeSymbol typeSymbol =jcExpression.symbol.getTypeSymbol();
            return  SymbolUtil.isString(typeSymbol);
        }
        return false;
    }

/*
    public static void attrNumberBinary(JCBinary tree)
    {
        AnalyzerUtil. checkNumber(tree.left);
        AnalyzerUtil.checkNumber(tree.right);
        tree.symbol= RClassSymbolManager.intPrimitiveSymbol;
    }
*/

    public static boolean checkNumber(JCTree tree)
    {
        boolean b =false;
        if(tree instanceof JCExpression)
        {
            b = SymbolUtil.isInt(((JCExpression)tree).symbol.getTypeSymbol());
        }
        if(!b)
            tree.error("数据类型应该是数字");
        return b;
    }
/*
    public static Symbol findMemberSymbol(String name, BTypeSymbol typeSymbol)
    {
        ArrayList<Symbol> symbols = typeSymbol.findMembers(name);
        if(symbols==null || symbols.size()==0)
        {
            return new BErroneousSymbol();
        }
        else if(symbols.size()==1)
        {
            return symbols.get(0);
        }
        else
        {
            BManySymbol moreSym = new BManySymbol(name,symbols);
            return moreSym;
        }
    }
*/
    public static boolean checkAssignable( JCExpression left,JCExpression right)
    {
        BTypeSymbol leftType = left.symbol.getTypeSymbol();
        BTypeSymbol rightType = right.symbol.getTypeSymbol();
        boolean assignable = checkAssignable(leftType,rightType);
        if(!assignable)
        {
            right.error("类型无法赋值");
        }
        return assignable;
    }

    public static boolean checkAssignable(BTypeSymbol leftType, BTypeSymbol rightType)
    {
        int v = SymbolUtil.matchAssignableSymbol(leftType,rightType);
        return v!=-1;
       /*if(SymbolUtil.isInt(leftType))
        {
            if( SymbolUtil.isInt(rightType))
                return true;
            return false;
        }
        else if(!leftType.isAssignableFromRight(rightType))
        {
            return false;
        }
        return true;*/
    }

    public static boolean writable(JCExpression expression)
    {
        if(expression instanceof JCIdent)
        {
            JCIdent jcIdent = (JCIdent) expression;
            return ((BVarSymbol)jcIdent.symbol).writable;
        }
        //else if(expression instanceof JCArrayAccess)
        //{
        //    return true;
        //}
        else if(expression instanceof JCFieldAccess)
        {
            JCFieldAccess jcFieldAccess = (JCFieldAccess) expression;
            return ((BVarSymbol)jcFieldAccess.symbol).writable;
        }
        return false;
    }
/*
    public static void scanAssign(BodyAnalyzer analyzer, JCExpression left, JCExpression right, BodyContext arg)
    {
        SearchKinds searchKinds = new SearchKinds();//只搜索变量
       // findKinds.isSearchVar = true;//只搜索变量
        left.scan(analyzer,arg.copy(searchKinds));
        right.scan(analyzer,arg.copy(searchKinds));
        AnalyzerUtil.checkAssignable(left,right);
        //right.requireConvertTo = (left.getTypeSymbol());
    }
*/
    /*
    public static boolean containsVar(SymbolScope scope, String varName, SearchKinds searchKinds)
    {
        SymbolScope temp = scope;
        while (temp!=null)
        {
            if(temp.containsVar(varName, searchKinds))
                return true;
            temp = temp.parent;
        }
        return false;
    }
*/

}
