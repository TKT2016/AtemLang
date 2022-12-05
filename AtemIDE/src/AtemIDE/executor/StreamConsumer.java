package AtemIDE.executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamConsumer extends Thread {

    private InputStream mInputStream;
    private InputStream errorStream;

    private IOException mExceptionIO;
    private StringBuilder mOutputBuilder;
    private StringBuilder errorBuilder;

    public StreamConsumer(InputStream stream,InputStream errorStream) {
        mInputStream = stream;
        this.errorStream=errorStream;
    }

    public String getCodeResult() {
        return errorBuilder.toString()+""+ mOutputBuilder.toString();
    }

    public IOException getCodeException() {
        return mExceptionIO;
    }

    public boolean hasException() {
        return !(mExceptionIO == null);
    }

    @Override
    public void run() {
        mOutputBuilder = new StringBuilder();
        errorBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream,"UTF-8"));
            int input;
            while ((input = bufferedReader.read()) != -1) {
                errorBuilder.append((char) input);
            }
            bufferedReader = new BufferedReader(new InputStreamReader(mInputStream,"UTF-8"));
            while ((input = bufferedReader.read()) != -1) {
                mOutputBuilder.append((char) input);
            }
        } catch (IOException ex) {
            mExceptionIO = ex;
        }
    }
}
