package atem.compiler.utils.msgresources;

import java.util.HashMap;

class CompileMessagesENGLISH implements ICompileMessages{

    private HashMap<String,String> msgs;

    private CompileMessagesENGLISH()
    {
        msgs = new HashMap<>();
        addMsgs();
    }

    private void addMsgs()
    {
        msgs.put(CompileMessagesUtil.SourceFileNotFound,"source file not found :%s");
        msgs.put(CompileMessagesUtil.SourceFileReadError,"source file exception while reading :%s");
        msgs.put(CompileMessagesUtil.TypeNotFound,"type '%s' not found");
        msgs.put(CompileMessagesUtil.TypeImporteDuplicated,"import type '%s' duplicated");
        msgs.put(CompileMessagesUtil.IntegerValueError,"integer value '%s' error or too big");
        msgs.put(CompileMessagesUtil.FloatValueError,"float value '%s'  error or too big");
        msgs.put(CompileMessagesUtil.ExpectFor," expect is '%s'");
        msgs.put(CompileMessagesUtil.ExpectIdent,"expect is identifier");
        msgs.put(CompileMessagesUtil.PackageMissingName,"package missing name");
        msgs.put(CompileMessagesUtil.ElseMissingIf,"'ELSE' statement minssing 'IF'");
        msgs.put(CompileMessagesUtil.RBRACEMissingLBRACE,"the closing brace has no matching opening brace");
        msgs.put(CompileMessagesUtil.IllegalExpressionStatementElement,"illegal expression statement element");
        msgs.put(CompileMessagesUtil.IllegalType,"illegal Type");
        msgs.put(CompileMessagesUtil.ImportMissingName,"'import' missing type or package");
        msgs.put(CompileMessagesUtil.ImportIllegalType,"imported type is not the correct type");
        msgs.put(CompileMessagesUtil.RequireMissingName,"'require' missing type or package");
        msgs.put(CompileMessagesUtil.FunctionMissingName,"function missing name");
        msgs.put(CompileMessagesUtil.FunctionMissingParameters,"function missing parameter");
        msgs.put(CompileMessagesUtil.FunctionMissingBody,"function missing body");
        msgs.put(CompileMessagesUtil.VariableMissingName,"variable missing name");
        msgs.put(CompileMessagesUtil.VariableIllegalNameSelf,"'%s' cannot be a declared variable name");
        msgs.put(CompileMessagesUtil.VariableIllegalNameDuplicated,"'%s' same as static type name, cannot be used as declaration variable name");
        msgs.put(CompileMessagesUtil.VariableIllegalNameDollar,"Lambda parameter '%s' cannot be a declared variable name");

        msgs.put(CompileMessagesUtil.WhileLoopMissingCondition,"while loop missing condition");
        msgs.put(CompileMessagesUtil.WhileLoopMissingBody,"while loop missing body");
        msgs.put(CompileMessagesUtil.IfMissingCondition,"'if' statement missing condition");
        msgs.put(CompileMessagesUtil.IfMissingBody,"'if' statement missing then statement");
        msgs.put(CompileMessagesUtil.StatementMissingExpression,"statement missing expression");
        msgs.put(CompileMessagesUtil.DynamicMemberShouldBePair,"members of Dynamic should be name key value pairs");
        msgs.put(CompileMessagesUtil.DynamicMemberShouldBeIdent,"members of Dynamic should be identifier");

        msgs.put(CompileMessagesUtil.BRACEMissingExperssion,"missing expression in parentheses");
        msgs.put(CompileMessagesUtil.AssignLeftMissingExperssion,"missing expression on the left side of assignment statement");
        msgs.put(CompileMessagesUtil.AssignRightMissingExperssion,"missing expression on the right side of assignment statement");
        msgs.put(CompileMessagesUtil.UnaryExpressionOpShouldBe,"the unary expression prefix can only be '+','-','!'");
        msgs.put(CompileMessagesUtil.UnaryExpressionRightMissingExperssion,"missing expression on the right of unary expression");
        msgs.put(CompileMessagesUtil.BinaryLeftMissingExperssion,"missing expression on left of binary expression");
        msgs.put(CompileMessagesUtil.BinaryRightMissingExperssion,"missing expression on right of binary expression");
        msgs.put(CompileMessagesUtil.DotExperssionMissingName,"the dot expression is missing a qualified name");
        msgs.put(CompileMessagesUtil.IdentMissingName,"identifier cannot be empty");
        msgs.put(CompileMessagesUtil.PairMissingKey,"key value pair is missing key");
        msgs.put(CompileMessagesUtil.PairIllegal,"'%s' not a map item");

        msgs.put(CompileMessagesUtil.DotExperssionMissingLeft,"missing expression on the left side of assignment statement the dot expression");
        msgs.put(CompileMessagesUtil.RedundantDivisionSymbols,"redundant division symbols");
        msgs.put(CompileMessagesUtil.RedundantCommaSymbols,"redundant comma symbols");
        msgs.put(CompileMessagesUtil.RedundantSemiSymbols,"Redundant semi symbols");
        msgs.put(CompileMessagesUtil.MacroFirstMustBeIdent,"the first item of macro must be an identifier");
        msgs.put(CompileMessagesUtil.MacroIllegalDefined,"bad macro definition");

        msgs.put(CompileMessagesUtil.SymbolAmbiguity,"ambiguous multiple symbols'%s'");
        msgs.put(CompileMessagesUtil.SymbolNotFound,"variable '%s'  not found");

        msgs.put(CompileMessagesUtil.ModifierStaticError,"'%s' static modifiers differ");
        msgs.put(CompileMessagesUtil.ParameterDuplicated,"function already has parameters defined '%s'");
        msgs.put(CompileMessagesUtil.ParameterSelfMustFirst,"parameter '%s' should be placed in the first position");
        msgs.put(CompileMessagesUtil.ParameterDollarMustInLambda,"dollar parameter '%s' can only be declared in lambda");

        msgs.put(CompileMessagesUtil.ParameterMacroNotAllowSelf,"parameters defining macro cannot have '% s'");
        msgs.put(CompileMessagesUtil.FunctionNameDuplicated,"function '%s' already defined");
        msgs.put(CompileMessagesUtil.FunctionInvocationError,"function call error");
        msgs.put(CompileMessagesUtil.MacroInvocationAmbiguity,"uncertain macro call");
        msgs.put(CompileMessagesUtil.MacroBodyNoReturn,"the return statement in the Macro method body cannot have a return value");
        msgs.put(CompileMessagesUtil.IncompatibleTypeInt,"incompatible type, cannot convert to int");
        msgs.put(CompileMessagesUtil.IncompatibleTypeBoolean,"Incompatible type, cannot convert to boolean");
        msgs.put(CompileMessagesUtil.IncompatibleTypeNumber,"the data type should be a number");
    }

    public String getMsg(String key)
    {
        return msgs.get(key);
    }

    private static CompileMessagesENGLISH _singleton;

    public static CompileMessagesENGLISH getSingleton()
    {
        if(_singleton==null)
        {
            _singleton = new CompileMessagesENGLISH();
        }
        return  _singleton;
    }

    public String getLineWord()
    {
        return " line ";
    }

    public String getColumnWord()
    {
        return " column ";
    }

    public String getErrorWord()
    {
        return " error ";
    }
}
