package atem.compiler.tools;

import atem.compiler.emits.SymbolSignatureUtil;
import atem.compiler.emits.jasm.RTMembers;
import atem.compiler.emits.jasm.RTSigns;
import atem.compiler.symbols.BTypeSymbol;
import atem.compiler.symbols.BVarSymbol;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

public abstract class JASMEmit {

    public static void loadNull(MethodVisitor mv)
    {
        mv.visitInsn(Opcodes.ACONST_NULL);
    }

    public static void loadStringLiteral(MethodVisitor mv,String str)
    {
        mv.visitLdcInsn(str);
    }

    public static void dup(MethodVisitor mv)
    {
        mv.visitInsn(DUP);
    }

    public static void aastore(MethodVisitor mv)
    {
        mv.visitInsn(AASTORE);
    }

    public static void loadBooleanLiteral(MethodVisitor methodVisitor,boolean b)
    {
        if(b)
            methodVisitor.visitInsn(ICONST_1);
        else
            methodVisitor.visitInsn(ICONST_0);
    }

    public static void getField(MethodVisitor methodVisitor, Field field)
    {
       // mv.visitFieldInsn(GETSTATIC, "atem/interpreter/rt/VoidReturn", "ret", "Latem/interpreter/rt/VoidReturn;");
        String owner = SignatureUtil.getSignature(field.getDeclaringClass(),false);
        String name = field.getName();
        String desc = SignatureUtil.getSignature( field.getType(),true);
        int op ;
        if (Modifier.isStatic(field.getModifiers() )  )
            op = GETSTATIC;
        else
            op = GETFIELD;
        methodVisitor.visitFieldInsn(op, owner, name, desc);
    }

    public static void storeField(MethodVisitor mv  , Field field )
    {
        int op =    (Modifier.isStatic(field.getModifiers() )  ) ? PUTSTATIC : PUTFIELD;
        String ownerSign =   SignatureUtil.getSignature(field.getDeclaringClass(),false);
        String descSign =  SignatureUtil.getSignature( field.getType(),true);
        String name = field.getName();
        mv.visitFieldInsn(op,ownerSign, name,descSign);
    }

    public static void invoke(MethodVisitor methodVisitor, Method method)
    {
        /* 生成函数所属类型签名 */
        String owner = SignatureUtil.getSignature(method.getDeclaringClass(),false);
        /* 获取函数名称 */
        String name = method.getName();
        /* 生成函数的返回类型的签名 */
        String methodDesc = SignatureUtil.getSignature( method);
        /* 判断调用指令 */
        boolean isInterface = method.getDeclaringClass().isInterface() ;
        int invokeOp ;
        if (Modifier.isStatic(method.getModifiers() )  )
            invokeOp = INVOKESTATIC;
        else if(isInterface)
            invokeOp = INVOKEINTERFACE;
        else if (Modifier.isFinal(method.getModifiers() )  )
            invokeOp = INVOKESPECIAL;
        else
            invokeOp = INVOKEVIRTUAL;
        /* 生成函数调用 */
        methodVisitor.visitMethodInsn(invokeOp, owner, name, methodDesc, isInterface);
    }

    public static void invoke(MethodVisitor methodVisitor, Constructor constructor)
    {
        /* 生成函数所属类型签名 */
        String owner = SignatureUtil.getSignature(constructor.getDeclaringClass(),false);
        /* 获取函数名称 */
        String name = "<init>";
        /* 生成函数的返回类型的签名 */
        String methodDesc = SignatureUtil.getSignature( constructor);
        int invokeOp =INVOKESPECIAL ;
        methodVisitor.visitMethodInsn(invokeOp, owner, name, methodDesc, false);
    }

    public static void newClass(MethodVisitor methodVisitor, Class<?> clazz,Constructor constructor)
    {
        methodVisitor.visitTypeInsn(NEW,  SignatureUtil.getSignature(clazz,false));
        methodVisitor.visitInsn(DUP);
        JASMEmit.invoke(methodVisitor, constructor);
    }
}
