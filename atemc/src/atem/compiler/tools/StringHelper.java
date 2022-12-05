package atem.compiler.tools;

public class StringHelper {

    public static boolean isNullOrEmpty(String str)
    {
        if(str==null) return true;
        if(str.isEmpty()) return true;
        return false;
    }

    public static int getNewLineCharPosForward(String str,int pos)
    {
        int i = pos;
        while (true)
        {
            if(i<=0)
                break;
            if(isNewLineChar( str.charAt(i)))
                break;
            i--;
        }
        return i;
    }

    public static int getNewLineCharPosBackward(String str,int pos)
    {
        int i=pos;
        int max = str.length();
        while (true)
        {
            if(i>=max-1)
                break;
            if(isNewLineChar( str.charAt(i)))
                break;
            i++;
        }
        return i;
    }

    public static boolean isNewLineChar(char ch)
    {
        return (ch=='\n'||ch=='\r');
    }
}
