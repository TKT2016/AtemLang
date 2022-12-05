package atem.compiler.emits.jasm;

import atem.compiler.ast.JCTree;
import atem.compiler.ast.models.BelongsInfo;
import atem.compiler.emits.SymbolSignatureUtil;

import atem.compiler.symbols.BVarSymbol;
import atem.compiler.symbols.DVarSymbol;
import atem.compiler.symbols.SymbolUtil;
import atem.compiler.symbols.VarSymbolKind;
import atem.compiler.tools.JASMEmit;
import atem.compiler.utils.CompileError;
import atem.lang.rt.InterpreterError;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.GETFIELD;

class VarEmiter {

    public VarEmiter()
    {

    }

    public void loadField(BVarSymbol declVarSymbol, MethodVisitor mv)
    {
        if(declVarSymbol.varKind== VarSymbolKind.field)
        {
            String owner = SymbolSignatureUtil.getParamsSignature(declVarSymbol.ownerType, false);
            var loadop = declVarSymbol.isStatic ?GETSTATIC:GETFIELD;
            String typeSign =SymbolSignatureUtil.getParamsSignature(declVarSymbol.getTypeSymbol(),true);
            mv.visitFieldInsn(loadop, owner , declVarSymbol.name ,typeSign);
        }
        else {
           throw new CompileError();
        }
    }

    public void loadNormalVar(DVarSymbol declVarSymbol, MethodVisitor mv)
    {
        if(declVarSymbol.varKind== VarSymbolKind.field)
        {
            loadField(declVarSymbol,mv);
          /*  String owner = SymbolSignatureUtil.getParamsSignature(declVarSymbol.ownerType, false);
            var loadop = declVarSymbol.isStatic ?GETSTATIC:GETFIELD;
            String typeSign =SymbolSignatureUtil.getParamsSignature(declVarSymbol.getTypeSymbol(),true);
            mv.visitFieldInsn(loadop, owner , declVarSymbol.name ,typeSign);*/
        }
        else {
            int adr = declVarSymbol.adr;
            if(adr<0) throw new CompileError();
            int op = OpCodeSelecter.load(declVarSymbol.getTypeSymbol());
            mv.visitVarInsn(op, adr);
        }
    }

    void loadVar(MethodVisitor mv , DVarSymbol declVarSymbol, DVarSymbol lambdaVarArraySymbol , BelongsInfo belongsInfo)
    {
        if(declVarSymbol.varKind== VarSymbolKind.FuncParameter ||declVarSymbol.varKind== VarSymbolKind.localVar)
        {
            if(declVarSymbol.isMacroCallArg)
            {
                EmitUtil.emitRef(mv,this ,declVarSymbol, belongsInfo);
            }
            else  if(declVarSymbol.isLambdaRefVar) //if(declVarSymbol.isEmitLocalVarRef())  //
            {
                loadVar(mv,lambdaVarArraySymbol,null, belongsInfo);
                EmitUtil.loadConstInteger(mv,declVarSymbol.adr_lambda);
                mv.visitInsn(AALOAD);
            }
            else
            {
                loadNormalVar(declVarSymbol,mv);
            }
        }
        else if(declVarSymbol.varKind== VarSymbolKind.field)
        {
            loadNormalVar(declVarSymbol,mv);
        }
        else if(declVarSymbol.varKind== VarSymbolKind.MacroParameter)
        {
            loadNormalVar(declVarSymbol,mv);
        }
        else
        {
            throw new InterpreterError("暂不实现this");
        }
    }

    /** 保存变量 */
    public void storeVar(MethodVisitor mv  , BVarSymbol varSymbol, JCTree tree , ValueEmitOperation valueEmitOperation) {
        if(varSymbol.varKind.equals(VarSymbolKind.field))
        {
            valueEmitOperation.emit();
            EmitUtil.emitStoreField(mv, varSymbol);
        }
        else if(varSymbol.varKind.equals(VarSymbolKind.FuncParameter)
                ||varSymbol.varKind.equals(VarSymbolKind.localVar )
                ||varSymbol.varKind.equals(VarSymbolKind.MacroParameter )
        )
        {
            if(SymbolUtil.isEmitLocalVarRef(varSymbol))
            {
                var lambdaVarArraySymbol =    tree.belongsInfo.defedCallableAST.getCallableCinfo().lambdaVarArraySymbol;//.getLambdaVarArraySymbol();
                loadNormalVar(lambdaVarArraySymbol,mv);//  loadVar(lambdaVarArraySymbol,mv,null);
               // methodVisitor.visitFieldInsn(GETFIELD, "asmtest/Test2", "objects", "[Ljava/lang/Object;");
                int adr_lambda = SymbolUtil.getLambdaAdr(varSymbol);
                EmitUtil.loadConstInteger(mv,adr_lambda);// methodVisitor.visitVarInsn(ILOAD, adr_lambda); //methodVisitor.visitVarInsn(ILOAD, 1);
                valueEmitOperation.emit();//   fileEmit.emit(valueTree,arg);//    methodVisitor.visitVarInsn(ALOAD, 2);
                JASMEmit.aastore(mv); // mv.visitInsn(AASTORE);
            }
            else
            {
                storeNormalVar(mv,varSymbol,valueEmitOperation);
            }
        }
        else
            throw new CompileError();
    }

    public void storeNormalVar(MethodVisitor mv , BVarSymbol varSymbol,   ValueEmitOperation valueEmitOperation)
    {
        DVarSymbol declVarSymbol = (DVarSymbol) varSymbol;
        if(declVarSymbol.adr<0)
            throw new CompileError();
        valueEmitOperation.emit();
        int op = OpCodeSelecter.getStoreOpCode(declVarSymbol.getTypeSymbol());
        mv.visitVarInsn(op, declVarSymbol.adr);
        if(declVarSymbol.adr<0) throw new CompileError();
    }

    public interface ValueEmitOperation {
        void emit( );
    }
}
