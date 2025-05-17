package org.umlgenerator;

import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Composite.FileComposite;
import org.umlgenerator.Composite.RelationUML;
import org.umlgenerator.Controller.*;
import org.umlgenerator.Model.ApplicationState;
import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.View.DiagramView;
import org.umlgenerator.View.ExplorerView;
import org.umlgenerator.View.TextView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

public class LoginWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        Image backgroundImage = new Image(getClass().getResourceAsStream("background.png"));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(400);
        backgroundView.setFitHeight(300);
        backgroundView.setPreserveRatio(true);

        VBox rightPane = new VBox(20);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(20));

        Text title = new Text("Вітаємо в ClassCraft!");
        title.setFont(Font.font("Arial", 24));

        Button newProjectButton = new Button("Новий проєкт");
        newProjectButton.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14px;");
        newProjectButton.setPrefWidth(200);

        Button openProjectButton = new Button("Відкрити проєкт");
        openProjectButton.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14px;");
        openProjectButton.setPrefWidth(200);

        rightPane.getChildren().addAll(title, newProjectButton, openProjectButton);

        root.setLeft(backgroundView);
        root.setCenter(rightPane);

        Scene scene = new Scene(root, 600, 300);
        primaryStage.setTitle("ClassCraft - Вхід");
        primaryStage.setScene(scene);
        primaryStage.show();

        newProjectButton.setOnAction(event -> openNewProjectWindow(primaryStage));
        openProjectButton.setOnAction(event -> openExistingProject(primaryStage));
    }

    private void openNewProjectWindow(Stage primaryStage) {
        Stage newProjectStage = new Stage();
        newProjectStage.setTitle("Створення нового проєкту");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Text title = new Text("Оберіть спосіб створення проєкту");
        title.setFont(Font.font("Arial", 18));

        Button emptyProjectButton = new Button("Створити порожній проєкт");
        emptyProjectButton.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14px;");
        emptyProjectButton.setPrefWidth(250);

        Button loadJavaFilesButton = new Button("Завантажити каталог із Java-файлами");
        loadJavaFilesButton.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14px;");
        loadJavaFilesButton.setPrefWidth(250);

        layout.getChildren().addAll(title, emptyProjectButton, loadJavaFilesButton);

        Scene scene = new Scene(layout, 400, 200);
        newProjectStage.setScene(scene);
        newProjectStage.show();

        emptyProjectButton.setOnAction(event -> {
            newProjectStage.close();
            startMainApplication(primaryStage, true, null);
        });

        loadJavaFilesButton.setOnAction(event -> {
            newProjectStage.close();
            startMainApplication(primaryStage, false, null);
        });
    }

    private void openExistingProject(Stage primaryStage) {
        // Создаём модель
        DiagramModel model = new DiagramModel("target/classes");

        // Импортируем модель из файла
        ImportController importController = new ImportController(model);
        boolean success = importController.importFromFile(primaryStage);

        // Только если успешно импортировано — отображаем основное окно
        if (success) {
            startMainApplication(primaryStage, false, model); // Передаём модель
        } else {
            showError("Помилка імпорту", "Не вдалося завантажити діаграму. Перевірте файл і повторіть спробу.");
        }
    }

    private void startMainApplication(Stage primaryStage, boolean isEmptyProject, DiagramModel model) {
        // Якщо модель не передана (для нових проєктів), створюємо нову
        if (model == null) {
            model = new DiagramModel("target/classes");
        }

        // Скидаємо модель, якщо це порожній проєкт
        if (isEmptyProject) {
            model.reset();
        }

        // Ініціалізація представлень
        TextView textView = new TextView();
        DiagramView diagramView = new DiagramView(model, 600, 700);
        ExplorerView explorerView = new ExplorerView(model);

        // Реєстрація спостерігачів
        model.registerObserver(textView);
        model.registerObserver(diagramView);
        model.registerObserver(explorerView);

        // Оновлення DiagramView після імпорту
        diagramView.update(model); // Явно оновлюємо DiagramView

        // Контролери
        AddClassController addClassController = new AddClassController(model);
        AddRelationController addRelationController = new AddRelationController(model);
        ModifyClassController modifClasse = new ModifyClassController(model);

        // Налаштування UI (меню, панель інструментів, тощо)
        BorderPane borderPane = new BorderPane();
        VBox topContainer = new VBox();

        MenuBar menuBar = new MenuBar();
        Menu fichierMenu = new Menu("Файл");

        MenuItem nouveauDiagrammeVide = new MenuItem("Нова порожня діаграма");
        NewDiagramController newDiagramController = new NewDiagramController(model);
        nouveauDiagrammeVide.setOnAction(newDiagramController);

        MenuItem importerFichierModele = new MenuItem("Імпорт файлу моделі");
        ImportController importController = new ImportController(model);
        importerFichierModele.setOnAction(importController);

        MenuItem exporterModele = new MenuItem("Зберегти модель");
        ExportModelController exportModelController = new ExportModelController(model);
        exporterModele.setOnAction(exportModelController);

        MenuItem exporterImage = new MenuItem("Зберегти зображення");
        ExportImageController exportImageController = new ExportImageController(model, diagramView);
        exporterImage.setOnAction(exportImageController);

        MenuItem exporterPuml = new MenuItem("Зберегти PlantUML");
        ExportPlantUMLController exportPlantUMLController = new ExportPlantUMLController(model);
        exporterPuml.setOnAction(exportPlantUMLController);

        MenuItem saveItem = new MenuItem("Зберегти Java файли");
        saveItem.setOnAction(new SaveJavaFileController(model));

        fichierMenu.getItems().addAll(nouveauDiagrammeVide, importerFichierModele, exporterModele, exporterImage, exporterPuml, saveItem);
        menuBar.getMenus().add(fichierMenu);

        ToolBar toolBar = new ToolBar();
        Button addClass = new Button("Додати клас");
        Button addRelation = new Button("Додати зв'язок");
        Button editClass = new Button("Редагувати клас");
        toolBar.getItems().addAll(addClass, addRelation, editClass);
        addClass.setOnAction(addClassController);
        addRelation.setOnAction(addRelationController);
        editClass.setOnAction(modifClasse);

        topContainer.getChildren().addAll(menuBar, toolBar);

        VBox repoContainer = new VBox();
        Button selectRepo = new Button("Обрати репозиторій");
        selectRepo.setOnAction(new ChangeDirectoryController(model));
        explorerView.setMaxWidth(600);
        explorerView.setMinHeight(800);
        selectRepo.setMinWidth(250);

        repoContainer.getChildren().addAll(selectRepo, explorerView);

        // Налаштування drag-and-drop
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

        DiagramModel finalModel = model;
        diagramView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                TreeItem<FileComposite> selectedItem = explorerView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue() instanceof ClassUML) {
                    double x = diagramView.sceneToLocal(event.getSceneX(), event.getSceneY()).getX();
                    double y = diagramView.sceneToLocal(event.getSceneY(), event.getSceneY()).getY();
                    finalModel.addClass((ClassUML) selectedItem.getValue(), x, y);
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
        primaryStage.setTitle("UML Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            ApplicationState.setCurrentWindowWidth(newValue.doubleValue());
        });
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}