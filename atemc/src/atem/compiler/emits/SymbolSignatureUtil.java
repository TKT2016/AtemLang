package atem.compiler.emits;

import atem.compiler.CompilerConsts;
import atem.compiler.emits.jasm.RTSigns;
import atem.compiler.symbols.*;
import atem.compiler.tools.SignatureUtil;

/** 签名生成 */
public abstract class SymbolSignatureUtil {

    public static String getParamsSignature(FileInnerClassSymbol symbol, boolean insertL)
    {
        StringBuilder stringBuilder = new StringBuilder();
        if(insertL)
            stringBuilder.append("L");

        String fullName = symbol.fileSymbol.getFullname();
        String str = fullName.replace(".", "/");
        stringBuilder.append(str);
        stringBuilder.append("$");
        stringBuilder.append(CompilerConsts.lambdaInnerClassName);
        if(insertL)
            stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public static String getParamsSignature(FileSymbol symbol, boolean insertL)
    {
        String lstr = insertL?"L":"";
        String fullName = symbol.getFullname();
        String str = fullName.replace(".", "/");
        String elstr = insertL?";":"";
        return lstr +str+elstr;
    }

    public static String getParamsSignature(Symbol symbol, boolean insertL)
    {
        if(symbol.equals(RClassSymbolManager.ObjectSymbol))
            return getParamsSignature( RClassSymbolManager.ObjectSymbol,  insertL);
        if(symbol instanceof FileSymbol)
            return getParamsSignature((FileSymbol) symbol,  insertL);
        else if(symbol instanceof BMethodSymbol)
            return getParamsSignature((BMethodSymbol) symbol,  insertL);
        else if(symbol instanceof RClassSymbol)
            return getParamsSignature((RClassSymbol) symbol,  insertL);
        else if(symbol instanceof BArrayTypeSymbol)
            return getParamsSignature((BArrayTypeSymbol) symbol,  insertL);
        else if(symbol instanceof BErroneousSymbol)
            return getParamsSignature( RClassSymbolManager.ObjectSymbol,  insertL);
        else if(symbol instanceof MacroSymbol)
            return getParamsSignature((MacroSymbol) symbol,  insertL);
       // else if(symbol instanceof AnySymbol)
       //     return getParamsSignature( RClassSymbolManager.ObjectSymbol,  insertL);
        else
            return getParamsSignature(symbol.getTypeSymbol(),insertL);
    }

    public  static  String getParamsSignature(BArrayTypeSymbol symbol, boolean insertL)
    {
        return "["+ getParamsSignature(symbol.elementType,insertL);// return "["+ elementType.getSignature(insertL);
    }

    public  static  String getParamsSignature(RClassSymbol symbol, boolean insertL)
    {
        return SignatureUtil.getSignature(symbol.clazz,insertL);
    }

    /**  生成函数参数列表及返回值的签名 */
    public  static  String getParamsSignature(BMethodSymbol symbol, boolean insertL)
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < symbol.getParameterCount(); i++) {
            Symbol symboli =  symbol.getParameterSymbol(i);
            BTypeSymbol typeSymboli = symboli.getTypeSymbol();
            if(typeSymboli!=null) {
                String singature = getParamsSignature(typeSymboli,(true));
                buf.append(singature);
            }
        }
        buf.append(")");
        if( symbol.isConstructor)
            buf.append("V");
        else
            buf.append( getParamsSignature( symbol.returnType,(insertL)));
        return buf.toString();
    }

    /**  生成函数参数列表及返回值的签名 */
    public  static  String getParamsSignature(MacroSymbol symbol, boolean insertL)
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < symbol.getParameterCount(); i++) {
            Symbol symboli =  symbol.getParameterSymbol(i);
            BTypeSymbol typeSymboli = symboli.getTypeSymbol();
            if(typeSymboli!=null) {
                String singature = getParamsSignature(typeSymboli,(true));
                buf.append(singature);
            }
        }
        buf.append(")");
        buf.append( getParamsSignature( symbol.returnType,(insertL)));
        return buf.toString();
    }


}
