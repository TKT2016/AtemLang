package atem.compiler.tools;

public class IdGenerator {
    static int index=0;

    public static String get()
    {
        return get("Id");
    }

    public static String get(String prefix)
    {
        index++;
        return prefix+"_"+index;
    }

    static int lambda_index=0;
    public static String getLambdaId(   )
    {
        lambda_index++;
        return  "Lambda_"+lambda_index;
    }
}
