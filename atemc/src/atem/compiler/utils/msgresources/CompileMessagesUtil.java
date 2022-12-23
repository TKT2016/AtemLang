package atem.compiler.utils.msgresources;

import java.util.Calendar;
import java.util.TimeZone;

public class CompileMessagesUtil {

    public static ICompileMessages getCompileMessages( )
    {
        Calendar ca = Calendar.getInstance();
        TimeZone tz = ca.getTimeZone();
        if(!tz.getID().equals("Asia/Shanghai"))
            return CompileMessagesZHCN.getSingleton();
        else
            return CompileMessagesENGLISH.getSingleton();
    }

    public static String getMsg(String key)
    {
        return getCompileMessages().getMsg(key);
    }

    public static String getLineWord()
    {
        return getCompileMessages().getLineWord();
    }

    public static String getColumnWord()
    {
        return getCompileMessages().getColumnWord();
    }

    public static String getErrorWord()
    {
        return getCompileMessages().getErrorWord();
    }

    public static final String SourceFileNotFound="SourceFileNotFound";
    public static final String SourceFileReadError="SourceFileReadError";
    public static final String TypeNotFound="TypeNotFound";
    public static final String TypeImporteDuplicated="TypeImporteDuplicated";
    public static final String IntegerValueError="IntegerValueError";
    public static final String FloatValueError="FloatValueError";
    public static final String ExpectFor="ExpectFor";
    public static final String ExpectIdent="ExpectIdent";
    public static final String PackageMissingName="PackageMissingName";
    public static final String ElseMissingIf="ElseMissingIf";
    public static final String RBRACEMissingLBRACE="RBRACEMissingLBRACE";
    public static final String IllegalExpressionStatementElement="IllegalExpressionStatementElement";
    public static final String IllegalType="IllegalType";
    public static final String ImportMissingName="ImportMissingName";
    public static final String ImportIllegalType="IllegalType";
    public static final String RequireMissingName="RequireMissingName";
    public static final String FunctionMissingName="FunctionMissingName";
    public static final String FunctionMissingParameters="FunctionMissingParameters";
    public static final String FunctionMissingBody="FunctionMissingBody";
    public static final String VariableMissingName="VariableMissingName";
    public static final String VariableIllegalNameSelf ="VariableIllegalName";
    public static final String VariableIllegalNameDuplicated="VariableIllegalNameDuplicated";
    public static final String VariableIllegalNameDollar="VariableIllegalNameDollar";

    public static final String WhileLoopMissingCondition="WhileLoopMissingCondition";
    public static final String WhileLoopMissingBody="WhileLoopMissingBody";
    public static final String IfMissingCondition="IfMissingCondition";
    public static final String IfMissingBody="IfMissingBody";
    public static final String StatementMissingExpression="StatementMissingExpression";
    public static final String DynamicMemberShouldBePair ="DynamicMemberShouldBePair";
    public static final String DynamicMemberShouldBeIdent ="DynamicMemberShouldBeIdent";
    public static final String BRACEMissingExperssion="BRACEMissingExperssion";

    public static final String AssignLeftMissingExperssion="AssignLeftMissingExperssion";
    public static final String AssignRightMissingExperssion="AssignRightMissingExperssion";
    public static final String UnaryExpressionOpShouldBe ="UnaryExpressionOpShouldBe";
    public static final String UnaryExpressionRightMissingExperssion ="UnaryExpressionRightMissingExperssion";
    public static final String BinaryLeftMissingExperssion="BinaryLeftMissingExperssion";
    public static final String BinaryRightMissingExperssion="BinaryRightMissingExperssion";
    public static final String DotExperssionMissingName="DotExperssionMissingName";
    public static final String DotExperssionMissingLeft="DotExperssionMissingLeft";
    public static final String IdentMissingName="IdentMissingName";
    public static final String PairMissingKey="PairMissingKey";
    public static final String PairIllegal="PairIllegal";

    public static final String RedundantDivisionSymbols="RedundantDivisionSymbols";
    public static final String RedundantCommaSymbols="RedundantCommaSymbols";
    public static final String RedundantSemiSymbols="RedundantSemiSymbols";
    public static final String MacroFirstMustBeIdent="MacroFirstMustBeIdent";
    public static final String MacroIllegalDefined="MacroIllegalDefined";

    public static final String SymbolAmbiguity="SymbolAmbiguity";
    public static final String SymbolNotFound="SymbolNotFound";

    public static final String ModifierStaticError="ModifierStaticError";

    public static final String ParameterDuplicated="ParameterDuplicated";
    public static final String ParameterSelfMustFirst="ParameterSelfMustFirst";
    public static final String ParameterDollarMustInLambda ="ParameterDollarMustInLambda";

    public static final String ParameterMacroNotAllowSelf="ParameterMacroNotAllowSelf";

    public static final String FunctionNameDuplicated="FunctionNameDuplicated";
    public static final String FunctionInvocationError ="FunctionInvocationError";
    public static final String MacroInvocationAmbiguity ="MacroInvocationAmbiguity";
    public static final String MacroBodyNoReturn ="MacroBodyNoReturn";
    public static final String IncompatibleTypeInt ="IncompatibleTypeInt";
    public static final String IncompatibleTypeBoolean ="IncompatibleTypeBoolean";
    public static final String IncompatibleTypeNumber ="IncompatibleTypeNumber";
}



