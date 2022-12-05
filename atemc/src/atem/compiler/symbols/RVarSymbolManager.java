package atem.compiler.symbols;
import java.lang.reflect.*;
import java.util.HashMap;

public class RVarSymbolManager {
    /* 变量符号缓存,确保每个字段或参数的符号是唯一的*/
    private static HashMap<Object, RVarSymbol> cache = new HashMap<>();

    /* 根据字段创建变量符号*/
    public static RVarSymbol getSymbol(Field field, BTypeSymbol owner) {
        if (cache.containsKey(field))
            return cache.get(field);
        RClassSymbol varType = RClassSymbolManager.getSymbol (field.getType());
        String varName = field.getName();
        RVarSymbol varSymbol = new RVarSymbol(varName  , VarSymbolKind.field, varType,field);
        varSymbol.ownerType = owner;
        /** 字段符号的相关属性要从它的访问标记上读取 */
        int fieldModifier = field.getModifiers();
        varSymbol.isStatic = Modifier.isStatic(fieldModifier);
        varSymbol.isPublic = Modifier.isPublic(fieldModifier);
        varSymbol.writable = !Modifier.isFinal(fieldModifier);
        cache.put(field, varSymbol);
        return varSymbol;
    }

    /* 根据函数参数创建变量符号*/
    public static RVarSymbol getSymbol(Parameter parameter) {
        if (cache.containsKey(parameter))
            return cache.get(parameter);
        RClassSymbol varType = RClassSymbolManager.getSymbol(parameter.getType());
        String varName = parameter.getName();
        RVarSymbol varSymbol = new RVarSymbol(varName, VarSymbolKind.FuncParameter, varType,null);
        /* 参数一般是非静态可写的 */
        varSymbol.isStatic = false;
        varSymbol.isPublic = false;
        varSymbol.writable = true;
        cache.put(parameter, varSymbol);
        return varSymbol;
    }
}
