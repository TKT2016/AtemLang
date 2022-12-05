package atem.compiler.symbols;

import java.lang.reflect.Field;

/** 反射得到的变量符号(包括字段、方法参数) */
public class RVarSymbol extends BVarSymbol {
    public final Field field;
    RVarSymbol(String name, VarSymbolKind kind, BTypeSymbol varType,Field field) {
        super(name, kind);
        this.varType = varType;
        this.field =field;
    }
}
