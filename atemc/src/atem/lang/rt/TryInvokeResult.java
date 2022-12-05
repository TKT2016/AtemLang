package atem.lang.rt;

public class TryInvokeResult {
    public boolean success;
    public Object result;

    public TryInvokeResult(boolean success,Object result)
    {
        this.success=success;
        this.result=result;
    }

    public TryInvokeResult(Object result)
    {
        this.success=true;
        this.result=result;
    }

    public TryInvokeResult( )
    {
        this.success=false;
        this.result=null;
    }
}
