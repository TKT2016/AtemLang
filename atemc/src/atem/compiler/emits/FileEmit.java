package atem.compiler.emits;

import atem.compiler.ast.JCFileTree;
import atem.compiler.emits.jasm.FileEmiter;
import atem.compiler.CompileContext;

public class FileEmit {
    public void emit(JCFileTree fileTree, CompileContext context)
    {
        new FileEmiter( ).emit(fileTree,context);
    }
}
