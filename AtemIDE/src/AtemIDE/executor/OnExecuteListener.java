package AtemIDE.executor;

public interface OnExecuteListener {

    void onCompileFailure(String msg);

    void onExecutorFailure(String msg);

    void onExecutorSuccess(String result);

}
