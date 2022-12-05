package atem.compiler.symbols;
import atem.compiler.CompileContext;
import org.objectweb.asm.Label;
import atem.compiler.tools.KeyValuesMap;
import java.util.ArrayList;

/** 作用域(包括代码块) */
public class SymbolScope
{
    public final SymbolScope parent;
    public ArrayList<SymbolScope> children = new ArrayList<>();
    public KeyValuesMap<String, Symbol> defSymbols = new KeyValuesMap<>();

    public SymbolScope(SymbolScope parent ,String scopeId)
    {
        this.parent = parent;
        _scopeId=scopeId;
    }

    public boolean isFileTopScope()
    {
        return this.parent==null;
    }

    public final String _scopeId;

    ArrayList<String> importedPackages ;
    public void addPackage(String packageName)
    {
        if(importedPackages==null)
            importedPackages = new ArrayList<>();
        importedPackages.add(packageName);
    }

    ArrayList<RClassSymbol> requireTypes = new ArrayList<>();

    public void addRequire(RClassSymbol classSymbol)
    {
        if(requireTypes==null)
            requireTypes = new ArrayList<>();
        requireTypes.add(classSymbol);
    }

    public ArrayList<BTypeSymbol> searchTypes(String typeName, CompileContext compileContext)
    {
        ArrayList<BTypeSymbol> list = new ArrayList<>();
        SymbolScope top= getTop();
        if(top.importedPackages==null|| top.importedPackages.size()==0)
            return list;
        for (String packag : top.importedPackages)
        {
            String fullName = packag+"."+typeName;
            RClassSymbol classSymbol = RClassSymbolManager.forName(fullName,compileContext);
            if(classSymbol!=null)
                list.add(classSymbol);
        }
        for (RClassSymbol reqClass: top.requireTypes)
        {
            if(reqClass.name.equals(typeName))
                list.add(reqClass);
        }
        return list;
    }

    public ArrayList<Symbol> searchRequireVar(String varName )
    {
        ArrayList<Symbol> list = new ArrayList<>();
        if(requireTypes!=null) {
            for (RClassSymbol reqClass : requireTypes) {
                ArrayList<Symbol> members = reqClass.findMembers(varName);
                for (Symbol member : members) {
                    if (SymbolUtil.isStatic(member)) {
                        list.add(member);
                    }
                }
            }
        }
        return list;
    }

    public ArrayList<MacroSymbol> searchRequireMacro(String macroAnnotationValue )
    {
        ArrayList<MacroSymbol> list = new ArrayList<>();
        for (RClassSymbol reqClass: requireTypes)
        {
            ArrayList<MacroSymbol> members = reqClass.findRequireMacro(macroAnnotationValue);
            if(members.size()>0)
            {
                list.addAll(members);
            }
        }
        return list;
    }

    public SymbolScope getTop()
    {
        SymbolScope temp = this;
        while (temp.parent!=null)
        {
            temp=temp.parent;
        }
        return temp;
    }

    public void addSymbol(Symbol symbol)
    {
        //if(defSymbols.contains(symbol.name))
        //    return;
        defSymbols.put(symbol.name,symbol);
    }

    public SymbolScope createChild( String scopeId)
    {
        SymbolScope blockSymbolFrame = new SymbolScope(this,scopeId);
        this.children.add(blockSymbolFrame);
        return blockSymbolFrame;
    }

    private Label startLabel; //开始标签
    private Label endLabel; // 结束标签

    public Label getStartLabel()
    {
        if(startLabel==null)
            startLabel = new Label();
        return startLabel;
    }

    public Label getEndLabel()
    {
        if(endLabel==null)
            endLabel = new Label();
        return endLabel;
    }
}
