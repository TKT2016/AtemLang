package atem.compiler.ast;

import atem.compiler.lex.Token;
import atem.compiler.symbols.SymbolScope;

import java.util.ArrayList;

/** 代码块语句 */
public class JCBlock extends JCStatement
{
    /** 语句块内的语句 */
    public ArrayList<JCStatement> statements;

    public JCBlock(Token posToken)
    {
        this.posToken = posToken;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitBlock(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateBlock(this, arg);
    }
    /** 是否是for循环的循环体 */
    public BodyKind bodyKind = BodyKind.NOMORL;

    public static enum BodyKind
    {
       // ForLoopBody,
        FunctionBody,
        NOMORL,
    }
}