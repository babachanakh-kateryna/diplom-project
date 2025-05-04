    package org.classcraft;

    import javafx.application.Application;
    import javafx.stage.Stage;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.input.*;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.VBox;
    import org.classcraft.Composite.ClassModel;
    import org.classcraft.Composite.FileComposite;
    import org.classcraft.Controller.*;
    import org.classcraft.Model.ApplicationState;
    import org.classcraft.Model.DiagramModel;
    import org.classcraft.View.DiagramView;
    import org.classcraft.View.ExplorerView;
    import org.classcraft.View.TextView;

    public class MainJavaFX extends Application {
        @Override
        public void start(Stage stage) throws Exception {
            DiagramModel model = new DiagramModel("target/classes");

            // Реєстрація спостерігачів
            TextView textView = new TextView();
            DiagramView diagramView = new DiagramView(model, 600, 700);
            ExplorerView explorerView = new ExplorerView(model);
            model.registerObserver(textView);
            model.registerObserver(diagramView);

            // Контролери
            AddClassController addClassController = new AddClassController(model);
            AddRelationController addRelationController = new AddRelationController(model);
            ModifyClassController modifyClassController = new ModifyClassController(model);
            BorderPane borderPane = new BorderPane();
            VBox topContainer = new VBox(); // Для меню та панелі інструментів

            // Налаштування меню
            MenuBar menuBar = new MenuBar();
            Menu fileMenu = new Menu("File");

            // Пункти меню
            MenuItem newEmptyDiagram = new MenuItem("New Empty Diagram");
            NewDiagramController newDiagramController = new NewDiagramController(model);
            newEmptyDiagram.setOnAction(newDiagramController);
            MenuItem importModelFile = new MenuItem("Import Model File");
            ImportController importController = new ImportController(model);
            importModelFile.setOnAction(importController);
            MenuItem exportModel = new MenuItem("Export Model");
            ModelExportController modelExportController = new ModelExportController(model);
            exportModel.setOnAction(modelExportController);
            MenuItem exportImage = new MenuItem("Export Image");
            ImageExportController imageExportController = new ImageExportController(model, diagramView);
            exportImage.setOnAction(imageExportController);
            MenuItem exportPuml = new MenuItem("Export PlantUML");
            PlantUMLExportController plantUMLExportController = new PlantUMLExportController(model);
            exportPuml.setOnAction(plantUMLExportController);

            MenuItem saveJavaFile = new MenuItem("Save Java File");
            saveJavaFile.setOnAction(new SaveJavaFileController(model));

            // Додавання пунктів до меню
            fileMenu.getItems().addAll(
                    newEmptyDiagram,
                    importModelFile,
                    exportModel,
                    exportImage,
                    exportPuml,
                    saveJavaFile
            );

            menuBar.getMenus().add(fileMenu);

            // Налаштування панелі інструментів
            ToolBar toolBar = new ToolBar();

            // Кнопки панелі інструментів
            Button addClassButton = new Button("Add Class");
            Button addRelationButton = new Button("Add Relation");
            Button editClassButton = new Button("Edit Class");

            // Додавання кнопок до панелі інструментів
            toolBar.getItems().addAll(addClassButton, addRelationButton, editClassButton);

            // Прив’язка дій до кнопок
            addClassButton.setOnAction(addClassController);
            addRelationButton.setOnAction(addRelationController);
            editClassButton.setOnAction(modifyClassController);

            // Додавання меню та панелі інструментів до верхнього контейнера
            topContainer.getChildren().addAll(menuBar, toolBar);

            // Налаштування контейнера для дерева файлів
            VBox repoContainer = new VBox(); // Для кнопки "Select Repository" та дерева
            Button selectRepoButton = new Button("Select Repository");
            selectRepoButton.setOnAction(new ChangeDirectoryController(model));

            explorerView.setMaxWidth(600);
            explorerView.setMinHeight(800);
            selectRepoButton.setMinWidth(250);
            model.registerObserver(explorerView);

            repoContainer.getChildren().addAll(selectRepoButton, explorerView);

            // Налаштування перетягування для додавання класу до діаграми
            explorerView.setOnDragDetected(event -> {
                TreeItem<FileComposite> selectedItem = explorerView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Dragboard db = explorerView.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selectedItem.getValue().getName());
                    db.setContent(content);
                    event.consume();
                }
            });

            diagramView.setOnDragOver(event -> {
                if (event.getGestureSource() != diagramView && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            diagramView.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    TreeItem<FileComposite> selectedItem = explorerView.getSelectionModel().getSelectedItem();

                    // Додавання перетягнутого елемента в місце скидання
                    if (selectedItem != null && selectedItem.getValue() instanceof ClassModel) {
                        double x = diagramView.sceneToLocal(event.getSceneX(), event.getSceneY()).getX();
                        double y = diagramView.sceneToLocal(event.getSceneY(), event.getSceneY()).getY();
                        model.addClass((ClassModel) selectedItem.getValue(), x, y);
                        success = true;
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            borderPane.setCenter(diagramView);
            borderPane.setTop(topContainer);
            borderPane.setLeft(repoContainer);

            Scene scene = new Scene(borderPane, 600, 700);
            stage.setTitle("UML Application");
            stage.setScene(scene);
            stage.show();

            // Збереження ширини вікна
            scene.widthProperty().addListener((observable, oldValue, newValue) -> {
                ApplicationState.setCurrentWindowWidth(newValue.doubleValue());
            });
        }

        public static void main(String[] args) {
            launch();
        }
    }