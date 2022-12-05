package atem.compiler.lex;
/** 词标记类型*/
public enum ReservedFunctionName
{
    invoke("__invoke"),
   // findDim("__findDim")
    ;
    /* 类型对应的名称 */
    public final String name;
    ReservedFunctionName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
