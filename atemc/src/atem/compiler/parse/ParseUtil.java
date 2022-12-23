package atem.compiler.parse;

import atem.compiler.lex.TokenKind;
import atem.compiler.utils.CompileError;
import atem.compiler.utils.msgresources.CompileMessagesUtil;

class ParseUtil {

    static Object parseTokenKindValue(Parser parser)
    {
        switch (parser.kind) {
            case STRINGLITERAL:
                return parser.token.valueString;
            case TRUE:
                return  true;
            case FALSE:
                return false;
            case INTLITERAL: {
                String valStr = parser.token.valueString;
                try {
                    return Integer.parseInt(valStr);
                } catch (NumberFormatException ex) {
                    parser.log.error(parser.token, CompileMessagesUtil.IntegerValueError, valStr);//parser.log.error(parser.token,"整数数值错误或者过大");
                    return 0;
                }
            }
            case FLOATLITERAL: {
                String valStr = parser.token.valueString;
                try {
                    return Float.parseFloat(valStr);
                } catch (NumberFormatException ex) {
                    parser.log.error(parser.token, CompileMessagesUtil.FloatValueError, valStr);//  parser.log.error(parser.token,"float数值错误或者过大");
                    return 0;
                }
            }
            case NULL:
                return null;
            default:
                throw new CompileError();
        }
    }
}
