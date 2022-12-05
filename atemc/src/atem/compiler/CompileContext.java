package atem.compiler;

import atem.compiler.symbols.FileSymbol;
import atem.compiler.tools.ListUtil;
import atem.compiler.tools.runs.JarClassLoadUtil;
import atem.compiler.utils.CompileError;
import atem.compiler.utils.SimpleLog;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class CompileContext {
    private String outPath="out";
    private String[] libPaths;
    public String[] sourcesFiles ;

    public void setLibPaths( String[] libPaths)
    {
        this.libPaths=libPaths;
        for(String cp:libPaths)
        {
            File folder = new File(cp);
            if(folder.exists()==false)
            {
                throw new CompileError("类库路径不存在:"+cp);
            }
        }
    }

    public String[] getLibPaths()
    {
        return libPaths;
    }

    public void setOutPath( String savePath)
    {
        this.outPath=savePath;
        if(this.outPath==null || this.outPath=="")
            this.outPath="out";
        while (this.outPath.endsWith("\""))
        {
            this.outPath=this.outPath.substring(0,this.outPath.length()-1);
        }
            File folder = new File(outPath);
            if(folder.exists()==false)
            {
                folder.mkdir();
            }
    }

    public String getOutPath()
    {
        return outPath;
    }

    public int errors = 0;
    //public int warnings = 0;

    public SimpleLog log = new SimpleLog(this);

    public ArrayList<URLClassLoader> urlClassLoaders;

    public ArrayList<FileSymbol> compiledFileSymbols = new ArrayList<>();

    public String toConfigsString()
    {
        var buff = new StringBuilder();
        buff.append(" outPath:"+outPath);
        buff.append(" LibPaths:"+ ListUtil.join(libPaths,","));
        buff.append(" sourcesFiles:"+ ListUtil.join(sourcesFiles,","));
        return buff.toString();
    }
}
