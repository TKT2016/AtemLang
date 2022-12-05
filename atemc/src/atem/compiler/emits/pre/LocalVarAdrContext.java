package atem.compiler.emits.pre;

/** 变量地址计算上下文参数 */
class LocalVarAdrContext
{
    public int adr;//局部变量地址
    public int adr_lambda;//局部变量地址

    public LocalVarAdrContext(int adr,int adr_lambda)
    {
        this.adr = adr;
        this.adr_lambda =adr_lambda;
    }
}
