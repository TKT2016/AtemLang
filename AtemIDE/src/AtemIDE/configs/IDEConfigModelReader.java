package AtemIDE.configs;

import AtemIDE.IDEConsts;
import AtemIDE.utils.FileUtil;
import AtemIDE.utils.JsonUtil;

import java.io.File;
import java.io.IOException;

public class IDEConfigModelReader {


    public static IDEConfigModel read() throws IOException
    {
        File file = new File(IDEConsts.ideConfigModelFileName);
        if(file.exists()==false)
        {
            //file.createNewFile();
            IDEConfigModel configModel = createDefaulConfigModel();
            var jsonOption = JsonUtil.toJsonString(configModel,true);
            var json  = jsonOption.get();
            FileUtil.saveText( file.getAbsolutePath(), json);
            return configModel;
        }
        else
        {
            String json = FileUtil.readText(file.getAbsolutePath());
            var model = JsonUtil.parse(json,IDEConfigModel.class);
            return model.get();
        }
    }

    public  static IDEConfigModel createDefaulConfigModel()
    {
        IDEConfigModel configModel = new IDEConfigModel();
        configModel.setCompilerJarPath("IDERes/AtemCompiler_jar/AtemCompiler.jar");
        configModel.setDLibPath("IDERes/dlibjar/");
        configModel.setExampleFolder("IDERes/stdsamples/");
        return configModel;
    }
}
