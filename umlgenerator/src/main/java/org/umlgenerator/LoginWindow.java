package org.umlgenerator;

import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Composite.FileComposite;
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

public class LoginWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Основний контейнер із BorderPane
        BorderPane root = new BorderPane();

        // Ліва частина - зображення фону
        Image backgroundImage = new Image(getClass().getResourceAsStream("background.png"));
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setFitWidth(400); // Налаштування розміру зображення
        backgroundView.setFitHeight(300);
        backgroundView.setPreserveRatio(true);

        // Права частина - текст і кнопки
        VBox rightPane = new VBox(20);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(20));

        // Заголовок
        Text title = new Text("Вітаємо в ClassCraft!");
        title.setFont(Font.font("Arial", 24));

        // Кнопка "Новий проєкт"
        Button newProjectButton = new Button("Новий проєкт");
        newProjectButton.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14px;");
        newProjectButton.setPrefWidth(200);

        // Кнопка "Відкрити проєкт"
        Button openProjectButton = new Button("Відкрити проєкт");
        openProjectButton.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14px;");
        openProjectButton.setPrefWidth(200);

        // Додавання елементів до правої частини
        rightPane.getChildren().addAll(title, newProjectButton, openProjectButton);

        // Додавання частин до BorderPane
        root.setLeft(backgroundView);
        root.setCenter(rightPane);

        // Налаштування сцени
        Scene scene = new Scene(root, 600, 300);
        primaryStage.setTitle("ClassCraft - Вхід");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Обробка натискання кнопки "Новий проєкт"
        newProjectButton.setOnAction(event -> openNewProjectWindow(primaryStage));

        // Обробка натискання кнопки "Відкрити проєкт"
        openProjectButton.setOnAction(event -> openExistingProject(primaryStage));
    }

    // Метод для відкриття вікна створення нового проєкту
    private void openNewProjectWindow(Stage primaryStage) {
        Stage newProjectStage = new Stage();
        newProjectStage.setTitle("Створення нового проєкту");

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Заголовок
        Text title = new Text("Оберіть спосіб створення проєкту");
        title.setFont(Font.font("Arial", 18));

        // Кнопка для створення порожнього проєкту
        Button emptyProjectButton = new Button("Створити порожній проєкт");
        emptyProjectButton.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14px;");
        emptyProjectButton.setPrefWidth(250);

        // Кнопка для завантаження каталогу з Java-файлами
        Button loadJavaFilesButton = new Button("Завантажити каталог із Java-файлами");
        loadJavaFilesButton.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14px;");
        loadJavaFilesButton.setPrefWidth(250);

        // Додавання елементів до layout
        layout.getChildren().addAll(title, emptyProjectButton, loadJavaFilesButton);

        // Налаштування сцени для нового вікна
        Scene scene = new Scene(layout, 400, 200);
        newProjectStage.setScene(scene);
        newProjectStage.show();

        // Обробка натискання кнопки "Створити порожній проєкт"
        emptyProjectButton.setOnAction(event -> {
            newProjectStage.close();
            startMainApplication(primaryStage, true); // true - порожній проєкт
        });

        // Обробка натискання кнопки "Завантажити каталог із Java-файлами"
        loadJavaFilesButton.setOnAction(event -> {
            newProjectStage.close();
            startMainApplication(primaryStage, false); // false - завантаження файлів
        });
    }

    // Метод для відкриття існуючого проєкту
    private void openExistingProject(Stage primaryStage) {
        // Логіка для відкриття існуючого проєкту (наприклад, через ImportController)
        startMainApplication(primaryStage, false);
    }

    // Метод для запуску основного додатку
    private void startMainApplication(Stage primaryStage, boolean isEmptyProject) {
        // Ініціалізація моделі
        DiagramModel model = new DiagramModel("target/classes");

        if (isEmptyProject) {
            model.reset(); // Скидаємо модель для порожнього проєкту
        }

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
        VBox topContainer = new VBox();

        // Налаштування меню
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

        fichierMenu.getItems().addAll(
                nouveauDiagrammeVide,
                importerFichierModele,
                exporterModele,
                exporterImage,
                exporterPuml,
                saveItem
        );

        menuBar.getMenus().add(fichierMenu);

        // Налаштування панелі інструментів
        ToolBar toolBar = new ToolBar();
        Button addClass = new Button("Додати клас");
        Button addRelation = new Button("Додати зв'язок");
        Button editClass = new Button("Редагувати клас");

        toolBar.getItems().addAll(addClass, addRelation, editClass);

        addClass.setOnAction(addClassController);
        addRelation.setOnAction(addRelationController);
        editClass.setOnAction(modifClasse);

        topContainer.getChildren().addAll(menuBar, toolBar);

        // Налаштування контейнера для дерева файлів
        VBox repoContainer = new VBox();
        Button selectRepo = new Button("Обрати репозиторій");
        selectRepo.setOnAction(new ChangeDirectoryController(model));

        explorerView.setMaxWidth(600);
        explorerView.setMinHeight(800);
        selectRepo.setMinWidth(250);
        model.registerObserver(explorerView);

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

        diagramView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                TreeItem<FileComposite> selectedItem = explorerView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getValue() instanceof ClassUML) {
                    double x = diagramView.sceneToLocal(event.getSceneX(), event.getSceneY()).getX();
                    double y = diagramView.sceneToLocal(event.getSceneY(), event.getSceneY()).getY();
                    model.addClass((ClassUML) selectedItem.getValue(), x, y);
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

        // Збереження ширини вікна
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            ApplicationState.setCurrentWindowWidth(newValue.doubleValue());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}