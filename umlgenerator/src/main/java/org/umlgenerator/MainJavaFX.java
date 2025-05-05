package org.umlgenerator;

import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Composite.FileComposite;
import org.umlgenerator.Controller.*;
import org.umlgenerator.Model.ApplicationState;
import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.View.DiagramView;
import org.umlgenerator.View.ExplorerView;
import org.umlgenerator.View.TextView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainJavaFX extends Application {
    @Override
    public void start(Stage stage) {

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
        ModifyClassController modifClasse = new ModifyClassController(model);
        BorderPane borderPane = new BorderPane();
        VBox topContainer = new VBox(); // Для меню та панелі інструментів


        // Налаштування меню
        MenuBar menuBar = new MenuBar();
        Menu fichierMenu = new Menu("Файл");

        // Пункти меню
        MenuItem nouveau_diagramme_vide = new MenuItem("Нова порожня діаграма");
        NewDiagramController newDiagramController = new NewDiagramController(model);
        nouveau_diagramme_vide.setOnAction(newDiagramController);
        MenuItem importer_fichier_modele = new MenuItem("Імпорт файлу моделі");
        ImportController importController = new ImportController(model);
        importer_fichier_modele.setOnAction(importController);
        MenuItem exporter_modele = new MenuItem("Зберегти модель");
        ExportModelController exportModelController = new ExportModelController(model);
        exporter_modele.setOnAction(exportModelController);
        MenuItem exporter_image = new MenuItem("Зберегти зображення");
        ExportImageController exportImageController = new ExportImageController(model, diagramView);
        exporter_image.setOnAction(exportImageController);
        MenuItem exporter_puml = new MenuItem("Зберегти PlantUML");
        ExportPlantUMLController exportPlantUMLController = new ExportPlantUMLController(model);
        exporter_puml.setOnAction(exportPlantUMLController);

        MenuItem saveItem = new MenuItem("Зберегти Java файли");
        saveItem.setOnAction(new SaveJavaFileController(model));

        // Додавання пунктів до меню
        fichierMenu.getItems().addAll(
                nouveau_diagramme_vide,
                importer_fichier_modele,
                exporter_modele,
                exporter_image,
                exporter_puml,
                saveItem
        );

        menuBar.getMenus().add(fichierMenu);


        // Налаштування панелі інструментів
        ToolBar toolBar = new ToolBar();

        // Кнопки панелі інструментів
        Button addClass = new Button("Додати клас");
        Button addRelation = new Button("Додати зв'язок");
        Button editClass = new Button("Редагувати клас");

        // Додавання кнопок до панелі інструментів
        toolBar.getItems().addAll(addClass, addRelation, editClass);

        // Прив’язка дій до кнопок
        addClass.setOnAction(addClassController);
        addRelation.setOnAction(addRelationController);
        editClass.setOnAction(modifClasse);

        // Додавання меню та панелі інструментів до верхнього контейнера
        topContainer.getChildren().addAll(menuBar, toolBar);

        // Налаштування контейнера для дерева файлів
        VBox repoContainer = new VBox(); // Для кнопки "Select Repository" та дерева
        Button selectRepo = new Button("Обрати репозиторій");
        selectRepo.setOnAction(new ChangeDirectoryController(model));

        explorerView.setMaxWidth(600);
        explorerView.setMinHeight(800);
        selectRepo.setMinWidth(250);
        model.registerObserver(explorerView);

        repoContainer.getChildren().addAll(selectRepo, explorerView);

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
                if (selectedItem != null && selectedItem.getValue() instanceof ClassUML) {
                    double x = diagramView.sceneToLocal(event.getSceneX(), event.getSceneY()).getX();
                    double y = diagramView.sceneToLocal(event.getSceneY(), event.getSceneY()).getY();
                    model.addClass((ClassUML) selectedItem.getValue(),x,y);
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