package AtemIDE.executor;

import AtemIDE.constants.Extension;
import com.kodedu.terminalfx.TerminalTab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

import java.io.File;
import java.util.Objects;

public class CodeExecutor {

    public static  ISourceExecutor getSourceExecutor(File source)
    {
        ISourceExecutor sourceExecutor = null;
        if(source.getName().toLowerCase().endsWith(Extension.JAVA))
            sourceExecutor = new JavaExecutor();
        else  if(source.getName().toLowerCase().endsWith(Extension.ATEM))
            sourceExecutor = new AtemExecutor();
        return sourceExecutor;
    }

    public static void execute(File source, TextArea outputArea , TerminalTab terminal ) {
        if (Objects.nonNull(source)) {
            final ISourceExecutor sourceExecutor = getSourceExecutor(source);

            Thread executeThread = new Thread(() -> {
                ExecuteListener executeListener = new ExecuteListener(outputArea,terminal);
                sourceExecutor.executeFile(source,executeListener,terminal);
            });
            executeThread.setPriority(Thread.MAX_PRIORITY);
            executeThread.start();
        }
    }


    public static void execute(File source, TextArea outputArea , TerminalTab terminal, TabPane bottomTabPane) {
        if (Objects.nonNull(source)) {
            bottomTabPane.getSelectionModel().select(1);
            final ISourceExecutor sourceExecutor = getSourceExecutor(source);

            Thread executeThread = new Thread(() -> {
                ExecuteListener executeListener = new ExecuteListener(outputArea,terminal);
                sourceExecutor.executeFile(source,executeListener,terminal);
            });
            executeThread.setPriority(Thread.MAX_PRIORITY);
            executeThread.start();
        }
    }
}
