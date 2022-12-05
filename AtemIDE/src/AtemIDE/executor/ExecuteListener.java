package AtemIDE.executor;

import AtemIDE.utils.DateUtils;
import com.kodedu.terminalfx.TerminalTab;
import javafx.scene.control.TextArea;

class ExecuteListener implements OnExecuteListener{
    TextArea outputArea;
    TerminalTab terminal;
    public ExecuteListener( TextArea outputArea, TerminalTab terminal)
    {
        this.outputArea = outputArea;
        this.terminal = terminal;
    }

    @Override
    public void onCompileFailure(String msg) {
        String message = "Compile is Failure\n" + DateUtils.getCurrentDate()+ "\n".concat(msg);
        outputArea.setText(message);
    }

    @Override
    public void onExecutorFailure(String msg) {
        String message = "Build is Failure\n" + DateUtils.getCurrentDate()+ "\n".concat(msg);
        outputArea.setText(message);
    }

    @Override
    public void onExecutorSuccess(String output) {
        String message =// "Build is Success\n" +
                DateUtils.getCurrentDate() + "\n".concat(output==null?"":output);
        outputArea.setText(message);
    }
}
