package atem.compiler.emits.jasm;

import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCMacroCall;
import atem.compiler.emits.SymbolSignatureUtil;
import atem.compiler.symbols.MacroSymbol;

import static org.objectweb.asm.Opcodes.*;

public class MacroCallCatchEmiter extends TryCatchEmiter{
    JCMacroCall tree;
    EmitContext arg;
    BodyEmit bodyEmit;

    public MacroCallCatchEmiter(JCMacroCall tree, EmitContext arg, BodyEmit bodyEmit)
    {
        exceptionSign = "atem/lang/rt/ReturnException";
        this.tree = tree;
        this.arg = arg;
        this.bodyEmit =bodyEmit;
        this.methodVisitor =arg.mv;
    }

    protected  void emitBody()
    {
        MacroSymbol macroSymbol = tree.targetMacro ;
        for (JCExpression jcExpression : tree.getArgValues()) //生成参数
            bodyEmit.emit(jcExpression,arg);
        String owner = SymbolSignatureUtil.getParamsSignature(macroSymbol.ownerType, false);
        String name = macroSymbol.name;
        String returnSign = SymbolSignatureUtil.getParamsSignature(macroSymbol, true);
        boolean isInterface = macroSymbol.ownerType.getTypeSymbol().isInterface;
        int invokeOp = INVOKESTATIC;
        arg.mv.visitMethodInsn(invokeOp, owner, name, returnSign, isInterface);
    }

    protected void emitHandler()
    {
        var retVarSymbol = tree.belongsInfo.defedCallableAST.getCallableCinfo().retVarSymbol;
        bodyEmit. varEmiter.storeVar(arg.mv, retVarSymbol, tree, () -> {
            methodVisitor.visitFieldInsn(GETFIELD, "atem/lang/rt/ReturnException", "result", "Ljava/lang/Object;");
        });
        var methodEndLabel = tree.belongsInfo.defedCallableAST.getScope().getEndLabel();
        arg.mv.visitJumpInsn(GOTO, methodEndLabel);
    }
}
