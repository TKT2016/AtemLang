package AtemIDE.configs;

public class IDEConfigModel {
    private String exampleFolder;

    private String compilerJarPath;

    private String dLibPath;

    public String getExampleFolder()
    {
        return exampleFolder;
    }

    public void setExampleFolder(String exampleFolder)
    {
        this.exampleFolder=exampleFolder;
    }

    public String getCompilerJarPath()
    {
        return compilerJarPath;
    }

    public void setCompilerJarPath(String compilerJarPath)
    {
        this.compilerJarPath=compilerJarPath;
    }

    public String getDLibPath()
    {
        return dLibPath;
    }

    public void setDLibPath(String dLibPath)
    {
        this.dLibPath=dLibPath;
    }
}
