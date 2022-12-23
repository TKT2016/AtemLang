package atem.compiler.utils.msgresources;

import java.util.HashMap;

class CompileMessagesZHCN implements ICompileMessages{

    private HashMap<String,String> msgs;

    private CompileMessagesZHCN()
    {
        msgs = new HashMap<>();
        addMsgs();
    }

    private void addMsgs()
    {
        msgs.put(CompileMessagesUtil.SourceFileNotFound,"源文件不存在:%s");
        msgs.put(CompileMessagesUtil.SourceFileReadError,"文件读取发生异常:%s");
        msgs.put(CompileMessagesUtil.TypeNotFound,"类型'%s'不存在");
        msgs.put(CompileMessagesUtil.TypeImporteDuplicated,"类型'%s'重复导入");
        msgs.put(CompileMessagesUtil.IntegerValueError,"整数数值'%s'错误或者过大");
        msgs.put(CompileMessagesUtil.FloatValueError,"float数值'%s'错误或者过大");
        msgs.put(CompileMessagesUtil.ExpectFor,"期望是'%s'");
        msgs.put(CompileMessagesUtil.ExpectIdent,"期望是标识符");
        msgs.put(CompileMessagesUtil.PackageMissingName,"package缺少名称");
        msgs.put(CompileMessagesUtil.ElseMissingIf,"ELSE缺少IF");
        msgs.put(CompileMessagesUtil.RBRACEMissingLBRACE,"右大括号没有匹配的左大括号");
        msgs.put(CompileMessagesUtil.IllegalExpressionStatementElement,"非法的表达式语句成分");
        msgs.put(CompileMessagesUtil.IllegalType,"非法的类型");
        msgs.put(CompileMessagesUtil.ImportMissingName,"import缺少类名称或包名称");
        msgs.put(CompileMessagesUtil.ImportIllegalType,"导入的不是正确的类型");
        msgs.put(CompileMessagesUtil.RequireMissingName,"require缺少类名称或包名称");
        msgs.put(CompileMessagesUtil.FunctionMissingName,"函数名称不能为空");
        msgs.put(CompileMessagesUtil.FunctionMissingParameters,"函数缺少形参");
        msgs.put(CompileMessagesUtil.FunctionMissingBody,"缺少函数体");
        msgs.put(CompileMessagesUtil.VariableMissingName,"变量缺少名称");
        msgs.put(CompileMessagesUtil.VariableIllegalNameSelf,"'%s'不能作为声明变量名称");
        msgs.put(CompileMessagesUtil.VariableIllegalNameDuplicated,"'%s'与静态类型名称相同,不能作为声明变量名称");
        msgs.put(CompileMessagesUtil.VariableIllegalNameDollar,"Lambda 内部参数'%s'不能作为声明变量名称");

        msgs.put(CompileMessagesUtil.WhileLoopMissingCondition,"while循环语句缺少条件表达式");
        msgs.put(CompileMessagesUtil.WhileLoopMissingBody,"while循环语句缺少循环体");
        msgs.put(CompileMessagesUtil.IfMissingCondition,"if语句缺少条件表达式");
        msgs.put(CompileMessagesUtil.IfMissingBody,"if语句缺少执行语句");
        msgs.put(CompileMessagesUtil.StatementMissingExpression,"语句缺少表达式");
        msgs.put(CompileMessagesUtil.DynamicMemberShouldBePair,"动态变量的成员应该是名称键值对");
        msgs.put(CompileMessagesUtil.DynamicMemberShouldBeIdent,"动态类成员必须是标识符");

        msgs.put(CompileMessagesUtil.BRACEMissingExperssion,"括号内缺少表达式");
        msgs.put(CompileMessagesUtil.AssignLeftMissingExperssion,"赋值语句左边缺少表达式");
        msgs.put(CompileMessagesUtil.AssignRightMissingExperssion,"赋值语句右边缺少表达式");
        msgs.put(CompileMessagesUtil.UnaryExpressionOpShouldBe,"一元表达式前缀只能为'+','-','!'");
        msgs.put(CompileMessagesUtil.UnaryExpressionRightMissingExperssion,"一元表达式右边缺少表达式");
        msgs.put(CompileMessagesUtil.BinaryLeftMissingExperssion,"二元表达式左边缺少表达式");
        msgs.put(CompileMessagesUtil.BinaryRightMissingExperssion,"二元表达式右边缺少表达式");
        msgs.put(CompileMessagesUtil.DotExperssionMissingName,"点表达式缺少被限定名称");
        msgs.put(CompileMessagesUtil.IdentMissingName,"标识符不能为空");
        msgs.put(CompileMessagesUtil.PairMissingKey,"键值对缺少 key");
        msgs.put(CompileMessagesUtil.PairIllegal,"'%s'不是map项");

        msgs.put(CompileMessagesUtil.DotExperssionMissingLeft,"'.'运算缺少左表达式");
        msgs.put(CompileMessagesUtil.RedundantDivisionSymbols,"多余的分割符号");
        msgs.put(CompileMessagesUtil.RedundantCommaSymbols,"多余的逗号");
        msgs.put(CompileMessagesUtil.RedundantSemiSymbols,"多余的分号");
        msgs.put(CompileMessagesUtil.MacroFirstMustBeIdent,"macro的第一项必须是标识符");
        msgs.put(CompileMessagesUtil.MacroIllegalDefined,"错误的宏定义");

        msgs.put(CompileMessagesUtil.SymbolAmbiguity,"有歧义的多个符号'%s'");
        msgs.put(CompileMessagesUtil.SymbolNotFound,"找不到变量'%s'");

        msgs.put(CompileMessagesUtil.ModifierStaticError,"'%s'静态修饰符不同");
        msgs.put(CompileMessagesUtil.ParameterDuplicated,"方法已经定义了参数 '%s'");
        msgs.put(CompileMessagesUtil.ParameterSelfMustFirst,"'%s' 参数应该放在第一个位置");
        msgs.put(CompileMessagesUtil.ParameterDollarMustInLambda,"'%s变量'只能在Lambda内声明使用");

        msgs.put(CompileMessagesUtil.ParameterMacroNotAllowSelf,"定义 macro的参数不能有'%s'");
        msgs.put(CompileMessagesUtil.FunctionNameDuplicated,"已经经定义了函数 '%s'");
        msgs.put(CompileMessagesUtil.FunctionInvocationError,"函数调用错误");
        msgs.put(CompileMessagesUtil.MacroInvocationAmbiguity,"不确定的macro调用");
        msgs.put(CompileMessagesUtil.MacroBodyNoReturn,"Macro方法体中的return语句不能有返回值");
        msgs.put(CompileMessagesUtil.IncompatibleTypeInt,"不兼容的类型,无法转换为int");
        msgs.put(CompileMessagesUtil.IncompatibleTypeBoolean,"不兼容的类型,无法转换为boolean");
        msgs.put(CompileMessagesUtil.IncompatibleTypeNumber,"数据类型应该是数字");
    }

    public String getMsg(String key)
    {
        return msgs.get(key);
    }

    private static CompileMessagesZHCN _singleton;

    public static CompileMessagesZHCN getSingleton()
    {
        if(_singleton==null)
        {
            _singleton = new CompileMessagesZHCN();
        }
        return  _singleton;
    }

    public String getLineWord()
    {
        return "行";
    }

    public String getColumnWord()
    {
        return "列";
    }

    public String getErrorWord()
    {
        return "错误";
    }
}
