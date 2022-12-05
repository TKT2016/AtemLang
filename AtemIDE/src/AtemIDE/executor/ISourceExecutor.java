package AtemIDE.executor;

import com.kodedu.terminalfx.TerminalTab;

import java.io.File;

public interface ISourceExecutor {
    public void executeFile(File javaCode, OnExecuteListener listener, TerminalTab terminal);
}
