package atem.compiler.emits.jasm;

import atem.compiler.CompilerConsts;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.emits.*;
import atem.compiler.emits.pre.LocalVarAdrScanner;
import atem.compiler.symbols.*;
import atem.compiler.ast.*;
import atem.compiler.CompileContext;
import org.objectweb.asm.*;
import atem.compiler.tools.FileUtil;
import static org.objectweb.asm.Opcodes.*;

/** 源文件生成器 */
public class FileEmiter
{
    BodyEmit bodyEmit = new BodyEmit();
    FileLambdaEmit fileLambdaEmit = new FileLambdaEmit();
    /** 生成 */
    public void emit(JCFileTree fileTree, CompileContext context)
    {
        //new ExpandTreeTranslator( ).translate(fileTree);
        new LocalVarAdrScanner( ).visitCompilationUnit(fileTree);
       // new MaxStackScanner( ).visitCompilationUnit(fileTree);
        EmitContext arg = new EmitContext();
        visitCompilationUnit(fileTree,arg,context.getOutPath());
        fileLambdaEmit.emit(fileTree,context.getOutPath());
    }

    /** 文件字节码生成 */
    public void visitCompilationUnit(JCFileTree fileTree  , EmitContext arg ,String outputPath) {
       // System.out.println("save path:"+outputPath);
        FileSymbol sourceFileSymbol  = fileTree.fileSymbol;
        /* 根据源文件名称生成类型签名 */
        String fileClassSignature = SymbolSignatureUtil.getParamsSignature(sourceFileSymbol,false);
        /* 创建classWriter */
        arg.classWriter= EmitUtil.newAtemSourceFileObjectClassWriter(fileClassSignature);
        /* classWriter记录源文件信息 */
        arg.classWriter.visitSource(fileTree.log.sourceFile, null);
        emitFields(  fileTree, arg. classWriter);
        //arg.classWriterInner = EmitUtil.emitLambdaClass(fileTree.fileInnerClassSymbol,fileTree);
        /* 生成一个无参数的默认构造函数 */
        EmitUtil.emitDefaultConstuctor(arg.classWriter, RTSigns.AtemSourceGenSign);

        /* 生成所有函数 */
        for(JCFunction method:fileTree.functions)
            method.scan(bodyEmit,arg);
        for(var macro:fileTree.JCMacroDecls)
            macro.scan(bodyEmit,arg);

        emitCLinit(fileTree,arg);
        emitVoidMain(arg.classWriter);
        /* classWriter结束 */
        arg.classWriter.visitEnd();
        /* 保存class字节码文件到out文件夹 */
        String className = FileUtil.getNameNoExt(fileTree.log.sourceFile);
        sourceFileSymbol.compiledClassFile = EmitUtil. saveClassByteFile( arg.classWriter , outputPath, sourceFileSymbol.packageName,className) ;
    }

    void emitFields(JCFileTree fileTree,ClassWriter classWriter)
    {
        for(JCVariableDecl variableDecl:fileTree.fieldDecls)
        {
            DVarSymbol varSymbol = (DVarSymbol)variableDecl.nameExpr.symbol;
           var fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_STATIC, varSymbol.name, "Ljava/lang/Object;", null, null);
            fieldVisitor.visitEnd();
        }
    }

    void emitVoidMain(ClassWriter classWriter)
    {
      var  methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(18, label0);
        methodVisitor.visitInsn(RETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("args", "[Ljava/lang/String;", null, label0, label1, 0);
        methodVisitor.visitMaxs(0, 1);
        methodVisitor.visitEnd();
    }

    void emitCLinit(JCFileTree fileTree,EmitContext arg)
    {
       Label startLabel = new Label();
        Label endLabel = new Label();
        MethodVisitor mv  =arg. classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);

        arg.mv =mv;
        arg.lineNumberEmit = new LineNumberEmit();
        mv.visitCode();
        mv.visitLabel( startLabel);
        String owner = SymbolSignatureUtil.getParamsSignature(fileTree.fileSymbol, false);
        String name = CompilerConsts.clinitMethodName;// methodSymbol.name;
        String returnSign ="()Ljava/lang/Object;";// SymbolSignatureUtil.getParamsSignature(methodSymbol, true);
        int invokeOp =INVOKESTATIC;
        var isInterface =false;
        mv.visitMethodInsn(invokeOp, owner, name, returnSign, isInterface);
        mv.visitInsn(RETURN);
        mv.visitLabel( endLabel);
        EmitUtil.visitMaxs(mv,1,0);
        mv.visitEnd();
    }
}
