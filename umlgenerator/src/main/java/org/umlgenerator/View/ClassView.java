package org.umlgenerator.View;

import org.umlgenerator.Composite.AttributeUML;
import org.umlgenerator.Composite.ClassUML;
import org.umlgenerator.Composite.MethodeUML;
import org.umlgenerator.Controller.AddRelationController;
import org.umlgenerator.Controller.DeleteRelationController;
import org.umlgenerator.Model.DiagramModel;
import org.umlgenerator.Composite.RelationUML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

// Клас для відображення класу в графічному інтерфейсі
public class ClassView extends GridPane {

    private DiagramModel model; // Модель діаграми
    private ClassUML javaClass; // Клас, який відображається
    private Pane previsualisation;// Панель для попереднього перегляду при зміні розміру або позиції

    private Circle rightCircle, bottomCircle, bottomRightCircle; // Кола для зміни розміру
    private double minHeight = 50; // Мінімальна висота панелі

    // Конструктор для ініціалізації відображення класу
    public ClassView(ClassUML c, DiagramModel m) {

        this.model = m;
        this.javaClass = c;
        this.setVisible(model.getClassVisibility(c));
        this.setCursor(Cursor.HAND);

        Label classNameLabel = new Label(javaClass.getName());
        classNameLabel.setFont(Font.font("Arial", 16));
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a4d78;");

        Label classTypeLabel = new Label(javaClass.getType());
        classTypeLabel.setFont(Font.font("Arial", 12));
        classTypeLabel.setStyle("-fx-text-fill: #7a8b99;");

        VBox classInfo = new VBox(5);
        classInfo.getChildren().addAll(classTypeLabel, classNameLabel);

        Separator separator1 = new Separator();

        VBox attributsSection = new VBox(5);
        for (AttributeUML attributeModel : javaClass.getAttributs()) {
            Label attributLabel = new Label(attributeModel.toString());
            attributLabel.setStyle("-fx-text-fill: #006699;");
            attributsSection.getChildren().add(attributLabel);
        }

        Separator separator2 = new Separator();

        VBox methodesSection = new VBox(5);
        for (MethodeUML methodeUML : javaClass.getMethodes()) {
            Label methodeLabel = new Label(methodeUML.toString());
            methodeLabel.setStyle("-fx-text-fill: #ff6600;");
            methodesSection.getChildren().add(methodeLabel);
        }

        add(classInfo, 0, 0);
        add(separator1, 0, 1);
        add(attributsSection, 0, 2);
        add(separator2, 0, 3);
        add(methodesSection, 0, 4);

        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setFillWidth(true);
        getColumnConstraints().add(colConstraints);

        setVgap(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white; -fx-border-color: #2a4d78; -fx-border-width: 2px; -fx-padding: 10px;");

        //EVENEMENT

        // Налаштовує обробку подій миші
        this.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {  // Лівий клік

                AnchorPane anchorPane = (AnchorPane) this.getParent();

                // Створення кіл для зміни розміру
                rightCircle = createResizeCircle();
                bottomCircle = createResizeCircle();
                bottomRightCircle = createResizeCircle();
                updateResizeCirclesPosition();

                // Додавання кіл до AnchorPane
                ((AnchorPane) this.getParent()).getChildren().addAll(bottomRightCircle);

                // Зміна вигляду курсору на колах
                rightCircle.setCursor(Cursor.E_RESIZE);
                bottomCircle.setCursor(Cursor.S_RESIZE);
                bottomRightCircle.setCursor(Cursor.MOVE);

                // Обробка подій відпускання миші
                rightCircle.setOnMouseReleased(eventB -> {
                    double deltaX = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY()).getX() - this.getLayoutX();

                    this.setPrefWidth(Math.max(50, deltaX));
                    model.setSizeClass(javaClass, this.getPrefWidth(), this.getPrefHeight());
                });
                bottomCircle.setOnMouseReleased(eventB -> {
                    double deltaY = anchorPane.sceneToLocal(event.getSceneY(), event.getSceneY()).getY() - this.getLayoutY();

                    this.setPrefHeight(Math.max(this.minHeight, deltaY));
                    model.setSizeClass(javaClass, this.getPrefWidth(), this.getPrefHeight());
                });
                bottomRightCircle.setOnMouseReleased(eventB -> {
                    // Отримання позиції миші в системі координат AnchorPane
                    double mouseX = anchorPane.sceneToLocal(eventB.getSceneX(), eventB.getSceneY()).getX();
                    double mouseY = anchorPane.sceneToLocal(eventB.getSceneY(), eventB.getSceneY()).getY();

                    // Обчислюємо нові позиції так, щоб правий нижній кут знаходився під мишкою
                    double newX = mouseX - this.getWidth();
                    double newY = mouseY - this.getHeight();

                    // Оновлення позицій панелей
                    AnchorPane.setLeftAnchor(this, newX);
                    AnchorPane.setTopAnchor(this, newY);

                    model.setPosClasse(javaClass, newX, newY);
                    updateResizeCirclesPosition();
                });

                // попередній перегляд
                bottomRightCircle.setOnMouseDragged(eventB ->{
                    anchorPane.getChildren().remove(previsualisation);
                    bottomCircle.setFill(new Color(0, 0, 0, 0));
                    bottomRightCircle.setFill(new Color(0, 0, 0, 0));
                    rightCircle.setFill(new Color(0, 0, 0, 0));

                    double mouseX = anchorPane.sceneToLocal(eventB.getSceneX(), eventB.getSceneY()).getX();
                    double mouseY = anchorPane.sceneToLocal(eventB.getSceneY(), eventB.getSceneY()).getY();

                    double newX = mouseX - this.getWidth();
                    double newY = mouseY - this.getHeight();

                    previsualisation = new Pane();
                    previsualisation.setPrefSize(this.getWidth(),this.getHeight());
                    previsualisation.setStyle("-fx-background-color: lightblue;");
                    anchorPane.getChildren().add(previsualisation);
                    AnchorPane.setLeftAnchor(previsualisation, newX);
                    AnchorPane.setTopAnchor(previsualisation, newY);
                });
                rightCircle.setOnMouseDragged(eventB -> {
                    anchorPane.getChildren().remove(previsualisation);
                    bottomCircle.setFill(new Color(0, 0, 0, 0));
                    bottomRightCircle.setFill(new Color(0, 0, 0, 0));
                    rightCircle.setFill(new Color(0, 0, 0, 0));

                    double deltaX = anchorPane.sceneToLocal(eventB.getSceneX(), eventB.getSceneY()).getX() - this.getLayoutX();

                    previsualisation = new Pane();
                    previsualisation.setPrefSize(Math.max(50, deltaX),this.getHeight());
                    previsualisation.setStyle("-fx-background-color: lightblue;");
                    anchorPane.getChildren().add(previsualisation);
                    AnchorPane.setLeftAnchor(previsualisation, this.getLayoutX());
                    AnchorPane.setTopAnchor(previsualisation, this.getLayoutY());
                });
                bottomCircle.setOnMouseDragged(eventB -> {
                    anchorPane.getChildren().remove(previsualisation);
                    bottomCircle.setFill(new Color(0, 0, 0, 0));
                    bottomRightCircle.setFill(new Color(0, 0, 0, 0));
                    rightCircle.setFill(new Color(0, 0, 0, 0));

                    double deltaY = anchorPane.sceneToLocal(eventB.getSceneY(), eventB.getSceneY()).getY() - this.getLayoutY();

                    previsualisation = new Pane();
                    previsualisation.setPrefSize(this.getWidth(),Math.max(this.minHeight,deltaY));
                    previsualisation.setStyle("-fx-background-color: lightblue;");
                    anchorPane.getChildren().add(previsualisation);
                    AnchorPane.setLeftAnchor(previsualisation, this.getLayoutX());
                    AnchorPane.setTopAnchor(previsualisation, this.getLayoutY());
                });
            }
        });
        // Контекстне меню при клацанні правою кнопкою миші на класі на діаграмі
        setContextMenu();
    }

    // Створює коло для зміни розміру
    private Circle createResizeCircle() {
        Circle circle = new Circle(10);
        circle.setFill(Color.BLUE);
        return circle;
    }

    // Оновлює позиції кіл для зміни розміру
    private void updateResizeCirclesPosition() {
        double radius = rightCircle.getRadius();

        // Змініть положення кіл, розмістивши їхній центр посередині між краями
        AnchorPane.setLeftAnchor(rightCircle, AnchorPane.getLeftAnchor(this) + this.getWidth() - radius);
        AnchorPane.setTopAnchor(rightCircle, AnchorPane.getTopAnchor(this) + this.getHeight() / 2 - radius);

        AnchorPane.setLeftAnchor(bottomCircle, AnchorPane.getLeftAnchor(this) + this.getWidth() / 2 - radius);
        AnchorPane.setTopAnchor(bottomCircle, AnchorPane.getTopAnchor(this) + this.getHeight() - radius);

        AnchorPane.setLeftAnchor(bottomRightCircle, AnchorPane.getLeftAnchor(this) + this.getWidth() - radius);
        AnchorPane.setTopAnchor(bottomRightCircle, AnchorPane.getTopAnchor(this) + this.getHeight() - radius);
    }

    // Контекстне меню при клацанні правою кнопкою миші на класі на діаграмі
    private void setContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addItem = new MenuItem("Додати атрибут");
        addItem.setOnAction(e -> model.addAttribute(javaClass));

        MenuItem addMethod = new MenuItem("Додати метод");
        addMethod.setOnAction(e -> model.addMethod(javaClass));

        MenuItem delete = new MenuItem("Видалити клас");
        delete.setOnAction(e -> model.removeClass(javaClass));

        MenuItem addRelation = new MenuItem("Додати зв’язок");
        addRelation.setOnAction(new AddRelationController(model,javaClass));

        MenuItem delRelation = new MenuItem("Видалити зв’язок");
        delRelation.setOnAction(new DeleteRelationController(model,javaClass));

        // Меню для приховування
        Menu masquerMenu = new Menu("Приховати");

        MenuItem masquerClasse = new MenuItem("Приховати клас");
        masquerClasse.setOnAction(e -> toggleVisibility());

        MenuItem masquerRelationsClasse = new MenuItem("Приховати зв’язки класу");
        masquerRelationsClasse.setOnAction(e -> toggleVisibilityRelations(false));

        MenuItem masquerToutesRelations = new MenuItem("Приховати всі зв’язки");
        masquerToutesRelations.setOnAction(e -> model.toggleVisibilityRelation(false));

        MenuItem demasquerToutesRelations = new MenuItem("Показати всі зв’язки");
        demasquerToutesRelations.setOnAction(e -> model.toggleVisibilityRelation(true));

        masquerMenu.getItems().addAll(masquerClasse, masquerRelationsClasse, new SeparatorMenuItem(), masquerToutesRelations, demasquerToutesRelations);

        // Показати все
        MenuItem demasquerTout = new MenuItem("Показати все");
        demasquerTout.setOnAction(e -> model.toggleVisibilityAll(true));

        contextMenu.getItems().addAll(addItem, addMethod, addRelation, delRelation, new SeparatorMenuItem(), delete, new SeparatorMenuItem(), masquerMenu, demasquerTout);

        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    // Перемикає видимість класу
    private void toggleVisibility() {
        boolean isVisible = this.isVisible();
        this.setVisible(!isVisible); // оновлення видимості класу
        model.setClassVisibility(javaClass, !isVisible); // зберегти стан видимості в моделі

        // Оновлення видимості зв’язків, пов’язаних із класом
        for (RelationUML relationUML : model.getRelations()) {
            if (relationUML.getSource().equals(javaClass.getName()) || relationUML.getTarget().equals(javaClass.getName())) {
                model.setRelationVisibility(relationUML, !isVisible);
            }
        }
    }

    // Перемикає видимість зв’язків класу
    private void toggleVisibilityRelations(boolean visible) {
        for (RelationUML relationUML : model.getRelations()) {
            if (relationUML.getSource().equals(javaClass.getName()) || relationUML.getTarget().equals(javaClass.getName())) {
                model.setRelationVisibility(relationUML, visible);
            }
        }
    }

    // Встановлює мінімальну висоту панелі
    public void setMinHauteur(double minHeight) {
        this.minHeight = minHeight;
    }
}
