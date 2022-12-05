package AtemIDE.controller;

import AtemIDE.constants.Icons;
import AtemIDE.model.Source;
import AtemIDE.utils.DialogUtils;
import AtemIDE.utils.ImageUtils;
import com.google.common.io.Files;
import javafx.application.Platform;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class MainControlTool {

    MainController mainController;

    public MainControlTool(  MainController mainController)
    {
        this.mainController = mainController;
    }

    Tab createNewTab(File sourceFile)
    {
        Tab newTab = new Tab(sourceFile.getName());
        newTab.setUserData(sourceFile.getPath());
        newTab.setOnClosed(event -> mainController. onTabCloseAction(newTab));
        newTab.setGraphic(ImageUtils.buildImageView(Icons.codeIconImage));
        return newTab;
    }

    private boolean reOpenTab(File sourceFile) {
        var codeAreaLayout = mainController.codeAreaLayout;
        SingleSelectionModel<Tab> selectionModel = codeAreaLayout.getSelectionModel();
        //ObservableList<Source> selectedItems = openedFilesList.getSelectionModel().getSelectedItems();

        final int currentTabNumber = codeAreaLayout.getTabs().size();
        for (int i = 0; i < currentTabNumber; i++) {
            if (sourceFile.getName().equals(codeAreaLayout.getTabs().get(i).getText())) {
                selectionModel.select(i);
                return true;
            }
        }
        return false;
    }

    void openSourceInTab(File sourceFile, Image iconImage,boolean isCreateEditorController) {
        if(reOpenTab(sourceFile))
            return;
        Tab javaTab = createNewTab(sourceFile);
        javaTab.setGraphic(ImageUtils.buildImageView(iconImage));

        CodeArea codeTextArea = new CodeArea();
        EditorController editorController = null;
        if(isCreateEditorController) {
            editorController = new EditorController(codeTextArea, mainController.resultTextArea,mainController.terminal,mainController);
            editorController.editorSettings();
        }

        try {
            StringBuilder code = new StringBuilder();
            Files.readLines(sourceFile, Charset.defaultCharset()).forEach(s -> code.append(s).append("\n"));
            codeTextArea.replaceText(0, 0, code.toString());
            javaTab.setContent(new VirtualizedScrollPane<>(codeTextArea));
            
            //Update On UI Thread
            Platform.runLater(() -> {
                var  codeAreaLayout = mainController.codeAreaLayout;
                codeAreaLayout.getTabs().add(javaTab);
                codeAreaLayout.getSelectionModel().select(javaTab);
                var openedFilesList = mainController.openedFilesList;
                openedFilesList.getItems().add(new Source(sourceFile));
            });
            if(isCreateEditorController)
                editorController.updateSourceFile(sourceFile);
            mainController.currentSourceFile = sourceFile;
        } catch (IOException e) {
            String warnMessage = "Can't Open File in Tab pane";
            //Debugging warning
            mainController.  debugger.warning(warnMessage);
            //UI warning
            DialogUtils.createWarningDialog(DialogUtils.WARNING_DIALOG, null, warnMessage);
        }
    }

    /*
    EventHandler<ActionEvent> runAction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent)
        {
            var sourceFile = mainController.currentSourceFile;
            CodeExecutor.execute(sourceFile,resultTextArea,terminal);
        }
    }*/


}
