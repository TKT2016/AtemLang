package AtemIDE;

import AtemIDE.configs.IDEConfigModelReader;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import AtemIDE.configs.IDEConfigModel;
public final class IDEConsts {
    //public static String evalClassDir = "D:\\WHZProjects\\JavaProjects\\AtemScript\\atemc\\target\\classes\\";
    //public static  String javaCodeName ="atemscript.compiler.AtemEval";

    public static final int APP_WIDTH = 900;
    public static final int APP_HEIGHT = 700;

    public static final String APP_NAME = "Atem Editor (beta , has some bugs)";
    public static final Image APP_ICON = new Image(ApplicationMain.class.getResourceAsStream("/AtemIDE/res/icons/main/astro_icon.png"));

    public static  String getExampleFolder()
    {
        return getIdeConfigModel().getExampleFolder();
    }

    public static  String getCompilerJarPath()
    {
        return getIdeConfigModel().getCompilerJarPath();
    }

    public static  String getDLibPath()
    {
        return getIdeConfigModel().getDLibPath();
    }

    private static IDEConfigModel ideConfigModel;

    public static String ideConfigModelFileName="AtemIDE.configs.json";

    public static IDEConfigModel getIdeConfigModel()
    {
        if(ideConfigModel==null)
        {
            try {
                ideConfigModel = IDEConfigModelReader.read();
            }
            catch (Exception ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Config Errror");
                String s = "read config error :"+ex.getMessage();
                alert.setContentText(s);
                alert.showAndWait();

                ideConfigModel = IDEConfigModelReader.createDefaulConfigModel();
            }
        }
        return ideConfigModel;
    }
}
