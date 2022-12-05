package atem.compiler.emits.jasm;

import atem.compiler.symbols.Symbol;
import atem.compiler.symbols.SymbolUtil;
import atem.compiler.ast.JCExpression;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/** 字符串联结指令生成 */
public class StringConcatEmit {
    static final String StringBuilderOwner = "java/lang/StringBuilder";
    EmitContext arg;
    Emiter fileEmit;
    public StringConcatEmit(EmitContext arg, Emiter fileEmit)
    {
        this.arg=arg;
        this.fileEmit=fileEmit;
    }

    /* 首先生成 创建StringBuilder 实例指令*/
    public void start()
    {
        MethodVisitor mv =arg.mv;
        mv.visitTypeInsn(NEW, StringBuilderOwner);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, StringBuilderOwner, "<init>", "()V", false);
    }

    /* 生成调用append方法指令 */
    public void append(JCExpression expression)
    {
        fileEmit.emit(expression,arg);
        Symbol symbol = expression.symbol.getTypeSymbol();
        String appendSignature = getAppendSignature(symbol);
        arg.mv.visitMethodInsn(INVOKEVIRTUAL, StringBuilderOwner, "append", appendSignature, false);
    }

    /* 根据符号数据类型选择不同参数类型的append的签名 */
    private String getAppendSignature(Symbol symbol )
    {
        if(SymbolUtil.isString(symbol))
          return  "(Ljava/lang/String;)Ljava/lang/StringBuilder;";
        else if(SymbolUtil.isBoolean(symbol))
            return  "(Z)Ljava/lang/StringBuilder;";
        else if(SymbolUtil.isInt(symbol))
            return "(I)Ljava/lang/StringBuilder;";
        else
            return "(Ljava/lang/Object;)Ljava/lang/StringBuilder;";
    }

    /* 生成调用toString方法,得到String结果 */
    public void end()
    {
        arg.mv.visitMethodInsn(INVOKEVIRTUAL, StringBuilderOwner, "toString", "()Ljava/lang/String;", false);
    }
}
