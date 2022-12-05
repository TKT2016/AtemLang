package atem.compiler.emits.jasm;

import atem.compiler.ast.JCTree;

public interface Emiter {
    void emit(JCTree tree, EmitContext arg);
}
