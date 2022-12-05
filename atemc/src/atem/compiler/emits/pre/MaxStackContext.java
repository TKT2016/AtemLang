package atem.compiler.emits.pre;

import atem.compiler.ast.callables.ICallableAST;

/** 变量地址计算上下文参数 */
class MaxStackContext
{
    public int result;

       ICallableAST callableAST;

    public MaxStackContext(ICallableAST  callableAST  )
    {
        this.callableAST = callableAST;
    }

    public void seStack(int count)
    {
        result = count;
        callableAST.getCallableCinfo().setMaxStack(count);
    }

    public MaxStackContext clone()
    {
        MaxStackContext context = new MaxStackContext(this.callableAST);

        context.result = this.result;
        return context;
    }

}
