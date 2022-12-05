package atem.compiler.emits.jasm;

import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.ICallableAST;
import atem.compiler.ast.models.BelongsInfo;
import atem.compiler.emits.SymbolSignatureUtil;
import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.*;
import atem.compiler.ast.*;
import atem.compiler.tools.JASMEmit;
import atem.compiler.tools.SignatureUtil;
import atem.compiler.utils.CompileError;
import org.objectweb.asm.*;
import java.io.File;
import java.io.FileOutputStream;
import static org.objectweb.asm.Opcodes.*;

/** 生成辅助类 */
public class EmitUtil {

    final static boolean isAuto  = true;

    public static void emitMethodRef(MethodVisitor methodVisitor,BMethodSymbol methodSymbol,String classSign,String methodName)
    {
        methodVisitor.visitTypeInsn(NEW, RTSigns.MethodRefSign);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitLdcInsn(Type.getType(classSign));
        JASMEmit.loadStringLiteral(methodVisitor,methodName);
        JASMEmit.loadBooleanLiteral(methodVisitor,methodSymbol.isSelfFirst);
        JASMEmit.invoke(methodVisitor, RTMembers.newMethodRef3);
    }

    public static ClassWriter newAtemSourceFileObjectClassWriter(String signName ) {
        int flag=0;
        if(isAuto)
            flag = ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS ;
        ClassWriter classWriter = new ClassWriter(flag );
        String genClassName = SignatureUtil.nameToSign(signName);
        String superClass = RTSigns.AtemSourceGenSign;//父类描述
        String[] interfacesSigns = new String[]{};//继承接口
        int access = ACC_PUBLIC | ACC_FINAL | ACC_SUPER ;
        classWriter.visit(Opcodes.V1_8, access, genClassName, null, superClass , interfacesSigns);
        return classWriter;
    }

    /** 创建public的,继承Object的,自动计算栈帧的 ClassWriter, */
    public static ClassWriter newClassWriter(String signName ) {
        int flag=0;
        if(isAuto)
         flag = ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS ;
        //flag = ClassWriter.COMPUTE_FRAMES;
        ClassWriter classWriter = new ClassWriter(flag );
        String genClassName = SignatureUtil.nameToSign(signName);
        String superClass = "java/lang/Object";//父类描述
        String[] interfacesSigns = new String[]{};//继承接口
        int access = Opcodes.ACC_PUBLIC +Opcodes.ACC_SUPER;
        classWriter.visit(Opcodes.V1_8, access, genClassName, null, superClass , interfacesSigns);
        return classWriter;
    }

    public static void visitMethodMax(ICallableAST tree,MethodVisitor mv )
    {
        if(isAuto) {
            EmitUtil.visitMaxs(mv, 0, 0);
            return;
        }
        var ci = tree.getCallableCinfo();
        EmitUtil.visitMaxs(mv,ci.maxStack +4,ci.maxLocals);
    }

    /** 自动计算asm计算栈帧等，如果异常提示并继续 */
    public static void visitMaxs(MethodVisitor mv) {
        visitMaxs(mv,0, 0);
    }

    /** 设置asm计算栈帧等，如果异常提示并继续 */
    public static void visitMaxs(MethodVisitor mv, final int maxStack, final int maxLocals) {
        try {
            mv.visitMaxs(maxStack, maxLocals);
        } catch (Exception ex) {
            System.err.println("MethodVisitor.visitMaxs exception:" + ex.getMessage());
        }
    }

    /** 生成一个无参默认构造函数 */
    public static void emitDefaultConstuctor(ClassWriter classWriter,String superSign)
    {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC,"<init>", "()V", null, null);
        mv.visitCode();//开始
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superSign, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        EmitUtil.visitMaxs(mv,1,1);//  EmitUtil.visitMaxs(mv); //计算栈帧
        mv.visitEnd();//结束
    }

    public static void emitFuncBodyInitRet(VarEmiter varEmiter, ICallableAST tree, EmitContext arg)
    {
        var mv = arg.mv;
        varEmiter.storeVar(arg.mv,tree.getCallableCinfo().retVarSymbol,(JCTree) tree,()->{
            JASMEmit.getField(mv, RTMembers.fieldVoidReturnRet);
        });
    }

    public static void emitLambdaRef(JCLambda tree, EmitContext arg ) {
        var methodVisitor = arg.mv;
        var innerSign = SymbolSignatureUtil.getParamsSignature(tree.belongsInfo.fileTree.fileInnerClassSymbol, false);
        var lambdaName = tree.methodName;
        /* new DotMember */
        methodVisitor.visitTypeInsn(NEW, RTSigns.ObjectDotMemberSign );
        methodVisitor.visitInsn(DUP);
        /* new LambdaClass */
        methodVisitor.visitTypeInsn(NEW, innerSign);// methodVisitor.visitTypeInsn(NEW, "asmtest/TestGenLambda$LambdaClass2");
        methodVisitor.visitInsn(DUP);
        if (tree.belongsInfo.callableASTParent == null)
            throw  new CompileError();

        /* LambdaClass init */
        var lambdaArraySymbol = tree.belongsInfo.callableASTParent.getCallableCinfo().lambdaVarArraySymbol;
        if(lambdaArraySymbol.adr<0) throw new CompileError();
        methodVisitor.visitVarInsn(ALOAD, lambdaArraySymbol.adr);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, innerSign, "<init>", "([Ljava/lang/Object;)V", false);

        /* DotMember init */
        methodVisitor.visitLdcInsn(lambdaName);
        JASMEmit.invoke(methodVisitor, RTMembers.newObjectDotMember );
    }

    /** 创建一个public的函数写入器  */
    public static MethodVisitor emitMethodDeclare( DMethodSymbol methodSymbol,ClassWriter classWriter )
    {
        String methodName = methodSymbol.name;//方法名称
        String desc = SymbolSignatureUtil.getParamsSignature(methodSymbol,true);//方法描述
        int access = ACC_PRIVATE;
        if(methodSymbol.isPublic)
            access = ACC_PUBLIC;
        if(methodSymbol.isStatic)
            access =access|ACC_STATIC;
        MethodVisitor methodVisitor = classWriter.visitMethod(access, methodName, desc, null, null);
        return methodVisitor;
    }

    public static MethodVisitor emitMacroVisitor(DMacroSymbol macroSymbol, ClassWriter classWriter )
    {
        String methodName = macroSymbol.macroDecl.createMethodName();//方法名称
        String desc = SymbolSignatureUtil.getParamsSignature(macroSymbol,true);//方法描述
        int access = ACC_PUBLIC|ACC_STATIC;//|ACC_FINAL;

        MethodVisitor methodVisitor = classWriter.visitMethod(access, methodName, desc, null, null);
        {
            var annotationVisitor0 = methodVisitor.visitAnnotation(SignatureUtil.warpL( RTSigns.MacroAnnotationSign ) , true);
            annotationVisitor0.visit("value", macroSymbol.macroDecl.createMacroAnnotationValue());
            annotationVisitor0.visit("detail", macroSymbol.macroDecl.createMacroAnnotationDetail());
            annotationVisitor0.visitEnd();
        }
        return methodVisitor;
    }

    /** 保存字节码文件,返回生成的class文件路径 */
    public static String saveClassByteFile(ClassWriter classWriter, String saveFilePath, String packageName, String className)
    {
        byte[] data = classWriter.toByteArray();
        String packagePath = packageName.replace(".","/");
        String folderPath =  saveFilePath+"/"+packagePath+"/";

        File folder = new File(folderPath);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        String fullpath =folderPath+ className+".class";
        File file = new File(fullpath);
        try {
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(data);
            fout.close();
            return file.getAbsolutePath();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /** 根据函数返回值生成不同的返回指令 */
 /*   public static void emitReturn(JCFunction tree, MethodVisitor methodWriter) {
        DMethodSymbol meth =tree.methodSymbol;
        BTypeSymbol retType = meth.returnType;
        emitRetOpCode(methodWriter, retType);
    }

    public static void emitReturn(MacroAST tree, MethodVisitor methodWriter) {
        var meth =tree.macroSymbol;
        BTypeSymbol retType = meth.returnType;
        emitRetOpCode(methodWriter, retType);
    }

    public static void emitReturn(JCLambda tree, MethodVisitor methodWriter) {
        DMethodSymbol meth =tree.methodSymbol;
        BTypeSymbol retType = meth.returnType;
        emitRetOpCode(methodWriter, retType);
    }
*/
    public static void emitReturn(ICallableAST callableAST, MethodVisitor methodWriter) {
        BTypeSymbol retType =callableAST.getRetType();
        emitRetOpCode(methodWriter, retType);
    }

    public static void emitRetOpCode(MethodVisitor mv, BTypeSymbol retype)
    {
        if(SymbolUtil.isVoid(retype))
        {
            mv.visitInsn(Opcodes.RETURN);
        }
        else if(retype instanceof RClassSymbol)
        {
            RClassSymbol rClassSymbol = (RClassSymbol)retype;
            Class<?> type  = rClassSymbol.clazz;
            int op= OpCodeSelecter.ret(type);
            mv.visitInsn(op);
        }
        else {
            mv.visitInsn(ARETURN);
        }
    }
/*
    public  static int getBinaryOpCode(JCBinary tree)
    {
        BTypeSymbol resultType = tree.symbol.getTypeSymbol();
        RClassSymbol rClassSymbol = (RClassSymbol) resultType;
        TokenKind opcode = tree.opcode;
        if (opcode.equals(TokenKind.ADD))
           return OpCodeSelecter.add(rClassSymbol.clazz);
        else if (opcode.equals(TokenKind.SUB))
            return OpCodeSelecter.sub(rClassSymbol.clazz);
        else if (opcode.equals(TokenKind.MUL))
            return OpCodeSelecter.mul(rClassSymbol.clazz);
        else if (opcode.equals(TokenKind.DIV))
            return OpCodeSelecter.div(rClassSymbol.clazz);
        else
            throw new CompileError();
    }*/
/*
    public static void emitAND(JCBinary tree, EmitContext arg , Emiter gen)
    {
        MethodVisitor mv =arg.mv;
        Label l1 = new Label();
        Label l2 = new Label();

        gen.emit(tree.left,arg);
        mv.visitJumpInsn(IFEQ, l1);
        gen.emit(tree.right,arg);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
    }*/
/*
    public static void emitOR( JCBinary tree, EmitContext arg ,Emiter gen)
    {
        MethodVisitor mv =arg.mv;
        Label l1 = new Label();
        Label l2 = new Label();
        Label l3 = new Label();

        gen.emit(tree.left,arg);
        mv.visitJumpInsn(IFNE, l1);
        gen. emit(tree.right,arg);
        mv.visitJumpInsn(IFEQ, l2);
        mv.visitLabel(l1);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l2);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l3);
    }*/
/*
    public  static void emitCompare(JCBinary tree, EmitContext arg ,Emiter gen,int opcode)
    {
        MethodVisitor mv =arg.mv;
        Label l0 = new Label();
        mv.visitLabel(l0);
        gen. emit(tree.left,arg);
        gen.emit(tree.right,arg);
        Label l1 = new Label();
        mv.visitJumpInsn(opcode, l1);
        mv.visitInsn(ICONST_0);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(l2);
    }
    */
    public static void emitStoreField(MethodVisitor mv  , BVarSymbol varSymbol )
    {
        BTypeSymbol typeSymbol = varSymbol.getTypeSymbol();
        int op = varSymbol.isStatic ? PUTSTATIC : PUTFIELD;
        String ownerSign =  SymbolSignatureUtil.getParamsSignature(varSymbol.ownerType,false);
        String descSign =  SymbolSignatureUtil.getParamsSignature( typeSymbol,true);
        mv.visitFieldInsn(op,ownerSign, varSymbol.name,descSign);
    }

    public static void loadConstInteger(MethodVisitor methodVisitor, int ivalue)
    {
        if(ivalue>=0 && ivalue<=5)
        {
            int opcode = OpCodeSelecter.pushIntConst(ivalue);
            methodVisitor.visitInsn(opcode);
        }
        else if(ivalue>=-128 && ivalue<127)
        {
            methodVisitor.visitIntInsn(BIPUSH,ivalue);
        }
        else if(ivalue>=-32768 && ivalue<32767)
        {
            methodVisitor.visitIntInsn(SIPUSH,ivalue);
        }
        else
        {
            methodVisitor.visitLdcInsn(ivalue);
        }
    }

    public static void emitNewDotMember(MethodVisitor methodVisitor)
    {
        JASMEmit.invoke(methodVisitor, RTMembers.newDotMember);
       // methodVisitor.visitMethodInsn(INVOKESTATIC, AtemLanguageConst.RTCoreSign, "newDotMember",
       //         "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
    }

    public static void emitTypeLiteral(MethodVisitor methodVisitor,Symbol symbol)
    {
        String desc =  SymbolSignatureUtil.getParamsSignature(symbol,true);
        methodVisitor.visitLdcInsn(Type.getType(desc)); //methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/String;"));
        JASMEmit.invoke(methodVisitor, RTMembers.TypeLiteralGet);
        /*methodVisitor.visitMethodInsn(INVOKESTATIC, AtemLanguageConst.TypeLiteralSign,
                "get", "(Ljava/lang/Class;)Latem/interpreter/rt/TypeLiteral;", false);*/
    }

    public static void emitRef(MethodVisitor methodVisitor, VarEmiter  varEmiter, DVarSymbol declVarSymbol, BelongsInfo belongsInfo)
    {
        if(declVarSymbol.varKind== VarSymbolKind.field)
        {
            methodVisitor.visitTypeInsn(NEW, RTSigns.FieldRefSign);
            methodVisitor.visitInsn(DUP);
            var fileSymbol = belongsInfo.fileTree.fileSymbol;
            EmitUtil.emitTypeLiteral(methodVisitor,fileSymbol);
            String varName = declVarSymbol.name;
            methodVisitor.visitLdcInsn(varName);
            JASMEmit.invoke(methodVisitor, RTMembers.newFieldRef);
            //methodVisitor.visitMethodInsn(INVOKESPECIAL, AtemLanguageConst.FieldRefSign, "<init>", "(Latem/interpreter/rt/TypeLiteral;Ljava/lang/String;)V", false);
        }
        else {
            var lambdaVarArraySymbol = belongsInfo.defedCallableAST.getCallableCinfo().lambdaVarArraySymbol;
            methodVisitor.visitTypeInsn(NEW, RTSigns.LocalVarRefSign);
            methodVisitor.visitInsn(DUP);
            varEmiter.loadNormalVar(lambdaVarArraySymbol, methodVisitor);
            EmitUtil.loadConstInteger(methodVisitor, declVarSymbol.adr_lambda);
            JASMEmit.invoke(methodVisitor, RTMembers.newLocalVarRef);
            //methodVisitor.visitMethodInsn(INVOKESPECIAL, AtemLanguageConst.LocalVarRefSign, "<init>", "([Ljava/lang/Object;I)V", false);
        }
    }
    
    public static void emitUnaryOp(MethodVisitor methodVisitor,TokenKind op)
    {
        if(op== TokenKind.ADD)
        {
             return;
        }
        else if(op== TokenKind.SUB)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.NG);
            //methodVisitor.visitMethodInsn(INVOKESTATIC, AtemLanguageConst.RTCoreSign, "NG", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        }
        else if(op== TokenKind.NOT)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.NOT);
        }
        else
            throw new CompileError();
    }

    public static void emitBinaryOp(MethodVisitor methodVisitor,TokenKind op)
    {
        if(op== TokenKind.ADD)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.ADD);
           // methodVisitor.visitMethodInsn(INVOKESTATIC, AtemLanguageConst.RTCoreSign, "ADD", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        }
        else if(op== TokenKind.SUB)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.SUB);
        }
        else if(op== TokenKind.MUL)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.MUL);
        }
        else if(op== TokenKind.DIV)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.DIV);
        }
        else if(op== TokenKind.AND)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.AND);
        }
        else if(op== TokenKind.OR)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.OR);
        }
        else if(op== TokenKind.GT)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.GT);
        }
        else if(op== TokenKind.GTEQ)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.GTEQ);
        }
        else if(op== TokenKind.LT)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.LT);
        }
        else if(op== TokenKind.LTEQ)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.LTEQ);
        }
        else if(op== TokenKind.EQEQ)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.EQEQ);
        }
        else if(op== TokenKind.NOTEQ)
        {
            JASMEmit.invoke(methodVisitor, RTMembers.NOTEQ);
        }
        else
            throw new CompileError();
    }

}
