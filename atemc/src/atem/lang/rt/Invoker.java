package atem.lang.rt;

public interface Invoker {
    Object invoke( Object[] args)  throws Exception;
    /* 根据参数个数判断能否运行,如果能，则运行 */
    @Deprecated
    TryInvokeResult tryInvoke(Object[] args)  throws Exception;
}
