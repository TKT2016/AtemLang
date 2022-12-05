package AtemIDE;

import AtemIDE.controller.MainController;
import AtemIDE.syntax.Keywords;
import com.kodedu.terminalfx.helper.ThreadHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

public class ApplicationMain extends Application {
    public static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/main_view.fxml"));
        Scene scene = new Scene(root, IDEConsts.APP_WIDTH, IDEConsts.APP_HEIGHT);
        addCSS(scene, "/AtemIDE/styles/editor_style_light.css");
     //   addCSS(scene,"/AtemIDE/styles/tab_pane_style.css");
      //  addCSS(scene,"/AtemIDE/styles/menu_style.css");
       // addCSS(scene,"/AtemIDE/styles/result_area_style.css");
       // addCSS(scene,"/AtemIDE/styles/list_style.css");

        primaryStage.getIcons().add(IDEConsts.APP_ICON);
        primaryStage.setTitle(IDEConsts.APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();

        Keywords.onKeywordsBind();
        mainStage = primaryStage;
        File exampleFolder = new File(IDEConsts.getExampleFolder());
        if(exampleFolder.exists())
            MainController.getInstance().updateFilesTreeView(exampleFolder);
    }

    @Override
    public void stop() throws Exception {
        ThreadHelper.stopExecutorService();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

    static void addCSS( Scene scene,String res)
    {
        var url = ResourceLoader.getResource(res);
        scene.getStylesheets().add(url.toString());
    }
}
