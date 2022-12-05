package atem.compiler.emits.pre;

import atem.compiler.ast.JCStatement;
import atem.compiler.symbols.FileSymbol;
import atem.compiler.utils.SimpleLog;

import java.net.URLClassLoader;
import java.util.ArrayList;

public class ExpandContext {

    ArrayList<JCStatement> statements;

    int varIndex=0;
    public ExpandContext()
    {

    }

    public String createVarId()
    {
        varIndex++;
        return "TempVar_"+varIndex;
    }

    public ExpandContext clone()
    {
        ExpandContext context2 = new ExpandContext();
        context2.varIndex = this.varIndex;
        context2.statements = new ArrayList<>();
        return context2;
    }
}
