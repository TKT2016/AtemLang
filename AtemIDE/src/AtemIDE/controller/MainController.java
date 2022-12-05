package AtemIDE.controller;

import AtemIDE.analysis.SyntaxAnalysis;
import AtemIDE.constants.Extension;
import AtemIDE.constants.Icons;
import AtemIDE.executor.CodeExecutor;
import AtemIDE.model.Source;
import AtemIDE.service.FileService;
import AtemIDE.service.ProjectWatcher;
import AtemIDE.utils.DialogUtils;
import AtemIDE.utils.*;
import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import org.fxmisc.richtext.CodeArea;

import java.io.File;

import java.net.URL;

import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    File currentSourceFile;
    //FX Views
    @FXML  TabPane  bottomTabPane;
    @FXML  TextArea resultTextArea;
    @FXML  TabPane codeAreaLayout;
    @FXML private SplitPane mainSplitPane;
    @FXML private SplitPane codeSplitPane;

    //FX Views Group
    //@FXML private ComboBox<String> tasksComboBox;
    //@FXML private ListView<Task> tasksListView;
    @FXML  ListView<Source> openedFilesList;

    @FXML private TreeView<Source> filesTreeView;
    //@FXML private TreeView<String> analysisTreeView;

    //New Menu Items
    @FXML private MenuItem newFileMenuItem;
    //@FXML private MenuItem newClassMenuItem1;
    //@FXML private MenuItem newProjectMenuItem;

    //File Menu Items
    @FXML private MenuItem openFileMenuItem;
    @FXML private MenuItem openFolderMenuItem;
    @FXML private MenuItem closeMenuItem;
    @FXML private MenuItem exitMenuItem;

    //View Menu Items
    @FXML private MenuItem showFilesMenuAction;
    @FXML private MenuItem showResultMenuAction;
    @FXML private MenuItem runFilesMenuAction;

    TerminalBuilder terminalBuilder;
    TerminalTab terminal;
    //Controllers
    @FXML static MainController mainController;

    private CodeArea currentCodeArea;

     static Logger debugger;
    private ProjectWatcher projectWatcherService;
    private SyntaxAnalysis syntaxAnalysis;
    private ExecutorService executorService;

    private String currentTaskType = TODO_COMMENT;

    private boolean isFilesPaneVisible = true;
    private boolean isResultPaneVisible = true;

    private static final int FILE_PANE_INDEX = 0;
    private static final int RESULT_PANE_INDEX = 0;
    private static final int NOT_FOUND_INDEX = -1;
    private static final String TODO_COMMENT = "TODO";
    private static final String WARN_COMMENT = "WARN";
    private static final String DEBUG_TAG = MainController.class.getSimpleName();
    private static final int THREAD_AVAILABLE_NUMBER = Runtime.getRuntime().availableProcessors();
    private final Image PROJECT_DIR = new Image(getClass().getResourceAsStream("/AtemIDE/res/icons/folder/main_folder.png"));

    MainControlTool mainControlTool = new MainControlTool(this);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainController = this;
        executorService = Executors.newFixedThreadPool(THREAD_AVAILABLE_NUMBER);
        debugger = Logger.getLogger(DEBUG_TAG);

        //comboBoxSettings();
        onMenuItemsActions();
        openedListSettings();

        resultTextArea.setEditable(false);

        codeAreaLayout.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        codeAreaLayout.setOnDragDropped(this::onCodeLayoutDragDropped);
        codeAreaLayout.setOnDragOver(this::onCodeLayoutDragOver);
        codeAreaLayout.getSelectionModel().selectedItemProperty().addListener(onTabSelectChangeListener);

        filesTreeView.getSelectionModel().selectedItemProperty().addListener(onFileSelectChangeListener);

        terminalBuilder = new TerminalBuilder();
        terminal = terminalBuilder.newTerminal();
        terminal.setText("Terminal");
        bottomTabPane.getTabs().add(terminal);
    }
/*
    private void comboBoxSettings(){
        tasksComboBox.getItems().addAll(TODO_COMMENT,WARN_COMMENT);
        tasksComboBox.getSelectionModel().select(0);
        tasksComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentTaskType = newValue;
            bindTasksInList();
        });
    }*/

    private void openedListSettings() {
        openedFilesList.setEditable(false);
       openedFilesList.setOnMouseClicked(event -> onListItemClicked());
    }

    private void onMenuItemsActions() {
        //File Menu
        openFileMenuItem.setOnAction(event -> onOpenFileMenuAction());
        openFolderMenuItem.setOnAction(event -> onOpenFolderMenuAction());
        closeMenuItem.setOnAction(event -> onCloseMenuAction());
        exitMenuItem.setOnAction(event -> onExitMenuAction());

        //New SubMenu
        newFileMenuItem.setOnAction(event -> showNewFileDialog());
        //newClassMenuItem1.setOnAction(event -> showNewClassDialog());
        //newProjectMenuItem.setOnAction(event -> showProjectFileDialog());

        //View Menu
        showFilesMenuAction.setOnAction(event -> onShowFilesMenuAction());
        showResultMenuAction.setOnAction(event -> onShowResultMenuAction());
        runFilesMenuAction.setOnAction(e ->
                {
                    CodeExecutor.execute(currentSourceFile,resultTextArea,terminal,this.bottomTabPane);
                });
    }

    private void showNewFileDialog() {
        if(openSourceFolder!=null)  //if (Objects.nonNull(filesTreeView.getTreeItem(0)))
        {
            //int directoryIndex = filesTreeView.getSelectionModel().getSelectedIndex();
          //  if (directoryIndex != NOT_FOUND_INDEX) {
                //TreeItem<Source> directory = filesTreeView.getSelectionModel().getSelectedItem();
             //   Source dirSource = directory.getValue();
               // File dirFile = dirSource.getFile();
            File dirFile = openSourceFolder;
                String dirPath = "";
                if (dirFile.isDirectory() && dirFile.canWrite()) {
                    dirPath = dirFile.getPath();
                } else if (dirFile.getParentFile().canWrite()) {
                    dirPath = dirFile.getParentFile().getPath();
                }
                Intent intent = Intent.getIntent();
                intent.addStringValue("CLASS_PATH", dirPath);
                String title = "New File";
                String viewPath = "/AtemIDE/views/new_file.fxml";
                String stylePath = "/AtemIDE/styles/new_create_style.css";
                intent.showAnotherView(viewPath, title, stylePath);
          //  }
           /* else {
                String warnMessage = "Please Select Directory First";
                //Debugging warning
                debugger.warning(warnMessage);
                //UI warning
                DialogUtils.createWarningDialog(DialogUtils.WARNING_DIALOG, null, warnMessage);
            }*/
        }
        else {
            String warnMessage = "Please Create or Open Project First";
            //Debugging warning
            debugger.warning(warnMessage);
            //UI warning
            DialogUtils.createWarningDialog(DialogUtils.WARNING_DIALOG, null, warnMessage);
        }
    }
/*
    private void showNewClassDialog() {
        if (Objects.nonNull(filesTreeView.getTreeItem(0))) {
            int directoryIndex = filesTreeView.getSelectionModel().getSelectedIndex();
            if (directoryIndex != NOT_FOUND_INDEX) {
                TreeItem<Source> directory = filesTreeView.getSelectionModel().getSelectedItem();
                Source dirSource = directory.getValue();
                File dirFile = dirSource.getFile();
                String dirPath = "";
                if (dirFile.isDirectory() && dirFile.canWrite()) {
                    dirPath = dirFile.getPath();
                } else if (dirFile.getParentFile().canWrite()) {
                    dirPath = dirFile.getParentFile().getPath();
                }
                Intent intent = Intent.getIntent();
                intent.addStringValue("CLASS_PATH", dirPath);
                String title = "New Java Class";
                String viewPath = "/AtemIDE/views/new_class.fxml";
                String stylePath = "/AtemIDE/styles/new_create_style.css";
                intent.showAnotherView(viewPath, title, stylePath);
            } else {
                String warnMessage = "Please Select Directory First";
                //Debugging warning
                debugger.warning(warnMessage);
                //UI warning
                DialogUtils.createWarningDialog(DialogUtils.WARNING_DIALOG, null, warnMessage);
            }
        } else {
            String warnMessage = "Please Create or Open Project First";
            //Debugging warning
            debugger.warning(warnMessage);
            //UI warning
            DialogUtils.createWarningDialog(DialogUtils.WARNING_DIALOG, null, warnMessage);
        }
    }*/
/*
    private void showProjectFileDialog() {
        String title = "Create New Project";
        String viewPath = "/AtemIDE/views/new_project.fxml";
        String stylePath = "/AtemIDE/styles/new_create_style.css";

        Intent intent = Intent.getIntent();
        intent.showAnotherView(viewPath, title, stylePath);
    }*/

    //TODO : Make Class to make easy to support all types of files extension
    private void onCodeLayoutDragDropped(DragEvent event) {
        List<File> currentDropped = event.getDragboard().getFiles();
        List<Source> sourceStream = openedFilesList.getItems()
                .parallelStream()
                .collect(Collectors.toList());
        for (File file : currentDropped) {
            if (sourceStream.indexOf(new Source(file)) == NOT_FOUND_INDEX) {
                String fileName = file.getName();
                if (fileName.endsWith(Extension.JAVA))
                    executorService.execute(() -> openSourceInTab(file));
                else if (fileName.endsWith(Extension.TEXT))
                    executorService.execute(() -> openTextInTab(file));
                else if (fileName.endsWith(Extension.MD))
                    executorService.execute(() -> openTextInTab(file));
            }
        }
    }

    private void onCodeLayoutDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }

    //TODO : Make Class to make easy to support all types of files extension
    private void onOpenFileMenuAction() {
        File outputFile = FileManager.openSourceFile("Open Java File");
        if (outputFile != null)
            if (outputFile.getName().endsWith(Extension.JAVA))
                executorService.execute(() -> openSourceInTab(outputFile));
            else if (outputFile.getName().endsWith(Extension.TEXT))
                executorService.execute(() -> openTextInTab(outputFile));
            else if (outputFile.getName().endsWith(Extension.MD))
                executorService.execute(() -> openTextInTab(outputFile));
    }


    private void onOpenFolderMenuAction() {
       var  openSourceFolder1 = FileManager.openSourceDir("Open Project Folder");
        if (Objects.nonNull(openSourceFolder1))
            updateFilesTreeView(openSourceFolder1);
    }

    private void onCloseMenuAction() {
        openedFilesList.getItems().clear();
        codeAreaLayout.getTabs().clear();
        resultTextArea.clear();
        filesTreeView.setRoot(null);
        if (Objects.nonNull(projectWatcherService)) projectWatcherService.stopWatcher();
    }

    private void onExitMenuAction() {
        Platform.exit();
        System.exit(0);
    }

    //TODO : Issue when User Change Astro IDE Size
    private void onShowFilesMenuAction() {
        if (isFilesPaneVisible) {
            mainSplitPane.setDividerPosition(FILE_PANE_INDEX, 0);
            showResultMenuAction.setText("Show Output");
            isFilesPaneVisible = false;
        } else {
            mainSplitPane.setDividerPosition(FILE_PANE_INDEX, .18);
            showResultMenuAction.setText("Hide Files");
            isFilesPaneVisible = true;
        }
    }

    //TODO : Issue when User Change Astro IDE Size
    private void onShowResultMenuAction() {
        if (isResultPaneVisible) {
            codeSplitPane.setDividerPosition(RESULT_PANE_INDEX, 1);
            showResultMenuAction.setText("Show Output");
            isResultPaneVisible = false;
        } else {
            codeSplitPane.setDividerPosition(RESULT_PANE_INDEX, .82);
            showResultMenuAction.setText("Hide Output");
            isResultPaneVisible = true;
        }
    }

    private void onListItemClicked() {
        SingleSelectionModel<Tab> selectionModel = codeAreaLayout.getSelectionModel();
        ObservableList<Source> selectedItems = openedFilesList.getSelectionModel().getSelectedItems();
        Source sourceFile = selectedItems.get(0);
        final int currentTabNumber = codeAreaLayout.getTabs().size();
        for (int i = 0; i < currentTabNumber; i++) {
            if (sourceFile.getName().equals(codeAreaLayout.getTabs().get(i).getText())) {
                selectionModel.select(i);
            }
        }
    }

    private void openSourceInTab(File sourceFile) {
        mainControlTool.openSourceInTab(sourceFile,Icons.codeIconImage,true);
    }

    private void openAtemScriptInTab(File sourceFile) {
        mainControlTool.openSourceInTab(sourceFile,Icons.codeIconImage,true);
    }

    private void openTextInTab(File textFile) {
        mainControlTool.openSourceInTab(textFile,Icons.codeIconImage,false);
    }

    void onTabCloseAction(Tab tab) {
        String tabName = tab.getText();
        ObservableList<Source> sourceItems = openedFilesList.getItems();
        int fileListItemCount = sourceItems.size();
        for (int i = 0; i < fileListItemCount; i++) {
            if (sourceItems.get(i).getName().equals(tabName)) {
                sourceItems.remove(i);
                openedFilesList.refresh();
                break;
            }
        }
        boolean isJavaFile = tabName.endsWith(Extension.JAVA);
        if (isJavaFile) {
            CodeArea codeArea = (CodeArea) ((Parent) tab.getContent()).getChildrenUnmodifiable().get(0);
            codeArea.displaceCaret(0);
        }
    }
/*
    private void bindTasksInList(){
        tasksListView.getItems().clear();
        String code = currentCodeArea.getText();
        if (currentTaskType.equals(TODO_COMMENT)) {
            Stream<Task> tasks = TaskMatcher.getTodoTasks(code);
            tasks.forEach(task -> tasksListView.getItems().add(task));
        } else if (currentTaskType.equals(WARN_COMMENT)) {
            Stream<Task> tasks = TaskMatcher.getWarnTasks(code);
            tasks.forEach(task -> tasksListView.getItems().add(task));
        }
    }*/

    private ChangeListener<Tab> onTabSelectChangeListener = (observable, oldValue, newValue) -> {
        if (Objects.nonNull(newValue)) {
            if (newValue.getText().endsWith(Extension.JAVA)) {
                currentCodeArea = (CodeArea) ((Parent) newValue.getContent()).getChildrenUnmodifiable().get(0);
               // bindTasksInList();
            }
        }
    };

    //TODO : Make Class to make easy to support all types of files extension
    private ChangeListener<TreeItem> onFileSelectChangeListener = (observable, oldValue, newValue) -> {
        if (Objects.nonNull(newValue)) {
            String fileName = newValue.getValue().toString();
            if (fileName.endsWith(Extension.JAVA)) {
                Source javaSource = (Source) newValue.getValue();
                openSourceInTab(javaSource.getFile());
            } else if (fileName.endsWith(Extension.TEXT)) {
                Source textSource = (Source) newValue.getValue();
                openTextInTab(textSource.getFile());
            } else if (fileName.endsWith(Extension.MD)) {
                Source textSource = (Source) newValue.getValue();
                openTextInTab(textSource.getFile());
            }
            else if (fileName.endsWith(Extension.ATEM)) {
                Source textSource = (Source) newValue.getValue();
                openAtemScriptInTab(textSource.getFile());
            }
        }
    };

    File openSourceFolder;
   public  void updateFilesTreeView(File sourceFolder) {
        if (Objects.nonNull(sourceFolder)) {
            this.openSourceFolder = sourceFolder;
            if (projectWatcherService != null) {
                projectWatcherService.stopWatcher();
            }
            FileCrawler crawler = new FileCrawler();
            TreeItem<Source> sourceTreeItem = crawler.getFilesForDirectory(sourceFolder);
            sourceTreeItem.setGraphic(new ImageView(PROJECT_DIR));
            executorService.submit(() -> Platform.runLater(() -> filesTreeView.setRoot(sourceTreeItem)));
            projectWatcherService = FileService.setFileService(filesTreeView, sourceFolder);
        }
    }
/*
    public  void updateFilesTreeView( )
    {
        updateFilesTreeView(this.openSourceFolder);
    }
*/
    public static MainController getInstance() {
        return mainController;
    }
}
