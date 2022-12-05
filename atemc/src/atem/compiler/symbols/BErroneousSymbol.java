package atem.compiler.symbols;
import java.util.ArrayList;
/** 错误类型符号 */
public class BErroneousSymbol extends BTypeSymbol
{
    public BErroneousSymbol() {
        super( "<ErroneousSymbol>",  false,true);
    }

    @Override
    public BVarSymbol findField(String name)
    {
        return null;
    }

    @Override
    public ArrayList<Symbol> findMembers(String name)
    {
        return null;
    }
}
