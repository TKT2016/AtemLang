package atem.compiler.ast;

import java.io.StringWriter;

import atem.compiler.ast.models.BelongsInfo;
import atem.compiler.lex.Token;
import atem.compiler.tools.IdGenerator;
import atem.compiler.utils.Debuger;
import atem.compiler.utils.SourceLog;
import atem.compiler.utils.msgresources.CompileMessagesUtil;

/** 抽象语法树父类 */
public abstract class JCTree implements Cloneable
{
    public Token posToken;
    /** 在线路中所在行号 */
    public int line;

    /** 报错器 */
    public SourceLog log;
    /** 用当前语法树的位置和行号以及代码加提示参数进行报错 */
    public void error(String key)
    {
        String msgf = CompileMessagesUtil.getMsg(key);
        String msg= String.format(msgf,this.toString());
        log.error(posToken.pos,line,msg,this.toString().length());
    }
/*
    public void error(String msg)
    {
        log.error(posToken.pos,line,msg,this.toString().length());
    }
    */
    /** 用当前语法树的位置和行号以及代码加格式化参数参数进行报错 */
    public void error(String key,String msgContent)
    {
        error(posToken,key,msgContent);
       /* String msgf = CompileMessagesUtil.getMsg(key);
        String msg= String.format(msgf,msgContent);
        log.error(posToken.pos,line,msg,msgContent.length());*/
    }
    /*
    public void error(String formatter,String key)
    {
        log.error(posToken.pos,line,String.format(formatter,key),key.length());
    }*/
    /** 用某词法标记的信息以及提示进行报错 */
    public void error(Token token,  String key,String msgContent)
    {
        //log.error(token,String.format(formatter,key));
      //  String msgf = CompileMessagesUtil.getMsg(key);
        //String msg= String.format(msgf,msgContent);
        log.error(token,key,msgContent);
    }

   /* public void error(Token token, String formatter,String key)
    {
        //log.error(token,String.format(formatter,key));
        log.error(token,formatter,key);
    }*/

    final String _treeId ;
    protected JCTree()
    {
        _treeId = IdGenerator.get("JCTree");
    }
    /** 用 TreePretty类生成格式化代码字符串 */
    @Override
    public String toString() {
        StringWriter s = new StringWriter();
        new TreePretty(s).printTree(this);
        return s.toString();
    }

    /** TreeScanner分析  */
    public abstract <D> void scan(TreeScanner<D> v, D arg);
    /* TreeTranslator转换当前语法树得到一个新的树 */
    public abstract <D> JCTree translate(TreeTranslator<D> v, D arg);

    public BelongsInfo belongsInfo;

    public TreePropertys propertys = new TreePropertys();

    public void setIsMacroCallArg(boolean isMacroCallArg)
    {
        propertys.isMacroCallArg =isMacroCallArg;
    }

    public void setIsLambdaBodyOne(boolean isLambdaBodyOne)
    {
        propertys.isLambdaBodyOne =isLambdaBodyOne;
    }

    public void  isAnalyzered(boolean v)
    {
      propertys. _isAnalyzered =v;
    }
}
