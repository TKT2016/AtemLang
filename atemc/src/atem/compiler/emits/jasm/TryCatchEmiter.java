package atem.compiler.emits.jasm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

abstract class TryCatchEmiter {
    protected String exceptionSign;
    protected MethodVisitor methodVisitor;
    protected abstract void emitBody();
    protected abstract void emitHandler();

     public void emit()
     {
         /*Label label0 = new Label();
         Label label1 = new Label();
         Label label2 = new Label();
         methodVisitor.visitTryCatchBlock(label0, label1, label2, exceptionSign);// methodVisitor.visitTryCatchBlock(label0, label1, label2, "atem/interpreter/rt/ReturnException");
         methodVisitor.visitLabel(label0);
         emitBody();*/

         Label label0 = new Label();
         Label label1 = new Label();
         Label label2 = new Label();
         //TRY
         methodVisitor.visitTryCatchBlock(label0, label1, label2, exceptionSign);
         methodVisitor.visitLabel(label0);

         emitBody();
        //CATCH
         //methodVisitor.visitLineNumber(24, label0);
         //methodVisitor.visitMethodInsn(INVOKESTATIC, "asmtest/TestReturnException", "f3", "()V", false);
         methodVisitor.visitLabel(label1);
        // methodVisitor.visitLineNumber(28, label1);
         Label label3 = new Label();
         methodVisitor.visitJumpInsn(GOTO, label3);
         methodVisitor.visitLabel(label2);
         methodVisitor.visitLineNumber(25, label2);
         //methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"atem/interpreter/rt/ReturnException"});
         //methodVisitor.visitVarInsn(ASTORE, 0);
         //HANDLER
         emitHandler();
       /*  Label label4 = new Label();
         methodVisitor.visitLabel(label4);
        // methodVisitor.visitLineNumber(27, label4);
         methodVisitor.visitVarInsn(ALOAD, 0);
         methodVisitor.visitFieldInsn(GETFIELD, "atem/interpreter/rt/ReturnException", "result", "Ljava/lang/Object;");
         methodVisitor.visitInsn(ARETURN);*/
         methodVisitor.visitLabel(label3);
         //methodVisitor.visitLineNumber(29, label3);
        // methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        // methodVisitor.visitInsn(ACONST_NULL);
       //  methodVisitor.visitInsn(ARETURN);
     }
}
