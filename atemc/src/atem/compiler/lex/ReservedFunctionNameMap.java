package atem.compiler.lex;
import java.util.HashMap;

/** TokenKind键值对应表 */
public class ReservedFunctionNameMap {
    /* 名称-类型表 */
    private static final HashMap<String, ReservedFunctionName> nameKindMap;
    /* 类型-名称表 */
    private static final HashMap<ReservedFunctionName, String> kindNameMap;

    static {
        nameKindMap = new HashMap<>();
        kindNameMap = new HashMap<>();
        /* 把TokenKind所有枚举值放入表中 */
        for (ReservedFunctionName t : ReservedFunctionName.values())
        {
            if (t.name != null)
                enterKeyword(t.name, t);
        }
    }

    private static void enterKeyword(String s, ReservedFunctionName tk) {
        if (!nameKindMap.containsKey(s))
            nameKindMap.put(s, tk);
        if (!kindNameMap.containsKey(tk))
            kindNameMap.put(tk, s);
    }

    /** 根据名称查类型 */
    public static ReservedFunctionName lookupKind(String name) {
        if (nameKindMap.containsKey(name))
            return nameKindMap.get(name);
       return null;
    }

    /** 根据类型查名称 */
    public static String lookupName(ReservedFunctionName tokenKind) {
        if (kindNameMap.containsKey(tokenKind))
            return kindNameMap.get(tokenKind);
        return null;
    }
}
