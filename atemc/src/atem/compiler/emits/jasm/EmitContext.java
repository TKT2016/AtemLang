package atem.compiler.emits.jasm;
import org.objectweb.asm.*;
/**源文件生成上下文参数 */
class EmitContext
{
    /** ASM字节码当前类型写入器 */
    public ClassWriter classWriter;

    //public ClassWriter classWriterInner;

    /** 当前函数ASM字节码写入器 */
    public MethodVisitor mv;
    /** 当前方法结束标签 */
    //public Label methodEndLabel;
    /** 行号生成器 */
    public LineNumberEmit lineNumberEmit;

   // public DVarSymbol retVarSymbol;

   // public DVarSymbol lambdaVarArraySymbol;

    //public FileSymbol sourceFileSymbol;

    public EmitContext clone()
    {
        EmitContext emitContextNew = new EmitContext();
        emitContextNew.classWriter = this.classWriter;
      //  emitContextNew.classWriterInner = this.classWriterInner;
        emitContextNew.mv = this.mv;
        //emitContextNew.methodEndLabel = this.methodEndLabel;
        emitContextNew.lineNumberEmit = this.lineNumberEmit;
       // emitContextNew.retVarSymbol = this.retVarSymbol;
      //  emitContextNew.lambdaVarArraySymbol = this.lambdaVarArraySymbol;
        //emitContextNew.sourceFileSymbol = this.sourceFileSymbol;
        return emitContextNew;
    }

}