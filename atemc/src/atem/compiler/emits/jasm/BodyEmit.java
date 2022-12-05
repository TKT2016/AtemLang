package atem.compiler.emits.jasm;

import atem.compiler.ast.*;
import atem.compiler.ast.callables.JCFunction;
import atem.compiler.ast.callables.JCLambda;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.makes.JCFieldAccess;
import atem.compiler.ast.makes.MKArrayAccess;
import atem.compiler.ast.makes.MKNewClass;
import atem.compiler.emits.*;
import atem.compiler.lex.TokenKind;
import atem.compiler.symbols.*;
import atem.compiler.tools.JASMEmit;
import atem.compiler.tools.SignatureUtil;
import atem.compiler.utils.CompileError;
import atem.compiler.utils.Debuger;
import atem.lang.rt.BreakException;
import atem.lang.Dynamic;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class BodyEmit  extends TreeScanner<EmitContext> implements Emiter
{
    VarEmiter varEmiter = new VarEmiter();

    public void emit(JCTree tree, EmitContext arg) {
        if (tree == null) return;
        tree.scan(this, arg);
    }

    /** 方法字节码生成 */
    @Override
    public void visitMethodDef(JCFunction tree, EmitContext arg)
    {
        /* 创建一个public的函数写入器 */
        MethodVisitor mv = EmitUtil.emitMethodDeclare(tree.methodSymbol,arg.classWriter);
        /* 设置上下文参数的函数字节码写入器、行号写入器、函数结束标签 */
        arg.mv =mv;
        arg.lineNumberEmit = new LineNumberEmit();
        if(tree.isSelfFirst)
        {
            //var annotationVisitor0 = mv.visitAnnotation("Latem/lang/ParameterSelfFirstAnnotation;", true);
            var annotationVisitor0 = mv.visitAnnotation(SignatureUtil.warpL(RTSigns.ParameterSelfFirstAnnotationSign ), true);
            annotationVisitor0.visitEnd();
        }
        /* 函数体生成正式开始 */
        mv.visitCode();
        /* 标记函数体开始标签 */
        mv.visitLabel( tree.getScope().getStartLabel() );
        EmitUtil. emitFuncBodyInitRet(varEmiter,tree,arg);

        var lambdaVarArraySymbol = tree.getCallableCinfo().lambdaVarArraySymbol;
        varEmiter.storeVar(arg.mv,lambdaVarArraySymbol,tree,()->{
            var lambdaVarCount =tree.getCallableCinfo().maxLambdaVarCount;
            //Debuger.outln("127 get lambdaVarCount:"+lambdaVarCount+"," +tree.name());
            EmitUtil.loadConstInteger(mv,lambdaVarCount);
            mv.visitTypeInsn(ANEWARRAY, RTSigns.ObjectSign);
        });

        for (int i = 0; i < tree.methodSymbol.getParameterCount(); i++) {
            DVarSymbol varSymbol =  tree.methodSymbol.getParameterSymbol(i);
            if(varSymbol.isEmitLocalVarRef() )  //   if(varSymbol.isLambdaRefVar || varSymbol.isMacroCallArg)
            {
                varEmiter. loadNormalVar(lambdaVarArraySymbol,mv);
                EmitUtil.loadConstInteger(mv,varSymbol.adr_lambda);
                int adr = varSymbol.adr;
                if(adr<0) throw new CompileError();
                int op = OpCodeSelecter.load(varSymbol.getTypeSymbol());
                mv.visitVarInsn(op, adr);
                JASMEmit.aastore(mv);
            }
        }

        /* 生成函数体 */
        tree.body.scan(this,arg);
        /* 标记函数体结束标签 */
        mv.visitLabel( tree.getScope().getEndLabel() );
        varEmiter. loadNormalVar(tree.getCallableCinfo().retVarSymbol,mv);
        /* 生成返回指令,把栈顶的值返回 */
        EmitUtil.emitReturn(tree,mv);
        /*  函数内变量信息生成 */
        new LocalVariableEmit( ).emitVars(tree,mv);
        /* 计算计算函数栈帧 */
       EmitUtil.  visitMethodMax(tree,mv);
        /* 函数体生成结束 */
        mv.visitEnd();
    }

    @Override
    public void visitLambda(JCLambda tree, EmitContext arg)
    {
       EmitUtil.emitLambdaRef(tree,arg);
    }

    /** 方法字节码生成 */
    @Override
    public void visitMacroDef(JCMacroDecl tree, EmitContext arg)
    {
        /* 创建一个public的函数写入器 */
        MethodVisitor mv = EmitUtil.emitMacroVisitor(tree.macroSymbol,arg.classWriter);
        arg.mv =mv;
        arg.lineNumberEmit = new LineNumberEmit();
        mv.visitCode();
        mv.visitLabel( tree.getScope() .getStartLabel() );

        EmitUtil. emitFuncBodyInitRet(varEmiter,tree,arg);

        var lambdaVarArraySymbol = tree.getCallableCinfo().lambdaVarArraySymbol;
        varEmiter.storeVar(arg.mv,lambdaVarArraySymbol,tree,()->{
            var lambdaVarCount =tree.getCallableCinfo().maxLambdaVarCount;
            EmitUtil.loadConstInteger(mv,lambdaVarCount);
            mv.visitTypeInsn(ANEWARRAY, RTSigns.ObjectSign);
        });

        for (int i = 0; i < tree.macroSymbol.getParameterCount(); i++) {
            DVarSymbol varSymbol =  tree.macroSymbol.getParameterSymbol(i);
            if(varSymbol.isEmitLocalVarRef() )  //  if(varSymbol.isLambdaRefVar)
            {
                varEmiter. loadNormalVar(lambdaVarArraySymbol,mv);
                EmitUtil.loadConstInteger(mv,varSymbol.adr_lambda);
                int adr = varSymbol.adr;
                if(adr<0) throw new CompileError();
                int op = OpCodeSelecter.load(varSymbol.getTypeSymbol());
                mv.visitVarInsn(op, adr);
                JASMEmit.aastore(mv);
            }
        }

        /* 生成函数体 */
        tree.body.scan(this,arg);
        mv.visitLabel( tree.getScope().getEndLabel() );
        varEmiter. loadNormalVar(tree.getCallableCinfo().retVarSymbol,mv);

        EmitUtil.emitReturn(tree,mv);
        /*  函数内变量信息生成 */
        new LocalVariableEmit( ).emitVars(tree,mv);
        /* 计算计算函数栈帧 */
        EmitUtil.  visitMethodMax(tree,mv);
        /* 函数体生成结束 */
        mv.visitEnd();
    }

    /** 代码块生成 */
    @Override
    public void visitBlock(JCBlock tree, EmitContext arg)
    {
        var scope =tree.belongsInfo.scope;
        arg.mv.visitLabel( scope.getStartLabel());
        if(tree.statements==null ||tree.statements.size()==0 )
        {
            arg.mv.visitInsn(NOP);
        }
        else
        {
            for(JCStatement statement:tree.statements)
            {
                arg.lineNumberEmit.emitLineNumber(arg,statement.line);
                statement.scan(this,arg);
            }
        }

        arg.mv.visitLabel(scope.getEndLabel());
        /* 生成结束标签行号 */
        arg.lineNumberEmit.emitLineNumber(arg,tree.line,scope.getEndLabel());
    }

    /** 生成While循环语句 */
    @Override
    public void visitWhileLoop(JCWhile tree, EmitContext arg) {
        /*循环开始标签*/
        Label loopStartLabel = new Label();
        /*循环结束标签*/
        Label loopEndLabel = new Label();

        MethodVisitor methodVisitor =arg.mv;
        /*标记循环开始标签*/
        methodVisitor.visitLabel(loopStartLabel);

        Label tryStartLabel = new Label();
        Label tryEndLabel = new Label();
        Label breakHandler = new Label();
       //methodVisitor.visitTryCatchBlock(tryStartLabel, tryEndLabel, breakHandler, "atem/interpreter/rt/BreakException");
        methodVisitor.visitTryCatchBlock(tryStartLabel, tryEndLabel, breakHandler, RTSigns.BreakExceptionSign);
     /*   Label continueHandler = new Label();
        methodVisitor.visitTryCatchBlock(tryStartLabel, tryEndLabel, continueHandler, "atem/interpreter/rt/ContinueException");*/

        /* 生成循环条件表达式 */
        emit(tree.cond,arg);
        //methodVisitor.visitMethodInsn(INVOKESTATIC, RTSigns.RTCoreSign, "toBoolean", "(Ljava/lang/Object;)Z", false);
        JASMEmit.invoke(methodVisitor,RTMembers.toBoolean);

        /* 条件结果为false时,跳转到loopEndLabel结束循环 */
        methodVisitor.visitJumpInsn(IFEQ, loopEndLabel);

        /*生成循环体*/
        methodVisitor.visitLabel(tryStartLabel);
        tree.body.scan(this,arg);
        methodVisitor.visitLabel(tryEndLabel);

        /*无条件跳转到循环开始标签*/
        methodVisitor.visitJumpInsn(GOTO, loopStartLabel);

         methodVisitor.visitLabel(breakHandler);
        methodVisitor.visitInsn(POP);
        methodVisitor.visitJumpInsn(GOTO, loopEndLabel);
/*
        methodVisitor.visitLabel(continueHandler);
        methodVisitor.visitInsn(POP);
        methodVisitor.visitJumpInsn(GOTO, loopStartLabel);
*/
        /*标记循环结束标签*/
        methodVisitor.visitLabel(loopEndLabel);
        /* 生成结束标签行号 */
        arg.lineNumberEmit.emitLineNumber(arg,tree.line,loopEndLabel);
    }

    @Override
    public void visitBreak(BreakStatement tree, EmitContext arg)
    {
        MethodVisitor methodVisitor =arg.mv;
        JASMEmit.newClass(methodVisitor, BreakException.class,RTMembers.newBreakException);
       /* methodVisitor.visitTypeInsn(NEW,  RTSigns.BreakExceptionSign);//"atem/interpreter/rt/BreakException");
        methodVisitor.visitInsn(DUP);
        JASMEmit.invoke(methodVisitor,RTMembers.newBreakException);
        //methodVisitor.visitMethodInsn(INVOKESPECIAL, "atem/interpreter/rt/BreakException", "<init>", "()V", false);*/
        methodVisitor.visitInsn(ATHROW);
    }
/*
    @Override
    public void visitContinue(ContinueStatement tree, EmitContext arg)
    {
        MethodVisitor methodVisitor =arg.mv;
        methodVisitor.visitTypeInsn(NEW, "atem/interpreter/rt/ContinueException");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "atem/interpreter/rt/ContinueException", "<init>", "()V", false);
        methodVisitor.visitInsn(ATHROW);
    }*/

    /** 生成if语句 */
    @Override
    public void visitIf(JCIf tree, EmitContext arg) {
        MethodVisitor mv =arg.mv;
        if(tree.elsepart==null)
        {
            /* IF结束标签 */
            Label endLabel = new Label();
            /* 生成条件表达式 */
            emit(tree.cond,arg);
            JASMEmit.invoke(mv,RTMembers.toBoolean);
           // mv.visitMethodInsn(INVOKESTATIC, RTSigns.RTCoreSign, "toBoolean", "(Ljava/lang/Object;)Z", false);
            /* IFEQ表示false,跳转到 endLabel */
            mv.visitJumpInsn(IFEQ, endLabel);
            /* 生成true执行语句 */
            emit(tree.thenpart,arg);
            /* 标记IF结束标签 */
            mv.visitLabel(endLabel);
        }
        else
        {
            /* else标签 */
            Label elseLabel = new Label();//else标签
            /* IF结束标签 */
            Label endLabel = new Label();
            /* 生成条件表达式 */
            emit(tree.cond,arg);
            JASMEmit.invoke(mv,RTMembers.toBoolean);//mv.visitMethodInsn(INVOKESTATIC, RTSigns.RTCoreSign, "toBoolean", "(Ljava/lang/Object;)Z", false);
            /* IFEQ表示false,跳转到 elseLabel */
            mv.visitJumpInsn(IFEQ, elseLabel);
            /* 生成true执行语句 */
            emit(tree.thenpart,arg);
            /* 无条件跳转到结束标签 */
            mv.visitJumpInsn(GOTO, endLabel);
            /* 标记else标签 */
            mv.visitLabel(elseLabel);//
            /* 生成else语句 */
            emit(tree.elsepart,arg);//
            /* 标记IF结束标签 */
            mv.visitLabel(endLabel);
        }
    }

    /** 生成return语句 */
    @Override
    public void visitReturn(JCReturn tree, EmitContext arg) {
        arg.lineNumberEmit.emitLineNumber(arg,tree.line);
        var methodVisitor =arg.mv;
       if(
               TreeeUtil.isMacroCallLamba(tree.belongsInfo.defedCallableAST)
               || tree.belongsInfo.defedCallableAST instanceof JCMacroDecl
       )
       {
           methodVisitor.visitTypeInsn(NEW, RTSigns.ReturnExceptionSign);//  methodVisitor.visitTypeInsn(NEW, "atem/interpreter/rt/ReturnException");
           if (tree.expr != null) {
               methodVisitor.visitInsn(DUP);
               emit(tree.expr, arg);
               JASMEmit.invoke(methodVisitor,RTMembers.newReturnException1);
               //methodVisitor.visitMethodInsn(INVOKESPECIAL,
               //        "atem/interpreter/rt/ReturnException", "<init>", "(Ljava/lang/Object;)V", false);
           }
           else
           {
               methodVisitor.visitInsn(DUP);
               JASMEmit.invoke(methodVisitor,RTMembers.newReturnException0);
               //methodVisitor.visitMethodInsn(INVOKESPECIAL, "atem/interpreter/rt/ReturnException", "<init>", "()V", false);
           }
           methodVisitor.visitInsn(ATHROW);
       }
       else {
           /* 生成return表达式 */
           if (tree.expr != null) {
               var retVarSymbol = tree.belongsInfo.defedCallableAST.getCallableCinfo().retVarSymbol;
               varEmiter.storeVar(arg.mv, retVarSymbol, tree, () -> {
                   emit(tree.expr, arg);
               });
           }
           var methodEndLabel = tree.belongsInfo.defedCallableAST.getScope().getEndLabel();
           /* 跳转到方法的结束标签 */
           arg.mv.visitJumpInsn(GOTO, methodEndLabel);
       }
    }

    /** 生成表达式语句 */
    @Override
    public void visitExprStmt(JCExprStatement tree, EmitContext arg) {
        var methodVisitor = arg.mv;
        arg.lineNumberEmit.emitLineNumber(arg,tree.line);

        /* 生成语句内的表达式 */
        emit(tree.expr,arg);
        boolean exprIsJCMethodInvocationVoid = false;
        if(tree.expr instanceof JCMethodInvocation) {
            /* 如果表达式有返回值,需要生成pop指令,把栈顶值清除 */
            if (SymbolUtil.isVoid(tree.expr.symbol.getTypeSymbol())) {
                exprIsJCMethodInvocationVoid =true;
            }
        }
        else if( tree.expr instanceof JCAssign)
        {
            exprIsJCMethodInvocationVoid =true;
        }
        else if( tree.expr instanceof JCVariableDecl)
        {
            exprIsJCMethodInvocationVoid =true;
        }
        if(tree.propertys.isLambdaBodyOne) //lambda最后一句作为返回值,不用POP
        {
            /*DVarSymbol varSymbol = null;
            if (tree.expr instanceof JCVariableDecl) {
                JCVariableDecl jcVariableDecl = (JCVariableDecl) tree.expr;
                varSymbol = (DVarSymbol) jcVariableDecl.nameExpr.symbol;
            }
            else if(tree.expr instanceof JCAssign ) {
                JCAssign exp = (JCAssign) tree.expr;
                varSymbol = (DVarSymbol) exp.left.symbol;
            }
            if(varSymbol!=null)
            {
                if (varSymbol.isLambdaRefVar) {
                    var lambdaVarArraySymbol = tree.belongsInfo.defedCallableAST.getCallableCinfo().lambdaVarArraySymbol;//.getLambdaVarArraySymbol();
                    methodVisitor.visitTypeInsn(NEW, AtemLanguageConst.LocalVarRefSign);
                    methodVisitor.visitInsn(DUP);
                    varEmiter.loadNormalVar(lambdaVarArraySymbol, arg.mv);
                    EmitUtil.loadConstInteger(arg.mv, varSymbol.adr_lambda);
                    methodVisitor.visitMethodInsn(INVOKESPECIAL,  AtemLanguageConst.LocalVarRefSign, "<init>", "([Ljava/lang/Object;I)V", false);
                } else {
                    varEmiter.loadNormalVar(varSymbol, arg.mv);
                }
            }*/
            if (!exprIsJCMethodInvocationVoid) {
                /* 生成return表达式 */
                var retVarSymbol = tree.belongsInfo.defedCallableAST.getCallableCinfo().retVarSymbol;//.getRetVarSymbol();
                varEmiter.storeVar(arg.mv, retVarSymbol, tree, () -> {
                    //emit(tree.expr, arg);
                });
            }
        }
        else {
           /* if(tree.expr instanceof JCAssign
            || tree.expr instanceof JCVariableDecl
            )
            {

            }
            else*/ if (exprIsJCMethodInvocationVoid == false)
                arg.mv.visitInsn(POP);
        }
    }

    /** 生成常量表达式 */
    @Override
    public void visitLiteral(JCLiteral tree, EmitContext arg) {
        MethodVisitor mv =arg.mv;
        Object value = tree.value;
        var methodVisitor = arg.mv;
        if(tree.isNullLiteral)
        {
            JASMEmit.loadNull(methodVisitor);
        }
        else if(value instanceof String)
        {
            /* 生成加载常量池中的字符串 */
            JASMEmit.loadStringLiteral(arg.mv,(String) value);
        }
        else if(value instanceof Integer)
        {
            /* 生成加载整数指令 */
            int valueInt =  ((Integer) value).intValue();
            EmitUtil.loadConstInteger(mv,valueInt);
            JASMEmit.invoke(methodVisitor,RTMembers.IntegerValueOf);
            //mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        }
        else if(value instanceof Float)
        {
            mv.visitLdcInsn((Float)value);
            JASMEmit.invoke(methodVisitor,RTMembers.FloatValueOf);
            //methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        }
        else if(value instanceof Boolean)
        {
            /* 把布尔值转为整数0或1,再加载整数 */
            boolean valueBoolean =  ((Boolean) value).booleanValue();
            int valueInt = valueBoolean?1:0;
            EmitUtil.loadConstInteger(mv,valueInt);
            JASMEmit.invoke(methodVisitor,RTMembers.BooleanValueOf);
            //methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        }
    }

    /** 生成标识符表达式 */
    public void visitIdent(JCIdent tree, EmitContext arg) {
        MethodVisitor methodVisitor =arg.mv;
        Symbol symbol = tree.symbol;

        if (symbol instanceof BTypeSymbol) {
            EmitUtil.emitTypeLiteral(methodVisitor,symbol);
        }
        else if (symbol instanceof DMethodSymbol) {
            DMethodSymbol bMethodSymbol =(DMethodSymbol)symbol;
            String fileClassSignature = SymbolSignatureUtil.getParamsSignature(tree.belongsInfo.fileTree.fileSymbol,true);
            String methodName =tree.getName();
            EmitUtil.emitMethodRef(methodVisitor,bMethodSymbol,fileClassSignature,methodName);
        }
        else if (symbol instanceof RMethodSymbol) {
            RMethodSymbol methodSymbol =(RMethodSymbol)symbol;
            String fileClassSignature = SymbolSignatureUtil.getParamsSignature(methodSymbol.ownerType,true);
            String methodName =tree.getName();
            EmitUtil.emitMethodRef(methodVisitor,methodSymbol,fileClassSignature,methodName);
        }
        else  if (symbol instanceof RVarSymbol)
        {
            RVarSymbol varSymbol = (RVarSymbol) symbol;
            this.varEmiter.loadField(varSymbol,methodVisitor);
        }
        else  if (symbol instanceof DVarSymbol) {
            DVarSymbol declVarSymbol = (DVarSymbol) symbol;
           // if(tree.isMacroCallSub)
            if(tree.propertys.isMacroCallArg)
            {
                EmitUtil.emitRef(methodVisitor, varEmiter, declVarSymbol, tree.belongsInfo );
            }
            else
            {
                var lambdaVarArraySymbol = tree.belongsInfo.defedCallableAST.getCallableCinfo().lambdaVarArraySymbol;
                varEmiter.loadVar(methodVisitor,declVarSymbol , lambdaVarArraySymbol,tree.belongsInfo);
            }
        }
        else
        {
            throw new CompileError();
        }
    }

    /** 生成点运算表达式访问 */
    @Override
    public void visitFieldAccess(JCFieldAccess tree, EmitContext arg) {
        //Debuger.outln("380 visitFieldAccess:"+tree);
        /*if(tree.isInstanceof)
        {
            emit(tree.selected, arg);
        }
        else*/ {
            var methodVisitor = arg.mv;
            emit(tree.selected, arg);
            methodVisitor.visitLdcInsn(tree.nameToken.identName);
            EmitUtil. emitNewDotMember(methodVisitor);
        }
    }

    /** 生成一元运算表达式 */
    @Override
    public void visitUnary(JCUnary tree, EmitContext arg) {
        MethodVisitor mv =arg.mv;
        /* 生成表达式 */
        emit(tree.expr,arg);
        TokenKind opcode = tree.opcode;
        EmitUtil.emitUnaryOp(mv,opcode);
    }

    @Override
    public void visitArrayLiteral(JCArrayLiteral tree, EmitContext context)
    {
        var methodVisitor =context.mv;
        if(tree.symbol.equals(RClassSymbolManager.AListSymbol))
        {
            JASMEmit.newClass(methodVisitor, atem.lang.List.class,RTMembers.newList);

          /*  methodVisitor.visitTypeInsn(NEW, RTSigns.AtemListSign);
            methodVisitor.visitInsn(DUP);
            JASMEmit.invoke(methodVisitor,RTMembers.newList);*/
            //methodVisitor.visitMethodInsn(INVOKESPECIAL, RTSigns.AtemListSign, "<init>", "()V", false);

            for(var element:tree.elements)
            {
                methodVisitor.visitInsn(DUP);
                emit(element,context);
                JASMEmit.invoke(methodVisitor,RTMembers.listAdd);
               // methodVisitor.visitMethodInsn(INVOKEVIRTUAL, RTSigns.AtemListSign, "add", "(Ljava/lang/Object;)V", false);
            }
        }
        else
        {
            methodVisitor.visitTypeInsn(NEW, RTSigns.AtemMapSign);
            if(tree.defaultPair==null)
            {
                methodVisitor.visitInsn(DUP);
                JASMEmit.invoke(methodVisitor,RTMembers.newMap0);
              //  methodVisitor.visitMethodInsn(INVOKESPECIAL, RTSigns.AtemMapSign, "<init>", "()V", false);
            }
            else
            {
                methodVisitor.visitInsn(DUP);
                emit(tree.defaultPair.right,context);
                JASMEmit.invoke(methodVisitor,RTMembers.newMap1);
               // methodVisitor.visitMethodInsn(INVOKESPECIAL, RTSigns.AtemMapSign, "<init>", "(Ljava/lang/Object;)V", false);
            }

            for(var element:tree.elements)
            {
                JCPair jcPair = (JCPair)element;
                if(jcPair.isMapDefaultItem==false)
                {
                    methodVisitor.visitInsn(DUP);
                    emit(element,context);
                    JASMEmit.invoke(methodVisitor,RTMembers.mapSetKV);
                   // methodVisitor.visitMethodInsn(INVOKEVIRTUAL, RTSigns.AtemMapSign, "put", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
                }
            }
        }
    }

    public void visitPair(JCPair tree, EmitContext arg)
    {
        if(tree.isMapDefaultItem)
            return;
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }

    public void visitDynamicLiteral(JCDynamicLiteral tree, EmitContext arg) {
        var methodVisitor =arg.mv;
        JASMEmit.newClass(methodVisitor, Dynamic.class,RTMembers.newDynamic);
        /*methodVisitor.visitTypeInsn(NEW, RTSigns.AtemDynamicSign);
        methodVisitor.visitInsn(DUP);
        JASMEmit.invoke(methodVisitor,RTMembers.newDynamic);*/
        //methodVisitor.visitMethodInsn(INVOKESPECIAL, RTSigns.AtemDynamicSign, "<init>", "()V", false);

        for(JCPair JCPair :tree.pairs)
        {
            methodVisitor.visitInsn(DUP);
            JASMEmit.getField(methodVisitor,RTMembers.fieldDynamicPrototype);
            /*methodVisitor.visitFieldInsn(GETFIELD,
                    RTSigns.AtemDynamicSign,
                    RTSigns.AtemObjectPropertyFieldName,
                    SignatureUtil.warpL(RTSigns.AtemPrototypeSign)
            );*/
            JCIdent left = (JCIdent) JCPair.left;
            JASMEmit.loadStringLiteral( methodVisitor, left.getName());
            visitTree(JCPair.right,arg);
            JASMEmit.invoke(methodVisitor,RTMembers.PrototypeAddMember);
           // methodVisitor.visitMethodInsn(INVOKEVIRTUAL, RTSigns.AtemPrototypeSign, "__addMember", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
        }
    }

    /** 生成二元运算表达式 */
    @Override
    public void visitBinary(JCBinary tree, EmitContext arg) {
        var op =tree.opcode;
        var methodVisitor =arg.mv;
        emit(tree.left, arg);
        emit(tree.right, arg);
        EmitUtil.emitBinaryOp(methodVisitor,op);
    }

    /** 生成变量声明表达式 */
    @Override
    public void visitVarDef(JCVariableDecl tree, EmitContext arg)
    {
        // if(tree.getDimName().equals("ele"))
        //    Debuger.outln("589 visitVarDef:"+tree);
        var lambdaVarArraySymbol = tree.belongsInfo.defedCallableAST.getCallableCinfo().lambdaVarArraySymbol;//.getLambdaVarArraySymbol();
        var kind = tree.expandKind;
        var mv= arg.mv;
        DVarSymbol varSymbol = (DVarSymbol) tree.nameExpr.symbol;
        if(kind== JCVariableDecl.ExpandKindEnum.NONE) {
            if (tree.init != null) {
                varEmiter.storeVar(arg.mv, varSymbol, tree, () -> {
                    emit(tree.init, arg);
                });
            }
            else {
                varEmiter.storeVar(arg.mv, varSymbol, tree, () -> {
                    JASMEmit.loadNull(arg.mv);
                });
            }
            if(tree.propertys.isMacroCallArg)
            {
                varEmiter.loadVar(mv,varSymbol,lambdaVarArraySymbol ,tree.belongsInfo);
            }
        }
        else if(kind == JCVariableDecl.ExpandKindEnum.StaticType)
        {
            varEmiter.storeVar(arg.mv, varSymbol, tree, () -> {
                BTypeSymbol typeSymbol = (BTypeSymbol)tree.expandInfo;
                EmitUtil.emitTypeLiteral(mv,typeSymbol);
            });
        }
        else if(kind == JCVariableDecl.ExpandKindEnum.LambdaVarGet)
        {
            varEmiter.storeVar(arg.mv, varSymbol, tree, () -> {
                DVarSymbol declVarSymbol = (DVarSymbol) tree.expandInfo;
                varEmiter.loadVar( mv,declVarSymbol, lambdaVarArraySymbol,tree.belongsInfo);
            });
        }
        else if(kind == JCVariableDecl.ExpandKindEnum.DotMember)
        {
            varEmiter.storeVar(arg.mv, varSymbol, tree, () -> {
                JCFieldAccess fieldAccess =  (JCFieldAccess) tree.expandInfo;
                var methodVisitor =arg.mv;
                emit(fieldAccess.selected,arg);// methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                methodVisitor.visitLdcInsn(fieldAccess.nameToken.identName);
                JASMEmit.invoke(methodVisitor, RTMembers.newDotMember);
                //methodVisitor.visitMethodInsn(INVOKESTATIC,  RTSigns.RTCoreSign, "newDotMember",
               //         "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            });
        }
        else if(kind == JCVariableDecl.ExpandKindEnum.LambdaRef)
        {
            varEmiter.storeVar(arg.mv, varSymbol, tree, () -> {
                JCLambda jcLambda  =(JCLambda)tree.expandInfo;
                var methodVisitor = arg.mv;
                var innerSign = SymbolSignatureUtil.getParamsSignature(tree.belongsInfo.fileTree.fileInnerClassSymbol, false);
                var lambdaName = jcLambda.methodName;
                /* new DotMember */
              //  methodVisitor.visitTypeInsn(NEW, AtemLanguageConst.ObjectDotMemberSign );
              // methodVisitor.visitInsn(DUP);
                /* new LambdaClass */
                methodVisitor.visitTypeInsn(NEW, innerSign);// methodVisitor.visitTypeInsn(NEW, "asmtest/TestGenLambda$LambdaClass2");
                methodVisitor.visitInsn(DUP);
                if (tree.belongsInfo.callableASTParent == null)
                    throw new CompileError();
                /* LambdaClass init */
                var lambdaArraySymbol = tree.belongsInfo.callableASTParent.getCallableCinfo().lambdaVarArraySymbol;//.getLambdaVarArraySymbol();
                if(lambdaArraySymbol.adr<0) throw new CompileError();
                methodVisitor.visitVarInsn(ALOAD, lambdaArraySymbol.adr);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, innerSign, "<init>", "([Ljava/lang/Object;)V", false);

                /* DotMember init */
                methodVisitor.visitLdcInsn(lambdaName);
                JASMEmit.invoke(methodVisitor, RTMembers.newDotMember);
                //methodVisitor.visitMethodInsn(INVOKESTATIC,  RTSigns.RTCoreSign, "newDotMember",
                //        "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
                //methodVisitor.visitMethodInsn(INVOKESPECIAL, AtemLanguageConst.ObjectDotMemberSign , "<init>", "(Ljava/lang/Object;Ljava/lang/String;)V", false);

            });
        }
        else
            throw new CompileError();
    }

    /** 生成赋值表达式 */
    @Override
    public void visitAssign(JCAssign tree, EmitContext arg) {
        //var methodVisitor = arg.mv;
        if(tree.left instanceof JCFieldAccess)
        {
            //JCFieldAccess jcFieldAccess = (JCFieldAccess)tree.left;
            /* 生成左边被赋值的表达式 */
            emit(tree.left,arg);
            /* 生成右边的值 */
            emit(tree.right,arg);
            JASMEmit.invoke(arg.mv,RTMembers.setValue);// arg.mv.visitMethodInsn(INVOKESTATIC, RTSigns.RTCoreSign, "setValue", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
        }
        else if(tree.left instanceof JCIdent)
        {
            Symbol leftSymbol = tree.left.symbol;
            if(leftSymbol instanceof DVarSymbol)
            {
                DVarSymbol varSymbol = (DVarSymbol) leftSymbol;
                if(varSymbol.varKind.equals(VarSymbolKind.MacroParameter))
                {
                    emit(tree.left,arg);
                    emit(tree.right,arg);
                    JASMEmit.invoke(arg.mv,RTMembers.setValue);// arg.mv.visitMethodInsn(INVOKESTATIC, RTSigns.RTCoreSign, "setValue", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
                }
                else {
                    varEmiter.storeVar(arg.mv, varSymbol, tree, () -> {
                        emit(tree.right, arg);
                    });
                }
            }
            else
            {
                RVarSymbol varSymbol = (RVarSymbol) leftSymbol;
                varEmiter.storeVar(arg.mv, varSymbol, tree, () -> {
                    emit(tree.right, arg);
                });
            }
        }
        else if(tree.left.symbol .equals(RClassSymbolManager.IAddrRefSymbol))
        {
            emit(tree.right,arg);
            JASMEmit.invoke(arg.mv,RTMembers.setValue);
            //arg.mv.visitMethodInsn(INVOKESTATIC, RTSigns.RTCoreSign, "setValue", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
            //methodVisitor.visitMethodInsn(INVOKEVIRTUAL,  AtemLanguageConst.LocalVarRefSign, "setValue", "(Ljava/lang/Object;)V", false);
        }
        else
        {
            throw new CompileError();
        }
    }
/*
    void emitInstanceof(JCMethodInvocation tree, EmitContext arg)
    {
        emit( tree.methodExpr,arg);
        var symbol = tree.getArgs()[0].symbol;
        String desc =  SymbolSignatureUtil.getParamsSignature(symbol,false);
        arg.mv.visitTypeInsn(INSTANCEOF, desc);
       // arg.mv.visitMethodInsn(INVOKESTATIC, AtemLanguageConsts.RTCoreSign, "toBoolean", "(Ljava/lang/Object;)Z", false);
        arg.mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
    }*/

    /** 生成函数调用表达式 */
    @Override
    public void visitMethodInvocation(JCMethodInvocation tree, EmitContext arg) {
        //if(tree.methodExpr.toString().endsWith(".setBounds"))
       //     Debuger.outln("748 visitMethodInvocation:"+tree);
       /* if(tree.isInstanceof)
        {
            emitInstanceof( tree,  arg);
            return;
        }*/
        //{
            /* 生成函数名称表达式 */
            emit( tree.methodExpr,arg);
            var methodVisitor =arg.mv;
            int argsCount = tree.getArgs().length;
            EmitUtil.loadConstInteger(methodVisitor,argsCount);
            methodVisitor.visitTypeInsn(ANEWARRAY, RTSigns.ObjectSign);
            if(argsCount>0) {
                /* 生成参数 */
                for (int i = 0; i < argsCount; i++) {
                    JASMEmit.dup(methodVisitor);
                    EmitUtil.loadConstInteger(methodVisitor, i);
                    var jcExpression = tree.getArgs()[i];
                    emit(jcExpression, arg);
                    JASMEmit.aastore(methodVisitor);
                }
            }
            JASMEmit.invoke(arg.mv,RTMembers.invoke);
       // }
    }

    @Override
    public void visitMacroCall(JCMacroCall tree, EmitContext arg) {
        MacroCallCatchEmiter macroCallCatchEmiter = new MacroCallCatchEmiter(tree,arg,this);
        macroCallCatchEmiter.emit();
    }

    @Override
    public void visitParens(JCParens tree, EmitContext arg) {
        emit(tree.expr,arg);
    }

    @Override
    public void visitArrayAccess(MKArrayAccess tree, EmitContext arg)
    {
        throw new CompileError();
    }

    @Override
    public void visitNewClass(MKNewClass tree, EmitContext arg)
    {
        throw new CompileError();
    }
}
