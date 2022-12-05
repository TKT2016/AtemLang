package AtemIDE.executor;

import AtemIDE.IDEConsts;
import com.kodedu.terminalfx.TerminalTab;

import java.io.File;
import java.io.IOException;

public class AtemExecutor implements ISourceExecutor{
    private String compileMsg="";
    private String codeResult="";

    public  void executeFile(File src, OnExecuteListener listener , TerminalTab terminal ) {
        try {
            int compileState = codeCompile_OK(src,terminal);
            listener.onExecutorSuccess(compileMsg);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private int codeCompile_OK(File srcFile , TerminalTab terminal ) throws InterruptedException, IOException {
        String srcFileName = srcFile.getAbsolutePath();
        String out = srcFile.getParentFile().getAbsolutePath()+"\\out\\";
        var cmd = CompileCmdBuilder.getCompileArgsCmd(srcFileName,out);

        var fullCmd = "java "+cmd+"\r";
       // System.out.println( "cmd:"+fullCmd);
        try {
            terminal.getTerminal().command("cls\r");
        }catch (Exception ex)
        {
            System.err.println("系统不支持cls命令");
        }
        terminal.getTerminal().command(fullCmd);
        return 1;
    }
/*
    public static void main(String[] args) throws Exception {
        File srcFile = new File("");
        File javaCodePath = srcFile.getParentFile();
        String srcFileName = srcFile.getAbsolutePath();
        String out = srcFile.getParentFile().toString()+"\\out\\";
        var cmd = CompileCmdBuilder.getCompileArgsCmd(srcFileName,out);

        ProcessBuilder pb = new ProcessBuilder("java", cmd);
        pb.redirectError();

        pb.directory(javaCodePath);

        Process p = pb.start();
        StreamConsumer consumer = new StreamConsumer(p.getInputStream(),p.getErrorStream());
        consumer.start();
        int result = p.waitFor();
        consumer.join();
        var codeResult = consumer.getCodeResult();
        System.out.println(codeResult);
    }*/
}
