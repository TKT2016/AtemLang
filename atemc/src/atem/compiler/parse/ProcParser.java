package atem.compiler.parse;

import atem.compiler.lex.Token;
import atem.compiler.lex.TokenKind;
import atem.compiler.ast.JCBlock;
import atem.compiler.ast.callables.JCMacroDecl;
import atem.compiler.ast.callables.proc.ProcItem;
import atem.compiler.ast.callables.proc.ProcItemParameter;
import atem.compiler.ast.callables.proc.ProcItemText;

import java.util.ArrayList;

import static atem.compiler.lex.TokenKind.EOF;

public class ProcParser {
    Parser parser;
    public ProcParser(Parser parser)
    {
        this.parser = parser;
    }

    public JCMacroDecl parse()
    {
        Token posToken  = parser.token;
        parser.nextToken();
        if(parser.kind!= TokenKind.IDENTIFIER)
        {
            parser.error(parser.token,"macro的第一项必须是标识符");
        }
        ArrayList<ProcItem> items = new ArrayList<>();
        while (parser.kind!=EOF)
        {
            if(parser.kind== TokenKind.IDENTIFIER)
            {
                ProcItemText metaItemText = new ProcItemText(parser.token);
                items.add(metaItemText);
                parser. nextToken();
            }
            else  if(parser.kind== TokenKind.LPAREN)
            {
                parser.nextToken();
                if(parser.kind== TokenKind.IDENTIFIER)
                {
                    ProcItemParameter metaItemParameter = new ProcItemParameter(parser.token);
                    parser.maker.initTree(metaItemParameter);
                    items.add(metaItemParameter);
                    parser.nextToken();
                }
                parser.accept(TokenKind.RPAREN);
            }
            else if(parser.kind==TokenKind.LBRACE)
                break;
            else
            {
                parser.error(parser.token,"错误的宏定义");
                parser.nextToken();
            }
        }
        /* 函数体 */
        JCBlock body = parser.block();
        /* 生成定义函数语法树 */
        return parser.maker.at(posToken).macroDecl( items , body);
    }
}
