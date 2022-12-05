package atem.compiler.symbols;

public enum VarSymbolKind
{
    field, //字段
    //parameter,// 函数参数
    localVar, //局部变量
    //arrayLength //数组的长度属性,
    //__this
    FuncParameter,
    MacroParameter
}