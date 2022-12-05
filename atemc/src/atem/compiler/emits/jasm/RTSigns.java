package atem.compiler.emits.jasm;

import atem.compiler.tools.SignatureUtil;
import atem.lang.rt.*;

public abstract class RTSigns {
    public static final String ObjectSign ="java/lang/Object";
    public static final String AtemListSign ;
    public static final String AtemMapSign ;
    public static final String AtemDynamicSign ;
    public static final String AtemPrototypeSign ;
    public static final String AtemObjectPropertyFieldName="prototype" ;

    public static final String ObjectDotMemberSign ;
    public static final String RTCoreSign ;
    public static final String TypeLiteralSign ;//="atem/interpreter/rt/TypeLiteral";
    public static final String MethodRefSign ;

    public static final String MacroAnnotationSign;
    public static final String ParameterSelfFirstAnnotationSign;
    public static final String VoidReturnSign;
    public static final String BreakExceptionSign;
    public static final String ReturnExceptionSign;
    public static final String AtemSourceGenSign;// ="atem/lang/AtemSourceGen";

    public static final String LocalVarRefSign ;//="atem/interpreter/rt/LocalVarRef";
    public static final String FieldRefSign ;//="atem/interpreter/rt/FieldRef";

    static
    {
        AtemListSign = SignatureUtil.getSignature(atem.lang.List.class,false);
        AtemMapSign = SignatureUtil.getSignature(atem.lang.Map.class,false);
        AtemDynamicSign = SignatureUtil.getSignature(atem.lang.Dynamic.class,false);
        AtemPrototypeSign = SignatureUtil.getSignature(atem.lang.Prototype.class,false);
        ObjectDotMemberSign = SignatureUtil.getSignature(ObjectDotMember.class,false);
        RTCoreSign =  SignatureUtil.getSignature(RTCore.class,false);
        TypeLiteralSign =  SignatureUtil.getSignature(TypeLiteral.class,false);
        MethodRefSign =  SignatureUtil.getSignature(MethodRef.class,false);
        MacroAnnotationSign =  SignatureUtil.getSignature(Macro.class,false);
        VoidReturnSign =  SignatureUtil.getSignature(VoidReturn.class,false);
        ParameterSelfFirstAnnotationSign = SignatureUtil.getSignature(SelfFunction.class,false);
        BreakExceptionSign = SignatureUtil.getSignature(BreakException.class,false);
        ReturnExceptionSign = SignatureUtil.getSignature(ReturnException.class,false);

        AtemSourceGenSign = SignatureUtil.getSignature(AtemSourceGen.class,false);
        LocalVarRefSign = SignatureUtil.getSignature(LocalVarRef.class,false);
        FieldRefSign = SignatureUtil.getSignature(FieldRef.class,false);
    }
}
