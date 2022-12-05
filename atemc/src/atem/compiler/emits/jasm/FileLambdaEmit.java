package atem.compiler.emits.jasm;

import atem.compiler.CompilerConsts;
import atem.compiler.ast.JCFileTree;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.emits.SymbolSignatureUtil;
import atem.compiler.symbols.FileInnerClassSymbol;
import atem.compiler.symbols.FileSymbol;
import atem.compiler.tools.FileUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

public class FileLambdaEmit {
    BodyEmit bodyEmit = new BodyEmit();
    LocalVariableEmit localVariableEmit = new LocalVariableEmit();
    public void emit(JCFileTree fileTree,String outputPath)
    {
        EmitContext arg = new EmitContext();
         var classWriter= emitLambdaClass(fileTree.fileInnerClassSymbol,fileTree);
        arg.classWriter=classWriter;
        FileSymbol sourceFileSymbol  = fileTree.fileSymbol;
        String className = FileUtil.getNameNoExt(fileTree.log.sourceFile);

        for(var lambda:fileTree.lambdas)
            emitLambdaBody(lambda,arg);

        fileTree.fileInnerClassSymbol.compiledClassFileInner =
                EmitUtil. saveClassByteFile( classWriter , outputPath, sourceFileSymbol.packageName,className+"$"+ CompilerConsts.lambdaInnerClassName) ;
    }

    public void emitLambdaBody(JCLambda tree, EmitContext arg)//, VarEmiter varEmiter, Emiter emiter)
    {
        var classWriter = arg.classWriter;
        var varEmiter = bodyEmit.varEmiter;
        arg =arg.clone();

        var methodSymbol =tree.methodSymbol;
        String methodName = methodSymbol.name;//方法名称
        String desc = SymbolSignatureUtil.getParamsSignature(methodSymbol,true);//方法描述
        int access = ACC_PUBLIC;

        var methodVisitor = classWriter.visitMethod(access, methodName, desc, null, null);
        var mv = methodVisitor;
        arg.mv =methodVisitor;
        arg.lineNumberEmit = new LineNumberEmit();

        methodVisitor.visitCode();
        methodVisitor.visitLabel(  tree.belongsInfo.scope.getStartLabel() );
        EmitUtil.emitFuncBodyInitRet(bodyEmit. varEmiter,tree,arg);
        varEmiter.storeVar(arg.mv,tree.getCallableCinfo().lambdaVarArraySymbol,tree,()->{
            mv.visitVarInsn(ALOAD, 0);
            String innerClassSign = SymbolSignatureUtil.getParamsSignature( tree.belongsInfo.fileTree.fileInnerClassSymbol ,false);
            mv.visitFieldInsn(GETFIELD, innerClassSign, CompilerConsts.lambdaInnerClassField, "[Ljava/lang/Object;");
        });
        bodyEmit.emit(tree.body,arg);//  tree.body.scan( emiter,arg);
        mv.visitLabel(  tree.belongsInfo.scope.getEndLabel() );
        varEmiter.loadNormalVar(tree.getCallableCinfo().retVarSymbol,   mv ); // fileEmit. varEmiter. loadVar(tree.getRetVarSymbol(), mv,null ); //返回默认变量
        EmitUtil.emitReturn(tree,mv);
        localVariableEmit.emitVars(tree,mv);
       EmitUtil. visitMethodMax(tree,mv);//    EmitUtil.visitMaxs(methodVisitor);
        methodVisitor.visitEnd();
    }

    public static ClassWriter emitLambdaClass(FileInnerClassSymbol fileInnerClassSymbol, JCFileTree fileTree   )
    {
        String innerClassSign = SymbolSignatureUtil.getParamsSignature(fileInnerClassSymbol,false);
        ClassWriter classWriter =EmitUtil. newClassWriter(innerClassSign);
        String outerSign = SymbolSignatureUtil.getParamsSignature(fileInnerClassSymbol.fileSymbol,false);
        classWriter.visitSource(fileTree.log.sourceFile, null);
        classWriter.visitNestHost(outerSign);

        classWriter.visitInnerClass(innerClassSign, outerSign, CompilerConsts.lambdaInnerClassName, ACC_PUBLIC | ACC_STATIC|ACC_FINAL);
        //classWriter.visitInnerClass("asmtest/TestGenLambda$LambdaClass2", "asmtest/TestGenLambda", "LambdaClass2", ACC_PUBLIC | ACC_STATIC);
        {
            var  fieldVisitor = classWriter.visitField(ACC_PUBLIC, CompilerConsts.lambdaInnerClassField, "[Ljava/lang/Object;", null, null);
            // var  fieldVisitor = classWriter.visitField(ACC_PUBLIC, "lambdaVars", "[Ljava/lang/Object;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            var  methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/Object;)V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(23, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(24, label1);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, innerClassSign , CompilerConsts. lambdaInnerClassField, "[Ljava/lang/Object;");
            //methodVisitor.visitFieldInsn(PUTFIELD, "asmtest/TestGenLambda$LambdaClass2", "lambdaVars", "[Ljava/lang/Object;");
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(25, label2);
            methodVisitor.visitInsn(RETURN);
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            //var outerClassSignL = SymbolSignatureUtil.getParamsSignature(fileInnerClassSymbol.fileSymbol,true);
            var innerClassSignL   = SymbolSignatureUtil.getParamsSignature(fileInnerClassSymbol,true);//outerClassSignL+"$"+AtemLanguageConsts.lambdaInnerClassName;
           // methodVisitor.visitLocalVariable("this", innerClassSignL, null, label0, label3, 0);

            methodVisitor.visitLocalVariable(CompilerConsts.lambdaInnerClassField, innerClassSignL , null, label0, label3, 1);
            //methodVisitor.visitLocalVariable("lambdaVars", "[Ljava/lang/Object;", null, label0, label3, 1);
           // EmitUtil.visitMaxs(methodVisitor);
            EmitUtil.visitMaxs(methodVisitor,2,2);// methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        return classWriter;
    }
}
