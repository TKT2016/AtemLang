package atem.lang.rt;

public class LocalVarRef implements VarRef {
    private final Object[] lambdaVarsLocal;
    private  final int adr_lambda;

    public LocalVarRef(Object[] lambdaVarsLocal, int adr_lambda)
    {
        this.lambdaVarsLocal=lambdaVarsLocal;
        this.adr_lambda =adr_lambda;
    }

    public Object getValue()
    {
        return lambdaVarsLocal[adr_lambda];
    }

    public void setValue(Object value)
    {
        lambdaVarsLocal[adr_lambda]=value;
    }
}
