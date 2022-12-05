package atem.compiler.symbols;
import java.util.ArrayList;
import java.util.HashMap;
/** 数组类型符号 */
public class BArrayTypeSymbol extends BTypeSymbol {
    /** 数组内的元素类型 */
    public final BTypeSymbol elementType;

    private BArrayTypeSymbol(BTypeSymbol elementType)
    {
        super(elementType.name+"[]",  false,true);
        this.elementType = elementType;
    }
   /** 缓存 */
    private static HashMap<BTypeSymbol, BArrayTypeSymbol> cache = new HashMap<>();

    /** 获取元素类型对应的数组类型符号 */
    public static BArrayTypeSymbol getSymbol(BTypeSymbol elemTypeSymbol)
    {
        if(cache.containsKey(elemTypeSymbol))
            return cache.get(elemTypeSymbol);
        BArrayTypeSymbol javaClassSymbol = new BArrayTypeSymbol(elemTypeSymbol);
        cache.put(elemTypeSymbol,javaClassSymbol);
        return javaClassSymbol;
    }

    /** 查找字段(数组只有length字段)*/
    @Override
    public  BVarSymbol findField(String name)
    {
      //  if(name.equals(RArrayLengthVarSymbol.ArrayLengthName))
       //     return RArrayLengthVarSymbol.getSymbol(this);
        return null;
    }

    /** 查找成员(数组只有length字段) */
    @Override
    public ArrayList<Symbol> findMembers(String name ) {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        BVarSymbol symbol = this.findField(name);
        if (symbol != null)
            arrayList.add(symbol);
        return arrayList;
    }

    public static final BArrayTypeSymbol ObjectArraySymbol;

    static
    {
        ObjectArraySymbol = BArrayTypeSymbol.getSymbol(RClassSymbolManager.ObjectSymbol);
    }
}
